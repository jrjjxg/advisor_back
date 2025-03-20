



# 问题分析：JWT 签名密钥格式错误

根据错误日志，我发现问题出在 JWT token 的解析过程中：

```
解析Token时发生其他异常: io.jsonwebtoken.io.DecodingException: Illegal base64 character: '_'
```

这个错误表明在解析 JWT token 时，遇到了非法的 Base64 字符 `_`。这通常是因为**签名密钥格式不匹配**导致的。

## 具体原因

1. **签名密钥格式问题**：
   - JWT 库在使用 `setSigningKey()` 方法时，期望接收的是一个 Base64 编码的密钥
   - 如果您的 `jwtSecret` 包含非 Base64 字符（如下划线 `_`），就会导致这个错误

2. **前后端密钥不一致**：
   - 前端生成 token 和后端验证 token 使用的密钥可能不一致

## 解决方案

### 方案一：修改 `getUserByToken` 方法，正确处理密钥

```java:demo1/src/main/java/com/advisor/service/impl/UserServiceImpl.java
@Override
public User getUserByToken(String token) {
    try {
        System.out.println("开始解析token: " + token.substring(0, Math.min(token.length(), 20)) + "...");
        
        // 使用字节数组作为密钥，避免 Base64 解码问题
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret.getBytes())
                .parseClaimsJws(token)
                .getBody();
        
        System.out.println("Token解析成功，claims: " + claims);
        
        String userId = claims.getSubject();
        System.out.println("从token中获取的userId: " + userId);
        
        User user = userMapper.selectById(userId);
        System.out.println("根据userId查询结果: " + (user == null ? "null" : user.getUsername()));
        
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user;
    } catch (ExpiredJwtException e) {
        System.out.println("Token已过期: " + e.getMessage());
        return null;
    } catch (SignatureException e) {
        System.out.println("Token签名验证失败: " + e.getMessage());
        return null;
    } catch (MalformedJwtException e) {
        System.out.println("Token格式不正确: " + e.getMessage());
        return null;
    } catch (Exception e) {
        System.out.println("解析Token时发生其他异常: " + e.getClass().getName() + ": " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
```

### 方案二：检查并修改 JWT 密钥配置

1. 检查 `application.properties` 或 `application.yml` 中的 JWT 密钥配置：

```properties:demo1/src/main/resources/application.properties
# 确保密钥是有效的 Base64 字符串，或者修改代码以适应当前密钥
jwt.secret=your_secret_key_without_special_characters
```

2. 如果您需要使用包含特殊字符的密钥，请确保在生成和验证 token 时使用相同的方法处理密钥。

### 方案三：使用 HMAC 算法正确处理密钥

```java:demo1/src/main/java/com/advisor/service/impl/UserServiceImpl.java
@Override
public User getUserByToken(String token) {
    try {
        System.out.println("开始解析token: " + token.substring(0, Math.min(token.length(), 20)) + "...");
        
        // 使用 HMAC 算法处理密钥
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        System.out.println("Token解析成功，claims: " + claims);
        
        String userId = claims.getSubject();
        System.out.println("从token中获取的userId: " + userId);
        
        User user = userMapper.selectById(userId);
        System.out.println("根据userId查询结果: " + (user == null ? "null" : user.getUsername()));
        
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user;
    } catch (Exception e) {
        System.out.println("解析Token时发生异常: " + e.getClass().getName() + ": " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
```

注意：这个方案需要添加 `io.jsonwebtoken:jjwt-api:0.11.2` 和相关依赖。

## 其他观察

从日志中我还注意到，虽然 token 解析失败，但系统仍然尝试通过用户名查询用户：

```
==>  Preparing: SELECT id,username,password,nickname,avatar,gender,birth_date,phone,email,create_time,update_time,last_login_time,status,follow_count,fans_count,letter_unread,comment_unread,at_unread,notification_unread,description,post_count,like_unread FROM user WHERE (username = ?)
==> Parameters: xg666(String)
<==      Total: 1
```

这表明用户 `xg666` 在数据库中是存在的，问题确实出在 token 解析环节。






您的 JWT 密钥配置本身没有错误，但是在处理方式上可能存在问题。

`````yaml:demo1/src/main/resources/application.yml
jwt:
  secret: your_jwt_secret_key_here_should_be_very_long_and_secure_12345678901234567890
  expiration: 86400000 # 24小时，单位毫秒
`````


### 问题分析

1. **密钥格式**：
   - 您的密钥 `your_jwt_secret_key_here_should_be_very_long_and_secure_12345678901234567890` 包含下划线 `_` 字符
   - 错误日志显示 `Illegal base64 character: '_'`，这表明 JJWT 库尝试将您的密钥作为 Base64 编码的字符串解析，但下划线不是有效的 Base64 字符

2. **处理方式不一致**：
   - 生成 token 和验证 token 时可能使用了不同的方法处理密钥

### 解决方案

修改 `UserServiceImpl.java` 中的 `getUserByToken` 方法，使用字节数组而不是字符串作为签名密钥：

`````java:demo1/src/main/java/com/advisor/service/impl/UserServiceImpl.java
@Override
public User getUserByToken(String token) {
    try {
        System.out.println("开始解析token: " + token.substring(0, Math.min(token.length(), 20)) + "...");
        
        // 使用字节数组作为密钥，避免 Base64 解码问题
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret.getBytes())
                .parseClaimsJws(token)
                .getBody();
        
        System.out.println("Token解析成功，claims: " + claims);
        
        String userId = claims.getSubject();
        System.out.println("从token中获取的userId: " + userId);
        
        User user = userMapper.selectById(userId);
        System.out.println("根据userId查询结果: " + (user == null ? "null" : user.getUsername()));
        
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user;
    } catch (Exception e) {
        System.out.println("解析Token时发生异常: " + e.getClass().getName() + ": " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
`````


