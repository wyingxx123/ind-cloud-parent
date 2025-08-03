package com.dfc.ind.controller.station;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.station.StationRelatedUserEntity;
import com.dfc.ind.service.station.IStationRelatedUserService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 描述: 工位用户关联表的前端控制器
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/9/2
 * @copyright 武汉数慧享智能科技有限公司
 */
@Api(tags = "工位用户关联表接口")
@RestController
@RequestMapping("/stationRelatedUser")
public class StationRelatedUserController extends BaseController {

    @Autowired
    private IStationRelatedUserService service;

    @PutMapping
    @ApiOperation(value = "新增")
    public JsonResults add(@RequestBody StationRelatedUserEntity entity) {
        return service.add(entity);
    }

    @PostMapping
    @ApiOperation(value = "修改")
    public JsonResults update(@RequestBody StationRelatedUserEntity entity) {
        return service.update(entity);
    }

    @DeleteMapping
    @ApiOperation("删除")
    public JsonResults delete(@RequestParam String stationNo) {
        if (service.removeById(stationNo)) {
            return JsonResults.success("删除成功");
        }
        return JsonResults.error("删除失败");
    }

    @DeleteMapping("/batchDelete")
    @ApiOperation("批量删除")
    public JsonResults batchDeleteByIds(@RequestParam String stationNos) {
        return service.deleteByIds(stationNos);
    }


    @GetMapping("/{id}")
    @ApiOperation("明细")
    public JsonResults getById(String stationNo) {
        return JsonResults.success(service.getById(stationNo));
    }

    @GetMapping("/codeAccess")
    @ApiOperation("二维码扫描")
    public JsonResults codeAccess(String codeUrl, Long userId, String merchantId) {
        return service.codeAccess(codeUrl, userId, merchantId);
    }

    @PutMapping("/createCode")
    @ApiOperation("生成二维码")
    public JsonResults createCode(String codeNo) {
        return service.createCode(codeNo);
    }

    @GetMapping("/getByIds")
    @ApiModelProperty("根据多个id获取明细")
    public JsonResults getByIds(String stationNos) {
        return service.getByIds(stationNos);
    }

    @GetMapping("/getStation")
    @ApiModelProperty("根据角色和用户获取工位")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roleId", value = "角色id", required = true),
            @ApiImplicitParam(name = "userId", value = "用户id", required = true),
            @ApiImplicitParam(name = "merchantId", value = "商户id", required = true)
    })
    public JsonResults getStation(String userId, String merchantId,String roleId){
        return service.getStation(userId,merchantId,roleId);
    }
    
    @PutMapping("/createCodeNew")
    @ApiOperation("生成二维码")
    public JsonResults createCodeNew(String codeNo, String codeType) {
        return service.createCode(codeNo, codeType);
    }
}
