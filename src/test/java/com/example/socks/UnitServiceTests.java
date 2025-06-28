package com.example.socks;

import com.example.socks.dto.CreateSockRequest;
import com.example.socks.exception.BadRequestException;
import com.example.socks.exception.NotFoundException;
import com.example.socks.exception.RequestValidatorException;
import com.example.socks.model.Sock;
import com.example.socks.repository.SockRepository;
import com.example.socks.service.impl.SockServiceImpl;
import com.example.socks.validator.RequestValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UnitServiceTests {
    private SockRepository repository;
    private SockServiceImpl sockService;

    @BeforeEach
    void setup() {
        repository = mock(SockRepository.class);
        sockService = new SockServiceImpl(repository);
    }

    @Test
    void testIncome_SockExists() {

        CreateSockRequest request = CreateSockRequest.builder()
                .color("синий")
                .cottonPart(25)
                .quantity(100)
                .build();
        Sock existingSock = Sock.builder().color("синий").cottonPart(25).quantity(50).build();

        when(repository.findByColorAndCottonPart("синий", 25)).thenReturn(Optional.of(existingSock));

        String result = sockService.income(request);
        assertEquals("Носки с цветом 'синий' и содержанием хлопка 25% были успешно обновлены.", result);
        assertEquals(150, existingSock.getQuantity());
        Mockito.verify(repository).save(existingSock);
    }

    @Test
    void testIncome_NewSock() {
        CreateSockRequest request = CreateSockRequest.builder()
                .color("зеленый")
                .cottonPart(50)
                .quantity(200)
                .build();

        when(repository.findByColorAndCottonPart("зеленый", 50)).thenReturn(Optional.empty());
        String result = sockService.income(request);
        assertEquals("Добавлена новая партия носков с цветом 'зеленый' и содержанием хлопка 50%.", result);
        Mockito.verify(repository).save(any(Sock.class));
    }

    @Test
    void testOutcome_SockExistsAndSufficientQuantity() {
        CreateSockRequest request = CreateSockRequest.builder()
                .color("красный")
                .cottonPart(30)
                .quantity(50)
                .build();
        Sock existingSock = Sock.builder().color("красный").cottonPart(30).quantity(100).build();

        when(repository.findByColorAndCottonPart("красный", 30)).thenReturn(Optional.of(existingSock));

        String result = sockService.outcome(request);
        assertEquals("Носки с цветом 'красный' и содержанием хлопка 30% были успешно отгружены.", result);

    }

    @Test
    void testOutcome_SockExistsButInsufficientQuantity() {

        CreateSockRequest request = CreateSockRequest.builder()
                .color("красный")
                .cottonPart(30)
                .quantity(150)
                .build();
        Sock existingSock = Sock.builder().color("красный").cottonPart(30).quantity(100).build();

        when(repository.findByColorAndCottonPart("красный", 30)).thenReturn(Optional.of(existingSock));
        Assertions.assertThrows(BadRequestException.class,
                () -> sockService.outcome(request),
                "Недостаточное количество носков с цветом красный и содержанием хлопка 30%. Запрашиваемое количество: 150, доступное количество: 100");

    }

    @Test
    void testOutcome_SockNotFound() {
        CreateSockRequest request = CreateSockRequest.builder()
                .color("желтый")
                .cottonPart(20)
                .quantity(10)
                .build();
        when(repository.findByColorAndCottonPart("желтый", 20)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> sockService.outcome(request),
                "Носков с цветом желтый и содержанием хлопка 20% на складе нет.");

    }

    @Test
    void testGetSockCountByFilter() {
        when(repository.countSocksByFilter("синий", "greater", 10)).thenReturn(15);
        Integer count = sockService.getSockCountByFilter("синий", "greater", 10);
        assertEquals(15, count);
        Mockito.verify(repository).countSocksByFilter("синий", "greater", 10);
    }

    @Test
    void testUploadSocksBatch_Success() throws Exception {
        String content = "синий,25,100\nзеленый,50,200";
        MockMultipartFile file = new MockMultipartFile("file", "socks.csv", "text/csv", content.getBytes());
        String result = sockService.uploadSocksBatch(file);
        assertEquals("Партии носков успешно обработаны.", result);
        Mockito.verify(repository, Mockito.times(1)).save(any(Sock.class));
    }

    @Test
    void testUpdateSock_SockFound() {

        Long sockId = 1L;
        CreateSockRequest request = CreateSockRequest.builder()
                .color("синий")
                .cottonPart(30)
                .quantity(75)
                .build();
        Sock existingSock = Sock.builder().id(sockId).color("красный").cottonPart(30).quantity(100).build();
        when(repository.findById(sockId)).thenReturn(Optional.of(existingSock));
        String result = sockService.updateSock(sockId, request);
        assertEquals("Параметры носков успешно обновлены.", result);
        assertEquals("синий", existingSock.getColor());
        Mockito.verify(repository).save(existingSock);
    }

    @Test
    void testUpdateSock_SockNotFound() {

        Long sockId = 2L;
        CreateSockRequest request = CreateSockRequest.builder().color("синий").cottonPart(25).quantity(100).build();
        when(repository.findById(sockId)).thenReturn(Optional.empty());
        Assertions.assertThrows(NotFoundException.class,
                () -> sockService.updateSock(sockId,request),
                "Носки с id 2 не найдены");

    }

    @Test
    void testValidationScenarios() {
        final RequestValidator validator = new RequestValidator();

        assertThrows(RequestValidatorException.class, () -> {
            validator.validate(null);
        }, "Запрос не может быть null");


        CreateSockRequest requestNegativeCotton = new CreateSockRequest("красный", -1, 10);
        assertThrows(RequestValidatorException.class, () -> {
            validator.validate(requestNegativeCotton);
        }, "Процентное содержание хлопка должно быть от 0 до 100");


        CreateSockRequest requestExceedsCotton = new CreateSockRequest("красный", 101, 10);
        assertThrows(RequestValidatorException.class, () -> {
            validator.validate(requestExceedsCotton);
        }, "Процентное содержание хлопка должно быть от 0 до 100");


        CreateSockRequest requestNegativeQuantity = new CreateSockRequest("красный", 50, -1);
        assertThrows(RequestValidatorException.class, () -> {
            validator.validate(requestNegativeQuantity);
        }, "Количество носков для заказа должно быть неотрицательным");

        CreateSockRequest validRequest = new CreateSockRequest("красный", 50, 10);
        assertDoesNotThrow(() -> {
            validator.validate(validRequest);
        });
    }


}
