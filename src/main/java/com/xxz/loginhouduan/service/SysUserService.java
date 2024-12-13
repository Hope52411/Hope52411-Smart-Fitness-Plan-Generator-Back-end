package com.xxz.loginhouduan.service;

import com.xxz.loginhouduan.req.SysUserLoginReq;
import com.xxz.loginhouduan.req.SysUserSaveReq;
import com.xxz.loginhouduan.resp.SysUserLoginResp;

public interface SysUserService {
    void register(SysUserSaveReq req);

    SysUserLoginResp login(SysUserLoginReq req);

    // 添加删除用户的方法
    void deleteUserById(Long id);
}
