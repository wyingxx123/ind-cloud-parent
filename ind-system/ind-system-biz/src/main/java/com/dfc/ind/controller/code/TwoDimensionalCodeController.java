package com.dfc.ind.controller.code;




import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.code.TwoDimensionalCodeEntity;
import com.dfc.ind.service.code.ITwoDimensionalCodeService;
import com.dfc.ind.service.sys.ISysCodeInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 描述: 本类为二维码信息的前端控制器
 * </p>
 *
 * @author dingw
 * @date 2020-09-16
 * @copyright 武汉数慧享智能科技有限公司
 */
@Api(tags = "二维码信息接口")
@RestController
@RequestMapping("/dimensionalCode")
public class TwoDimensionalCodeController extends BaseController {

    @Autowired
    private ITwoDimensionalCodeService service;

    @Autowired
    private ISysCodeInfoService sysCodeInfoService;

    /**
     * 二维码信息 新增
     *
     * @param entity
     */
    @PostMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody TwoDimensionalCodeEntity entity) {
        return service.saveCode(entity);
    }

    /**
     * 二维码信息 删除
     *
     * @param id
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除")
    @ApiImplicitParam(name = "id", value = "二维码编号", required = true, dataType = "String")
    public JsonResults del(@PathVariable String id) {
        return toResult(service.removeById(id));
    }

    /**
     * 二维码信息 修改
     *
     * @param entity
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody TwoDimensionalCodeEntity entity) {
        return service.updateCode(entity);
    }

    /**
     * 二维码信息 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "二维码编号", required = true, dataType = "String")
    public JsonResults getById(@PathVariable String id) {
        TwoDimensionalCodeEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("二维码编号不存在!");
    }

    /**
     * 二维码信息  查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    public JsonResults listAll(TwoDimensionalCodeEntity entity) {
        return JsonResults.success(service.list(new QueryWrapper<TwoDimensionalCodeEntity>().setEntity(entity)));
    }

    /**
     * 二维码信息 分页查询
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
    public JsonResults list(TwoDimensionalCodeEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {
        return JsonResults.success(service.page(startPage(), entity));
    }

    /**
     * 导出二维码信息
     *
     * @param entity 对象
     */
    @ApiOperation(value = "导出")
    @PostMapping("/export")
    public void export(HttpServletResponse response, TwoDimensionalCodeEntity entity) throws IOException
    {
        List<TwoDimensionalCodeEntity> list =service.list(new QueryWrapper<TwoDimensionalCodeEntity>().setEntity(entity));
        ExcelUtil<TwoDimensionalCodeEntity> util = new ExcelUtil<TwoDimensionalCodeEntity>(TwoDimensionalCodeEntity.class);
        util.exportExcel(response, list, "二维码信息");
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
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "merchantId", value = "商户id", required = true)
    })
    public JsonResults codeAccess(String codeUrl,Long userId,String merchantId){
        String s = "&";
        if (codeUrl.contains(s)) {
            String[] str = codeUrl.split("&");
            if (str.length > 1) {
                byte[] decode = Base64.decodeBase64(str[1].getBytes());
                String codeNo = new String(decode);
                if(codeNo.contains("QR")){
                    return service.codeAccess(codeUrl,userId, merchantId);
                }else if(codeNo.contains("EWM")){
                    return sysCodeInfoService.codeAccess(codeUrl,userId, merchantId);
                }
            }
        }
       return JsonResults.error("二维码错误!");
    }


}
