package com.dfc.ind.service.impl.merchant;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dfc.ind.common.core.constant.CommonConstants;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.merchant.AgrMerchantInfoEntity;
import com.dfc.ind.entity.merchant.EveAuthInfoEntity;
import com.dfc.ind.entity.merchant.EveAuthInfoVO;
import com.dfc.ind.entity.sys.*;
import com.dfc.ind.entity.vo.EveApprovalInfoVO;
import com.dfc.ind.mapper.merchant.AgrMerchantInfoMapper;
import com.dfc.ind.mapper.merchant.EveAuthInfoMapper;
import com.dfc.ind.mapper.sys.*;
import com.dfc.ind.service.merchant.IAgrMerchantInfoService;
import com.dfc.ind.service.merchant.IEveAuthInfoService;
import com.dfc.ind.service.sys.ISysDeptService;
import com.dfc.ind.service.sys.ISysMenuService;
import com.dfc.ind.service.sys.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * <p>
 * 中台认证信息表 服务实现类
 * </p>
 *
 * @author ylyan
 * @since 2020-03-28
 */
@Service
public class EveAuthInfoServiceImpl extends ServiceImpl<EveAuthInfoMapper, EveAuthInfoEntity> implements IEveAuthInfoService {

    @Autowired
    private AgrMerchantInfoMapper agrMerchantInfoMapper;

    @Autowired
    private IAgrMerchantInfoService agrMerchantInfoService;



    @Autowired
    private ISysMenuService sysMenuService;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private ISysUserService mallUserService;







    @Autowired
    public JdbcTemplate jdbcTemplate;

    @Autowired
    private  EveAuthInfoMapper  EveAuthInfoMapper;

    @Value("${wechat.ip}")
    private  String  ip;

    @Value("${wechat.port}")
    private String port;

    @Value("${wechat.phone}")
    private String phone;



