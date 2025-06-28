package com.example.socks.validator;

import com.example.socks.dto.CreateSockRequest;
import com.example.socks.exception.RequestValidatorException;

public class RequestValidator {

    public void validate(CreateSockRequest request) {
        if (request == null) {
            throw new RequestValidatorException("Запрос не может быть null");
        }
        if (request.getCottonPart() < 0 || request.getCottonPart() > 100) {
            throw new RequestValidatorException("Процентное содержание хлопка должно быть от 0 до 100");
        }
        if (request.getQuantity() < 0) {
            throw new RequestValidatorException("Количество носков для заказа должно быть неотрицательным");
        }
    }
}

