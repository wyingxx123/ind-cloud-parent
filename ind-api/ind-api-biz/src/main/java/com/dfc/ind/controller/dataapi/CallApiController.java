package com.dfc.ind.controller.dataapi;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.dataapi.param.LoadApiDataToRedisParam;
import com.dfc.ind.entity.dataapi.vo.ApiInfoCatchDTO;
import com.dfc.ind.service.dataapi.IDataApiInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
@Api(tags = "api接口")
@RestController
@RequestMapping("/dpub")
public class CallApiController {

    @Autowired
    private IDataApiInfoService service;



    @PostMapping("/saveToDataApi")
    @ApiOperation(value = "保存到dataapi数据库")
    public JsonResults saveToDataApi(HttpServletRequest request,@RequestBody List<ApiInfoCatchDTO> list) {
        return JsonResults.success(service.saveToDataApi(request,list,false));
    }

    @PostMapping("/saveAllToDataApiByAppId")
    @ApiOperation(value = "api数据同步到dataApi")
    public JsonResults saveAllToDataApiByAppId(@RequestParam String applicationCode) {

        return service.saveAllToDataApiByAppId(applicationCode);
    }

    @PostMapping("/loadRedis")
    @ApiOperation(value = "加载到缓存")
    public JsonResults loadRedis(@RequestBody LoadApiDataToRedisParam param) {

        return service.loadRedis(param);
    }
    @PostMapping("/clearRedis")
    @ApiOperation(value = "清空缓存")
    public JsonResults clearRedis() {

        return service.clearRedis();
    }

    @ApiOperation("授权")
    @PostMapping("/authorize")
    public JsonResults authorize(@Valid @NotBlank String applicationCode, @NotBlank @Length(min = 64, max = 64) String secret) throws Exception {
        String token = service.authorize(applicationCode, secret);
        return JsonResults.success(token);
    }

    @RequestMapping("/apiCall/test666")
    @ApiOperation(value = "api调用")
    public JsonResults test666(HttpServletRequest request) throws Exception{
        return JsonResults.success("service.apiServer(apiPath,request)service.apiServer(apiPath,request)service.apiServer(apiPath,request)service.apiServer(apiPath,request)service.apiServer(apiPath,request)service.apiServer(apiPath,request)serservice.apiServer(apiPath,request)service.apiServer(apiPath,request)service.apiServer(apiPath,request)service.apiServer(apiPath,request)service.apiServer(apiPath,request)service.apiServer(apiPath,request)vice.apiServer(apiPath,request)service.apiServer(apiPath,request)") ;
    }

    @RequestMapping("/apiCall/**")
    @ApiOperation(value = "api调用")
    public JsonResults apiCall(HttpServletRequest request) throws Exception{
        String pathInfo = request.getServletPath();
        String apiPath=pathInfo.split("apiCall/")[1];
        if (apiPath==null){
            return  JsonResults.error("apiPath不能为空") ;
        }
        return JsonResults.success(service.apiServer(apiPath,request)) ;
    }
    @RequestMapping("/innitResourceByAppId")
    @ApiOperation(value = "添加数据连接池测试")
    public JsonResults innitResourceByAppId(@RequestParam String appId) throws Exception {


        return service.innitResourceByAppId(appId);

    }




    @PostMapping("/getMetaData")
    @ApiOperation(value = "获取元模型")
    public JsonResults getMetaData(@RequestBody LoadApiDataToRedisParam param) {

        return service.getMetaData(param);
    }
}
