package com.advisor.controller.admin;

import com.advisor.common.Result;
import com.advisor.dto.UpdateUserStatusDTO;
import com.advisor.dto.UserQueryDTO;
import com.advisor.service.UserService;
import com.advisor.vo.UserManagementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/users")
// @PreAuthorize("hasRole('ADMIN')") // 稍后添加权限控制
public class UserManagementController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表（分页）
     *
     * @param queryDTO 查询参数
     * @return 分页用户数据
     */
    @GetMapping
    public Result<IPage<UserManagementVO>> listUsers(@Validated UserQueryDTO queryDTO) {
        // 调用 Service 层方法获取分页数据
        IPage<UserManagementVO> userPage = userService.listUsersForAdmin(queryDTO);
        return Result.success(userPage);
    }

    /**
     * 更新用户状态
     *
     * @param userId  用户ID
     * @param statusDTO 更新状态的DTO
     * @return 操作结果
     */
    @PatchMapping("/{userId}/status")
    public Result<?> updateUserStatus(@PathVariable String userId, @Valid @RequestBody UpdateUserStatusDTO statusDTO) {
        userService.updateUserStatus(userId, statusDTO.getStatus());
        return Result.success(null);
    }

    /**
     * 删除用户
     *
     * @param userId 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{userId}")
    public Result<?> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return Result.success(null);
    }
} 