package com.dfc.ind.common.core.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 描述:属性sku处理工具类
 * </p>
 *
 * @author wudj 伍达将
 * @date 2020/11/18
 * @copyright 武汉数慧享智能科技有限公司
 */
public class PropertySKUUtils {

    /**
     * 将属性sku字符串解析成map
     * @param propertysSku
     * @return
     */
    public static final Map<String, String> skuToMap(final String propertysSku) {
        Map<String, String> map = new HashMap<String, String>();
        if (StringUtils.isNotEmpty(propertysSku)) {
            int indexNum = 0;
            int startIndex = 0;
            while ((startIndex = propertysSku.indexOf("[", indexNum)) > 0) {
                int endIndex = propertysSku.indexOf("]", indexNum) + 1;
                if (endIndex > startIndex) {
                    String propertysStr = propertysSku.substring(startIndex+1, endIndex-1);
                    List<String> propertysList = Arrays.asList(propertysStr.split(","));
                    String propertyName = propertysList.get(0).replaceAll("\"","").trim();
                    String valuesName = propertysList.get(1).replaceAll("\"","").trim();
                    map.put(propertyName, valuesName);
                    indexNum = endIndex;
                } else {
                    indexNum = propertysSku.length();
                }
            }
        }

        return map;
    }

    /**
     * 判断两个属性sku字符串是否相同，去除属性次序影响，仅判断属性及属性值是否相同
     * @param oldPropertysSku
     * @param newPropertysSku
     * @return
     */
    public static final boolean judgeSKUIsSame(final String oldPropertysSku, final String newPropertysSku) {
        Map<String, String> oldSkuMap = skuToMap(oldPropertysSku);
        Map<String, String> newSkuMap = skuToMap(newPropertysSku);
        if (oldSkuMap.size() == 0 || newSkuMap.size() == 0) {
            if (oldSkuMap.size() == 0 && newSkuMap.size() == 0) {
                return true;
            } else {
                return false;
            }
        }
        for (Map.Entry<String, String> entry : oldSkuMap.entrySet()) {
            if (newSkuMap.containsKey(entry.getKey())) {
                if (!entry.getValue().equals(newSkuMap.get(entry.getKey()))) {
                    return false;
                }
            } else {
                return false;
            }
        }
        for (Map.Entry<String, String> entry : newSkuMap.entrySet()) {
            if (oldSkuMap.containsKey(entry.getKey())) {
                if (!entry.getValue().equals(oldSkuMap.get(entry.getKey()))) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断新的属性sku字符串是否包含在旧的属性sku字符串中，去除属性次序影响，可以选择判断属性及属性值是否相同或者只判断属性相同
     * @param oldPropertysSku
     * @param newPropertysSku
     * @param matchValue
     * @return
     */
    public static final boolean judgeSKUNewBelongOld(final String oldPropertysSku, final String newPropertysSku, Boolean matchValue) {
        Map<String, String> oldSkuMap = skuToMap(oldPropertysSku);
        Map<String, String> newSkuMap = skuToMap(newPropertysSku);
        if (oldSkuMap.size() == 0 || newSkuMap.size() == 0) {
            if (oldSkuMap.size() == 0 && newSkuMap.size() == 0) {
                return true;
            } else if (newSkuMap.size() == 0) {
                return true;
            } else {
                return false;
            }
        }
        for (Map.Entry<String, String> entry : newSkuMap.entrySet()) {
            if (oldSkuMap.containsKey(entry.getKey())) {
                if (matchValue) {
                    if (!entry.getValue().equals(oldSkuMap.get(entry.getKey()))) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        return true;
    }
}
