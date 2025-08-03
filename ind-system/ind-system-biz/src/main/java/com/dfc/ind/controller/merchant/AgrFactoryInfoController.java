package com.dfc.ind.controller.merchant;

import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.merchant.AgrFactoryInfoEntity;
import com.dfc.ind.service.merchant.IAgrFactoryInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 描述: 本类为工厂信息表的前端控制器
 * </p>
 *
 * @author ylyan
 * @date 2020-04-13
 * @copyright 武汉数慧享智能科技有限公司
 */
@Api(tags = "工厂信息表接口")
@RestController
@RequestMapping("/factory")
public class AgrFactoryInfoController extends BaseController {

    @Autowired
    private IAgrFactoryInfoService service;

    /**
     * 工厂信息表 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody AgrFactoryInfoEntity entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        entity.setCreateBy(SecurityUtils.getUserName());
        service.save(entity);
        return JsonResults.success(entity);
    }

    /**
     * <工厂信息表 删除>
     *
     * @param idList 主键ID列表
     * @return
     * @author ylyan
     * @Date 2020/4/13 12:48
     */
    @DeleteMapping("/{idList}")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "idList", value = "工厂唯一id", required = true, dataType = "List")
    public JsonResults del(@PathVariable List<Long> idList) {
        return toResult(service.removeByIds(idList));
    }

    /**
     * 工厂信息表 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody AgrFactoryInfoEntity entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        entity.setUpdateBy(SecurityUtils.getUserName());
        service.updateById(entity);
        return JsonResults.success();
    }

    /**
     * 工厂信息表 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "工厂唯一id", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        AgrFactoryInfoEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("工厂唯一id不存在!");
    }

    /**
     * 工厂信息表  查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(AgrFactoryInfoEntity entity) {
        return JsonResults.success(service.list(new QueryWrapper<AgrFactoryInfoEntity>().setEntity(entity)));
    }

    /**
     * 工厂信息表 分页查询
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
    public JsonResults list(AgrFactoryInfoEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.page(startPage(), new QueryWrapper<AgrFactoryInfoEntity>().setEntity(entity)));
    }

    /**
     * 导出工厂信息表
     *
     * @param entity 对象
     */
    @ApiOperation(value = "导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response, AgrFactoryInfoEntity entity)  throws IOException {
        List<AgrFactoryInfoEntity> list = service.list(new QueryWrapper<AgrFactoryInfoEntity>().setEntity(entity));
        ExcelUtil<AgrFactoryInfoEntity> util = new ExcelUtil<>(AgrFactoryInfoEntity.class);
        util.exportExcel(response, list, "工厂信息表");
    }

    /**
     * <根据商户id获取工厂信息明细>
     *
     * @param merchantId 商户唯一id
     * @return
     * @author ylyan
     * @Date 2020/4/13 12:55
     */
    @GetMapping("/info/{merchantId}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "merchantId", value = "商户唯一id", required = true, dataType = "Long")
    public JsonResults info(@PathVariable Long merchantId) {
        AgrFactoryInfoEntity entity = service.getByMerchantId(merchantId);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("工厂信息不存在");
    }

}
