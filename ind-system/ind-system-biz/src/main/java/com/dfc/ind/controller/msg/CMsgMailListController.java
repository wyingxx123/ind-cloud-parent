package com.dfc.ind.controller.msg;


import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.msg.CMsgMailListEntity;
import com.dfc.ind.service.msg.ICMsgMailListService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 描述: 本类为的前端控制器
 * </p>
 *
 * @author nwy
 * @date 2020-04-27
 * @copyright 武汉数慧享智能科技有限公司
 */
@Api(tags = "消息内容明细接口")
@RestController
@RequestMapping("/cMsgMailList")
public class CMsgMailListController extends BaseController {

    @Autowired
    private ICMsgMailListService service;

    /**
     * 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody CMsgMailListEntity entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        entity.setCreateBy(SecurityUtils.getUserName());
        service.save(entity);
        return JsonResults.success(entity);
    }

    /**
     * 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody CMsgMailListEntity entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        entity.setUpdateBy(SecurityUtils.getUserName());
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
    @ApiImplicitParam(name = "id", value = "", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        CMsgMailListEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("不存在!");
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
    public JsonResults list(CMsgMailListEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        String username = SecurityUtils.getUserName();
        if (username.equals("admin")) {
            return JsonResults.success(service.page(startPage(), new QueryWrapper<CMsgMailListEntity>().setEntity(entity)));
        }
        return JsonResults.success(service.page(startPage(), new QueryWrapper<CMsgMailListEntity>().setEntity(entity)
                .lambda().eq(CMsgMailListEntity::getUserName, username)
        ));
    }

    /**
     * 统计消息类型个数
     *
     * @param type
     */
    @GetMapping(value = {"/count/{type}", "/count"})
    @ApiOperation(value = "统计消息类型个数")
    public JsonResults count(@PathVariable(value = "type", required = false) String type) {
        if (type != null && type != "") {
            return JsonResults.success(service.count(new QueryWrapper<CMsgMailListEntity>().lambda()
                    .eq(CMsgMailListEntity::getType, type)
                    .eq(CMsgMailListEntity::getIsRead, 0)
                    .eq(CMsgMailListEntity::getUserName, SecurityUtils.getUserName())
            ));
        }
        return JsonResults.success(service.count(new QueryWrapper<CMsgMailListEntity>().lambda()
                .eq(CMsgMailListEntity::getIsRead, 0)
                .eq(CMsgMailListEntity::getUserName, SecurityUtils.getUserName())
        ));
    }

}
