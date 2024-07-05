package com.biluo.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.biluo.yygh.common.exception.YyghException;
import com.biluo.yygh.common.helper.JwtHelper;
import com.biluo.yygh.common.result.ResultCodeEnum;
import com.biluo.yygh.enums.AuthStatusEnum;
import com.biluo.yygh.model.user.Patient;
import com.biluo.yygh.model.user.UserInfo;
import com.biluo.yygh.user.mapper.UserInfoMapper;
import com.biluo.yygh.user.service.PatientService;
import com.biluo.yygh.user.service.UserInfoService;
import com.biluo.yygh.vo.user.LoginVo;
import com.biluo.yygh.vo.user.UserAuthVo;
import com.biluo.yygh.vo.user.UserInfoQueryVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
	private final StringRedisTemplate redisTemplate;
	private final PatientService patientService;


	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> loginUser(LoginVo loginVo) {
		// 从loginVo获取输入的手机号，和验证码
		String phone = loginVo.getPhone();
		String code = loginVo.getCode();

		// 判断手机号和验证码是否为空
		if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
			throw new YyghException(ResultCodeEnum.PARAM_ERROR);
		}

		// 判断手机验证码和输入的验证码是否一致
		String redisCode = redisTemplate.opsForValue().get(phone);
		if (!code.equals(redisCode)) {
			throw new YyghException(ResultCodeEnum.CODE_ERROR);
		}

		// 绑定手机号码
		UserInfo userInfo = null;
		if (!StringUtils.isEmpty(loginVo.getOpenid())) {
			userInfo = this.selectWxInfoOpenId(loginVo.getOpenid());
			if (null != userInfo) {
				userInfo.setPhone(loginVo.getPhone());
				this.updateById(userInfo);
			} else {
				throw new YyghException(ResultCodeEnum.DATA_ERROR);
			}
		}

		// 如果userinfo为空，进行正常手机登录
		if (userInfo == null) {
			// 判断是否第一次登录：根据手机号查询数据库，如果不存在相同手机号就是第一次登录
			QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
			wrapper.eq("phone", phone);
			userInfo = baseMapper.selectOne(wrapper);
			if (userInfo == null) { // 第一次使用这个手机号登录
				// 添加信息到数据库
				userInfo = new UserInfo();
				userInfo.setName("");
				userInfo.setPhone(phone);
				userInfo.setStatus(1);
				baseMapper.insert(userInfo);
			}
		}

		// 校验是否被禁用
		if (userInfo.getStatus() == 0) {
			throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
		}

		// 不是第一次，直接登录
		// 返回登录信息
		// 返回登录用户名
		// 返回token信息
		Map<String, Object> map = new HashMap<>();
		String name = userInfo.getName();
		if (StringUtils.isEmpty(name)) {
			name = userInfo.getNickName();
		}
		if (StringUtils.isEmpty(name)) {
			name = userInfo.getPhone();
		}
		map.put("name", name);

		// jwt生成token字符串
		String token = JwtHelper.createToken(userInfo.getId(), name);
		map.put("token", token);
		return map;
	}

	@Override
	public UserInfo selectWxInfoOpenId(String openid) {
		return lambdaQuery().eq(UserInfo::getOpenid, openid).one();
	}

	// 用户认证
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void userAuth(Long userId, UserAuthVo userAuthVo) {
		// 根据用户id查询用户信息
		UserInfo userInfo = baseMapper.selectById(userId);
		// 设置认证信息
		// 认证人姓名
		userInfo.setName(userAuthVo.getName());
		// 其他认证信息
		userInfo.setCertificatesType(userAuthVo.getCertificatesType());
		userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
		userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
		userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
		// 进行信息更新
		baseMapper.updateById(userInfo);
	}

	// 用户列表（条件查询带分页）
	@Override
	public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
		// UserInfoQueryVo获取条件值
		String name = userInfoQueryVo.getKeyword(); // 用户名称
		Integer status = userInfoQueryVo.getStatus();// 用户状态
		Integer authStatus = userInfoQueryVo.getAuthStatus(); // 认证状态
		String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); // 开始时间
		String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); // 结束时间
		IPage<UserInfo> pages = lambdaQuery()
				.like(!StringUtils.isEmpty(name), UserInfo::getName, name)
				.eq(!StringUtils.isEmpty(status), UserInfo::getStatus, status)
				.eq(!StringUtils.isEmpty(authStatus), UserInfo::getAuthStatus, authStatus)
				.ge(!StringUtils.isEmpty(createTimeBegin), UserInfo::getCreateTime, createTimeBegin)
				.le(!StringUtils.isEmpty(createTimeEnd), UserInfo::getCreateTime, createTimeEnd)
				.page(pageParam);
		// 编号变成对应值封装
		pages.getRecords().forEach(this::packageUserInfo);
		return pages;
	}

	// 用户锁定
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void lock(Long userId, Integer status) {
		if (status == 0 || status == 1) {
			UserInfo userInfo = baseMapper.selectById(userId);
			userInfo.setStatus(status);
			baseMapper.updateById(userInfo);
		}
	}

	// 用户详情
	@Override
	public Map<String, Object> show(Long userId) {
		Map<String, Object> map = new HashMap<>();
		// 根据userid查询用户信息
		UserInfo userInfo = this.packageUserInfo(baseMapper.selectById(userId));
		map.put("userInfo", userInfo);
		// 根据userid查询就诊人信息
		List<Patient> patientList = patientService.findAllUserId(userId);
		map.put("patientList", patientList);
		return map;
	}

	// 认证审批  2通过  -1不通过
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void approval(Long userId, Integer authStatus) {
		if (authStatus == 2 || authStatus == -1) {
			UserInfo userInfo = baseMapper.selectById(userId);
			userInfo.setAuthStatus(authStatus);
			baseMapper.updateById(userInfo);
		}
	}

	// 编号变成对应值封装
	private UserInfo packageUserInfo(UserInfo userInfo) {
		// 处理认证状态编码
		userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
		// 处理用户状态 0  1
		String statusString = userInfo.getStatus() == 0 ? "锁定" : "正常";
		userInfo.getParam().put("statusString", statusString);
		return userInfo;
	}
}
