package com.biluo.yygh.hosp.controller;

import com.biluo.yygh.common.result.Result;
import com.biluo.yygh.hosp.service.DepartmentService;
import com.biluo.yygh.vo.hosp.DepartmentVo;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hosp/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    //根据医院编号，查询医院所有科室列表
    @ApiOperation(value = "查询医院所有科室列表")
    @GetMapping("getDeptList/{hoscode}")
    public Result<List<DepartmentVo>> getDeptList(@PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.findDeptTree(hoscode);
        return Result.ok(list);
    }
}
