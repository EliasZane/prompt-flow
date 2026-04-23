package com.promptflow.template.controller;

import com.promptflow.common.result.Result;
import com.promptflow.template.model.entity.TemplateEntity;
import com.promptflow.template.model.vo.TemplateDetailVO;
import com.promptflow.template.model.vo.TemplateVO;
import com.promptflow.template.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    /**
     * 查询所有启用的模板列表
     * GET /api/templates
     * @return 模板VO列表
     */
    @GetMapping
    public Result<List<TemplateVO>> listTemplates() {
        List<TemplateVO> templates = templateService.listAllTemplates();
        return Result.success(templates);
    }

    /**
     * 根据模板编码获取模板详情
     * GET /api/templates/{templateCode}
     * @param templateCode 模板编码
     * @return 模板详情VO
     */
    @GetMapping("/{templateCode}")
    public Result<TemplateDetailVO> getTemplateDetail(@PathVariable String templateCode) {
        TemplateDetailVO template = templateService.getTemplateDetail(templateCode);
        if (template == null) {
            return Result.failed(404, "模板不存在或已禁用");
        }
        return Result.success(template);
    }
}
