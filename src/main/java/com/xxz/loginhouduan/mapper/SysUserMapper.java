package com.xxz.loginhouduan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xxz.loginhouduan.entity.SysUserEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {

    // 查询所有用户
    @Select("SELECT id, login_name, email, password FROM sys_user")
    List<SysUserEntity> findAll();

    // 通过邮箱查找用户
    @Select("SELECT * FROM sys_user WHERE email = #{email}")
    SysUserEntity findByEmail(@Param("email") String email);

    // 通过 Token 查找用户
    @Select("SELECT * FROM sys_user WHERE reset_token = #{resetToken}")
    SysUserEntity findByResetToken(@Param("resetToken") String resetToken);

    // 更新用户的重置 Token
    @Update("UPDATE sys_user SET reset_token = #{resetToken}, reset_token_expire = #{resetTokenExpire} WHERE email = #{email}")
    void updateResetToken(@Param("email") String email, @Param("resetToken") String resetToken, @Param("resetTokenExpire") String resetTokenExpire);

    // 更新密码并清空 Token
    @Update("UPDATE sys_user SET password = #{password}, reset_token = NULL, reset_token_expire = NULL WHERE reset_token = #{resetToken}")
    void updatePassword(@Param("resetToken") String resetToken, @Param("password") String password);

    // 通过 loginName 查找用户
    @Select("SELECT * FROM sys_user WHERE login_name = #{loginName}")
    SysUserEntity findByLoginName(@Param("loginName") String loginName);

}
