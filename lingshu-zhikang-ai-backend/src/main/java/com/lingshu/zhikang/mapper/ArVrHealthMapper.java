package com.lingshu.zhikang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingshu.zhikang.entity.po.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AR/VR健康教育Mapper接口
 */
@Mapper
public interface ArVrHealthMapper extends BaseMapper<HumanOrganModel> {
    
    /**
     * 查询所有人体器官模型
     */
    List<HumanOrganModel> selectAllOrgans();
    
    /**
     * 根据身体系统查询器官
     */
    List<HumanOrganModel> selectOrgansBySystem(String bodySystem);
    
    /**
     * 查询所有VR冥想场景
     */
    List<VrMeditationScene> selectAllMeditationScenes();
    
    /**
     * 根据类型查询冥想场景
     */
    List<VrMeditationScene> selectMeditationScenesByType(String sceneType);
    
    /**
     * 查询所有康复训练计划
     */
    List<VrRehabilitationPlan> selectAllRehabilitationPlans();
    
    /**
     * 根据疾病类型查询康复计划
     */
    List<VrRehabilitationPlan> selectRehabilitationPlansByDisease(String diseaseType);
    
    /**
     * 查询所有康复游戏
     */
    List<RehabilitationGame> selectAllGames();
    
    /**
     * 根据游戏类型查询游戏
     */
    List<RehabilitationGame> selectGamesByType(String gameType);
}

