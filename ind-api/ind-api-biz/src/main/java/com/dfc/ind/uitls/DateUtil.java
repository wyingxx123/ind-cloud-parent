package com.dfc.ind.uitls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 * @author zhouzhenhui
 */
public class DateUtil {

    /**
     * 获取当前日期 pattern为null默认格式：yyyy-MM-dd
     * @param pattern
     * @return
     */
    public static String getCurrentDate(String pattern) {
        if (pattern == null || "".equals(pattern.trim())) {
            pattern = "yyyy-MM-dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date());
    }

    /**
     * 获取当前日期时间 pattern为null默认格式：yyyy-MM-dd HH:mm:ss
     * 格式 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getCurrentDateTime(String pattern) {
        if (pattern == null || "".equals(pattern.trim())) {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        return getCurrentDate(pattern);
    }

    /**
     * 格式化日期
     * @param date
     * @param pattern
     * @return
     */
    public static String formatDateGetString(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }

    /**
     * 格式化日期
     * @param date
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static Date formatDateGetDate(Date date, String pattern) throws ParseException {
        String formatDate = formatDateGetString(date, pattern);
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.parse(formatDate);
    }

    /**
     * 当前日期是否在指定日期范围内，（startDate和endDate都为空时，为true）
     * @param startDate yyyy-MM-dd
     * @param endDate yyyy-MM-dd
     * @return
     * @throws ParseException
     */
    public static boolean currDateIsInDateRange(Date startDate, Date endDate) throws ParseException {
        Date currDate = formatDateGetDate(new Date(), "yyyy-MM-dd");
        if (startDate != null) {
            if (currDate.compareTo(startDate) < 0) {
                return false;
            }
        }
        if (endDate != null) {
            if (currDate.compareTo(endDate) > 0) {
                return false;
            }
        }
        return true;
    }
}
