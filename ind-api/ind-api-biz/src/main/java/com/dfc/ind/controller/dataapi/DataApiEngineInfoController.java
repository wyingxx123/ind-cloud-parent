package com.dfc.ind.controller.dataapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.dataapi.DataApiEngineInfoEntity;
import com.dfc.ind.service.dataapi.IDataApiEngineInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * 描述: 本类为的前端控制器
 * </p>
 *
 * @author huff
 * @date 2022-09-08
 */
@Api(tags = "接口")
@RestController
@RequestMapping("/dataApiEngine")
public class DataApiEngineInfoController extends BaseController {

    @Autowired
    private IDataApiEngineInfoService service;


    /**
     * 查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(DataApiEngineInfoEntity entity, HttpServletRequest request) {
        return JsonResults.success(service.list(new QueryWrapper<DataApiEngineInfoEntity>().setEntity(entity)));
    }

    /**
     * 分页查询
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
    public JsonResults list(DataApiEngineInfoEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.page(startPage(), new QueryWrapper<DataApiEngineInfoEntity>().setEntity(entity)));
    }


}
