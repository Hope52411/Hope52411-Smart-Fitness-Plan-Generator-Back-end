package com.xxz.loginhouduan.repository;

import com.xxz.loginhouduan.entity.SysUserEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserRepository extends BaseMapper<SysUserEntity> {

    // ✅ 1. 使用 @Select 确保 MyBatis 能识别
    @Select("SELECT * FROM sys_user WHERE login_name = #{loginName} LIMIT 1")
    SysUserEntity findByLoginName(@Param("loginName") String loginName);

    // ✅ 2. 更新聊天历史，确保 @Update 语法正确
    @Update("UPDATE sys_user SET chat_history = #{chatHistory} WHERE id = #{userId}")
    void updateChatHistory(@Param("userId") Long userId, @Param("chatHistory") String chatHistory);
}
