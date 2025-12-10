package ua.edu.viti.military.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.edu.viti.military.entity.HazardClass;
import ua.edu.viti.military.entity.ItemStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyItemCreateDTO {

    @NotBlank(message = "Назва матеріалу не може бути пустою")
    @Size(max = 200, message = "Назва матеріалу не може перевищувати 200 символів")
    private String name;

    @NotBlank(message = "Номер партії не може бути пустим")
    @Size(max = 50, message = "Номер партії не може перевищувати 50 символів")
    private String batchNumber;

    @NotNull(message = "Категорія має бути вказана")
    private Long categoryId;

    @NotNull(message = "Кількість має бути вказана")
    @Positive(message = "Кількість має бути додатним числом")
    private Integer quantity;

    @Size(max = 20, message = "Одиниця виміру не може перевищувати 20 символів")
    private String unit;

    @Future(message = "Термін придатності має бути в майбутньому")
    private LocalDate expirationDate;

    @NotNull(message = "Клас небезпеки має бути вказаний")
    private HazardClass hazardClass;

    @Size(max = 200, message = "Умови зберігання не можуть перевищувати 200 символів")
    private String storageConditions;

    private Long warehouseId;

    @NotNull(message = "Статус має бути вказаний")
    private ItemStatus status;
}
