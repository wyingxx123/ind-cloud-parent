package com.dfc.ind.common.core.web.domain;

import com.dfc.ind.common.core.constant.HttpStatus;
import com.dfc.ind.common.core.utils.StringUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


import java.util.HashMap;

/**
 * 统一JSON返回格式
 * @author admin
 */

@ApiModel(description = "接口返回JSON数据实例")
public class JsonResults extends HashMap<String, Object>
{
    private static final long serialVersionUID = 1L;

    /** 状态码 */
    @ApiModelProperty(value = "请求成功状态码")
    public static final String CODE_TAG = "code";

    /** 返回内容 */
    @ApiModelProperty(value = "请求成功描述")
    public static final String MSG_TAG = "msg";

    /** 数据对象 */
    @ApiModelProperty(value = "接口数据对象")
    public static final String DATA_TAG = "data";

    /**
     * 初始化一个新创建的 JsonResult 对象，使其表示一个空消息。
     */
    public JsonResults()
    {
    }

    /**
     * 初始化一个新创建的 JsonResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     */
    public JsonResults(int code, String msg)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }
    /**
     * 初始化一个新创建的 JsonResult 对象
     */
    public  Boolean isSuccess()
    {
        if (get("code")!=null&&(int)get("code")==200){
            return true;
        }else {
            return false;
        }

    }
    /**
     * 初始化一个新创建的 JsonResult 对象
     *
     * @param code 状态码
     * @param msg 返回内容
     * @param data 数据对象
     */
    public JsonResults(int code, String msg, Object data)
    {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (StringUtils.isNotNull(data))
        {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 返回成功消息
     *
     * @return 成功消息
     */
    public static JsonResults success()
    {
        return JsonResults.success("操作成功");
    }

    /**
     * 返回成功数据
     *
     * @return 成功消息
     */
    public static JsonResults success(Object data)
    {
        return JsonResults.success("操作成功", data);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @return 成功消息
     */
    public static JsonResults success(String msg)
    {
        return JsonResults.success(msg, null);
    }

    /**
     * 返回成功消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 成功消息
     */
    public static JsonResults success(String msg, Object data)
    {
        return new JsonResults(HttpStatus.SUCCESS, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @return
     */
    public static JsonResults error()
    {
        return JsonResults.error("操作失败");
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @return 警告消息
     */
    public static JsonResults error(String msg)
    {
        return JsonResults.error(msg, null);
    }

    /**
     * 返回错误消息
     *
     * @param msg 返回内容
     * @param data 数据对象
     * @return 警告消息
     */
    public static JsonResults error(String msg, Object data)
    {
        return new JsonResults(HttpStatus.ERROR, msg, data);
    }

    /**
     * 返回错误消息
     *
     * @param code 状态码
     * @param msg 返回内容
     * @return 警告消息
     */
    public static JsonResults error(int code, String msg)
    {
        return new JsonResults(code, msg, null);
    }

    public static JsonResults noAuth(String msg)
    {
        return new JsonResults(401,msg);
    }
}
