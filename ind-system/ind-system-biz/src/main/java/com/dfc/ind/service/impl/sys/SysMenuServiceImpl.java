package com.dfc.ind.service.impl.sys;

import com.alibaba.fastjson.JSON;
import com.dfc.ind.common.core.constant.UserConstants;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.merchant.AgrMerchantInfoEntity;
import com.dfc.ind.entity.sys.SysMenu;
import com.dfc.ind.entity.sys.SysUser;
import com.dfc.ind.entity.vo.MetaVo;
import com.dfc.ind.entity.vo.RouterVo;
import com.dfc.ind.entity.vo.SyncInfoVo;
import com.dfc.ind.entity.vo.TreeSelect;
import com.dfc.ind.mapper.sys.SysMenuMapper;
import com.dfc.ind.mapper.sys.SysRoleMenuMapper;
import com.dfc.ind.service.merchant.IAgrMerchantInfoService;
import com.dfc.ind.service.sys.ISysMenuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单 业务层处理
 *
 * @author admin
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService
{
    public static final String PREMISSION_STRING = "perms[\"{0}\"]";

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private IAgrMerchantInfoService agrMerchantInfoService;

    /**
     * 根据用户查询系统菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(Long userId, Long merchantId)
    {
        return selectMenuList(new SysMenu(), userId, merchantId);
    }

    /**
     * 查询系统菜单列表
     *
     * @param menu 菜单信息
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuList(SysMenu menu, Long userId, Long merchantId)
    {
        List<SysMenu> menuList = null;
        // 管理员显示所有菜单信息
        if (SysUser.isAdmin(userId))
        {
            menuList = menuMapper.selectMenuList(menu);
        }
        else
        {
            Map<String, Object> params = new HashMap<>(8);
            params.put("userId", userId);
            params.put("merchantId", merchantId);
            AgrMerchantInfoEntity merchantInfoEntity = agrMerchantInfoService.getById(merchantId);
            String empowerJson = merchantInfoEntity == null ? null : merchantInfoEntity.getEmpowerJson();
            List<String> adaptiveList = new ArrayList<String>();
            adaptiveList.add("0");
            if (StringUtils.isNotEmpty(empowerJson)) {
                Map<String, String> empowerMap = (Map<String, String>)JSON.parseObject(empowerJson, HashMap.class);
                for (Map.Entry<String,String> entry : empowerMap.entrySet()) {
                    if (StringUtils.isNotEmpty(entry.getValue())) {
                        adaptiveList.add(entry.getValue());
                    }
                }
            }
            params.put("adaptiveFlag", adaptiveList);
            menu.setParams(params);
            menuList = baseMapper.selectMenuListByUserId(menu);
        }
        return menuList;
    }

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public Set<String> selectMenuPermsByUserId(Long userId,Long merchantId)
    {
        List<String> perms = menuMapper.selectMenuPermsByUserId(userId,merchantId);
        Set<String> permsSet = new HashSet<>();
        for (String perm : perms)
        {
            if (StringUtils.isNotEmpty(perm))
            {
                permsSet.addAll(Arrays.asList(perm.trim().split(",")));
            }
        }
        return permsSet;
    }

    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户名称
     * @return 菜单列表
     */
    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId,Long merchantId)
    {
        List<SysMenu> menus = null;
        if (SecurityUtils.isAdmin(userId))
        {
            menus = menuMapper.selectMenuTreeAll();
        }
        else
        {
            AgrMerchantInfoEntity merchantInfoEntity = agrMerchantInfoService.getById(merchantId);
            String empowerJson = merchantInfoEntity == null ? null : merchantInfoEntity.getEmpowerJson();
            List<String> adaptiveList = new ArrayList<String>();
            adaptiveList.add("0");
            if (StringUtils.isNotEmpty(empowerJson)) {
                Map<String, String> empowerMap = (Map<String, String>)JSON.parseObject(empowerJson, HashMap.class);
                for (Map.Entry<String,String> entry : empowerMap.entrySet()) {
                    if (StringUtils.isNotEmpty(entry.getValue())) {
                        adaptiveList.add(entry.getValue());
                    }
                }
            }
            menus = menuMapper.selectMenuTreeByUserId(userId, merchantId, adaptiveList);
        }
        return getChildPerms(menus, 0);
    }

    /**
     * 根据角色ID查询菜单树信息
     *
     * @param roleId 角色ID
     * @return 选中菜单列表
     */
    @Override
    public List<Integer> selectMenuListByRoleId(Long roleId)
    {
        return menuMapper.selectMenuListByRoleId(roleId);
    }

    /**
     * 构建前端路由所需要的菜单
     *
     * @param menus 菜单列表
     * @return 路由列表
     */
    @Override
    public List<RouterVo> buildMenus(List<SysMenu> menus)
    {
        List<RouterVo> routers = new LinkedList<RouterVo>();
        for (SysMenu menu : menus)
        {
            RouterVo router = new RouterVo();
            router.setHidden("1".equals(menu.getVisible()));
            router.setName(getRouteName(menu));
            router.setPath(getRouterPath(menu));
            router.setComponent(getComponent(menu));
            router.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
            List<SysMenu> cMenus = menu.getChildren();
            if (!cMenus.isEmpty() && cMenus.size() > 0 && UserConstants.TYPE_DIR.equals(menu.getMenuType()))
            {
                router.setAlwaysShow(true);
                router.setRedirect("noRedirect");
                router.setChildren(buildMenus(cMenus));
            }
            else if (isMeunFrame(menu))
            {
                List<RouterVo> childrenList = new ArrayList<RouterVo>();
                RouterVo children = new RouterVo();
                children.setPath(menu.getPath());
                children.setComponent(menu.getComponent());
                children.setName(StringUtils.capitalize(menu.getPath()));
                children.setMeta(new MetaVo(menu.getMenuName(), menu.getIcon()));
                childrenList.add(children);
                router.setChildren(childrenList);
            }
            routers.add(router);
        }
        return routers;
    }

    /**
     * 构建前端所需要树结构
     *
     * @param menus 菜单列表
     * @return 树结构列表
     */
    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus)
    {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (Iterator<SysMenu> iterator = menus.iterator(); iterator.hasNext();)
        {
            SysMenu t = (SysMenu) iterator.next();
            // 根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == 0)
            {
                recursionFn(menus, t);
                returnList.add(t);
            }
        }
        if (returnList.isEmpty())
        {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 构建前端所需要下拉树结构
     *
     * @param menus 菜单列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildMenuTreeSelect(List<SysMenu> menus)
    {
        List<SysMenu> menuTrees = buildMenuTree(menus);
        return menuTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    /**
     * 根据菜单ID查询信息
     *
     * @param menuId 菜单ID
     * @return 菜单信息
     */
    @Override
    public SysMenu selectMenuById(Long menuId)
    {
        Long merchantId = null != SecurityUtils.getLoginUser().getMerchantId() ? SecurityUtils.getLoginUser().getMerchantId() : 0L;

        return menuMapper.selectMenuById(menuId,merchantId);
    }

    /**
     * 是否存在菜单子节点
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean hasChildByMenuId(Long menuId)
    {
        int result = menuMapper.hasChildByMenuId(menuId);
        return result > 0 ? true : false;
    }

    /**
     * 查询菜单使用数量
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public boolean checkMenuExistRole(Long menuId)
    {
        int result = roleMenuMapper.checkMenuExistRole(menuId);
        return result > 0 ? true : false;
    }

    /**
     * 新增保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public boolean insertMenu(SysMenu menu)
    {
        return this.save(menu);
    }

    /**
     * 修改保存菜单信息
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public int updateMenu(SysMenu menu)
    {
        return menuMapper.updateById(menu);
    }

    /**
     * 删除菜单管理信息
     *
     * @param menuId 菜单ID
     * @return 结果
     */
    @Override
    public int deleteMenuById(Long menuId)
    {
        return menuMapper.deleteMenuById(menuId);
    }

    /**
     * 校验菜单名称是否唯一
     *
     * @param menu 菜单信息
     * @return 结果
     */
    @Override
    public String checkMenuNameUnique(SysMenu menu)
    {
        Long menuId = StringUtils.isNull(menu.getMenuId()) ? -1L : menu.getMenuId();
        Long merchantId = SecurityUtils.getLoginUser().getMerchantId();
        SysMenu info = menuMapper.checkMenuNameUnique(menu.getMenuName(), menu.getParentId(),merchantId);
        if (StringUtils.isNotNull(info) && info.getMenuId().longValue() != menuId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 获取路由名称
     *
     * @param menu 菜单信息
     * @return 路由名称
     */
    public String getRouteName(SysMenu menu)
    {
        String routerName = StringUtils.capitalize(menu.getPath());
        // 非外链并且是一级目录（类型为目录）
        if (isMeunFrame(menu))
        {
            routerName = StringUtils.EMPTY;
        }
        return routerName;
    }

    /**
     * 获取路由地址
     *
     * @param menu 菜单信息
     * @return 路由地址
     */
    public String getRouterPath(SysMenu menu)
    {
        String routerPath = menu.getPath();
        // 非外链并且是一级目录（类型为目录）
        if (0 == menu.getParentId().intValue() && UserConstants.TYPE_DIR.equals(menu.getMenuType())
                && UserConstants.NO_FRAME.equals(menu.getIsFrame()))
        {
            routerPath = "/" + menu.getPath();
        }
        // 非外链并且是一级目录（类型为菜单）
        else if (isMeunFrame(menu))
        {
            routerPath = "/";
        }
        return routerPath;
    }

    /**
     * 获取组件信息
     *
     * @param menu 菜单信息
     * @return 组件信息
     */
    public String getComponent(SysMenu menu)
    {
        String component = UserConstants.LAYOUT;
        if (StringUtils.isNotEmpty(menu.getComponent()) && !isMeunFrame(menu))
        {
            component = menu.getComponent();
        }
        return component;
    }

    /**
     * 是否为菜单内部跳转
     *
     * @param menu 菜单信息
     * @return 结果
     */
    public boolean isMeunFrame(SysMenu menu)
    {
        return menu.getParentId().intValue() == 0 && UserConstants.TYPE_MENU.equals(menu.getMenuType())
                && menu.getIsFrame().equals(UserConstants.NO_FRAME);
    }

    /**
     * 根据父节点的ID获取所有子节点
     *
     * @param list 分类表
     * @param parentId 传入的父节点ID
     * @return String
     */
    public List<SysMenu> getChildPerms(List<SysMenu> list, int parentId)
    {
        List<SysMenu> returnList = new ArrayList<SysMenu>();
        for (Iterator<SysMenu> iterator = list.iterator(); iterator.hasNext();)
        {
            SysMenu t = (SysMenu) iterator.next();
            // 一、根据传入的某个父节点ID,遍历该父节点的所有子节点
            if (t.getParentId() == parentId)
            {
                recursionFn(list, t);
                returnList.add(t);
            }
        }
        return returnList;
    }

    /**
     * 递归列表
     *
     * @param list
     * @param t
     */
    private void recursionFn(List<SysMenu> list, SysMenu t)
    {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList)
        {
            if (hasChild(list, tChild))
            {
                // 判断是否有子节点
                Iterator<SysMenu> it = childList.iterator();
                while (it.hasNext())
                {
                    SysMenu n = (SysMenu) it.next();
                    recursionFn(list, n);
                }
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t)
    {
        List<SysMenu> tlist = new ArrayList<SysMenu>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext())
        {
            SysMenu n = (SysMenu) it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue())
            {
                tlist.add(n);
            }
        }
        return tlist;
    }

    @Override
    public JsonResults getSynchronizeInfo(Long menuId) {
        SysMenu entity = this.getOne(new QueryWrapper<SysMenu>().lambda().eq(SysMenu::getMenuId, menuId).eq(SysMenu::getMerchantId, 0));
        List<AgrMerchantInfoEntity> list = agrMerchantInfoService.list(new QueryWrapper<AgrMerchantInfoEntity>().lambda().eq(AgrMerchantInfoEntity::getStatus, "0"));
        if (list != null && list.size() > 0) {
            for(AgrMerchantInfoEntity merchantInfoEntity:list){
                SysMenu menuEntity = this.getOne(new QueryWrapper<SysMenu>().lambda().eq(SysMenu::getMenuId, menuId).eq(SysMenu::getMerchantId, merchantInfoEntity.getMerchantId()));
                if(menuEntity == null){
                    menuEntity = entity;
                    menuEntity.setMerchantId(merchantInfoEntity.getMerchantId());
                    if(baseMapper.insert(menuEntity) == 0){
                        return JsonResults.error("同步失败!");
                    }
                }
            }
        }
        return JsonResults.success("同步成功!");
    }

    @Override
    public JsonResults synchronizeMenuInfo(SyncInfoVo syncInfoVo) {
        if (StringUtils.isNull(syncInfoVo.getMenuId())) {
            return JsonResults.error("请选择要同步的菜单资源!");
        }
        if (StringUtils.isEmpty(syncInfoVo.getSyncType())) {
            return JsonResults.error("请选择要同步到哪些商户!");
        }
        SysMenu entity = this.getOne(new QueryWrapper<SysMenu>().lambda().eq(SysMenu::getMenuId, syncInfoVo.getMenuId()).eq(SysMenu::getMerchantId, 0));
        if (null == entity) {
            return JsonResults.error("找不到要同步的菜单资源！");
        }
        List<AgrMerchantInfoEntity> list = new ArrayList<>();
        if ("00".equals(syncInfoVo.getSyncType())) {
            list = agrMerchantInfoService.list(new QueryWrapper<AgrMerchantInfoEntity>().lambda().eq(AgrMerchantInfoEntity::getStatus, "0"));
        } else if ("01".equals(syncInfoVo.getSyncType()) && StringUtils.isNotNull(syncInfoVo.getSyncMerchant()) && syncInfoVo.getSyncMerchant().size() > 0) {
            list = agrMerchantInfoService.list(new QueryWrapper<AgrMerchantInfoEntity>().lambda().eq(AgrMerchantInfoEntity::getStatus, "0")
                    .in(AgrMerchantInfoEntity::getMerchantId, syncInfoVo.getSyncMerchant()));
        }
        if (list != null && list.size() > 0) {
            for(AgrMerchantInfoEntity merchantInfoEntity:list){
                SysMenu menuEntity = this.getOne(new QueryWrapper<SysMenu>().lambda().eq(SysMenu::getMenuId, syncInfoVo.getMenuId())
                        .eq(SysMenu::getMerchantId, merchantInfoEntity.getMerchantId()));
                if(menuEntity == null){
                    menuEntity = entity;
                    menuEntity.setMerchantId(merchantInfoEntity.getMerchantId());
                    if(baseMapper.insert(menuEntity) == 0){
                        return JsonResults.error("同步失败!");
                    }
                }
            }
        }
        return JsonResults.success("同步成功!");
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t)
    {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    @Override
    public List<SysMenu> getByRole(Long roleId) {
        return baseMapper.getByRole(roleId);
    }

    @Override
    public boolean updateMenuById(SysMenu menu) {
        return baseMapper.updateMenuById(menu);
    }
}
