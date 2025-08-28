package com.dfc.ind.service.dataapi;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.dataapi.DataApiInfoEntity;
import com.dfc.ind.entity.dataapi.param.LoadApiDataToRedisParam;
import com.dfc.ind.entity.dataapi.vo.ApiInfoCatchDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huff
 * @since 2022-09-08
 */
public interface IDataApiInfoService extends IService<DataApiInfoEntity> {


    JsonResults saveToDataApi(HttpServletRequest request, List<ApiInfoCatchDTO> list,Boolean isAll);
     JsonResults loadRedis( LoadApiDataToRedisParam param) ;

    Object apiServer(String apiPath, HttpServletRequest request) throws Exception;

    String authorize(String applicationCode, String secret) throws Exception;

    JsonResults innitResource();

    JsonResults saveAllToDataApiByAppId(String applicationCode);

    JsonResults clearRedis();

    JsonResults pageAll(Page startPage, DataApiInfoEntity entity);


    JsonResults innitResourceByAppId(String appId);

    JsonResults getMetaData(LoadApiDataToRedisParam param);
}
