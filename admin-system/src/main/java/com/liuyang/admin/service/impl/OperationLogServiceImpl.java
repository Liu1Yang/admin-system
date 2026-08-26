package com.liuyang.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyang.admin.entity.OperationLog;
import com.liuyang.admin.mapper.OperationLogMapper;
import com.liuyang.admin.mq.OperationLogMessage;
import com.liuyang.admin.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public void saveFromMessage(OperationLogMessage message) { // 将 MQ 消息转成数据库实体并保存
        OperationLog log = new OperationLog();
        log.setUserId(message.getUserId());
        log.setUsername(message.getUsername());
        log.setModule(message.getModule());
        log.setAction(message.getAction());
        log.setMethod(message.getMethod());
        log.setUri(message.getUri());
        log.setIp(message.getIp());
        log.setSuccess(message.getSuccess() != null && message.getSuccess() ? 1 : 0);
        log.setDurationMs(message.getDurationMs());
        log.setCreateTime(LocalDateTime.now());
        operationLogMapper.insert(log);
    }

    @Override
    public Page<OperationLog> page(int pageNum, int pageSize, String module, String username) { //分页查询操作日志（支持模块、用户名筛选）
        Page<OperationLog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(module)) {
            wrapper.like(OperationLog::getModule, module);
        }
        if (StringUtils.hasText(username)) {
            wrapper.like(OperationLog::getUsername, username);
        }
        wrapper.orderByDesc(OperationLog::getCreateTime);
        return operationLogMapper.selectPage(page, wrapper);
    }
}
