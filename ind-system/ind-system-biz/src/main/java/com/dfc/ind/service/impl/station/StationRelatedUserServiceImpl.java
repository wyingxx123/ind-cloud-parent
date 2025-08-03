package com.dfc.ind.service.impl.station;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;


import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfc.ind.common.core.constant.HttpStatus;
import com.dfc.ind.common.core.text.Convert;
import com.dfc.ind.common.core.utils.AutoGenerateNo;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.station.StationRelatedUserEntity;
import com.dfc.ind.entity.sys.SysRole;
import com.dfc.ind.mapper.station.StationRelatedUserMapper;
import com.dfc.ind.service.station.IStationRelatedUserService;
import com.dfc.ind.service.sys.ISysRoleService;
/**
 * <p>
 * 描述: 工位用户关联信息表 服务实现类
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/9/3
 * @copyright 武汉数慧享智能科技有限公司
 */
@Service
public class StationRelatedUserServiceImpl extends ServiceImpl<StationRelatedUserMapper, StationRelatedUserEntity> implements IStationRelatedUserService {


    @Autowired
    private ISysRoleService roleService;

    @Autowired
    public JdbcTemplate jdbcTemplate;

    public String getSequence(String key) {
        // 下一个序列
        String netStr = key + AutoGenerateNo.getStrDate(new Date())
                + jdbcTemplate.queryForObject(AutoGenerateNo.map.get(key), String.class);
        return netStr;
    }

    @Override
    //@GlobalTransactional
    public JsonResults add(StationRelatedUserEntity entity) {
        if (StringUtils.isNotEmpty(entity.getMerchantId())) {
            if (StringUtils.isNotEmpty(entity.getStationNo())) {
                if (StringUtils.isNotEmpty(entity.getStationType())) {
                    if (StringUtils.isNotEmpty(entity.getStationName())) {
                        if (StringUtils.isNotEmpty(entity.getUserId()) || StringUtils.isNotEmpty(entity.getRoleId())) {
                            if (StringUtils.isNotEmpty(entity.getStates())) {
                                entity.setCreateTime(DateUtils.getNowDate());
                                entity.setCreateBy(SecurityUtils.getUserName());
                                entity.setCodeNo(getSequence(AutoGenerateNo.EWM_TITLE));
                                if (this.save(entity)) {
                                    return JsonResults.success("新增成功!");
                                } else {
                                    return JsonResults.error("新增失败!");
                                }
                            } else {
                                return JsonResults.error("状态不能为空!");
                            }
                        } else {
                            return JsonResults.error("角色范围或者指定人员范围不能为空!");
                        }
                    } else {
                        return JsonResults.error("工位名不能为空!");
                    }
                } else {
                    return JsonResults.error("工位类型不能为空!");
                }
            } else {
                return JsonResults.error("工位号不能为空!");
            }
        } else {
            return JsonResults.error("工厂id不能为空!");
        }
    }

    @Override
   //@GlobalTransactional
    public JsonResults update(StationRelatedUserEntity entity) {
        if (StringUtils.isNotNull(entity.getStationNo())) {
            if (StringUtils.isNotEmpty(entity.getMerchantId())) {
                if (StringUtils.isNotEmpty(entity.getStationType())) {
                    if (StringUtils.isNotEmpty(entity.getUserId()) || StringUtils.isNotEmpty(entity.getRoleId())) {
                        if (StringUtils.isNotEmpty(entity.getStates())) {
                            entity.setUpdateTime(DateUtils.getNowDate());
                            entity.setUpdateBy(SecurityUtils.getUserName());
                            if (null == this.getById(entity.getStationNo())) {
                                JsonResults jsonResults = this.add(entity);
                                if (HttpStatus.SUCCESS == (int) jsonResults.get("code") || null != jsonResults.get("data")) {
                                    if (baseMapper.updateById(entity) > 0) {
                                        return JsonResults.success("修改成功!");
                                    } else {
                                        return JsonResults.error("修改失败!");
                                    }
                                }
                            }
                            if (baseMapper.updateById(entity) > 0) {
                                return JsonResults.success("修改成功!");
                            } else {
                                return JsonResults.error("修改失败!");
                            }
                        } else {
                            return JsonResults.error("状态不能为空！");
                        }
                    } else {
                        return JsonResults.error("角色范围或者指定人员范围不能为空!");
                    }
                } else {
                    return JsonResults.error("工位类型不能为空!");
                }
            } else {
                return JsonResults.error("工厂id不能为空!");
            }
        } else {
            return JsonResults.error("工位号不能为空");
        }
    }

