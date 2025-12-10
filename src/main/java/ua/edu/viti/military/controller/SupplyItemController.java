package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.SupplyItemCreateDTO;
import ua.edu.viti.military.dto.SupplyItemResponseDTO;
import ua.edu.viti.military.dto.SupplyItemUpdateDTO;
import ua.edu.viti.military.entity.ItemStatus;
import ua.edu.viti.military.service.SupplyItemService;

import java.util.List;

@RestController
@RequestMapping("/api/supply-items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Supply Items", description = "API для управління матеріалами на складі")
public class SupplyItemController {

    private final SupplyItemService itemService;

    @PostMapping
    @Operation(
            summary = "Створити новий матеріал",
            description = "Створює новий матеріал з унікальним номером партії"
    )
    public ResponseEntity<SupplyItemResponseDTO> create(
            @Valid @RequestBody SupplyItemCreateDTO dto) {

        log.info("REST request to create supply item: {}", dto.getBatchNumber());

        SupplyItemResponseDTO created = itemService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Отримати матеріал по ID",
            description = "Повертає деталі матеріалу по його унікальному ідентифікатору"
    )
    public ResponseEntity<SupplyItemResponseDTO> getById(
            @Parameter(description = "ID матеріалу")
            @PathVariable Long id) {

        log.info("REST request to get supply item with ID: {}", id);

        SupplyItemResponseDTO item = itemService.getById(id);

        return ResponseEntity.ok(item);
    }

    @GetMapping
    @Operation(
            summary = "Отримати всі матеріали",
            description = "Повертає список всіх матеріалів з можливістю фільтрації по статусу та категорії"
    )
    public ResponseEntity<List<SupplyItemResponseDTO>> getAll(
            @Parameter(description = "Фільтр по статусу матеріалу")
            @RequestParam(required = false) ItemStatus status,
            @Parameter(description = "Фільтр по ID категорії")
            @RequestParam(required = false) Long categoryId) {

        log.info("REST request to get all supply items with filters - status: {}, categoryId: {}",
                status, categoryId);

        List<SupplyItemResponseDTO> items = itemService.getAll(status, categoryId);

        return ResponseEntity.ok(items);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Оновити матеріал",
            description = "Оновлює дані про матеріал"
    )
    public ResponseEntity<SupplyItemResponseDTO> update(
            @Parameter(description = "ID матеріалу")
            @PathVariable Long id,
            @Valid @RequestBody SupplyItemUpdateDTO dto) {

        log.info("REST request to update supply item with ID: {}", id);

        dto.setId(id);
        SupplyItemResponseDTO updated = itemService.update(dto);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Видалити матеріал",
            description = "Видаляє матеріал по його ID"
    )
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID матеріалу")
            @PathVariable Long id) {

        log.info("REST request to delete supply item with ID: {}", id);

        itemService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/expiring-soon")
    @Operation(
            summary = "Матеріали з терміном що закінчується",
            description = "Повертає список матеріалів з терміном придатності " +
                    "що закінчується в найближчі N днів"
    )
    public ResponseEntity<List<SupplyItemResponseDTO>> getExpiringSoon(
            @Parameter(description = "Кількість днів (за замовчуванням 30)")
            @RequestParam(defaultValue = "30") int days) {

        log.info("REST request to get items expiring in {} days", days);

        List<SupplyItemResponseDTO> items = itemService.findExpiringSoon(days);

        return ResponseEntity.ok(items);
    }
}