    /**
     * <审批商户申请>
     *
     * @param eveApprovalInfoVO 审批信息封装
     * @return
     * @author dingw
     * @Date 2020/5/6 10:56
     */
    @Override
    //@GlobalTransactional(timeoutMills = 3000000)
    public JsonResults approval(EveApprovalInfoVO eveApprovalInfoVO) {
        if (eveApprovalInfoVO.getMerchatid()==null||eveApprovalInfoVO.getMerchatid().compareTo(0L)==0){
            return JsonResults.error("该商户号不能为空或不能为0");
        }
        int count = agrMerchantInfoService.count(new QueryWrapper<AgrMerchantInfoEntity>().lambda()
                .eq(AgrMerchantInfoEntity::getMerchantId, eveApprovalInfoVO.getMerchatid()));
        if (count>0){
            return JsonResults.error("该商户号已经存在");
        }
        //商户同步
        Long merchantId=eveApprovalInfoVO.getMerchatid();
        //获取认证id
        Long authId = eveApprovalInfoVO.getAuthId();
        //获取审批状态
        String approvalStatus = eveApprovalInfoVO.getApprovalStatus();
        //通过认证id获取认证信息
        EveAuthInfoEntity entity = baseMapper.selectById(authId);
        //分别在认证信息里加入审批明细
        entity.setApprovalStatus(approvalStatus);
        entity.setUpdateBy(SecurityUtils.getUserName());
        entity.setUpdateTime(DateUtils.getNowDate());
        boolean updateById = this.updateById(entity);
        if (!updateById) {
            return JsonResults.error();
        }
        boolean pass = CommonConstants.ObjectNum.OBJECT_NUM_1.equals(approvalStatus);
        //1 个人认证  2 商户认证
        if (CommonConstants.ObjectNum.OBJECT_NUM_1.equals(entity.getAuthType()) && pass) {
            //实名认证审批
        } else if (CommonConstants.ObjectNum.OBJECT_NUM_2.equals(entity.getAuthType()) && pass) {
            //商户认证审批
            AgrMerchantInfoEntity agrMerchantInfoEntity = new AgrMerchantInfoEntity();
            /////SQ或许商户id
          // Long merchatId = jdbcTemplate.queryForObject(AutoGenerateNo.map.get(AutoGenerateNo.MERCHANT_TITLE), Long.class);
            agrMerchantInfoEntity.setMerchantId(merchantId);
            ////////////////////////////////////////////////////////////////////////
            agrMerchantInfoEntity.setAuthId(authId);
            agrMerchantInfoEntity.setMerchantName(entity.getMerchantName());
            agrMerchantInfoEntity.setApprovalStatus(entity.getApprovalStatus());
            agrMerchantInfoEntity.setCreateBy(SecurityUtils.getUserName());
            agrMerchantInfoEntity.setCreateTime(DateUtils.getNowDate());
            agrMerchantInfoEntity.setLegalName(entity.getLegalName());
            agrMerchantInfoEntity.setTelephone(entity.getTelephone());
            agrMerchantInfoEntity.setEmail(entity.getEmail());
            agrMerchantInfoEntity.setAddress(entity.getAddress());
            agrMerchantInfoEntity.setCredentialsNo(entity.getCredentialsNo());
            agrMerchantInfoEntity.setCredentialsAddress(entity.getCredentialsAddress());
            //==========审批时插入所属机构及所属机构编号
            agrMerchantInfoEntity.setMechanism(entity.getMechanism());
            agrMerchantInfoEntity.setSerialNumber(entity.getSerialNumber());
            //==========审批时插入法人身份证正面、反面
//            agrMerchantInfoEntity.setIdCard(entity.getIdCard());
//            agrMerchantInfoEntity.setIdCardtails(entity.getIdCardtails());
            int insert = agrMerchantInfoMapper.insert(agrMerchantInfoEntity);
            if (insert <= 0) {
                return JsonResults.error();
            }
            //商户审批通过后默认给用户添加商户角色
            List<SysRole> roleEntityList = sysRoleMapper.selectList(new QueryWrapper<SysRole>().lambda().like(SysRole::getRoleName, "商户管理员").eq(SysRole::getMerchantId, 1L));
            if (null == roleEntityList || roleEntityList.size() <= 0) {
                return JsonResults.error("请先添加商户角色");
            }
            int rows = 0;
            List<Long> roleIds = new ArrayList<>();
            for (SysRole SysRole : roleEntityList) {
                roleIds.add(SysRole.getRoleId());
            }
            if (StringUtils.isNotNull(roleIds) && roleIds.size() > 0) {
                // 新增用户与角色管理
                List<SysUserRole> list = new ArrayList<SysUserRole>();
                List<SysMenu> menuList = new ArrayList<SysMenu>();
                Date nowDate = DateUtils.getNowDate();

                for (Long roleId : roleIds) {
                    SysUserRole up = new SysUserRole();
                    up.setUserId(entity.getUserId());
                    up.setRoleId(roleId);
                    list.add(up);

                    //复制商户角色对应的菜单给新工厂商户

                    menuList = sysMenuService.getByRole(roleId);
                    if (menuList != null && menuList.size() > 0) {
                        for (SysMenu sysMenuEntity : menuList) {
                            sysMenuEntity.setMerchantId(merchantId);
                            sysMenuEntity.setCreateBy(SecurityUtils.getUserName());
                            sysMenuEntity.setCreateTime(nowDate);
                            sysMenuEntity.setUpdateBy(null);
                            sysMenuEntity.setUpdateTime(null);
                            sysMenuMapper.insertInto(sysMenuEntity);
                        }
                       // sysMenuMapper.insertIntoList(menuList);
                    }


                }
                if (list.size() > 0) {
                   // rows = sysUserRoleMapper.batchUserRole(list);
                }
            }


            SysUser mallUserEntity = mallUserService.getById(entity.getUserId());
            int save = 0;
            //通过商户名称，商户id 获取到所在的部门 如果为空 将商户名称作为部门名称新增部门
            SysDept byDeptName = sysDeptService.getByDeptName(entity.getMerchantName(), merchantId);
            if (byDeptName==null){
                SysDept SysDept = new SysDept();
                SysDept.setParentId(0L);
                SysDept.setAncestors("0");
                SysDept.setDeptName(entity.getMerchantName());
                SysDept.setOrderNum("0");
                SysDept.setUserId(entity.getUserId());
                SysDept.setLeader(entity.getLegalName());
                SysDept.setPhone(entity.getTelephone());
                SysDept.setEmail(entity.getEmail());
                SysDept.setStatus("0");
                SysDept.setDelFlag("0");
                SysDept.setCreateBy(SecurityUtils.getUserName());
                SysDept.setCreateTime(DateUtils.getNowDate());
                SysDept.setMerchantId(merchantId);
                save = sysDeptMapper.insert(SysDept);
            }

            if (save <= 0) {
                return JsonResults.error();
            } else {
                //获取子部门id
                Long deptId = sysDeptService.getOne(new QueryWrapper<SysDept>().lambda()
                        .eq(SysDept::getMerchantId, merchantId)).getDeptId();
                List<Long> sonDeptIds = sysDeptService.getSonDeptIdList(deptId);
                if(null != sonDeptIds && sonDeptIds.size() > 0) {
                    //将子部门所有用户类型改为01（商户用户）,并设置商户id
                    mallUserService.updateUserType(sonDeptIds, merchantId);
                    //将完善子部门信息
                    SysDept dept = new SysDept();
                    Map<String, Object> map = new HashMap<>();
                    map.put("userId", entity.getUserId());
                    map.put("leader", entity.getLegalName());
                    map.put("phone", entity.getTelephone());
                    map.put("email", entity.getEmail());
                    map.put("merchantId", merchantId);
                    map.put("uodateBy", SecurityUtils.getUserName());
                    map.put("updateTime", DateUtils.getNowDate());
                    map.put("idList", sonDeptIds);
                    sysDeptService.updateMerchantIdDept(map);
                }
                //修改新商户用户信息
                mallUserEntity.setDeptId(deptId);
                mallUserEntity.setMerchantId(merchantId);
               // mallUserEntity.setPhonenumber(entity.getTelephone());
                mallUserEntity.setRealName(entity.getLegalName());
                mallUserEntity.setEmail(entity.getEmail());
                mallUserEntity.setCreateBy(SecurityUtils.getUserName());
                mallUserEntity.setCreateTime(DateUtils.getNowDate());
                mallUserEntity.setUserType("01");
                mallUserService.updateById(mallUserEntity);
                List<SysDept> SysDeptList = sysDeptService.list(new QueryWrapper<SysDept>().lambda()
                        .eq(SysDept::getMerchantId, 1L));
//                        .eq(SysDept::getCreateBy, "ding"));
                //复制初始部门给新工厂商户
                if (SysDeptList != null && SysDeptList.size() > 0) {
                    for (SysDept dept : SysDeptList) {
                        dept.setParentId(deptId);
                        dept.setCreateBy(SecurityUtils.getUserName());
                        dept.setCreateTime(DateUtils.getNowDate());
                        dept.setMerchantId(merchantId);
                        dept.setLeader(entity.getLegalName());
                        dept.setPhone(entity.getTelephone());
                        dept.setEmail(entity.getEmail());
                        dept.setUserId(entity.getUserId());
                        int saveDept = sysDeptMapper.insert(dept);
                        if (saveDept <= 0) {
                            return JsonResults.error();
                        }
                    }
                }
            }
            //复制初始角色给新工厂商户

            List<SysRole> roleList = sysRoleMapper.selectList(new QueryWrapper<SysRole>().lambda()
                    .eq(SysRole::getMerchantId, 1L));
                    /*.eq(SysRole::getCreateBy, "ding"));*/
            //新商户管理ID
            Long ShId=null;
            for (SysRole role : roleList) {

                List<SysRoleMenu> roleMenuList = sysRoleMapper.getByRole(role.getRoleId());
                role.setCreateBy(SecurityUtils.getUserName());
                role.setCreateTime(DateUtils.getNowDate());
                role.setMerchantId(merchantId);
//                StringBuffer url = new StringBuffer();
                int saveRole = sysRoleMapper.insert(role);
                if(role.getRoleName().equals("商户管理员")){
                    ShId=role.getRoleId();
                }
                if (saveRole <= 0) {
                    return JsonResults.error();
                } else {
                    Long roleId = role.getRoleId();
                    List<SysRoleMenu> roleMenuLists = new ArrayList<>();
                    if (roleMenuList != null && roleMenuList.size() > 0) {
                        for (SysRoleMenu roleMenu : roleMenuList) {
                            roleMenu.setRoleId(roleId);
                            roleMenuLists.add(roleMenu);
                        }
                        int row = sysRoleMapper.batchRoleMenu(roleMenuLists);
                        if (row <= 0) {
                            return JsonResults.error();
                        }
                    }
                }
            }
            List<SysUserRole> list = new ArrayList<SysUserRole>();
            SysUserRole up = new SysUserRole();
            up.setUserId(entity.getUserId());
            up.setRoleId(ShId);
            list.add(up);
            if (list.size() > 0) {
                rows = sysUserRoleMapper.batchUserRole(list);
            }
            if (rows <= 0) {
                return JsonResults.error();
            }
        }
        /*消息内容
        CMsgMailBodyEntity msgBody = msgMailBodyService.getOne(new QueryWrapper<CMsgMailBodyEntity>().lambda().eq(CMsgMailBodyEntity::getType, 21));
        String userName = msgBody.getUserName();
        List<String> roleIds = Arrays.asList(userName.split(",")).stream().map(s -> (s.trim())).collect(Collectors.toList());
        String content = msgBody.getContent();
        for (String roleId : roleIds) {
            if (roleId.equals(0)) {
                String newContent = content.replace("$", SecurityUtils.getUsername());
                //插入消息明细
                CMsgMailListEntity cMsgMailListEntity = new CMsgMailListEntity();
                cMsgMailListEntity.setContent(newContent);
                cMsgMailListEntity.setMailId(msgBody.getMailId());
                cMsgMailListEntity.setUserName(SecurityUtils.getUsername());
                cMsgMailListEntity.setIsRead(StatusEnum.status_notread.getCode());
                msgMailListService.save(cMsgMailListEntity);
            }
        }
        List<String> users = sysUserMapper.selectUserById(roleIds);
        for (String user : users) {
            if(user.equals(SecurityUtils.getUsername())){

            }
            String newContent = content.replace("$", user);
            //插入消息明细
            CMsgMailListEntity cMsgMailListEntity = new CMsgMailListEntity();
            cMsgMailListEntity.setContent(newContent);
            cMsgMailListEntity.setMailId(msgBody.getMailId());
            cMsgMailListEntity.setUserName(user);
            cMsgMailListEntity.setIsRead(StatusEnum.status_notread.getCode());
            msgMailListService.save(cMsgMailListEntity);
        }
*/
        //管理员账号登录获取token
//        Boolean  ifuser=false;
//        RestTemplate restTemplate = new RestTemplate();
//        String phoneNumber = phone;
//        String loginUrl = "http://"+ip+":"+port+"/api/ind-auth/oauth/token?username=" + phoneNumber
//                + "&password=&grant_type=password&scope=server&client_id=web&client_secret=123456&loginFlag=false";
//        Map<String, Object> loginResult = restTemplate.getForObject(loginUrl, Map.class);
//        String access_token =(String)loginResult.get("access_token");
//        MediaType mediaType =  MediaType.parseMediaType("application/json");
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(mediaType);
//        headers.add("accept",MediaType.ALL_VALUE);
//        headers.add("Authorization",(String)loginResult.get("token_type")+" "+access_token);
//        //用户新增
//        SysUser  sysUser = sysUserMapper.selectById(entity.getUserId());
//
//        JSONObject  jsonObjectuser = new JSONObject();
//        jsonObjectuser.put("password",sysUser.getPassword());
//        jsonObjectuser.put("grade","");
//        jsonObjectuser.put("phonenumber",sysUser.getPhonenumber());
//        //jsonObjectuser.put("userId",sysUser.getUserId());
//        jsonObjectuser.put("nickName",sysUser.getNickName());
//        jsonObjectuser.put("userName",sysUser.getUserName());
//        jsonObjectuser.put("sex",sysUser.getSex());
//        jsonObjectuser.put("email",sysUser.getEmail());
//        jsonObjectuser.put("status",sysUser.getStatus());
//        jsonObjectuser.put("deptId",sysUser.getDeptId());
//        HttpEntity EntityUser = new HttpEntity(jsonObjectuser,headers);
//        String userurl="http://"+ip+":"+port+"/api/ind-system/user/yxadd";
//        ResponseEntity<Map> stringResponseEntity = restTemplate.postForEntity(userurl, EntityUser, Map.class);
//        if(stringResponseEntity.getBody().get("code").equals(200)){
//            ifuser=true;
//        }
//        Boolean  ifauth=false;
//        //商户申请
//        if(ifuser){
//            EveAuthInfoEntity EntityEveAuthInfo = baseMapper.selectById(authId);
//            JSONObject  jsonObjectEvauthinfo = new JSONObject();
//            jsonObjectEvauthinfo.put("address",EntityEveAuthInfo.getAddress());
//            jsonObjectEvauthinfo.put("applyType",EntityEveAuthInfo.getApplyType());
//            jsonObjectEvauthinfo.put("authType",EntityEveAuthInfo.getAuthType());
//            jsonObjectEvauthinfo.put("authId",EntityEveAuthInfo.getAuthId());
//            jsonObjectEvauthinfo.put("credentialsAddress",EntityEveAuthInfo.getCredentialsAddress());
//            jsonObjectEvauthinfo.put("credentialsNo",EntityEveAuthInfo.getCredentialsNo());
//            jsonObjectEvauthinfo.put("legalName",EntityEveAuthInfo.getLegalName());
//            jsonObjectEvauthinfo.put("merchantName",EntityEveAuthInfo.getMerchantName());
//            jsonObjectEvauthinfo.put("email",EntityEveAuthInfo.getEmail());
//            jsonObjectEvauthinfo.put("telephone",EntityEveAuthInfo.getTelephone());
//            jsonObjectEvauthinfo.put("userId",stringResponseEntity.getBody().get("data"));
//            HttpEntity HttpEntityEvauthinfo = new HttpEntity(jsonObjectEvauthinfo,headers);
//            String Evauserurl="http://"+ip+":"+port+"/api/ind-system/authentication/admin/applyMch";
//            ResponseEntity<Map> EvauthInfoResponseEntity = restTemplate.postForEntity(Evauserurl, HttpEntityEvauthinfo, Map.class);
//            if(EvauthInfoResponseEntity.getBody().get("code").equals(200)){
//                ifauth=true;
//            }
//        }else{
//            return JsonResults.error(300,"服务器用户新增未成功");
//        }
//        if(ifauth){
//            //运管需要去-->207调用商户认证;
//            JSONObject  jsonObjectapproval = new JSONObject();
//            jsonObjectapproval.put("approvalStatus",1);
//            jsonObjectapproval.put("authId",authId);
//            jsonObjectapproval.put("merchatid",merchantId);
//            HttpEntity entityapproval = new HttpEntity(jsonObjectapproval,headers);
//            String approvaluserurl="http://"+ip+":"+port+"/api/ind-system/authentication/approval";
//            ResponseEntity<Map> mapResponseEntity = restTemplate.postForEntity(approvaluserurl, entityapproval, Map.class);
//            if(mapResponseEntity.getBody().get("code").equals(200)){
//                return JsonResults.success();
//            }
//        }else{
//            return JsonResults.error(300,"服务器商户新增未成功");
//        }


        return JsonResults.success();
    }

