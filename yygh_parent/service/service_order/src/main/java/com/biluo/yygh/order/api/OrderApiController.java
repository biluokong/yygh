package com.biluo.yygh.order.api;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.common.utils.AuthContextHolder;
import com.biluo.yygh.enums.OrderStatusEnum;
import com.biluo.yygh.model.order.OrderInfo;
import com.biluo.yygh.order.service.OrderService;
import com.biluo.yygh.vo.order.OrderCountQueryVo;
import com.biluo.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/order/orderInfo")
@RequiredArgsConstructor
public class OrderApiController {
	private final OrderService orderService;

	// 生成挂号订单
	@PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
	public Result<Long> savaOrders(@PathVariable String scheduleId,
								   @PathVariable Long patientId) {
		Long orderId = orderService.saveOrder(scheduleId, patientId);
		return Result.ok(orderId);
	}

	// 根据订单id查询订单详情
	@GetMapping("auth/getOrders/{orderId}")
	public Result<OrderInfo> getOrders(@PathVariable String orderId) {
		OrderInfo orderInfo = orderService.getOrder(orderId);
		return Result.ok(orderInfo);
	}

	// 订单列表（条件查询带分页）
	@GetMapping("auth/{page}/{limit}")
	public Result<IPage<OrderInfo>> list(@PathVariable Long page,
										 @PathVariable Long limit,
										 OrderQueryVo orderQueryVo, HttpServletRequest request) {
		// 设置当前用户id
		orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
		Page<OrderInfo> pageParam = new Page<>(page, limit);
		IPage<OrderInfo> pageModel =
				orderService.selectPage(pageParam, orderQueryVo);
		return Result.ok(pageModel);
	}

	@ApiOperation(value = "获取订单状态")
	@GetMapping("auth/getStatusList")
	public Result<List<Map<String, Object>>> getStatusList() {
		return Result.ok(OrderStatusEnum.getStatusList());
	}

	// 取消预约
	@GetMapping("auth/cancelOrder/{orderId}")
	public Result<Boolean> cancelOrder(@PathVariable Long orderId) {
		Boolean isOrder = orderService.cancelOrder(orderId);
		return Result.ok(isOrder);
	}

	@ApiOperation(value = "获取订单统计数据")
	@PostMapping("inner/getCountMap")
	public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
		return orderService.getCountMap(orderCountQueryVo);
	}
}



