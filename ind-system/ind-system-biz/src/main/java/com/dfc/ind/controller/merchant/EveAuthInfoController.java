package com.dfc.ind.controller.merchant;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.dfc.ind.common.core.constant.CommonConstants;
import com.dfc.ind.common.core.exception.CustomException;
import com.dfc.ind.common.core.utils.DateUtils;
import com.dfc.ind.common.core.utils.StringUtils;
import com.dfc.ind.common.core.utils.poi.ExcelUtil;
import com.dfc.ind.common.core.web.controller.BaseController;
import com.dfc.ind.common.core.web.domain.JsonResults;
import com.dfc.ind.common.security.utils.SecurityUtils;
import com.dfc.ind.entity.merchant.EveAuthInfoEntity;
import com.dfc.ind.entity.merchant.EveAuthInfoVO;
import com.dfc.ind.entity.sys.SysUser;
import com.dfc.ind.entity.vo.EveApprovalInfoVO;
import com.dfc.ind.service.merchant.IEveAuthInfoService;
import com.dfc.ind.service.sys.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * <p>
 * 功能:本类为中台认证信息表的前端控制器
 * </p>
 *
 * @author ylyan
 * @date 2020-03-28
 */
@Api(tags = "认证信息表接口")
@RestController
@RequestMapping("/authentication")
public class EveAuthInfoController extends BaseController {

    @Autowired
    private IEveAuthInfoService service;

    @Autowired
    private ISysUserService userService;


    /**
     * <用户提交申请商户>
     *
     * @param entity 申请信息对象（实名申请、商户申请）
     * @return
     * @author ylyan
     * @Date 2020/4/26 10:37
     */
    @PostMapping("/applyMch")
    @ApiOperation(value = "app用户提交申请信息,models:EveAuthInfoVO")
    public JsonResults applyMch(@RequestBody EveAuthInfoVO entity) throws CustomException {
        SysUser mallUserEntity = userService.selectUserByUserName(SecurityUtils.getUserName());
        if (null != mallUserEntity) {
            return getJsonResults(entity, mallUserEntity.getUserId());
        }
        return JsonResults.error();
    }

    /**
     * <系统管理员添加提交申请信息>
     *
     * @param entity 申请信息对象（实名申请、商户申请）
     * @return
     * @author ylyan
     * @Date 2020/4/26 10:37
     */
    @PostMapping("/admin/applyMch")
    @ApiOperation(value = "系统管理员添加申请信息,models:EveAuthInfoVO")
    public JsonResults adminApplyMch(@RequestBody EveAuthInfoVO entity) throws CustomException {
        if (null == entity.getUserId() || entity.getUserId() <= 0) {
            return JsonResults.error("请选择申请用户");
        }
        return getJsonResults(entity, entity.getUserId());
    }

    /**
     * <校验、申请通用方法>
     *
     * @param
     * @return
     * @author ylyan
     * @Date 2020/4/27 17:02
     */
    private JsonResults getJsonResults(@RequestBody EveAuthInfoVO entity, Long userId) {
        EveAuthInfoEntity eveAuthInfoEntity = new EveAuthInfoEntity();
        eveAuthInfoEntity.setUserId(userId);
        eveAuthInfoEntity.setAuthType(entity.getAuthType());
        if (CommonConstants.FlgCode.FLG_Y.equals(service.checkAuthTypeUnique(eveAuthInfoEntity))) {
            String authTypeName = CommonConstants.ObjectNum.OBJECT_NUM_1.equals(entity.getAuthType()) ? "实名认证" : "商户认证";
            return JsonResults.error("当前用户已提交过：" + authTypeName);
        }
        return service.applyMch(entity, userId);
    }

    /**
     * <取消申请>
     *
     * @param id 认证id
     * @return
     * @author ylyan
     * @Date 2020/4/26 15:38
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "取消申请")
    @ApiImplicitParam(name = "id", value = "认证id", required = true, dataType = "Long")
    public JsonResults del(@PathVariable Long id) throws CustomException {
        return toResult(service.removeById(id));
    }

    /**
     * <修改申请>
     *
     * @param
     * @return
     * @author ylyan
     * @Date 2020/4/26 15:59
     */
    @PutMapping
    @ApiOperation(value = "修改")
    public JsonResults edit(@RequestBody EveAuthInfoEntity entity) throws CustomException {
        if (CommonConstants.FlgCode.FLG_Y.equals(service.checkAuthTypeUnique(entity))) {
            String authTypeName = CommonConstants.ObjectNum.OBJECT_NUM_1.equals(entity.getAuthType()) ? "实名认证" : "商户认证";
            return JsonResults.error("当前用户已提交过：" + authTypeName);
        }
        entity.setUpdateBy(SecurityUtils.getUserName());
        entity.setUpdateTime(DateUtils.getNowDate());
        entity.setApprovalStatus(CommonConstants.AutoApprovalStatus.AUTO_APPROVAL_STATUS_0);
        service.update(entity, new UpdateWrapper<EveAuthInfoEntity>().lambda()
                .eq(EveAuthInfoEntity::getAuthId, entity.getAuthId())
                .ne(EveAuthInfoEntity::getApprovalStatus, CommonConstants.AutoApprovalStatus.AUTO_APPROVAL_STATUS_1));
        return JsonResults.success();
    }

