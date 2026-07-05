package com.liuyang.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.liuyang.admin.common.Result;
import com.liuyang.admin.dto.UserCreateDTO;
import com.liuyang.admin.dto.UserUpdateDTO;
import com.liuyang.admin.entity.User;
import com.liuyang.admin.service.UserService;
import com.liuyang.admin.vo.PageVO;
import com.liuyang.admin.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<PageVO<UserVO>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String username) { // 分页+可选搜索
        Page<User> userPage = userService.page(page, size, username);

        PageVO<UserVO> pageVO = new PageVO<>();
        pageVO.setRecords(userPage.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList()));
/* 等同于   =》    把 List<User> 转成 List<UserVO>。
    可更换成
    List<User> userList = userPage.getRecords();
    List<UserVO> voList = new ArrayList<>();
    for (User user : userList) {
        voList.add(toVO(user));
    }
    pageVO.setRecords(voList);
*/
        pageVO.setTotal(userPage.getTotal());
        pageVO.setCurrent(userPage.getCurrent());
        pageVO.setSize(userPage.getSize());
        pageVO.setPages(userPage.getPages());

        return Result.success(pageVO);
    }

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.fail(404, "用户不存在");
        }
        return Result.success(toVO(user));
    }

    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserCreateDTO dto) {
        User user = userService.create(dto);
        return Result.success(toVO(user));
    }

    @PutMapping("/{id}")
    public Result<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO dto) {
        User user = userService.update(id, dto);
        return Result.success(toVO(user));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    private UserVO toVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
