package com.dfc.ind.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.log.annotation.Log;
import com.dfc.ind.common.log.enums.BusinessType;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.PubDictTypeEntity;
import com.dfc.ind.service.IPubDictTypeService;
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
 * 描述: 本类为商户参数类型信息的前端控制器
 * </p>
 *
 * @author huff
 * @date 2024-03-12
 */
@Api(tags = "商户参数类型信息接口")
@RestController
@RequestMapping("/pubDictType")
public class PubDictTypeController extends BaseController {

    @Autowired
    private IPubDictTypeService service;

    /**
     * 商户参数类型信息 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    @Log(title = "商户参数类型信息-新增", businessType = BusinessType.INSERT)
    public JsonResults add(@RequestBody PubDictTypeEntity entity) {
        entity.setOpTime(DateUtils.getNowDate());
        entity.setOperator(SecurityUtils.getUsername());
        entity.setOpDate(DateUtils.getNowDate());
        service.save(entity);
        return JsonResults.success(entity);
    }

    /**
     * 商户参数类型信息 删除
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    @Log(title = "商户参数类型信息-删除", businessType = BusinessType.DELETE)
    @ApiImplicitParam(name = "id", value = "商户字典类型识别号", required = true, dataType = "Long")
    public JsonResults del(@PathVariable Long id) {
        return toResult(service.removeById(id));
    }

    /**
     * 商户参数类型信息 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    @Log(title = "商户参数类型信息-修改", businessType = BusinessType.UPDATE)
    public JsonResults edit(@RequestBody PubDictTypeEntity entity) {
        entity.setOpTime(DateUtils.getNowDate());
        entity.setOpDate(DateUtils.getNowDate());
        entity.setOperator(SecurityUtils.getUsername());
        service.updateById(entity);
        return JsonResults.success();
    }

    /**
     * 商户参数类型信息 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "商户字典类型识别号", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        PubDictTypeEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("商户字典类型识别号不存在!");
    }

    /**
     * 商户参数类型信息  查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(PubDictTypeEntity entity) {
        QueryWrapper<PubDictTypeEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().likeRight(StringUtils.isNotEmpty(entity.getDictType()),PubDictTypeEntity::getDictType,entity.getDictType())
                .eq(StringUtils.isNotEmpty(entity.getSubjectUp()),PubDictTypeEntity::getSubjectUp,entity.getSubjectUp())
                .eq(PubDictTypeEntity::getMerchantId,entity.getMerchantId());

        return JsonResults.success(service.list(queryWrapper));
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
    public JsonResults list(PubDictTypeEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.page(startPage(), new QueryWrapper<PubDictTypeEntity>().setEntity(entity)));
    }

    /**
     * 导出商户参数类型信息
     *
     * @param entity 对象
     */
    @ApiOperation(value = "导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response, PubDictTypeEntity entity) throws Exception {
        List<PubDictTypeEntity> list = service.list(new QueryWrapper<PubDictTypeEntity>().setEntity(entity));
        ExcelUtil<PubDictTypeEntity> util = new ExcelUtil<>(PubDictTypeEntity.class);
        util.exportExcel(response, list, "商户参数类型信息");
    }

}