    /**
     * <获取结果集>
     *
     * @param jsonResults
     * @return
     * @author ylyan
     * @Date 2020/4/26 20:50
     */
    private String getJsonString(JsonResults jsonResults) {
        Map map = (Map) jsonResults.get("data");
        int code = (int) jsonResults.get("code");
        if (CommonConstants.SUCCESS == code) {
            return JSONObject.toJSONString(map);
        }
        return null;
    }


    /**
     * <用户请求成为商户>
     *
     * @param entity 审批信息对象
     * @param userId 用户id
     * @return
     * @author ylyan
     * @Date 2020/4/26 10:37
     */
    @Override
    public JsonResults applyMch(EveAuthInfoVO entity, Long userId) {
        if (null != userId && userId > 0) {
            EveAuthInfoEntity eveAuthInfoEntity = new EveAuthInfoEntity();
            eveAuthInfoEntity.setUserId(userId);
            //
            eveAuthInfoEntity.setAuthType(entity.getAuthType());
            eveAuthInfoEntity.setAuthId(null);
            eveAuthInfoEntity.setApplyType(entity.getApplyType());
            eveAuthInfoEntity.setMerchantName(entity.getMerchantName());
            eveAuthInfoEntity.setLegalName(entity.getLegalName());
            eveAuthInfoEntity.setCredentialsNo(entity.getCredentialsNo());
            eveAuthInfoEntity.setCredentialsAddress(entity.getCredentialsAddress());
            eveAuthInfoEntity.setEmail(entity.getEmail());
            eveAuthInfoEntity.setTelephone(entity.getTelephone());
            eveAuthInfoEntity.setAddress(entity.getAddress());
            eveAuthInfoEntity.setCreateBy(SecurityUtils.getUserName());
            eveAuthInfoEntity.setCreateTime(DateUtils.getNowDate());
            //=========添加商户申请时把所属机构及所属机构编号放到实体类
            eveAuthInfoEntity.setMechanism(entity.getMechanism());
            eveAuthInfoEntity.setSerialNumber(entity.getSerialNumber());
            //=========添加商户申请时把法人身份证正反面放到实体类
//            eveAuthInfoEntity.setIdCard(entity.getIdCard());
//            eveAuthInfoEntity.setIdCardtails(entity.getIdCardtails());
            boolean insert = this.save(eveAuthInfoEntity);
            if (!insert) {
                return JsonResults.error("申请失败,请稍后重试");
            }
        } else {
            return JsonResults.error("申请失败,请稍后重试");
        }
        return JsonResults.success("申请成功,请耐心等待审批");
    }

