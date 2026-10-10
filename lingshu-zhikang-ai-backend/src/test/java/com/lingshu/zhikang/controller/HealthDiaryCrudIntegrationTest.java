package com.lingshu.zhikang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingshu.zhikang.entity.po.HealthDiary;
import com.lingshu.zhikang.service.IHealthDiaryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HealthDiaryCrudIntegrationTest {
    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();
    IHealthDiaryService service;

    @BeforeEach void setUp() { service = mock(IHealthDiaryService.class); mvc = MockMvcBuilders.standaloneSetup(new HealthDiaryController(service)).build(); }

    private HealthDiary diary() {
        HealthDiary diary = new HealthDiary();
        diary.setId(1L); diary.setUserId(1L); diary.setDate("2026-09-28"); diary.setMood(4); diary.setSleep(8.0); diary.setTags("运动,休息");
        return diary;
    }

    @Test void completeCrudFlowIsExposed() throws Exception {
        when(service.addDiary(any())).thenReturn(diary());
        when(service.updateDiary(any())).thenReturn(diary());
        when(service.getDiariesByUserId(1L)).thenReturn(List.of(diary()));

        mvc.perform(post("/api/health-diary/add").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(diary())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(get("/api/health-diary/list").param("userId", "1")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1));
        mvc.perform(put("/api/health-diary/update").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(diary())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(delete("/api/health-diary/delete/1").param("userId", "1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
}
