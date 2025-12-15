package ua.edu.viti.military.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ua.edu.viti.military.entity.MovementType;

@Data
public class MovementRequestDTO {

    @NotNull(message = "ID матеріалу обов'язковий")
    @Positive
    private Long itemId;

    @NotNull(message = "Тип операції обов'язковий")
    private MovementType type;

    @NotNull(message = "Кількість обов'язкова")
    @Positive(message = "Кількість має бути позитивною")
    private Integer quantity;

    @Size(max = 100)
    private String recipientName;

    @Size(max = 50)
    private String recipientUnit;

    @Size(max = 500)
    private String notes;
}
