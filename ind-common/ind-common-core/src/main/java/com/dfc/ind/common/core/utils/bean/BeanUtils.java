package com.dfc.ind.common.core.utils.bean;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Bean 工具类
 * 
 * @author admin
 */
public class BeanUtils extends org.springframework.beans.BeanUtils
{
    /** Bean方法名中属性名开始的下标 */
    private static final int BEAN_METHOD_PROP_INDEX = 3;

    /** * 匹配getter方法的正则表达式 */
    private static final Pattern GET_PATTERN = Pattern.compile("get(\\p{javaUpperCase}\\w*)");

    /** * 匹配setter方法的正则表达式 */
    private static final Pattern SET_PATTERN = Pattern.compile("set(\\p{javaUpperCase}\\w*)");

    /**
     * Bean属性复制工具方法。
     * 
     * @param dest 目标对象
     * @param src 源对象
     */
    public static void copyBeanProp(Object dest, Object src)
    {
        try
        {
            copyProperties(src, dest);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取对象的setter方法。
     * 
     * @param obj 对象
     * @return 对象的setter方法列表
     */
    public static List<Method> getSetterMethods(Object obj)
    {
        // setter方法列表
        List<Method> setterMethods = new ArrayList<Method>();

        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();

        // 查找setter方法

        for (Method method : methods)
        {
            Matcher m = SET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 1))
            {
                setterMethods.add(method);
            }
        }
        // 返回setter方法列表
        return setterMethods;
    }

    /**
     * 获取对象的getter方法。
     * 
     * @param obj 对象
     * @return 对象的getter方法列表
     */

    public static List<Method> getGetterMethods(Object obj)
    {
        // getter方法列表
        List<Method> getterMethods = new ArrayList<Method>();
        // 获取所有方法
        Method[] methods = obj.getClass().getMethods();
        // 查找getter方法
        for (Method method : methods)
        {
            Matcher m = GET_PATTERN.matcher(method.getName());
            if (m.matches() && (method.getParameterTypes().length == 0))
            {
                getterMethods.add(method);
            }
        }
        // 返回getter方法列表
        return getterMethods;
    }

    /**
     * 检查Bean方法名中的属性名是否相等。<br>
     * 如getName()和setName()属性名一样，getName()和setAge()属性名不一样。
     * 
     * @param m1 方法名1
     * @param m2 方法名2
     * @return 属性名一样返回true，否则返回false
     */

    public static boolean isMethodPropEquals(String m1, String m2)
    {
        return m1.substring(BEAN_METHOD_PROP_INDEX).equals(m2.substring(BEAN_METHOD_PROP_INDEX));
    }


    /**
     * 从旧的搬过来的
     */
    /**
     * DTO对象和PO对象转换方法
     *
     * @param object          要转换的对象
     * @param targetObject 目标对象
     * @return
     */
    public static void objectTransform(Object object, Object targetObject) {
        try {
            org.springframework.beans.BeanUtils.copyProperties(object, targetObject);
        } catch (Exception e) {
            targetObject = targetObject;
            //e.printStackTrace();
        }
    }

    /**
     * 新老对象赋值
     *
     * @param object
     * @param targetObject
     * @param object1
     */
    public static void objectTransform(Object object, Object targetObject, Object object1) {
        try {
            org.springframework.beans.BeanUtils.copyProperties(object, targetObject, getNullPropertyNames(object1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * DTO对象和PO的List对象转换方法
     *
     * @param list       要转换的对象
     * @param targetList 目标对象
     * @param cls        目标的class
     * @return
     */
    public static void objectListTransform(List list, List targetList, Class cls) {
        try {
            for (Object obj : list) {
                Object targetObject = cls.newInstance();
                objectTransform(obj, targetObject);
                targetList.add(targetObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * html转义
     *
     * @param html
     * @return
     */
    public static String htmlEncode(String html) {
        if (html == null) {
            return "";
        }
        html = html.replaceAll("&", "&amp;");
        html = html.replace("\"", "&quot;");
        // 替换跳格
        html = html.replace("\t", "&nbsp;&nbsp;");
        // 替换空格
        html = html.replace(" ", "&nbsp;");
        html = html.replace("<", "&lt;");

        html = html.replaceAll(">", "&gt;");

        return html;
    }

    /**
     * IPage对象中Records转vo对象
     * @param vo
     * @param voPage
     * @return
     */
    public static IPage<?> pageRecordsTransform(Class<?> vo, IPage<?> voPage) {
        List voList = new ArrayList<>();
        objectListTransform(voPage.getRecords(), voList, vo);
        voPage.getRecords().clear();
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 描述：对象的拷贝，可以排除指定的属性
     *
     * @param source 源对象
     * @param target 目标对象
     * @param ignoreProperties 要排除拷贝的属性集
     * @author wdj
     * @date 2020-07-30 14:22:08
     */
    public static void copyProperties(Object source, Object target,String... ignoreProperties)
            throws BeansException {

        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = org.springframework.beans.BeanUtils.getPropertyDescriptors(actualEditable);
        List<String> ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;

        for (PropertyDescriptor targetPd : targetPds) {
            Method writeMethod = targetPd.getWriteMethod();
            if (writeMethod != null && (ignoreProperties == null || (!ignoreList.contains(targetPd.getName())))) {
                PropertyDescriptor sourcePd = org.springframework.beans.BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
                if (sourcePd != null) {
                    Method readMethod = sourcePd.getReadMethod();
                    if (readMethod != null &&
                            ClassUtils.isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType())) {
                        try {
                            if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                                readMethod.setAccessible(true);
                            }
                            Object value = readMethod.invoke(source);
                            if(value != null){  //只拷贝不为null的属性
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(target, value);
                            }
                        }
                        catch (Throwable ex) {
                            throw new FatalBeanException(
                                    "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                        }
                    }
                }
            }
        }
    }

    public static <T> List<T> castList(Object obj, Class<T> clazz)
    {
        List<T> result = new ArrayList<T>();
        if(obj instanceof List<?>)
        {
            for (Object o : (List<?>) obj)
            {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * json 转 List<T>
     */
    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        @SuppressWarnings("unchecked")
        List<T> ts = (List<T>) JSONArray.parseArray(jsonString, clazz);
        return ts;
    }

}
