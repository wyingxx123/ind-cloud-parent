package com.dfc.ind.service.impl.sys;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfc.ind.common.core.text.Convert;
import com.dfc.ind.common.core.utils.AutoGenerateNo;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.sys.SysAppMenuEntity;
import com.dfc.ind.entity.sys.SysCodeInfoEntity;
import com.dfc.ind.entity.sys.SysRole;
import com.dfc.ind.mapper.sys.SysAppRecordMapper;
import com.dfc.ind.mapper.sys.SysCodeInfoMapper;
import com.dfc.ind.mapper.sys.SysUserRoleMapper;
import com.dfc.ind.service.sys.ISysAppMenuService;
import com.dfc.ind.service.sys.ISysCodeInfoService;
import com.dfc.ind.service.sys.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import java.util.*;

/**
 * <p>
 * 二维码配置信息 服务实现类
 * </p>
 *
 * @author dingw
 * @since 2020-08-25
 */
@Service
public class SysCodeInfoServiceImpl extends ServiceImpl<SysCodeInfoMapper, SysCodeInfoEntity> implements ISysCodeInfoService {
    @Autowired
    private ISysAppMenuService sysAppMenuService;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private SysAppRecordMapper sysAppRecordMapper;

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
    @Transactional
    public JsonResults saveCode(SysCodeInfoEntity entity) {
        if (StringUtils.isNotEmpty(entity.getMerchantId())) {
            if (StringUtils.isNotEmpty(entity.getCodeMenu())) {
                if (StringUtils.isNotEmpty(entity.getCodeRole()) || StringUtils.isNotEmpty(entity.getCodeUser())) {
                    if (StringUtils.isNotNull(entity.getEffectiveTime())) {
                        entity.setCodeNo(getSequence(AutoGenerateNo.EWM_TITLE));
                        entity.setCreateTime(DateUtils.getNowDate());
                        entity.setCreateBy(SecurityUtils.getUserName());
                        if (this.save(entity)) {
                            return JsonResults.success("新增成功!");
                        } else {
                            return JsonResults.error("新增失败!");
                        }
                    } else {
                        return JsonResults.error("有效期不能为空!");
                    }
                } else {
                    return JsonResults.error("角色范围或者指定人员范围不能为空!");
                }
            } else {
                return JsonResults.error("菜单权限不能为空!");
            }
        } else {
            return JsonResults.error("工厂id不能为空!");
        }
    }


    @Override
    @Transactional
    public JsonResults updateCode(SysCodeInfoEntity entity) {
        if (StringUtils.isNotEmpty(entity.getCodeNo())) {
            if (StringUtils.isNotEmpty(entity.getMerchantId())) {
                if (StringUtils.isNotEmpty(entity.getCodeMenu())) {
                    if (StringUtils.isNotEmpty(entity.getCodeRole()) || StringUtils.isNotEmpty(entity.getCodeUser())) {
                        if (StringUtils.isNotNull(entity.getEffectiveTime())) {
                            entity.setUpdateBy(SecurityUtils.getUserName());
                            entity.setUpdateTime(DateUtils.getNowDate());
                            SysCodeInfoEntity sysCodeInfoEntity = getById(entity.getCodeNo());
                            List<Long> menuList = Arrays.asList(Convert.toLongArray(sysCodeInfoEntity.getCodeMenu()));
                            List<Long> menuList2 = Arrays.asList(Convert.toLongArray(entity.getCodeMenu()));
                            List<Long> list = new ArrayList<>();
                            for (Long menuId : menuList) {
                                if (!menuList2.contains(menuId)) {
                                    list.add(menuId);
                                }
                            }
                            if (list != null && list.size() > 0) {
                                List<String> permsList = new ArrayList<>();
                                for(Long menuId:list){
                                    permsList.add(sysAppMenuService.getById(menuId).getPerms());
                                }
                                String[] perms = (String[]) permsList.toArray();
                                Long [] roleIds = Convert.toLongArray(sysCodeInfoEntity.getCodeRole());
                                deleteRecord(roleIds,sysCodeInfoEntity.getCodeUser(),perms);
                            }
                            if (this.updateById(entity)) {
                                return JsonResults.success("修改成功");
                            } else {
                                return JsonResults.error("修改失败!");
                            }
                        } else {
                            return JsonResults.error("有效期不能为空!");
                        }
                    } else {
                        return JsonResults.error("角色范围或者指定人员范围不能为空!");
                    }
                } else {
                    return JsonResults.error("菜单权限不能为空!");
                }
            } else {
                return JsonResults.error("工厂id不能为空!");
            }
        } else {
            return JsonResults.error("二维码主键不能为空!");
        }
    }

