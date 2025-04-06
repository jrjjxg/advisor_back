package com.advisor.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HTML图片提取工具类
 */
public class HtmlImageExtractor {
    
    private static final Pattern IMG_PATTERN = Pattern.compile("<img\\s+[^>]*src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
    
    /**
     * 从HTML内容中提取所有图片URL
     * @param htmlContent HTML内容
     * @return 图片URL列表
     */
    public static List<String> extractImageUrls(String htmlContent) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> imageUrls = new ArrayList<>();
        Matcher matcher = IMG_PATTERN.matcher(htmlContent);
        
        while (matcher.find()) {
            String imageUrl = matcher.group(1);
            // 只添加有效的远程URL，排除data:image和blob:链接
            if (imageUrl != null && !imageUrl.isEmpty() 
                    && !imageUrl.startsWith("data:") 
                    && !imageUrl.startsWith("blob:")) {
                imageUrls.add(imageUrl);
            }
        }
        
        return imageUrls;
    }
    
    /**
     * 检查HTML内容中是否包含图片
     * @param htmlContent HTML内容
     * @return 是否包含图片
     */
    public static boolean containsImage(String htmlContent) {
        if (htmlContent == null || htmlContent.isEmpty()) {
            return false;
        }
        
        return IMG_PATTERN.matcher(htmlContent).find();
    }
} 