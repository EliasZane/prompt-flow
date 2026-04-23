package com.promptflow.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.promptflow.template.model.entity.TemplateEntity;
import com.promptflow.template.model.vo.TemplateDetailVO;
import com.promptflow.template.model.vo.TemplateVO;

import java.util.List;

public interface TemplateService extends IService<TemplateEntity> {

    /**
     * 查询所有启用的模板列表
     * @return 模板VO列表
     */
    List<TemplateVO> listAllTemplates();

    /**
     * 根据模板编码获取模板详情 VO
     * @param templateCode 模板编码
     * @return 模板详情VO
     */
    TemplateDetailVO getTemplateDetail(String templateCode);

    /**
     * 根据模板编码获取模板实体
     * @param templateCode 模板编码
     * @return 模板实体
     */
    TemplateEntity getTemplateByCode(String templateCode);
}
