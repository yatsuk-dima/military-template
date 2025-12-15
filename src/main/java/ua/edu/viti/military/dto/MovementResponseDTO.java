package ua.edu.viti.military.dto;

import lombok.Data;
import ua.edu.viti.military.entity.MovementType;

import java.time.LocalDateTime;

@Data
public class MovementResponseDTO {
    private Long id;
    private Long itemId;
    private String itemName;
    private MovementType type;
    private Integer quantity;
    private String recipientName;
    private String recipientUnit;
    private String notes;
    private String performedBy;
    private LocalDateTime performedAt;
}
