package com.dfc.ind.common.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 自主生成
 *
 * @author cjp
 */
public class AutoGenerateNo {


    public final static Map<String, String> map = new HashMap();

    static {
        map.put("AG", "SELECT NEXTVAL('AG')");
        map.put("CG", "SELECT NEXTVAL('CG')");
        map.put("PL", "SELECT NEXTVAL('PL')");
        map.put("SC", "SELECT NEXTVAL('SC')");
        map.put("OD", "SELECT NEXTVAL('OD')");
        map.put("GD", "SELECT NEXTVAL('GD')");
        map.put("FT", "SELECT NEXTVAL('FT')");
        map.put("ST", "SELECT NEXTVAL('ST')");
        map.put("QT", "SELECT NEXTVAL('QT')");
        map.put("HJ", "SELECT NEXTVAL('HJ')");
        map.put("CJ", "SELECT NEXTVAL('CJ')");
        map.put("LT", "SELECT NEXTVAL('LT')");
        map.put("PD", "SELECT NEXTVAL('PD')");
        map.put("GW", "SELECT NEXTVAL('GW')");
        map.put("EQ", "SELECT NEXTVAL('EQ')");
        map.put("GY", "SELECT NEXTVAL('GY')");
        map.put("LC", "SELECT NEXTVAL('LC')");
        map.put("SL", "SELECT NEXTVAL('SL')");
        map.put("CK", "SELECT NEXTVAL('CK')");
        map.put("MX", "SELECT NEXTVAL('MX')");
        map.put("RHA", "SELECT NEXTVAL('RHA')");
        map.put("BM", "SELECT NEXTVAL('BM')");
        map.put("PC", "SELECT NEXTVAL('PC')");
        map.put("SP", "SELECT NEXTVAL('SP')");
        map.put("KG", "SELECT NEXTVAL('KG')");
        map.put("LS", "SELECT NEXTVAL('LS')");
        map.put("AP", "SELECT NEXTVAL('AP')");
        map.put("SPD", "SELECT NEXTVAL('SPD')");
        map.put("CP", "SELECT NEXTVAL('CP')");
        map.put("P", "SELECT NEXTVAL('P')");
        map.put("Y", "SELECT NEXTVAL('Y')");
        map.put("T", "SELECT NEXTVAL('T')");
        map.put("G", "SELECT NEXTVAL('G')");
        map.put("W", "SELECT NEXTVAL('W')");
        map.put("N", "SELECT NEXTVAL('N')");
        map.put("M", "SELECT NEXTVAL('M')");
        map.put("F", "SELECT NEXTVAL('F')");
        map.put("B", "SELECT NEXTVAL('B')");
        map.put("Q", "SELECT NEXTVAL('Q')");
        //流水号
        map.put("SQ", "SELECT NEXTVAL('SQ')");
        //预览号
        map.put("YL", "SELECT NEXTVAL('YL')");
        //计划影响分析号
        map.put("YX", "SELECT NEXTVAL('YX')");
        //工单影响分析号
        map.put("GDYX", "SELECT NEXTVAL('GDYX')");
        //产业链订单表主键
        map.put("RNI", "SELECT NEXTVAL('RNI')");
        //产业链发布产品明细表主键
        map.put("PDUDETI", "SELECT NEXTVAL('PDUDETI')");
        //产业链发布工艺要求信息表主键
        map.put("ORPI", "SELECT NEXTVAL('ORPI')");

        //产业链工艺要求表主键
        map.put("PCEI", "SELECT NEXTVAL('PCEI')");
        //产业链工艺属性表主键
        map.put("PCTI", "SELECT NEXTVAL('PCTI')");
        //产业链产品基础表主键
        map.put("PDUI", "SELECT NEXTVAL('PDUI')");
        //产业链产品BOM表主键
        map.put("PDUBOMI", "SELECT NEXTVAL('PDUBOMI')");

        //审批信息表主键
        map.put("APPR", "SELECT NEXTVAL('APPR')");
        //意向合同信息编号
        map.put("AGZ", "SELECT NEXTVAL('AGZ')");

        //资讯栏目信息表主键
        map.put("LM", "SELECT NEXTVAL('LM')");
        //资讯主题信息表主键
        map.put("ZT", "SELECT NEXTVAL('ZT')");
        //商品信息表主键
        map.put("SH", "SELECT NEXTVAL('SH')");
        //主题内容信息表主键
        map.put("NR", "SELECT NEXTVAL('NR')");
        map.put("MA", "SELECT NEXTVAL('MA')");

        map.put("RULE", "SELECT NEXTVAL('RULE')");
        map.put("SP_RULE", "SELECT NEXTVAL('SP_RULE')");
        map.put("DP", "SELECT NEXTVAL('DP')");
        map.put("GYS", "SELECT NEXTVAL('GYS')");
        map.put("KH", "SELECT NEXTVAL('KH')");
        map.put("WWGC", "SELECT NEXTVAL('WWGC')");
        map.put("WLGS", "SELECT NEXTVAL('WLGS')");
        map.put("PZ", "SELECT NEXTVAL('PZ')");
        map.put("CPGX", "SELECT NEXTVAL('CPGX')");
        map.put("SX", "SELECT NEXTVAL('SX')");
        map.put("SXZ", "SELECT NEXTVAL('SXZ')");
        map.put("PSX", "SELECT NEXTVAL('PSX')");
        map.put("CPKC", "SELECT NEXTVAL('CPKC')");
        map.put("EWM", "SELECT NEXTVAL('EWM')");
        map.put("QR", "SELECT NEXTVAL('QR')");
        map.put("LCR", "SELECT NEXTVAL('LCR')");
        //服装品牌商检测申请单
        map.put("BiYinLeFen", "SELECT NEXTVAL('BiYinLeFen')");
        //装箱编号
        map.put("PBN", "SELECT NEXTVAL('PBN')");
        //服装品牌商批办号
        map.put("BRD", "SELECT NEXTVAL('BRD')");
        //商户审批主键
        map.put("MERCHANT", "SELECT NEXTVAL('MERCHANT')");
    }

