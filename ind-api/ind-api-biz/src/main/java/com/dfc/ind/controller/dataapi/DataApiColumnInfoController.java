package com.dfc.ind.controller.dataapi;

import com.dfc.ind.common.core.utils.*;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.utils.SecurityUtils;

import com.dfc.ind.entity.dataapi.DataApiColumnInfoEntity;
import com.dfc.ind.service.dataapi.IDataApiColumnInfoService;
import io.swagger.annotations.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 * 描述: 本类为api字段配置表的前端控制器
 * </p>
 *
 * @author huff
 * @date 2024-04-07
 */
@Api(tags = "api字段配置表接口")
@RestController
@RequestMapping("/dataApiColumnInfo")
public class DataApiColumnInfoController extends BaseController {

    @Autowired
    private IDataApiColumnInfoService service;

    /**
     * 应用id 新增
     *
     * @param appId 应用id
     */
    @PostMapping("/syncColumnInfo")
    @ApiOperation(value = "api字段数据同步")
    public JsonResults syncColumnInfo(@RequestParam String appId) {
        return  service.syncColumnInfo(appId);
    }




    /**
     * api字段配置表  查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(DataApiColumnInfoEntity entity) {
        return JsonResults.success(service.list(new QueryWrapper<DataApiColumnInfoEntity>().setEntity(entity)));
    }

    /**
     * api字段配置表 分页查询
     *
     * @param entity        对象
     * @param pageNum       页码
     * @param pageSize      页数
     * @param orderByColumn 排序字段
     * @param isAsc         是否升序 true false
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true),
            @ApiImplicitParam(name = "orderByColumn", value = "排序字段"),
            @ApiImplicitParam(name = "isAsc", value = "是否升序 true false"),
    })
    public JsonResults list(DataApiColumnInfoEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.pageList(startPage(), entity));
    }

    /**
     * 导出api字段配置表
     *
     * @param entity 对象
     */
    @ApiOperation(value = "导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response, DataApiColumnInfoEntity entity) throws Exception {
        List<DataApiColumnInfoEntity> list = service.list(new QueryWrapper<DataApiColumnInfoEntity>().setEntity(entity));
        ExcelUtil<DataApiColumnInfoEntity> util = new ExcelUtil<>(DataApiColumnInfoEntity.class);
        util.exportExcel(response, list, "api字段配置表");
    }

}
