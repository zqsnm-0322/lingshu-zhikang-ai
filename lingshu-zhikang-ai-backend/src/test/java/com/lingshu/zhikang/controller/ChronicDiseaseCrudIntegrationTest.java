package com.lingshu.zhikang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingshu.zhikang.entity.po.ChronicDiseaseRecord;
import com.lingshu.zhikang.service.IChronicDiseaseService;
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

class ChronicDiseaseCrudIntegrationTest {
    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();
    IChronicDiseaseService service;

    @BeforeEach void setUp() { service = mock(IChronicDiseaseService.class); mvc = MockMvcBuilders.standaloneSetup(new ChronicDiseaseController(service)).build(); }

    private ChronicDiseaseRecord record() {
        ChronicDiseaseRecord record = new ChronicDiseaseRecord();
        record.setId(1L); record.setUserId(1L); record.setType("weight");
        record.setDate("2026-09-28"); record.setTime("08:00"); record.setValue(65.0);
        return record;
    }

    @Test void completeCrudFlowIsExposed() throws Exception {
        when(service.addRecord(any())).thenReturn(record());
        when(service.updateRecord(any())).thenReturn(record());
        when(service.getAllRecords(1L)).thenReturn(List.of(record()));

        mvc.perform(post("/api/chronic-disease/add").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(record()))).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(get("/api/chronic-disease/all").param("userId", "1")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1));
        mvc.perform(put("/api/chronic-disease/update").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(record()))).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(delete("/api/chronic-disease/delete/1").param("userId", "1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
}