    @Override
    public IPage pageList(Page startPage, EveAuthInfoEntity entity) {
        QueryWrapper<EveAuthInfoEntity> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(!StringUtils.isEmpty(entity.getAddress()), EveAuthInfoEntity::getAddress, entity.getAddress())
                .likeRight(!StringUtils.isEmpty(entity.getMerchantName()), EveAuthInfoEntity::getMerchantName, entity.getMerchantName())
                .ge(null != entity.getBeginDate() && !"".equals(entity.getBeginDate()), EveAuthInfoEntity::getCreateTime, entity.getBeginDate())
                .le(null != entity.getEndDate() && !"".equals(entity.getEndDate()), EveAuthInfoEntity::getCreateTime, entity.getEndDate())
                .eq(StringUtils.isNotEmpty(entity.getApprovalStatus()), EveAuthInfoEntity::getApprovalStatus, entity.getApprovalStatus())
                .eq(StringUtils.isNotEmpty(entity.getAuthType()), EveAuthInfoEntity::getAuthType, entity.getAuthType())
                .eq(StringUtils.isNotEmpty(entity.getApplyType()), EveAuthInfoEntity::getApplyType, entity.getApplyType())
        ;
        List<EveAuthInfoEntity> eveAuthInfoEntities = baseMapper.selectList(queryWrapper);

        return this.page(startPage, queryWrapper);
    }

