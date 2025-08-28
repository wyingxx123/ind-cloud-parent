package com.dfc.ind.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.utils.SecurityUtils;
import com.dfc.ind.entity.PubDictDataEntity;

import com.dfc.ind.service.IPubDictDataService;
import com.dfc.ind.vo.PubDictDataVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 描述: 本类为商户参数值信息的前端控制器
 * </p>
 *
 * @author huff
 * @date 2023-05-17
 */
@Api(tags = "商户参数值信息接口")
@RestController
@RequestMapping("/pubDictData")
public class PubDictDataController extends BaseController {

    @Autowired
    private IPubDictDataService service;

    /**
     * 商户参数值信息 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody PubDictDataEntity entity) {
        service.save(entity);
        return JsonResults.success(entity);
    }

    /**
     * 商户参数值信息 删除
     *
     * @param entity
     */
    @DeleteMapping("/delData")
    @ApiOperation(value = "删除")
    public JsonResults del( PubDictDataEntity entity) {
        QueryWrapper<PubDictDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_type", entity.getDictType())
                   .eq("merchant_id", entity.getMerchantId())
                   .eq("dict_code", entity.getDictCode());
        return toResult(service.remove(queryWrapper));
    }

    /**
     * 商户参数值信息 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody PubDictDataEntity entity) {
        service.saveOrUpdate(entity);
        return JsonResults.success();
    }

    /**
     * 商户参数值信息 修改
     *
     * @param list
     */
    @PostMapping("/editList")
    @ApiOperation(value = "修改")
    public JsonResults editList(@RequestBody List<PubDictDataEntity> list) {
        for (PubDictDataEntity entity : list) {
            entity.setOperator(SecurityUtils.getUserName());
            entity.setOpDate(new Date());
            entity.setOpTime(new Date());
        }
        service.saveOrUpdateBatch(list);
        return JsonResults.success();
    }

    @PostMapping("/saveOrUpdateList")
    @ApiOperation(value = "修改")
    public JsonResults saveOrUpdateList(@RequestBody List<PubDictDataEntity> list) {
        service.saveOrUpdateBatch(list);
        return JsonResults.success();
    }

    /**
     * 商户参数值信息 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "商户字典类型", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        PubDictDataEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("商户字典类型不存在!");
    }

    @GetMapping("/getDictDataByType")
    @ApiOperation(value = "查询字典数据接口")
    @ApiImplicitParam(name = "id", value = "商户字典类型", required = true, dataType = "Long")
    public JsonResults getDictDataByType(PubDictDataEntity entity) {
        List<PubDictDataVo>  list= service.getDictDataByType(entity.getMerchantId(),entity.getDictType(),entity.getDictCode());
        return JsonResults.success(list);
    }

    @GetMapping("/getDictData")
    @ApiOperation(value = "查询字典数据接口")
    public JsonResults getDictData(PubDictDataEntity entity) {
        List<PubDictDataVo>  list= service.getDictData(entity);
        return JsonResults.success(list);
    }

    @GetMapping("/getDictDataByTypeLike")
    @ApiOperation(value = "查询字典数据接口")
    @ApiImplicitParam(name = "id", value = "商户字典类型", required = true, dataType = "Long")
    public JsonResults getDictDataByTypeLike(PubDictDataEntity entity) {
        List<PubDictDataVo>  list= service.getDictDataByTypeLike(entity);
        return JsonResults.success(list);
    }
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(PubDictDataEntity entity) {
        return JsonResults.success(service.list(new QueryWrapper<PubDictDataEntity>().lambda().setEntity(entity).orderByAsc(PubDictDataEntity::getSortNo)));
    }

    /**
     * 商户参数类型信息 分页查询
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
    public JsonResults list(PubDictDataEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.page(startPage(), new QueryWrapper<PubDictDataEntity>().setEntity(entity)));
    }
    /**
     * 导出商户参数值信息
     *
     * @param entity 对象
     */
    @ApiOperation(value = "导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response, PubDictDataEntity entity) throws Exception {
        List<PubDictDataEntity> list = service.list(new QueryWrapper<PubDictDataEntity>().setEntity(entity));
        ExcelUtil<PubDictDataEntity> util = new ExcelUtil<>(PubDictDataEntity.class);
        util.exportExcel(response, list, "商户参数值信息");
    }

}
