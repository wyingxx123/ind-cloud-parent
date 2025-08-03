package com.dfc.ind.controller.station.sys;

import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysCodeInfoEntity;
import com.dfc.ind.service.sys.ISysCodeInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 *
 * <p>
 * 描述: 本类为二维码配置信息的前端控制器
 * </p>
 *
 * @author dingw
 * @date 2020-08-25
 * @copyright 武汉数慧享智能科技有限公司
 */
@Api(tags = "二维码配置信息接口")
@RestController
@RequestMapping("/code")
public class SysCodeInfoController extends BaseController {

    @Autowired
    private ISysCodeInfoService service;

    /**
     * 二维码配置信息 新增
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody SysCodeInfoEntity entity){
        return service.saveCode(entity);
    }

    /**
     * 二维码配置信息 删除
     * @param id
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "id", value = "二维码编号", required = true, dataType = "String")
    public JsonResults del(@PathVariable String id){
        return toResult(service.removeById(id));
    }

    /**
     * 二维码配置信息 修改
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody SysCodeInfoEntity entity){
        return service.updateCode(entity);
    }

    /**
     * 二维码配置信息 明细
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "二维码编号", required = true, dataType = "String")
    public JsonResults getById(@PathVariable String id){
        SysCodeInfoEntity entity=service.getById(id);
        if(StringUtils.isNotNull(entity)){
            return JsonResults.success(entity);
        }
        return JsonResults.success("二维码编号不存在!");
    }

    /**
     * 二维码配置信息  查询全部
     *
     * @param entity  对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(SysCodeInfoEntity entity){
        return JsonResults.success(service.list(entity));
    }

    /**
     * 二维码配置信息 分页查询
     * @param entity 对象
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询")
    public JsonResults list(SysCodeInfoEntity entity){
        return JsonResults.success(service.pageList(startPage(),entity));
    }

    /**
     * 导出二维码配置信息
     *
     * @param entity 对象
     */
    @ApiOperation(value = "导出")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysCodeInfoEntity entity) throws IOException
    {
        List<SysCodeInfoEntity> list =service.list(new QueryWrapper<SysCodeInfoEntity>().setEntity(entity));
        ExcelUtil<SysCodeInfoEntity> util = new ExcelUtil<SysCodeInfoEntity>(SysCodeInfoEntity.class);
        util.exportExcel(response, list, "二维码配置信息");
    }

    /**
     * 生成二维码url
     *
     * @param codeNo
     * @return
     */
    @PutMapping("/createCode/{codeNo}")
    @ApiOperation(value = "生成二维码url")
    @ApiImplicitParam(name = "codeNo", value = "二维码编号", required = true, dataType = "String")
    public JsonResults createCode(@PathVariable String codeNo){
        return service.createCode(codeNo);
    }

    /**
     * 扫码访问
     *
     * @param codeUrl
     * @param userId
     * @param merchantId
     * @return
     */
    @GetMapping("/codeAccess")
    @ApiOperation(value = "扫码访问")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "codeUrl", value = "二维码地址", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
            @ApiImplicitParam(name = "merchantId", value = "商户id", required = true)
    })
    public JsonResults codeAccess(String codeUrl,Long userId,String merchantId){
        return service.codeAccess(codeUrl,userId, merchantId);
    }

}
