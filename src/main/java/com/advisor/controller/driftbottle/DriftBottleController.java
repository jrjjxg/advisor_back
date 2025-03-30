package com.advisor.controller.driftbottle;

import com.advisor.common.Result;
import com.advisor.dto.driftbottle.BottleCreateRequest;
import com.advisor.dto.driftbottle.BottleReplyRequest;
import com.advisor.service.driftbottle.DriftBottleService;
import com.advisor.util.UserUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/driftbottle")
public class DriftBottleController {
    
    @Autowired
    private DriftBottleService driftBottleService;
    
    @PostMapping("/throw")
    public Result throwBottle(@RequestBody BottleCreateRequest request) {
        String userId = UserUtil.getCurrentUserId();
        String bottleId = driftBottleService.createBottle(request, userId);
        return Result.success(bottleId);
    }
    
    @GetMapping("/pick")
    public Result pickBottle() {
        String userId = UserUtil.getCurrentUserId();
        return Result.success(driftBottleService.pickRandomBottle(userId));
    }
    
    @PostMapping("/reply")
    public Result replyBottle(@RequestBody BottleReplyRequest request) {
        String userId = UserUtil.getCurrentUserId();
        driftBottleService.replyBottle(request, userId);
        return Result.success();
    }
    
    @GetMapping("/detail/{bottleId}")
    public Result getBottleDetail(@PathVariable String bottleId) {
        String userId = UserUtil.getCurrentUserId();
        return Result.success(driftBottleService.getBottleDetail(bottleId, userId));
    }
    
    @GetMapping("/my/thrown")
    public Result getMyThrownBottles(@RequestParam(defaultValue = "1") int pageNum, 
                                    @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserUtil.getCurrentUserId();
        return Result.success(driftBottleService.getMyThrownBottles(userId, pageNum, pageSize));
    }
    
    @GetMapping("/my/picked")
    public Result getMyPickedBottles(@RequestParam(defaultValue = "1") int pageNum, 
                                    @RequestParam(defaultValue = "10") int pageSize) {
        String userId = UserUtil.getCurrentUserId();
        return Result.success(driftBottleService.getMyPickedBottles(userId, pageNum, pageSize));
    }
} 