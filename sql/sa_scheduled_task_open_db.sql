/*
 Navicat Premium Data Transfer

 Source Server         : mysql@127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80300
 Source Host           : localhost:3306
 Source Schema         : sa_scheduled_task_open_db

 Target Server Type    : MySQL
 Target Server Version : 80300
 File Encoding         : 65001

 Date: 08/05/2024 13:17:29
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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '执行器' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of s_actuator_info
-- ----------------------------

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
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '调度日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of s_scheduling_log
-- ----------------------------

-- ----------------------------
-- Table structure for s_task_info
-- ----------------------------
DROP TABLE IF EXISTS `s_task_info`;
CREATE TABLE `s_task_info`  (
  `id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `code` varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '代码',
  `title` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标题',
  `app_name` varchar(900) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行器名称',
  `initial_delay` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '延迟时间（毫秒）',
  `task_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务携带参数',
  `cancelled` tinyint(1) NULL DEFAULT NULL COMMENT '是否已取消',
  `done` tinyint(1) NULL DEFAULT NULL COMMENT '是否已完成',
  `next_execution_time` bigint NULL DEFAULT NULL COMMENT '下一次执行时间',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `tenant_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户号',
  `cron` varchar(90) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'cron',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '任务基表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of s_task_info
-- ----------------------------
INSERT INTO `s_task_info` VALUES ('c887ec55ecf14037a29beed6c44ce7b1', 'deleteThirtyDaysAgoActuatorLogs', '删除三十天前的执行器日志', 'actuator-1', NULL, '', 0, 0, 1714410000000, '2024-02-28 09:41:36', 'a2fd0ec3170eeaed9077bc89b4c92f7f', '0 0 1 * * ?');

SET FOREIGN_KEY_CHECKS = 1;
