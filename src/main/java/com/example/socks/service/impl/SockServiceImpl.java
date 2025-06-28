package com.example.socks.service.impl;

import com.example.socks.dto.CreateSockRequest;
import com.example.socks.exception.BadRequestException;
import com.example.socks.exception.NotFoundException;
import com.example.socks.model.Sock;
import com.example.socks.repository.SockRepository;
import com.example.socks.service.SockService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SockServiceImpl implements SockService {
    private final SockRepository repository;

    @Override
    public String income(CreateSockRequest request) {
        request.validate();
        Optional<Sock> existingSock = repository.findByColorAndCottonPart(request.getColor(), request.getCottonPart());
        return existingSock.map(sock -> updateExistingSock(sock, request.getQuantity()))
                .orElseGet(() -> createNewSock(request));

    }

    private String updateExistingSock(Sock sock, int quantity) {
        sock.setQuantity(sock.getQuantity() + quantity);
        repository.save(sock);
        return String.format("Носки с цветом '%s' и содержанием хлопка %d%% были успешно обновлены.", sock.getColor(), sock.getCottonPart());
    }

    private String createNewSock(CreateSockRequest request) {
        Sock newSock = Sock.builder()
                .color(request.getColor())
                .cottonPart(request.getCottonPart())
                .quantity(request.getQuantity())
                .build();
        repository.save(newSock);
        return String.format("Добавлена новая партия носков с цветом '%s' и содержанием хлопка %d%%.", request.getColor(), request.getCottonPart());
    }

    @Override
    public String outcome(CreateSockRequest request) {
        request.validate();
        Optional<Sock> existingSock = repository.findByColorAndCottonPart(request.getColor(), request.getCottonPart());
        Sock sock = existingSock.orElseThrow(() -> new NotFoundException(createNotFoundResponse(request)));
        return processSockOutcome(sock, request.getQuantity());
    }

    private String createNotFoundResponse(CreateSockRequest request) {
        return String.format("Носков с цветом '%s' и содержанием хлопка %d%% на складе нет.", request.getColor(), request.getCottonPart());
    }

    private String processSockOutcome(Sock sock, int quantity) {
        if (sock.getQuantity() >= quantity) {
            sock.setQuantity(sock.getQuantity() - quantity);
            repository.save(sock);
            return String.format("Носки с цветом '%s' и содержанием хлопка %d%% были успешно отгружены.", sock.getColor(), sock.getCottonPart());
        } else {
            createInsufficientQuantityResponse(sock, quantity);
            return "";
        }
    }

    private void createInsufficientQuantityResponse(Sock sock, int requestedQuantity) {
        String errorMsg = String.format("Недостаточно носков с цветом '%s' и содержанием хлопка %d%%." +
                        " Запрашиваемое количество: %d, доступное количество: %d",
                sock.getColor(), sock.getCottonPart(), requestedQuantity, sock.getQuantity());
        throw new BadRequestException(errorMsg);
    }

    @Override
    public Integer getSockCountByFilter(String color, String comparison, Integer cottonPart) {
        return Optional.ofNullable(repository.countSocksByFilter(color, comparison, cottonPart)).orElse(0);
    }


    @Override
    public String uploadSocksBatch(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineCounter = 0;
            while ((line = reader.readLine()) != null) {
                if (lineCounter++ == 0) {
                    continue;
                }
                processSockData(line);
            }
        } catch (IOException e) {
            log.error("Ошибка при обработке файла", e);
            throw new RuntimeException(e);
        }
        return "Партии носков успешно обработаны.";
    }

    @Override
    public String updateSock(Long id, CreateSockRequest request) {
        request.validate();
        return repository.findById(id).map(sock -> {
            sock.setColor(request.getColor());
            sock.setCottonPart(request.getCottonPart());
            sock.setQuantity(request.getQuantity());
            repository.save(sock);
            return "Параметры носков успешно обновлены.";
        }).orElseThrow(() -> new NotFoundException(String.format("Носки с id %d не найдены.", id)));
    }

    @Override
    public List<Sock> filterSocks(int minCottonPart, int maxCottonPart, String sortBy) {
        List<Sock> socks = repository.findByCottonPartBetween(minCottonPart, maxCottonPart);
        if ("color".equalsIgnoreCase(sortBy)) {
            socks.sort(Comparator.comparing(Sock::getColor));
        } else if ("cottonPart".equalsIgnoreCase(sortBy)) {
            socks.sort(Comparator.comparingInt(Sock::getCottonPart));
        }
        return socks;
    }


    private void processSockData(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Неверный формат CSV: ожидаются три поля.");
            }
            String color = parts[0].trim();
            int cottonPart = Integer.parseInt(parts[1].trim());
            int quantity = Integer.parseInt(parts[2].trim());
            Optional<Sock> existingSock = repository.findByColorAndCottonPart(color, cottonPart);
            Sock sock = existingSock.orElseGet(() ->
                    Sock.builder().color(color).cottonPart(cottonPart).quantity(0).build()
            );
            sock.setQuantity(sock.getQuantity() + quantity);
            repository.save(sock);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ошибка преобразования числа.", e);
        }
    }

    @PostConstruct
    private void init() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("data/sk.csv"))) {
            String line;
            int lineCounter = 0;
            while ((line = reader.readLine()) != null) {
                if (lineCounter++ == 0) {
                    continue;
                }
                processSockData(line);
            }
        } catch (IOException e) {
            log.error("Ошибка при инициализации данных", e);
            throw e;
        }
    }
}
