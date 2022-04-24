/*
 Navicat Premium Data Transfer

 Source Server         : MDM-DEV
 Source Server Type    : MySQL
 Source Server Version : 80025
 Source Host           : 10.1.1.92:3306
 Source Schema         : demdm

 Target Server Type    : MySQL
 Target Server Version : 80025
 File Encoding         : 65001

 Date: 24/04/2022 09:39:15
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for MDM_SUB_ID_MAP
-- ----------------------------
DROP TABLE IF EXISTS `MDM_SUB_ID_MAP`;
CREATE TABLE `MDM_SUB_ID_MAP`  (
  `ID` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '主键',
  `FORM_ID` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '表单id',
  `MDM_ID` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '主数据id',
  `MDM_SUB_ID` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '子表数据id',
  `SUB_TB_PK_ID` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '子表主键',
  `ESB_ID` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原始主数据id',
  `ESB_SUB_ID` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原始子数据id',
  `CREATED_BY` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `CREATION_DATE` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `LAST_UPDATED_BY` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后更新人',
  `LAST_UPDATE_DATE` datetime NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
  `OBJECT_VERSION_NUMBER` int NULL DEFAULT NULL COMMENT '版本号',
  `OWNER` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '拥有者',
  `TENANT_ID` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户id',
  `DATA_STATUS` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '数据状态',
  `ENABLE_STATUS` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'DISABLED' COMMENT '状态：启用/禁用',
  PRIMARY KEY (`ID`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '三方ID与MDM子表映射' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
