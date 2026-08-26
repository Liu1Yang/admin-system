package com.liuyang.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyang.admin.entity.OperationLog;
import com.liuyang.admin.mq.OperationLogMessage;

public interface OperationLogService {

    void saveFromMessage(OperationLogMessage message);

    Page<OperationLog> page(int pageNum, int pageSize, String module, String username);
}
