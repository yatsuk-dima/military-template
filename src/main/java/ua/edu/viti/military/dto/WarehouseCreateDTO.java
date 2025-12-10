package ua.edu.viti.military.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseCreateDTO {

    @NotBlank(message = "Назва складу не може бути пустою")
    @Size(max = 100, message = "Назва складу не може перевищувати 100 символів")
    private String name;

    @NotBlank(message = "Код складу не може бути пустим")
    @Size(max = 20, message = "Код складу не може перевищувати 20 символів")
    private String code;

    @Size(max = 200, message = "Адреса не може перевищувати 200 символів")
    private String address;

    @PositiveOrZero(message = "Місткість має бути невід'ємним числом")
    private Integer capacity;

    @PositiveOrZero(message = "Заповненість має бути невід'ємним числом")
    private Integer currentOccupancy;

    private Boolean hasRefrigeration;
}
