package com.dfc.ind.common.core.utils;

import com.dfc.ind.common.core.text.UUID;

import java.util.Random;

/**
 * ID生成器工具类
 * 
 * @author admin
 */
public class IdUtils
{
    /**
     * 获取随机UUID
     * 
     * @return 随机UUID
     */
    public static String randomUUID()
    {
        return UUID.randomUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     * 
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID()
    {
        return UUID.randomUUID().toString(true);
    }

    /**
     * 获取随机UUID，使用性能更好的ThreadLocalRandom生成UUID
     * 
     * @return 随机UUID
     */
    public static String fastUUID()
    {
        return UUID.fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线，使用性能更好的ThreadLocalRandom生成UUID
     * 
     * @return 简化的UUID，去掉了横线
     */
    public static String fastSimpleUUID()
    {
        return UUID.fastUUID().toString(true);
    }
    public static String keyUtils() {
        // 定义一个包含数字 0 到 9 的字符串
        String str = "0123456789";

        // 创建一个空的 StringBuilder 对象，用于存储生成的随机数 -- 4位
        StringBuilder st = new StringBuilder(6);

        // 使用 for 循环4次
        for (int i = 0; i < 6; i++) {

            // 在 str 字符串中随机选择一个字符，并添加到 stringBuilder 对象中
            char ch = str.charAt(new Random().nextInt(str.length()));
            st.append(ch);

        }

        // 将 stringBuilder 对象转化为字符串，并转化为小写字母

        // 返回生成的随机数
        return st.toString().toLowerCase();
    }
}
