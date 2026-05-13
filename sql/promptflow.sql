/*
 Navicat Premium Dump SQL

 Source Server         : MySQL_8.4.5
 Source Server Type    : MySQL
 Source Server Version : 80405 (8.4.5)
 Source Host           : localhost:3307
 Source Schema         : promptflow

 Target Server Type    : MySQL
 Target Server Version : 80405 (8.4.5)
 File Encoding         : 65001

 Date: 21/04/2026 23:49:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for template
-- ----------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE `template`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板编码',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板名称',
  `template_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板类型：CONTENT_GENERATION/PROMPT_GENERATION',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板简介',
  `scene_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '使用场景说明',
  `output_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '输出说明',
  `icon` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '模板图标',
  `form_schema` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '表单结构JSON',
  `prompt_config` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '模板配置JSON',
  `sort_num` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1启用 0禁用',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_template_code`(`template_code` ASC) USING BTREE,
  INDEX `idx_template_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of template
-- ----------------------------
INSERT INTO `template` VALUES (1, 'ai_cover_prompt', 'AI翻唱提示词生成', 'PROMPT_GENERATION', '根据歌曲、风格、歌手等生成AI翻唱提示词', '适用于AI翻唱、风格改编', '输出结构化Prompt，可直接用于Suno', 'fas fa-music', '[{\"fieldKey\":\"songName\",\"label\":\"原歌曲名称\",\"componentType\":\"input\",\"required\":true,\"placeholder\":\"例如：夜曲 - 周杰伦\"},{\"fieldKey\":\"style\",\"label\":\"改编风格\",\"componentType\":\"tag\",\"required\":true,\"placeholder\":\"请选择改编风格\",\"multiple\":false,\"options\":[{\"label\":\"流行\",\"value\":\"Pop\"},{\"label\":\"R&B\",\"value\":\"R&B\"},{\"label\":\"电子\",\"value\":\"EDM\"},{\"label\":\"摇滚\",\"value\":\"Rock\"},{\"label\":\"民谣\",\"value\":\"Folk\"},{\"label\":\"说唱\",\"value\":\"Rap\"},{\"label\":\"国风\",\"value\":\"Chinese Style\"},{\"label\":\"日系\",\"value\":\"J-Pop\"},{\"label\":\"韩系\",\"value\":\"K-Pop\"},{\"label\":\"原声\",\"value\":\"Acoustic\"},{\"label\":\"合成器流行\",\"value\":\"Synthpop\"},{\"label\":\"灵魂流行\",\"value\":\"Pop Soul\"}]},{\"fieldKey\":\"artist\",\"label\":\"参考歌手 / 声线\",\"componentType\":\"input\",\"required\":false,\"placeholder\":\"例如：温柔男声、清亮女声、低沉磁性嗓音\"},{\"fieldKey\":\"mood\",\"label\":\"情绪氛围\",\"componentType\":\"tag\",\"required\":false,\"multiple\":true,\"maxSelect\":2,\"options\":[{\"label\":\"慵懒\",\"value\":\"lazy\"},{\"label\":\"治愈\",\"value\":\"healing\"},{\"label\":\"伤感\",\"value\":\"sad\"},{\"label\":\"浪漫\",\"value\":\"romantic\"},{\"label\":\"都市感\",\"value\":\"urban\"},{\"label\":\"忧郁\",\"value\":\"melancholic\"},{\"label\":\"明亮\",\"value\":\"bright\"},{\"label\":\"活力\",\"value\":\"energetic\"},{\"label\":\"热血\",\"value\":\"passionate\"},{\"label\":\"梦幻\",\"value\":\"dreamy\"},{\"label\":\"暗黑\",\"value\":\"dark\"},{\"label\":\"复古\",\"value\":\"retro\"},{\"label\":\"氛围感\",\"value\":\"atmospheric\"},{\"label\":\"高级感\",\"value\":\"sophisticated\"}]},{\"fieldKey\":\"tempo\",\"label\":\"节奏感\",\"componentType\":\"slider\",\"required\":false,\"defaultValue\":110,\"min\":60,\"max\":160,\"step\":1,\"unit\":\"BPM\",\"marks\":[{\"label\":\"舒缓\",\"value\":70},{\"label\":\"适中\",\"value\":100},{\"label\":\"活力\",\"value\":120},{\"label\":\"热血\",\"value\":140}]},{\"fieldKey\":\"language\",\"label\":\"输出语言\",\"componentType\":\"radio\",\"required\":true,\"defaultValue\":\"English\",\"options\":[{\"label\":\"中文\",\"value\":\"Chinese\"},{\"label\":\"英文\",\"value\":\"English\"}]},{\"fieldKey\":\"extraRemark\",\"label\":\"附加说明\",\"componentType\":\"textarea\",\"required\":false,\"placeholder\":\"例如：前奏加入一段萨克斯，整体偏深夜都市氛围\"}]', '{\r\n    \"promptFile\": \"ai_cover_prompt.txt\",\r\n    \"maxLength\": 1000\r\n  }', 1, 1, '2026-04-20 21:55:07', '2026-04-21 13:28:43');
INSERT INTO `template` VALUES (2, 'ai_short_video_script', '短视频脚本生成', 'CONTENT_GENERATION', '根据主题、受众和风格生成短视频脚本内容', '适用于抖音、小红书、快手等平台内容创作', '输出分镜化或分段式短视频脚本文案', 'fas fa-film', '[\r\n    {\r\n      \"fieldKey\": \"topic\",\r\n      \"label\": \"视频主题\",\r\n      \"componentType\": \"input\",\r\n      \"required\": true\r\n    },\r\n    {\r\n      \"fieldKey\": \"targetAudience\",\r\n      \"label\": \"目标用户\",\r\n      \"componentType\": \"input\"\r\n    },\r\n    {\r\n      \"fieldKey\": \"style\",\r\n      \"label\": \"脚本风格\",\r\n      \"componentType\": \"input\"\r\n    },\r\n    {\r\n      \"fieldKey\": \"duration\",\r\n      \"label\": \"视频时长\",\r\n      \"componentType\": \"input\"\r\n    },\r\n    {\r\n      \"fieldKey\": \"extraRemark\",\r\n      \"label\": \"补充说明\",\r\n      \"componentType\": \"textarea\"\r\n    }\r\n  ]', '{\r\n    \"promptFile\": \"ai_short_video_script.txt\",\r\n    \"maxLength\": 1500\r\n  }', 4, 0, '2026-04-20 21:55:07', '2026-04-21 13:29:13');
INSERT INTO `template` VALUES (3, 'ai_social_post', '小红书爆款文案', 'CONTENT_GENERATION', '根据主题和语气生成适合社交平台发布的文案', '生成吸引眼球的小红书风格文案', '输出可直接发布或二次修改的社交媒体文案', 'fas fa-book-open', '[\r\n    {\r\n      \"fieldKey\": \"topic\",\r\n      \"label\": \"文案主题\",\r\n      \"componentType\": \"input\",\r\n      \"required\": true\r\n    },\r\n    {\r\n      \"fieldKey\": \"platform\",\r\n      \"label\": \"发布平台\",\r\n      \"componentType\": \"input\"\r\n    },\r\n    {\r\n      \"fieldKey\": \"tone\",\r\n      \"label\": \"语气风格\",\r\n      \"componentType\": \"input\"\r\n    },\r\n    {\r\n      \"fieldKey\": \"keywords\",\r\n      \"label\": \"关键词\",\r\n      \"componentType\": \"textarea\"\r\n    },\r\n    {\r\n      \"fieldKey\": \"extraRemark\",\r\n      \"label\": \"补充要求\",\r\n      \"componentType\": \"textarea\"\r\n    }\r\n  ]', '{\r\n    \"promptFile\": \"ai_social_post.txt\",\r\n    \"maxLength\": 1200\r\n  }', 5, 0, '2026-04-20 21:55:07', '2026-04-21 13:29:44');
INSERT INTO `template` VALUES (4, 'ai_title_optimizer', '社交媒体文案', 'CONTENT_GENERATION', '为各大社交平台定制文案', '适用于短视频、文章、笔记、海报标题等场景', '输出多个标题候选，便于筛选使用', 'fas fa-hashtag', '[\r\n    {\r\n      \"fieldKey\": \"topic\",\r\n      \"label\": \"内容主题\",\r\n      \"componentType\": \"input\",\r\n      \"required\": true\r\n    },\r\n   {\r\n  \"fieldKey\": \"platform\",\r\n  \"label\": \"平台\",\r\n  \"componentType\": \"input\"\r\n},\r\n    {\r\n      \"fieldKey\": \"style\",\r\n      \"label\": \"标题风格\",\r\n      \"componentType\": \"input\"\r\n    },\r\n    {\r\n      \"fieldKey\": \"keywords\",\r\n      \"label\": \"关键词\",\r\n      \"componentType\": \"textarea\"\r\n    }\r\n  ]', '{\r\n    \"promptFile\": \"ai_title_optimizer.txt\",\r\n    \"maxLength\": 800\r\n  }', 6, 0, '2026-04-20 21:55:07', '2026-04-21 13:34:47');

-- ----------------------------
-- Table structure for workflow_run
-- ----------------------------
DROP TABLE IF EXISTS `workflow_run`;
CREATE TABLE `workflow_run`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `template_id` bigint NOT NULL COMMENT '模板ID',
  `template_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板编码',
  `run_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '运行状态：PENDING/RUNNING/SUCCESS/FAILED',
  `input_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '输入数据JSON',
  `result_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '最终结果JSON',
  `extra_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '附加信息JSON',
  `error_message` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '错误信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_workflow_run_template_id`(`template_id` ASC) USING BTREE,
  INDEX `idx_workflow_run_template_code`(`template_code` ASC) USING BTREE,
  INDEX `idx_workflow_run_status`(`run_status` ASC) USING BTREE,
  INDEX `idx_workflow_run_created_at`(`created_at` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 61 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程运行记录表' ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for workflow_step_run
-- ----------------------------
DROP TABLE IF EXISTS `workflow_step_run`;
CREATE TABLE `workflow_step_run`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `run_id` bigint NOT NULL COMMENT '所属运行记录ID',
  `step_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '步骤编码',
  `step_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '步骤名称',
  `step_order` int NOT NULL COMMENT '步骤顺序',
  `step_status` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '步骤状态：PENDING/RUNNING/SUCCESS/FAILED',
  `input_snapshot` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '步骤输入快照JSON',
  `output_snapshot` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '步骤输出快照JSON',
  `error_message` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '步骤错误信息',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_step_run_run_id`(`run_id` ASC) USING BTREE,
  INDEX `idx_step_run_order`(`step_order` ASC) USING BTREE,
  INDEX `idx_step_run_status`(`step_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '流程步骤记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of workflow_step_run
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '密码哈希值',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `remaining_count` int NOT NULL DEFAULT 3 COMMENT '剩余生成次数',
  `total_used_count` int NOT NULL DEFAULT 0 COMMENT '累计使用次数',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_phone` (`phone`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sms_code
-- ----------------------------
DROP TABLE IF EXISTS `sms_code`;
CREATE TABLE `sms_code` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
  `phone` VARCHAR(20) NOT NULL COMMENT '手机号',
  `code` VARCHAR(10) NOT NULL COMMENT '验证码',
  `scene` VARCHAR(30) NOT NULL COMMENT '使用场景：REGISTER/LOGIN/RESET_PWD',
  `used` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已使用：1是 0否',
  `expire_time` DATETIME NOT NULL COMMENT '过期时间',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `ip` VARCHAR(64) DEFAULT NULL COMMENT '请求来源IP',
  INDEX `idx_phone_scene` (`phone`, `scene`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '短信验证码记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_history
-- ----------------------------
DROP TABLE IF EXISTS `user_history`;
CREATE TABLE `user_history`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `template_id` bigint NULL DEFAULT NULL COMMENT '模板ID',
  `template_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '模板编码',
  `tag` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标签',
  `input_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '输入数据JSON',
  `output_data` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '生成结果内容',
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_history_user_time`(`user_id` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_user_history_user_tag_time`(`user_id` ASC, `tag` ASC, `created_at` ASC) USING BTREE,
  INDEX `idx_user_history_template_code`(`template_code` ASC) USING BTREE,
  CONSTRAINT `fk_user_history_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_history_template_id` FOREIGN KEY (`template_id`) REFERENCES `template` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户生成历史记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for recharge_package
-- ----------------------------
DROP TABLE IF EXISTS `recharge_package`;
CREATE TABLE `recharge_package` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `package_name` VARCHAR(100) NOT NULL COMMENT '套餐名称',
    `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
    `generate_count` INT NOT NULL COMMENT '增加次数',
    `status` TINYINT DEFAULT 1 COMMENT '1启用，0禁用',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值套餐表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recharge_package
-- ----------------------------
INSERT INTO `recharge_package`(`package_name`, `price`, `generate_count`)
VALUES
('体验包', 9.90, 50),
('标准包', 19.90, 120),
('高级包', 39.90, 300);

-- ----------------------------
-- Table structure for recharge_request
-- ----------------------------
DROP TABLE IF EXISTS `recharge_request`;
CREATE TABLE `recharge_request` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(64) NOT NULL COMMENT '本系统订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `package_id` BIGINT NOT NULL COMMENT '套餐ID',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `generate_count` INT NOT NULL COMMENT '应增加次数',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING待支付，SUCCESS已发放次数，FAILED已失败，CLOSED订单关闭',
    `paid_at` DATETIME DEFAULT NULL COMMENT '支付完成时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '充值业务订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for payment_transaction
-- ----------------------------
DROP TABLE IF EXISTS `payment_transaction`;
CREATE TABLE `payment_transaction` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '业务订单号',
    `pay_channel` VARCHAR(30) NOT NULL COMMENT '支付渠道：ALIPAY/WECHAT/EPAY',
    `pay_scene` VARCHAR(30) DEFAULT NULL COMMENT '支付场景：PC_WEB/FACE_TO_FACE/H5/JSAPI',
    `out_trade_no` VARCHAR(64) NOT NULL COMMENT '传给第三方的商户订单号',
    `third_trade_no` VARCHAR(100) DEFAULT NULL COMMENT '第三方交易号',
    `trade_status` VARCHAR(50) DEFAULT NULL COMMENT '第三方交易状态',
    `pay_amount` DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    `notify_data` TEXT COMMENT '支付回调原始数据',
    `notify_count` INT NOT NULL DEFAULT 0 COMMENT '回调次数',
    `notify_time` DATETIME DEFAULT NULL COMMENT '最近回调时间',
    `paid_at` DATETIME DEFAULT NULL COMMENT '支付成功时间',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_out_trade_no` (`out_trade_no`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_pay_channel` (`pay_channel`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '第三方支付交易表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for usage_record
-- ----------------------------
DROP TABLE IF EXISTS `usage_record`;
CREATE TABLE `usage_record` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `order_no` VARCHAR(64) DEFAULT NULL COMMENT '关联订单号',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `run_id` BIGINT DEFAULT NULL COMMENT '生成任务ID',
    `change_count` INT NOT NULL COMMENT '变动次数，消费为-1，充值为正数',
    `balance_after` INT NOT NULL COMMENT '变动后余额',
    `change_type` VARCHAR(20) NOT NULL COMMENT 'GENERATE/RECHARGE/ADMIN',
    `remark` VARCHAR(255) DEFAULT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY `idx_user_id` (`user_id`),
    KEY `idx_order_no` (`order_no`)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '次数消耗记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
