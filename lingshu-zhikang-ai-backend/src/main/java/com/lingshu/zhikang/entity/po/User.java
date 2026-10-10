package com.lingshu.zhikang.entity.po;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
public class User implements Serializable {
    private Long id;
    private String username;
    private String password;
    private String role; // USER-普通用户, ADMIN-管理员
    private String email;
    private String phone;
    private String avatar;
    private String memberLevel; // FREE-免费版, STANDARD-标准版, PROFESSIONAL-专业版, ENTERPRISE-企业版
    private Integer dailyQuota; // 每日使用配额
    private Integer dailyUsed; // 今日已使用次数
    private Integer totalUsed; // 累计使用次数
    private LocalDate lastUseDate; // 最后使用日期
    private LocalDateTime registerTime; // 注册时间
    private LocalDateTime lastLoginTime; // 最后登录时间
    private String status; // ACTIVE-正常, DISABLED-禁用, DELETED-已删除
    
    // 原有字段保留
    private String verificationCode;
    private LocalDateTime verificationCodeExpiry;
    private Boolean isActivated;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(String memberLevel) {
        this.memberLevel = memberLevel;
    }

    public Integer getDailyQuota() {
        return dailyQuota;
    }

    public void setDailyQuota(Integer dailyQuota) {
        this.dailyQuota = dailyQuota;
    }

    public Integer getDailyUsed() {
        return dailyUsed;
    }

    public void setDailyUsed(Integer dailyUsed) {
        this.dailyUsed = dailyUsed;
    }

    public Integer getTotalUsed() {
        return totalUsed;
    }

    public void setTotalUsed(Integer totalUsed) {
        this.totalUsed = totalUsed;
    }

    public LocalDate getLastUseDate() {
        return lastUseDate;
    }

    public void setLastUseDate(LocalDate lastUseDate) {
        this.lastUseDate = lastUseDate;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getVerificationCodeExpiry() {
        return verificationCodeExpiry;
    }

    public void setVerificationCodeExpiry(LocalDateTime verificationCodeExpiry) {
        this.verificationCodeExpiry = verificationCodeExpiry;
    }

    public Boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                ", memberLevel='" + memberLevel + '\'' +
                ", dailyQuota=" + dailyQuota +
                ", dailyUsed=" + dailyUsed +
                ", totalUsed=" + totalUsed +
                ", registerTime=" + registerTime +
                ", status='" + status + '\'' +
                '}';
    }
}
