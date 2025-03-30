package com.advisor.service.driftbottle.impl;

import com.advisor.common.ResultCode;
import com.advisor.dto.driftbottle.BottleAuditRequest;
import com.advisor.dto.driftbottle.BottleCreateRequest;
import com.advisor.dto.driftbottle.BottleReplyRequest;
import com.advisor.entity.base.User;
import com.advisor.entity.driftbottle.DriftBottle;
import com.advisor.entity.driftbottle.DriftBottleReply;
import com.advisor.exception.BusinessException;
import com.advisor.mapper.base.UserMapper;
import com.advisor.mapper.driftbottle.DriftBottleMapper;
import com.advisor.mapper.driftbottle.DriftBottleReplyMapper;
import com.advisor.service.driftbottle.DriftBottleService;
import com.advisor.vo.driftbottle.DriftBottleReplyVO;
import com.advisor.vo.driftbottle.DriftBottleVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriftBottleServiceImpl implements DriftBottleService {

    @Autowired
    private DriftBottleMapper driftBottleMapper;
    
    @Autowired
    private DriftBottleReplyMapper driftBottleReplyMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    @Transactional
    public String createBottle(BottleCreateRequest request, String userId) {
        if (request == null || !StringUtils.hasText(request.getContent())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "内容不能为空");
        }
        
        // 创建漂流瓶
        DriftBottle bottle = new DriftBottle();
        bottle.setUserId(userId);
        bottle.setContent(request.getContent());
        
        // 处理图片
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            bottle.setImages(String.join(",", request.getImages()));
        }
        
        // 匿名设置
        bottle.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : 0);
        
        // 初始状态为待审核
        bottle.setStatus(0);
        bottle.setCreateTime(LocalDateTime.now());
        bottle.setUpdateTime(LocalDateTime.now());
        
        driftBottleMapper.insert(bottle);
        
        return bottle.getId();
    }
    
    @Override
    @Transactional
    public DriftBottleVO pickRandomBottle(String userId) {
        // 随机捞取一个漂流瓶
        DriftBottle bottle = driftBottleMapper.randomPickOneBottle();
        
        if (bottle == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "暂时没有可捞取的漂流瓶");
        }
        
        // 更新漂流瓶状态为已捞取
        driftBottleMapper.updateBottlePickStatus(bottle.getId(), userId);
        
        // 转换为VO
        return convertToVO(bottle, userId);
    }
    
    @Override
    @Transactional
    public void replyBottle(BottleReplyRequest request, String userId) {
        if (request == null || !StringUtils.hasText(request.getBottleId()) || !StringUtils.hasText(request.getContent())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "参数错误");
        }
        
        // 查询漂流瓶是否存在
        DriftBottle bottle = driftBottleMapper.selectById(request.getBottleId());
        if (bottle == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "漂流瓶不存在");
        }
        
        // 只能回复已发布或已捞取的漂流瓶
        if (bottle.getStatus() != 1 && bottle.getStatus() != 2) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "该漂流瓶当前状态不允许回复");
        }
        
        // 创建回复
        DriftBottleReply reply = new DriftBottleReply();
        reply.setBottleId(request.getBottleId());
        reply.setUserId(userId);
        reply.setContent(request.getContent());
        reply.setStatus(1);
        reply.setCreateTime(LocalDateTime.now());
        
        driftBottleReplyMapper.insert(reply);
    }
    
    @Override
    public DriftBottleVO getBottleDetail(String bottleId, String userId) {
        if (!StringUtils.hasText(bottleId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "漂流瓶ID不能为空");
        }
        
        // 查询漂流瓶
        DriftBottle bottle = driftBottleMapper.selectById(bottleId);
        if (bottle == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "漂流瓶不存在");
        }
        
        // 检查是否有权限查看
        if (bottle.getStatus() == 0 || bottle.getStatus() == 3) {
            // 待审核或已拒绝的漂流瓶，只有发布者和管理员可以查看
            if (!bottle.getUserId().equals(userId)) {
                // 这里需要检查用户是否是管理员，简化处理先不实现
                throw new BusinessException(ResultCode.FORBIDDEN, "无权查看该漂流瓶");
            }
        }
        
        // 转换为VO并返回
        return convertToVO(bottle, userId);
    }
    
    @Override
    public Page<DriftBottleVO> getMyThrownBottles(String userId, int pageNum, int pageSize) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "用户ID不能为空");
        }
        
        // 分页查询
        Page<DriftBottle> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DriftBottle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DriftBottle::getUserId, userId)
               .orderByDesc(DriftBottle::getCreateTime);
        
        Page<DriftBottle> bottlePage = driftBottleMapper.selectPage(page, wrapper);
        
        // 转换结果
        Page<DriftBottleVO> voPage = new Page<>();
        BeanUtils.copyProperties(bottlePage, voPage, "records");
        
        List<DriftBottleVO> voList = bottlePage.getRecords().stream()
                .map(bottle -> convertToVO(bottle, userId))
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    public Page<DriftBottleVO> getMyPickedBottles(String userId, int pageNum, int pageSize) {
        if (!StringUtils.hasText(userId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "用户ID不能为空");
        }
        
        // 分页查询
        Page<DriftBottle> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DriftBottle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DriftBottle::getPickUserId, userId)
               .eq(DriftBottle::getStatus, 2)  // 只查询已被捞取的
               .orderByDesc(DriftBottle::getPickTime);
        
        Page<DriftBottle> bottlePage = driftBottleMapper.selectPage(page, wrapper);
        
        // 转换结果
        Page<DriftBottleVO> voPage = new Page<>();
        BeanUtils.copyProperties(bottlePage, voPage, "records");
        
        List<DriftBottleVO> voList = bottlePage.getRecords().stream()
                .map(bottle -> convertToVO(bottle, userId))
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    public Page<DriftBottleVO> getPendingAuditBottles(int pageNum, int pageSize) {
        // 分页查询待审核的漂流瓶
        Page<DriftBottle> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DriftBottle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DriftBottle::getStatus, 0)  // 待审核
               .orderByAsc(DriftBottle::getCreateTime);  // 先进先出
        
        Page<DriftBottle> bottlePage = driftBottleMapper.selectPage(page, wrapper);
        
        // 转换结果
        Page<DriftBottleVO> voPage = new Page<>();
        BeanUtils.copyProperties(bottlePage, voPage, "records");
        
        List<DriftBottleVO> voList = bottlePage.getRecords().stream()
                .map(bottle -> convertToVO(bottle, null))
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    @Transactional
    public void auditBottle(BottleAuditRequest request, String auditUserId) {
        if (request == null || !StringUtils.hasText(request.getBottleId()) || request.getStatus() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "参数错误");
        }
        
        // 查询漂流瓶
        DriftBottle bottle = driftBottleMapper.selectById(request.getBottleId());
        if (bottle == null) {
            throw new BusinessException(ResultCode.DATA_NOT_FOUND, "漂流瓶不存在");
        }
        
        // 只能审核待审核状态的漂流瓶
        if (bottle.getStatus() != 0) {
            throw new BusinessException(ResultCode.BUSINESS_ERROR, "该漂流瓶当前状态不允许审核");
        }
        
        // 更新审核状态
        bottle.setStatus(request.getStatus());
        bottle.setAuditUserId(auditUserId);
        bottle.setAuditTime(LocalDateTime.now());
        
        // 如果是拒绝，需要记录拒绝原因
        if (request.getStatus() == 3 && StringUtils.hasText(request.getReason())) {
            bottle.setAuditReason(request.getReason());
        }
        
        bottle.setUpdateTime(LocalDateTime.now());
        
        driftBottleMapper.updateById(bottle);
    }
    
    // 转换实体到VO
    private DriftBottleVO convertToVO(DriftBottle bottle, String currentUserId) {
        if (bottle == null) {
            return null;
        }
        
        DriftBottleVO vo = new DriftBottleVO();
        BeanUtils.copyProperties(bottle, vo);
        
        // 处理图片
        if (StringUtils.hasText(bottle.getImages())) {
            vo.setImages(List.of(bottle.getImages().split(",")));
        } else {
            vo.setImages(new ArrayList<>());
        }
        
        // 设置作者信息
        User author = userMapper.selectById(bottle.getUserId());
        if (author != null) {
            DriftBottleVO.UserInfo userInfo = new DriftBottleVO.UserInfo();
            userInfo.setId(author.getId());
            userInfo.setNickname(author.getNickname());
            userInfo.setAvatar(author.getAvatar());
            vo.setAuthor(userInfo);
        }
        
        // 获取回复列表
        LambdaQueryWrapper<DriftBottleReply> replyWrapper = new LambdaQueryWrapper<>();
        replyWrapper.eq(DriftBottleReply::getBottleId, bottle.getId())
                   .eq(DriftBottleReply::getStatus, 1)
                   .orderByAsc(DriftBottleReply::getCreateTime);
        
        List<DriftBottleReply> replies = driftBottleReplyMapper.selectList(replyWrapper);
        
        if (replies != null && !replies.isEmpty()) {
            List<DriftBottleReplyVO> replyVOs = replies.stream().map(reply -> {
                DriftBottleReplyVO replyVO = new DriftBottleReplyVO();
                BeanUtils.copyProperties(reply, replyVO);
                
                // 设置回复作者信息
                User replyAuthor = userMapper.selectById(reply.getUserId());
                if (replyAuthor != null) {
                    DriftBottleVO.UserInfo userInfo = new DriftBottleVO.UserInfo();
                    userInfo.setId(replyAuthor.getId());
                    userInfo.setNickname(replyAuthor.getNickname());
                    userInfo.setAvatar(replyAuthor.getAvatar());
                    replyVO.setAuthor(userInfo);
                }
                
                return replyVO;
            }).collect(Collectors.toList());
            
            vo.setReplies(replyVOs);
        }
        
        return vo;
    }
} 