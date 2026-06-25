-- Pipeline 任务控制表
DROP TABLE IF EXISTS `pipeline_task`;
CREATE TABLE `pipeline_task` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` varchar(64) NOT NULL,
  `task_status` varchar(32) NOT NULL DEFAULT 'CREATED',
  `current_stage` varchar(64) NOT NULL DEFAULT 'CREATED',
  `message` varchar(512) NULL DEFAULT NULL,
  `log_file` varchar(512) NULL DEFAULT NULL,
  `local_dir` varchar(512) NULL DEFAULT NULL,
  `submitted_at` datetime NULL DEFAULT NULL,
  `start_time` datetime NULL DEFAULT NULL,
  `end_time` datetime NULL DEFAULT NULL,
  `error_message` text NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_task_id`(`task_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;

DROP TABLE IF EXISTS `pipeline_task_log`;
CREATE TABLE `pipeline_task_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `task_id` varchar(64) NOT NULL,
  `log_time` datetime NOT NULL,
  `level` varchar(16) NOT NULL DEFAULT 'INFO',
  `stage` varchar(64) NULL DEFAULT NULL,
  `status` varchar(32) NULL DEFAULT NULL,
  `message` varchar(1024) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_task_id`(`task_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
