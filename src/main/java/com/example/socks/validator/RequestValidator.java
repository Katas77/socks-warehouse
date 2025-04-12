package com.example.socks.validator;

import com.example.socks.dto.CreateSockRequest;

public class RequestValidator {

    public void validate(CreateSockRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Запрос не может быть null");
        }
        if (request.getCottonPart() < 0 || request.getCottonPart() > 100) {
            throw new IllegalArgumentException("Процентное содержание хлопка должно быть от 0 до 100");
        }
        if (request.getQuantity() < 0) {
            throw new IllegalArgumentException("Количество носков для заказа должно быть неотрицательным");
        }
    }
}

