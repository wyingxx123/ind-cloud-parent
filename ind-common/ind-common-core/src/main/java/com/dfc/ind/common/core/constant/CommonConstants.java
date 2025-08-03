package com.dfc.ind.common.core.constant;

/**
 * 系统通用  常量值
 *
 * @author admin
 */
public interface CommonConstants {

    /**
     * 成功标记
     */
    Integer SUCCESS = 200;

    /**
     * 错误标记
     */
    Integer ERROR = 500;

    /**
     * 使用
     */
    String USE = "00";

    /**
     * 禁用
     */
    String FORBIDDEN = "01";

    /**
     * 发布订单状态:  00-订单完成，01-未处理（待审批），02-已发布（审批通过），03-订单撮合，04-意向合同审批，05-计划生产,10-失效
     * 11-未开始    12-处理中    13-备料中   14-生产中  15-待发货  16-已发货  17-确认收货 18-已收料
     * 意向合同状态:  00-合同生效，01-待下发，02-待审批（已下发状态），03-延期，10-失效
     * 意向承接状态:  00-已承接，01-未承接
     * 规则类型:      01-合同下发，02-合同磋商
     * 下发模式类型：  01-签约后下发，02-签约前下发
     * 用户商户申请状态：00-未申请，01-已申请（审批中），02-审批通过，03-不通过
     */
    public static class ObjectStatus {
        public static final String OBJECT_STATUS_0 = "00";
        public static final String OBJECT_STATUS_1 = "01";
        public static final String OBJECT_STATUS_2 = "02";
        public static final String OBJECT_STATUS_3 = "03";
        public static final String OBJECT_STATUS_4 = "04";
        public static final String OBJECT_STATUS_5 = "05";
        public static final String OBJECT_STATUS_10 = "10";
        public static final String OBJECT_STATUS_11 = "11";
        public static final String OBJECT_STATUS_12 = "12";
        public static final String OBJECT_STATUS_13 = "13";
        public static final String OBJECT_STATUS_14 = "14";
        public static final String OBJECT_STATUS_15 = "15";
        public static final String OBJECT_STATUS_16 = "16";
        public static final String OBJECT_STATUS_17 = "17";
        public static final String OBJECT_STATUS_18 = "18";
    }

    /**
     * 发布订单审批状态
     */
    public static class ApprovalStatus {
        // 0-未审批，
        public static final String APPROVAL_STATUS_0 = "00";
        // 1-审批通过，
        public static final String APPROVAL_STATUS_1 = "01";
        // 2-不通过，
        public static final String APPROVAL_STATUS_2 = "02";
    }

    /**
     * 商户认证审批状态
     */
    public static class AutoApprovalStatus {
        // 0-未审批，
        public static final String AUTO_APPROVAL_STATUS_0 = "0";
        // 1-审批通过，
        public static final String AUTO_APPROVAL_STATUS_1 = "1";
        // 2-不通过，
        public static final String AUTO_APPROVAL_STATUS_2 = "2";
    }

    /**
     * 发布订单审批类型
     */
    public static class ApprovalType {
        // 01-认证审批，
        public static final String APPROVAL_TYPE_1 = "01";
        // 02-授权审批，
        public static final String APPROVAL_TYPE_2 = "02";
        // 03-发布订单审批
        public static final String APPROVAL_TYPE_3 = "03";
        // 04-意向合同审批
        public static final String APPROVAL_TYPE_4 = "04";
    }

    /**
     * 数据来源代码
     */
    public static class DataSrcCd {
        // 产业链数据，
        public static final String DATA_SRC_CD_I_BSS = "IBSS";
        // 厂商数据，
        public static final String DATA_SRC_CD_F_BSS = "FBSS";
        // 中台数据，
        public static final String DATA_SRC_CD_C_BSS = "CBSS";
    }

    /**
     * 授权事项代码
     */
    public static class EmpowerItemCd {
        // 订单模式授权代码，包含订单计划工单模式00和订单计划流转单模式01
        public static final String EMPOWER_ORDER_MODEL_CD = "ORDERMODEL";
    }

    /**
     * Y-1,N-0
     * 删除标志、修改标志、启停标志、校验返回结果码（UNIQUE = "0" NOT_UNIQUE = "1"）
     * 规则使用状态  1-启用，0-停用
     */
    public static class FlgCode {
        // Y
        public static final String FLG_Y = "1";
        // N
        public static final String FLG_N = "0";
    }


    /**
     * 意向合同审批对象：1-承接方，2-发布方
     * 订单合作方式：1-被动，2-主动
     * 认证类型：1-实名认证，2-商户认证
     * 申请类型：1-个人，2-企业
     */
    public static class ObjectNum {
        // 1-承接方，
        public static final String OBJECT_NUM_1 = "1";
        // 2-发布方，
        public static final String OBJECT_NUM_2 = "2";
    }


