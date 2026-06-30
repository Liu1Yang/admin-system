package com.liuyang.admin.controller;

import com.liuyang.admin.common.Result;
import com.liuyang.admin.entity.User;
import com.liuyang.admin.service.UserService;
import com.liuyang.admin.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<List<UserVO>> list() {
        List<UserVO> list = userService.listAll() // [user1,user2]
                .stream() // 变成流，方便处理
                .map(this::toVO)  // [userVO1,userVo2]   // 等价于.map(user -> toVO(user))   // ✅ 同文件，不用 this:: 也行
                .collect(Collectors.toList()); // 再变回list
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        return Result.success(toVO(user));
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);  // 把 user 里和 vo 同名的属性，复制到 vo 上
        return vo;
    }
}
