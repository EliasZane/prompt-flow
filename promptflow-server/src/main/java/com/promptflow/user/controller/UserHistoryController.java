package com.promptflow.user.controller;

import com.promptflow.common.result.Result;
import com.promptflow.user.model.entity.UserEntity;
import com.promptflow.user.model.vo.UserHistoryVO;
import com.promptflow.user.service.UserHistoryService;
import com.promptflow.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/history")
public class UserHistoryController {

    @Autowired
    private UserHistoryService userHistoryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<List<UserHistoryVO>> getUserHistory(@RequestParam(required = false) String tag) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username == null || "anonymousUser".equals(username)) {
            return Result.failed("用户未登录");
        }

        UserEntity user = userService.findByUsername(username);
        if (user == null) {
            return Result.failed("用户未找到");
        }

        List<UserHistoryVO> historyList = userHistoryService.getUserHistoryList(user.getId(), tag);
        return Result.success(historyList);
    }
}
