package com.dfc.ind.common.core.constant;

import jdk.nashorn.internal.ir.debug.ClassHistogramElement;

/**
 * 通用常量信息
 * 
 * @author admin
 */
public class Constants
{
    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 200;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 500;

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销
     */
    public static final String LOGOUT = "Logout";

    /**
     * 注册
     */
    public static final String REGISTER = "Register";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY_COLUMN = "orderByColumn";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * 单引擎
     */
    public final static String SERVICE_TYPE_ENGINE = "SINGLE-ENGINE";

    /**
     * 多引擎
     */
    public final static String SERVICE_TYPE_FLOW = "FLOW-ENGINE";

    /**
     * 是
     */
    public static final String Y = "Y";

    /**
     * 前一天
     */
    public static final String PRE_DAY = "PRE_DAY";

    /**
     * 是
     */
    public static final String NOW_DAY = "NOW_DAY";

    /**
     * 是
     */
    public static final String MERCHANT_ID = "MERCHANT_ID";

    public static final String IMPORT_BY = "IMPORT_BY";
    public static final String APP_ID = "APP_ID";
    /**
     * 是
     */
    public static final String N = "N";
    public static final String GATEWAY_TOKEN_HEADER = "GATEWAY_TOKEN_HEADER";
    public static final String GATEWAY_TOKEN_VALUE = "GATEWAY_TOKEN_VALUE";
}
