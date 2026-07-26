-- 商品分类表（在 rbac.sql 已执行后运行）
USE admin_system;

CREATE TABLE IF NOT EXISTS category (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(50)  NOT NULL COMMENT '分类名称',
    parent_id   BIGINT       NOT NULL DEFAULT 0 COMMENT '父分类 ID，0 表示顶级',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序值，越小越靠前',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 示例数据：数码 → 手机 / 电脑
INSERT IGNORE INTO category (id, name, parent_id, sort) VALUES
(1, '数码', 0, 1),
(2, '手机', 1, 1),
(3, '电脑', 1, 2),
(4, '服装', 0, 2);
