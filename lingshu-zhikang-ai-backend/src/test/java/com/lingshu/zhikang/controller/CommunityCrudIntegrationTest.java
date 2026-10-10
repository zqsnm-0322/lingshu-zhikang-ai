package com.lingshu.zhikang.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingshu.zhikang.entity.po.CommunityPost;
import com.lingshu.zhikang.service.ICommunityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CommunityCrudIntegrationTest {
    MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();
    ICommunityService service;

    @BeforeEach void setUp() {
        service = mock(ICommunityService.class);
        CommunityController controller = new CommunityController();
        ReflectionTestUtils.setField(controller, "communityService", service);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private CommunityPost samplePost() {
        CommunityPost post = new CommunityPost();
        post.setId(1L); post.setUserId(1L); post.setTitle("测试帖子"); post.setContent("内容"); post.setCategory("经验分享");
        return post;
    }

    @Test void completeCrudFlowIsExposed() throws Exception {
        when(service.createPost(any())).thenReturn(samplePost());
        when(service.updatePost(any())).thenReturn(samplePost());
        when(service.getPostList(1, 10)).thenReturn(List.of(samplePost()));
        when(service.deletePost(1L, 1L)).thenReturn(true);
        mvc.perform(post("/api/community/post").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(samplePost()))).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(get("/api/community/post/list").param("page", "1").param("pageSize", "10")).andExpect(status().isOk()).andExpect(jsonPath("$.data[0].id").value(1));
        mvc.perform(put("/api/community/post").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(samplePost()))).andExpect(status().isOk()).andExpect(jsonPath("$.code").value(200));
        mvc.perform(delete("/api/community/post/1").param("userId", "1")).andExpect(status().isOk()).andExpect(jsonPath("$.data").value(true));
    }
}
