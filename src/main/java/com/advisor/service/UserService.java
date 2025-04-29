package com.advisor.service;

import com.advisor.dto.UserQueryDTO;
import com.advisor.entity.base.User;
import com.advisor.vo.UserManagementVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    boolean sendVerificationCode(String email);
    void register(String username, String password, String email, String code);
    User login(String username, String password);
    User login(String username, String password, String ipAddress);
    User getUserByToken(String token);
    User getUserByUsername(String username);
    User getUserById(String userId);
    void updateUser(User user);
    boolean isUsernameAvailable(String username);
    // 新增的密码更新方法
    void updatePassword(String username, String oldPassword, String newPassword);

    // --- 用户管理相关接口 ---

    /**
     * 分页查询用户列表（管理员）
     *
     * @param queryDTO 查询参数
     * @return 分页用户视图对象
     */
    IPage<UserManagementVO> listUsersForAdmin(UserQueryDTO queryDTO);

    /**
     * 更新用户状态（管理员）
     *
     * @param userId 用户ID
     * @param status 状态 (0-禁用, 1-正常)
     */
    void updateUserStatus(String userId, Integer status);

    /**
     * 删除用户（管理员）
     *
     * @param userId 用户ID
     */
    void deleteUser(String userId);

    boolean checkUsernameExists(String username);

    boolean checkEmailExists(String email);
}