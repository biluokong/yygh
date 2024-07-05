package com.biluo.yygh.user.api;

import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.common.utils.AuthContextHolder;
import com.biluo.yygh.model.user.UserInfo;
import com.biluo.yygh.user.service.UserInfoService;
import com.biluo.yygh.vo.user.LoginVo;
import com.biluo.yygh.vo.user.UserAuthVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInfoApiController {
	private final UserInfoService userInfoService;

	// 用户手机号登录接口
	@PostMapping("login")
	public Result<Map<String, Object>> login(@RequestBody LoginVo loginVo) {
		Map<String, Object> info = userInfoService.loginUser(loginVo);
		return Result.ok(info);
	}

	// 用户认证接口
	@PostMapping("auth/userAuth")
	public Result userAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request) {
		// 传递两个参数，第一个参数用户id，第二个参数认证数据vo对象
		userInfoService.userAuth(AuthContextHolder.getUserId(request), userAuthVo);
		return Result.ok();
	}

	// 获取用户id信息接口
	@GetMapping("auth/getUserInfo")
	public Result<UserInfo> getUserInfo(HttpServletRequest request) {
		Long userId = AuthContextHolder.getUserId(request);
		UserInfo userInfo = userInfoService.getById(userId);
		return Result.ok(userInfo);
	}
}
