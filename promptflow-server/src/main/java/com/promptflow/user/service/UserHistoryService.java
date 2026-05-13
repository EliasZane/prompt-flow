package com.promptflow.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.promptflow.user.model.entity.UserHistoryEntity;
import com.promptflow.user.model.vo.UserHistoryVO;

import java.util.List;

public interface UserHistoryService extends IService<UserHistoryEntity> {

    /**
     * 保存用户生成历史记录
     * @param userId 用户ID
     * @param templateId 模板ID
     * @param templateCode 模板编码
     * @param tag 标签
     * @param inputData 输入数据
     * @param outputData 输出数据
     */
    void saveUserHistory(Long userId, Long templateId, String templateCode, String tag, String inputData, String outputData);

    /**
     * 获取用户历史记录列表
     * @param userId 用户ID
     * @param tag 标签 (可选)
     * @return 用户历史记录VO列表
     */
    List<UserHistoryVO> getUserHistoryList(Long userId, String tag);
}
