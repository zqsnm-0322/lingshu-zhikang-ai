package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.*;

import java.util.List;
import java.util.Map;

/**
 * AR/VR健康教育服务接口
 */
public interface IArVrHealthService {
    
    /**
     * 获取所有人体器官模型
     */
    List<HumanOrganModel> getAllOrgans();
    
    /**
     * 根据ID获取器官模型
     */
    HumanOrganModel getOrganById(Long id);
    
    /**
     * 根据身体系统获取器官
     */
    List<HumanOrganModel> getOrgansBySystem(String bodySystem);
    
    /**
     * 获取所有VR冥想场景
     */
    List<VrMeditationScene> getAllMeditationScenes();
    
    /**
     * 根据ID获取冥想场景
     */
    VrMeditationScene getMeditationSceneById(Long id);
    
    /**
     * 根据类型获取冥想场景
     */
    List<VrMeditationScene> getMeditationScenesByType(String sceneType);
    
    /**
     * 获取推荐的冥想场景（根据用户情况）
     */
    List<VrMeditationScene> getRecommendedMeditationScenes(String userCondition);
    
    /**
     * 获取所有康复训练计划
     */
    List<VrRehabilitationPlan> getAllRehabilitationPlans();
    
    /**
     * 根据ID获取康复计划
     */
    VrRehabilitationPlan getRehabilitationPlanById(Long id);
    
    /**
     * 根据疾病类型获取康复计划
     */
    List<VrRehabilitationPlan> getRehabilitationPlansByDisease(String diseaseType);
    
    /**
     * 获取所有康复游戏
     */
    List<RehabilitationGame> getAllGames();
    
    /**
     * 根据ID获取游戏
     */
    RehabilitationGame getGameById(Long id);
    
    /**
     * 根据游戏类型获取游戏
     */
    List<RehabilitationGame> getGamesByType(String gameType);
    
    /**
     * 获取推荐的康复游戏（根据用户情况）
     */
    List<RehabilitationGame> getRecommendedGames(String targetBodyPart, String difficultyLevel);
    
    /**
     * 记录VR冥想使用
     */
    void recordMeditationUsage(Long userId, Long sceneId, Map<String, Object> usageData);
    
    /**
     * 记录康复游戏使用
     */
    void recordGameUsage(Long userId, Long gameId, Map<String, Object> gameData);
    
    /**
     * 获取用户的康复进度统计
     */
    Map<String, Object> getUserRehabilitationProgress(Long userId);
}

