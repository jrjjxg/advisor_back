package com.advisor.service.impl.community;

import com.advisor.dto.PostCreateRequest;
import com.advisor.dto.PostQueryRequest;
import com.advisor.entity.base.User;
import com.advisor.entity.community.Post;
import com.advisor.entity.community.PostTag;
import com.advisor.entity.mood.MoodRecord;
import com.advisor.entity.test.TestResult;
import com.advisor.mapper.base.UserMapper;
import com.advisor.mapper.community.LikeRecordMapper;
import com.advisor.mapper.community.PostMapper;
import com.advisor.mapper.community.PostTagMapper;
import com.advisor.mapper.mood.MoodRecordMapper;
import com.advisor.mapper.test.TestResultMapper;
import com.advisor.service.community.PostService;
import com.advisor.vo.community.PostVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 帖子服务实现类
 */
@Slf4j
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private PostTagMapper postTagMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private LikeRecordMapper likeRecordMapper;
    
    @Autowired
    private MoodRecordMapper moodRecordMapper;
    
    @Autowired
    private TestResultMapper testResultMapper;
    
    @Override
    @Transactional
    public String createPost(PostCreateRequest request, String userId) {
        log.info("创建帖子请求, userId={}, content={}, images={}", userId, request.getContent(), request.getImages());
        
        // 创建帖子
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(request.getContent());
        post.setLocation(request.getLocation());
        post.setMoodRecordId(request.getMoodRecordId());
        post.setTestResultId(request.getTestResultId());
        post.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : 0);
        post.setViewCount(0);
        post.setLikeCount(0);
        post.setCommentCount(0);
        post.setStatus(1);
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        
        // 处理图片 - 确保存储的是完整的七牛云URL
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            log.info("帖子包含{}张图片", request.getImages().size());
            // 验证所有图片URL是否为七牛云URL
            List<String> validImageUrls = request.getImages().stream()
                    .filter(url -> url != null && !url.isEmpty())
                    .map(url -> {
                        // 如果URL已经是完整的七牛云URL，则直接使用
                        if (url.startsWith("http://") || url.startsWith("https://")) {
                            log.info("图片URL已是完整URL: {}", url);
                            return url;
                        } 
                        // 否则，可能是相对路径，需要转换为完整URL
                        log.warn("图片URL不是完整URL: {}", url);
                        return url;
                    })
                    .collect(Collectors.toList());
            
            String imageStr = String.join(",", validImageUrls);
            log.info("最终保存的图片URL字符串: {}", imageStr);
            post.setImages(imageStr);
        } else {
            log.info("帖子不包含图片");
        }
        
        // 保存帖子
        postMapper.insert(post);
        
        // 处理标签
        if (request.getTags() != null && !request.getTags().isEmpty()) {
            for (String tag : request.getTags()) {
                PostTag postTag = new PostTag();
                postTag.setPostId(post.getId());
                postTag.setTagName(tag);
                postTagMapper.insert(postTag);
            }
        }
        
        // 更新用户发帖数
        userMapper.updatePostCount(userId, 1);
        
        return post.getId();
    }
    
    @Override
    public PostVO getPostDetail(String postId, String userId) {
        // 查询帖子
        Post post = postMapper.selectById(postId);
        if (post == null || post.getStatus() == 0) {
            return null;
        }
        
        // 增加浏览数
        incrementViewCount(postId);
        
        return convertToPostVO(post, userId);
    }
    
    @Override
    public Page<PostVO> getPostList(PostQueryRequest request, String userId) {
        // 构建查询条件
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getStatus, 1);
        
        // 关键词搜索
        if (StringUtils.isNotBlank(request.getKeyword())) {
            queryWrapper.like(Post::getContent, request.getKeyword());
        }
        
        // 排序方式
        if ("hot".equals(request.getOrderBy())) {
            queryWrapper.orderByDesc(Post::getLikeCount, Post::getCommentCount, Post::getCreateTime);
        } else {
            queryWrapper.orderByDesc(Post::getCreateTime);
        }
        
        // 分页查询
        Page<Post> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<Post> postPage = postMapper.selectPage(page, queryWrapper);
        
        // 转换为VO
        return convertToPostVOPage(postPage, userId);
    }
    
    @Override
    public Page<PostVO> getUserPosts(String targetUserId, String userId, Integer pageNum, Integer pageSize) {
        // 构建查询条件
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Post::getUserId, targetUserId)
                .eq(Post::getStatus, 1)
                .orderByDesc(Post::getCreateTime);
        
        // 分页查询
        Page<Post> page = new Page<>(pageNum, pageSize);
        Page<Post> postPage = postMapper.selectPage(page, queryWrapper);
        
        // 转换为VO
        return convertToPostVOPage(postPage, userId);
    }
    
    @Override
    public Page<PostVO> getUserLikedPosts(String userId, Integer pageNum, Integer pageSize) {
        // 查询用户点赞的帖子ID列表
        LambdaQueryWrapper<com.advisor.entity.community.LikeRecord> likeWrapper = new LambdaQueryWrapper<>();
        likeWrapper.eq(com.advisor.entity.community.LikeRecord::getUserId, userId)
                .eq(com.advisor.entity.community.LikeRecord::getType, 1); // 1-帖子
        
        List<com.advisor.entity.community.LikeRecord> likeRecords = likeRecordMapper.selectList(likeWrapper);
        List<String> postIds = likeRecords.stream()
                .map(com.advisor.entity.community.LikeRecord::getTargetId)
                .collect(Collectors.toList());
        
        if (postIds.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }
        
        // 查询帖子
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Post::getId, postIds)
                .eq(Post::getStatus, 1)
                .orderByDesc(Post::getCreateTime);
        
        // 分页查询
        Page<Post> page = new Page<>(pageNum, pageSize);
        Page<Post> postPage = postMapper.selectPage(page, queryWrapper);
        
        // 转换为VO
        return convertToPostVOPage(postPage, userId);
    }
    
    @Override
    @Transactional
    public void deletePost(String postId, String userId) {
        // 查询帖子
        Post post = postMapper.selectById(postId);
        if (post == null || !post.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该帖子");
        }
        
        // 逻辑删除帖子
        post.setStatus(0);
        postMapper.updateById(post);
        
        // 更新用户发帖数
        userMapper.updatePostCount(userId, -1);
    }
    
    @Override
    @Transactional
    public String createPostFromMoodRecord(Long moodRecordId, String content, Integer isAnonymous, String userId) {
        // 查询情绪记录是否存在
        MoodRecord moodRecord = moodRecordMapper.selectById(moodRecordId);
        if (moodRecord == null) {
            throw new RuntimeException("情绪记录不存在");
        }
        
        // 检查是否是当前用户的情绪记录
        if (!moodRecord.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作他人的情绪记录");
        }
        
        // 创建帖子请求
        PostCreateRequest request = new PostCreateRequest();
        request.setContent(content);
        
        // 设置情绪标签
        List<String> tags = new ArrayList<>();
        tags.add(moodRecord.getEmotionType()); // 使用emotionType作为情绪名称标签
        
        // 如果有备注，也添加为标签
        if (moodRecord.getNote() != null && !moodRecord.getNote().isEmpty()) {
            tags.add(moodRecord.getNote());
        }
        
        request.setTags(tags);
        request.setMoodRecordId(moodRecordId);
        request.setIsAnonymous(isAnonymous);
        
        // 创建帖子
        return createPost(request, userId);
    }
    
    @Override
    @Transactional
    public String createPostFromTestResult(String testResultId, String content, Integer isAnonymous, String userId) {
        // 查询测试结果是否存在
        TestResult testResult = testResultMapper.selectById(testResultId);
        if (testResult == null) {
            throw new RuntimeException("测试结果不存在");
        }
        
        // 检查是否是当前用户的测试结果
        if (!testResult.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作他人的测试结果");
        }
        
        // 创建帖子请求
        PostCreateRequest request = new PostCreateRequest();
        request.setContent(content);
        
        // 设置测试相关标签
        List<String> tags = new ArrayList<>();
        tags.add(testResult.getTestTypeId()); // 添加测试名称作为标签
        
        request.setTags(tags);
        request.setTestResultId(testResultId);
        request.setIsAnonymous(isAnonymous);
        
        // 创建帖子
        return createPost(request, userId);
    }
    
    @Override
    public void incrementViewCount(String postId) {
        postMapper.incrementViewCount(postId);
    }
    
    /**
     * 将Post实体转换为PostVO
     */
    private PostVO convertToPostVO(Post post, String currentUserId) {
        PostVO postVO = new PostVO();
        BeanUtils.copyProperties(post, postVO);
        
        // 处理图片
        if (StringUtils.isNotBlank(post.getImages())) {
            postVO.setImages(Arrays.asList(post.getImages().split(",")));
        } else {
            postVO.setImages(new ArrayList<>());
        }
        
        // 查询标签
        List<String> tags = postTagMapper.findTagsByPostId(post.getId());
        postVO.setTags(tags);
        
        // 查询用户信息
        if (post.getIsAnonymous() == 0) {
            User user = userMapper.selectById(post.getUserId());
            if (user != null) {
                postVO.setUsername(user.getUsername());
                postVO.setAvatar(user.getAvatar());
            }
        } else {
            postVO.setUsername("匿名用户");
            postVO.setAvatar(null);
        }
        
        // 查询当前用户是否点赞
        if (currentUserId != null) {
            LambdaQueryWrapper<com.advisor.entity.community.LikeRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(com.advisor.entity.community.LikeRecord::getUserId, currentUserId)
                    .eq(com.advisor.entity.community.LikeRecord::getTargetId, post.getId())
                    .eq(com.advisor.entity.community.LikeRecord::getType, 1); // 1-帖子
            
            long count = likeRecordMapper.selectCount(queryWrapper);
            postVO.setIsLiked(count > 0);
        } else {
            postVO.setIsLiked(false);
        }
        
        return postVO;
    }
    
    /**
     * 将Post分页结果转换为PostVO分页结果
     */
    private Page<PostVO> convertToPostVOPage(Page<Post> postPage, String currentUserId) {
        Page<PostVO> voPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        
        List<PostVO> voList = postPage.getRecords().stream()
                .map(post -> convertToPostVO(post, currentUserId))
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
}