    @Override
   //@GlobalTransactional
    public JsonResults createCode(String codeNo) {
        if (StringUtils.isNotEmpty(codeNo)) {
            StationRelatedUserEntity entity = this.getOne(new QueryWrapper<StationRelatedUserEntity>().lambda()
                    .eq(StationRelatedUserEntity::getCodeNo, codeNo)
                    .eq(StationRelatedUserEntity::getDelFlg, "0"));
            if (null != entity) {
                StringBuffer codeUrl = new StringBuffer();
                codeUrl.append(codeNo);
                byte[] encode = Base64.encodeBase64(codeUrl.toString().getBytes());
                entity.setCodeUrl("http://192.168.4.47:8080/&" + new String(encode));
                entity.setUpdateBy(SecurityUtils.getUserName());
                entity.setUpdateTime(DateUtils.getNowDate());
                if (this.updateById(entity)) {
                    return JsonResults.success("操作成功", entity.getCodeUrl());
                } else {
                    return JsonResults.error("操作失败!");
                }
            }
        }
        return JsonResults.error("获取二维码失败!");
    }

    @Override
    public JsonResults codeAccess(String codeUrl, Long userId, String merchantId) {
        String sign = "&";
        if (codeUrl.contains(sign)) {
            String[] str = codeUrl.split("&");
            if (str.length > 1) {
                byte[] decode = Base64.decodeBase64(str[1].getBytes());
                String codeNo = new String(decode);
                String codeType = "";
                if (codeNo.indexOf("-") > 0) {
                	codeType = codeNo.split("-")[1];
                	codeNo = codeNo.split("-")[0];
                }
                StationRelatedUserEntity entity = this.getOne(new QueryWrapper<StationRelatedUserEntity>().lambda()
                        .eq(StationRelatedUserEntity::getCodeNo, codeNo)
                        .eq(StationRelatedUserEntity::getDelFlg, "0")
                        .eq(StationRelatedUserEntity::getStates, "00"));
                if (entity != null) {
                    if (StringUtils.isNotEmpty(entity.getRoleId()) || StringUtils.isNotEmpty(entity.getUserId())) {
                        if (!merchantId.equals(entity.getMerchantId())) {
                            return JsonResults.error("没有该二维码访问权限!");
                        }
                        entity.setCodeType(codeType);
                        String adminId = "0";
                        if (adminId.equals(entity.getMerchantId())) {
                            return JsonResults.success(entity);
                        }
                        Set<String> roles = roleService.selectRoleByUserId(userId);
                        SysRole roleEntity = roleService.getOne(new QueryWrapper<SysRole>().lambda()
                                .eq(SysRole::getRoleName, "商户管理员").eq(SysRole::getMerchantId, SecurityUtils.getLoginUser().getMerchantId()));
                        if (null != roles && roles.size() > 0) {
                            //判断是否为商户管理员
                            if (roles.contains(roleEntity.getRoleId().toString())) {
                                return JsonResults.success(entity);
                            }
                            List<String> roleList = Arrays.asList(entity.getRoleId().split(","));
                            for (String role : roles) {
                                if (roleList.contains(String.valueOf(role))) {
                                    return JsonResults.success(entity);
                                }
                            }
                        }
                        List<String> userList = Arrays.asList(entity.getUserId().split(","));
                        if (userList.contains(String.valueOf(userId))) {
                            return JsonResults.success(entity);
                        }
                        return JsonResults.error("没有该二维码访问权限!");
                    }
                }
            }
        }
        return JsonResults.error("二维码错误!");
    }


    @Override
    public JsonResults getByIds(String stationNos) {
        List<String> ids = Arrays.asList(stationNos.split(","));
        return JsonResults.success(baseMapper.getByIds(ids));
    }

