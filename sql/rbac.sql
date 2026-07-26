-- RBAC 权限表（在 init.sql 已执行后运行）
USE admin_system;

-- 角色表
CREATE TABLE IF NOT EXISTS role (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    code        VARCHAR(50)  NOT NULL COMMENT '角色编码，如 ADMIN',
    name        VARCHAR(50)  NOT NULL COMMENT '角色名称',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表（接口级）
CREATE TABLE IF NOT EXISTS permission (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    code        VARCHAR(100) NOT NULL COMMENT '权限编码，如 user:delete',
    name        VARCHAR(100) NOT NULL COMMENT '权限名称',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户-角色关联（复合主键）
CREATE TABLE IF NOT EXISTS user_role (
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    role_id BIGINT NOT NULL COMMENT '角色 ID',
    PRIMARY KEY (user_id, role_id),
    KEY idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色-权限关联（复合主键）
CREATE TABLE IF NOT EXISTS role_permission (
    role_id       BIGINT NOT NULL COMMENT '角色 ID',
    permission_id BIGINT NOT NULL COMMENT '权限 ID',
    PRIMARY KEY (role_id, permission_id),
    KEY idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 初始角色
INSERT IGNORE INTO role (code, name) VALUES
('ADMIN', '管理员'),
('USER', '普通用户');

-- 初始权限
INSERT IGNORE INTO permission (code, name) VALUES
('user:delete',   '删除用户'),
('role:assign',   '分配用户角色'),
('product:write', '商品增删改'),
('product:delete','删除商品');

-- ADMIN 拥有全部权限
INSERT IGNORE INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permission p
WHERE r.code = 'ADMIN';

-- 测试账号绑定角色：admin → ADMIN，liuyang → USER
INSERT IGNORE INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM user u
CROSS JOIN role r
WHERE u.username = 'admin' AND r.code = 'ADMIN';

INSERT IGNORE INTO user_role (user_id, role_id)
SELECT u.id, r.id
FROM user u
CROSS JOIN role r
WHERE u.username = 'liuyang' AND r.code = 'USER';
