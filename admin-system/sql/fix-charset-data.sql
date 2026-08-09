-- Fix mojibake: ASCII-only SQL (safe on Windows PowerShell)
SET NAMES utf8mb4;

UPDATE user SET nickname = _utf8mb4 X'E7AEA1E79086E59198' WHERE username = 'admin';
UPDATE user SET nickname = _utf8mb4 X'E58898E6B48B' WHERE username = 'liuyang';

UPDATE role SET name = _utf8mb4 X'E7AEA1E79086E59198' WHERE code = 'ADMIN';
UPDATE role SET name = _utf8mb4 X'E699AEE9809AE794A8E688B7' WHERE code = 'USER';

UPDATE permission SET name = _utf8mb4 X'E588A0E997A4E794A8E688B7' WHERE code = 'user:delete';
UPDATE permission SET name = _utf8mb4 X'E58886E9858DE794A8E688B7E8A792E889B2' WHERE code = 'role:assign';
UPDATE permission SET name = _utf8mb4 X'E59586E59381E5899EE588A0E694B9' WHERE code = 'product:write';
UPDATE permission SET name = _utf8mb4 X'E588A0E997A4E59586E59381' WHERE code = 'product:delete';

UPDATE category SET name = _utf8mb4 X'E695B0E7A081' WHERE id = 1;
UPDATE category SET name = _utf8mb4 X'E6898BE69CBA' WHERE id = 2;
UPDATE category SET name = _utf8mb4 X'E794B5E88491' WHERE id = 3;
UPDATE category SET name = _utf8mb4 X'E69C8DE8A385' WHERE id = 4;

UPDATE product SET description = _utf8mb4 X'E8919BE69E9CE699BAE883BDE6898BE69CBA' WHERE id = 1;
UPDATE product SET description = _utf8mb4 X'E8919BE7AC94E695B0E794B5E88491' WHERE id = 2;
UPDATE product SET name = _utf8mb4 X'E4BC91E99785205420E681A4' WHERE id = 3;
UPDATE product SET description = _utf8mb4 X'E7BAAFE68DA9E789ADE88996' WHERE id = 3;
