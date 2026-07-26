package com.liuyang.admin.common;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String message;
    private T data;

   //  public static Result<T> success(T data) ❌ 编译报错：静态方法不能引用类的类型参数 T
   //实例方法（非static）:对象存在 → 类的 T 有具体含义 → 实例方法可以直接用。
   //静态方法没有「当前对象是哪个 Result<XXX>」→ 必须自己声明 <T>。
    public static <T> Result<T> success(T data) {  // static: 属于类本身，不属于某个对象
        //         ↑ 这是方法自己的 T，不是「借」类上的 T

        Result<T> result = new Result<>(); // 这么设计任何地方都能Result.success()而不用先 new Result<>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}
