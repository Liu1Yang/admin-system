package com.liuyang.admin.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注接口所需权限编码，由 PermissionInterceptor 校验。
 * 可加在 Controller 类或方法上；方法上的注解优先。
 */
@Target({ElementType.METHOD, ElementType.TYPE}) // 1. @Target：告诉编译器，这个标签能贴在哪里（METHOD=方法，TYPE=类/接口）
@Retention(RetentionPolicy.RUNTIME) // 2. @Retention：告诉编译器，这个标签要保留到运行时（这样才能在拦截器里通过反射读取）
public @interface RequirePermission { // 注意：这里用的是 @interface，不是 interface

    String value();

    // 还可以写别的参数，比如逻辑关系（但你们目前只用了 value）
    // String message() default "无权限";
}