    /**
     * 中台认证信息表 明细
     *
     * @param id
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "明细")
    @ApiImplicitParam(name = "id", value = "认证id", required = true, dataType = "Long")
    public JsonResults getById(@PathVariable Long id) {
        EveAuthInfoEntity entity = service.getById(id);
        if (StringUtils.isNotNull(entity)) {
            return JsonResults.success(entity);
        }
        return JsonResults.success("认证id不存在!");
    }

    /**
     * 中台认证信息表  查询全部
     *
     * @param entity 对象
     */
    @GetMapping
    @ApiOperation(value = "查询全部")
    @ApiIgnore
    public JsonResults listAll(EveAuthInfoEntity entity) {
        return JsonResults.success(service.list(new QueryWrapper<EveAuthInfoEntity>().setEntity(entity)));
    }

    /**
     * 中台认证信息表 分页查询
     *
     * @param entity        对象
     * @param pageNum       页码
     * @param pageSize      页数
     * @param orderByColumn 排序字段
     * @param isAsc         是否升序 true false
     */
    @GetMapping("/list")
    @ApiOperation(value = "查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页数", required = true),
            @ApiImplicitParam(name = "orderByColumn", value = "排序字段"),
            @ApiImplicitParam(name = "isAsc", value = "是否升序 true false"),
    })
    public JsonResults list(EveAuthInfoEntity entity, int pageNum, int pageSize, String orderByColumn, String isAsc) {

        return JsonResults.success(service.pageLists(startPage(), entity));
    }

    /**
     * 导出中台认证信息表
     *
     * @param entity 对象
     */
    @ApiOperation(value = "导出")
    @PostMapping("/export")
    public void export(HttpServletResponse response, EveAuthInfoEntity entity) throws IOException {

        List<EveAuthInfoEntity> list = service.list(new QueryWrapper<EveAuthInfoEntity>().setEntity(entity));
        ExcelUtil<EveAuthInfoEntity> util = new ExcelUtil<EveAuthInfoEntity>(EveAuthInfoEntity.class);
        util.exportExcel(response, list, "中台认证信息表");
    }

    /**
     * <审批商户申请>
     *
     * @param eveApprovalInfoVO 审批信息封装
     * @return
     * @author ylyan
     * @Date 2020/4/26 15:31
     */
    @PostMapping("/approval")
    @ApiOperation("审批申请,models:EveApprovalInfoVO")
    public JsonResults approval(@RequestBody EveApprovalInfoVO eveApprovalInfoVO) throws CustomException {
        return service.approval(eveApprovalInfoVO);
    }


    /**
     * 发布<审批商户申请>
     *
     * @param eveApprovalInfoVO 审批信息封装发布
     * @return
     * @author ylyan
     * @Date 2020/4/26 15:31
     */
    @PostMapping("/fbapproval")
    @ApiOperation("审批申请,models:EveApprovalInfoVO")
    public JsonResults fbapproval(@RequestBody EveAuthInfoVO eveAuthInfoVO) throws CustomException {
        return service.fbapproval(eveAuthInfoVO);
    }


    /**
     * <根据当前用户查询商户申请信息>
     *
     * @param
     * @return
     * @author ylyan
     * @Date 2020/4/28 15:17
     */
    @GetMapping("/auth/info")
    @ApiOperation(value = "根据当前用户查询商户申请信息")
    public JsonResults getAuthInfo() {
        EveAuthInfoEntity eveAuthInfoEntity = service.getOne(new QueryWrapper<EveAuthInfoEntity>().lambda().eq(EveAuthInfoEntity::getUserId, SecurityUtils.getLoginUser().getUserId()));
        return JsonResults.success(eveAuthInfoEntity);
    }


    /**
     * 查询商户待审核数量
     * @return
     * @throws CustomException
     */
    @GetMapping("/count")
    @ApiOperation("待审批数量")
    public int wsCount() throws CustomException {
        Long merchantId = SecurityUtils.getLoginUser().getMerchantId();
        if (merchantId != 0){
            return 0;
        }
        int count =service.list(new QueryWrapper<EveAuthInfoEntity>().setEntity(new EveAuthInfoEntity().setApprovalStatus("0"))).size();
        return count;
    }

}
