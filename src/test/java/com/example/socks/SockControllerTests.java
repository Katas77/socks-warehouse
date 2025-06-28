package com.example.socks;

import com.example.socks.controller.SockController;
import com.example.socks.dto.CreateSockRequest;
import com.example.socks.service.SockService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@Slf4j
class SockControllerTests {

    private static final String API_URL = "/api/socks";

    private SockService serviceMock;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        serviceMock = Mockito.mock(SockService.class);
        mockMvc = standaloneSetup(new SockController(serviceMock)).build();
    }

    @DisplayName("Test successful income operation")
    @Test
    void incomeTest() throws Exception {
        CreateSockRequest request = CreateSockRequest.builder()
                .color("blu")
                .cottonPart(25)
                .quantity(100)
                .build();

        Mockito.when(serviceMock.income(Mockito.any(CreateSockRequest.class))).thenReturn("The sock batch was successfully added or updated.");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(request);
        String expectedResponse = "The sock batch was successfully added or updated.";

        MvcResult result = mockMvc.perform(post(API_URL + "/income")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        log.info("Income HTTP response status: {}", result.getResponse().getStatus());
        String actualResponse = result.getResponse().getContentAsString();
        Mockito.verify(serviceMock).income(Mockito.any(CreateSockRequest.class));
        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("Test successful outcome operation")
    @Test
    void outcomeTest() throws Exception {
        CreateSockRequest request = CreateSockRequest.builder()
                .color("синий")
                .cottonPart(25)
                .quantity(50)
                .build();

        Mockito.when(serviceMock.outcome(Mockito.any(CreateSockRequest.class)))
                .thenReturn("Socks have been shipped successfully.");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(request);
        String expectedResponse = "Socks have been shipped successfully.";

        MvcResult result = mockMvc.perform(post(API_URL + "/outcome")
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        log.info("Outcome HTTP response status: {}", result.getResponse().getStatus());

        String actualResponse = result.getResponse().getContentAsString();

        Mockito.verify(serviceMock).outcome(Mockito.any(CreateSockRequest.class));
        assertEquals(expectedResponse, actualResponse);
    }

    @DisplayName("Test get sock count")
    @Test
    void getSockCountTest() throws Exception {
        String color = "синий";
        String comparison = "greater";
        int cottonPart = 25;

        Mockito.when(serviceMock.getSockCountByFilter(color, comparison, cottonPart)).thenReturn(10);

        MvcResult result = mockMvc.perform(get(API_URL)
                        .param("color", color)
                        .param("comparison", comparison)
                        .param("cottonPart", String.valueOf(cottonPart)))
                .andExpect(status().isOk())
                .andReturn();

        int actualCount = Integer.parseInt(result.getResponse().getContentAsString());
        log.info("Sock count HTTP response status: {}", result.getResponse().getStatus());
        assertEquals(10, actualCount);
        Mockito.verify(serviceMock).getSockCountByFilter(color, comparison, cottonPart);
    }

    @DisplayName("Test upload socks batch")
    @Test
    void uploadSocksBatchTest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "socks.csv", MediaType.TEXT_PLAIN_VALUE, "color,cottonPart,quantity\nсиний,25,100".getBytes());

        Mockito.when(serviceMock.uploadSocksBatch(file)).thenReturn("The batch of socks has been successfully loaded");

        MvcResult result = mockMvc.perform(multipart(API_URL + "/batch") // изменено с post на multipart
                        .file(file))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = result.getResponse().getContentAsString();
        log.info("Upload batch HTTP response status: {}", result.getResponse().getStatus());
        assertEquals("The batch of socks has been successfully loaded", actualResponse);
        Mockito.verify(serviceMock).uploadSocksBatch(file);
    }

    @DisplayName("Test update sock")
    @Test
    void updateSockTest() throws Exception {
        Long sockId = 1L;
        CreateSockRequest request = CreateSockRequest.builder()
                .color("синий")
                .cottonPart(40)
                .quantity(80)
                .build();
        Mockito.when(serviceMock.updateSock(Mockito.eq(sockId), Mockito.any(CreateSockRequest.class))).thenReturn("Sock parameters have been updated successfully.");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonContent = objectMapper.writeValueAsString(request);
        String expectedResponse = "Sock parameters have been updated successfully.";
        MvcResult result = mockMvc.perform(put(API_URL + "/" + sockId)
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        String actualResponse = result.getResponse().getContentAsString();
        log.info("Update sock HTTP response status: {}", result.getResponse().getStatus());
        assertEquals(expectedResponse, actualResponse);
        Mockito.verify(serviceMock).updateSock(Mockito.eq(sockId), Mockito.any(CreateSockRequest.class));
    }

}

