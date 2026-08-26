-- Day41 操作日志表（在 rbac.sql 已执行后运行）
USE admin_system;

SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS operation_log (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    user_id     BIGINT       DEFAULT NULL COMMENT '操作人 ID',
    username    VARCHAR(50)  DEFAULT NULL COMMENT '操作人用户名',
    module      VARCHAR(50)  NOT NULL COMMENT '模块，如 商品',
    action      VARCHAR(50)  NOT NULL COMMENT '动作，如 新增',
    method      VARCHAR(10)  NOT NULL COMMENT 'HTTP 方法',
    uri         VARCHAR(255) NOT NULL COMMENT '请求 URI',
    ip          VARCHAR(64)  DEFAULT NULL COMMENT '客户端 IP',
    success     TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否成功：1 是 0 否',
    duration_ms INT          DEFAULT NULL COMMENT '耗时毫秒',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志';

-- 查看操作日志权限（ADMIN 通过 rbac 全量授权获得）
INSERT IGNORE INTO permission (code, name) VALUES
('log:read', '查看操作日志');

INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
JOIN permission p ON p.code = 'log:read'
WHERE r.code = 'ADMIN';
