package com.example.socks.controller;

import com.example.socks.dto.CreateSockRequest;
import com.example.socks.service.SockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/socks")
@RequiredArgsConstructor
public class SockController {
    private final SockService sockService;


    @Operation(summary = "Добавление новой партии носков", description = "Позволяет добавить новую партию носков или обновить существующую.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Партия носков успешно добавлена или обновлена."),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса."),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера.")
    })
    @PostMapping("/income")
    public ResponseEntity<String> income(@RequestBody CreateSockRequest request) {
        return ResponseEntity.ok(sockService.income(request));
    }

    @Operation(summary = "Отгрузка носков", description = "Позволяет отгрузить указанное количество носков.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Носки успешно отгружены."),
            @ApiResponse(responseCode = "404", description = "Носки не найдены."),
            @ApiResponse(responseCode = "400", description = "Недостаточное количество носков для отгрузки.")
    })
    @PostMapping("/outcome")
    public ResponseEntity<String> outcome(@RequestBody CreateSockRequest request) {

        return sockService.outcome(request);
    }

    @Operation(summary = "Получение количества носков по фильтру", description = "Возвращает количество носков, соответствующих заданным критериям.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Количество носков успешно возвращено."),
            @ApiResponse(responseCode = "400", description = "Некорректные параметры фильтра."),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера.")
    })
    @GetMapping("")
    public ResponseEntity<Integer> getSockCount(@RequestParam(required = false) String color,
                                                @RequestParam(required = false) String comparison,
                                                @RequestParam(required = false) Integer cottonPart) {
        Integer count = sockService.getSockCountByFilter(color, comparison, cottonPart);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Загрузка партии носков", description = "Позволяет загрузить партию носков из CSV файла.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Партия носков успешно загружена."),
            @ApiResponse(responseCode = "400", description = "Ошибка в формате файла."),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера.")
    })
    @PostMapping(value = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadSocksBatch(@RequestParam("file") MultipartFile file) throws IOException {
        return sockService.uploadSocksBatch(file);
    }

    @Operation(summary = "Обновление параметров носков", description = "Позволяет изменить цвет, процент хлопка и количество носков.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Параметры носков успешно обновлены."),
            @ApiResponse(responseCode = "404", description = "Носки не найдены."),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updateSock(@PathVariable Long id, @RequestBody CreateSockRequest request) {
        return sockService.updateSock(id, request);
    }
}
