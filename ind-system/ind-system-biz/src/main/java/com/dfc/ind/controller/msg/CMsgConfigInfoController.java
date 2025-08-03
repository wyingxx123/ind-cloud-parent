package com.dfc.ind.controller.msg;

import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.msg.CMsgConfigInfoEntity;
import com.dfc.ind.service.msg.ICMsgConfigInfoService;
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

/**
 * <p>
 * 描述: 本类为消息配置表的前端控制器
 * </p>
 *
 * @author nwy
 * @date 2020-04-27
 * @copyright 武汉数慧享智能科技有限公司
 */
@Api(tags = "消息配置表接口")
@RestController
@RequestMapping("/cMsgConfigInfo")
public class CMsgConfigInfoController extends BaseController {

    @Autowired
    private ICMsgConfigInfoService service;

    /**
     * 消息配置表 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody CMsgConfigInfoEntity entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        entity.setCreateBy(SecurityUtils.getUserName());
        service.save(entity);
        return JsonResults.success(entity);
    }

    /**
     * 消息配置表 删除
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long")
    public JsonResults del(@PathVariable Long id) {
        return toResult(service.removeById(id));
    }

    /**
     * 消息配置表 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody CMsgConfigInfoEntity entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        entity.setUpdateBy(SecurityUtils.getUserName());
        service.updateById(entity);
        return JsonResults.success();
    }

    /**
     * 消息配置表 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        CMsgConfigInfoEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("id不存在!");
    }


    /**
     * 消息配置表 分页查询
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
    public JsonResults list(CMsgConfigInfoEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.page(startPage(), new QueryWrapper<CMsgConfigInfoEntity>().setEntity(entity)));
    }
}
