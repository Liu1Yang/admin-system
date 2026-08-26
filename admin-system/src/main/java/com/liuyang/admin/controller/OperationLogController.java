package com.liuyang.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyang.admin.annotation.RequirePermission;
import com.liuyang.admin.common.Result;
import com.liuyang.admin.entity.OperationLog;
import com.liuyang.admin.service.OperationLogService;
import com.liuyang.admin.vo.OperationLogVO;
import com.liuyang.admin.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Tag(name = "操作日志")
@RestController
@RequestMapping("/api/operation-logs")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @Operation(summary = "分页查询操作日志", description = "需 log:read 权限，数据由 MQ 异步写入")
    @RequirePermission("log:read")
    @GetMapping
    public Result<PageVO<OperationLogVO>> page(
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "模块（模糊）") @RequestParam(required = false) String module,
            @Parameter(description = "用户名（模糊）") @RequestParam(required = false) String username) {
        Page<OperationLog> logPage = operationLogService.page(page, size, module, username);

        PageVO<OperationLogVO> pageVO = new PageVO<>();
        pageVO.setRecords(logPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList()));
        pageVO.setTotal(logPage.getTotal());
        pageVO.setCurrent(logPage.getCurrent());
        pageVO.setSize(logPage.getSize());
        pageVO.setPages(logPage.getPages());
        return Result.success(pageVO);
    }

    private OperationLogVO toVO(OperationLog log) {
        OperationLogVO vo = new OperationLogVO();
        BeanUtils.copyProperties(log, vo);
        vo.setSuccess(log.getSuccess() != null && log.getSuccess() == 1);
        return vo;
    }
}
