package com.example.socks.service;


import com.example.socks.dto.CreateSockRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface SockService {
   String income(CreateSockRequest request);

   ResponseEntity <String> outcome(CreateSockRequest request);

    Integer getSockCountByFilter(String color, String comparison, Integer cottonPart);

    ResponseEntity<String> uploadSocksBatch(MultipartFile file);

    ResponseEntity<String> updateSock(Long id, CreateSockRequest request);
}

