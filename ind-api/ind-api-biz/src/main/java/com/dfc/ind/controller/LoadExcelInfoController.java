package com.dfc.ind.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.log.annotation.Log;
import com.dfc.ind.common.log.enums.BusinessType;
import com.dfc.ind.entity.LoadExcelInfoEntity;
import com.dfc.ind.service.ILoadExcelInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * <p>
 * 描述: 本类为导入模板信息的前端控制器
 * </p>
 *
 * @author huff
 * @date 2024-07-22
 */
@Api(tags = "导入模板信息接口")
@RestController
@RequestMapping("/loadExcelInfo")
public class LoadExcelInfoController extends BaseController {

    @Autowired
    private ILoadExcelInfoService service;

    /**
     * 导入模板信息 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    @Log(title = "导入模板信息-新增", businessType = BusinessType.INSERT)
    public JsonResults add(@RequestBody LoadExcelInfoEntity entity) {
        service.saveOrUpdate(entity);
        return JsonResults.success(entity);
    }

    /**
     * 导入模板信息 删除
     *
     * @param entity
     */
    @DeleteMapping("/deleteTmpl")
    @ApiOperation(value = "删除")
    @Log(title = "导入模板信息-删除", businessType = BusinessType.DELETE)
    public JsonResults del(LoadExcelInfoEntity entity) {
        QueryWrapper<LoadExcelInfoEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("merchant_id", entity.getMerchantId())
                   .eq("template_no", entity.getTemplateNo());
        return toResult(service.remove(queryWrapper));
    }

    /**
     * 导入模板信息 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    @Log(title = "导入模板信息-修改", businessType = BusinessType.UPDATE)
    public JsonResults edit(@RequestBody LoadExcelInfoEntity entity) {

        service.updateById(entity);
        return JsonResults.success();
    }

    /**
     * 导入模板信息 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "模板编号", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        LoadExcelInfoEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("模板编号不存在!");
    }

    /**
     * 导入模板信息  查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(LoadExcelInfoEntity entity) {
        return JsonResults.success(service.list(new QueryWrapper<LoadExcelInfoEntity>().setEntity(entity)));
    }

    /**
     * 导入模板信息 分页查询
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
    public JsonResults list(LoadExcelInfoEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.page(startPage(), new QueryWrapper<LoadExcelInfoEntity>().setEntity(entity)));
    }




    /**
     * 导入
     * @param entity 对象
     */
    @ApiOperation(value = "清除指定数据")
    @PostMapping("/clearDataByTempNo")
    public JsonResults clearDataByTempNo(@Validated LoadExcelInfoEntity entity) throws Exception {
        return service.clearDataByTempNo(entity);
    }

    /**
     * 导入
     * @param entity 对象
     */
    @ApiOperation(value = "导入")
    @PostMapping("/importData")
    public JsonResults importData(MultipartFile file, @Validated LoadExcelInfoEntity entity) throws Exception {
       return service.importData(file,entity);
    }
}