    @Override
    public JsonResults pageLists(Page startPage, EveAuthInfoEntity entity) {

        Map  map = new HashMap();
        QueryWrapper<EveAuthInfoEntity> queryWrapper = new QueryWrapper();
        queryWrapper.lambda()
                .likeRight(!StringUtils.isEmpty(entity.getAddress()), EveAuthInfoEntity::getAddress, entity.getAddress())
                .likeRight(!StringUtils.isEmpty(entity.getMerchantName()), EveAuthInfoEntity::getMerchantName, entity.getMerchantName())
                .ge(null != entity.getBeginDate() && !"".equals(entity.getBeginDate()), EveAuthInfoEntity::getCreateTime, entity.getBeginDate())
                .le(null != entity.getEndDate() && !"".equals(entity.getEndDate()), EveAuthInfoEntity::getCreateTime, entity.getEndDate())
                .eq(StringUtils.isNotEmpty(entity.getApprovalStatus()), EveAuthInfoEntity::getApprovalStatus, entity.getApprovalStatus())
                .eq(StringUtils.isNotEmpty(entity.getAuthType()), EveAuthInfoEntity::getAuthType, entity.getAuthType())
                .eq(StringUtils.isNotEmpty(entity.getApplyType()), EveAuthInfoEntity::getApplyType, entity.getApplyType())
        ;
        Page page = EveAuthInfoMapper.selectPage(startPage, queryWrapper);
        List<EveAuthInfoEntity> eveAuthInfoEntities = page.getRecords();
        List<EveAuthInfoVO>  eveAuthInfoVOS = new ArrayList<>();
        List<EveAuthInfoEntity> size = baseMapper.selectList(queryWrapper);
        for (int i = 0; i <eveAuthInfoEntities.size() ; i++) {
            EveAuthInfoVO  eveAuthInfoVO  = new EveAuthInfoVO();
            eveAuthInfoVO.setAddress(eveAuthInfoEntities.get(i).getAddress());
            eveAuthInfoVO.setApplyType(eveAuthInfoEntities.get(i).getApplyType());
            eveAuthInfoVO.setAuthType(eveAuthInfoEntities.get(i).getAuthType());
          SysUser  sysUser =  sysUserMapper.selectUserById(eveAuthInfoEntities.get(i).getUserId());
          if(sysUser!=null){
              eveAuthInfoVO.setMerchantId( String.valueOf(sysUserMapper.selectUserById(eveAuthInfoEntities.get(i).getUserId()).getMerchantId()));
          }
            eveAuthInfoVO.setCredentialsAddress(eveAuthInfoEntities.get(i).getCredentialsAddress());
            eveAuthInfoVO.setEmail(eveAuthInfoEntities.get(i).getEmail());
            eveAuthInfoVO.setCredentialsNo(eveAuthInfoEntities.get(i).getCredentialsNo());
            eveAuthInfoVO.setLegalName(eveAuthInfoEntities.get(i).getLegalName());
            eveAuthInfoVO.setMerchantName(eveAuthInfoEntities.get(i).getMerchantName());
            eveAuthInfoVO.setUserId(eveAuthInfoEntities.get(i).getUserId());
            eveAuthInfoVO.setAuthId(eveAuthInfoEntities.get(i).getAuthId());
            eveAuthInfoVO.setCreateBy(eveAuthInfoEntities.get(i).getCreateBy());
            eveAuthInfoVO.setCreateTime(eveAuthInfoEntities.get(i).getCreateTime());
            eveAuthInfoVO.setUpdateBy(eveAuthInfoEntities.get(i).getUpdateBy());
            eveAuthInfoVO.setUpdateTime(eveAuthInfoEntities.get(i).getUpdateTime());
            eveAuthInfoVO.setTelephone(eveAuthInfoEntities.get(i).getTelephone());
            eveAuthInfoVO.setApprovalStatus(eveAuthInfoEntities.get(i).getApprovalStatus());
            eveAuthInfoVOS.add(eveAuthInfoVO);
        }
        map.put("data",eveAuthInfoVOS);
        map.put("total",size.size());
        return JsonResults.success(map);
    }



