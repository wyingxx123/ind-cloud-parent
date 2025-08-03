package com.dfc.ind.controller.dataapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.dataapi.DataApiParaInfoEntity;
import com.dfc.ind.service.dataapi.IDataApiParaInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
@RequestMapping("/dataApiPara")
public class DataApiParaInfoController extends BaseController {

    @Autowired
    private IDataApiParaInfoService service;

    /**
     * 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody DataApiParaInfoEntity entity) {

        service.save(entity);
        return JsonResults.success(entity);
    }

    /**
     * 删除
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "id", value = "服务识别码", required = true, dataType = "Long")
    public JsonResults del(@PathVariable Long id) {
        return toResult(service.removeById(id));
    }

    /**
     * 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody DataApiParaInfoEntity entity) {

        service.updateById(entity);
        return JsonResults.success();
    }

    /**
     * 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "服务识别码", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        DataApiParaInfoEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("服务识别码不存在!");
    }

    /**
     * 查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(DataApiParaInfoEntity entity) {
        return JsonResults.success(service.list(new QueryWrapper<DataApiParaInfoEntity>().setEntity(entity)));
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
    public JsonResults list(DataApiParaInfoEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.page(startPage(), new QueryWrapper<DataApiParaInfoEntity>().setEntity(entity)));
    }

    /**
     * 导出
     *
     * @param entity 对象
     */
    @ApiOperation(value = "导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response, DataApiParaInfoEntity entity) throws Exception {
        List<DataApiParaInfoEntity> list = service.list(new QueryWrapper<DataApiParaInfoEntity>().setEntity(entity));
        ExcelUtil<DataApiParaInfoEntity> util = new ExcelUtil<>(DataApiParaInfoEntity.class);
        util.exportExcel(response, list, "");
    }

}
