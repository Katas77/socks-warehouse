package com.example.socks.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Процентное содержание хлопка в носках (от 0 до 100)",
            example = "80")
    private int cottonPart;

    @Schema(description = "Количество носков для заказа",
            example = "3")
    private int quantity;
}

