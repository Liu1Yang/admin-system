-- RBAC 数据验证（Day13 联调时在 MySQL 执行）
USE admin_system;

-- 1. 用户与角色
SELECT u.id, u.username, r.code AS role_code, r.name AS role_name
FROM user u
LEFT JOIN user_role ur ON u.id = ur.user_id
LEFT JOIN role r ON ur.role_id = r.id
ORDER BY u.id;

-- 2. 角色与权限
SELECT r.code AS role_code, p.code AS permission_code, p.name AS permission_name
FROM role r
JOIN role_permission rp ON r.id = rp.role_id
JOIN permission p ON rp.permission_id = p.id
ORDER BY r.code, p.code;

-- 3. 某用户的有效权限（以 liuyang 为例，应无 permission 行）
SELECT u.username, p.code AS permission_code
FROM user u
JOIN user_role ur ON u.id = ur.user_id
JOIN role_permission rp ON ur.role_id = rp.role_id
JOIN permission p ON rp.permission_id = p.id
WHERE u.username = 'liuyang';

-- 4. admin 应有 4 条权限
SELECT u.username, COUNT(DISTINCT p.id) AS permission_count
FROM user u
JOIN user_role ur ON u.id = ur.user_id
JOIN role_permission rp ON ur.role_id = rp.role_id
JOIN permission p ON rp.permission_id = p.id
WHERE u.username = 'admin'
GROUP BY u.username;
