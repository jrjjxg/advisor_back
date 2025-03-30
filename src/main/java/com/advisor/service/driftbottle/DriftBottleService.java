package com.advisor.service.driftbottle;

import com.advisor.dto.driftbottle.BottleAuditRequest;
import com.advisor.dto.driftbottle.BottleCreateRequest;
import com.advisor.dto.driftbottle.BottleReplyRequest;
import com.advisor.vo.driftbottle.DriftBottleVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface DriftBottleService {
    
    // 创建漂流瓶
    String createBottle(BottleCreateRequest request, String userId);
    
    // 随机捞取一个漂流瓶
    DriftBottleVO pickRandomBottle(String userId);
    
    // 回复漂流瓶
    void replyBottle(BottleReplyRequest request, String userId);
    
    // 获取漂流瓶详情
    DriftBottleVO getBottleDetail(String bottleId, String userId);
    
    // 获取我投掷的漂流瓶列表
    Page<DriftBottleVO> getMyThrownBottles(String userId, int pageNum, int pageSize);
    
    // 获取我捞到的漂流瓶列表
    Page<DriftBottleVO> getMyPickedBottles(String userId, int pageNum, int pageSize);
    
    // 获取待审核的漂流瓶列表
    Page<DriftBottleVO> getPendingAuditBottles(int pageNum, int pageSize);
    
    // 审核漂流瓶
    void auditBottle(BottleAuditRequest request, String auditUserId);
} 