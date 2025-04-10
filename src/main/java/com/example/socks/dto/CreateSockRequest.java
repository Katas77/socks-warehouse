package com.example.socks.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateSockRequest {

    @Schema(description = "Цвет носков",
            example = "синий")
    private String color;
    @Size(min = 0, max = 100, message = "Процентное содержание хлопка в носках (от 0 до 100)")
    @Schema(description = "Процентное содержание хлопка в носках (от 0 до 100)",
            example = "80")
    private int cottonPart;
    @Schema(description = "Количество носков для заказа",
            example = "3")
    @Min(value = 0, message = "Количество носков для заказа должно быть неотрицательным")
    private int quantity;
}
