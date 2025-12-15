package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.MovementRequestDTO;
import ua.edu.viti.military.dto.MovementResponseDTO;
import ua.edu.viti.military.entity.*;
import ua.edu.viti.military.exception.*;
import ua.edu.viti.military.repository.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SupplyMovementService {

    private final SupplyMovementRepository movementRepository;
    private final SupplyItemRepository itemRepository;

    /**
     * Видача матеріалів
     * Транзакція з REPEATABLE_READ для запобігання phantom reads
     */
    @Transactional(
        isolation = Isolation.REPEATABLE_READ,
        rollbackFor = Exception.class
    )
    public MovementResponseDTO issueItem(MovementRequestDTO dto) {
        log.info("Issuing item: itemId={}, quantity={}", dto.getItemId(), dto.getQuantity());

        // 1. Знайти матеріал
        SupplyItem item = itemRepository.findById(dto.getItemId())
            .orElseThrow(() -> new ResourceNotFoundException("Матеріал не знайдено"));

        // 2. Перевірити статус
        if (item.getStatus() != ItemStatus.IN_STOCK) {
            throw new BusinessLogicException("Матеріал не на складі (статус: " + item.getStatus() + ")");
        }

        // 3. Перевірити термін придатності
        if (item.getExpirationDate() != null &&
            item.getExpirationDate().isBefore(LocalDate.now())) {
            throw new BusinessLogicException("Не можна видати прострочений матеріал");
        }

        // 4. Перевірити наявність
        if (item.getQuantity() < dto.getQuantity()) {
            throw new BusinessLogicException(
                String.format("Недостатньо матеріалів. Доступно: %d, запитано: %d",
                    item.getQuantity(), dto.getQuantity())
            );
        }

        // 5. Зменшити кількість
        item.setQuantity(item.getQuantity() - dto.getQuantity());

        // 6. Якщо кількість = 0, змінити статус
        if (item.getQuantity() == 0) {
            item.setStatus(ItemStatus.ISSUED);
        }

        itemRepository.save(item);

        // 7. Створити запис в журналі
        SupplyMovement movement = new SupplyMovement();
        movement.setItem(item);
        movement.setType(MovementType.ISSUE);
        movement.setQuantity(dto.getQuantity());
        movement.setRecipientName(dto.getRecipientName());
        movement.setRecipientUnit(dto.getRecipientUnit());
        movement.setNotes(dto.getNotes());
        movement.setPerformedBy(getCurrentUser());

        SupplyMovement saved = movementRepository.save(movement);

        log.info("Item issued successfully. Movement ID: {}", saved.getId());

        return toResponseDTO(saved);
    }

    /**
     * Повернення матеріалів
     */
    @Transactional(
        isolation = Isolation.REPEATABLE_READ,
        rollbackFor = Exception.class
    )
    public MovementResponseDTO returnItem(MovementRequestDTO dto) {
        log.info("Returning item: itemId={}, quantity={}", dto.getItemId(), dto.getQuantity());

        SupplyItem item = itemRepository.findById(dto.getItemId())
            .orElseThrow(() -> new ResourceNotFoundException("Матеріал не знайдено"));

        // Додати кількість назад
        item.setQuantity(item.getQuantity() + dto.getQuantity());
        item.setStatus(ItemStatus.IN_STOCK);

        itemRepository.save(item);

        // Створити запис в журналі
        SupplyMovement movement = new SupplyMovement();
        movement.setItem(item);
        movement.setType(MovementType.RETURN);
        movement.setQuantity(dto.getQuantity());
        movement.setNotes(dto.getNotes());
        movement.setPerformedBy(getCurrentUser());

        SupplyMovement saved = movementRepository.save(movement);

        log.info("Item returned successfully. Movement ID: {}", saved.getId());

        return toResponseDTO(saved);
    }

    /**
     * Списання по терміну придатності або пошкодження
     */
    @Transactional(rollbackFor = Exception.class)
    public MovementResponseDTO writeOffItem(MovementRequestDTO dto) {
        log.info("Writing off item: itemId={}, quantity={}", dto.getItemId(), dto.getQuantity());

        SupplyItem item = itemRepository.findById(dto.getItemId())
            .orElseThrow(() -> new ResourceNotFoundException("Матеріал не знайдено"));

        if (item.getQuantity() < dto.getQuantity()) {
            throw new BusinessLogicException("Недостатньо матеріалів для списання");
        }

        // Зменшити кількість
        item.setQuantity(item.getQuantity() - dto.getQuantity());

        if (item.getQuantity() == 0) {
            item.setStatus(ItemStatus.WRITTEN_OFF);
        }

        itemRepository.save(item);

        // Створити запис в журналі
        SupplyMovement movement = new SupplyMovement();
        movement.setItem(item);
        movement.setType(MovementType.WRITE_OFF);
        movement.setQuantity(dto.getQuantity());
        movement.setNotes(dto.getNotes());
        movement.setPerformedBy(getCurrentUser());

        SupplyMovement saved = movementRepository.save(movement);

        log.info("Item written off successfully. Movement ID: {}", saved.getId());

        return toResponseDTO(saved);
    }

    /**
     * Отримати історію операцій для конкретного матеріалу
     */
    public List<MovementResponseDTO> getMovementHistory(Long itemId) {
        return movementRepository.findByItemIdOrderByPerformedAtDesc(itemId)
            .stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * Отримати останні операції
     */
    public List<MovementResponseDTO> getRecentMovements() {
        return movementRepository.findTop10ByOrderByPerformedAtDesc()
            .stream()
            .map(this::toResponseDTO)
            .collect(Collectors.toList());
    }

    /**
     * Конвертація Entity → DTO
     */
    private MovementResponseDTO toResponseDTO(SupplyMovement movement) {
        MovementResponseDTO dto = new MovementResponseDTO();
        dto.setId(movement.getId());
        dto.setItemId(movement.getItem().getId());
        dto.setItemName(movement.getItem().getName());
        dto.setType(movement.getType());
        dto.setQuantity(movement.getQuantity());
        dto.setRecipientName(movement.getRecipientName());
        dto.setRecipientUnit(movement.getRecipientUnit());
        dto.setNotes(movement.getNotes());
        dto.setPerformedBy(movement.getPerformedBy());
        dto.setPerformedAt(movement.getPerformedAt());
        return dto;
    }

    /**
     * Отримати поточного користувача з Security Context
     */
    private String getCurrentUser() {
        try {
            org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.warn("Could not get current user from security context: {}", e.getMessage());
        }
        return "system";
    }
}
