package ua.edu.viti.military.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.entity.HazardClass;
import ua.edu.viti.military.entity.ItemStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyItemUpdateDTO {

    @NotNull(message = "ID має бути вказаний")
    private Long id;

    @Size(max = 200, message = "Назва матеріалу не може перевищувати 200 символів")
    private String name;

    @Positive(message = "Кількість має бути додатним числом")
    private Integer quantity;

    @Size(max = 20, message = "Одиниця виміру не може перевищувати 20 символів")
    private String unit;

    @Future(message = "Термін придатності має бути в майбутньому")
    private LocalDate expirationDate;

    private HazardClass hazardClass;

    @Size(max = 200, message = "Умови зберігання не можуть перевищувати 200 символів")
    private String storageConditions;

    private Long warehouseId;

    private ItemStatus status;
}
