package com.liuyang.admin;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * 冒烟测试：不启动 Spring 容器（无需 MySQL/Redis）。
 * 业务单元测试见 common/、service/、controller/ 包下 *Test 类。
 */
class AdminApplicationTests {

    @Test
    void mainClassShouldLoad() {
        assertDoesNotThrow(() -> Class.forName("com.liuyang.admin.AdminApplication"));
    }
}
