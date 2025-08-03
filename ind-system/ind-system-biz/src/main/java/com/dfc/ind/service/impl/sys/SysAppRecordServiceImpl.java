package com.dfc.ind.service.impl.sys;

import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.sys.SysAppMenuEntity;
import com.dfc.ind.entity.sys.SysAppRecordEntity;
import com.dfc.ind.mapper.sys.SysAppRecordMapper;
import com.dfc.ind.service.sys.ISysAppMenuService;
import com.dfc.ind.service.sys.ISysAppRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * app访问记录 服务实现类
 * </p>
 *
 * @author dingw
 * @since 2020-09-28
 */
@Service
public class SysAppRecordServiceImpl extends ServiceImpl<SysAppRecordMapper, SysAppRecordEntity> implements ISysAppRecordService {
    @Autowired
    private ISysAppMenuService sysAppMenuService;

    @Override
    public Boolean saveUpdate(SysAppRecordEntity entity) {
        if(StringUtils.isNull(entity.getUserId()) || StringUtils.isNull(entity.getMenuPerms()) ){
            return false;
        }
        SysAppRecordEntity sysAppRecordEntity = this.getOne(queryWrapper(entity));
        if(sysAppRecordEntity != null){
            sysAppRecordEntity.setUpdateTime(DateUtils.getNowDate());
            sysAppRecordEntity.setUpdateBy(SecurityUtils.getUserName());
            return updateById(sysAppRecordEntity);
        }else{
            entity.setUpdateTime(DateUtils.getNowDate());
            entity.setUpdateBy(SecurityUtils.getUserName());
            entity.setDelFlg("0");
            entity.setIsCollection("01");
            entity.setCreateTime(DateUtils.getNowDate());
            entity.setCreateBy(SecurityUtils.getUserName());
            return save(entity);
        }
    }


    @Override
    public IPage pageList(Page startPage, SysAppRecordEntity entity) {
        IPage<SysAppRecordEntity> pages =  page(startPage,queryWrapper(entity));
        List<SysAppRecordEntity> list = pages.getRecords();
        pages.setRecords(getMenuInfo(list));
        return pages;
    }


    @Override
    public List<SysAppRecordEntity> listAll(SysAppRecordEntity entity) {
        List<SysAppRecordEntity> list = list(queryWrapper(entity));
        return getMenuInfo(list);
    }

    private List<SysAppRecordEntity> getMenuInfo(List<SysAppRecordEntity> list){
        for(SysAppRecordEntity sysAppRecordEntity:list){
            SysAppMenuEntity sysAppMenuEntity = sysAppMenuService.getOne(new QueryWrapper<SysAppMenuEntity>().lambda().eq(SysAppMenuEntity::getPerms,sysAppRecordEntity.getMenuPerms()));
            sysAppRecordEntity.setIcon(sysAppMenuEntity.getIcon());
            sysAppRecordEntity.setMenuName(sysAppMenuEntity.getMenuName());
            sysAppRecordEntity.setPath(sysAppMenuEntity.getPath());
        }
        return list;
    }

    private LambdaQueryWrapper<SysAppRecordEntity> queryWrapper(SysAppRecordEntity entity){
        return new QueryWrapper<SysAppRecordEntity>().lambda()
                .eq(StringUtils.isNotNull(entity.getUserId()), SysAppRecordEntity::getUserId, entity.getUserId())
                .eq(StringUtils.isNotEmpty(entity.getMenuPerms()), SysAppRecordEntity::getMenuPerms, entity.getMenuPerms())
                .eq(StringUtils.isNotEmpty(entity.getMerchantId()), SysAppRecordEntity::getMerchantId, entity.getMerchantId())
                .eq(StringUtils.isNotEmpty(entity.getIsCollection()), SysAppRecordEntity::getIsCollection, entity.getIsCollection())
                .eq(StringUtils.isNotEmpty(entity.getDelFlg()), SysAppRecordEntity::getDelFlg, entity.getDelFlg())
                .orderByDesc(SysAppRecordEntity::getUpdateTime);
    }
}
