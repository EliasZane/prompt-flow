package com.promptflow.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.promptflow.user.model.entity.UserHistoryEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserHistoryMapper extends BaseMapper<UserHistoryEntity> {
}
