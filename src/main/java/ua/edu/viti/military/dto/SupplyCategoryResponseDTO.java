package ua.edu.viti.military.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyCategoryResponseDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private Boolean requiresColdStorage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
