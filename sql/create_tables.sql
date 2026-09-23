-- ============================================================
-- 校园活动报名系统 建表脚本
-- 数据库：MySQL 8.x   字符集：utf8mb4
-- 使用方法：mysql -uroot -p < sql/create_tables.sql
-- ============================================================

CREATE DATABASE IF NOT EXISTS school_register
    DEFAULT CHARACTER SET utf8mb4;

USE school_register;

-- ------------------------------------------------------------
-- 1. 系统用户表 sys_user
--    （契约冻结字段 + 登录令牌 token，token 用于登录态校验）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user
(
    user_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username  VARCHAR(20) NOT NULL UNIQUE        COMMENT '用户名',
    password  VARCHAR(255) NOT NULL              COMMENT '密码（BCrypt 哈希，兼容旧 SHA-256 记录）',
    real_name VARCHAR(10) NOT NULL               COMMENT '真实姓名',
    token     VARCHAR(64) NULL                   COMMENT '登录令牌（登录时生成）'
) ENGINE = InnoDB COMMENT = '系统用户表';

-- ------------------------------------------------------------
-- 2. 校园活动表 activity
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS activity
(
    activity_id   BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '活动ID',
    title         VARCHAR(100) NOT NULL             COMMENT '活动标题',
    description   TEXT                              COMMENT '活动描述',
    max_people    INT          NOT NULL             COMMENT '最大报名人数',
    deadline      DATETIME     NOT NULL             COMMENT '报名截止时间',
    create_user_id BIGINT      NOT NULL             COMMENT '创建者ID',
    create_time   DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',

    CONSTRAINT fk_activity_user
        FOREIGN KEY (create_user_id) REFERENCES sys_user (user_id)
) ENGINE = InnoDB COMMENT = '校园活动表';

-- ------------------------------------------------------------
-- 3. 活动报名记录表 apply_record
--    同一用户对同一活动只能报名一次（唯一约束）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS apply_record
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '报名记录ID',
    user_id     BIGINT NOT NULL                   COMMENT '报名用户ID',
    activity_id BIGINT NOT NULL                   COMMENT '活动ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '报名时间',

    UNIQUE KEY uk_user_activity (user_id, activity_id),

    CONSTRAINT fk_apply_user
        FOREIGN KEY (user_id) REFERENCES sys_user (user_id),
    CONSTRAINT fk_apply_activity
        FOREIGN KEY (activity_id) REFERENCES activity (activity_id)
) ENGINE = InnoDB COMMENT = '活动报名记录表';

-- ============================================================
-- 示例数据（两个测试账号密码均为 123456，存储的是其 SHA-256 摘要）
-- SHA-256('123456') = 8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92
-- ============================================================
INSERT INTO sys_user (username, password, real_name)
VALUES ('zhangsan', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '张三'),
       ('lisi', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', '李四');

INSERT INTO activity (title, description, max_people, deadline, create_user_id)
VALUES ('春季校园运动会',
        '一年一度的春季运动会，设有田径、篮球、羽毛球等多个项目，欢迎同学们踊跃报名参加。',
        50, '2026-12-31 23:59:59', 1),
       ('校园编程马拉松',
        '48小时极限开发挑战，组队完成创意作品，丰厚奖金等你来拿！',
        30, '2026-12-31 23:59:59', 1),
       ('读书分享会',
        '本期主题：科幻文学中的想象力。欢迎带着你喜欢的书来分享交流。',
        20, '2026-12-31 23:59:59', 2);

INSERT INTO apply_record (user_id, activity_id)
VALUES (2, 1);
