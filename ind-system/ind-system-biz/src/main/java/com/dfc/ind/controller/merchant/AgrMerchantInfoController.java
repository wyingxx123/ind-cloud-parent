package com.dfc.ind.controller.merchant;

import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.merchant.AgrMerchantInfoEntity;
import com.dfc.ind.service.merchant.IAgrMerchantInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * <p>
 * 功能:本类为商户实体表的前端控制器
 * </p>
 *
 * @author nwy
 * @date 2020-03-26
 */
@Api(tags = "商户信息表接口")
@RestController
@RequestMapping("/merchant")
public class AgrMerchantInfoController extends BaseController {

    @Autowired
    private IAgrMerchantInfoService service;

    /**
     * 商户实体表 新增
     *
     * @param entity
     */
    @ApiIgnore
    public JsonResults add(@RequestBody AgrMerchantInfoEntity entity) throws CustomException {
        service.save(entity);
        return JsonResults.success(entity);
    }

    /**
     * 商户实体表 删除
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "id", value = "商户ID", required = true, dataType = "Long")
    public JsonResults del(@PathVariable Long id) throws CustomException {
        return toResult(service.removeMerchantById(id));
    }

    /**
     * 商户实体表 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody AgrMerchantInfoEntity entity) throws CustomException {
        service.updateById(entity);
        return JsonResults.success();
    }

    /**
     * 商户实体表 保存商户授权事项
     *
     * @param entity
     */
    @PutMapping("/empower")
    @ApiOperation(value = "商户授权事项")
    public JsonResults empower(@RequestBody AgrMerchantInfoEntity entity) throws CustomException {

        if (service.update(null, new UpdateWrapper<AgrMerchantInfoEntity>().lambda()
                .eq(AgrMerchantInfoEntity::getMerchantId, entity.getMerchantId())
                .set(AgrMerchantInfoEntity::getEmpowerJson, entity.getEmpowerJson()))) {
            return JsonResults.success("授权成功");
        } else {
            return JsonResults.error("授权失败");
        }
    }

    /**
     * 商户实体表 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "商户ID", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        AgrMerchantInfoEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("商户ID不存在!");
    }

    /**
     * 商户实体表  查询全部
     *
     * @param entity 对象
     */
    @ApiIgnore
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(AgrMerchantInfoEntity entity) {
        return JsonResults.success(service.list(new QueryWrapper<AgrMerchantInfoEntity>().setEntity(entity)));
    }

    /**
     * 描述：获取所有商户信息，只返回商户id和商户名称
     *
     * @return com.dfc.ind.common.core.web.domain.JsonResults
     * @author wdj
     * @date 2020-10-15 11:28:45
     */
    @GetMapping("/all")
    public JsonResults getAllMerchant() {
        return JsonResults.success(service.list(new QueryWrapper<AgrMerchantInfoEntity>()
                .select("merchant_id as merchantId", "merchant_name as merchantName")
                .setEntity(new AgrMerchantInfoEntity().setDelFlg("0"))));
    }

    /**
     * 商户实体表 分页查询
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
            @ApiImplicitParam(name = "filterCurrentId", value = "过滤当前商户"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true, defaultValue = "10"),
            @ApiImplicitParam(name = "orderByColumn", value = "排序字段"),
            @ApiImplicitParam(name = "isAsc", value = "是否升序 true false"),
    })
    public JsonResults list(AgrMerchantInfoEntity entity, Boolean filterCurrentId, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.pageList(startPage(), entity, filterCurrentId));
    }



    /*
    * 运管-商户管理修改商户状态后   商户表同步修改状态
    * */
    @ApiOperation(value = "运管-商户管理修改商户状态")
    @PostMapping("/updateMerchantStatus")
    public JsonResults updateMerchantStatus(@RequestBody AgrMerchantInfoEntity entity) {
        return service.updateMerchantStatus(entity);
    }

}
