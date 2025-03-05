package com.advisor.controller;

import com.advisor.common.Result;
import com.advisor.entity.PsychologicalProfile;
import com.advisor.mapper.PsychologicalProfileMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private PsychologicalProfileMapper profileMapper;
    
    /**
     * 获取用户心理档案
     */
    @GetMapping
    public Result<PsychologicalProfile> getUserProfile(@RequestHeader("userId") String userId) {
        PsychologicalProfile profile = profileMapper.selectOne(
            new LambdaQueryWrapper<PsychologicalProfile>()
                .eq(PsychologicalProfile::getUserId, userId)
        );
        
        if (profile == null) {
            return Result.fail("用户档案不存在，请先完成心理测试");
        }
        
        return Result.success(profile);
    }
}