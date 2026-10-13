/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80046 (8.0.46)
 Source Host           : localhost:3306
 Source Schema         : course

 Target Server Type    : MySQL
 Target Server Version : 80046 (8.0.46)
 File Encoding         : 65001

 Date: 13/10/2026 11:43:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for api_call_log
-- ----------------------------
DROP TABLE IF EXISTS `api_call_log`;
CREATE TABLE `api_call_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` bigint NOT NULL COMMENT '租户ID(企业客户ID)',
  `api_endpoint` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API端点',
  `request_params` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '请求参数(JSON格式)',
  `response_status` int NOT NULL COMMENT '响应状态码',
  `response_time_ms` int NOT NULL COMMENT '响应时间(毫秒)',
  `billing_amount` decimal(10, 4) NOT NULL DEFAULT 0.0000 COMMENT '计费金额',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_tenant_id`(`tenant_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_api_endpoint`(`api_endpoint` ASC) USING BTREE,
  INDEX `idx_tenant_date`(`tenant_id` ASC, `created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'API调用记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of api_call_log
-- ----------------------------
INSERT INTO `api_call_log` VALUES (1, 1, '/api/v1/medical-qa', '{\"question\": \"感冒了应该吃什么药？\"}', 200, 150, 0.1000, '2026-05-02 19:03:54');
INSERT INTO `api_call_log` VALUES (2, 1, '/api/v1/symptom-analysis', '{\"symptoms\": \"头痛发烧\"}', 200, 280, 0.1500, '2026-05-02 19:03:54');
INSERT INTO `api_call_log` VALUES (3, 2, '/api/v1/medical-qa', '{\"question\": \"高血压如何控制？\"}', 200, 120, 0.1000, '2026-05-02 19:03:54');
INSERT INTO `api_call_log` VALUES (4, 2, '/api/v1/health-advice', '{\"age\": 45, \"gender\": \"male\"}', 200, 200, 0.1000, '2026-05-02 19:03:54');

