-- 商品表（在 category.sql 已执行后运行）
USE admin_system;

CREATE TABLE IF NOT EXISTS product (
    id          BIGINT         NOT NULL AUTO_INCREMENT COMMENT '主键',
    name        VARCHAR(100)   NOT NULL COMMENT '商品名称',
    category_id BIGINT         NOT NULL COMMENT '分类 ID',
    price       DECIMAL(10, 2) NOT NULL COMMENT '售价',
    stock       INT            NOT NULL DEFAULT 0 COMMENT '库存',
    status      TINYINT        NOT NULL DEFAULT 0 COMMENT '0下架 1上架',
    cover_url   VARCHAR(255)   DEFAULT NULL COMMENT '封面图 URL',
    description TEXT           COMMENT '商品描述',
    creator_id  BIGINT         NOT NULL COMMENT '创建人 user.id',
    create_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category_id (category_id),
    KEY idx_status (status),
    KEY idx_creator_id (creator_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 示例商品（creator_id=1 对应 admin）
INSERT IGNORE INTO product (id, name, category_id, price, stock, status, description, creator_id) VALUES
(1, 'iPhone 15', 2, 5999.00, 100, 1, '苹果智能手机', 1),
(2, 'MacBook Pro', 3, 12999.00, 50, 1, '苹果笔记本电脑', 1),
(3, '休闲 T 恤', 4, 99.00, 200, 0, '纯棉短袖', 1);