    @Override
    @Transactional
    public JsonResults createCode(String codeNo) {
        if (StringUtils.isNotEmpty(codeNo)) {
            SysCodeInfoEntity entity = this.getById(codeNo);
            if (entity != null) {
                StringBuffer codeUrl = new StringBuffer();
                codeUrl.append(codeNo);
               byte[] encode = Base64Utils.encode(codeUrl.toString().getBytes());


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
    @Transactional
    public JsonResults codeAccess(String codeUrl, Long userId, String merchantId) {
        String s = "&";
        if (codeUrl.contains(s)) {
            String[] str = codeUrl.split("&");
            if (str.length > 1) {
                byte[] decode = Base64Utils.decode(str[1].getBytes());
                String codeNo = new String(decode);
                SysCodeInfoEntity entity = this.getById(codeNo);
                if (entity != null) {
                    if (StringUtils.isNotEmpty(entity.getCodeMenu()) && StringUtils.isNotEmpty(entity.getCodeRole())
                            && StringUtils.isNotNull(entity.getEffectiveTime())) {
                        if (entity.getEffectiveTime().compareTo(new Date()) < 0) {
                            return JsonResults.error("二维码已过期!");
                        }
                        if (!merchantId.equals(entity.getMerchantId())) {
                            return JsonResults.error("没有该二维码访问权限!");
                        }
                        List<Integer> roles = roleService.selectRoleListByUserId(userId);
                        SysRole roleEntity = roleService.getOne(new QueryWrapper<SysRole>().lambda()
                                .eq(SysRole::getRoleName, "商户管理员"));
                        Map<String, Object> map = new HashMap<>();
                        map.put("longitude", null);
                        map.put("latitude", null);
                        map.put("effectiveRange", null);
                        if (roles != null && roles.size() > 0) {
                            for (Integer role : roles) {
                                if (roleEntity != null) {
                                    //判断是否为商户管理员
                                    if (role.longValue() == roleEntity.getRoleId()) {
                                        List<SysAppMenuEntity> menuList = sysAppMenuService.getByMenu(entity.getCodeMenu());
                                        map.put("menuList", menuList);
                                        return JsonResults.success(map);
                                    } else {
                                        Long[] roleIds = Convert.toLongArray(entity.getCodeRole());
                                        if (Arrays.asList(roleIds).contains(role.longValue())) {
                                            List<SysAppMenuEntity> menuList = sysAppMenuService.getByMenu(entity.getCodeMenu());
                                            map.put("menuList", menuList);
                                            return JsonResults.success(map);
                                        }
                                    }
                                }
                            }
                        }
                        return JsonResults.error("没有该二维码访问权限!");
                    }
                }
            }
        }
        return JsonResults.error("二维码错误!");
    }

    private int deleteRecord(Long[] roleIds, String ids, String[] perms) {
        List<Long> userList = sysUserRoleMapper.getByRoleId(roleIds);
        if (StringUtils.isNotEmpty(ids)) {
            List<Long> list = Arrays.asList(Convert.toLongArray(ids));
            userList.addAll(list);
        }
        Long[] userIds = (Long[]) userList.toArray();
        return sysAppRecordMapper.deleteByUserPerms(userIds, perms);
    }

    @Override
    public IPage pageList(Page startPage, SysCodeInfoEntity entity) {
        return this.page(startPage, this.queryWrapper(entity));
    }


    @Override
    public List<SysCodeInfoEntity> list(SysCodeInfoEntity entity) {
        return list(queryWrapper(entity));
    }

    public QueryWrapper<SysCodeInfoEntity> queryWrapper(SysCodeInfoEntity entity) {
        QueryWrapper<SysCodeInfoEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StringUtils.isNotEmpty(entity.getCodeStatus()), SysCodeInfoEntity::getCodeStatus, entity.getCodeStatus())
                .eq(StringUtils.isNotEmpty(entity.getCodeType()), SysCodeInfoEntity::getCodeType, entity.getCodeType())
                .eq(StringUtils.isNotEmpty(entity.getMerchantId()), SysCodeInfoEntity::getMerchantId, entity.getMerchantId())
                .eq(StringUtils.isNotEmpty(entity.getDelFlg()), SysCodeInfoEntity::getDelFlg, entity.getDelFlg())
                .ge(null != entity.getBeginDate(), SysCodeInfoEntity::getEffectiveTime, entity.getBeginDate())
                .le(null != entity.getEndDate(), SysCodeInfoEntity::getEffectiveTime, entity.getEndDate());
        return wrapper;
    }
}
