package com.dfc.ind.uitls;

/**
 * redis key常量
 * @author zhouzhenhui
 */
public class RedisKeyConstant {

    /**
     * 用户登录失败次数key
     */
    public static final String USER_LOGIN_FAILED_COUNT_KEY = "user:login:failed:count:";
    /**
     * api授权登录失败次数key
     */
    public static final String API_AUTHORIZE_FAILED_COUNT_KEY = "api:authorize:failed:count:";

    /**
     * 是否开启ip白名单模式 key
     */
    public static final String OPEN_IP_WHITE_KEY = "dpub:open_ip_white";
    /**
     * ip白名单 key
     */
    public static final String IP_WHITE_LIST_KEY = "dpub:ip_white_list";

}
