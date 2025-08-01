package com.dfc.ind.gateway.service;

import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.entity.sys.SysMenu;
import com.dfc.ind.gateway.entity.FiltersterVo;

import java.util.List;
import java.util.Map;

public interface FilterService {
    List<String> selectByUserId(String username,String merchantId);

    int  selectByurlName(String path);

    int selectByPathAndRoleId(String path, String roleId);


    List<SysMenu> selectByroldormulis(String roleId, String merchantId);

    List<String> selectByroldormuliscount(String roleId, String merchantId, String path);


    int selectBymunename(String muenname, String roleId, String merchantId);

    int selectBytypeorroleId(String substring, String roleId,String path);

    Map lists(FiltersterVo filtersterVo);

    JsonResults addrebo(FiltersterVo filtersterVo);

    FiltersterVo selectOne(FiltersterVo filtersterVo);

    JsonResults updateRul(FiltersterVo filtersterVo);

    JsonResults deteleOne(FiltersterVo filtersterVo);
}
