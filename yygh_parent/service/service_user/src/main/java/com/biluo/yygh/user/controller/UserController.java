package com.biluo.yygh.user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.model.user.UserInfo;
import com.biluo.yygh.user.service.UserInfoService;
import com.biluo.yygh.vo.user.UserInfoQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class UserController {
	private final UserInfoService userInfoService;

	// 用户列表（条件查询带分页）
	@GetMapping("{page}/{limit}")
	public Result<IPage<UserInfo>> list(@PathVariable Long page,
						 @PathVariable Long limit,
						 UserInfoQueryVo userInfoQueryVo) {
		Page<UserInfo> pageParam = new Page<>(page, limit);
		IPage<UserInfo> pageModel =
				userInfoService.selectPage(pageParam, userInfoQueryVo);
		return Result.ok(pageModel);
	}

	// 用户锁定
	@GetMapping("lock/{userId}/{status}")
	public Result lock(@PathVariable Long userId, @PathVariable Integer status) {
		userInfoService.lock(userId, status);
		return Result.ok();
	}

	// 用户详情
	@GetMapping("show/{userId}")
	public Result<Map<String, Object>> show(@PathVariable Long userId) {
		Map<String, Object> map = userInfoService.show(userId);
		return Result.ok(map);
	}

	// 认证审批
	@GetMapping("approval/{userId}/{authStatus}")
	public Result approval(@PathVariable Long userId, @PathVariable Integer authStatus) {
		userInfoService.approval(userId, authStatus);
		return Result.ok();
	}
}
