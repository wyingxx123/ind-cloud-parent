package com.dfc.ind.service.impl.code;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfc.ind.common.core.constant.CommonConstants;
import com.dfc.ind.common.core.text.Convert;
import com.dfc.ind.common.core.utils.AutoGenerateNo;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.code.TwoDimensionalCodeEntity;
import com.dfc.ind.entity.sys.SysAppMenuEntity;
import com.dfc.ind.entity.sys.SysCodeInfoEntity;
import com.dfc.ind.entity.sys.SysRole;
import com.dfc.ind.mapper.code.TwoDimensionalCodeMapper;
import com.dfc.ind.service.code.ITwoDimensionalCodeService;
import com.dfc.ind.service.sys.ISysAppMenuService;
import com.dfc.ind.service.sys.ISysCodeInfoService;
import com.dfc.ind.service.sys.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;


/**
 * <p>
 * 二维码信息 服务实现类
 * </p>
 *
 * @author dingw
 * @since 2020-09-16
 */
@Service
public class TwoDimensionalCodeServiceImpl extends ServiceImpl<TwoDimensionalCodeMapper, TwoDimensionalCodeEntity> implements ITwoDimensionalCodeService {
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysCodeInfoService sysCodeInfoService;

    @Autowired
    private ISysAppMenuService sysAppMenuService;

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
    public JsonResults saveCode(TwoDimensionalCodeEntity entity) {
        if (StringUtils.isEmpty(entity.getMerchantId())) {
            return JsonResults.error("企业编号不能为空!");
        }
        if(CommonConstants.USE.equals(entity.getIsTime()) && StringUtils.isNull(entity.getEffectiveTime())){
            return JsonResults.error("有效时间不能为空!");
        }
        if(CommonConstants.USE.equals(entity.getIsLocation()) && StringUtils.isEmpty(entity.getAddress())){
            return JsonResults.error("地址不能为空!");
        }
        if(CommonConstants.USE.equals(entity.getIsLocation()) && StringUtils.isNull(entity.getEffectiveRange())){
            return JsonResults.error("有效范围不能为空!");
        }
        if(StringUtils.isEmpty(entity.getCodeIds())){
            return JsonResults.error("二维码明细不能为空!");
        }
        entity.setCodeNo(getSequence(AutoGenerateNo.QR_TITLE));
        entity.setCreateTime(DateUtils.getNowDate());
        entity.setCreateBy(SecurityUtils.getUserName());
        if (this.save(entity)) {
            return JsonResults.success("新增成功!");
        } else {
            return JsonResults.error("新增失败!");
        }
    }

    @Override
    @Transactional
    public JsonResults updateCode(TwoDimensionalCodeEntity entity) {
        if(CommonConstants.USE.equals(entity.getIsLocation()) && StringUtils.isEmpty(entity.getAddress())){
            return JsonResults.error("地址不能为空!");
        }
        if(CommonConstants.USE.equals(entity.getIsTime()) && StringUtils.isNull(entity.getEffectiveTime())){
            return JsonResults.error("有效时间不能为空!");
        }
        if(StringUtils.isEmpty(entity.getCodeIds())){
            return JsonResults.error("二维码明细不能为空!");
        }
        if(CommonConstants.USE.equals(entity.getIsLocation()) && StringUtils.isNull(entity.getEffectiveRange())){
            return JsonResults.error("有效范围不能为空!");
        }
        entity.setUpdateTime(DateUtils.getNowDate());
        entity.setUpdateBy(SecurityUtils.getUserName());
        if (this.updateById(entity)) {
            return JsonResults.success("修改成功");
        } else {
            return JsonResults.error("修改失败!");
        }
    }

