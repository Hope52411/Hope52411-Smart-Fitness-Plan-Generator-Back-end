package com.xxz.loginhouduan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxz.loginhouduan.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {
    @Select("select id,login_name, email, password from sys_user")
    List<SysUserEntity> findAll();

}
