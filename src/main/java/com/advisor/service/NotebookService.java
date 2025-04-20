package com.advisor.service;

import com.advisor.dto.NotebookEntryDTO;
import com.advisor.entity.NotebookEntry;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 笔记本服务接口
 */
public interface NotebookService extends IService<NotebookEntry> {

    /**
     * 保存笔记条目
     *
     * @param dto 笔记条目DTO
     * @return 是否保存成功
     */
    boolean saveNotebookEntry(NotebookEntryDTO dto);

    /**
     * 根据用户ID获取笔记条目列表
     *
     * @param userId 用户ID
     * @return 笔记条目列表
     */
    List<NotebookEntryDTO> getNotebookEntriesByUserId(String userId);

    /**
     * 分页获取用户笔记本条目
     *
     * @param userId   用户ID
     * @param page     页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<NotebookEntryDTO> getNotebookEntriesByPage(String userId, Integer page, Integer pageSize);

    /**
     * 获取笔记条目详情
     *
     * @param id     笔记条目ID
     * @param userId 用户ID
     * @return 笔记条目详情
     */
    NotebookEntryDTO getNotebookEntryDetail(Long id, String userId);

    /**
     * 更新笔记条目
     *
     * @param dto 笔记条目DTO
     * @return 是否更新成功
     */
    boolean updateNotebookEntry(NotebookEntryDTO dto);

    /**
     * 删除笔记条目
     *
     * @param id     笔记条目ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteNotebookEntry(Long id, String userId);
} 