    @Override
    public JsonResults createCode(String codeNo) {
        if (StringUtils.isNotEmpty(codeNo)) {
            TwoDimensionalCodeEntity entity = this.getById(codeNo);
            if (entity != null) {
                StringBuffer codeUrl = new StringBuffer();
                codeUrl.append(codeNo);
                byte[] encode = Base64.getEncoder().encode(codeUrl.toString().getBytes());
                entity.setCodeUrl("http://47.114.55.11:7002/&" + new String(encode));
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
        String s = "&";
        if (codeUrl.contains(s)) {
            String[] str = codeUrl.split("&");
            if (str.length > 1) {
                byte[] decode = Base64.getDecoder().decode(str[1].getBytes());
                String codeNo = new String(decode);
                TwoDimensionalCodeEntity entity = this.getById(codeNo);
                if (entity != null) {
                    Map<String,Object>  map = new HashMap<>();
                    BigDecimal longitude = null;
                    BigDecimal latitude = null;
                    BigDecimal effectiveRange = null;
                    if(CommonConstants.USE.equals(entity.getIsTime())){
                        if (entity.getEffectiveTime().compareTo(new Date()) < 0) {
                            return JsonResults.error("二维码已过期!");
                        }
                    }
                    if (!merchantId.equals(entity.getMerchantId())) {
                        return JsonResults.error("没有该二维码访问权限!");
                    }
                    if(CommonConstants.USE.equals(entity.getIsLocation())){
                        longitude = entity.getLongitude();
                        latitude = entity.getLatitude();
                        effectiveRange = entity.getEffectiveRange();
                    }
                    map.put("longitude",longitude);
                    map.put("latitude",latitude);
                    map.put("effectiveRange",effectiveRange);
                    List<String> codeList = Arrays.asList(entity.getCodeIds().split(","));
                    List<Integer> roles = roleService.selectRoleListByUserId(userId);
                    SysRole roleEntity = roleService.getOne(new QueryWrapper<SysRole>().lambda()
                            .eq(SysRole::getRoleName, "商户管理员"));
                    List<SysAppMenuEntity> list = new ArrayList<>();
                    for(String codeId:codeList){
                        SysCodeInfoEntity codeInfoEntity = sysCodeInfoService.getById(codeId);
                        if(roleEntity != null){
                            if (roles != null && roles.size() > 0) {
                                for (Integer role : roles) {
                                    if (roleEntity != null) {
                                        //判断是否为商户管理员
                                        if (role.longValue() == roleEntity.getRoleId()) {
                                            list = sysAppMenuService.list(new QueryWrapper<SysAppMenuEntity>().lambda().ge(SysAppMenuEntity::getVisible,"0"));
                                            map.put("menuList",list);
                                            return JsonResults.success(map);
                                        }else{
                                            Long[] roleIds = Convert.toLongArray(codeInfoEntity.getCodeRole());
                                            if (Arrays.asList(roleIds).contains(role.longValue())) {
                                                List<SysAppMenuEntity> menuList = sysAppMenuService.getByMenu(codeInfoEntity.getCodeMenu());
                                                if(menuList != null && menuList.size() > 0){
                                                    list.addAll(menuList);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if(list != null && list.size() > 0){
                        Set<SysAppMenuEntity> set = new TreeSet<>((o1, o2) -> o1.getMenuId().compareTo(o2.getMenuId()));
                        set.addAll(list);
                        map.put("menuList",new ArrayList<SysAppMenuEntity>(set));
                        return JsonResults.success(map);
                    }
                }
            }
        }
        return JsonResults.error("二维码错误!");
    }

    @Override
    public IPage page(Page startPage, TwoDimensionalCodeEntity entity) {
        return this.page(startPage, this.queryWrapper(entity));
    }

    public QueryWrapper<TwoDimensionalCodeEntity> queryWrapper(TwoDimensionalCodeEntity entity) {
        QueryWrapper<TwoDimensionalCodeEntity> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(StringUtils.isNotEmpty(entity.getCodeStatus()), TwoDimensionalCodeEntity::getCodeStatus, entity.getCodeStatus())
                .eq(StringUtils.isNotEmpty(entity.getCodeType()), TwoDimensionalCodeEntity::getCodeType, entity.getCodeType())
                .eq(StringUtils.isNotEmpty(entity.getMerchantId()), TwoDimensionalCodeEntity::getMerchantId, entity.getMerchantId())
                .eq(StringUtils.isNotEmpty(entity.getDelFlg()), TwoDimensionalCodeEntity::getDelFlg, entity.getDelFlg())
                .ge(null != entity.getBeginDate(), TwoDimensionalCodeEntity::getEffectiveTime, entity.getBeginDate())
                .le(null != entity.getEndDate(), TwoDimensionalCodeEntity::getEffectiveTime, entity.getEndDate());
        return wrapper;
    }
}
