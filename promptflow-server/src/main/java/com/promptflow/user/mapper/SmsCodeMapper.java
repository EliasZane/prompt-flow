package com.promptflow.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.promptflow.user.model.entity.SmsCodeEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SmsCodeMapper extends BaseMapper<SmsCodeEntity> {
}
