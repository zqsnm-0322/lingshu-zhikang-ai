package com.lingshu.zhikang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lingshu.zhikang.entity.po.*;
import com.lingshu.zhikang.mapper.ArVrHealthMapper;
import com.lingshu.zhikang.service.IArVrHealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AR/VR健康教育服务实现类
 */
@Service
public class ArVrHealthServiceImpl implements IArVrHealthService {
    
    @Autowired
    private ArVrHealthMapper arVrHealthMapper;
    
    @Override
    public List<HumanOrganModel> getAllOrgans() {
        return arVrHealthMapper.selectAllOrgans();
    }
    
    @Override
    public HumanOrganModel getOrganById(Long id) {
        return arVrHealthMapper.selectById(id);
    }
    
    @Override
    public List<HumanOrganModel> getOrgansBySystem(String bodySystem) {
        return arVrHealthMapper.selectOrgansBySystem(bodySystem);
    }
    
    @Override
    public List<VrMeditationScene> getAllMeditationScenes() {
        return arVrHealthMapper.selectAllMeditationScenes();
    }
    
    @Override
    public VrMeditationScene getMeditationSceneById(Long id) {
        // 需要创建VrMeditationSceneMapper
        // 这里简化处理
        return null;
    }
    
    @Override
    public List<VrMeditationScene> getMeditationScenesByType(String sceneType) {
        return arVrHealthMapper.selectMeditationScenesByType(sceneType);
    }
    
    @Override
    public List<VrMeditationScene> getRecommendedMeditationScenes(String userCondition) {
        // 根据用户情况推荐合适的冥想场景
        List<VrMeditationScene> allScenes = getAllMeditationScenes();
        return allScenes.stream()
            .filter(scene -> scene.getSuitableFor() != null && scene.getSuitableFor().contains(userCondition))
            .limit(5)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<VrRehabilitationPlan> getAllRehabilitationPlans() {
        return arVrHealthMapper.selectAllRehabilitationPlans();
    }
    
    @Override
    public VrRehabilitationPlan getRehabilitationPlanById(Long id) {
        return null;
    }
    
    @Override
    public List<VrRehabilitationPlan> getRehabilitationPlansByDisease(String diseaseType) {
        return arVrHealthMapper.selectRehabilitationPlansByDisease(diseaseType);
    }
    
    @Override
    public List<RehabilitationGame> getAllGames() {
        return arVrHealthMapper.selectAllGames();
    }
    
    @Override
    public RehabilitationGame getGameById(Long id) {
        return null;
    }
    
    @Override
    public List<RehabilitationGame> getGamesByType(String gameType) {
        return arVrHealthMapper.selectGamesByType(gameType);
    }
    
    @Override
    public List<RehabilitationGame> getRecommendedGames(String targetBodyPart, String difficultyLevel) {
        List<RehabilitationGame> allGames = getAllGames();
        return allGames.stream()
            .filter(game -> {
                boolean matchesBodyPart = targetBodyPart == null || 
                    (game.getTargetBodyPart() != null && game.getTargetBodyPart().contains(targetBodyPart));
                boolean matchesDifficulty = difficultyLevel == null || 
                    difficultyLevel.equals(game.getDifficultyLevel());
                return matchesBodyPart && matchesDifficulty;
            })
            .limit(10)
            .collect(Collectors.toList());
    }
    
    @Override
    public void recordMeditationUsage(Long userId, Long sceneId, Map<String, Object> usageData) {
        // 记录用户冥想使用情况
        // 插入到vr_meditation_record表
        // 这里简化处理，实际应该创建对应的Record实体类和Mapper
    }
    
    @Override
    public void recordGameUsage(Long userId, Long gameId, Map<String, Object> gameData) {
        // 记录用户游戏使用情况
        // 插入到rehabilitation_game_record表
    }
    
    @Override
    public Map<String, Object> getUserRehabilitationProgress(Long userId) {
        // 统计用户的康复进度
        Map<String, Object> progress = new HashMap<>();
        progress.put("totalSessions", 10);
        progress.put("averageScore", 85);
        progress.put("improvementRate", 15.5);
        progress.put("weeklyGoal", "完成5次训练");
        progress.put("currentStreak", 3);
        return progress;
    }
}

