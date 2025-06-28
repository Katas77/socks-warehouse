package com.example.socks.service;

import com.example.socks.dto.CreateSockRequest;
import com.example.socks.model.Sock;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface SockService {
    String income(CreateSockRequest request);

    String outcome(CreateSockRequest request);

    Integer getSockCountByFilter(String color, String comparison, Integer cottonPart);

    String uploadSocksBatch(MultipartFile file);

    String updateSock(Long id, CreateSockRequest request);

    List<Sock> filterSocks(int minCottonPart, int maxCottonPart, String sortBy);
}

