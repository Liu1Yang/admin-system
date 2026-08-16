# Day35 单元测试

> **目标：** 用 JUnit 5 + Mockito 为核心业务写**不依赖 MySQL/Redis** 的单元测试。

## 测试分层

| 类型 | 说明 | 本项目示例 |
|------|------|------------|
| **单元测试** | 只测一个类，依赖用 Mock | `JwtUtilTest`、`AuthServiceImplTest` |
| **接口测试** | MockMvc 测 Controller | `HealthControllerTest` |
| **集成测试** | 启动 Spring + 真 DB | 暂未引入（Day35 先掌握单元测试） |

## 项目内测试类

| 类 | 测什么 |
|----|--------|
| `JwtUtilTest` | Token 生成、解析、过期、篡改 |
| `AuthServiceImplTest` | 登录成功/失败、登出黑名单 |
| `TokenBlacklistServiceTest` | Redis 黑名单读写 |
| `HealthControllerTest` | `/api/health` 返回 UP |
| `AdminApplicationTests` | 冒烟：主类可加载 |

## 运行

```powershell
cd admin-system
mvn test
```

只跑某一类：

```powershell
mvn test -Dtest=JwtUtilTest
mvn test -Dtest=AuthServiceImplTest
```

IDEA：右键测试类 → Run。

## 关键技术点

### 1. 纯单元测试（不启 Spring）

```java
@BeforeEach
void setUp() {
    JwtProperties props = new JwtProperties();
    props.setSecret("test-jwt-secret-key-at-least-32-chars");
    jwtUtil = new JwtUtil(props);
}
```

### 2. Mockito 测 Service

```java
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock UserMapper userMapper;
    // when(...).thenReturn(...)
    // verify(...).add(...)
}
```

### 3. Standalone MockMvc 测 Controller

```java
mockMvc = MockMvcBuilders.standaloneSetup(new HealthController()).build();
mockMvc.perform(get("/api/health")).andExpect(status().isOk());
```

## 面试常问

- **单元测试 vs 集成测试**：单元快、隔离；集成测链路但慢、依赖环境  
- **Mock 是什么**：替真实依赖，只验证当前类逻辑  
- **`@SpringBootTest` 何时用**：集成测试；Day35 故意不用，避免没 DB 跑不起来  
- **测试命名**：`方法_条件_期望` 如 `login_shouldThrowWhenPasswordWrong`

## Day35 验收

- [ ] `mvn test` 全部通过
- [ ] 能说出 JwtUtil / AuthService 各测了哪些场景
- [ ] 知道 Mockito 的 `when` / `verify` 用法

## 下一步

- **Day36**：双 Token（Access + Refresh）
- **Day37**：接口限流
- **Day38**：收尾与复习
