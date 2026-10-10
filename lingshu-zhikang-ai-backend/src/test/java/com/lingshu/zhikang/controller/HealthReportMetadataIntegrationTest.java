package com.lingshu.zhikang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingshu.zhikang.entity.po.HealthReportMetadata;
import com.lingshu.zhikang.mapper.HealthReportMetadataMapper;
import com.lingshu.zhikang.repository.FileRepository;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HealthReportMetadataIntegrationTest {
    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();
    HealthReportMetadataMapper mapper;

    @BeforeEach void setUp() {
        mapper = mock(HealthReportMetadataMapper.class);
        HealthReportController controller = new HealthReportController(mock(FileRepository.class), mock(MinioClient.class), mapper);
        ReflectionTestUtils.setField(controller, "bucketName", "test-bucket");
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private HealthReportMetadata report() {
        HealthReportMetadata report = new HealthReportMetadata();
        report.setId(1L); report.setUserId(1L); report.setFileId("report.pdf"); report.setTitle("年度体检");
        return report;
    }

    @Test void metadataReadAndUpdateAreExposed() throws Exception {
        when(mapper.selectList(any())).thenReturn(List.of(report()));
        when(mapper.selectOne(any())).thenReturn(report());
        mvc.perform(get("/api/health-report/list").param("userId", "1")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].fileId").value("report.pdf"));
        mvc.perform(put("/api/health-report/1").param("userId", "1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("title", "新标题"))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.title").value("新标题"));
    }
}
