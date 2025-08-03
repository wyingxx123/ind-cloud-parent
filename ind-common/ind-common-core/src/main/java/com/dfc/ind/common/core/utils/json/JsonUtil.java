package com.dfc.ind.common.core.utils.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 描述: Json格式工具栏
 * </p>
 *
 * @author zhaoyq 赵亚强
 * @date 2020/9/23
 * @copyright 武汉数慧享智能科技有限公司
 */
public class JsonUtil {

    /**
     * 判断字符串是否为json格式
     *
     * @param str
     * @returnstr
     */
    public static boolean isJson(String str) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(str);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 判断json是否包含某个key
     *
     * @param jsonStr
     * @param key
     * @return
     */
    public static boolean containsKey(String jsonStr, String key) {
        if(!isJson(jsonStr)) {
            return false;
        }
        JSONObject obj = JSONObject.parseObject(jsonStr);
        return obj.containsKey(key);
    }

    /**
     * 判断json是否包含key
     *
     * @param jsonStr
     * @param keys
     * @return
     */
    public static boolean containsKey(String jsonStr, List<String> keys) {
        if(!isJson(jsonStr)) {
            return false;
        }
        JSONObject obj = JSONObject.parseObject(jsonStr);
        for(String key : keys) {
            if(!obj.containsKey(key)) {
                return false;
            }
        }
        return true;
    }


}
