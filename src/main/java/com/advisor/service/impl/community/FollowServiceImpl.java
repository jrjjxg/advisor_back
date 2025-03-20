package com.advisor.service.impl.community;

import com.advisor.entity.base.User;
import com.advisor.entity.community.FollowRelation;
import com.advisor.mapper.base.UserMapper;
import com.advisor.mapper.community.FollowRelationMapper;
import com.advisor.service.community.FollowService;
import com.advisor.service.community.NotificationService;
import com.advisor.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 关注服务实现类
 */
@Slf4j
@Service
public class FollowServiceImpl extends ServiceImpl<FollowRelationMapper, FollowRelation> implements FollowService {
    
    @Autowired
    private FollowRelationMapper followRelationMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    @Transactional
    public boolean toggleFollow(String targetUserId, String userId) {
        // 不能关注自己
        if (targetUserId.equals(userId)) {
            throw new RuntimeException("不能关注自己");
        }
        
        // 查询目标用户是否存在
        User targetUser = userMapper.selectById(targetUserId);
        if (targetUser == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 查询是否已关注
        LambdaQueryWrapper<FollowRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FollowRelation::getUserId, userId)
                .eq(FollowRelation::getFollowUserId, targetUserId);
        
        FollowRelation relation = followRelationMapper.selectOne(queryWrapper);
        
        if (relation != null) {
            // 已关注，取消关注
            followRelationMapper.deleteById(relation.getId());
            
            // 更新用户关注数和粉丝数
            User user = userMapper.selectById(userId);
            user.setFollowCount(user.getFollowCount() - 1);
            userMapper.updateById(user);
            
            targetUser.setFansCount(targetUser.getFansCount() - 1);
            userMapper.updateById(targetUser);
            
            return false;
        } else {
            // 未关注，添加关注
            relation = new FollowRelation();
            relation.setUserId(userId);
            relation.setFollowUserId(targetUserId);
            relation.setCreateTime(LocalDateTime.now());
            followRelationMapper.insert(relation);
            
            // 更新用户关注数和粉丝数
            User user = userMapper.selectById(userId);
            user.setFollowCount(user.getFollowCount() + 1);
            userMapper.updateById(user);
            
            targetUser.setFansCount(targetUser.getFansCount() + 1);
            userMapper.updateById(targetUser);
            
            // 发送通知
            notificationService.createNotification(
                    targetUserId,
                    userId,
                    "follow",
                    userId,
                    "关注了你"
            );
            
            return true;
        }
    }
    
    @Override
    public boolean checkFollowed(String targetUserId, String userId) {
        LambdaQueryWrapper<FollowRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FollowRelation::getUserId, userId)
                .eq(FollowRelation::getFollowUserId, targetUserId);
        
        return followRelationMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public Page<UserVO> getFollowingList(String userId, Integer pageNum, Integer pageSize) {
        // 查询关注关系
        LambdaQueryWrapper<FollowRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FollowRelation::getUserId, userId)
                .orderByDesc(FollowRelation::getCreateTime);
        
        Page<FollowRelation> page = new Page<>(pageNum, pageSize);
        Page<FollowRelation> relationPage = followRelationMapper.selectPage(page, queryWrapper);
        
        // 转换为UserVO
        Page<UserVO> voPage = new Page<>(relationPage.getCurrent(), relationPage.getSize(), relationPage.getTotal());
        
        List<UserVO> voList = relationPage.getRecords().stream()
                .map(relation -> {
                    User user = userMapper.selectById(relation.getFollowUserId());
                    UserVO vo = new UserVO();
                    if (user != null) {
                        BeanUtils.copyProperties(user, vo);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
    
    @Override
    public Page<UserVO> getFollowerList(String userId, Integer pageNum, Integer pageSize) {
        // 查询粉丝关系
        LambdaQueryWrapper<FollowRelation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FollowRelation::getFollowUserId, userId)
                .orderByDesc(FollowRelation::getCreateTime);
        
        Page<FollowRelation> page = new Page<>(pageNum, pageSize);
        Page<FollowRelation> relationPage = followRelationMapper.selectPage(page, queryWrapper);
        
        // 转换为UserVO
        Page<UserVO> voPage = new Page<>(relationPage.getCurrent(), relationPage.getSize(), relationPage.getTotal());
        
        List<UserVO> voList = relationPage.getRecords().stream()
                .map(relation -> {
                    User user = userMapper.selectById(relation.getUserId());
                    UserVO vo = new UserVO();
                    if (user != null) {
                        BeanUtils.copyProperties(user, vo);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
}