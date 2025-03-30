package com.advisor.controller.admin;

import com.advisor.common.Result;
import com.advisor.dto.driftbottle.BottleAuditRequest;
import com.advisor.service.driftbottle.DriftBottleService;
import com.advisor.common.Result;
import com.advisor.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/driftbottle")
public class DriftBottleAdminController {
    
    @Autowired
    private DriftBottleService driftBottleService;
    
    @GetMapping("/pending")
    public Result getPendingAuditBottles(@RequestParam(defaultValue = "1") int pageNum, 
                                        @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(driftBottleService.getPendingAuditBottles(pageNum, pageSize));
    }
    
    @PostMapping("/audit")
    public Result auditBottle(@RequestBody BottleAuditRequest request) {
        String userId = UserUtil.getCurrentUserId();
        driftBottleService.auditBottle(request, userId);
        return Result.success();
    }
} 