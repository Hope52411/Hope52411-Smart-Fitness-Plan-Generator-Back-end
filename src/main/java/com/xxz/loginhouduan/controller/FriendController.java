package com.xxz.loginhouduan.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xxz.loginhouduan.entity.FriendRelation;
import com.xxz.loginhouduan.entity.SysUserEntity;
import com.xxz.loginhouduan.mapper.FriendRelationMapper;
import com.xxz.loginhouduan.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private FriendRelationMapper friendRelationMapper;

    // ✅ 添加好友
    @PostMapping("/add")
    public String addFriend(@RequestBody Map<String, Object> payload) {
        System.out.println("🔥 收到前端 payload: " + payload);

        // 1️⃣ 安全解析参数
        String userName = (String) payload.get("userName");
        String friendIdStr = String.valueOf(payload.get("friendId"));

        if (userName == null || friendIdStr == null || friendIdStr.equals("null")) {
            System.out.println("❌ 参数缺失！userName=" + userName + " | friendId=" + friendIdStr);
            return "参数缺失";
        }

        Long friendId;
        try {
            friendId = new BigDecimal(friendIdStr).longValue(); // 防止 JS 精度丢失
        } catch (Exception e) {
            System.out.println("❌ 解析 friendId 出错：" + e.getMessage());
            return "friendId 参数错误";
        }

        System.out.println("✅ 添加好友当前用户: " + userName);
        System.out.println("✅ 添加好友目标用户 ID: " + friendId);

        // 2️⃣ 查询用户是否存在
        SysUserEntity currentUser = sysUserMapper.selectOne(
                new QueryWrapper<SysUserEntity>().eq("login_name", userName));
        SysUserEntity friendUser = sysUserMapper.selectById(friendId);

        if (currentUser == null || friendUser == null) {
            System.out.println("❌ 用户不存在: currentUser=" + currentUser + ", friendUser=" + friendUser);
            return "用户不存在";
        }
        if (currentUser.getId().equals(friendId)) {
            System.out.println("⚠️ 不允许添加自己为好友！");
            return "不能添加自己为好友";
        }
        // 3️⃣ 检查是否已是好友
        QueryWrapper<FriendRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", currentUser.getId()).eq("friend_id", friendId);
        if (friendRelationMapper.selectOne(wrapper) != null) {
            System.out.println("⚠️ 已经是好友: " + currentUser.getLoginName() + " → " + friendId);
            return "已经是好友了";
        }

        // 4️⃣ 插入好友关系
        FriendRelation relation = new FriendRelation();
        relation.setUserId(currentUser.getId());
        relation.setFriendId(friendId);
        int result = friendRelationMapper.insert(relation);

        if (result > 0) {
            System.out.println("✅ 插入成功！影响行数: " + result);
            return "添加成功";
        } else {
            System.out.println("❌ 插入失败！");
            return "添加失败";
        }
    }




    // ✅ 获取好友列表
    @GetMapping("/list")
    public List<Map<String, Object>> getFriends(@RequestParam String userName) {
        SysUserEntity user = sysUserMapper.selectOne(
                new QueryWrapper<SysUserEntity>().eq("login_name", userName));
        return friendRelationMapper.getFriends(user.getId());
    }
}
