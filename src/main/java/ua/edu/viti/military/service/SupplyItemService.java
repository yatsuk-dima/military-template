package ua.edu.viti.military.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.edu.viti.military.dto.SupplyCategoryResponseDTO;
import ua.edu.viti.military.dto.SupplyItemCreateDTO;
import ua.edu.viti.military.dto.SupplyItemResponseDTO;
import ua.edu.viti.military.dto.SupplyItemUpdateDTO;
import ua.edu.viti.military.entity.*;
import ua.edu.viti.military.exception.BusinessLogicException;
import ua.edu.viti.military.exception.DuplicateResourceException;
import ua.edu.viti.military.exception.ResourceNotFoundException;
import ua.edu.viti.military.repository.SupplyCategoryRepository;
import ua.edu.viti.military.repository.SupplyItemRepository;
import ua.edu.viti.military.repository.WarehouseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplyItemService {

    private final SupplyItemRepository itemRepository;
    private final SupplyCategoryRepository categoryRepository;
    private final WarehouseRepository warehouseRepository;

    /**
     * Створити новий матеріал
     */
    @Transactional
    public SupplyItemResponseDTO create(SupplyItemCreateDTO dto) {
        log.info("Creating new supply item with batch number: {}", dto.getBatchNumber());

        // Перевірка унікальності номера партії
        if (itemRepository.existsByBatchNumber(dto.getBatchNumber())) {
            throw new DuplicateResourceException(
                    "Матеріал з номером партії '" + dto.getBatchNumber() + "' вже існує"
            );
        }

        // Перевірка терміну придатності
        validateExpirationDate(dto.getExpirationDate());

        // Завантаження категорії
        SupplyCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Категорію з ID " + dto.getCategoryId() + " не знайдено"
                ));

        // Завантаження складу (опційно)
        Warehouse warehouse = null;
        if (dto.getWarehouseId() != null) {
            warehouse = warehouseRepository.findById(dto.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Склад з ID " + dto.getWarehouseId() + " не знайдено"
                    ));
        }

        SupplyItem item = new SupplyItem();
        item.setName(dto.getName());
        item.setBatchNumber(dto.getBatchNumber());
        item.setCategory(category);
        item.setQuantity(dto.getQuantity());
        item.setUnit(dto.getUnit());
        item.setExpirationDate(dto.getExpirationDate());
        item.setHazardClass(dto.getHazardClass());
        item.setStorageConditions(dto.getStorageConditions());
        item.setWarehouse(warehouse);
        item.setStatus(dto.getStatus());

        SupplyItem saved = itemRepository.save(item);
        log.info("Supply item created with ID: {}", saved.getId());

        return toResponseDTO(saved);
    }

    /**
     * Отримати матеріал по ID
     */
    public SupplyItemResponseDTO getById(Long id) {
        log.debug("Fetching supply item with ID: {}", id);

        SupplyItem item = itemRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Матеріал з ID " + id + " не знайдено"
                ));

        return toResponseDTO(item);
    }

    /**
     * Отримати всі матеріали з фільтрацією
     */
    public List<SupplyItemResponseDTO> getAll(ItemStatus status, Long categoryId) {
        log.debug("Fetching all supply items with filters - status: {}, categoryId: {}",
                status, categoryId);

        List<SupplyItem> items;

        if (status != null) {
            items = itemRepository.findByStatus(status);
        } else if (categoryId != null) {
            items = itemRepository.findByCategoryId(categoryId);
        } else {
            items = itemRepository.findAllWithDetails();
        }

        return items.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Оновити матеріал
     */
    @Transactional
    public SupplyItemResponseDTO update(SupplyItemUpdateDTO dto) {
        log.info("Updating supply item with ID: {}", dto.getId());

        SupplyItem item = itemRepository.findByIdWithDetails(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Матеріал з ID " + dto.getId() + " не знайдено"
                ));

        // Оновити тільки ті поля, що передані
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }

        if (dto.getQuantity() != null) {
            item.setQuantity(dto.getQuantity());
        }

        if (dto.getUnit() != null) {
            item.setUnit(dto.getUnit());
        }

        if (dto.getExpirationDate() != null) {
            validateExpirationDate(dto.getExpirationDate());
            item.setExpirationDate(dto.getExpirationDate());
        }

        if (dto.getHazardClass() != null) {
            item.setHazardClass(dto.getHazardClass());
        }

        if (dto.getStorageConditions() != null) {
            item.setStorageConditions(dto.getStorageConditions());
        }

        if (dto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Склад з ID " + dto.getWarehouseId() + " не знайдено"
                    ));
            item.setWarehouse(warehouse);
        }

        if (dto.getStatus() != null) {
            item.setStatus(dto.getStatus());
        }

        SupplyItem updated = itemRepository.save(item);
        log.info("Supply item with ID {} updated successfully", updated.getId());

        return toResponseDTO(updated);
    }

    /**
     * Видалити матеріал
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting supply item with ID: {}", id);

        if (!itemRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Матеріал з ID " + id + " не знайдено"
            );
        }

        itemRepository.deleteById(id);
        log.info("Supply item with ID {} deleted successfully", id);
    }

    /**
     * Знайти матеріали з терміном що закінчується
     */
    public List<SupplyItemResponseDTO> findExpiringSoon(int daysThreshold) {
        log.debug("Finding items expiring in {} days", daysThreshold);

        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);

        List<SupplyItem> expiring = itemRepository.findByExpirationDateBefore(thresholdDate);

        return expiring.stream()
                .filter(item -> item.getExpirationDate() != null)
                .filter(item -> item.getExpirationDate().isAfter(LocalDate.now()))
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Перевірка терміну придатності
     */
    private void validateExpirationDate(LocalDate expirationDate) {
        if (expirationDate != null && expirationDate.isBefore(LocalDate.now())) {
            throw new BusinessLogicException(
                    "Не можна створити матеріал з минулим терміном придатності"
            );
        }
    }

    /**
     * Маппінг Entity -> DTO
     */
    private SupplyItemResponseDTO toResponseDTO(SupplyItem entity) {
        SupplyItemResponseDTO dto = new SupplyItemResponseDTO();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setBatchNumber(entity.getBatchNumber());

        // Конвертація вкладеної Category
        dto.setCategory(toCategoryDTO(entity.getCategory()));

        dto.setQuantity(entity.getQuantity());
        dto.setUnit(entity.getUnit());
        dto.setExpirationDate(entity.getExpirationDate());
        dto.setHazardClass(entity.getHazardClass());
        dto.setStorageConditions(entity.getStorageConditions());

        // Warehouse - тільки ID та назва
        if (entity.getWarehouse() != null) {
            dto.setWarehouseId(entity.getWarehouse().getId());
            dto.setWarehouseName(entity.getWarehouse().getName());
        }

        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    /**
     * Маппінг Category Entity -> DTO
     */
    private SupplyCategoryResponseDTO toCategoryDTO(SupplyCategory entity) {
        SupplyCategoryResponseDTO dto = new SupplyCategoryResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCode(entity.getCode());
        dto.setDescription(entity.getDescription());
        dto.setRequiresColdStorage(entity.getRequiresColdStorage());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
