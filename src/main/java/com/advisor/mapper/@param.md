在 MyBatis Mapper 接口的方法参数中使用 `@Param` 注解的主要原因是 **为了在 XML 映射文件或注解 SQL 语句中能够引用这些参数**。

让我详细解释一下为什么需要 `@Param` 以及它的作用：

**1.  参数引用方式：**

在 MyBatis 中，当你需要在 SQL 语句中引用 Mapper 接口方法的参数时，你需要使用特定的占位符。  MyBatis 提供了两种主要的占位符：

*   **`#{}` (参数占位符):**  MyBatis 会将 `#{}` 占位符替换为参数值，并进行预编译处理，可以防止 SQL 注入。
*   **`${}` (字符串替换占位符):**  MyBatis 会将 `${}` 占位符直接替换为参数值，**不进行预编译**，存在 SQL 注入风险，通常不建议使用，除非有特殊需求（例如动态表名或排序字段）。

**2.  默认参数名：**

*   **单个参数:**  如果 Mapper 接口方法只有一个参数，在 XML 映射文件或注解 SQL 语句中，你可以直接使用 `#{param1}` 或 `#{_parameter}` 来引用这个参数，`param1` 是默认的参数名，`_parameter` 是 MyBatis 提供的通用参数名。

    例如，如果你的 `updateFollowCount` 方法只有一个参数 `userId`：

    ```java
    int updateFollowCount(String userId);
    ```

    你可以这样写 SQL (使用注解或 XML)：

    ```sql
    UPDATE user SET follow_count = follow_count + 1 WHERE id = #{param1}
    -- 或者
    UPDATE user SET follow_count = follow_count + 1 WHERE id = #{_parameter}
    ```

*   **多个参数:**  **当 Mapper 接口方法有多个参数时，默认情况下，MyBatis 并不会自动为这些参数生成有意义的名字。**  在没有 `@Param` 注解的情况下，你只能使用 `#{param1}`, `#{param2}`, `#{param3}` ...  或者 `#{arg0}`, `#{arg1}`, `#{arg2}` ...  这样的默认参数名来引用参数。

    例如，对于你的 `updateFollowCount` 方法：

    ```java
    int updateFollowCount(String userId, int count);
    ```

    在 **没有 `@Param` 注解** 的情况下，你只能这样写 SQL：

    ```sql
    UPDATE user SET follow_count = follow_count + #{param2} WHERE id = #{param1}
    -- 或者
    UPDATE user SET follow_count = follow_count + #{arg1} WHERE id = #{arg0}
    ```

    或者使用索引 `#{0}`, `#{1}`:

    ```sql
    UPDATE user SET follow_count = follow_count + #{1} WHERE id = #{0}
    ```

    **问题：**  使用 `param1`, `param2`, `arg0`, `arg1` 或索引 `#{0}`, `#{1}` 这样的默认参数名 **可读性非常差**，难以理解哪个参数对应哪个值，尤其当参数数量增多时，维护和理解 SQL 语句会变得非常困难。

**3.  `@Param` 注解的作用：**

`@Param` 注解就是为了解决 **多个参数情况下参数命名可读性差** 的问题而引入的。  **`@Param("参数名")` 注解可以显式地为方法参数指定一个名字**，这样你就可以在 SQL 语句中使用这个名字来引用参数，提高 SQL 语句的可读性和可维护性。

对于你的 `updateFollowCount` 方法，使用了 `@Param` 注解：

```java
int updateFollowCount(@Param("userId") String userId, @Param("count") int count);
```

现在，你就可以在 SQL 语句中使用 `@Param` 注解中指定的参数名 `userId` 和 `count` 来引用参数：

```sql
UPDATE user SET follow_count = follow_count + #{count} WHERE id = #{userId}
```

这样，SQL 语句就变得非常清晰易懂，一眼就能看出 `#{userId}` 对应 `userId` 参数，`#{count}` 对应 `count` 参数。

**总结：**

*   **当 Mapper 接口方法只有一个参数时，`@Param` 注解不是必须的**，可以使用默认参数名 `param1` 或 `_parameter`。
*   **当 Mapper 接口方法有多个参数时，为了提高 SQL 语句的可读性和可维护性，强烈建议使用 `@Param` 注解来显式地为每个参数指定名字。**  这样可以避免使用默认的 `paramN`, `argN` 或索引参数名，使 SQL 语句更易于理解和维护。

**最佳实践：**

即使是单个参数的方法，为了保持代码风格的一致性和未来的可扩展性（例如，将来可能需要添加更多参数），也建议在所有 Mapper 接口方法的参数上都使用 `@Param` 注解，显式地指定参数名。  这是一种良好的编程习惯，可以提高代码的整体质量。
