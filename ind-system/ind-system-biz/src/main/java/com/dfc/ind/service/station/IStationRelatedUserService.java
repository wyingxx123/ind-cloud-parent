package com.dfc.ind.service.station;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.station.StationRelatedUserEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 描述: 工位用户关联信息表 服务类
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/9/2
 * @copyright 武汉数慧享智能科技有限公司
 */
public interface IStationRelatedUserService extends IService<StationRelatedUserEntity> {

    /**
     * 新增工位用户关联信息
     *
     * @param entity
     * @return
     */
    JsonResults add(StationRelatedUserEntity entity);

    /**
     * 修改工位用户关联信息
     *
     * @param entity
     * @return
     */
    JsonResults update(StationRelatedUserEntity entity);


    /**
     * 根据二维码url查询工位用户关联信息
     *
     * @param codeUrl
     * @return
     */
    JsonResults codeAccess(String codeUrl, Long roleId, String merchantId);


    /**
     * 生成二维码
     *
     * @param codeNo
     * @return
     */
    JsonResults createCode(String codeNo);


    /**
     * 根据多个ID获取
     *
     * @param stationNos
     * @return
     */
    JsonResults getByIds(String stationNos);

    /**
     * 批量删除
     *
     * @param stationNos
     * @return
     */
    JsonResults deleteByIds(String stationNos);

    /**
     * 根据角色和用户获取工位
     * @param userId
     * @param merchantId
     * @param roleId
     * @return
     */
    JsonResults getStation(String userId, String merchantId,String roleId);
    
    /**
     * 根据代码类型生成二维码
     *
     * @param codeNo
     * @return
     */
    JsonResults createCode(String codeNo, String codeType);
}
