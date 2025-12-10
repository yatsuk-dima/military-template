package ua.edu.viti.military.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplyCategoryCreateDTO {

    @NotBlank(message = "Назва категорії не може бути пустою")
    @Size(max = 100, message = "Назва категорії не може перевищувати 100 символів")
    private String name;

    @NotBlank(message = "Код категорії не може бути пустим")
    @Size(max = 20, message = "Код категорії не може перевищувати 20 символів")
    private String code;

    @Size(max = 500, message = "Опис не може перевищувати 500 символів")
    private String description;

    private Boolean requiresColdStorage;
}
