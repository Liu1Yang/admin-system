package com.liuyang.admin.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler { // 自定义类名 类名不重要，注解才重要。

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)  // 注解含义：分类型处理
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class) //@RequestBody+@Valid 处理JSON接口  测试Valid校验失败
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        String message = extractFirstFieldMessage(e.getBindingResult());
        return Result.fail(400, message);
    }

    @ExceptionHandler(BindException.class)  // 表单提交
    public Result<Void> handleBindException(BindException e) { // （表单绑定失败） 一般出现在 表单提交（application/x-www-form-urlencoded）场景。
        String message = extractFirstFieldMessage(e.getBindingResult());
        return Result.fail(400, message);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // 开发阶段：在 IDEA 控制台打印完整堆栈，方便定位 bug
        log.error("未捕获异常: {}", e.getMessage(), e);
        return Result.fail(500, "服务器内部错误");
    }

    /**
     * 从校验结果里取出第一条字段错误提示。
     * BindingResult 里可能有多个 FieldError，这里先只返回第一个。
     */
    private String extractFirstFieldMessage(BindingResult bindingResult) { // 提取第一个字段信息
        FieldError fieldError = bindingResult.getFieldError();
        if (fieldError == null) {
            return "参数校验失败";
        }
        return fieldError.getDefaultMessage();
    }
}
