package com.dfc.ind.controller.station.sys;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dfc.ind.common.core.constant.UserConstants;
import com.dfc.ind.common.core.domain.R;
import com.dfc.ind.common.core.utils.FileUploadTool;
import com.dfc.ind.common.core.utils.IdUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.log.annotation.Log;
import com.dfc.ind.common.log.enums.BusinessType;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.merchant.AgrMerchantInfoEntity;
import com.dfc.ind.entity.sys.SysDept;
import com.dfc.ind.entity.sys.SysPost;
import com.dfc.ind.entity.sys.SysRole;
import com.dfc.ind.entity.sys.SysUser;
import com.dfc.ind.entity.vo.MallUserEntityVO;
import com.dfc.ind.mapper.sys.SysDeptMapper;
import com.dfc.ind.mapper.sys.SysUserMapper;
import com.dfc.ind.mapper.sys.SysUserRoleMapper;
import com.dfc.ind.service.merchant.IAgrMerchantInfoService;
import com.dfc.ind.service.sys.*;
import com.dfc.ind.sys.model.UserInfo;
import com.dfc.ind.utils.CryptoUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20190711.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20190711.models.SendStatus;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户信息
 *
 * @author admin
 */
@RestController
@RequestMapping("/user")
public class
SysUserController extends BaseController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private ISysPermissionService permissionService;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private IAgrMerchantInfoService agrMerchantInfoService;

    @Autowired
    private SysUserMapper  sysUserMapper;


    @Autowired
    private SysDeptMapper  SysDeptMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 获取用户列表
     */
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public JsonResults list(SysUser user) {
        Long deptId = user.getDeptId();
        SysDept sysDept = sysDeptService.getById(deptId);
        List<Long> deptIds = new ArrayList<>(10);
        deptIds.add(deptId);
        if (sysDept != null) {
            Long merchantId = sysDept.getMerchantId();
            List<SysDept> sysDeptList = sysDeptService.list(new QueryWrapper<SysDept>().lambda()
                    .eq(SysDept::getParentId, deptId));
            if (sysDeptList != null && sysDeptList.size() > 0) {
                deptIds = queryDeptId(sysDeptList, deptIds);
            }
        }
        //正常状态用户为0
        user.setDelFlag("0");
        user.setDeptIds(deptIds);
        return JsonResults.success(userService.pageList(startPage(), user));
    }

    @GetMapping("/listAll")
    public JsonResults listAll(SysUser user) {
        Long deptId = user.getDeptId();
        SysDept sysDept = sysDeptService.getById(deptId);
        List<Long> deptIds = new ArrayList<>(10);
        deptIds.add(deptId);
        if (sysDept != null) {
            Long merchantId = sysDept.getMerchantId();
            List<SysDept> sysDeptList = sysDeptService.list(new QueryWrapper<SysDept>().lambda()
                    .eq(SysDept::getParentId, deptId));
            if (sysDeptList != null && sysDeptList.size() > 0) {
                deptIds = queryDeptId(sysDeptList, deptIds);
            }
        }
        //正常状态用户为0
        user.setDelFlag("0");
        user.setDeptIds(deptIds);
        return JsonResults.success(userService.selectAllUser(user));
    }

    @PostMapping("/all")
    public JsonResults allUser(@RequestBody SysUser user) {
        SysUser sysUser = userService.selectUserByUserName(SecurityUtils.getUsername());
        List<Long> deptIds = new ArrayList<>(10);
        deptIds.add(sysUser.getDeptId());
        if (sysUser != null) {
            Long merchantId = sysUser.getMerchantId();
            List<SysDept> sysDeptList = sysDeptService.list(new QueryWrapper<SysDept>().lambda()
                    .eq(SysDept::getParentId, sysUser.getDeptId()));
            if (sysDeptList != null && sysDeptList.size() > 0) {
                deptIds = queryDeptId(sysDeptList, deptIds);
            }
        }
        //正常状态用户为0
        user.setDelFlag("0");
        user.setDeptIds(deptIds);
        return JsonResults.success(userService.selectAllUser(user));
    }
    /**
     * 获取用户角色详细信息
     *
     * @param roleType
     * @param merchantId
     * @return
     */
    @GetMapping("/getUserByRoleType")
    public JsonResults getUserByRoleType( String roleType, String userName,Long userId,Long deptId,String status,String phonenumber,String beginDate,String endDate, Long merchantId) {
        SysDept sysDept = sysDeptService.getById(deptId);
        List<Long> deptIds = new ArrayList<>(10);
        deptIds.add(deptId);
        if (sysDept != null) {
            List<SysDept> sysDeptList = sysDeptService.list(new QueryWrapper<SysDept>().lambda()
                    .eq(SysDept::getParentId, deptId));
            if (sysDeptList != null && sysDeptList.size() > 0) {
                deptIds = queryDeptId(sysDeptList, deptIds);
            }
        }
        Page<SysUser> userByRoleType = userService.getUserByRoleType(startPage(), roleType, userName, deptIds, status, phonenumber, beginDate, endDate, merchantId);
        List<SysUser> records = userByRoleType.getRecords();
        if (!CollectionUtils.isEmpty(records)){
            for (SysUser record : records) {
                Set<String> roles= roleService.getRoleNamesByUerId(record.getUserId());
                record.setRoleNames(roles);
            }
        }
        return JsonResults.success(userByRoleType);
    }

    @Log(title = "用户管理-导出", businessType = BusinessType.EXPORT)
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysUser user) throws IOException {
        Long deptId = user.getDeptId();
        SysDept sysDept = sysDeptService.getById(deptId);
        List<Long> deptIds = new ArrayList<>(10);
        deptIds.add(deptId);
        if (sysDept != null) {
            Long merchantId = sysDept.getMerchantId();
            List<SysDept> sysDeptList = sysDeptService.list(new QueryWrapper<SysDept>().lambda()
                    .eq(SysDept::getParentId, deptId));
            if (sysDeptList != null && sysDeptList.size() > 0) {
                deptIds = queryDeptId(sysDeptList, deptIds);
            }
        }
        //正常状态用户为0
        user.setDelFlag("0");
        user.setDeptIds(deptIds);
        List<SysUser> list = userService.list(queryWrapper(user));
        for (SysUser sysUser : list) {
            sysUser.setDeptName(sysDeptService.getById(sysUser.getDeptId()).getDeptName());
            Set<String> roles= roleService.getRoleNamesByUerId(sysUser.getUserId());
            String collect = roles.stream().collect(Collectors.joining(","));
            sysUser.setRoleName(collect);
        }
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.exportExcel(response, list, "用户数据");
    }

    @Log(title = "用户管理-导入", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/importData")
    public JsonResults importData(MultipartFile file, boolean updateSupport) throws Exception {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = SecurityUtils.getUsername();
        String message = userService.importUser(userList, updateSupport, operName);
        return JsonResults.success(message);
    }
    @Log(title = "用户管理-导入", businessType = BusinessType.IMPORT)
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        util.importTemplateExcel(response, "用户数据");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info/{username}")
    public R<UserInfo> info(@PathVariable("username") String username) {
        SysUser sysUser = userService.selectUserByUserName(username);
       // SysUser sysUser = userService.selectUserByPoneNumber(username);
        if (StringUtils.isNull(sysUser)) {
            return R.fail("用户名或密码错误");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser.getUserId());
        if(roles.size()<=0){
            return R.fail("当前用户暂无角色");
        }
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId(), sysUser.getMerchantId());
        UserInfo sysUserVo = new UserInfo();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }
    /**
     * 获取当前用户信息
     */
    @GetMapping("/info/getUserInfoAndMerchantId")
    public R<UserInfo> getUserInfoAndMerchantId(@RequestParam String username,@RequestParam String merchantId) {
        SysUser sysUser = userService.selectUserByUserNameAndMerchantId(username,merchantId);
        if (StringUtils.isNull(sysUser)) {
            return R.fail("用户名或密码错误");
        }
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(sysUser.getUserId());
        if(roles.size()<=0){
            return R.fail("当前用户暂无角色");
        }
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId(), sysUser.getMerchantId());
        UserInfo sysUserVo = new UserInfo();
        sysUserVo.setSysUser(sysUser);
        sysUserVo.setRoles(roles);
        sysUserVo.setPermissions(permissions);
        return R.ok(sysUserVo);
    }
    /**
     * 获取用户信息
     *
     * @return 用户信息
     */
    @GetMapping("/getInfo")
    public JsonResults getInfo() {
        Long userId = SecurityUtils.getLoginUser().getUserId();
        // 角色集合
        Set<String> roles = permissionService.getRolePermission(userId);
        // 权限集合
        Set<String> permissions = permissionService.getMenuPermission(userId, SecurityUtils.getLoginUser().getMerchantId());
        JsonResults ajax = JsonResults.success();

        SysUser user = userService.selectUserById(userId);
        //获取商户信息

        AgrMerchantInfoEntity merchantInfo = agrMerchantInfoService.getById(SecurityUtils.getLoginUser().getMerchantId());
        if (StringUtils.isNotNull(merchantInfo)) {
            user.setMerchantName(merchantInfo.getMerchantName());
        }
        ajax.put("user", user);
        ajax.put("roles", roles);
        ajax.put("merchantInfo", merchantInfo);
        ajax.put("permissions", permissions);
        return ajax;
    }

    /**
     * 根据用户编号获取详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping(value = {"/", "/{userId}"})
    public JsonResults getInfo(@PathVariable(value = "userId", required = false) Long userId) {
        JsonResults ajax = JsonResults.success();
        List<SysRole> roles = roleService.selectRoleAll();
        ajax.put("roles", SysUser.isAdmin(userId) ? roles : roles.stream().filter(r -> !r.isAdmin()).collect(Collectors.toList()));
        ajax.put("posts", postService.selectPostAll());
        if (StringUtils.isNotNull(userId)) {
            ajax.put(JsonResults.DATA_TAG, userService.selectUserById(userId));
            ajax.put("postIds", postService.selectPostListByUserId(userId));
            ajax.put("roleIds", roleService.selectRoleListByUserId(userId));
        }
        return ajax;
    }

    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "用户管理-新增", businessType = BusinessType.INSERT)
    @PostMapping
    public JsonResults add(@Validated @RequestBody SysUser user) {

      SysDept  sysDept =  SysDeptMapper.selectBydeptName(user.getDeptName());
      if(sysDept!=null){
          user.setDeptId(sysDept.getDeptId());
      }

        //双重校验 同一商户下用户唯一性
     SysUser   sysUser =   userService.selectUserByUserName(user.getUserName());
     Boolean   isuser=true;
     if(sysUser!=null){
         if(SecurityUtils.getLoginUser().getMerchantId().equals(sysUser.getMerchantId())){
             isuser=false;
         }
     }
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))  && isuser!=true ) {
            SysUser existUser = userService.selectUserByUserName(user.getUserName());
            if ("2".equals(existUser.getDelFlag())) {
                user.setUserId(existUser.getUserId());
                user.setDelFlag("0");
                return toAjax(userService.updateUser(user));
            }
            return JsonResults.error("新增用户'" + user.getUserName() + "'失败，当前商户下登录账号已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return JsonResults.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return JsonResults.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        Long merchantId = getByDept(user.getDeptId());
        user.setMerchantId(merchantId);
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        //-------------------------------------------------------------
        if (userService.insertUser(user) > 0) {
            return JsonResults.success();
        }

        return JsonResults.error("操作失败");
    }





    /**
     * 新增用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @Log(title = "运管新增用户管理-新增", businessType = BusinessType.INSERT)
    @PostMapping("/yxadd")
    public JsonResults yxadd(@Validated @RequestBody SysUser user) {

        SysDept  sysDept =  SysDeptMapper.selectBydeptName(user.getDeptName());
        if(sysDept!=null){
            user.setDeptId(sysDept.getDeptId());
        }
        //双重校验 同一商户下用户唯一性
        SysUser   sysUser =   userService.selectUserByUserName(user.getUserName());
        Boolean   isuser=true;
        if(sysUser!=null){
            if(SecurityUtils.getLoginUser().getMerchantId().equals(sysUser.getMerchantId())){
                isuser=false;
            }
        }
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(user.getUserName()))  && isuser!=true ) {
            SysUser existUser = userService.selectUserByUserName(user.getUserName());
            if ("2".equals(existUser.getDelFlag())) {
                user.setUserId(existUser.getUserId());
                user.setDelFlag("0");
                return toAjax(userService.updateUser(user));
            }
            return JsonResults.error("新增用户'" + user.getUserName() + "'失败，当前商户下登录账号已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user))) {
            return JsonResults.error("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user))) {
            return JsonResults.error("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        Long merchantId = getByDept(user.getDeptId());
        user.setMerchantId(merchantId);
        user.setCreateBy(SecurityUtils.getUsername());
        user.setPassword(user.getPassword());
        //-------------------------------------------------------------
        if (userService.insertUser(user) > 0) {
            return JsonResults.success(user.getUserId());
        }

        return JsonResults.error("操作失败");
    }



    /**
     * 修改用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理-修改", businessType = BusinessType.UPDATE)
    @PutMapping
    public JsonResults edit(@Validated @RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(SecurityUtils.getUsername());
        return JsonResults.success(userService.updateUser(user)==1?"手机号码已存在,请输入其它手机号":"修改成功");
    }

    /**
     * 删除用户
     */
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @Log(title = "用户管理-删除", businessType = BusinessType.DELETE)
    @DeleteMapping("/{userIds}")
    public JsonResults remove(@PathVariable Long[] userIds) {
        return toAjax(userService.deleteUserByIds(userIds));
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @Log(title = "用户管理-修改密码", businessType = BusinessType.UPDATE)
    @PutMapping("/resetPwd")
    public JsonResults resetPwd(SysUser user) {
        userService.checkUserAllowed(user);
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        user.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(userService.resetPwd(user));
    }

    /**
     * 状态修改
     */
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @Log(title = "用户管理-修改状态", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public JsonResults changeStatus(@RequestBody SysUser user) {
        userService.checkUserAllowed(user);
        user.isAdmin();
        user.setUpdateBy(SecurityUtils.getUsername());
        String start=userService.queryCurrentUserPermission(user);
        if(StringUtils.isNotEmpty(start)){
            return JsonResults.error("对不起,您无权限修改该用户状态");
        }
        return toAjax(userService.updateUserStatus(user));
    }

    /**
     * <app二维码扫码注册>
     *
     * @param
     * @return
     * @author dingw
     * @Date 2020/5/16 9:37
     */
    @PostMapping("/twoCodeRegister")
    @ApiOperation(value = "二维码扫码注册")
    public JsonResults twoCodeRegister(@RequestBody MallUserEntityVO mallUserEntityVO) {
        if (UserConstants.NOT_UNIQUE.equals(userService.checkUserNameUnique(mallUserEntityVO.getUserName()))) {
            return JsonResults.error("新增用户'" + mallUserEntityVO.getUserName() + "'失败，登录账号已存在");
        }
        return userService.twoCodeRegister(mallUserEntityVO);
    }

    /**
     * <根据商户id查询所属下所有用户信息>
     *
     * @param merchantId 商户id
     * @return
     * @author ylyan
     * @Date 2020/4/29 20:06
     */
    @GetMapping("/list/{merchantId}")
    @ApiIgnore
    public JsonResults mAndUList(@PathVariable Long merchantId) {

        return JsonResults.success(userService.list(new QueryWrapper<SysUser>().lambda()
                .eq(SysUser::getMerchantId, merchantId)));
    }

    /**
     * 根据用户名称查询用户等级
     * @param userName 用户名称
     * @param merchantId 商户号
     * @return 返回用户等级
     */
    @GetMapping("/getGrade")
    @ApiIgnore
    public String getGrade(@RequestParam String userName,@RequestParam Long merchantId) {
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUserName,userName)
                .eq(SysUser::getMerchantId,merchantId);
        SysUser one = userService.getOne(queryWrapper);
        return one==null?null:one.getGrade();
    }
    /**
     * 获取用户岗位角色信息
     *
     * @param userId
     * @return
     */
    @SneakyThrows
    @GetMapping(value = {"/userDetails/", "/userDetails/{userId}"})
    @ApiOperation(value = "获取用户信息")
    @ApiImplicitParam(name = "userId", value = "用户Id", dataType = "Long")
    public JsonResults userDetailsById(@PathVariable(value = "userId", required = false) Long userId) {
        JsonResults results = JsonResults.success();
        Long merchantId = SecurityUtils.getLoginUser().getMerchantId();
        results.put("roles", roleService.list(new QueryWrapper<SysRole>().lambda()
                .eq(null != merchantId && merchantId > 0, SysRole::getMerchantId, merchantId)));
        results.put("posts", postService.list(new QueryWrapper<SysPost>().lambda()
                .eq(null != merchantId && merchantId > 0, SysPost::getMerchantId, merchantId)));
        //===========解决渗透安全  个人中心返回字段进行加密
        SysUser sysUser = userService.getById(userId);
        if(sysUser != null){
            if(StringUtils.isNotEmpty(sysUser.getEmail())){
                sysUser.setEmail(CryptoUtils.encrypt(sysUser.getEmail()));
            }
            if(StringUtils.isNotEmpty(sysUser.getPhonenumber())){
                sysUser.setPhonenumber(CryptoUtils.encrypt(sysUser.getPhonenumber()));
            }
        }
        results.put("data", sysUser);
        results.put("roleIds", roleService.selectRoleIdsByUserId(userId));
        results.put("postIds", postService.selectPostById(userId));
        results.put("roleGroup", roleService.selectRolesByUserId(userId));
        results.put("postGroup", postService.selectPostsByUserId(userId));
        return results;
    }

    /**
     * 递归查询所有子级部门Id
     *
     * @param list
     * @param deptIds
     * @return
     * @author dingw
     * @Date 2020/5/12 17:10:52
     */
    private List<Long> queryDeptId(List<SysDept> list, List<Long> deptIds) {
        if (list != null && list.size() > 0) {
            for (SysDept entity : list) {
                List<SysDept> sysDeptList = sysDeptService.list(new QueryWrapper<SysDept>().lambda()
                        .eq(SysDept::getParentId, entity.getDeptId()));
                deptIds.add(entity.getDeptId());
                queryDeptId(sysDeptList, deptIds);
            }
        }
        return deptIds;
    }

    /**
     * 描述：
     *
     * @param deptId
     * @author dingw
     * @date 2020-05-13
     */
    private Long getByDept(Long deptId) {
        Long merchantId = 0L;
        if (deptId != null && deptId > 0) {
            SysDept SysDept = sysDeptService.getById(deptId);
            if (SysDept != null) {
                merchantId = SysDept.getMerchantId();
            }
        }
        return merchantId;
    }

    @GetMapping("/getUserAll")
    @ApiOperation("查询所有用户")
    public JsonResults getUserAll() {
        List<SysUser> ds = userService.list(new QueryWrapper<SysUser>()
                .lambda()
                .eq(SysUser::getDelFlag, "0")
                .orderByDesc(SysUser::getCreateTime)
        );
        List<Map<String, Object>> listMap = new ArrayList<>();
        for (SysUser sysUser : ds) {
            Map<String, Object> map = new HashMap<>();
            map.put("userId", "" + sysUser.getUserId());
            map.put("userName", sysUser.getUserName());
            listMap.add(map);
        }

        return JsonResults.success(listMap);
    }

    /**
     * 上传图片
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation(value = "上传头像")
    public JsonResults upload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if(fileName.equals("undefined")){
            return JsonResults.error("请选择图片进行上传头像");
        }
        String substring = fileName.substring(fileName.lastIndexOf("."));
        ArrayList<Object> objects = new ArrayList<>();
        objects.add(".jpg");
        objects.add(".png");
        objects.add(".gif");
        objects.add(".JPG");
        objects.add(".PNG");
        objects.add(".GIF");
        if (objects.stream().anyMatch(x -> x.equals(substring))) {
            try {
                String url = "";
                if (!file.isEmpty()) {
                    FileUploadTool fileUploadTool = new FileUploadTool();
                    // 上传到fastDFS方法
                    url = fileUploadTool.uploadFile(file.getBytes(), file.getOriginalFilename());
                    return JsonResults.success("上传成功", url);
                }
                return JsonResults.error("上传失败");
            } catch (Exception e) {
                e.printStackTrace();
                return JsonResults.error("上传失败");
            }
        } else {
            return JsonResults.error("请上传后缀名正确的头像");
        }
    }

    @PostMapping("/uploadFile")
    @ApiOperation(value = "上传头像")
    public JsonResults uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String url = "";
            if (!file.isEmpty()) {
                FileUploadTool fileUploadTool = new FileUploadTool();
                // 上传到fastDFS方法
                url = fileUploadTool.uploadFile(file.getBytes(), file.getOriginalFilename());
                return JsonResults.success("上传成功", url);
            }
            return JsonResults.error("上传失败");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResults.error("上传失败:"+e.getMessage());
        }
    }

    @PostMapping("/deleteFile")
    @ApiOperation(value = "删除文件")
    public JsonResults deleteFile(@RequestParam String fileId) {
        try {
            FileUploadTool fileUploadTool = new FileUploadTool();
            fileUploadTool.deleteFile(fileId);
            return JsonResults.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return JsonResults.error("删除失败");
        }
    }
    /**
     * 根据商户号查询用户
     *
     * @param merchantId
     * @return
     */
    @GetMapping("/getUsersByMerchantId")
    public JsonResults getUsersByMerchantId(Long merchantId) {
        return JsonResults.success(userService.getUsersByMerchantId(merchantId));
    }

    /**
     * 删除记录
     *
     * @param userId
     * @return
     */
    @DeleteMapping("/deleteRecord")
    public JsonResults deleteRecord(@RequestParam Long userId) {
        return userService.removeById(userId) ? JsonResults.success() : JsonResults.error();
    }

    /**
     * 根据商户号、角色查询用户
     *
     * @param merchantId
     * @param roleIds
     * @return
     */
    @GetMapping("/getUsersByRolesMerchantId")
    public JsonResults getUsersByRolesMerchantId(@RequestParam Long merchantId, @RequestParam String roleIds) {
        return userService.getUsersByRolesMerchantId(merchantId, roleIds);
    }

    public LambdaQueryWrapper<SysUser> queryWrapper(SysUser entity) {
        return new QueryWrapper<SysUser>().lambda()
                .likeRight(!StringUtils.isEmpty(entity.getUserName()), SysUser::getUserName, entity.getUserName())
                .likeRight(!StringUtils.isEmpty(entity.getPhonenumber()), SysUser::getPhonenumber, entity.getPhonenumber())
                .eq(!StringUtils.isEmpty(entity.getDelFlag()), SysUser::getDelFlag, entity.getDelFlag())
                .eq(!StringUtils.isEmpty(entity.getStatus()), SysUser::getStatus, entity.getStatus())
                .in(!StringUtils.isEmpty(entity.getDeptIds()), SysUser::getDeptId, entity.getDeptIds())
                .eq(null != entity.getMerchantId() && entity.getMerchantId() > 0, SysUser::getMerchantId, entity.getMerchantId())
                .ge(null != entity.getBeginDate() && !"".equals(entity.getBeginDate()), SysUser::getCreateTime, entity.getBeginDate())
                .le(null != entity.getEndDate() && !"".equals(entity.getEndDate()), SysUser::getCreateTime, entity.getEndDate())
                .eq(null != entity.getUserId(), SysUser::getUserId, entity.getUserId())
                .orderByDesc(SysUser::getCreateTime);
    }

    /**
     * 获取用户角色详细信息
     *
     * @param roleType
     * @param merchantId
     * @return
     */
    @GetMapping("/getUserRoleInfo")
    public JsonResults getUserRoleInfo(@RequestParam(value = "roleType", required = false) String roleType, @RequestParam(value = "merchantId") Long merchantId) {
        return JsonResults.success(userService.getUserRoleInfo(roleType, merchantId));
    }

    /**
     * 根据手机号码获取用户信息
     * */
    @GetMapping("/info/getUserByPhone")
    public R<UserInfo> getUserByPhone(@RequestParam String phoneNumber) {
        SysUser sysUser = new SysUser();
        sysUser.setPhonenumber(phoneNumber);
        sysUser = userService.selectUserByPoneNumber(sysUser.getPhonenumber(), null);
        UserInfo sysUserVo = null;
        if (StringUtils.isNotNull(sysUser)) {
            sysUserVo = new UserInfo();
            // 角色集合
            Set<String> roles = permissionService.getRolePermission(sysUser.getUserId());
            // 权限集合
            Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId(), sysUser.getMerchantId());
            sysUserVo.setSysUser(sysUser);
            sysUserVo.setRoles(roles);
            sysUserVo.setPermissions(permissions);
        }else{
            R.fail("用戶名或密碼錯誤");
        }
        return R.ok(sysUserVo);
    }
    @GetMapping("/info/getUserByPhoneAndMerchantId")
    public R<UserInfo> getUserByPhoneAndMerchantId(@RequestParam String phoneNumber,@RequestParam String merchantId) {
        SysUser sysUser = new SysUser();
        sysUser.setPhonenumber(phoneNumber);
        sysUser = userService.selectUserByPoneNumber(sysUser.getPhonenumber(),merchantId);
        UserInfo sysUserVo = null;
        if (StringUtils.isNotNull(sysUser)) {
            sysUserVo = new UserInfo();
            // 角色集合
            Set<String> roles = permissionService.getRolePermission(sysUser.getUserId());
            // 权限集合
            Set<String> permissions = permissionService.getMenuPermission(sysUser.getUserId(), sysUser.getMerchantId());
            sysUserVo.setSysUser(sysUser);
            sysUserVo.setRoles(roles);
            sysUserVo.setPermissions(permissions);
        }else{
            R.fail("用戶名或密碼錯誤");
        }
        return R.ok(sysUserVo);
    }

    /**
     * 提供给微信授权添加openId，根据手机号码获取用户信息
     * */
    @GetMapping("/info/getUserForPhone")
    public List<SysUser> getUserForPhone(@RequestParam String phoneNumber) {
        SysUser sysUser = new SysUser();
        sysUser.setPhonenumber(phoneNumber);
        List<SysUser> list = userService.list(queryWrapper(sysUser));
        return list;
    }

    /**
     * 提供给微信授权添加openId的接口
     * @param user
     * @return
     */
    @PutMapping("/info/editUserForPhone")
    public JsonResults editUserForPhone(@RequestBody SysUser user) {
        return JsonResults.success(userService.updateById(user));
    }

    /**
     * 提供给微信授权添加openId，根据手机号码获取用户信息
     * */
    @GetMapping("/info/getUserForId")
    public SysUser getUserForId(@RequestParam Long userId) {
        SysUser sysUser = new SysUser();
        sysUser.setUserId(userId);
        SysUser user = userService.getOne(queryWrapper(sysUser));
        return user;
    }
    /**
     * 提供给微信授权添加openId，根据手机号码获取用户信息
     * */
    @GetMapping("/getUserListByRoleIds")
    public List<SysUser> getUserListByRoleIds(@RequestParam String roleIds,Long merchantId) {
        String[] roleIdArr = roleIds.split(",");
        return userService.getUserListByRoleIds(roleIdArr, merchantId);
    }

    /**
     * 获取当前用户信息
     */
    @PostMapping("/info/sendSms")
    public JsonResults sendSms(@RequestParam String phoneNumber,@RequestParam String merchantId) {

        return   userService.sendSms(phoneNumber,merchantId);

    }

}
