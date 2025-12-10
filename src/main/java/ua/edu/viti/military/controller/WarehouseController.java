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
import ua.edu.viti.military.dto.WarehouseCreateDTO;
import ua.edu.viti.military.dto.WarehouseResponseDTO;
import ua.edu.viti.military.service.WarehouseService;

import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Warehouses", description = "API для управління складами")
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    @Operation(
            summary = "Створити новий склад",
            description = "Створює новий склад з унікальним кодом"
    )
    public ResponseEntity<WarehouseResponseDTO> create(
            @Valid @RequestBody WarehouseCreateDTO dto) {

        log.info("REST request to create warehouse: {}", dto.getCode());

        WarehouseResponseDTO created = warehouseService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Отримати склад по ID",
            description = "Повертає деталі складу по його унікальному ідентифікатору"
    )
    public ResponseEntity<WarehouseResponseDTO> getById(
            @Parameter(description = "ID складу")
            @PathVariable Long id) {

        log.info("REST request to get warehouse with ID: {}", id);

        WarehouseResponseDTO warehouse = warehouseService.getById(id);

        return ResponseEntity.ok(warehouse);
    }

    @GetMapping
    @Operation(
            summary = "Отримати всі склади",
            description = "Повертає список всіх складів з можливістю фільтрації по наявності холодильного обладнання"
    )
    public ResponseEntity<List<WarehouseResponseDTO>> getAll(
            @Parameter(description = "Фільтр по наявності холодильного обладнання")
            @RequestParam(required = false) Boolean hasRefrigeration) {

        log.info("REST request to get all warehouses with filter - hasRefrigeration: {}",
                hasRefrigeration);

        List<WarehouseResponseDTO> warehouses = warehouseService.getAll(hasRefrigeration);

        return ResponseEntity.ok(warehouses);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Видалити склад",
            description = "Видаляє склад по його ID"
    )
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID складу")
            @PathVariable Long id) {

        log.info("REST request to delete warehouse with ID: {}", id);

        warehouseService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
