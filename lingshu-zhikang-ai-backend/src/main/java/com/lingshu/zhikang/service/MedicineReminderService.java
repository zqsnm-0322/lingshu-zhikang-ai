package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.MedicineRecord;
import com.lingshu.zhikang.entity.po.MedicineReminder;
import com.lingshu.zhikang.mapper.MedicineRecordMapper;
import com.lingshu.zhikang.mapper.MedicineReminderMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用药提醒服务
 */
@Slf4j
@Service
public class MedicineReminderService {

    private final org.springframework.ai.chat.client.ChatClient chatClient;
    private final MedicineReminderMapper medicineReminderMapper;
    private final MedicineRecordMapper medicineRecordMapper;

    public MedicineReminderService(org.springframework.ai.chat.client.ChatClient chatClient,
                                   MedicineReminderMapper medicineReminderMapper,
                                   MedicineRecordMapper medicineRecordMapper) {
        this.chatClient = chatClient;
        this.medicineReminderMapper = medicineReminderMapper;
        this.medicineRecordMapper = medicineRecordMapper;
    }

    /**
     * 检查药物相互作用
     * @param newMedicineName 新药名称
     * @param userId 用户ID（用于查询当前正在服用的药物）
     */
    public Map<String, Object> checkDrugInteraction(String newMedicineName, Long userId) {
        log.info("检查药物相互作用: new={}, userId={}", newMedicineName, userId);
        
        // 获取用户当前正在服用的药物
        List<String> currentMedicines = getMedicineList(userId).stream()
                .filter(MedicineReminder::getIsActive)
                .map(MedicineReminder::getName)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("currentMedicines", currentMedicines);
        
        if (currentMedicines.isEmpty()) {
            result.put("hasRisk", false);
            result.put("message", "当前没有正在服用的药物，无相互作用风险。");
            return result;
        }
        
        try {
            String medicinesStr = String.join(", ", currentMedicines);
            String prompt = """
                请分析以下药物之间的相互作用风险。
                新药：%s
                正在服用的药物：%s
                
                请以JSON格式返回：
                - hasRisk: true/false
                - riskLevel: 高/中/低/无
                - warning: 警告信息（如果有风险，请详细说明，100字以内）
                - suggestion: 用药建议
                """.formatted(newMedicineName, medicinesStr);
            
            String aiResponse = chatClient.prompt().user(prompt).call().content();
            result.put("analysis", aiResponse); // 前端解析JSON
            result.put("success", true);
            
        } catch (Exception e) {
            log.error("药物分析失败", e);
            result.put("success", false);
            result.put("message", "AI分析暂时不可用");
        }
        
        return result;
    }

    /**
     * 获取用户所有用药提醒
     */
    public List<MedicineReminder> getMedicineList(Long userId) {
        return medicineReminderMapper.selectList(new LambdaQueryWrapper<MedicineReminder>()
                .eq(MedicineReminder::getUserId, userId)
                .orderByDesc(MedicineReminder::getCreateTime));
    }