    /**
     * AG-合同编号  * CG-采购编号* PL-计划编号* SC-生产编号* OD-订单编号*
     * GD-工单编号* FT-工厂编号*  ST-库房编号* QT-区间编号* HJ-货架编号* CJ-层级编号
     * LT-货位编号*PD-产品编号*SPD-子产品编号*GW-工位编号*EQ-设备编号* GY-工艺编号* LC-流程 *SL-子流程
     * CK-出入库流水*MX-生产明细流水*EM-员工编号*BM-部门编号*PC-采购商编号*SP-供应商编号*KG-开工编号*AP-审批编号*CP-点检
     * P-计划生产*Y-新产品*T-退货*G-外购产品*W-外圈*N-内圈*M-密封圈*F-防尘盖*B-保持器*Q-钢球
     * GYS-供应商*KH-客户 PZ-设备采集配置 CPGX-产品工序 SX-属性 SXZ-属性值 PSX-产品属性 CPKC-产品库存 EWM-二维码配置
     * QR-二维码信息 LCR-流程记录编号 PBN-装箱编号 BRD-服装品牌商批办号
     */
    public static final String AG_TITLE = "AG";
    public static final String CG_TITLE = "CG";
    public static final String PL_TITLE = "PL";
    public static final String SC_TITLE = "SC";
    public static final String OD_TITLE = "OD";
    public static final String GD_TITLE = "GD";
    public static final String FT_TITLE = "FT";
    public static final String ST_TITLE = "ST";
    public static final String QT_TITLE = "QT";
    public static final String HJ_TITLE = "HJ";
    public static final String CJ_TITLE = "CJ";
    public static final String LT_TITLE = "LT";
    public static final String PD_TITLE = "PD";
    public static final String GW_TITLE = "GW";
    public static final String EQ_TITLE = "EQ";
    public static final String GY_TITLE = "GY";
    public static final String LC_TITLE = "LC";
    public static final String SL_TITLE = "SL";
    public static final String CK_TITLE = "CK";
    public static final String MX_TITLE = "MX";
    public static final String RHA_TITLE = "RHA";
    public static final String BM_TITLE = "BM";
    public static final String PC_TITLE = "PC";
    public static final String SP_TITLE = "SP";
    public static final String KG_TITLE = "KG";
    public static final String LS_TITLE = "LS";
    public static final String AP_TITLE = "AP";
    public static final String SPD_TITLE = "SPD";
    public static final String CP_TITLE = "CP";
    public static final String P_TITLE = "P";
    public static final String Y_TITLE = "Y";
    public static final String T_TITLE = "T";
    public static final String G_TITLE = "G";
    public static final String W_TITLE = "W";
    public static final String N_TITLE = "N";
    public static final String M_TITLE = "M";
    public static final String F_TITLE = "F";
    public static final String B_TITLE = "B";
    public static final String Q_TITLE = "Q";
    public static final String SQ_TITLE = "SQ";
    public static final String YL_TITLE = "YL";
    public static final String YX_TITLE = "YX";
    public static final String GDYX_TITLE = "GDYX";
    public static final String RN_I_TITLE = "RNI";
    public static final String PDU_DET_I_TITLE = "PDUDETI";
    public static final String ORP_I_TITLE = "ORPI";

    public static final String PCE_I_TITLE = "PCEI";
    public static final String PCT_I_TITLE = "PCTI";
    public static final String PDU_I_TITLE = "PDUI";
    public static final String PDU_BOM_I_TITLE = "PDUBOMI";

    public static final String APPR_TITLE = "APPR";
    public static final String AGZ_TITLE = "AGZ";

    public static final String LM_TITLE = "LM";
    public static final String ZT_TITLE = "ZT";
    public static final String SH_TITLE = "SH";
    public static final String NR_TITLE = "NR";
    public static final String MERCHANT_AUTH_TITLE = "MA";

    public static final String RULE_TITLE = "RULE";
    public static final String SPECIAL_RULE_TITLE = "SP_RULE";
    public static final String DP_TITLE = "DP";
    public static final String GYS_TITLE = "GYS";
    public static final String KH_TITLE = "KH";
    public static final String WWGC_TITLE = "WWGC";
    public static final String WLGS_TITLE = "WLGS";
    public static final String PZ_TITLE = "PZ";
    public static final String CPGX_TITLE = "CPGX";
    public static final String SX_TITLE = "SX";
    public static final String SXZ_TITLE = "SXZ";
    public static final String PSX_TITLE = "PSX";
    public static final String CPKC_TITLE = "CPKC";
    public static final String EWM_TITLE = "EWM";
    public static final String QR_TITLE = "QR";
    public static final String LCR_TITLE = "LCR";
    public static final String BIYINLEFEN_TITLE = "BiYinLeFen";
    public static final String PBN_TITLE = "PBN";
    public static final String BRD_TITLE = "BRD";
    public static final String MERCHANT_TITLE = "MERCHANT";

    public static Date getNewDate(Date date) {

        String dateStr = new SimpleDateFormat("yyyy-MM-dd").format(date);
        Date strDate = null;
        try {
            strDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;
    }

    public static String getStrDate(Date date) {

        String dateStr = new SimpleDateFormat("yyyyMMdd").format(date);

        return dateStr;
    }

    public static String getStr() {

        String str = "500000000";

        return str;
    }

}
