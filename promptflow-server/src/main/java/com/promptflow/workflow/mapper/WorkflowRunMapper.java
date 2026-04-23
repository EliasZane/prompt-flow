package com.promptflow.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.promptflow.workflow.model.entity.WorkflowRunEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WorkflowRunMapper extends BaseMapper<WorkflowRunEntity> {

}