-- ----------------------------
-- Table structure for ar_medicine_guide
-- ----------------------------
DROP TABLE IF EXISTS `ar_medicine_guide`;
CREATE TABLE `ar_medicine_guide`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `medicine_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '药品名称',
  `medicine_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '药品编码（如国药准字）',
  `barcode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '条形码',
  `qr_code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '二维码内容',
  `ar_marker` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'AR识别标记（图像特征）',
  `usage_guide` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '使用指导',
  `dosage` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用法用量',
  `side_effects` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '副作用',
  `contraindications` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '禁忌症',
  `storage_method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '储存方法',
  `interaction_medicines` json NULL COMMENT '药物相互作用列表',
  `ar_animation_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'AR演示动画URL',
  `video_guide_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '视频指导URL',
  `is_prescription` tinyint(1) NULL DEFAULT 0 COMMENT '是否处方药',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `medicine_code`(`medicine_code` ASC) USING BTREE,
  INDEX `idx_medicine_name`(`medicine_name` ASC) USING BTREE,
  INDEX `idx_barcode`(`barcode` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AR药品识别表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ar_medicine_guide
-- ----------------------------

-- ----------------------------
-- Table structure for ar_vr_resource
-- ----------------------------
DROP TABLE IF EXISTS `ar_vr_resource`;
CREATE TABLE `ar_vr_resource`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源名称',
  `type` enum('3d_model','scene','texture','audio','video','animation') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源类型',
  `category` enum('organ','medicine','food','exercise','meditation','game') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '资源分类',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件URL',
  `thumbnail_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缩略图URL',
  `file_size` bigint NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '资源描述',
  `metadata` json NULL COMMENT '元数据（如模型参数、场景配置等）',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签（逗号分隔）',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `download_count` int NULL DEFAULT 0 COMMENT '下载次数',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_type_category`(`type` ASC, `category` ASC) USING BTREE,
  INDEX `idx_tags`(`tags` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AR/VR资源表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ar_vr_resource
-- ----------------------------

-- ----------------------------
-- Table structure for chat_counter
-- ----------------------------
DROP TABLE IF EXISTS `chat_counter`;
CREATE TABLE `chat_counter`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型',
  `counter` int NOT NULL DEFAULT 1 COMMENT '计数器值',
  `updated_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '对话ID计数器表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_counter
-- ----------------------------
INSERT INTO `chat_counter` VALUES (1, 'chat', 10, '2025-07-01 11:42:00');
INSERT INTO `chat_counter` VALUES (2, 'pdf', 1, '2025-06-30 20:24:47');
INSERT INTO `chat_counter` VALUES (3, 'service', 1, '2025-06-30 20:24:47');

-- ----------------------------
-- Table structure for chat_history
-- ----------------------------
DROP TABLE IF EXISTS `chat_history`;
CREATE TABLE `chat_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '业务类型，如：chat、service、pdf',
  `conversation_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对话ID',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_type_conversation`(`type` ASC, `conversation_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 197 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '对话历史记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_history
-- ----------------------------
INSERT INTO `chat_history` VALUES (157, 'service', '1', '2025-10-04 18:21:45');
INSERT INTO `chat_history` VALUES (178, 'chat', '1', '2025-11-21 12:34:47');
INSERT INTO `chat_history` VALUES (179, 'chat', '2', '2025-11-21 12:34:48');
INSERT INTO `chat_history` VALUES (180, 'chat', '3', '2025-11-21 12:34:50');
INSERT INTO `chat_history` VALUES (181, 'chat', '4', '2025-11-21 12:34:55');
INSERT INTO `chat_history` VALUES (182, 'chat', '5', '2025-11-21 12:38:11');
INSERT INTO `chat_history` VALUES (184, 'chat', '6', '2026-05-02 15:12:58');
INSERT INTO `chat_history` VALUES (193, 'chat', 'chat_1790505639384', '2026-09-27 18:40:40');
INSERT INTO `chat_history` VALUES (194, 'chat', 'chat_1790505648839', '2026-09-27 18:40:52');
INSERT INTO `chat_history` VALUES (195, 'chat', 'codex-check', '2026-09-28 11:17:24');
INSERT INTO `chat_history` VALUES (196, 'chat', 'codex-check-2', '2026-09-28 11:18:00');

-- ----------------------------
-- Table structure for chat_memory
-- ----------------------------
DROP TABLE IF EXISTS `chat_memory`;
CREATE TABLE `chat_memory`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `conversation_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对话ID',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息类型: SYSTEM, USER, ASSISTANT',
  `message_text` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `metadata` json NULL COMMENT '元数据',
  `tool_calls` json NULL COMMENT '工具调用信息',
  `created_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_conversation_id`(`conversation_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 465 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '聊天记录存储表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chat_memory
-- ----------------------------
INSERT INTO `chat_memory` VALUES (401, '5', 'USER', '你好', '{\"messageType\": \"USER\"}', NULL, '2025-12-10 20:53:14');
INSERT INTO `chat_memory` VALUES (402, '5', 'USER', '你好', '{\"messageType\": \"USER\"}', NULL, '2025-12-10 21:00:30');
INSERT INTO `chat_memory` VALUES (404, '6', 'USER', '最近头疼、乏力，可能是什么原因？', '{\"messageType\": \"USER\"}', NULL, '2026-05-02 15:13:00');
INSERT INTO `chat_memory` VALUES (405, '6', 'ASSISTANT', '哎呀，听起来你不太舒服呢！头疼和乏力可能是很多原因造成的哦。比如压力太大了，或者是睡眠不足，也有可能是身体缺水或者营养不良。当然啦，也有可能是因为感冒或者其他小病引起的。建议你可以先好好休息一下，多喝水，看看能不能缓解一些症状。如果情况没有好转，最好还是去医院检查一下，听听医生的意见吧。希望你能快点好起来！', '{\"id\": \"chatcmpl-ab5694ec-17c9-9cd7-a764-eb6d767e4381\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"STOP\"}', '[]', '2026-05-02 15:13:02');
INSERT INTO `chat_memory` VALUES (406, '1', 'USER', '我出现了以下症状：头痛、咽喉痛、腹泻、出汗、关节痛、红肿。症状持续了1-3天。我今年18岁。性别：男。症状严重程度为3级（1-5级）。请帮我分析可能的疾病，并给出就医建议。', '{\"messageType\": \"USER\"}', NULL, '2026-05-02 17:01:37');
INSERT INTO `chat_memory` VALUES (407, '1', 'ASSISTANT', '你好！根据你描述的症状，可能是感冒或流感引起的。这些症状包括头痛、咽喉痛、腹泻、出汗、关节痛和红肿等。\n\n建议：\n1. 多休息，保持充足的睡眠。\n2. 保持良好的饮食习惯，多喝水，避免油腻食物。\n3. 如果症状加重或者持续时间较长，建议去医院就诊，医生可能会开一些药物来缓解症状。\n4. 注意个人卫生，勤洗手，避免接触其他人以防传染。\n\n希望你能早日康复！如果还有其他问题，欢迎随时向我提问。', '{\"id\": \"chatcmpl-c72c727c-1c31-9caf-a0a8-065c49dddf8c\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"STOP\"}', '[]', '2026-05-02 17:01:39');
INSERT INTO `chat_memory` VALUES (408, '1', 'USER', '我出现了以下症状：偏头痛、呼吸困难、腹泻、乏力、出汗、关节痛、红肿。症状持续了1-3天。我今年19岁。性别：男。症状严重程度为3级（1-5级）。请帮我分析可能的疾病，并给出就医建议。', '{\"messageType\": \"USER\"}', NULL, '2026-05-02 17:03:47');
INSERT INTO `chat_memory` VALUES (409, '1', 'ASSISTANT', '嗨，我是小团团，很高兴能帮助到你！听起来你遇到了不少不适呢。根据你描述的症状——偏头痛、呼吸困难、腹泻、乏力、出汗、关节痛以及红肿，这确实需要引起重视。虽然我不能提供专业的医疗诊断，但我可以给你一些建议：\n\n首先，如果你的症状已经持续了一至三天，并且感觉比较严重，那么最好尽快去看医生。特别是呼吸困难这种症状，它可能提示有更严重的健康问题，比如哮喘发作或者其他呼吸道感染。\n\n其次，在看医生之前，你可以尝试做一些自我护理措施来缓解症状：\n- 确保充分休息，让身体有机会恢复。\n- 喝足够的水和其他液体，防止脱水。\n- 尽量吃容易消化的食物，避免刺激性食物。\n- 使用热敷或冷敷来缓解疼痛和炎症。\n\n最后，记得不要忽视任何不寻常的身体反应，尤其是当它们影响到日常生活时。及时寻求专业意见总是最安全的做法。\n\n希望你能很快好起来！如果有任何新的发展或者疑问，别犹豫，继续与我分享哦。祝你早日康复！', '{\"id\": \"chatcmpl-2d185903-19c7-92af-b01d-c4ddedefbb26\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"STOP\"}', '[]', '2026-05-02 17:03:51');
INSERT INTO `chat_memory` VALUES (416, '1', 'USER', '我出现了以下症状：头晕、呼吸困难、便秘、寒战、关节痛、瘙痒。症状持续了1天以内。我今年17岁。性别：男。症状严重程度为3级（1-5级）。请帮我分析可能的疾病，并给出就医建议。', '{\"messageType\": \"USER\"}', NULL, '2026-05-07 12:36:00');
INSERT INTO `chat_memory` VALUES (417, '1', 'USER', '我出现了以下症状：头晕、呼吸困难、便秘、寒战、关节痛、瘙痒。症状持续了1天以内。我今年17岁。性别：男。症状严重程度为3级（1-5级）。请帮我分析可能的疾病，并给出就医建议。', '{\"messageType\": \"USER\"}', NULL, '2026-05-07 12:38:21');
INSERT INTO `chat_memory` VALUES (418, '1', 'ASSISTANT', '嗨，我是小团团，很高兴能帮到你！听起来你遇到了不少不舒服的地方呢。根据你描述的症状——头晕、呼吸困难、便秘、寒战、关节痛和瘙痒，这确实需要引起注意。虽然我不能提供专业的医疗诊断，但我可以给你一些建议：\n\n首先，如果你的症状已经持续了一天以内，并且感觉比较严重，那么最好尽快去看医生。特别是呼吸困难这种症状，它可能提示有更严重的健康问题，比如哮喘发作或者其他呼吸道感染。\n\n其次，在看医生之前，你可以尝试做一些自我护理措施来缓解症状：\n- 确保充分休息，让身体有机会恢复。\n- 喝足够的水和其他液体，防止脱水。\n- 尽量吃容易消化的食物，避免刺激性食物。\n- 使用热敷或冷敷来缓解疼痛和炎症。\n\n最后，记得不要忽视任何不寻常的身体反应，尤其是当它们影响到日常生活时。及时寻求专业意见总是最安全的做法。\n\n希望你能很快好起来！如果有任何新的发展或者疑问，别犹豫，继续与我分享哦。祝你早日康复！', '{\"id\": \"chatcmpl-f653267a-d151-908e-9a99-2e6ebe8658bf\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"STOP\"}', '[]', '2026-05-07 12:38:25');
INSERT INTO `chat_memory` VALUES (419, '1', 'USER', '我出现了以下症状：偏头痛、呼吸困难、腹泻、寒战、关节痛、红肿。症状持续了1天以内。我今年16岁。症状严重程度为3级（1-5级）。请帮我分析可能的疾病，并给出就医建议。', '{\"messageType\": \"USER\"}', NULL, '2026-05-07 12:38:56');
INSERT INTO `chat_memory` VALUES (420, '1', 'ASSISTANT', '嗨，我是小团团，很高兴能帮你解答疑惑！听起来你遇到了不少不舒服的地方呢。根据你描述的症状——偏头痛、呼吸困难、腹泻、寒战、关节痛和红肿，这确实需要引起注意。虽然我不能提供专业的医疗诊断，但我可以给你一些建议：\n\n首先，如果你的症状已经持续了一天以内，并且感觉比较严重，那么最好尽快去看医生。特别是呼吸困难这种症状，它可能提示有更严重的健康问题，比如哮喘发作或者其他呼吸道感染。\n\n其次，在看医生之前，你可以尝试做一些自我护理措施来缓解症状：\n- 确保充分休息，让身体有机会恢复。\n- 喝足够的水和其他液体，防止脱水。\n- 尽量吃容易消化的食物，避免刺激性食物。\n- 使用热敷或冷敷来缓解疼痛和炎症。\n\n最后，记得不要忽视任何不寻常的身体反应，尤其是当它们影响到日常生活时。及时寻求专业意见总是最安全的做法。\n\n希望你能很快好起来！如果有任何新的发展或者疑问，别犹豫，继续与我分享哦。祝你早日康复！', '{\"id\": \"chatcmpl-1cf24ebd-1111-955c-b3da-d595a665623c\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"STOP\"}', '[]', '2026-05-07 12:38:59');
INSERT INTO `chat_memory` VALUES (430, '1', 'USER', '我出现了以下症状：头晕、咽喉痛、腹泻、寒战、关节痛、红肿。症状持续了1天以内。我今年18岁。性别：女。症状严重程度为3级（1-5级）。请帮我分析可能的疾病，并给出就医建议。', '{\"messageType\": \"USER\"}', NULL, '2026-07-29 21:56:49');
INSERT INTO `chat_memory` VALUES (431, '1', 'ASSISTANT', '嗨，我是小团团，很高兴能帮你解答疑惑！听起来你遇到了不少不舒服的地方呢。根据你描述的症状——头晕、咽喉痛、腹泻、寒战、关节痛和红肿，这确实需要引起注意。虽然我不能提供专业的医疗诊断，但我可以给你一些建议：\n\n首先，如果你的症状已经持续了一天以内，并且感觉比较严重，那么最好尽快去看医生。特别是寒战这种症状，它可能提示有发烧或其他感染的情况。\n\n其次，在看医生之前，你可以尝试做一些自我护理措施来缓解症状：\n- 确保充分休息，让身体有机会恢复。\n- 喝足够的水和其他液体，防止脱水。\n- 尽量吃容易消化的食物，避免刺激性食物。\n- 使用热敷或冷敷来缓解疼痛和炎症。\n\n最后，记得不要忽视任何不寻常的身体反应，尤其是当它们影响到日常生活时。及时寻求专业意见总是最安全的做法。\n\n希望你能很快好起来！如果有任何新的发展或者疑问，别犹豫，继续与我分享哦。祝你早日康复！', '{\"id\": \"chatcmpl-27212666-7248-9570-bdba-b1ef5735064c\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"STOP\"}', '[]', '2026-07-29 21:56:52');
INSERT INTO `chat_memory` VALUES (446, 'chat_1790505639384', 'USER', '最近头疼、乏力，可能是什么原因？', '{\"messageType\": \"USER\"}', NULL, '2026-09-27 18:40:40');
INSERT INTO `chat_memory` VALUES (447, 'chat_1790505639384', 'ASSISTANT', '\n\n小团团来帮你分析一下～头疼+乏力是比较常见的组合症状，可能的原因有这些哦：\n\n**常见原因：**\n- 🌡️ **感冒/病毒感染前期** — 可能会有发热感、浑身没劲\n- 💤 **睡眠不足或质量差** — 熬夜、失眠都会导致\n- 😰 **压力大、焦虑** — 紧张性头痛很常见\n- 💧 **脱水** — 喝水太少也会头疼乏力\n- 👓 **用眼过度** — 长时间看屏幕导致眼疲劳\n- 🩸 **贫血或低血糖** — 尤其是女性，可能伴随头晕\n- 📱 **颈椎问题** — 低头族容易中招\n\n**建议：**\n1. 先保证充足睡眠，多喝水\n2. 减少屏幕时间，适当活动颈部\n3. 观察是否有发热等其他症状\n\n⚠️ **如果头疼持续不缓解、加重，或伴随发热、呕吐、视力模糊等情况，一定要及时去看医生哦！** 小团团不能代替专业诊断～\n\n你最近是不是太累了？注意身体呀！(づ｡◕‿‿◕｡)づ', '{\"id\": \"c20bf888f5f8467b81162d72d5c71162\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"\"}', '[]', '2026-09-27 18:40:46');
INSERT INTO `chat_memory` VALUES (448, 'chat_1790505648839', 'USER', '你好', '{\"messageType\": \"USER\"}', NULL, '2026-09-27 18:40:52');
INSERT INTO `chat_memory` VALUES (449, 'chat_1790505648839', 'ASSISTANT', '\n\n你好呀～我是小团团！很高兴认识你！有什么我可以帮你的吗？✨', '{\"id\": \"7354bd5fea8c4cd09d2eda4ab0e862b9\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"\"}', '[]', '2026-09-27 18:40:53');
INSERT INTO `chat_memory` VALUES (450, 'chat_1790505648839', 'USER', '如何改善睡眠质量？', '{\"messageType\": \"USER\"}', NULL, '2026-09-27 18:46:01');
INSERT INTO `chat_memory` VALUES (451, 'chat_1790505648839', 'ASSISTANT', '\n\n嗨～想知道如何睡得好吗？小团团这就给你分享几个实用小贴士！✨\n\n**1. 保持规律作息**\n尽量每天在同一时间上床睡觉和起床，周末也不要睡懒觉太久哦～让身体形成生物钟！\n\n**2. 打造舒适睡眠环境**\n- 卧室保持安静、黑暗、温度适中\n- 选一张舒服的床垫和枕头\n- 遮光窗帘可以帮助阻挡光线\n\n**3. 睡前远离电子设备**\n手机、平板的蓝光会干扰褪黑素分泌，睡前1小时尽量不看屏幕啦～\n\n**4. 注意饮食**\n- 下午后少喝咖啡、浓茶\n- 睡前不要饮酒（虽然助眠，但会影响睡眠质量）\n- 晚餐不要吃太饱\n\n**5. 适当运动**\n白天适度运动有助于改善睡眠，但睡前2～3小时避免剧烈运动哦！\n\n**6. 放松身心**\n睡前可以试试泡个热水澡、听轻音乐、做深呼吸或冥想，帮助自己放松～\n\n**7. 建立睡前仪式感**\n比如泡脚、看一会儿纸质书，告诉身体\"该睡觉啦\"～\n\n希望这些建议对你有帮助！如果长期睡眠问题严重，记得咨询医生哦～晚安，好梦！🌙💤', '{\"id\": \"88298f2c505d4448b7dd10d255c52d4c\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"\"}', '[]', '2026-09-27 18:46:05');
INSERT INTO `chat_memory` VALUES (452, 'chat_1790505648839', 'USER', '分析图片', '{\"messageType\": \"USER\"}', NULL, '2026-09-28 11:16:12');
INSERT INTO `chat_memory` VALUES (453, 'chat_1790505648839', 'USER', '分析图片', '{\"messageType\": \"USER\"}', NULL, '2026-09-28 11:16:57');
INSERT INTO `chat_memory` VALUES (454, 'codex-check', 'USER', '请回复OK', '{\"messageType\": \"USER\"}', NULL, '2026-09-28 11:17:24');
INSERT INTO `chat_memory` VALUES (455, 'codex-check-2', 'USER', '请回复OK', '{\"messageType\": \"USER\"}', NULL, '2026-09-28 11:18:00');
INSERT INTO `chat_memory` VALUES (456, 'codex-check-2', 'USER', '最近头疼、乏力，可能是什么原因？', '{\"messageType\": \"USER\"}', NULL, '2026-09-28 11:18:58');
INSERT INTO `chat_memory` VALUES (457, 'codex-check-2', 'USER', '最近头疼、乏力，可能是什么原因？', '{\"messageType\": \"USER\"}', NULL, '2026-09-28 11:20:06');
INSERT INTO `chat_memory` VALUES (458, 'codex-check-2', 'ASSISTANT', '\n\n🐰 哎呀，头疼又乏力，小团团好担心你！这种情况可能是这些原因哦：\n\n**常见可能性：**\n- 💤 **睡眠问题** — 熬夜、睡不够或睡眠质量差\n- 😰 **压力过大** — 精神紧张、焦虑会让身体发出\"求救信号\"\n- 🤒 **感冒/病毒感染前期** — 身体在对抗病菌\n- 💧 **脱水** — 喝水太少也会头疼乏力\n- 🍬 **低血糖** — 没好好吃饭，能量跟不上\n- 👀 **用眼过度** — 长时间看屏幕导致疲劳\n- 🦴 **颈椎问题** — 姿势不良引起的牵连性头疼\n\n**小团团的建议：**\n✅ 多喝水、好好休息\n✅ 规律作息，别熬夜\n✅ 适当活动，放松身心\n✅ 如果症状持续超过3天或加重，一定要去看医生哦！\n\n🐰 身体最重要，照顾好自己！有什么需要随时找小团团~ 💕', '{\"id\": \"344710a7a3ac468d824086aceaf38bee\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"\"}', '[]', '2026-09-28 11:21:02');
INSERT INTO `chat_memory` VALUES (459, 'codex-check-2', 'USER', '最近头疼、乏力，可能是什么原因？', '{\"messageType\": \"USER\"}', NULL, '2026-09-28 11:21:58');
INSERT INTO `chat_memory` VALUES (460, 'codex-check-2', 'ASSISTANT', '\n\n🐰 头疼+乏力确实让人不舒服呢！常见原因有：\n\n1️⃣ **没睡好** — 熬夜或睡眠不足最常见\n2️⃣ **压力太大** — 精神紧张会消耗大量能量\n3️⃣ **感冒前兆** — 身体在对抗病毒\n4️⃣ **喝水太少** — 轻度脱水就会乏力头疼\n5️⃣ **没好好吃饭** — 血糖偏低\n6️⃣ **看屏幕太久** — 眼睛疲劳引发头疼\n\n💡 **小建议：**\n- 多喝温水，早点休息\n- 吃点东西补充能量\n- 远离手机电脑，闭眼放松\n- 适度散步透气\n\n🐰 如果两三天还没好转，一定要去看医生哦！照顾好自己~ 💕', '{\"id\": \"df78f199478647a298c6e5099ef36801\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"\"}', '[]', '2026-09-28 11:22:11');
INSERT INTO `chat_memory` VALUES (461, 'codex-check-2', 'USER', '你好', '{\"messageType\": \"USER\"}', NULL, '2026-09-28 11:22:45');
INSERT INTO `chat_memory` VALUES (462, 'codex-check-2', 'ASSISTANT', '\n\n🐰 你好呀！我是小团团，很高兴认识你~ ✨\n\n有什么我可以帮你的吗？无论是聊天、解答问题，还是只是想找个人说说话，小团团都在这儿陪着你哦！💕', '{\"id\": \"f4fc5d0c326e4f0f920cdb24ad6637b3\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"\"}', '[]', '2026-09-28 11:23:00');
INSERT INTO `chat_memory` VALUES (463, 'codex-check-2', 'USER', '最近头疼、乏力，可能是什么原因？', '{\"messageType\": \"USER\"}', NULL, '2026-10-13 09:40:37');
INSERT INTO `chat_memory` VALUES (464, 'codex-check-2', 'ASSISTANT', '\n\n🐰 哎呀，头疼又乏力，小团团好担心你！这种情况可能是这些原因哦：\n\n**常见可能性：**\n- 💤 **睡眠问题** — 熬夜、睡不够或睡眠质量差\n- 😰 **压力过大** — 精神紧张、焦虑会让身体发出\"求救信号\"\n- 🤒 **感冒/病毒感染前期** — 身体在对抗病菌\n- 💧 **脱水** — 喝水太少也会头疼乏力\n- 🍬 **低血糖** — 没好好吃饭，能量跟不上\n- 👀 **用眼过度** — 长时间看屏幕导致疲劳\n- 🦴 **颈椎问题** — 姿势不良引起的牵连性头疼\n\n**小团团的建议：**\n✅ 多喝水、好好休息\n✅ 规律作息，别熬夜\n✅ 适当活动，放松身心\n✅ 如果症状持续超过3天或加重，一定要去看医生哦！\n\n🐰 身体最重要，照顾好自己！有什么需要随时找小团团~ 💕', '{\"id\": \"8b418a43861a4659b5584256e59a391f\", \"role\": \"ASSISTANT\", \"index\": 0, \"refusal\": \"\", \"messageType\": \"ASSISTANT\", \"finishReason\": \"\"}', '[]', '2026-10-13 09:40:42');

-- ----------------------------
-- Table structure for chronic_disease_record
-- ----------------------------
DROP TABLE IF EXISTS `chronic_disease_record`;
CREATE TABLE `chronic_disease_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '指标类型: blood_pressure(血压), blood_sugar(血糖), weight(体重), heart_rate(心率)',
  `date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录日期',
  `time` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '记录时间',
  `systolic` int NULL DEFAULT NULL COMMENT '收缩压 (仅血压类型使用)',
  `diastolic` int NULL DEFAULT NULL COMMENT '舒张压 (仅血压类型使用)',
  `value` double NULL DEFAULT NULL COMMENT '通用数值 (血糖、体重、心率等使用)',
  `meal_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用餐类型 (仅血糖类型使用): 空腹、餐后1小时、餐后2小时、睡前',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注信息',
  `is_abnormal` tinyint(1) NULL DEFAULT 0 COMMENT '是否异常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_id` ASC, `type` ASC) USING BTREE,
  INDEX `idx_date`(`date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 77 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '慢性病记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of chronic_disease_record
-- ----------------------------
INSERT INTO `chronic_disease_record` VALUES (33, 1, 'blood_pressure', '2025-10-16', '08:00', 120, 80, NULL, NULL, '早晨测量', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (34, 1, 'blood_pressure', '2025-10-16', '20:00', 125, 82, NULL, NULL, '晚上测量', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (35, 1, 'blood_pressure', '2025-10-15', '08:00', 145, 92, NULL, NULL, '有点偏高', 1, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (36, 1, 'blood_pressure', '2025-10-14', '08:00', 118, 78, NULL, NULL, '正常', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (37, 1, 'blood_pressure', '2025-10-13', '08:00', 135, 88, NULL, NULL, '稍高', 1, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (38, 1, 'blood_sugar', '2025-10-16', '08:00', NULL, NULL, 5.6, '空腹', '正常范围', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (39, 1, 'blood_sugar', '2025-10-16', '14:00', NULL, NULL, 7.8, '餐后2小时', '正常', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (40, 1, 'blood_sugar', '2025-10-15', '08:00', NULL, NULL, 6.8, '空腹', '稍高', 1, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (41, 1, 'blood_sugar', '2025-10-14', '08:00', NULL, NULL, 5.2, '空腹', '正常', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (42, 1, 'blood_sugar', '2025-10-13', '08:00', NULL, NULL, 5.8, '空腹', '正常', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (43, 1, 'weight', '2025-10-16', '08:00', NULL, NULL, 68.5, NULL, '稳定', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (44, 1, 'weight', '2025-10-13', '08:00', NULL, NULL, 69, NULL, '稍重', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (45, 1, 'weight', '2025-10-10', '08:00', NULL, NULL, 69.5, NULL, '需要控制', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (46, 1, 'heart_rate', '2025-10-16', '08:00', NULL, NULL, 72, NULL, '正常', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (47, 1, 'heart_rate', '2025-10-15', '08:00', NULL, NULL, 85, NULL, '稍快', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (48, 1, 'heart_rate', '2025-10-14', '08:00', NULL, NULL, 70, NULL, '正常', 0, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `chronic_disease_record` VALUES (49, 5, 'blood_pressure', '2026-09-08', '07:35', 126, 82, NULL, NULL, '[dashboard-demo] 晨间测量', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (50, 5, 'blood_pressure', '2026-09-09', '07:42', 124, 80, NULL, NULL, '[dashboard-demo] 晨间测量', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (51, 5, 'blood_pressure', '2026-09-10', '07:28', 132, 86, NULL, NULL, '[dashboard-demo] 睡眠不足', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (52, 5, 'blood_pressure', '2026-09-11', '07:40', 128, 83, NULL, NULL, '[dashboard-demo] 晨间测量', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (53, 5, 'blood_pressure', '2026-09-12', '08:02', 136, 89, NULL, NULL, '[dashboard-demo] 略有升高', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (54, 5, 'blood_pressure', '2026-09-13', '07:51', 130, 84, NULL, NULL, '[dashboard-demo] 晨间测量', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (55, 5, 'blood_pressure', '2026-09-14', '07:36', 122, 79, NULL, NULL, '[dashboard-demo] 状态良好', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (56, 5, 'blood_sugar', '2026-09-08', '07:45', NULL, NULL, 5.8, '空腹', '[dashboard-demo] 空腹血糖', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (57, 5, 'blood_sugar', '2026-09-09', '07:50', NULL, NULL, 5.6, '空腹', '[dashboard-demo] 空腹血糖', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (58, 5, 'blood_sugar', '2026-09-10', '07:40', NULL, NULL, 6.3, '空腹', '[dashboard-demo] 略高', 1, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (59, 5, 'blood_sugar', '2026-09-11', '07:48', NULL, NULL, 5.7, '空腹', '[dashboard-demo] 空腹血糖', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (60, 5, 'blood_sugar', '2026-09-12', '08:10', NULL, NULL, 5.9, '空腹', '[dashboard-demo] 空腹血糖', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (61, 5, 'blood_sugar', '2026-09-13', '08:00', NULL, NULL, 5.5, '空腹', '[dashboard-demo] 空腹血糖', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (62, 5, 'blood_sugar', '2026-09-14', '07:46', NULL, NULL, 5.4, '空腹', '[dashboard-demo] 状态良好', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (63, 5, 'weight', '2026-09-08', '07:20', NULL, NULL, 70.2, NULL, '[dashboard-demo] 晨起体重', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (64, 5, 'weight', '2026-09-09', '07:25', NULL, NULL, 70, NULL, '[dashboard-demo] 晨起体重', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (65, 5, 'weight', '2026-09-10', '07:18', NULL, NULL, 69.8, NULL, '[dashboard-demo] 晨起体重', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (66, 5, 'weight', '2026-09-11', '07:26', NULL, NULL, 69.9, NULL, '[dashboard-demo] 晨起体重', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (67, 5, 'weight', '2026-09-12', '07:52', NULL, NULL, 69.6, NULL, '[dashboard-demo] 晨起体重', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (68, 5, 'weight', '2026-09-13', '07:40', NULL, NULL, 69.5, NULL, '[dashboard-demo] 晨起体重', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (69, 5, 'weight', '2026-09-14', '07:22', NULL, NULL, 69.3, NULL, '[dashboard-demo] 稳步改善', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (70, 5, 'heart_rate', '2026-09-08', '07:38', NULL, NULL, 76, NULL, '[dashboard-demo] 静息心率', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (71, 5, 'heart_rate', '2026-09-09', '07:44', NULL, NULL, 74, NULL, '[dashboard-demo] 静息心率', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (72, 5, 'heart_rate', '2026-09-10', '07:31', NULL, NULL, 82, NULL, '[dashboard-demo] 睡眠不足', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (73, 5, 'heart_rate', '2026-09-11', '07:43', NULL, NULL, 77, NULL, '[dashboard-demo] 静息心率', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (74, 5, 'heart_rate', '2026-09-12', '08:05', NULL, NULL, 73, NULL, '[dashboard-demo] 静息心率', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (75, 5, 'heart_rate', '2026-09-13', '07:54', NULL, NULL, 72, NULL, '[dashboard-demo] 静息心率', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `chronic_disease_record` VALUES (76, 5, 'heart_rate', '2026-09-14', '07:39', NULL, NULL, 71, NULL, '[dashboard-demo] 状态良好', 0, '2026-09-14 15:36:19', '2026-09-14 15:36:19');

-- ----------------------------
-- Table structure for community_challenge
-- ----------------------------
DROP TABLE IF EXISTS `community_challenge`;
CREATE TABLE `community_challenge`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `challenge_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挑战名称',
  `challenge_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '挑战类型：运动/饮食/睡眠/用药',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '挑战描述',
  `target_goal` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标要求',
  `duration_days` int NOT NULL COMMENT '挑战天数',
  `points_reward` int NULL DEFAULT 100 COMMENT '积分奖励',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `participant_count` int NULL DEFAULT 0 COMMENT '参与人数',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ongoing' COMMENT '状态：upcoming/ongoing/finished',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_start_date`(`start_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '打卡挑战表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of community_challenge
-- ----------------------------
INSERT INTO `community_challenge` VALUES (1, '21天早睡挑战', '睡眠', '养成良好睡眠习惯，每天23:00前入睡', '连续21天在23:00前睡觉', 21, 500, '2025-10-01', '2025-10-21', 0, 'ongoing', '2025-10-17 16:57:10');
INSERT INTO `community_challenge` VALUES (2, '30天步行挑战', '运动', '每天步行至少8000步', '每天步行8000步以上', 30, 800, '2025-10-01', '2025-10-30', 0, 'ongoing', '2025-10-17 16:57:10');
INSERT INTO `community_challenge` VALUES (3, '14天低糖饮食', '饮食', '控制糖分摄入，健康饮食', '每日糖分摄入<50g', 14, 400, '2025-10-01', '2025-10-14', 0, 'ongoing', '2025-10-17 16:57:10');
INSERT INTO `community_challenge` VALUES (4, '7天按时服药', '用药', '准时服药，不遗漏', '连续7天按时服药', 7, 200, '2025-10-01', '2025-10-07', 0, 'ongoing', '2025-10-17 16:57:10');

-- ----------------------------
-- Table structure for community_checkin
-- ----------------------------
DROP TABLE IF EXISTS `community_checkin`;
CREATE TABLE `community_checkin`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `challenge_id` bigint NULL DEFAULT NULL COMMENT '挑战ID，可为空表示普通打卡',
  `checkin_date` date NOT NULL COMMENT '打卡日期',
  `checkin_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '打卡类型：运动/饮食/睡眠/用药',
  `checkin_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '打卡内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '打卡图片(JSON格式)',
  `points_earned` int NULL DEFAULT 10 COMMENT '获得积分',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_challenge_date`(`user_id` ASC, `challenge_id` ASC, `checkin_date` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_challenge_id`(`challenge_id` ASC) USING BTREE,
  INDEX `idx_checkin_date`(`checkin_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '打卡记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of community_checkin
-- ----------------------------
INSERT INTO `community_checkin` VALUES (6, 1, 1, '2025-10-17', '睡眠', '今天22:45就睡了，睡眠质量很好！', NULL, 10, '2025-10-24 14:16:50');
INSERT INTO `community_checkin` VALUES (7, 1, 2, '2025-10-17', '运动', '今天步行12000步，超额完成！', NULL, 15, '2025-10-24 14:16:50');
INSERT INTO `community_checkin` VALUES (8, 1, 1, '2025-10-16', '睡眠', '23:00准时睡觉', NULL, 10, '2025-10-24 14:16:50');
INSERT INTO `community_checkin` VALUES (9, 1, 2, '2025-10-16', '运动', '步行8500步', NULL, 10, '2025-10-24 14:16:50');
INSERT INTO `community_checkin` VALUES (10, 2, 3, '2025-10-17', '饮食', '今天没吃甜食，控制糖分摄入', NULL, 10, '2025-10-24 14:16:50');

-- ----------------------------
-- Table structure for community_collection
-- ----------------------------
DROP TABLE IF EXISTS `community_collection`;
CREATE TABLE `community_collection`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_post`(`user_id` ASC, `post_id` ASC) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收藏记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of community_collection
-- ----------------------------

-- ----------------------------
-- Table structure for community_comment
-- ----------------------------
DROP TABLE IF EXISTS `community_comment`;
CREATE TABLE `community_comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父评论ID，0表示一级评论',
  `reply_to_id` bigint NULL DEFAULT 0 COMMENT '回复给谁的ID，0表示回复帖子',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `is_author` tinyint(1) NULL DEFAULT 0 COMMENT '是否楼主',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'normal' COMMENT '状态：normal/deleted/blocked',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_id`(`post_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_parent_id`(`parent_id` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '帖子评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of community_comment
-- ----------------------------

-- ----------------------------
-- Table structure for community_follow
-- ----------------------------
DROP TABLE IF EXISTS `community_follow`;
CREATE TABLE `community_follow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `follower_id` bigint NOT NULL COMMENT '关注者ID',
  `followee_id` bigint NOT NULL COMMENT '被关注者ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_follower_followee`(`follower_id` ASC, `followee_id` ASC) USING BTREE,
  INDEX `idx_followee_id`(`followee_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '关注关系表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of community_follow
-- ----------------------------

-- ----------------------------
-- Table structure for community_like
-- ----------------------------
DROP TABLE IF EXISTS `community_like`;
CREATE TABLE `community_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标类型：post/comment',
  `target_id` bigint NOT NULL COMMENT '目标ID',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_target`(`user_id` ASC, `target_type` ASC, `target_id` ASC) USING BTREE,
  INDEX `idx_target`(`target_type` ASC, `target_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '点赞记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of community_like
-- ----------------------------

-- ----------------------------
-- Table structure for community_post
-- ----------------------------
DROP TABLE IF EXISTS `community_post`;
CREATE TABLE `community_post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '发帖用户ID',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类：经验分享/求助问答/病友日记/健康资讯',
  `disease_tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '疾病标签：高血压/糖尿病等',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '帖子内容',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '图片URL列表(JSON格式)',
  `is_anonymous` tinyint(1) NULL DEFAULT 0 COMMENT '是否匿名发帖',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览次数',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `collect_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `is_top` tinyint(1) NULL DEFAULT 0 COMMENT '是否置顶',
  `is_essence` tinyint(1) NULL DEFAULT 0 COMMENT '是否精华',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'published' COMMENT '状态：draft/published/deleted/blocked',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE,
  INDEX `idx_disease_tag`(`disease_tag` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '社区帖子表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of community_post
-- ----------------------------
INSERT INTO `community_post` VALUES (6, 1, '经验分享', '高血压', '我的降压成功经验分享', '大家好！我想分享一下我成功控制血压的经验。\n\n三个月前，我的血压经常在150/95左右，医生建议我通过生活方式调整来控制。我做了以下改变：\n\n1. 饮食调整\n- 每天盐摄入控制在5克以内\n- 多吃蔬菜水果，特别是芹菜、香蕉\n- 戒掉了油炸食品和甜食\n- 每天喝水2000ml以上\n\n2. 运动习惯\n- 每天早晚各散步30分钟\n- 周末去游泳池游泳1小时\n- 做了一些简单的拉伸运动\n\n3. 生活作息\n- 23点前睡觉，保证8小时睡眠\n- 戒烟戒酒\n- 学会了冥想放松\n\n现在我的血压稳定在125/80左右，感觉整个人的状态都好多了！希望我的经验对大家有帮助。\n\n记住：控制血压是一个长期的过程，贵在坚持！加油！💪', NULL, 0, 156, 23, 8, 12, 1, 1, 'published', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `community_post` VALUES (7, 2, '求助问答', '糖尿病', '新确诊糖尿病，请问需要注意什么？', '大家好，我刚刚被确诊为2型糖尿病，空腹血糖8.5，医生开了二甲双胍。\n\n我想请教一下有经验的朋友们：\n1. 日常饮食应该怎么安排？\n2. 运动方面有什么建议？\n3. 需要注意哪些并发症？\n4. 血糖多久测一次比较好？\n\n谢谢大家！', NULL, 0, 89, 15, 12, 5, 0, 0, 'published', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `community_post` VALUES (8, 1, '病友日记', '高血压', '控压第30天 - 坚持就是胜利', '今天是我开始认真控制血压的第30天！\n\n早上血压：122/78 ✅\n晚上血压：125/80 ✅\n\n今天的饮食：\n- 早餐：燕麦粥+水煮蛋\n- 午餐：鸡胸肉沙拉\n- 晚餐：清蒸鲈鱼+蔬菜\n\n运动：\n- 早晨散步40分钟\n- 晚上做了20分钟瑜伽\n\n感受：\n坚持一个月真的看到效果了！血压稳定了，体重也轻了2公斤，整个人精神多了。\n\n最大的收获是养成了健康的生活习惯，现在早睡早起已经成为自然。\n\n继续加油！目标是坚持三个月！💪', NULL, 0, 45, 18, 3, 8, 0, 0, 'published', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `community_post` VALUES (9, 2, '健康资讯', NULL, '秋季养生小贴士', '秋天到了，给大家分享一些秋季养生的小贴士：\n\n🍂 饮食养生：\n1. 多吃润燥食物：梨、银耳、百合、蜂蜜\n2. 多喝水，预防秋燥\n3. 适当进补，但不要过量\n4. 多吃应季蔬果\n\n🏃 运动建议：\n1. 适合登山、慢跑等有氧运动\n2. 避免清晨锻炼，防止受凉\n3. 运动后及时补水\n4. 运动量循序渐进\n\n😴 起居调养：\n1. 早睡早起，顺应天时\n2. 注意保暖，特别是脚部\n3. 保持室内湿度\n4. 避免熬夜\n\n💆 情志调理：\n1. 保持心情愉快\n2. 多参加户外活动\n3. 与朋友家人多交流\n4. 适当放松减压\n\n希望大家都能健康度过秋天！🍁', NULL, 0, 234, 45, 15, 32, 0, 1, 'published', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `community_post` VALUES (10, 1, '经验分享', '减肥', '3个月减重15斤的心路历程', '终于达到目标体重了！从70kg减到62.5kg，整整15斤！💪\n\n我的方法：\n\n📅 饮食篇：\n- 早餐：燕麦+牛奶+水果\n- 午餐：正常吃，但控制碳水\n- 晚餐：清淡，18点前吃完\n- 戒掉了零食和奶茶\n\n🏃 运动篇：\n- 每天步行10000步\n- 每周跑步3次，每次5公里\n- 周末游泳或打羽毛球\n- 每天做20分钟核心训练\n\n💪 心态篇：\n- 不要急于求成\n- 允许偶尔放纵\n- 找到适合自己的方式\n- 坚持记录，看到进步\n\n📊 数据变化：\n- 体重：70kg → 62.5kg\n- 体脂率：28% → 22%\n- 腰围：85cm → 75cm\n\n最大的收获不是体重下降，而是养成了健康的生活习惯。现在每天都充满活力！\n\n给正在减肥路上的朋友加油！你也可以的！🎉', NULL, 0, 312, 67, 24, 45, 0, 1, 'published', '2025-10-24 14:16:50', '2025-10-24 14:16:50');

-- ----------------------------
-- Table structure for daily_food
-- ----------------------------
DROP TABLE IF EXISTS `daily_food`;
CREATE TABLE `daily_food`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '美食名称',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '分类：早餐、午餐、晚餐、小吃、甜点',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '美食描述',
  `price` decimal(10, 2) NOT NULL COMMENT '价格',
  `calories` int NULL DEFAULT NULL COMMENT '卡路里',
  `recommend_date` date NOT NULL COMMENT '推荐日期',
  `image_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图片链接',
  `is_vegetarian` tinyint(1) NULL DEFAULT 0 COMMENT '是否素食：0-否，1-是',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_recommend_date`(`recommend_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '每日推荐美食表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of daily_food
-- ----------------------------
INSERT INTO `daily_food` VALUES (1, '番茄炒蛋盖饭', '午餐', '新鲜番茄搭配嫩滑鸡蛋，营养美味', 15.00, 450, '2025-07-03', 'food/tomato_egg_rice.jpg', 0);
INSERT INTO `daily_food` VALUES (2, '红豆薏米粥', '早餐', '健脾去湿，养颜美容', 8.50, 220, '2025-07-03', 'food/red_bean_porridge.jpg', 1);
INSERT INTO `daily_food` VALUES (3, '水煮鱼', '晚餐', '麻辣鲜香，川菜代表', 48.00, 580, '2025-07-03', 'food/boiled_fish.jpg', 0);
INSERT INTO `daily_food` VALUES (4, '抹茶冰淇淋', '甜点', '清新淡雅，夏日解暑', 12.00, 180, '2025-07-03', 'food/matcha_icecream.jpg', 1);
INSERT INTO `daily_food` VALUES (5, '鸡蛋灌饼', '早餐', '外酥里嫩，香气四溢', 6.50, 320, '2025-07-04', 'food/egg_pancake.jpg', 0);
INSERT INTO `daily_food` VALUES (6, '宫保鸡丁', '午餐', '甜辣适中，下饭佳品', 22.00, 480, '2025-07-04', 'food/kung_pao_chicken.jpg', 0);
INSERT INTO `daily_food` VALUES (7, '香菇青菜', '晚餐', '清爽健康，营养丰富', 18.00, 120, '2025-07-04', 'food/mushroom_greens.jpg', 1);

-- ----------------------------
-- Table structure for diagnosis_report
-- ----------------------------
DROP TABLE IF EXISTS `diagnosis_report`;
CREATE TABLE `diagnosis_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `symptoms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '症状描述',
  `disease_probabilities` json NULL COMMENT '疾病概率(JSON格式)',
  `recommended_tests` json NULL COMMENT '推荐检查项目(JSON格式)',
  `treatment_suggestions` json NULL COMMENT '治疗建议(JSON格式)',
  `urgency_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '紧急程度: LOW, MEDIUM, HIGH, URGENT',
  `ai_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI分析结果',
  `accuracy_score` double NULL DEFAULT NULL COMMENT '诊断准确率',
  `is_paid` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否付费: 0-免费, 1-付费',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_is_paid`(`is_paid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI智能诊断报告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of diagnosis_report
-- ----------------------------
INSERT INTO `diagnosis_report` VALUES (1, 1, '头痛、发烧、咳嗽', '{\"其他\": 0.05, \"感冒\": 0.75, \"流感\": 0.2}', '[\"血常规\", \"胸部X光\"]', '[\"多休息\", \"多喝水\", \"服用退烧药\"]', 'MEDIUM', 'AI分析：根据症状判断，最可能是普通感冒，建议多休息并观察症状变化。', 0.92, 1, '2026-05-02 19:03:54');
INSERT INTO `diagnosis_report` VALUES (2, 2, '胸闷、气短', '{\"心脏病\": 0.45, \"焦虑症\": 0.3, \"呼吸系统疾病\": 0.25}', '[\"心电图\", \"心脏彩超\", \"胸部CT\"]', '[\"立即就医\", \"避免剧烈运动\"]', 'HIGH', 'AI分析：症状较为严重，建议尽快到医院进行详细检查。', 0.88, 1, '2026-05-02 19:03:54');
INSERT INTO `diagnosis_report` VALUES (3, 5, '头痛、乏力', '{\"睡眠不足\": 0.25, \"紧张性头痛\": 0.68}', '[\"血压监测\", \"睡眠评估\"]', '[\"保证休息\", \"适量饮水\"]', 'LOW', '[dashboard-demo] 建议观察并改善睡眠。', 0.91, 0, '2026-09-10 09:12:00');
INSERT INTO `diagnosis_report` VALUES (4, 5, '咳嗽、咽喉痛', '{\"咽炎\": 0.21, \"上呼吸道感染\": 0.72}', '[\"血常规\"]', '[\"温水润喉\", \"避免刺激性食物\"]', 'LOW', '[dashboard-demo] 症状较轻，持续观察。', 0.89, 0, '2026-09-06 18:25:00');
INSERT INTO `diagnosis_report` VALUES (5, 5, '胸闷', '{\"疲劳\": 0.51, \"焦虑反应\": 0.32}', '[\"心电图\", \"血氧监测\"]', '[\"停止剧烈运动\", \"症状加重及时就医\"]', 'MEDIUM', '[dashboard-demo] 建议关注心率和持续时间。', 0.86, 1, '2026-08-31 15:40:00');
INSERT INTO `diagnosis_report` VALUES (6, 5, '腹痛、腹胀', '{\"消化不良\": 0.64, \"胃肠功能紊乱\": 0.24}', '[\"腹部检查\"]', '[\"清淡饮食\", \"规律进餐\"]', 'LOW', '[dashboard-demo] 建议调整饮食后观察。', 0.88, 0, '2026-08-25 20:10:00');
INSERT INTO `diagnosis_report` VALUES (7, 5, '发热、肌肉酸痛', '{\"普通感冒\": 0.29, \"病毒感染\": 0.62}', '[\"体温监测\", \"血常规\"]', '[\"充分休息\", \"持续高热及时就医\"]', 'MEDIUM', '[dashboard-demo] 注意体温变化。', 0.9, 1, '2026-08-18 10:05:00');
INSERT INTO `diagnosis_report` VALUES (8, 5, '头晕、乏力', '{\"低血糖\": 0.22, \"睡眠不足\": 0.58}', '[\"血糖监测\", \"血压监测\"]', '[\"规律饮食\", \"避免突然起身\"]', 'LOW', '[dashboard-demo] 当前风险较低。', 0.87, 0, '2026-08-12 08:50:00');

-- ----------------------------
-- Table structure for enterprise_customer
-- ----------------------------
DROP TABLE IF EXISTS `enterprise_customer`;
CREATE TABLE `enterprise_customer`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `company_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '企业名称',
  `contact_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系人姓名',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `employee_count` int NOT NULL DEFAULT 0 COMMENT '员工数量',
  `service_plan` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务套餐: BASIC, STANDARD, PREMIUM',
  `annual_fee` decimal(12, 2) NOT NULL COMMENT '年费金额',
  `contract_start_date` date NOT NULL COMMENT '合同开始日期',
  `contract_end_date` date NOT NULL COMMENT '合同结束日期',
  `api_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'API密钥',
  `api_quota` int NOT NULL DEFAULT 10000 COMMENT 'API调用配额(每月)',
  `api_used` int NOT NULL DEFAULT 0 COMMENT 'API已使用次数(当月)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE, EXPIRED, SUSPENDED',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_api_key`(`api_key` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_contract_end_date`(`contract_end_date` ASC) USING BTREE,
  INDEX `idx_company_name`(`company_name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '企业客户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of enterprise_customer
-- ----------------------------
INSERT INTO `enterprise_customer` VALUES (1, '示例科技有限公司', '张经理', '13800138000', 'zhang@example.com', 500, 'STANDARD', 200000.00, '2026-05-02', '2027-05-02', 'sk_test_1234567890abcdef', 50000, 1250, 'ACTIVE', '2026-05-02 19:03:54', '2026-05-02 19:03:54');
INSERT INTO `enterprise_customer` VALUES (2, '健康管理集团', '李总监', '13900139000', 'li@health.com', 1000, 'PREMIUM', 500000.00, '2026-05-02', '2027-05-02', 'sk_test_abcdef1234567890', 100000, 3500, 'ACTIVE', '2026-05-02 19:03:54', '2026-05-02 19:03:54');
INSERT INTO `enterprise_customer` VALUES (3, '创新医疗科技', '王总', '13700137000', 'wang@innovation.com', 200, 'BASIC', 100000.00, '2026-05-02', '2027-05-02', 'sk_test_innovation123456', 20000, 800, 'ACTIVE', '2026-05-02 19:03:54', '2026-05-02 19:03:54');

-- ----------------------------
-- Table structure for health_analysis_report
-- ----------------------------
DROP TABLE IF EXISTS `health_analysis_report`;
CREATE TABLE `health_analysis_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `report_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '报告类型：weekly/monthly/quarterly/yearly',
  `report_date` date NOT NULL COMMENT '报告日期',
  `health_score` int NULL DEFAULT 85 COMMENT '综合健康评分',
  `health_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '良好' COMMENT '健康等级',
  `key_findings` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '关键发现',
  `improvement_areas` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '需改善领域',
  `achievements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '进步成就',
  `detailed_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细分析(JSON格式)',
  `ai_recommendations` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI建议',
  `report_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '完整报告内容(HTML格式)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_report_date`(`report_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康分析报告表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_analysis_report
-- ----------------------------

-- ----------------------------
-- Table structure for health_butler_conversation
-- ----------------------------
DROP TABLE IF EXISTS `health_butler_conversation`;
CREATE TABLE `health_butler_conversation`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `conversation_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '对话会话ID',
  `message_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息类型：USER/ASSISTANT',
  `message_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `emotion` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '情感识别：开心/难过/焦虑/平静',
  `intent` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户意图',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_conversation_id`(`conversation_id` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康管家对话记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_butler_conversation
-- ----------------------------
INSERT INTO `health_butler_conversation` VALUES (7, 1, 'butler_20251017_001', 'USER', '我最近血压有点高，应该注意什么？', '焦虑', '健康咨询', '2025-10-24 14:16:50');
INSERT INTO `health_butler_conversation` VALUES (8, 1, 'butler_20251017_001', 'ASSISTANT', '您好！血压偏高需要注意以下几点：\n\n1. 饮食方面：\n   - 减少盐分摄入，每天不超过6克\n   - 多吃新鲜蔬菜水果\n   - 避免油腻、高脂肪食物\n\n2. 生活习惯：\n   - 保持规律作息\n   - 避免熬夜\n   - 适度运动，如散步、游泳\n\n3. 情绪管理：\n   - 保持心情愉悦\n   - 避免过度焦虑\n   - 学会放松减压\n\n4. 定期监测：\n   - 每天早晚测量血压\n   - 记录血压变化\n   - 如持续偏高，及时就医\n\n您做得很好，已经开始关注自己的健康了！继续加油！', NULL, NULL, '2025-10-24 14:16:50');
INSERT INTO `health_butler_conversation` VALUES (9, 1, 'butler_20251017_001', 'USER', '那我应该吃什么降压药吗？', '关切', '用药咨询', '2025-10-24 14:16:50');
INSERT INTO `health_butler_conversation` VALUES (10, 1, 'butler_20251017_001', 'ASSISTANT', '关于用药，我有以下建议：\n\n⚠️ 重要提醒：降压药必须在医生指导下使用，不要自行购买服用。\n\n建议您：\n1. 先观察1-2周，记录每天的血压数据\n2. 如果血压持续在140/90以上，应该尽快就医\n3. 就医时携带血压记录，方便医生诊断\n4. 医生会根据您的具体情况开具合适的药物\n\n在就医前，可以通过饮食和运动来调节：\n- 减少钠盐摄入\n- 增加钾的摄入（香蕉、橙子等）\n- 每天适度运动\n- 保持良好心态\n\n需要我帮您预约挂号吗？', NULL, NULL, '2025-10-24 14:16:50');
INSERT INTO `health_butler_conversation` VALUES (11, 1, 'butler_20251017_002', 'USER', '帮我制定一个减重计划', '积极', '计划制定', '2025-10-24 14:16:50');
INSERT INTO `health_butler_conversation` VALUES (12, 1, 'butler_20251017_002', 'ASSISTANT', '好的！我来帮您制定一个科学的减重计划：\n\n🎯 目标：3个月减重5-8公斤\n\n📅 饮食计划：\n- 早餐：燕麦粥+水煮蛋+苹果\n- 午餐：鸡胸肉/鱼肉+蔬菜沙拉+糙米饭\n- 晚餐：清蒸鱼/豆腐+大量蔬菜\n- 加餐：坚果、水果（适量）\n\n🏃 运动计划：\n- 每天步行8000-10000步\n- 每周3次有氧运动（游泳、慢跑、骑行）\n- 每周2次力量训练\n\n📊 监测指标：\n- 每周测量体重2次\n- 记录每日饮食和运动\n- 拍照记录身体变化\n\n💪 注意事项：\n- 减重速度每周0.5-1公斤为宜\n- 不要节食，保证营养\n- 多喝水，每天2000ml以上\n- 早睡早起，保证睡眠\n\n我可以帮您创建这个计划并跟踪进度，要开始吗？', NULL, NULL, '2025-10-24 14:16:50');

-- ----------------------------
-- Table structure for health_butler_report
-- ----------------------------
DROP TABLE IF EXISTS `health_butler_report`;
CREATE TABLE `health_butler_report`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `report_date` date NOT NULL COMMENT '报告日期',
  `sleep_quality` double NULL DEFAULT NULL COMMENT '睡眠质量评分(0-10)',
  `sleep_duration` double NULL DEFAULT NULL COMMENT '睡眠时长(小时)',
  `medication_count` int NULL DEFAULT 0 COMMENT '今日用药数量',
  `health_tasks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '今日健康任务(JSON格式)',
  `diet_suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '饮食建议',
  `exercise_suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '运动建议',
  `health_score` int NULL DEFAULT 85 COMMENT '健康评分(0-100)',
  `health_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '良好' COMMENT '健康等级：优秀/良好/一般/较差',
  `ai_suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI个性化建议',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id` ASC, `report_date` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_report_date`(`report_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'AI健康管家晨报表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_butler_report
-- ----------------------------
INSERT INTO `health_butler_report` VALUES (9, 1, '2025-10-17', 7.5, 7.5, 2, '[\"测量血压\",\"记录饮食\",\"步行30分钟\",\"按时服药\"]', '建议低盐低脂饮食，多吃新鲜蔬菜水果，控制碳水化合物摄入。早餐可以选择燕麦粥配水果，午餐建议鸡胸肉沙拉，晚餐清蒸鱼配蔬菜。', '建议每天步行30-40分钟，可以分早晚两次进行。注意运动强度不要过大，保持舒适的节奏。可以尝试瑜伽或太极拳等温和运动。', 85, '良好', '您最近的健康状况整体良好！睡眠质量不错，继续保持规律作息。注意昨天血压略有升高，建议今天多次测量并记录。饮食方面做得很好，继续保持清淡饮食。加油！', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_butler_report` VALUES (10, 1, '2025-10-16', 8, 8, 2, '[\"测量血压\",\"记录血糖\",\"步行30分钟\"]', '建议多喝水，控制盐分摄入，增加蔬菜水果比例。', '今天可以尝试瑜伽或游泳等有氧运动。', 88, '良好', '昨天睡眠充足，状态很好！继续保持健康的生活方式。', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_butler_report` VALUES (11, 1, '2025-10-15', 6, 6, 2, '[\"测量血压\",\"充足睡眠\",\"规律饮食\"]', '昨天饮食不规律，今天要注意三餐定时。', '睡眠不足，今天早点休息，避免剧烈运动。', 75, '一般', '昨天睡眠不足导致状态欠佳，今晚记得早点休息哦！', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_butler_report` VALUES (12, 1, '2026-05-02', 7.5, 7, 0, '[\"测量血压\",\"记录饮食\",\"步行30分钟\"]', '建议低盐低脂饮食，多吃蔬菜水果', '建议每天步行30分钟，适度运动', 82, '良好', '1. 血糖值波动较大，建议控制饮食中高糖食物的摄入，选择低GI食物如全谷物和蔬菜。  \n2. 体重在健康范围内，但需保持均衡饮食与适量运动，建议每天进行30分钟有氧运动如快走或慢跑。  \n3. 心率正常，继续保持规律作息，避免过度劳累和压力过大影响心血管健康。  \n4. 血压数据缺失，建议每日定时测量血压并记录，确保血压维持在正常范围（低于120/80 mmHg）。  \n5. 定期监测血糖变化，若持续高于7.0，需及时咨询医生以评估糖尿病风险。', '2026-05-02 18:33:37', '2026-05-02 19:05:56');
INSERT INTO `health_butler_report` VALUES (30, 1, '2026-05-07', 7.5, 7, 0, '[\"测量血压\",\"记录饮食\",\"步行30分钟\"]', '建议低盐低脂饮食，多吃蔬菜水果', '建议每天步行30分钟，适度运动', 82, '良好', '1. 血糖值近期波动较大，建议控制饮食中糖分和精制碳水的摄入，避免高糖食物。  \n2. 体重在健康范围内，但需保持规律运动，例如每天快走30分钟，以维持健康代谢。  \n3. 心率正常，继续保持良好的作息习惯，避免熬夜和过度劳累。  \n4. 血压数据缺失，建议每日定时测量血压并记录，以便及时发现异常。  \n5. 多摄入富含膳食纤维的食物，如蔬菜和全谷物，有助于稳定血糖和改善心血管健康。  \n6. 定期复查血糖和血压，密切关注指标变化，必要时咨询医生调整治疗方案。', '2026-05-07 12:44:18', '2026-05-07 12:44:21');
INSERT INTO `health_butler_report` VALUES (32, 1, '2026-09-14', 7.5, 7, 0, '[\"测量血压\",\"记录饮食\",\"步行30分钟\"]', '建议低盐低脂饮食，多吃蔬菜水果', '建议每天步行30分钟，适度运动', 82, '良好', '健康管家暂时无法提供建议，请稍后再试。', '2026-09-14 16:15:52', '2026-09-14 16:15:52');

-- ----------------------------
-- Table structure for health_diary
-- ----------------------------
DROP TABLE IF EXISTS `health_diary`;
CREATE TABLE `health_diary`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '日记日期',
  `mood` int NULL DEFAULT NULL COMMENT '心情等级 (1-5)',
  `sleep` double NULL DEFAULT NULL COMMENT '睡眠时长 (小时)',
  `exercise` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '运动情况',
  `diet` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '饮食情况',
  `symptoms` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '症状描述',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '备注',
  `tags` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签 (逗号分隔)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date`(`user_id` ASC, `date` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_date`(`date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 28 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康日记表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_diary
-- ----------------------------
INSERT INTO `health_diary` VALUES (16, 1, '2025-10-16', 4, 7.5, '步行30分钟', '早餐：燕麦粥，午餐：鸡胸肉沙拉，晚餐：清蒸鱼', NULL, '今天感觉不错，精神状态良好', '运动,健康饮食', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_diary` VALUES (17, 1, '2025-10-15', 3, 6, '无', '饮食不规律', '轻微头痛', '睡眠不足，需要改善', '睡眠不足', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_diary` VALUES (18, 1, '2025-10-14', 5, 8, '瑜伽40分钟', '三餐正常，多喝水', NULL, '状态很好', '运动,充足睡眠', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_diary` VALUES (19, 1, '2025-10-13', 4, 7, '慢跑20分钟', '均衡饮食', NULL, '坚持运动', '运动', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_diary` VALUES (20, 1, '2025-10-12', 3, 6.5, '散步15分钟', '晚餐吃得比较多', '有点疲劳', '需要控制饮食', '疲劳', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_diary` VALUES (21, 5, '2026-09-08', 4, 7.2, '快走35分钟', '饮食清淡', '轻微肩颈酸痛', '完成每日健康记录', '规律作息,运动', '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `health_diary` VALUES (22, 5, '2026-09-09', 5, 7.8, '慢跑30分钟', '蔬菜和优质蛋白', '无明显不适', '精神状态很好', '心情愉快,运动', '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `health_diary` VALUES (23, 5, '2026-09-10', 3, 6.1, '拉伸15分钟', '晚餐偏晚', '轻微头痛、乏力', '工作较忙，提前休息', '睡眠不足', '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `health_diary` VALUES (24, 5, '2026-09-11', 4, 7.5, '快走40分钟', '三餐规律', '无明显不适', '状态恢复', '规律作息', '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `health_diary` VALUES (25, 5, '2026-09-12', 5, 8, '骑行45分钟', '低盐低脂', '无明显不适', '周末户外活动', '户外运动,心情愉快', '2026-09-14 15:36:19', '2026-09-14 15:36:19');

-- ----------------------------
-- Table structure for health_plan
-- ----------------------------
DROP TABLE IF EXISTS `health_plan`;
CREATE TABLE `health_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '计划名称',
  `plan_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '计划类型：饮食/运动/睡眠/用药/综合',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `target_goal` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '目标描述',
  `daily_tasks` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '每日任务(JSON格式)',
  `progress` int NULL DEFAULT 0 COMMENT '完成进度(0-100)',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '进行中' COMMENT '状态：进行中/已完成/已暂停/已取消',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康计划表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_plan
-- ----------------------------
INSERT INTO `health_plan` VALUES (5, 1, '血压管理计划', '用药', '2025-10-01', '2025-11-30', '通过规律监测、合理饮食和适度运动，将血压控制在130/85以下。', '[\"每天早晚测血压\",\"低盐饮食\",\"避免熬夜\",\"适度运动\",\"按时服药\"]', 60, '进行中', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_plan` VALUES (6, 1, '30天早睡挑战', '睡眠', '2025-10-01', '2025-10-30', '养成良好睡眠习惯，每天23:00前入睡，保证7-8小时睡眠。', '[\"22:30准备睡觉\",\"23:00前入睡\",\"睡前不玩手机\",\"记录睡眠时长\"]', 85, '进行中', '2025-10-24 14:16:50', '2025-10-24 14:16:50');

-- ----------------------------
-- Table structure for health_report_metadata
-- ----------------------------
DROP TABLE IF EXISTS `health_report_metadata`;
CREATE TABLE `health_report_metadata`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `file_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `content_type` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `file_size` bigint NULL DEFAULT 0,
  `created_time` datetime NULL DEFAULT NULL,
  `updated_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_health_report_file`(`file_id` ASC) USING BTREE,
  INDEX `idx_health_report_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of health_report_metadata
-- ----------------------------

-- ----------------------------
-- Table structure for health_risk_assessment
-- ----------------------------
DROP TABLE IF EXISTS `health_risk_assessment`;
CREATE TABLE `health_risk_assessment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `enterprise_id` bigint NULL DEFAULT NULL COMMENT '企业客户ID(如果是企业员工)',
  `assessment_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评估类型: PERSONAL, ENTERPRISE',
  `health_score` int NOT NULL COMMENT '健康评分(0-100)',
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '风险等级: LOW, MEDIUM, HIGH, CRITICAL',
  `risk_factors` json NULL COMMENT '风险因素(JSON格式)',
  `disease_predictions` json NULL COMMENT '疾病预测(JSON格式)',
  `intervention_suggestions` json NULL COMMENT '干预建议(JSON格式)',
  `lifestyle_recommendations` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '生活方式建议',
  `ai_analysis_detail` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI分析详情',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_enterprise_id`(`enterprise_id` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE,
  INDEX `idx_user_type`(`user_id` ASC, `assessment_type` ASC) USING BTREE,
  INDEX `idx_risk_level`(`risk_level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康风险评估表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_risk_assessment
-- ----------------------------
INSERT INTO `health_risk_assessment` VALUES (1, 1, NULL, 'PERSONAL', 85, 'LOW', '{\"吸烟\": false, \"运动\": \"规律\", \"饮酒\": \"偶尔\"}', '{\"糖尿病\": 0.1, \"心血管疾病\": 0.15}', '[\"保持当前生活方式\", \"定期体检\"]', '建议继续保持良好的生活习惯，每年进行一次全面体检。', 'AI分析：整体健康状况良好，风险较低。', '2026-05-02 19:03:54', '2026-05-02 19:03:54');
INSERT INTO `health_risk_assessment` VALUES (2, 2, 1, 'ENTERPRISE', 72, 'MEDIUM', '{\"吸烟\": true, \"运动\": \"很少\", \"饮酒\": \"经常\"}', '{\"糖尿病\": 0.25, \"高血压\": 0.3, \"心血管疾病\": 0.35}', '[\"戒烟\", \"减少饮酒\", \"增加运动\", \"控制饮食\"]', '建议戒烟限酒，每周至少进行3次有氧运动，每次30分钟以上。', 'AI分析：存在多个健康风险因素，需要积极改善生活方式。', '2026-05-02 19:03:54', '2026-05-02 19:03:54');

-- ----------------------------
-- Table structure for health_statistics
-- ----------------------------
DROP TABLE IF EXISTS `health_statistics`;
CREATE TABLE `health_statistics`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '统计类型：daily/weekly/monthly/yearly',
  `health_score` int NULL DEFAULT 85 COMMENT '健康评分',
  `blood_pressure_avg` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '平均血压',
  `blood_sugar_avg` double NULL DEFAULT NULL COMMENT '平均血糖',
  `weight_avg` double NULL DEFAULT NULL COMMENT '平均体重',
  `heart_rate_avg` int NULL DEFAULT NULL COMMENT '平均心率',
  `sleep_hours_avg` double NULL DEFAULT NULL COMMENT '平均睡眠时长',
  `exercise_minutes_total` int NULL DEFAULT 0 COMMENT '总运动时长(分钟)',
  `medication_compliance` int NULL DEFAULT 100 COMMENT '用药依从性(%)',
  `abnormal_count` int NULL DEFAULT 0 COMMENT '异常数据次数',
  `data_summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '数据摘要(JSON格式)',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_date_type`(`user_id` ASC, `stat_date` ASC, `stat_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康数据统计表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_statistics
-- ----------------------------
INSERT INTO `health_statistics` VALUES (5, 1, '2025-10-16', 'daily', 85, '122/81', 6.7, 68.5, 72, 7.5, 30, 100, 0, '{\"morning_bp\":\"120/80\",\"evening_bp\":\"125/82\",\"fasting_glucose\":5.6,\"mood\":4,\"exercise\":\"步行30分钟\"}', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_statistics` VALUES (6, 1, '2025-10-15', 'daily', 75, '145/92', 6.8, 69, 85, 6, 0, 100, 2, '{\"morning_bp\":\"145/92\",\"fasting_glucose\":6.8,\"mood\":3,\"sleep_insufficient\":true}', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_statistics` VALUES (7, 1, '2025-10-14', 'daily', 88, '118/78', 5.2, 69, 70, 8, 40, 100, 0, '{\"morning_bp\":\"118/78\",\"fasting_glucose\":5.2,\"mood\":5,\"exercise\":\"瑜伽40分钟\"}', '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_statistics` VALUES (8, 1, '2025-10-14', 'weekly', 82, '128/83', 6.1, 68.8, 75, 7.2, 180, 100, 2, NULL, '2025-10-24 14:16:50', '2025-10-24 14:16:50');
INSERT INTO `health_statistics` VALUES (9, 5, '2026-09-08', 'daily', 88, '126/82', 5.8, 70.2, 76, 7.2, 35, 96, 0, '{\"status\":\"stable\"}', '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `health_statistics` VALUES (10, 5, '2026-09-09', 'daily', 91, '124/80', 5.6, 70, 74, 7.8, 30, 100, 0, '{\"status\":\"good\"}', '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `health_statistics` VALUES (11, 5, '2026-09-10', 'daily', 82, '132/86', 6.3, 69.8, 82, 6.1, 15, 92, 1, '{\"status\":\"attention\"}', '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `health_statistics` VALUES (12, 5, '2026-09-11', 'daily', 89, '128/83', 5.7, 69.9, 77, 7.5, 40, 100, 0, '{\"status\":\"stable\"}', '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `health_statistics` VALUES (13, 5, '2026-09-12', 'daily', 93, '136/89', 5.9, 69.6, 73, 8, 45, 100, 0, '{\"status\":\"good\"}', '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `health_statistics` VALUES (14, 5, '2026-09-13', 'daily', 94, '130/84', 5.5, 69.5, 72, 7.6, 25, 100, 0, '{\"status\":\"good\"}', '2026-09-14 15:36:19', '2026-09-14 15:36:19');
INSERT INTO `health_statistics` VALUES (15, 5, '2026-09-14', 'daily', 95, '122/79', 5.4, 69.3, 71, 7.9, 30, 100, 0, '{\"status\":\"excellent\"}', '2026-09-14 15:36:19', '2026-09-14 15:36:19');

-- ----------------------------
-- Table structure for health_trend
-- ----------------------------
DROP TABLE IF EXISTS `health_trend`;
CREATE TABLE `health_trend`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `indicator_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '指标类型：血压/血糖/体重/心率等',
  `trend_period` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '趋势周期：week/month/quarter/year',
  `trend_direction` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '趋势方向：上升/下降/平稳/波动',
  `trend_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '趋势数据(JSON格式)',
  `analysis_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI分析结果',
  `suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '改善建议',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_indicator_type`(`indicator_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康趋势分析表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_trend
-- ----------------------------

-- ----------------------------
-- Table structure for health_warning
-- ----------------------------
DROP TABLE IF EXISTS `health_warning`;
CREATE TABLE `health_warning`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `rule_id` bigint NULL DEFAULT NULL COMMENT '触发规则ID',
  `warning_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预警类型：急性/慢性/预测性',
  `warning_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预警级别：绿色/黄色/橙色/红色',
  `indicator_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '指标类型',
  `indicator_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '指标值',
  `warning_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预警标题',
  `warning_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预警内容',
  `suggestions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '建议措施',
  `risk_score` int NULL DEFAULT 50 COMMENT '风险评分(0-100)',
  `is_urgent` tinyint(1) NULL DEFAULT 0 COMMENT '是否紧急',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'pending' COMMENT '状态：pending/viewed/handled/dismissed',
  `handled_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `handled_note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '处理备注',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_warning_level`(`warning_level` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 13 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '健康预警记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of health_warning
-- ----------------------------
INSERT INTO `health_warning` VALUES (7, 1, 2, '急性', '橙色', '血压', '145/92', '血压偏高预警', '您的血压偏高（145/92 mmHg），需要注意！', '1. 注意休息，避免劳累\n2. 监测血压变化\n3. 如持续偏高，建议就医', 75, 0, 'pending', NULL, NULL, '2025-10-24 14:16:50');
INSERT INTO `health_warning` VALUES (8, 1, 4, '慢性', '黄色', '血糖', '6.8 mmol/L', '空腹血糖偏高提醒', '您的空腹血糖略高（6.8 mmol/L）。', '1. 控制碳水化合物摄入\n2. 增加运动\n3. 定期监测血糖', 60, 0, 'viewed', NULL, NULL, '2025-10-24 14:16:50');
INSERT INTO `health_warning` VALUES (9, 1, 6, '慢性', '黄色', '用药', '75%', '用药依从性预警', '您最近用药不够规律，依从性为75%，请按时服药。', '1. 设置用药提醒\n2. 规律服药很重要\n3. 如有疑问咨询医生', 50, 0, 'handled', NULL, NULL, '2025-10-24 14:16:50');
INSERT INTO `health_warning` VALUES (10, 1, NULL, '预测性', '绿色', '综合', '良好', '健康状态良好', '您的整体健康状况良好，请继续保持！', '1. 保持良好生活习惯\n2. 定期体检\n3. 适度运动', 30, 0, 'viewed', NULL, NULL, '2025-10-24 14:16:50');
INSERT INTO `health_warning` VALUES (11, 1, NULL, '急性', '黄色', 'blood_pressure', '145/92', '血压偏高提醒', '您的血压略高（145/92 mmHg）。', '1. 注意休息\n2. 减少盐分摄入\n3. 保持监测', 60, 0, 'pending', NULL, NULL, '2026-09-14 15:36:10');
INSERT INTO `health_warning` VALUES (12, 1, NULL, '急性', '黄色', 'blood_pressure', '145/92', '血压偏高提醒', '您的血压略高（145/92 mmHg）。', '1. 注意休息\n2. 减少盐分摄入\n3. 保持监测', 60, 0, 'pending', NULL, NULL, '2026-09-28 11:11:26');

-- ----------------------------
-- Table structure for human_organ_model
-- ----------------------------
DROP TABLE IF EXISTS `human_organ_model`;
CREATE TABLE `human_organ_model`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `organ_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '器官名称（如：心脏、肝脏、肺）',
  `organ_name_en` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '器官英文名',
  `body_system` enum('circulatory','respiratory','digestive','nervous','skeletal','muscular','endocrine','urinary','reproductive','immune') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属系统',
  `model_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '3D模型URL',
  `position` json NULL COMMENT '器官位置坐标（x,y,z）',
  `function_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '器官功能描述',
  `common_diseases` json NULL COMMENT '常见疾病列表',
  `health_tips` json NULL COMMENT '健康建议',
  `interactive_points` json NULL COMMENT '交互热点（可点击查看详情的位置）',
  `animation_urls` json NULL COMMENT '动画URL列表（如心跳动画）',
  `ar_marker` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'AR识别标记',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_organ_name`(`organ_name` ASC) USING BTREE,
  INDEX `idx_body_system`(`body_system` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '人体器官模型表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of human_organ_model
-- ----------------------------
INSERT INTO `human_organ_model` VALUES (1, '心脏', 'heart', 'circulatory', '/assets/models/organs/heart.glb', '{\"x\": 0, \"y\": 20, \"z\": -5}', '心脏是人体的动力泵，负责将血液输送到全身各处，为器官和组织提供氧气和营养。', '[\"冠心病\", \"心肌梗死\", \"心律失常\", \"心力衰竭\"]', '[\"保持适度运动\", \"控制血压和血脂\", \"戒烟限酒\", \"减少压力\"]', NULL, NULL, NULL, '2025-10-24 14:12:13');
INSERT INTO `human_organ_model` VALUES (2, '肺', 'lungs', 'respiratory', '/assets/models/organs/lungs.glb', '{\"x\": 0, \"y\": 25, \"z\": 0}', '肺是呼吸系统的主要器官，负责气体交换，将氧气输入血液，排出二氧化碳。', '[\"肺炎\", \"哮喘\", \"慢性阻塞性肺病\", \"肺癌\"]', '[\"戒烟\", \"避免空气污染\", \"适度运动\", \"预防呼吸道感染\"]', NULL, NULL, NULL, '2025-10-24 14:12:13');
INSERT INTO `human_organ_model` VALUES (3, '肝脏', 'liver', 'digestive', '/assets/models/organs/liver.glb', '{\"x\": 5, \"y\": 15, \"z\": -3}', '肝脏是人体最大的内脏器官，具有代谢、解毒、分泌胆汁等多种功能。', '[\"脂肪肝\", \"肝炎\", \"肝硬化\", \"肝癌\"]', '[\"少饮酒\", \"健康饮食\", \"控制体重\", \"定期体检\"]', NULL, NULL, NULL, '2025-10-24 14:12:13');
INSERT INTO `human_organ_model` VALUES (4, '胃', 'stomach', 'digestive', '/assets/models/organs/stomach.glb', '{\"x\": 0, \"y\": 10, \"z\": 0}', '胃是消化系统的重要器官，负责储存和消化食物。', '[\"胃炎\", \"胃溃疡\", \"胃食管反流\", \"胃癌\"]', '[\"规律饮食\", \"避免辛辣刺激\", \"减少熬夜\", \"保持好心情\"]', NULL, NULL, NULL, '2025-10-24 14:12:13');
INSERT INTO `human_organ_model` VALUES (5, '大脑', 'brain', 'nervous', '/assets/models/organs/brain.glb', '{\"x\": 0, \"y\": 50, \"z\": 0}', '大脑是神经系统的最高级部分，控制思维、记忆、情感和身体活动。', '[\"阿尔茨海默病\", \"中风\", \"脑肿瘤\", \"帕金森病\"]', '[\"保持学习\", \"充足睡眠\", \"健康饮食\", \"控制血压\"]', NULL, NULL, NULL, '2025-10-24 14:12:13');

-- ----------------------------
-- Table structure for medicine_record
-- ----------------------------
DROP TABLE IF EXISTS `medicine_record`;
CREATE TABLE `medicine_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `reminder_id` bigint NOT NULL,
  `record_date` date NOT NULL,
  `reminder_time` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_medicine_record_slot`(`user_id` ASC, `reminder_id` ASC, `record_date` ASC, `reminder_time` ASC) USING BTREE,
  INDEX `idx_medicine_record_user_date`(`user_id` ASC, `record_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medicine_record
-- ----------------------------

-- ----------------------------
-- Table structure for medicine_reminder
-- ----------------------------
DROP TABLE IF EXISTS `medicine_reminder`;
CREATE TABLE `medicine_reminder`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `dosage` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `frequency` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `reminder_times` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `start_date` date NULL DEFAULT NULL,
  `end_date` date NULL DEFAULT NULL,
  `notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `is_active` tinyint(1) NULL DEFAULT 1,
  `color` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_medicine_reminder_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medicine_reminder
-- ----------------------------

-- ----------------------------
-- Table structure for member_level_config
-- ----------------------------
DROP TABLE IF EXISTS `member_level_config`;
CREATE TABLE `member_level_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `level_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '等级代码: FREE, STANDARD, PROFESSIONAL, ENTERPRISE',
  `level_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '等级名称',
  `daily_quota` int NOT NULL COMMENT '每日配额',
  `monthly_price` decimal(10, 2) NOT NULL COMMENT '月费(元)',
  `yearly_price` decimal(10, 2) NOT NULL COMMENT '年费(元)',
  `features` json NULL COMMENT '功能权限(JSON格式)',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_level_code`(`level_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员等级配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_level_config
-- ----------------------------
INSERT INTO `member_level_config` VALUES (1, 'FREE', '免费版', 5, 0.00, 0.00, '{\"ai_chat\": true, \"daily_limit\": 5, \"basic_diagnosis\": true}', 1, 1, '2026-05-02 14:51:47', '2026-05-02 14:51:47');
INSERT INTO `member_level_config` VALUES (2, 'STANDARD', '标准版', 50, 99.00, 950.00, '{\"ai_chat\": true, \"diagnosis\": true, \"daily_limit\": 50, \"health_report\": true, \"medical_record\": true}', 2, 1, '2026-05-02 14:51:47', '2026-05-02 14:51:47');
INSERT INTO `member_level_config` VALUES (3, 'PROFESSIONAL', '专业版', 200, 299.00, 2870.00, '{\"ai_chat\": true, \"diagnosis\": true, \"daily_limit\": 200, \"health_report\": true, \"image_analysis\": true, \"medical_record\": true, \"priority_support\": true}', 3, 1, '2026-05-02 14:51:47', '2026-05-02 14:51:47');
INSERT INTO `member_level_config` VALUES (4, 'ENTERPRISE', '企业版', 999999, 999.00, 9590.00, '{\"ai_chat\": true, \"diagnosis\": true, \"api_access\": true, \"daily_limit\": 999999, \"health_report\": true, \"custom_service\": true, \"image_analysis\": true, \"medical_record\": true, \"priority_support\": true}', 4, 1, '2026-05-02 14:51:47', '2026-05-02 14:51:47');

-- ----------------------------
-- Table structure for member_subscription
-- ----------------------------
DROP TABLE IF EXISTS `member_subscription`;
CREATE TABLE `member_subscription`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `plan_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '会员套餐类型: FREE, STANDARD, PROFESSIONAL, ENTERPRISE',
  `price` decimal(10, 2) NOT NULL DEFAULT 0.00 COMMENT '订阅价格',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '订阅状态: ACTIVE, EXPIRED, CANCELLED',
  `payment_method` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式: ALIPAY, WECHAT, BANK_CARD',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_end_date`(`end_date` ASC) USING BTREE,
  INDEX `idx_user_status`(`user_id` ASC, `status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '会员订阅表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of member_subscription
-- ----------------------------
INSERT INTO `member_subscription` VALUES (1, 1, 'STANDARD', 99.00, '2026-05-02', '2026-06-02', 'ACTIVE', 'ALIPAY', '2026-05-02 19:03:54', '2026-05-02 19:03:54');
INSERT INTO `member_subscription` VALUES (2, 2, 'PROFESSIONAL', 299.00, '2026-05-02', '2026-06-02', 'ACTIVE', 'WECHAT', '2026-05-02 19:03:54', '2026-05-02 19:03:54');
INSERT INTO `member_subscription` VALUES (3, 3, 'STANDARD', 950.40, '2026-05-02', '2027-05-02', 'ACTIVE', 'ALIPAY', '2026-05-02 19:03:54', '2026-05-02 19:03:54');
INSERT INTO `member_subscription` VALUES (4, 4, 'FREE', 0.00, '2026-05-02', '2027-05-02', 'ACTIVE', NULL, '2026-05-02 19:03:54', '2026-05-02 19:03:54');
INSERT INTO `member_subscription` VALUES (5, 6, 'PROFESSIONAL', 299.00, '2026-05-02', '2026-06-02', 'ACTIVE', 'ALIPAY', '2026-05-02 19:05:07', '2026-05-02 19:05:07');
INSERT INTO `member_subscription` VALUES (6, 6, 'ENTERPRISE', 999.00, '2026-05-07', '2026-06-07', 'ACTIVE', 'ALIPAY', '2026-05-07 12:44:58', '2026-05-07 12:44:58');

-- ----------------------------
-- Table structure for points_history
-- ----------------------------
DROP TABLE IF EXISTS `points_history`;
CREATE TABLE `points_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `points_change` int NOT NULL COMMENT '积分变化',
  `points_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型：earn/consume',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '来源：checkin/challenge/post/comment等',
  `description` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_created_time`(`created_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 17 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '积分明细表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of points_history
-- ----------------------------
INSERT INTO `points_history` VALUES (9, 1, 100, 'earn', 'post', '发布帖子：我的降压成功经验分享', '2025-10-24 14:16:50');
INSERT INTO `points_history` VALUES (10, 1, 50, 'earn', 'essence', '帖子被设为精华', '2025-10-24 14:16:50');
INSERT INTO `points_history` VALUES (11, 1, 10, 'earn', 'checkin', '每日打卡', '2025-10-24 14:16:50');
INSERT INTO `points_history` VALUES (12, 1, 20, 'earn', 'like', '帖子获得点赞', '2025-10-24 14:16:50');
INSERT INTO `points_history` VALUES (13, 2, 50, 'earn', 'post', '发布帖子：新确诊糖尿病，请问需要注意什么？', '2025-10-24 14:16:50');
INSERT INTO `points_history` VALUES (14, 2, 10, 'earn', 'comment', '发表评论', '2025-10-24 14:16:50');
INSERT INTO `points_history` VALUES (15, 1, 10, 'earn', 'checkin', '每日打卡', '2025-10-24 14:16:50');
INSERT INTO `points_history` VALUES (16, 1, 15, 'earn', 'comment', '帮助其他用户解答问题', '2025-10-24 14:16:50');

-- ----------------------------
-- Table structure for recharge_order
-- ----------------------------
DROP TABLE IF EXISTS `recharge_order`;
CREATE TABLE `recharge_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '目标会员等级',
  `payment_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '支付类型: MONTHLY-月付, YEARLY-年付',
  `amount` decimal(10, 2) NOT NULL COMMENT '支付金额',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式: ALIPAY-支付宝, WECHAT-微信, BANK_CARD-银行卡',
  `payment_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PENDING' COMMENT '支付状态: PENDING-待支付, PAID-已支付, CANCELLED-已取消, REFUNDED-已退款',
  `paid_at` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `expire_at` datetime NULL DEFAULT NULL COMMENT '到期时间',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_payment_status`(`payment_status` ASC) USING BTREE,
  INDEX `idx_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '充值订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of recharge_order
-- ----------------------------

-- ----------------------------
-- Table structure for rehabilitation_game
-- ----------------------------
DROP TABLE IF EXISTS `rehabilitation_game`;
CREATE TABLE `rehabilitation_game`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `game_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '游戏名称',
  `game_type` enum('puzzle','action','rhythm','balance','coordination','memory') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '游戏类型',
  `target_body_part` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '目标部位（如：手臂、腿部、颈部）',
  `difficulty_level` enum('easy','medium','hard') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'easy' COMMENT '难度等级',
  `game_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '游戏URL',
  `thumbnail_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缩略图URL',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '游戏描述',
  `instructions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '游戏说明',
  `therapeutic_benefits` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '治疗益处',
  `estimated_duration` int NULL DEFAULT NULL COMMENT '预计时长（分钟）',
  `is_vr_supported` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持VR',
  `is_ar_supported` tinyint(1) NULL DEFAULT 0 COMMENT '是否支持AR',
  `min_age` int NULL DEFAULT NULL COMMENT '最小年龄',
  `max_age` int NULL DEFAULT NULL COMMENT '最大年龄',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `rating_avg` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '平均评分',
  `play_count` int NULL DEFAULT 0 COMMENT '游玩次数',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_game_type`(`game_type` ASC) USING BTREE,
  INDEX `idx_target_part`(`target_body_part` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '康复游戏表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of rehabilitation_game
-- ----------------------------
INSERT INTO `rehabilitation_game` VALUES (1, '水果忍者康复版', 'action', '上肢,肩膀,手臂', 'easy', '/games/fruit_ninja_rehab.html', '/games/thumbnails/fruit_ninja.jpg', '通过切水果的动作锻炼手臂力量和协调性，适合上肢康复训练。', NULL, '提高手眼协调能力，增强肩关节和手臂的活动范围，改善精细运动控制。', 10, 1, 0, NULL, NULL, '有趣,上肢训练,协调性', 0.00, 0, 1, '2025-10-24 14:12:13');
INSERT INTO `rehabilitation_game` VALUES (2, '平衡大师', 'balance', '全身,核心,下肢', 'medium', '/games/balance_master.html', '/games/thumbnails/balance.jpg', '通过各种平衡挑战训练，提高身体平衡能力和核心稳定性。', NULL, '改善平衡感，增强核心肌群，预防跌倒，适合中风康复。', 15, 1, 0, NULL, NULL, '平衡训练,防跌倒,核心', 0.00, 0, 1, '2025-10-24 14:12:13');
INSERT INTO `rehabilitation_game` VALUES (3, '音乐节奏手', 'rhythm', '手部,手指', 'easy', '/games/rhythm_hand.html', '/games/thumbnails/rhythm.jpg', '跟随音乐节奏做手部动作，锻炼手指灵活性和协调性。', NULL, '提高手指灵活性，改善精细动作，适合手部功能障碍康复。', 12, 0, 0, NULL, NULL, '手部训练,节奏感,精细动作', 0.00, 0, 1, '2025-10-24 14:12:13');
INSERT INTO `rehabilitation_game` VALUES (4, '记忆宫殿', 'memory', '认知,大脑', 'medium', '/games/memory_palace.html', '/games/thumbnails/memory.jpg', '通过记忆训练游戏提高认知能力，适合老年人预防痴呆。', NULL, '增强记忆力，提高专注力，延缓认知衰退。', 20, 0, 0, NULL, NULL, '认知训练,记忆力,老年友好', 0.00, 0, 1, '2025-10-24 14:12:13');

-- ----------------------------
-- Table structure for rehabilitation_game_record
-- ----------------------------
DROP TABLE IF EXISTS `rehabilitation_game_record`;
CREATE TABLE `rehabilitation_game_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `game_id` bigint NOT NULL COMMENT '游戏ID',
  `play_date` date NOT NULL COMMENT '游戏日期',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `duration_minutes` int NULL DEFAULT NULL COMMENT '游戏时长（分钟）',
  `score` int NULL DEFAULT NULL COMMENT '得分',
  `level_reached` int NULL DEFAULT NULL COMMENT '达到关卡',
  `accuracy_rate` decimal(5, 2) NULL DEFAULT NULL COMMENT '准确率（%）',
  `reaction_time_avg` int NULL DEFAULT NULL COMMENT '平均反应时间（毫秒）',
  `movement_quality` int NULL DEFAULT NULL COMMENT '动作质量评分（0-100）',
  `achievements_unlocked` json NULL COMMENT '解锁的成就',
  `game_data` json NULL COMMENT '游戏数据（详细统计）',
  `improvement_trend` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '改善趋势（improving/stable/declining）',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_game`(`user_id` ASC, `game_id` ASC) USING BTREE,
  INDEX `idx_play_date`(`play_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '康复游戏记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of rehabilitation_game_record
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '密码',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'USER' COMMENT '用户角色: USER-普通用户, ADMIN-管理员',
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `member_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'FREE' COMMENT '会员等级: FREE-免费版, STANDARD-标准版, PROFESSIONAL-专业版, ENTERPRISE-企业版',
  `daily_quota` int NOT NULL DEFAULT 5 COMMENT '每日使用配额(AI对话次数)',
  `daily_used` int NOT NULL DEFAULT 0 COMMENT '今日已使用次数',
  `total_used` int NOT NULL DEFAULT 0 COMMENT '累计使用次数',
  `last_use_date` date NULL DEFAULT NULL COMMENT '最后使用日期',
  `register_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'ACTIVE' COMMENT '账号状态: ACTIVE-正常, DISABLED-禁用, DELETED-已删除',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_member_level`(`member_level` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_register_time`(`register_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (5, 'admin', '123456', 'ADMIN', 'admin@zq-ai.com', NULL, NULL, 'ENTERPRISE', 999999, 31, 31, '2026-10-13', '2026-05-02 14:51:54', NULL, 'ACTIVE', '2026-05-02 14:51:54', '2026-10-13 09:40:42');
INSERT INTO `user` VALUES (6, 'user001', '123456', 'USER', 'user001@test.com', NULL, NULL, 'ENTERPRISE', 999999, 0, 0, NULL, '2026-04-02 14:56:27', NULL, 'ACTIVE', '2026-05-02 14:56:27', '2026-05-07 12:44:57');
INSERT INTO `user` VALUES (7, 'user002', '123456', 'USER', 'user002@test.com', NULL, NULL, 'STANDARD', 50, 0, 0, NULL, '2026-04-12 14:56:27', NULL, 'ACTIVE', '2026-05-02 14:56:27', '2026-05-02 14:56:27');
INSERT INTO `user` VALUES (8, 'user003', '123456', 'USER', 'user003@test.com', NULL, NULL, 'PROFESSIONAL', 200, 0, 0, NULL, '2026-04-22 14:56:27', NULL, 'ACTIVE', '2026-05-02 14:56:27', '2026-05-02 14:56:27');
INSERT INTO `user` VALUES (9, 'user004', '123456', 'USER', 'user004@test.com', NULL, NULL, 'FREE', 5, 0, 0, NULL, '2026-04-27 14:56:27', NULL, 'ACTIVE', '2026-05-02 14:56:27', '2026-05-02 14:56:27');
INSERT INTO `user` VALUES (10, 'user005', '123456', 'USER', 'user005@test.com', NULL, NULL, 'STANDARD', 50, 0, 0, NULL, '2026-04-30 14:56:27', NULL, 'ACTIVE', '2026-05-02 14:56:27', '2026-05-02 14:56:27');

-- ----------------------------
-- Table structure for user_arvr_device
-- ----------------------------
DROP TABLE IF EXISTS `user_arvr_device`;
CREATE TABLE `user_arvr_device`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `device_type` enum('vr_headset','ar_glasses','smartphone','tablet') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设备类型',
  `device_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备名称',
  `device_model` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备型号',
  `capabilities` json NULL COMMENT '设备能力（支持的功能）',
  `last_used_time` datetime NULL DEFAULT NULL COMMENT '最后使用时间',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否激活',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_device_type`(`device_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户AR/VR设备信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_arvr_device
-- ----------------------------

-- ----------------------------
-- Table structure for user_points
-- ----------------------------
DROP TABLE IF EXISTS `user_points`;
CREATE TABLE `user_points`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `total_points` int NULL DEFAULT 0 COMMENT '总积分',
  `current_points` int NULL DEFAULT 0 COMMENT '当前可用积分',
  `level` int NULL DEFAULT 1 COMMENT '等级：1-青铜 2-白银 3-黄金 4-铂金 5-钻石',
  `level_name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '青铜' COMMENT '等级名称',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_level`(`level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户积分表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_points
-- ----------------------------
INSERT INTO `user_points` VALUES (3, 1, 1250, 850, 3, '黄金', '2025-10-24 14:16:50');
INSERT INTO `user_points` VALUES (4, 2, 450, 450, 2, '白银', '2025-10-24 14:16:50');

-- ----------------------------
-- Table structure for user_usage_log
-- ----------------------------
DROP TABLE IF EXISTS `user_usage_log`;
CREATE TABLE `user_usage_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `feature_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '功能类型: AI_CHAT-AI对话, DIAGNOSIS-智能诊断, IMAGE_ANALYSIS-影像分析, SYMPTOM_CHECK-症状检查',
  `usage_date` date NOT NULL COMMENT '使用日期',
  `usage_count` int NOT NULL DEFAULT 1 COMMENT '使用次数',
  `duration_seconds` int NULL DEFAULT NULL COMMENT '使用时长(秒)',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_usage_date`(`usage_date` ASC) USING BTREE,
  INDEX `idx_feature_type`(`feature_type` ASC) USING BTREE,
  INDEX `idx_user_date`(`user_id` ASC, `usage_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户使用记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user_usage_log
-- ----------------------------
INSERT INTO `user_usage_log` VALUES (1, 6, 'AI_CHAT', '2026-04-06', 1, 260, '2026-05-02 14:51:47');
INSERT INTO `user_usage_log` VALUES (2, 7, 'AI_CHAT', '2026-04-28', 8, 90, '2026-05-02 14:51:47');
INSERT INTO `user_usage_log` VALUES (3, 8, 'AI_CHAT', '2026-04-21', 7, 83, '2026-05-02 14:51:47');
INSERT INTO `user_usage_log` VALUES (4, 9, 'AI_CHAT', '2026-04-19', 10, 186, '2026-05-02 14:51:47');
INSERT INTO `user_usage_log` VALUES (5, 10, 'AI_CHAT', '2026-04-24', 1, 231, '2026-05-02 14:51:47');

-- ----------------------------
-- Table structure for vr_meditation_record
-- ----------------------------
DROP TABLE IF EXISTS `vr_meditation_record`;
CREATE TABLE `vr_meditation_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `scene_id` bigint NOT NULL COMMENT '场景ID',
  `meditation_date` date NOT NULL COMMENT '冥想日期',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `duration_minutes` int NULL DEFAULT NULL COMMENT '冥想时长（分钟）',
  `mood_before` enum('very_bad','bad','neutral','good','very_good') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '冥想前情绪',
  `mood_after` enum('very_bad','bad','neutral','good','very_good') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '冥想后情绪',
  `stress_level_before` int NULL DEFAULT NULL COMMENT '冥想前压力等级（0-10）',
  `stress_level_after` int NULL DEFAULT NULL COMMENT '冥想后压力等级（0-10）',
  `heart_rate_avg` int NULL DEFAULT NULL COMMENT '平均心率',
  `breathing_rate_avg` int NULL DEFAULT NULL COMMENT '平均呼吸频率',
  `completion_rate` decimal(5, 2) NULL DEFAULT NULL COMMENT '完成率（%）',
  `user_rating` int NULL DEFAULT NULL COMMENT '用户评分（1-5）',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户笔记',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_scene`(`user_id` ASC, `scene_id` ASC) USING BTREE,
  INDEX `idx_meditation_date`(`meditation_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'VR冥想记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of vr_meditation_record
-- ----------------------------

-- ----------------------------
-- Table structure for vr_meditation_scene
-- ----------------------------
DROP TABLE IF EXISTS `vr_meditation_scene`;
CREATE TABLE `vr_meditation_scene`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `scene_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场景名称',
  `scene_type` enum('nature','beach','forest','mountain','space','temple','garden') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '场景类型',
  `vr_scene_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'VR场景URL',
  `thumbnail_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缩略图URL',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '场景描述',
  `background_music_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '背景音乐URL',
  `ambient_sounds` json NULL COMMENT '环境音效列表',
  `guided_audio_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '引导语音URL',
  `duration_minutes` int NULL DEFAULT NULL COMMENT '建议时长（分钟）',
  `suitable_for` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '适用人群（如：焦虑、失眠、压力）',
  `difficulty_level` enum('beginner','intermediate','advanced') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'beginner' COMMENT '难度等级',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签',
  `rating_avg` decimal(3, 2) NULL DEFAULT 0.00 COMMENT '平均评分',
  `usage_count` int NULL DEFAULT 0 COMMENT '使用次数',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_scene_type`(`scene_type` ASC) USING BTREE,
  INDEX `idx_suitable_for`(`suitable_for` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'VR冥想场景表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of vr_meditation_scene
-- ----------------------------
INSERT INTO `vr_meditation_scene` VALUES (1, '宁静森林', 'forest', '/assets/vr/scenes/forest.glb', '/assets/vr/thumbnails/forest.jpg', '漫步在郁郁葱葱的森林中，感受大自然的宁静与和谐。鸟鸣声、流水声让您完全放松。', '/assets/audio/meditation/forest_music.mp3', NULL, NULL, 15, '焦虑,压力,失眠', 'beginner', '自然,放松,初学者友好', 0.00, 0, 1, '2025-10-24 14:12:13');
INSERT INTO `vr_meditation_scene` VALUES (2, '海边日落', 'beach', '/assets/vr/scenes/beach_sunset.glb', '/assets/vr/thumbnails/beach.jpg', '站在沙滩上，观看美丽的日落。海浪拍打的声音帮助您进入深度放松状态。', '/assets/audio/meditation/ocean_waves.mp3', NULL, NULL, 20, '失眠,压力,抑郁', 'beginner', '海洋,平静,睡眠', 0.00, 0, 1, '2025-10-24 14:12:13');
INSERT INTO `vr_meditation_scene` VALUES (3, '禅意庭院', 'garden', '/assets/vr/scenes/zen_garden.glb', '/assets/vr/thumbnails/zen_garden.jpg', '日式禅意庭院，枯山水、竹林、石灯笼营造宁静氛围。', '/assets/audio/meditation/zen_music.mp3', NULL, NULL, 25, '焦虑,冥想进阶', 'intermediate', '禅修,专注,进阶', 0.00, 0, 1, '2025-10-24 14:12:13');
INSERT INTO `vr_meditation_scene` VALUES (4, '星空宇宙', 'space', '/assets/vr/scenes/space.glb', '/assets/vr/thumbnails/space.jpg', '漂浮在浩瀚的宇宙中，感受宇宙的广阔与自我的渺小，获得心灵的平静。', '/assets/audio/meditation/space_ambient.mp3', NULL, NULL, 30, '焦虑,存在主义思考', 'advanced', '宇宙,哲学,高级', 0.00, 0, 1, '2025-10-24 14:12:13');

-- ----------------------------
-- Table structure for vr_rehabilitation_plan
-- ----------------------------
DROP TABLE IF EXISTS `vr_rehabilitation_plan`;
CREATE TABLE `vr_rehabilitation_plan`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `plan_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '计划名称',
  `disease_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '疾病类型（如：中风、骨折、运动损伤）',
  `difficulty_level` enum('beginner','intermediate','advanced') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'beginner' COMMENT '难度等级',
  `duration_weeks` int NULL DEFAULT NULL COMMENT '计划周期（周）',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '计划描述',
  `target_muscle_groups` json NULL COMMENT '目标肌肉群',
  `exercises` json NULL COMMENT '训练项目列表',
  `vr_scene_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'VR场景URL',
  `precautions` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '注意事项',
  `expected_results` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '预期效果',
  `doctor_advice` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '医生建议',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `created_by` bigint NULL DEFAULT NULL COMMENT '创建人（医生ID）',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_disease_type`(`disease_type` ASC) USING BTREE,
  INDEX `idx_difficulty`(`difficulty_level` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'VR康复训练计划表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of vr_rehabilitation_plan
-- ----------------------------

-- ----------------------------
-- Table structure for vr_rehabilitation_record
-- ----------------------------
DROP TABLE IF EXISTS `vr_rehabilitation_record`;
CREATE TABLE `vr_rehabilitation_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `plan_id` bigint NOT NULL COMMENT '计划ID',
  `session_date` date NOT NULL COMMENT '训练日期',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `duration_minutes` int NULL DEFAULT NULL COMMENT '训练时长（分钟）',
  `exercises_completed` json NULL COMMENT '完成的训练项目',
  `performance_score` int NULL DEFAULT NULL COMMENT '表现评分（0-100）',
  `accuracy_rate` decimal(5, 2) NULL DEFAULT NULL COMMENT '动作准确率（%）',
  `calories_burned` decimal(10, 2) NULL DEFAULT NULL COMMENT '消耗热量（千卡）',
  `heart_rate_avg` int NULL DEFAULT NULL COMMENT '平均心率',
  `heart_rate_max` int NULL DEFAULT NULL COMMENT '最高心率',
  `pain_level_before` int NULL DEFAULT NULL COMMENT '训练前疼痛等级（0-10）',
  `pain_level_after` int NULL DEFAULT NULL COMMENT '训练后疼痛等级（0-10）',
  `motion_data` json NULL COMMENT '运动数据（关节角度、位移等）',
  `user_feedback` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '用户反馈',
  `ai_analysis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'AI分析建议',
  `created_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_plan`(`user_id` ASC, `plan_id` ASC) USING BTREE,
  INDEX `idx_session_date`(`session_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'VR康复训练记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of vr_rehabilitation_record
-- ----------------------------

-- ----------------------------
-- Table structure for warning_rule
-- ----------------------------
DROP TABLE IF EXISTS `warning_rule`;
CREATE TABLE `warning_rule`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `rule_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则名称',
  `rule_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '规则类型：急性/慢性/预测性',
  `indicator_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '指标类型：血压/血糖/心率等',
  `warning_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预警级别：绿色/黄色/橙色/红色',
  `condition_expression` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '条件表达式',
  `threshold_value` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '阈值',
  `action_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '预警动作：推送/电话/联系家人等',
  `message_template` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '消息模板',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用',
  `priority` int NULL DEFAULT 0 COMMENT '优先级',
  `created_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_rule_type`(`rule_type` ASC) USING BTREE,
  INDEX `idx_is_active`(`is_active` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '预警规则配置表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of warning_rule
-- ----------------------------
INSERT INTO `warning_rule` VALUES (1, '血压危险预警', '急性', '血压', '红色', 'systolic >= 180 OR diastolic >= 110', '180/110', '推送+电话', '您的血压异常升高，请立即就医！', 1, 100, '2025-10-17 16:57:10', '2025-10-17 16:57:10');
INSERT INTO `warning_rule` VALUES (2, '血压偏高预警', '急性', '血压', '橙色', 'systolic >= 140 OR diastolic >= 90', '140/90', '推送', '您的血压偏高，请注意休息并监测。', 1, 80, '2025-10-17 16:57:10', '2025-10-17 16:57:10');
INSERT INTO `warning_rule` VALUES (3, '血糖危险预警', '急性', '血糖', '红色', 'value >= 16.7 OR value <= 3.9', '3.9-16.7', '推送+电话', '您的血糖异常，请立即采取措施！', 1, 100, '2025-10-17 16:57:10', '2025-10-17 16:57:10');
INSERT INTO `warning_rule` VALUES (4, '血糖偏高预警', '慢性', '血糖', '黄色', 'value >= 7.0 AND meal_type = \"空腹\"', '7.0', '推送', '您的空腹血糖偏高，建议调整饮食。', 1, 60, '2025-10-17 16:57:10', '2025-10-17 16:57:10');
INSERT INTO `warning_rule` VALUES (5, '心率异常预警', '急性', '心率', '橙色', 'value >= 100 OR value <= 50', '50-100', '推送', '您的心率异常，请注意休息。', 1, 70, '2025-10-17 16:57:10', '2025-10-17 16:57:10');
INSERT INTO `warning_rule` VALUES (6, '用药依从性预警', '慢性', '用药', '黄色', 'compliance_rate < 80', '80%', '推送', '您最近用药不规律，请按时服药。', 1, 50, '2025-10-17 16:57:10', '2025-10-17 16:57:10');

-- ----------------------------
-- View structure for v_today_food
-- ----------------------------
DROP VIEW IF EXISTS `v_today_food`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_today_food` AS select `daily_food`.`id` AS `id`,`daily_food`.`name` AS `name`,`daily_food`.`category` AS `category`,`daily_food`.`description` AS `description`,`daily_food`.`price` AS `price`,`daily_food`.`calories` AS `calories`,`daily_food`.`recommend_date` AS `recommend_date`,`daily_food`.`image_url` AS `image_url`,`daily_food`.`is_vegetarian` AS `is_vegetarian` from `daily_food` where (`daily_food`.`recommend_date` = curdate());

-- ----------------------------
-- Procedure structure for add_tomorrow_food
-- ----------------------------
DROP PROCEDURE IF EXISTS `add_tomorrow_food`;
delimiter ;;
CREATE DEFINER=`root`@`localhost` PROCEDURE `add_tomorrow_food`(
  IN p_name VARCHAR(100),
  IN p_category VARCHAR(50),
  IN p_description TEXT,
  IN p_price DECIMAL(10,2),
  IN p_calories INT,
  IN p_image_url VARCHAR(255),
  IN p_is_vegetarian TINYINT(1)
)
BEGIN
  INSERT INTO `daily_food` (
    `name`, 
    `category`, 
    `description`, 
    `price`, 
    `calories`, 
    `recommend_date`, 
    `image_url`, 
    `is_vegetarian`
  ) VALUES (
    p_name, 
    p_category, 
    p_description, 
    p_price, 
    p_calories, 
    DATE_ADD(CURDATE(), INTERVAL 1 DAY), 
    p_image_url, 
    p_is_vegetarian
  );
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table daily_food
-- ----------------------------
DROP TRIGGER IF EXISTS `check_daily_food_limit`;
delimiter ;;
CREATE TRIGGER `check_daily_food_limit` BEFORE INSERT ON `daily_food` FOR EACH ROW BEGIN
  DECLARE food_count INT;
  
  SELECT COUNT(*) INTO food_count 
  FROM `daily_food` 
  WHERE `recommend_date` = NEW.recommend_date;
  
  IF food_count >= 10 THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = '每天最多只能有10个推荐美食';
  END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