### 确保一致性

同时，您需要确保在生成 token 的方法中也使用相同的方式处理密钥。检查您的 `JwtUtil` 或类似的工具类中生成 token 的方法：

`````java:demo1/src/main/java/com/advisor/util/JwtUtil.java
public static String generateToken(String userId, String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
    
    return Jwts.builder()
            .setSubject(userId)
            .claim("username", username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes()) // 使用字节数组
            .compact();
}
`````


### 最佳实践

为了避免这类问题，建议使用更现代的 JJWT API（如果您使用的是较新版本的 JJWT）：

`````java:demo1/src/main/java/com/advisor/service/impl/UserServiceImpl.java
@Override
public User getUserByToken(String token) {
    try {
        // 使用更现代的 JJWT API
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        String userId = claims.getSubject();
        User user = userMapper.selectById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    } catch (Exception e) {
        System.out.println("解析Token时发生异常: " + e.getMessage());
        return null;
    }
}
`````


这需要添加 `io.jsonwebtoken:jjwt-api:0.11.2` 及相关依赖。

总结：问题不在于您的配置值本身，而是在于 JJWT 库处理密钥的方式。通过使用字节数组而不是字符串作为签名密钥，您可以解决这个问题。






# 新旧 JJWT API 的区别与迁移指南

如果您想使用新的 JJWT API，需要了解以下几个主要区别和需要更改的内容：

## 1. 依赖变化

### 旧版 JJWT (0.9.x 及以前)
`````xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.9.1</version>
</dependency>
`````

### 新版 JJWT (0.10.x 及以后)
`````xml:demo1/pom.xml
<!-- API -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<!-- 实现 -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<!-- JSON 序列化 -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
`````

## 2. API 变化

### 生成 Token

#### 旧版 API
`````java
String token = Jwts.builder()
    .setSubject(userId)
    .setIssuedAt(new Date())
    .setExpiration(expiryDate)
    .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
    .compact();
`````

#### 新版 API
`````java
String token = Jwts.builder()
    .setSubject(userId)
    .setIssuedAt(new Date())
    .setExpiration(expiryDate)
    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
    .compact();
`````

### 解析 Token

#### 旧版 API
`````java
Claims claims = Jwts.parser()
    .setSigningKey(jwtSecret.getBytes())
    .parseClaimsJws(token)
    .getBody();
`````

#### 新版 API
`````java
Claims claims = Jwts.parserBuilder()
    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
    .build()
    .parseClaimsJws(token)
    .getBody();
`````

## 3. 密钥处理的变化

### 旧版 API
- 直接使用字符串或字节数组作为密钥
- 可能导致安全问题和兼容性问题

### 新版 API
- 使用 `Keys` 类创建密钥
- 更安全、更标准化的密钥处理
- 支持多种密钥类型（HMAC、RSA、EC）

## 4. 需要修改的文件

### 1. JwtUtil.java (生成 Token 的工具类)

`````java:demo1/src/main/java/com/advisor/util/JwtUtil.java
// 导入新的包
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;

// 生成 Token 方法
public static String generateToken(String userId, String username) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
    
    return Jwts.builder()
            .setSubject(userId)
            .claim("username", username)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS512)
            .compact();
}
`````

### 2. UserServiceImpl.java (验证 Token 的方法)

`````java:demo1/src/main/java/com/advisor/service/impl/UserServiceImpl.java
// 导入新的包
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;

@Override
public User getUserByToken(String token) {
    try {
        System.out.println("开始解析token: " + token.substring(0, Math.min(token.length(), 20)) + "...");
        
        // 使用新的 API
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        System.out.println("Token解析成功，claims: " + claims);
        
        String userId = claims.getSubject();
        System.out.println("从token中获取的userId: " + userId);
        
        User user = userMapper.selectById(userId);
        System.out.println("根据userId查询结果: " + (user == null ? "null" : user.getUsername()));
        
        if (user != null) {
            user.setPassword(null); // 不返回密码
        }
        return user;
    } catch (Exception e) {
        System.out.println("解析Token时发生异常: " + e.getClass().getName() + ": " + e.getMessage());
        e.printStackTrace();
        return null;
    }
}
`````

### 3. JwtAuthenticationFilter.java (如果有的话)

`````java:demo1/src/main/java/com/advisor/config/JwtAuthenticationFilter.java
// 导入新的包
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;

// 验证 Token 的方法
private Claims validateToken(String token) {
    try {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    } catch (Exception e) {
        return null;
    }
}
`````

## 5. 新版 API 的优势

1. **更好的安全性**：
   - 使用专门的密钥生成方法
   - 更严格的密钥验证

2. **更清晰的 API**：
   - 构建器模式更加明确
   - 方法链更加直观

3. **更好的异常处理**：
   - 异常类型更加具体
   - 更容易诊断问题

4. **更好的扩展性**：
   - 支持更多的算法和密钥类型
   - 更容易配置和自定义

## 6. 迁移建议

1. **同时更新所有相关代码**：
   - 确保生成和验证 Token 的代码使用相同的 API
   - 避免混用新旧 API

2. **测试验证**：
   - 迁移后全面测试 Token 生成和验证
   - 确保现有 Token 仍然有效

3. **逐步迁移**：
   - 如果系统较大，可以先更新依赖
   - 然后逐步更新各个组件的代码

通过使用新版 JJWT API，您可以获得更好的安全性和可维护性，同时解决当前遇到的密钥处理问题。