    /**
     * 获取今日用药提醒
     */
    public Map<String, Object> getTodayReminders(Long userId) {
        LocalDate today = LocalDate.now();
        List<MedicineReminder> activeMedicines = medicineReminderMapper.selectList(
                new LambdaQueryWrapper<MedicineReminder>()
                        .eq(MedicineReminder::getUserId, userId)
                        .eq(MedicineReminder::getIsActive, true)
                        .le(MedicineReminder::getStartDate, today)
                        .ge(MedicineReminder::getEndDate, today));

        List<Map<String, Object>> todayList = new ArrayList<>();
        
        for (MedicineReminder medicine : activeMedicines) {
            // 解析提醒时间
            String[] times = medicine.getReminderTimes()
                    .replace("[", "")
                    .replace("]", "")
                    .replace("\"", "")
                    .split(",");
            
            for (String time : times) {
                time = time.trim();
                MedicineRecord record = medicineRecordMapper.selectOne(new LambdaQueryWrapper<MedicineRecord>()
                        .eq(MedicineRecord::getUserId, userId)
                        .eq(MedicineRecord::getReminderId, medicine.getId())
                        .eq(MedicineRecord::getRecordDate, today)
                        .eq(MedicineRecord::getReminderTime, time));
                
                String status = "pending";
                String statusText = "待服用";
                
                if (record != null) {
                    status = record.getStatus();
                    statusText = getStatusText(status);
                } else {
                    // 判断是否已过期
                    LocalTime reminderTime = LocalTime.parse(time);
                    if (LocalTime.now().isAfter(reminderTime)) {
                        status = "overdue";
                        statusText = "已过期";
                    }
                }
                
                Map<String, Object> item = new HashMap<>();
                item.put("id", medicine.getId() + "_" + time);
                item.put("medicineId", medicine.getId());
                item.put("name", medicine.getName());
                item.put("dosage", medicine.getDosage());
                item.put("time", time);
                item.put("notes", medicine.getNotes());
                item.put("status", status);
                item.put("statusText", statusText);
                
                todayList.add(item);
            }
        }
        
        // 按时间排序
        todayList.sort(Comparator.comparing(item -> (String) item.get("time")));
        
        // 计算统计数据
        long completedCount = todayList.stream()
                .filter(item -> "completed".equals(item.get("status")))
                .count();
        int completionRate = todayList.isEmpty() ? 0 : (int) ((completedCount * 100) / todayList.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("todayList", todayList);
        result.put("todayReminders", todayList.size());
        result.put("totalMedicines", activeMedicines.size());
        result.put("completionRate", completionRate);
        
        return result;
    }

    /**
     * 添加用药提醒
     */
    public MedicineReminder addMedicine(MedicineReminder medicine) {
        medicine.setCreateTime(LocalDateTime.now());
        medicine.setUpdateTime(LocalDateTime.now());
        
        if (medicine.getIsActive() == null) {
            medicine.setIsActive(true);
        }
        
        medicineReminderMapper.insert(medicine);
        log.info("添加用药提醒成功: {}", medicine.getName());
        
        return medicine;
    }

    /**
     * 更新用药提醒
     */
    public MedicineReminder updateMedicine(MedicineReminder medicine) {
        MedicineReminder existing = medicineReminderMapper.selectById(medicine.getId());
        if (existing == null) {
            throw new RuntimeException("用药提醒不存在");
        }
        
        medicine.setCreateTime(existing.getCreateTime());
        medicine.setUpdateTime(LocalDateTime.now());
        
        medicineReminderMapper.updateById(medicine);
        log.info("更新用药提醒成功: {}", medicine.getName());
        
        return medicine;
    }

    /**
     * 删除用药提醒
     */
    public void deleteMedicine(Long id) {
        MedicineReminder removed = medicineReminderMapper.selectById(id);
        if (removed != null) {
            medicineReminderMapper.deleteById(id);
            medicineRecordMapper.delete(new LambdaQueryWrapper<MedicineRecord>()
                    .eq(MedicineRecord::getReminderId, id));
            log.info("删除用药提醒成功: {}", removed.getName());
        }
    }

    /**
     * 切换用药提醒状态
     */
    public void toggleMedicineStatus(Long id, Boolean isActive) {
        MedicineReminder medicine = medicineReminderMapper.selectById(id);
        if (medicine == null) {
            throw new RuntimeException("用药提醒不存在");
        }
        
        medicine.setIsActive(isActive);
        medicine.setUpdateTime(LocalDateTime.now());
        medicineReminderMapper.updateById(medicine);
        
        log.info("切换用药提醒状态: {} -> {}", medicine.getName(), isActive ? "启用" : "暂停");
    }

    /**
     * 记录服药状态
     */
    public void recordMedicineStatus(MedicineRecord record) {
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        
        MedicineRecord existing = medicineRecordMapper.selectOne(new LambdaQueryWrapper<MedicineRecord>()
                .eq(MedicineRecord::getUserId, record.getUserId())
                .eq(MedicineRecord::getReminderId, record.getReminderId())
                .eq(MedicineRecord::getRecordDate, record.getRecordDate())
                .eq(MedicineRecord::getReminderTime, record.getReminderTime()));
        if (existing == null) {
            medicineRecordMapper.insert(record);
        } else {
            record.setId(existing.getId());
            record.setCreateTime(existing.getCreateTime());
            medicineRecordMapper.updateById(record);
        }
        log.info("记录服药状态: reminderId={} - {}", record.getReminderId(), record.getStatus());
    }

    /**
     * 获取用药统计
     */
    public Map<String, Object> getStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().minusDays(30);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        final LocalDate finalStartDate = startDate;
        final LocalDate finalEndDate = endDate;
        
        // 统计该时间段内的服药记录
        List<MedicineRecord> records = medicineRecordMapper.selectList(new LambdaQueryWrapper<MedicineRecord>()
                .eq(MedicineRecord::getUserId, userId)
                .between(MedicineRecord::getRecordDate, finalStartDate, finalEndDate));
        
        long completedCount = records.stream().filter(r -> "completed".equals(r.getStatus())).count();
        long skippedCount = records.stream().filter(r -> "skipped".equals(r.getStatus())).count();
        long overdueCount = records.stream().filter(r -> "overdue".equals(r.getStatus())).count();
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalRecords", records.size());
        statistics.put("completedCount", completedCount);
        statistics.put("skippedCount", skippedCount);
        statistics.put("overdueCount", overdueCount);
        statistics.put("completionRate", records.isEmpty() ? 0 : (completedCount * 100) / records.size());
        
        return statistics;
    }

    /**
     * 生成记录键
     */
    private String generateRecordKey(Long userId, Long reminderId, LocalDate date, String time) {
        return String.format("%d_%d_%s_%s", userId, reminderId, date.toString(), time);
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(String status) {
        switch (status) {
            case "completed":
                return "已完成";
            case "skipped":
                return "已跳过";
            case "overdue":
                return "已过期";
            default:
                return "待服用";
        }
    }
}

