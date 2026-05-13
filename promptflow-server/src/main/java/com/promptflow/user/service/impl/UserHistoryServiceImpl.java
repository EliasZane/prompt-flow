package com.promptflow.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.promptflow.common.utils.JsonUtils;
import com.promptflow.user.mapper.UserHistoryMapper;
import com.promptflow.user.model.entity.UserHistoryEntity;
import com.promptflow.user.model.vo.UserHistoryVO;
import com.promptflow.user.service.UserHistoryService;
import com.promptflow.workflow.model.vo.WorkflowResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserHistoryServiceImpl extends ServiceImpl<UserHistoryMapper, UserHistoryEntity> implements UserHistoryService {

    @Override
    public void saveUserHistory(Long userId, Long templateId, String templateCode, String tag, String inputData, String outputData) {
        UserHistoryEntity userHistory = new UserHistoryEntity();
        userHistory.setUserId(userId);
        userHistory.setTemplateId(templateId);
        userHistory.setTemplateCode(templateCode);
        userHistory.setTag(tag);
        userHistory.setInputData(inputData);
        userHistory.setOutputData(outputData);
        userHistory.setCreatedAt(LocalDateTime.now());
        userHistory.setUpdatedAt(LocalDateTime.now());
        save(userHistory);
    }

    @Override
    public List<UserHistoryVO> getUserHistoryList(Long userId, String tag) {
        LambdaQueryWrapper<UserHistoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserHistoryEntity::getUserId, userId);
        if (StringUtils.hasText(tag)) {
            queryWrapper.eq(UserHistoryEntity::getTag, tag);
        }
        queryWrapper.orderByDesc(UserHistoryEntity::getCreatedAt); // 按时间倒序

        List<UserHistoryEntity> entities = list(queryWrapper);
        return entities.stream().map(entity -> {
            UserHistoryVO vo = new UserHistoryVO();
            BeanUtils.copyProperties(entity, vo);
            
            // 解析 JSON 字符串为对象
            if (StringUtils.hasText(entity.getInputData())) {
                vo.setInputData(JsonUtils.parse(entity.getInputData(), Map.class));
            }
            if (StringUtils.hasText(entity.getOutputData())) {
                vo.setOutputData(JsonUtils.parse(entity.getOutputData(), WorkflowResultVO.class));
            }
            
            return vo;
        }).collect(Collectors.toList());
    }
}
