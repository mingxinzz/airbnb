/*
 Navicat Premium Data Transfer

 Source Server         : 4207产教融合
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44-0ubuntu0.24.04.2)
 Source Host           : 43.142.157.145:3306
 Source Schema         : airbnb_analysis

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44-0ubuntu0.24.04.2)
 File Encoding         : 65001

 Date: 24/06/2026 19:11:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for calendar_price_trend
-- ----------------------------
DROP TABLE IF EXISTS `calendar_price_trend`;
CREATE TABLE `calendar_price_trend`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stat_month` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avg_price` decimal(10, 2) NULL DEFAULT NULL,
  `sample_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for host_house_topn
-- ----------------------------
DROP TABLE IF EXISTS `host_house_topn`;
CREATE TABLE `host_house_topn`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `host_id` bigint NULL DEFAULT NULL,
  `host_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `house_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for monthly_review_trend
-- ----------------------------
DROP TABLE IF EXISTS `monthly_review_trend`;
CREATE TABLE `monthly_review_trend`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `review_month` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `review_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 91 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for price_review_relation
-- ----------------------------
DROP TABLE IF EXISTS `price_review_relation`;
CREATE TABLE `price_review_relation`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `listing_id` bigint NULL DEFAULT NULL,
  `neighbourhood_cleansed` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `room_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NULL DEFAULT NULL,
  `number_of_reviews` int NULL DEFAULT NULL,
  `review_scores_rating` double NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3586 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for region_avg_price
-- ----------------------------
DROP TABLE IF EXISTS `region_avg_price`;
CREATE TABLE `region_avg_price`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `neighbourhood_cleansed` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avg_price` decimal(10, 2) NULL DEFAULT NULL,
  `min_price` decimal(10, 2) NULL DEFAULT NULL,
  `max_price` decimal(10, 2) NULL DEFAULT NULL,
  `house_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for region_house_count
-- ----------------------------
DROP TABLE IF EXISTS `region_house_count`;
CREATE TABLE `region_house_count`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `neighbourhood_cleansed` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `house_count` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for review_word_freq
-- ----------------------------
DROP TABLE IF EXISTS `review_word_freq`;
CREATE TABLE `review_word_freq`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `word` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `freq` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for room_type_ratio
-- ----------------------------
DROP TABLE IF EXISTS `room_type_ratio`;
CREATE TABLE `room_type_ratio`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `room_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `house_count` int NULL DEFAULT NULL,
  `ratio` decimal(10, 4) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
