-- 滑动窗口限流（ZSET + Lua，单次 EVAL 保证原子性）
-- KEYS[1] = Redis key
-- ARGV[1] = 窗口毫秒 windowMs
-- ARGV[2] = 限额 maxRequests
-- ARGV[3] = 当前时间戳毫秒 now
-- ARGV[4] = 本次请求唯一 member（避免同一毫秒冲突）
-- 返回 1=放行 0=限流

local key = KEYS[1]
local window = tonumber(ARGV[1])
local limit = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local member = ARGV[4]

-- 1. 删掉窗口外的请求记录
redis.call('ZREMRANGEBYSCORE', key, 0, now - window)

-- 2. 统计窗口内请求数
local count = redis.call('ZCARD', key)
if count >= limit then
  return 0
end

-- 3. 记录本次请求，并设置 key 过期（窗口结束后自动清理）
redis.call('ZADD', key, now, member)
redis.call('PEXPIRE', key, window)
return 1
