package com.dfc.ind.controller.station.sys;

import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.sys.SysAppRecordEntity;
import com.dfc.ind.service.sys.ISysAppRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
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
 *
 * <p>
 * 描述: 本类为app访问记录的前端控制器
 * </p>
 *
 * @author dingw
 * @date 2020-09-28
 * @copyright 武汉数慧享智能科技有限公司
 */
@Api(tags = "app访问记录接口")
@RestController
@RequestMapping("/record")
public class SysAppRecordController extends BaseController {

    @Autowired
    private ISysAppRecordService service;

    /**
     * app访问记录 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody SysAppRecordEntity entity) {
        entity.setCreateTime(DateUtils.getNowDate());
        entity.setCreateBy(SecurityUtils.getUserName());
        service.save(entity);
        return JsonResults.success(entity);
    }

    /**
     * app访问记录 删除
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "id", value = "记录id", required = true, dataType = "Long")
    public JsonResults del(@PathVariable Long id) {
        return toResult(service.removeById(id));
    }

    /**
     * app访问记录 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody SysAppRecordEntity entity) {
        entity.setUpdateTime(DateUtils.getNowDate());
        entity.setUpdateBy(SecurityUtils.getUserName());
        service.updateById(entity);
        return JsonResults.success();
    }

    /**
     * app访问记录 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "记录id", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        SysAppRecordEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("记录id不存在!");
    }

    /**
     * app访问记录  查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(SysAppRecordEntity entity) {
        return JsonResults.success(service.listAll(entity));
    }

    /**
     * app访问记录 分页查询
     *
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询")
    public JsonResults list(SysAppRecordEntity entity) {
        return JsonResults.success(service.pageList(startPage(), entity));
    }

    /**
     * app访问记录 新增修改记录
     *
     * @param entity
     */
    @PostMapping("/saveUpdate")
    @ApiOperation(value = "新增修改记录")
    public JsonResults saveUpdate(@RequestBody SysAppRecordEntity entity){
        return toResult(service.saveUpdate(entity));
    }

}
