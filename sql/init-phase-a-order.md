-- 阶段 A 数据库初始化顺序（Day20 联调）
-- 在 MySQL 中按顺序执行以下脚本：

-- 1. 基础用户表
--    sql/init.sql

-- 2. 密码改为 BCrypt（测试账号密码仍为 123456）
--    sql/update-password-bcrypt.sql

-- 3. RBAC 角色权限
--    sql/rbac.sql

-- 4. 商品分类
--    sql/category.sql

-- 5. 商品表
--    sql/product.sql

-- 验证脚本（可选）
--    sql/rbac-verify.sql
