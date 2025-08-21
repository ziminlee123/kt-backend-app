package com.kt.backendapp.controller;

import com.kt.backendapp.dto.FestivalRequestDTO;
import com.kt.backendapp.service.FestivalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FestivalController.class)
class FestivalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FestivalService festivalService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("축제 목록 조회 API")
    void getAllFestivals() throws Exception {
        mockMvc.perform(get("/api/festivals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("축제 생성 API - 유효한 데이터")
    void createFestivalWithValidData() throws Exception {
        // given
        FestivalRequestDTO requestDTO = FestivalRequestDTO.builder()
                .name("테스트 축제")
                .location("서울특별시")
                .startDate(LocalDate.of(2024, 12, 1))
                .endDate(LocalDate.of(2024, 12, 3))
                .target("10,000명")
                .description("테스트용 축제입니다")
                .status("before")
                .build();

        // when & then
        mockMvc.perform(post("/api/festivals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("축제 생성 API - 잘못된 날짜 범위")
    void createFestivalWithInvalidDateRange() throws Exception {
        // given
        FestivalRequestDTO requestDTO = FestivalRequestDTO.builder()
                .name("테스트 축제")
                .location("서울특별시")
                .startDate(LocalDate.of(2024, 12, 5))  // 종료일보다 늦음
                .endDate(LocalDate.of(2024, 12, 3))
                .target("10,000명")
                .description("테스트용 축제입니다")
                .status("before")
                .build();

        // when & then
        mockMvc.perform(post("/api/festivals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("축제 생성 API - 필수 필드 누락")
    void createFestivalWithMissingFields() throws Exception {
        // given
        FestivalRequestDTO requestDTO = FestivalRequestDTO.builder()
                .name("") // 빈 이름
                .location("서울특별시")
                .startDate(LocalDate.of(2024, 12, 1))
                .endDate(LocalDate.of(2024, 12, 3))
                .target("10,000명")
                .status("before")
                .build();

        // when & then
        mockMvc.perform(post("/api/festivals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("축제 상태 변경 API")
    void updateFestivalStatus() throws Exception {
        mockMvc.perform(patch("/api/festivals/1/status")
                .param("status", "during"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("현재 운영 중인 축제 조회 API")
    void getRunningFestivals() throws Exception {
        mockMvc.perform(get("/api/festivals/running"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }

    @Test
    @DisplayName("축제 통계 조회 API")
    void getFestivalStatistics() throws Exception {
        mockMvc.perform(get("/api/festivals/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
