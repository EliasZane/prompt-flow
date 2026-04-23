package com.promptflow.template.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.promptflow.template.mapper.TemplateMapper;
import com.promptflow.template.model.entity.TemplateEntity;
import com.promptflow.template.model.vo.TemplateDetailVO;
import com.promptflow.template.model.vo.TemplateVO;
import com.promptflow.template.service.TemplateService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class TemplateServiceImpl extends ServiceImpl<TemplateMapper, TemplateEntity> implements TemplateService {

    @Override
    public List<TemplateVO> listAllTemplates() {
        LambdaQueryWrapper<TemplateEntity> queryWrapper = new LambdaQueryWrapper<>();
        // 不再限制 status=1，返回所有状态的模板以便前端根据 status 判断（如显示“即将上线”）
        queryWrapper.orderByAsc(TemplateEntity::getSortNum); // 按排序字段升序

        List<TemplateEntity> entities = list(queryWrapper);
        return entities.stream().map(entity -> {
            TemplateVO vo = new TemplateVO();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public TemplateDetailVO getTemplateDetail(String templateCode) {
        TemplateEntity entity = getTemplateByCode(templateCode);
        if (entity == null) {
            return null;
        }
        TemplateDetailVO vo = new TemplateDetailVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Override
    public TemplateEntity getTemplateByCode(String templateCode) {
        LambdaQueryWrapper<TemplateEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TemplateEntity::getTemplateCode, templateCode);
        // 获取详情时也不再强制要求 status=1，以便查看预览或即将上线的模板详情
        return getOne(queryWrapper);
    }
}