    @Override
    public JsonResults deleteByIds(String stationNos) {
        List<String> stationNoList = Arrays.asList(stationNos.split(","));
        List<String> successList = new ArrayList<>();
        for (String stationNo :
                stationNoList) {
            if (null != this.getById(stationNo)) {
                if (this.removeById(stationNo)) {
                    successList.add(stationNo);
                }
            }
        }
        return JsonResults.success(successList);
    }


    @Override
    public JsonResults getStation(String userId, String merchantId, String roleId) {
        SysRole roleEntity = roleService.getOne(new QueryWrapper<SysRole>().lambda()
                .eq(SysRole::getRoleName, "商户管理员").eq(SysRole::getMerchantId, SecurityUtils.getLoginUser().getMerchantId()));
        List<StationRelatedUserEntity> list = new ArrayList<>();
        if (roleEntity != null) {
            QueryWrapper<StationRelatedUserEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(StationRelatedUserEntity::getMerchantId, merchantId)
                    .eq(StationRelatedUserEntity::getDelFlg, "0")
                    .eq(StationRelatedUserEntity::getStates, "00");
            List<String> strList = Arrays.asList(Convert.toStrArray(roleId));
            Boolean bool = false;
            for(String str:strList){
                if (roleEntity.getRoleId().toString().equals(str)) {
                    bool = true;
                    break;
                }
            }
            if (bool) {
                list = list(queryWrapper);
            } else {
                queryWrapper.lambda().and(wrapper -> wrapper.like(StationRelatedUserEntity::getRoleId, roleId)
                                .or()
                                .like(StationRelatedUserEntity::getUserId, userId));
                List<StationRelatedUserEntity> entityList = list(queryWrapper);
                if(entityList != null && entityList.size() > 0){
                    for(StationRelatedUserEntity entity:entityList){
                        if(StringUtils.isNotEmpty(entity.getUserId())){
                            String[] userIds = Convert.toStrArray(entity.getUserId());
                            if(Arrays.asList(userIds).contains(userId)){
                                list.add(entity);
                                continue;
                            }
                        }
                        if(StringUtils.isNotEmpty(entity.getRoleId())){
                            for(String str:strList){
                                String[] roleIds = Convert.toStrArray(entity.getRoleId());
                                if(Arrays.asList(roleIds).contains(str)){
                                    list.add(entity);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return JsonResults.success(list);
    }

    public QueryWrapper<StationRelatedUserEntity> queryWrapper(StationRelatedUserEntity entity) {
        QueryWrapper<StationRelatedUserEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StringUtils.isNotNull(entity.getMerchantId()) && !"0".equals(entity.getMerchantId()), StationRelatedUserEntity::getMerchantId, entity.getMerchantId())
                .eq(StringUtils.isNotEmpty(entity.getStationNo()), StationRelatedUserEntity::getStationNo, entity.getStationNo())
                .eq(StringUtils.isNotEmpty(entity.getStationType()), StationRelatedUserEntity::getStationType, entity.getStationType())
                .eq(StringUtils.isNotEmpty(entity.getDelFlg()), StationRelatedUserEntity::getDelFlg, entity.getDelFlg());
        return wrapper;
    }

    @Override
   //@GlobalTransactional
    public JsonResults createCode(String codeNo, String codeType) {
        StationRelatedUserEntity entity = this.getOne(new QueryWrapper<StationRelatedUserEntity>().lambda()
                .eq(StationRelatedUserEntity::getCodeNo, codeNo)
                .eq(StationRelatedUserEntity::getDelFlg, "0"));
        if (null != entity) {
            StringBuffer codeUrl = new StringBuffer();
            codeUrl.append(codeNo);
            codeUrl.append("-" + codeType);
            byte[] encode = Base64.encodeBase64(codeUrl.toString().getBytes());
            entity.setCodeUrl("http://192.168.4.47:8080/&" + new String(encode));
            entity.setUpdateBy(SecurityUtils.getUserName());
            entity.setUpdateTime(DateUtils.getNowDate());
            if (this.updateById(entity)) {
                return JsonResults.success("操作成功", entity.getCodeUrl());
            } else {
                return JsonResults.error("获取二维码失败!");
            }
        }
        return JsonResults.error("获取二维码失败!");
    }
}
