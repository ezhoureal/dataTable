shiro是控制网络会话
#一、架构
1. 令牌
2. 执行登录动作
```SecurityUtils.setSecurityManager(securityManager); // 注入SecurityManager
Subject subject = SecurityUtils.getSubject(); // 获取Subject单例对象
subject.login(token); // 登陆
```
SecurityManager负责安全认证和授权。subject是正在设计的用shiro保护的那个项目或模块
3. 判断用户
shiro无法判断用户是否合法，项目设计人员通过Realm来自定义判断是否合法的条件。
4. AuthenticationInfo和AuthorizationInfo
第一个是身份认证信息（用户名和密码），第二个是用户的权限信息。

#二、实现Realm
注册时用散列算法对密码进行加密，再存入数据库。
```
public class PasswordHelper {
    private RandomNumberGenerator randomNumberGenerator = new SecureRandomNumberGenerator();
    private String algorithmName = "md5";
    private final int hashIterations = 2;

    public void encryptPassword(User user) {
        // User对象包含最基本的字段Username和Password
        user.setSalt(randomNumberGenerator.nextBytes().toHex());
        // 将用户的注册密码经过散列算法替换成一个不可逆的新密码保存进数据，散列过程使用了盐
        String newPassword = new SimpleHash(algorithmName, user.getPassword(),
                ByteSource.Util.bytes(user.getCredentialsSalt()), hashIterations).toHex();
        user.setPassword(newPassword);
    }
}
```

获取用户的角色和权限信息
```
public class UserRealm extends AuthorizingRealm {
    // 用户对应的角色信息与权限信息都保存在数据库中，通过UserService获取数据
    private UserService userService = new UserServiceImpl();

    /**
     * 提供用户信息返回权限信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        // 根据用户名查询当前用户拥有的角色
        Set<Role> roles = userService.findRoles(username);
        Set<String> roleNames = new HashSet<String>();
        for (Role role : roles) {
            roleNames.add(role.getRole());
        }
        // 将角色名称提供给info
        authorizationInfo.setRoles(roleNames);
        // 根据用户名查询当前用户权限
        Set<Permission> permissions = userService.findPermissions(username);
        Set<String> permissionNames = new HashSet<String>();
        for (Permission permission : permissions) {
            permissionNames.add(permission.getPermission());
        }
        // 将权限名称提供给info
        authorizationInfo.setStringPermissions(permissionNames);

        return authorizationInfo;
    }

    /**
     * 提供账户信息返回认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        String username = (String) token.getPrincipal();
        User user = userService.findByUsername(username);
        if (user == null) {
            // 用户名不存在抛出异常
            throw new UnknownAccountException();
        }
        if (user.getLocked() == 0) {
            // 用户被管理员锁定抛出异常
            throw new LockedAccountException();
        }
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getUsername(),
                user.getPassword(), ByteSource.Util.bytes(user.getCredentialsSalt()), getName());
        return authenticationInfo;
    }
}
```
在数据库中需要因此建立5张表，分别是用户表（存储用户名，密码，盐等）、角色表（角色名称，相关描述等）、权限表（权限名称，相关描述等）、用户-角色对应中间表（以用户ID和角色ID作为联合主键）、角色-权限对应中间表（以角色ID和权限ID作为联合主键）。
Shiro需要根据用户名和密码首先判断登录的用户是否合法，然后再对合法用户授权。而这个过程就是Realm的实现过程。

#shiro与springMVC结合使用
在web.xml中的配置
```
<context-param>
        <param-name>contextConfigLocation</param-name>
        <!-- 将Shiro的配置文件交给Spring监听器初始化 -->
        <param-value>classpath:spring.xml,classpath:spring-shiro-web.xml</param-value>
    </context-param>
    <context-param>
        <param-name>log4jConfigLoaction</param-name>
        <param-value>classpath:log4j.properties</param-value>
    </context-param>
    <!-- shiro配置 开始 -->
    <filter>
        <filter-name>shiroFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        <async-supported>true</async-supported>
        <init-param>
            <param-name>targetFilterLifecycle</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>shiroFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```
然后要配置spring-shiro-web.xml
```
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:p="http://www.springframework.org/schema/p"
    xmlns:context="http://www.springframework.org/schema/context"
    xmlns:mvc="http://www.springframework.org/schema/mvc"
    xsi:schemaLocation="http://www.springframework.org/schema/beans    
                        http://www.springframework.org/schema/beans/spring-beans-3.1.xsd    
                        http://www.springframework.org/schema/context    
                        http://www.springframework.org/schema/context/spring-context-3.1.xsd    
                        http://www.springframework.org/schema/mvc    
                        http://www.springframework.org/schema/mvc/spring-mvc-4.0.xsd">

    <!-- 缓存管理器 使用Ehcache实现 -->
    <bean id="cacheManager" class="org.apache.shiro.cache.ehcache.EhCacheManager">
        <property name="cacheManagerConfigFile" value="classpath:ehcache.xml" />
    </bean>

    <!-- 凭证匹配器 -->
    <bean id="credentialsMatcher" class="utils.RetryLimitHashedCredentialsMatcher">
        <constructor-arg ref="cacheManager" />
        <property name="hashAlgorithmName" value="md5" />
        <property name="hashIterations" value="2" />
        <property name="storedCredentialsHexEncoded" value="true" />
    </bean>

    <!-- Realm实现 -->
    <bean id="userRealm" class="utils.UserRealm">
        <property name="credentialsMatcher" ref="credentialsMatcher" />
    </bean>

    <!-- 安全管理器 -->
    <bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
        <property name="realm" ref="userRealm" />
    </bean>

    <!-- Shiro的Web过滤器 -->
    <bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
        <property name="securityManager" ref="securityManager" />
        <property name="loginUrl" value="/" />
        <property name="unauthorizedUrl" value="/" />
        <property name="filterChainDefinitions">
            <value>
                /authc/admin = roles[admin]
                /authc/** = authc
                /** = anon
            </value>
        </property>
    </bean>

    <bean id="lifecycleBeanPostProcessor" class="org.apache.shiro.spring.LifecycleBeanPostProcessor" />
</beans>
```
filterChainDefinitions过滤器中对于路径的配置是有顺序的，当找到匹配的条目之后容器不会再继续寻找。因此带有通配符的路径要放在后面。三条配置的含义是： /authc/admin需要用户有用admin权限、/authc/** 用户必须登录才能访问、/**其他所有路径任何人都可以访问。

接着是controller的写法
```
@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @RequestMapping("login")
    public ModelAndView login(@RequestParam("username") String username, @RequestParam("password") String password) {
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (IncorrectCredentialsException ice) {
            // 捕获密码错误异常
            ModelAndView mv = new ModelAndView("error");
            mv.addObject("message", "password error!");
            return mv;
        } catch (UnknownAccountException uae) {
            // 捕获未知用户名异常
            ModelAndView mv = new ModelAndView("error");
            mv.addObject("message", "username error!");
            return mv;
        } catch (ExcessiveAttemptsException eae) {
            // 捕获错误登录过多的异常
            ModelAndView mv = new ModelAndView("error");
            mv.addObject("message", "times error");
            return mv;
        }
        User user = userService.findByUsername(username);
        subject.getSession().setAttribute("user", user);
        return new ModelAndView("success");
    }
}
```
登录完成以后，当前用户信息被保存进Session。这个Session是通过Shiro管理的会话对象，要获取依然必须通过Shiro。传统的Session中不存在User对象。
