package com.dfc.ind.gateway.service.impl;

import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;

import com.dfc.ind.entity.sys.SysMenu;
import com.dfc.ind.gateway.entity.FiltersterVo;
import com.dfc.ind.gateway.mapper.FiltersterMapper;
import com.dfc.ind.gateway.service.FilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilterServiceImpl implements FilterService {


    @Autowired
    private FiltersterMapper filtersterMapper;



    @Override
    public List<String> selectByUserId(String userId,String merchantId) {
        return filtersterMapper.selectByUserId(userId,merchantId);
    }

    @Override
    public int selectByurlName(String path) {

        return filtersterMapper.selectByurlName(path);
    }

    @Override
    public int selectByPathAndRoleId(String path, String roleId) {
        return filtersterMapper.selectByPathAndRoleId(path,roleId);
    }


    @Override
    public List<SysMenu> selectByroldormulis(String roleId, String merchantId) {
        return filtersterMapper.selectByroldormulis(roleId,merchantId);
    }

    @Override
    public List<String> selectByroldormuliscount(String roleId, String merchantId, String path) {
        return filtersterMapper.selectByroldormuliscount(roleId,merchantId,path);
    }

    @Override
    public int selectBymunename(String muenname, String roleId, String merchantId) {
        return filtersterMapper.selectBymunename(muenname,roleId,merchantId);
    }

    @Override
    public int selectBytypeorroleId(String substring, String roleId,String path) {
        return filtersterMapper.selectBytypeorroleId(substring,roleId,path);
    }


    /**
     * 查询三张表数据
    * */
    @Override
    public Map lists(FiltersterVo filtersterVo) {
        Map  map = new HashMap();
        filtersterVo.setPageNum((filtersterVo.getPageNum() - 1) * filtersterVo.getPageSize());
   List<FiltersterVo>  filtersterVos =filtersterMapper.lists(filtersterVo);
        for (int i = 0; i <filtersterVos.size() ; i++) {
            filtersterVos.get(i).setPathtype(filtersterVo.getPathtype());
           }
         map.put("data",filtersterVos);
        filtersterVo.setPageSize(0);
        List<FiltersterVo>  total =filtersterMapper.lists(filtersterVo);

        map.put("total",total.size());
        return map;
    }

    @Override
    public JsonResults addrebo(FiltersterVo filtersterVo) {

        filtersterVo.setCreateTime(DateUtils.getNowDate());
        return JsonResults.success(filtersterMapper.addrebo(filtersterVo));
    }

    @Override
    public FiltersterVo selectOne(FiltersterVo filtersterVo) {
        return filtersterMapper.selectOne(filtersterVo);
    }

    @Override
    public JsonResults deteleOne(FiltersterVo filtersterVo) {
        return JsonResults.success(filtersterMapper.deteleOne(filtersterVo));
    }

    @Override
    public JsonResults updateRul(FiltersterVo filtersterVo) {
        filtersterMapper.deteleOne(filtersterVo);

        return JsonResults.success( filtersterMapper.addrebo(filtersterVo));
    }




}
