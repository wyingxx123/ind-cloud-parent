package com.dfc.ind.service.impl.sys;

import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.text.Convert;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.sys.SysAppMenuEntity;
import com.dfc.ind.mapper.sys.SysAppMenuMapper;
import com.dfc.ind.service.sys.ISysAppMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 描述: app菜单权限接口实现类
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/8/26
 * @copyright 武汉数慧享智能科技有限公司
 */
@Service
public class SysAppMenuServiceImpl extends ServiceImpl<SysAppMenuMapper, SysAppMenuEntity> implements ISysAppMenuService {
    @Override
    public JsonResults listAll(SysAppMenuEntity entity) {
        List<SysAppMenuEntity> list = this.list(queryWrapper(entity));
        if(null != list && list.size() > 0) {
            return JsonResults.success(list);
        }
        return JsonResults.success();
    }

    @Override
    public JsonResults add(SysAppMenuEntity entity) {
        SysAppMenuEntity sysAppMenuEntity = getOne(new QueryWrapper<SysAppMenuEntity>().lambda().eq(SysAppMenuEntity::getPerms,entity.getPerms()));
        if(StringUtils.isNull(sysAppMenuEntity)){
            entity.setCreateBy(SecurityUtils.getUserName());
            entity.setCreateTime(DateUtils.getNowDate());
            if(this.save(entity)) {
                return JsonResults.success(entity);
            }
            return JsonResults.error("新增失败");
        }else{
            return JsonResults.error("权限标识不能重复");
        }
    }


    @Override
    public JsonResults update(SysAppMenuEntity entity) {
        entity.setUpdateBy(SecurityUtils.getUserName());
        entity.setUpdateTime(DateUtils.getNowDate());
        if(this.updateById(entity)) {
            return JsonResults.success("修改成功");
        }
        return JsonResults.error("修改失败");
    }

    @Override
    public JsonResults list(Page startPage, SysAppMenuEntity entity) {
        return JsonResults.success(this.page(startPage, queryWrapper(entity)));
    }

    @Override
    public JsonResults batchDelete(String menuIds) {
        if(StringUtils.isNotEmpty(menuIds)) {
            try{
                if(this.removeByIds(Arrays.asList(menuIds.split(",")))){
                    return JsonResults.success("删除成功");
                }
            } catch (Exception e) {
                throw new CustomException("删除失败");
            }
        }
        return JsonResults.error("删除失败");
    }


    @Override
    public JsonResults getByIds(String ids) {
        if(StringUtils.isNotEmpty(ids)) {
            Long[] menuIds = Convert.toLongArray(ids);
            List list = Arrays.asList(menuIds);
            List<SysAppMenuEntity> menuList = list(new QueryWrapper<SysAppMenuEntity>().lambda()
                    .in(SysAppMenuEntity::getMenuId,list)
                    .eq(SysAppMenuEntity::getVisible,"0"));
            return JsonResults.success(menuList);
        }
        return JsonResults.error("查询失败!");
    }


    @Override
    public List<SysAppMenuEntity> getByMenu(String ids) {
        if(StringUtils.isNotEmpty(ids)) {
            Long[] menuIds = Convert.toLongArray(ids);
            List list = Arrays.asList(menuIds);
            List<SysAppMenuEntity> menuList = list(new QueryWrapper<SysAppMenuEntity>().lambda()
                    .in(SysAppMenuEntity::getMenuId,list)
                    .eq(SysAppMenuEntity::getVisible,"0"));
            return menuList;
        }
        return null;
    }


    private LambdaQueryWrapper<SysAppMenuEntity> queryWrapper(SysAppMenuEntity entity) {
        return new QueryWrapper<SysAppMenuEntity>().lambda()
                .eq(StringUtils.isNotNull(entity.getMenuId()), SysAppMenuEntity::getMenuId, entity.getMenuId())
                .like(StringUtils.isNotEmpty(entity.getPath()), SysAppMenuEntity::getPath, entity.getPath())
                .eq(StringUtils.isNotEmpty(entity.getMenuType()), SysAppMenuEntity::getMenuType, entity.getMenuType())
                .like(StringUtils.isNotEmpty(entity.getPerms()), SysAppMenuEntity::getPerms, entity.getPerms());
    }
}
