package com.dfc.ind.controller.external;

import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.external.ExternalUserEntity;
import com.dfc.ind.service.external.IExternalUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 描述: 内外部客户信息对照表 前端控制器
 * </p>
 *
 * @author wubt
 * @date 2020/11/26
 * @copyright 武汉数慧享智能科技有限公司
 */
@RestController
@RequestMapping("/externalUser")
@Api(tags = "内外部客户信息对照表")
public class ExternalUserController extends BaseController {
    @Autowired
    private IExternalUserService externalUserService;

    /**
     * 根据内部商户ID获取详情
     *
     * @param merchantId
     * @return
     */
    @GetMapping
    @ApiOperation(value = "根据内部商户ID获取详情")
    public JsonResults getExternalUser(Long merchantId) {
        ExternalUserEntity entity = externalUserService.getUser(merchantId);
        return JsonResults.success(entity);
    }

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增信息")
    public JsonResults add(@Validated @RequestBody ExternalUserEntity entity) {
        List externalUser = externalUserService.list(new QueryWrapper<ExternalUserEntity>().lambda()
                .eq(ExternalUserEntity::getMerchantId, entity.getMerchantId())
                .eq(ExternalUserEntity::getDelFlag, "0"));
        if (externalUser.size() > 0) {
            return JsonResults.error("重复增加");
        }
        entity.setDelFlag("0");
        entity.setCreateBy(SecurityUtils.getUserName());
        entity.setCreateTime(DateUtils.getNowDate());
        externalUserService.save(entity);
        return JsonResults.success();
    }

    /**
     * 修改
     *
     * @param entity
     * @return
     */
    @PutMapping
    @ApiOperation(value = "修改信息")
    public JsonResults edit(@Validated @RequestBody ExternalUserEntity entity) {
        entity.setUpdateBy(SecurityUtils.getUserName());
        entity.setUpdateTime(DateUtils.getNowDate());
        externalUserService.saveOrUpdate(entity);
        return JsonResults.success();
    }

    /**
     * 分页查询
     *
     * @param entity
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "分页查询")
    public JsonResults list(ExternalUserEntity entity) {
        return JsonResults.success(externalUserService.pageList(startPage(), entity));
    }

    /**
     * 明细
     *
     * @param
     * @return
     */
    @GetMapping(value = "/{id}")
    @ApiOperation(value = "根据ID获取明细")
    public JsonResults getInfo(@PathVariable Integer id) {
        return JsonResults.success(externalUserService.getById(id));
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据ID删除明细")
    public JsonResults remove(@PathVariable Integer id) {
        ExternalUserEntity entity = externalUserService.getById(id);
        entity.setDelFlag("1");
        entity.setUpdateBy(SecurityUtils.getUserName());
        entity.setUpdateTime(new Date());
        externalUserService.saveOrUpdate(entity);
        return JsonResults.success();
    }

    /**
     * 条件查询
     *
     * @param entity
     * @return
     */
    @PostMapping("/listAll")
    @ApiOperation(value = "条件查询")
    public JsonResults getExternalUser(@RequestBody ExternalUserEntity entity) {
        return JsonResults.success(externalUserService.list(new QueryWrapper<ExternalUserEntity>().setEntity(entity)));
    }

    /**
     * 根据外部商户ID查询
     *
     * @param externalId
     * @return
     */
    @GetMapping("/getMerchantId")
    @ApiOperation(value = "根据外部商户ID查询")
    public JsonResults getByExternalId(String externalId) {
        return JsonResults.success(externalUserService.getByExternalId(externalId));
    }
}
