package com.lingshu.zhikang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingshu.zhikang.entity.po.MedicineReminder;
import com.lingshu.zhikang.service.MedicineReminderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MedicineReminderCrudIntegrationTest {
    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    MedicineReminderService service;

    @BeforeEach void setUp() { service = mock(MedicineReminderService.class); mvc = MockMvcBuilders.standaloneSetup(new MedicineReminderController(service)).build(); }

    private MedicineReminder reminder() {
        MedicineReminder item = new MedicineReminder();
        item.setId(1L); item.setUserId(1L); item.setName("维生素C"); item.setDosage("1片");
        item.setReminderTimes("[\"08:00\"]"); item.setStartDate(LocalDate.now()); item.setEndDate(LocalDate.now().plusDays(7)); item.setIsActive(true);
        return item;
    }

    @Test void completeCrudFlowIsExposed() throws Exception {
        when(service.addMedicine(any())).thenReturn(reminder());
        when(service.updateMedicine(any())).thenReturn(reminder());
        when(service.getMedicineList(1L)).thenReturn(List.of(reminder()));

        mvc.perform(post("/api/medicine/add").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reminder())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(get("/api/medicine/list").param("userId", "1")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1));
        mvc.perform(put("/api/medicine/update").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(reminder())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(delete("/api/medicine/delete/1")).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
    }
}
