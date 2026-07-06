-- 已有数据库执行此脚本，将明文密码更新为 BCrypt 加密（密码仍为 123456）
USE admin_system;

UPDATE user SET password = '$2a$10$SIurTUFpiF95/ernfT389.gqpE2BLIReqSefR7nBuwaLERhG3A9JG'
WHERE username IN ('admin', 'liuyang');
