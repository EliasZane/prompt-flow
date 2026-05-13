package com.promptflow.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.promptflow.user.model.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {
    UserEntity selectByUsername(String username);
}
