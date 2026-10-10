package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.MedicineRecord;
import com.lingshu.zhikang.entity.po.MedicineReminder;
import com.lingshu.zhikang.entity.vo.ResponseResult;
import com.lingshu.zhikang.service.MedicineReminderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 用药提醒控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/medicine")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class MedicineReminderController {

    private final MedicineReminderService medicineReminderService;

    /**
     * 检查药物相互作用
     */
    @GetMapping("/check-interaction")
    public ResponseResult<Map<String, Object>> checkInteraction(
            @RequestParam String medicineName, 
            @RequestParam Long userId) {
        try {
            Map<String, Object> result = medicineReminderService.checkDrugInteraction(medicineName, userId);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("检查药物相互作用失败", e);
            return ResponseResult.error("检查失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户所有用药提醒
     */
    @GetMapping("/list")
    public ResponseResult<List<MedicineReminder>> getMedicineList(@RequestParam Long userId) {
        try {
            List<MedicineReminder> list = medicineReminderService.getMedicineList(userId);
            return ResponseResult.success(list);
        } catch (Exception e) {
            log.error("获取用药提醒列表失败", e);
            return ResponseResult.error("获取用药提醒列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取今日用药提醒
     */
    @GetMapping("/today")
    public ResponseResult<Map<String, Object>> getTodayReminders(@RequestParam Long userId) {
        try {
            Map<String, Object> result = medicineReminderService.getTodayReminders(userId);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("获取今日用药提醒失败", e);
            return ResponseResult.error("获取今日用药提醒失败：" + e.getMessage());
        }
    }

    /**
     * 添加用药提醒
     */
    @PostMapping("/add")
    public ResponseResult<MedicineReminder> addMedicine(@RequestBody MedicineReminder medicine) {
        try {
            MedicineReminder result = medicineReminderService.addMedicine(medicine);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("添加用药提醒失败", e);
            return ResponseResult.error("添加用药提醒失败：" + e.getMessage());
        }
    }

    /**
     * 更新用药提醒
     */
    @PutMapping("/update")
    public ResponseResult<MedicineReminder> updateMedicine(@RequestBody MedicineReminder medicine) {
        try {
            MedicineReminder result = medicineReminderService.updateMedicine(medicine);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("更新用药提醒失败", e);
            return ResponseResult.error("更新用药提醒失败：" + e.getMessage());
        }
    }

    /**
     * 删除用药提醒
     */
    @DeleteMapping("/delete/{id}")
    public ResponseResult<Void> deleteMedicine(@PathVariable Long id) {
        try {
            medicineReminderService.deleteMedicine(id);
            return ResponseResult.success(null);
        } catch (Exception e) {
            log.error("删除用药提醒失败", e);
            return ResponseResult.error("删除用药提醒失败：" + e.getMessage());
        }
    }

    /**
     * 切换用药提醒状态
     */
    @PutMapping("/toggle/{id}")
    public ResponseResult<Void> toggleMedicineStatus(@PathVariable Long id, @RequestParam Boolean isActive) {
        try {
            medicineReminderService.toggleMedicineStatus(id, isActive);
            return ResponseResult.success(null);
        } catch (Exception e) {
            log.error("切换用药提醒状态失败", e);
            return ResponseResult.error("切换用药提醒状态失败：" + e.getMessage());
        }
    }

    /**
     * 记录服药状态
     */
    @PostMapping("/record")
    public ResponseResult<Void> recordMedicineStatus(@RequestBody MedicineRecord record) {
        try {
            medicineReminderService.recordMedicineStatus(record);
            return ResponseResult.success(null);
        } catch (Exception e) {
            log.error("记录服药状态失败", e);
            return ResponseResult.error("记录服药状态失败：" + e.getMessage());
        }
    }

    /**
     * 获取用药统计
     */
    @GetMapping("/statistics")
    public ResponseResult<Map<String, Object>> getStatistics(@RequestParam Long userId, 
                                                      @RequestParam(required = false) LocalDate startDate,
                                                      @RequestParam(required = false) LocalDate endDate) {
        try {
            Map<String, Object> statistics = medicineReminderService.getStatistics(userId, startDate, endDate);
            return ResponseResult.success(statistics);
        } catch (Exception e) {
            log.error("获取用药统计失败", e);
            return ResponseResult.error("获取用药统计失败：" + e.getMessage());
        }
    }
}