    /**
     * <校验用户申请类型是否唯一>
     *
     * @param entity 认证信息对象
     * @return
     * @author ylyan
     * @Date 2020/4/26 17:13
     */
    @Override
    public String checkAuthTypeUnique(EveAuthInfoEntity entity) {
        Long authId = StringUtils.isNull(entity.getAuthId()) ? -1L : entity.getAuthId();
        EveAuthInfoEntity authType = this.getOne(new QueryWrapper<EveAuthInfoEntity>().lambda()
                .eq(EveAuthInfoEntity::getUserId, entity.getUserId())
                .eq(EveAuthInfoEntity::getAuthType, entity.getAuthType())
        );
        if (StringUtils.isNotNull(authType) && authType.getAuthId().longValue() != authId.longValue()) {
            return CommonConstants.FlgCode.FLG_Y;
        }
        return CommonConstants.FlgCode.FLG_N;
    }

    @Override
    public JsonResults fbapproval(EveAuthInfoVO eveAuthInfoVO) {
        //请求头基础信息
        RestTemplate restTemplate = new RestTemplate();
        String phoneNumber = "15888888886";
        String loginUrl = "http://10.1.110.60:18888/api/ind-auth/oauth/token?username=" + phoneNumber
                + "&password=&grant_type=password&scope=server&client_id=web&client_secret=123456&loginFlag=false";
        Map<String, Object> loginResult = restTemplate.getForObject(loginUrl, Map.class);
        String access_token =(String)loginResult.get("access_token");
        MediaType mediaType =  MediaType.parseMediaType("application/json");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.add("accept",MediaType.ALL_VALUE);
        headers.add("Authorization",(String)loginResult.get("token_type")+" "+access_token);
        SysUser  sysUser = sysUserMapper.selectById(eveAuthInfoVO.getUserId());
       //查询用户是否创建
        JSONObject  ISUSER = new JSONObject();
        ISUSER.put("userName",sysUser.getUserName());
        ISUSER.put("phonenumber",sysUser.getPhonenumber());
        ISUSER.put("merchantId",sysUser.getMerchantId());
        //authIdiS
        ISUSER.put("openId",eveAuthInfoVO.getAuthId());
        HttpEntity ISEntityUser = new HttpEntity(ISUSER,headers);
        ResponseEntity<Map> ISstringResponseEntity = restTemplate.postForEntity("http://10.1.110.60:18888/api/ind-system/user/IsuserAndAuthId", ISUSER, Map.class);
     //
        Boolean  ifuser=false;
        //用户新增
        SysDept sysDept = sysDeptMapper.selectBydeptName("平台管理团队");
        JSONObject  jsonObjectuser = new JSONObject();
        jsonObjectuser.put("password",sysUser.getPassword());
        jsonObjectuser.put("grade","");
        jsonObjectuser.put("phonenumber",sysUser.getPhonenumber());
        //jsonObjectuser.put("userId",sysUser.getUserId());
        jsonObjectuser.put("nickName",sysUser.getNickName());
        jsonObjectuser.put("userName",sysUser.getUserName());
        jsonObjectuser.put("sex",sysUser.getSex());
        jsonObjectuser.put("email",sysUser.getEmail());
        jsonObjectuser.put("status",sysUser.getStatus());
        jsonObjectuser.put("deptId",sysDept.getDeptId());
        HttpEntity EntityUser = new HttpEntity(jsonObjectuser,headers);
        String userurl="http://"+System.getProperty("JAVA_TTIP")+":8603/ind-system/user";
        ResponseEntity<Map> stringResponseEntity = restTemplate.postForEntity("http://10.1.110.60:18888/api/ind-system/user/yxadd", EntityUser, Map.class);
        if(stringResponseEntity.getBody().get("code").equals(200)){
            ifuser=true;
        }
        Boolean  ifauth=false;
        //商户申请
        if(ifuser){
            EveAuthInfoEntity EntityEveAuthInfo = baseMapper.selectById(eveAuthInfoVO.getAuthId());
            JSONObject  jsonObjectEvauthinfo = new JSONObject();
            jsonObjectEvauthinfo.put("address",EntityEveAuthInfo.getAddress());
            jsonObjectEvauthinfo.put("applyType",EntityEveAuthInfo.getApplyType());
            jsonObjectEvauthinfo.put("authType",EntityEveAuthInfo.getAuthType());
            jsonObjectEvauthinfo.put("authId",EntityEveAuthInfo.getAuthId());
            jsonObjectEvauthinfo.put("credentialsAddress",EntityEveAuthInfo.getCredentialsAddress());
            jsonObjectEvauthinfo.put("credentialsNo",EntityEveAuthInfo.getCredentialsNo());
            jsonObjectEvauthinfo.put("legalName",EntityEveAuthInfo.getLegalName());
            jsonObjectEvauthinfo.put("merchantName",EntityEveAuthInfo.getMerchantName());
            jsonObjectEvauthinfo.put("email",EntityEveAuthInfo.getEmail());
            jsonObjectEvauthinfo.put("telephone",EntityEveAuthInfo.getTelephone());
            jsonObjectEvauthinfo.put("userId",stringResponseEntity.getBody().get("data"));
            //==================商户申请时添加所属机构/所属机构编号/法人身份证正反面到208商户表
            jsonObjectEvauthinfo.put("mechanism",EntityEveAuthInfo.getMechanism());
            jsonObjectEvauthinfo.put("serialNumber",EntityEveAuthInfo.getSerialNumber());
//            jsonObjectEvauthinfo.put("idCard",EntityEveAuthInfo.getIdCard());
//            jsonObjectEvauthinfo.put("idCardtails",EntityEveAuthInfo.getIdCardtails());
            HttpEntity HttpEntityEvauthinfo = new HttpEntity(jsonObjectEvauthinfo,headers);
            ResponseEntity<Map> EvauthInfoResponseEntity = restTemplate.postForEntity("http://10.1.110.60:18888/api/ind-system/authentication/admin/applyMch", HttpEntityEvauthinfo, Map.class);
            if(EvauthInfoResponseEntity.getBody().get("code").equals(200)){
                ifauth=true;
            }
        }else{
            return JsonResults.error(300,"服务器用户新增未成功");
        }
        if(ifauth){
            //运管需要去-->207调用商户认证;
            JSONObject  jsonObjectapproval = new JSONObject();
            jsonObjectapproval.put("approvalStatus",1);
            jsonObjectapproval.put("authId",eveAuthInfoVO.getAuthId());
            jsonObjectapproval.put("merchatid",eveAuthInfoVO.getMerchantId());
            HttpEntity entityapproval = new HttpEntity(jsonObjectapproval,headers);
            ResponseEntity<Map> mapResponseEntity = restTemplate.postForEntity("http://10.1.110.60:18888/api/ind-system/authentication/approval", entityapproval, Map.class);
            if(mapResponseEntity.getBody().get("code").equals(200)){
                return JsonResults.success();
            }
        }else{
            return JsonResults.error(300,"服务器商户新增未成功");
        }

        return JsonResults.success();
    }

}
