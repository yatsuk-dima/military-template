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
import ua.edu.viti.military.dto.SupplyCategoryCreateDTO;
import ua.edu.viti.military.dto.SupplyCategoryResponseDTO;
import ua.edu.viti.military.service.SupplyCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/supply-categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Supply Categories", description = "API для управління категоріями постачання")
public class SupplyCategoryController {

    private final SupplyCategoryService categoryService;

    @PostMapping
    @Operation(
            summary = "Створити нову категорію",
            description = "Створює нову категорію постачання з унікальним кодом і назвою"
    )
    public ResponseEntity<SupplyCategoryResponseDTO> create(
            @Valid @RequestBody SupplyCategoryCreateDTO dto) {

        log.info("REST request to create supply category: {}", dto.getCode());

        SupplyCategoryResponseDTO created = categoryService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Отримати категорію по ID",
            description = "Повертає деталі категорії постачання по її унікальному ідентифікатору"
    )
    public ResponseEntity<SupplyCategoryResponseDTO> getById(
            @Parameter(description = "ID категорії")
            @PathVariable Long id) {

        log.info("REST request to get supply category with ID: {}", id);

        SupplyCategoryResponseDTO category = categoryService.getById(id);

        return ResponseEntity.ok(category);
    }

    @GetMapping
    @Operation(
            summary = "Отримати всі категорії",
            description = "Повертає список всіх категорій постачання"
    )
    public ResponseEntity<List<SupplyCategoryResponseDTO>> getAll() {
        log.info("REST request to get all supply categories");

        List<SupplyCategoryResponseDTO> categories = categoryService.getAll();

        return ResponseEntity.ok(categories);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Видалити категорію",
            description = "Видаляє категорію постачання по її ID"
    )
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID категорії")
            @PathVariable Long id) {

        log.info("REST request to delete supply category with ID: {}", id);

        categoryService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