    /**
     * 角色类型等级 消息范围等级
     * 0 所有人
     */
    public static class RoleType {
        // 0-所有人
        public static final String TYPE_0 = "0";
        // 00-超级管理员
        public static final String TYPE_00 = "00";
        // 01-采购管理员
        public static final String TYPE_01 = "01";
        // 02-销售管理员
        public static final String TYPE_02 = "02";
        // 03-生产管理员
        public static final String TYPE_03 = "03";
        // 04-审批管理员
        public static final String TYPE_04 = "04";
        // 05-库房管理员
        public static final String TYPE_05 = "05";
        // 06-运营管理员
        public static final String TYPE_06 = "06";
        // 07-商户管理员
        public static final String TYPE_07 = "07";
        // 11-采购经办
        public static final String TYPE_11 = "11";
        // 12-采购经理
        public static final String TYPE_12 = "12";
        // 13-生产经办
        public static final String TYPE_13 = "13";
        // 14-生产经理
        public static final String TYPE_14 = "14";
        // 15-销售经办
        public static final String TYPE_15 = "15";
        // 16-销售经理
        public static final String TYPE_16 = "16";
        // 17-库房经办
        public static final String TYPE_17 = "17";
        // 18-库房经理
        public static final String TYPE_18 = "18";
    }

    /**
     * 消息模板生成范围
     */
    public static class CreateScope {
        // 00-所有人
        public static final String SCOPE_00 = "00";
        // 01-商户及管理员
        public static final String SCOPE_01 = "01";
        // 02-商户
        public static final String SCOPE_02 = "02";
        // 03-个人
        public static final String SCOPE_03 = "03";
        //04-指定人
        public static final String SCOPE_04 = "04";
    }

    /**
     * 预警类型 01=订单预警，02=计划预警，03=工单预警，04=库房预警
     */
    public static class WarnType {
        public static final String Type_01 = "01";
        public static final String Type_02 = "02";
        public static final String Type_03 = "03";
        public static final String Type_04 = "04";
    }

    /**
     * 定时任务类型 01=订单预警，02=计划预警，03=工单预警，04=库房预警
     */
    public static class ScheduledType {
        public static final String TYPE_01 = "01";
        public static final String TYPE_02 = "02";
        public static final String TYPE_03 = "03";
        public static final String TYPE_04 = "04";
    }

    /**
     * 业务子类型 01=采购，02=销售，03=生产
     */
    public static class BusinessSubType {
        public static final String TYPE_01 = "01";
        public static final String TYPE_02 = "02";
        public static final String TYPE_03 = "03";
    }

    /**
     * 发布订单类型 01=协同生产,02=集中采购,03=销售订单,04=采购订单
     */
    public static class OrderType {
        public static final String Type_01 = "01";
        public static final String Type_02 = "02";
        public static final String Type_03 = "03";
        public static final String Type_04 = "04";
        public static final String Type_05 = "05";
        public static final String Type_06 = "06";
        public static final String Type_07 = "07";
        public static final String Type_08 = "08";
    }

    public static class NewOrderType{
        //生产
        public static final String TYPE_PRODUCE = "01";
        //委外
        public static final String TYPE_OUTSOURCING = "02";
        //销售
        public static final String TYPE_SALE = "03";
        //采购
        public static final String TYPE_PURCHASE = "04";
    }

    /**
     * 流程类型 13-发料 14-回货 15-批办 16-补料 17-退料 18-对账
     */
    public static class BiemlfProductionType {
        public static final String Type_13 = "13";
        public static final String Type_14 = "14";
        public static final String Type_15 = "15";
        public static final String Type_16 = "16";
        public static final String Type_17 = "17";
        public static final String Type_18 = "18";
        public static final String Type_20 = "20";
    }

    /**
     * 流程类型 39-客供发料 40-物料签收 41-申请回货 42-QC尾查 43-出销售单 44-发货 45-签收货物 48-补料申请 49-申请退料
     */
    public static class BiemlfAnnexType {
        public static final String Type_39 = "39";
        public static final String Type_40 = "40";
        public static final String Type_41 = "41";
        public static final String Type_42 = "42";
        public static final String Type_43 = "43";
        public static final String Type_44 = "44";
        public static final String Type_45 = "45";
        public static final String Type_46 = "46";
        public static final String Type_47 = "47";
        public static final String Type_48 = "48";
        public static final String Type_49 = "49";
        public static final String Type_50 = "50";
    }

    /**
     * 流程类型 01-接单 02-入库 05-生产质检 07-三签 08-领料 10-生产 98 首检 99巡检 03 出库
     */
    public static class AnnexType {
        public static final String Type_01 = "01";
        public static final String Type_02 = "02";
        public static final String Type_05 = "05";
        public static final String Type_07 = "07";
        public static final String Type_08 = "08";
        public static final String Type_10 = "10";
        public static final String Type_98 = "98";
        public static final String Type_99 = "99";
        public static final String Type_03 = "03";
    }

    /**
     * 出入库类型 01入库 02出库
     */
    public static class ProcessingType {
        public static final String Type_01 = "01";
        public static final String Type_02 = "02";
    }

    /**
     * 出入库来源类型 00库管调库 01采购入库 02销售出库 03生产出入库 04批量出入库 05委外出入库 06掉队出入库 07报工退回 08红字入库
     */
    public static class MaterialType {
        public static final String Type_00 = "00";
        public static final String Type_01 = "01";
        public static final String Type_02 = "02";
        public static final String Type_03 = "03";
        public static final String Type_04 = "04";
        public static final String Type_05 = "05";
        public static final String Type_06 = "06";
        public static final String Type_07 = "07";
        public static final String Type_08 = "08";
    }

    /**
     * 工单类型 01-采购入库工单 02-销售出库工单 04-生产工单
     */
    public static class WorkType {
        public static final String Type_01 = "01";
        public static final String Type_02 = "02";
        public static final String Type_04 = "04";
    }


    public static class TempType{
        public static final String TYPE_FORMAL = "01";
        public static final String TYPE_TEMP = "02";
    }
}
