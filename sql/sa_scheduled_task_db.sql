/*
 Navicat Premium Data Transfer

 Source Server         : mysql@154.8.144.189-测试数据库
 Source Server Type    : MySQL
 Source Server Version : 80200
 Source Host           : 154.8.144.189:23306
 Source Schema         : sa_scheduled_task_db

 Target Server Type    : MySQL
 Target Server Version : 80200
 File Encoding         : 65001

 Date: 27/11/2023 10:23:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for s_actuator_info
-- ----------------------------
DROP TABLE IF EXISTS `s_actuator_info`;
CREATE TABLE `s_actuator_info`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `actuator_name` varchar(900) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器名称',
  `actuator_ip` varchar(900) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器ip',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '执行器' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_scheduling_log
-- ----------------------------
DROP TABLE IF EXISTS `s_scheduling_log`;
CREATE TABLE `s_scheduling_log`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `task_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务id',
  `app_name` varchar(900) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器名称',
  `task_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '携带参数',
  `done` tinyint(1) NULL DEFAULT NULL COMMENT '完成状态',
  `response_result` varchar(900) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '响应结果',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for s_task_info
-- ----------------------------
DROP TABLE IF EXISTS `s_task_info`;
CREATE TABLE `s_task_info`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `title` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `app_name` varchar(900) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器名称',
  `periodic` tinyint(1) NULL DEFAULT NULL COMMENT '是否为循环任务',
  `initial_delay` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '延迟时间（毫秒）',
  `period` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '间隔时间（毫秒）',
  `task_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务携带参数',
  `cancelled` tinyint(1) NULL DEFAULT NULL COMMENT '是否已取消',
  `done` tinyint(1) NULL DEFAULT NULL COMMENT '是否已完成',
  `next_execution_time` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '下一次执行时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务基表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
