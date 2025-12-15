package ua.edu.viti.military.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.edu.viti.military.dto.MovementRequestDTO;
import ua.edu.viti.military.dto.MovementResponseDTO;
import ua.edu.viti.military.service.SupplyMovementService;

import java.util.List;

@RestController
@RequestMapping("/api/movements")
@RequiredArgsConstructor
@Tag(name = "Supply Movements", description = "Управління операціями з матеріалами")
public class SupplyMovementController {

    private final SupplyMovementService movementService;

    @PostMapping("/issue")
    @Operation(summary = "Видача матеріалів", description = "Видати матеріали зі складу")
    public ResponseEntity<MovementResponseDTO> issueItem(@Valid @RequestBody MovementRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movementService.issueItem(dto));
    }

    @PostMapping("/return")
    @Operation(summary = "Повернення матеріалів", description = "Повернути матеріали на склад")
    public ResponseEntity<MovementResponseDTO> returnItem(@Valid @RequestBody MovementRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movementService.returnItem(dto));
    }

    @PostMapping("/write-off")
    @Operation(summary = "Списання матеріалів", description = "Списати матеріали (прострочені або пошкоджені)")
    public ResponseEntity<MovementResponseDTO> writeOffItem(@Valid @RequestBody MovementRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movementService.writeOffItem(dto));
    }

    @GetMapping("/history/{itemId}")
    @Operation(summary = "Історія операцій", description = "Отримати історію всіх операцій для конкретного матеріалу")
    public ResponseEntity<List<MovementResponseDTO>> getMovementHistory(@PathVariable Long itemId) {
        return ResponseEntity.ok(movementService.getMovementHistory(itemId));
    }

    @GetMapping("/recent")
    @Operation(summary = "Останні операції", description = "Отримати останні 10 операцій")
    public ResponseEntity<List<MovementResponseDTO>> getRecentMovements() {
        return ResponseEntity.ok(movementService.getRecentMovements());
    }
}
