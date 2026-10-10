package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.*;
import com.lingshu.zhikang.entity.vo.ResponseResult;
import com.lingshu.zhikang.service.IArVrHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AR/VR健康教育与康复训练控制器
 */
@RestController
@RequestMapping("/api/arvr-health")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ArVrHealthController {
    
    @Autowired
    private IArVrHealthService arVrHealthService;
    
    /**
     * 获取所有人体器官模型
     */
    @GetMapping("/organs")
    public ResponseResult<List<HumanOrganModel>> getAllOrgans() {
        try {
            List<HumanOrganModel> organs = arVrHealthService.getAllOrgans();
            return ResponseResult.success(organs);
        } catch (Exception e) {
            return ResponseResult.error("获取器官模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据ID获取器官模型
     */
    @GetMapping("/organs/{id}")
    public ResponseResult<HumanOrganModel> getOrganById(@PathVariable Long id) {
        try {
            HumanOrganModel organ = arVrHealthService.getOrganById(id);
            return ResponseResult.success(organ);
        } catch (Exception e) {
            return ResponseResult.error("获取器官模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据身体系统获取器官
     */
    @GetMapping("/organs/system/{system}")
    public ResponseResult<List<HumanOrganModel>> getOrgansBySystem(@PathVariable String system) {
        try {
            List<HumanOrganModel> organs = arVrHealthService.getOrgansBySystem(system);
            return ResponseResult.success(organs);
        } catch (Exception e) {
            return ResponseResult.error("获取器官模型失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有VR冥想场景
     */
    @GetMapping("/meditation/scenes")
    public ResponseResult<List<VrMeditationScene>> getAllMeditationScenes() {
        try {
            List<VrMeditationScene> scenes = arVrHealthService.getAllMeditationScenes();
            return ResponseResult.success(scenes);
        } catch (Exception e) {
            return ResponseResult.error("获取冥想场景失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据类型获取冥想场景
     */
    @GetMapping("/meditation/scenes/type/{type}")
    public ResponseResult<List<VrMeditationScene>> getMeditationScenesByType(@PathVariable String type) {
        try {
            List<VrMeditationScene> scenes = arVrHealthService.getMeditationScenesByType(type);
            return ResponseResult.success(scenes);
        } catch (Exception e) {
            return ResponseResult.error("获取冥想场景失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取推荐的冥想场景
     */
    @GetMapping("/meditation/recommended")
    public ResponseResult<List<VrMeditationScene>> getRecommendedMeditationScenes(
            @RequestParam String condition) {
        try {
            List<VrMeditationScene> scenes = arVrHealthService.getRecommendedMeditationScenes(condition);
            return ResponseResult.success(scenes);
        } catch (Exception e) {
            return ResponseResult.error("获取推荐场景失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有康复训练计划
     */
    @GetMapping("/rehabilitation/plans")
    public ResponseResult<List<VrRehabilitationPlan>> getAllRehabilitationPlans() {
        try {
            List<VrRehabilitationPlan> plans = arVrHealthService.getAllRehabilitationPlans();
            return ResponseResult.success(plans);
        } catch (Exception e) {
            return ResponseResult.error("获取康复计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据疾病类型获取康复计划
     */
    @GetMapping("/rehabilitation/plans/disease/{disease}")
    public ResponseResult<List<VrRehabilitationPlan>> getRehabilitationPlansByDisease(
            @PathVariable String disease) {
        try {
            List<VrRehabilitationPlan> plans = arVrHealthService.getRehabilitationPlansByDisease(disease);
            return ResponseResult.success(plans);
        } catch (Exception e) {
            return ResponseResult.error("获取康复计划失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有康复游戏
     */
    @GetMapping("/games")
    public ResponseResult<List<RehabilitationGame>> getAllGames() {
        try {
            List<RehabilitationGame> games = arVrHealthService.getAllGames();
            return ResponseResult.success(games);
        } catch (Exception e) {
            return ResponseResult.error("获取康复游戏失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据游戏类型获取游戏
     */
    @GetMapping("/games/type/{type}")
    public ResponseResult<List<RehabilitationGame>> getGamesByType(@PathVariable String type) {
        try {
            List<RehabilitationGame> games = arVrHealthService.getGamesByType(type);
            return ResponseResult.success(games);
        } catch (Exception e) {
            return ResponseResult.error("获取康复游戏失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取推荐的康复游戏
     */
    @GetMapping("/games/recommended")
    public ResponseResult<List<RehabilitationGame>> getRecommendedGames(
            @RequestParam(required = false) String targetBodyPart,
            @RequestParam(required = false) String difficultyLevel) {
        try {
            List<RehabilitationGame> games = arVrHealthService.getRecommendedGames(
                targetBodyPart, difficultyLevel);
            return ResponseResult.success(games);
        } catch (Exception e) {
            return ResponseResult.error("获取推荐游戏失败: " + e.getMessage());
        }
    }
    
    /**
     * 记录VR冥想使用
     */
    @PostMapping("/meditation/record")
    public ResponseResult<String> recordMeditationUsage(@RequestBody Map<String, Object> requestData) {
        try {
            Long userId = Long.parseLong(requestData.get("userId").toString());
            Long sceneId = Long.parseLong(requestData.get("sceneId").toString());
            arVrHealthService.recordMeditationUsage(userId, sceneId, requestData);
            return ResponseResult.success("记录成功");
        } catch (Exception e) {
            return ResponseResult.error("记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 记录康复游戏使用
     */
    @PostMapping("/games/record")
    public ResponseResult<String> recordGameUsage(@RequestBody Map<String, Object> requestData) {
        try {
            Long userId = Long.parseLong(requestData.get("userId").toString());
            Long gameId = Long.parseLong(requestData.get("gameId").toString());
            arVrHealthService.recordGameUsage(userId, gameId, requestData);
            return ResponseResult.success("记录成功");
        } catch (Exception e) {
            return ResponseResult.error("记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户康复进度
     */
    @GetMapping("/rehabilitation/progress/{userId}")
    public ResponseResult<Map<String, Object>> getUserRehabilitationProgress(@PathVariable Long userId) {
        try {
            Map<String, Object> progress = arVrHealthService.getUserRehabilitationProgress(userId);
            return ResponseResult.success(progress);
        } catch (Exception e) {
            return ResponseResult.error("获取进度失败: " + e.getMessage());
        }
    }
}

