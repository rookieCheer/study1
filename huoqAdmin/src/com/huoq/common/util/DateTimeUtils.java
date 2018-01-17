package com.huoq.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 ** @author：zhuhaojie
 * @time：2017年5月7日 上午11:17:49
 */
public class DateTimeUtils {

    /**
     * 分
     */
    private static final long M = 60 * 1000L;
    /**
     * 小时
     */
    private static final long HOUR = 3600 * 1000L;
    /**
     * 天
     */
    private static final long DAY = 24 * HOUR;
    /**
     * yyyy-MM-dd HH:mm
     */
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /**
     * yyyyMMdd
     */
    public static SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

    /**
     * yyyy-MM-dd HH:mm:ss
     */
    public static SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 根据timestamp时间毫秒返回格式化时间
     * 
     * @param timestamp
     *            毫秒
     * 
     * @return 返回格式为 "yyyy-MM-dd HH:mm:ss" 字符串
     */
    public static String getDateStr(long timestamp) {
        String ymdhmsStr = ymdhms.format(timestamp);
        return ymdhmsStr;
    }

    /**
     * 根据系统时间毫秒返回格式化时间
     * 
     * @return String 返回格式为 "yyyy-MM-dd HH:mm:ss"的系统时间字符串
     */
    public static String getSysDateStr() {
        String ymdhmsStr = ymdhms.format(System.currentTimeMillis());
        return ymdhmsStr;
    }

    /**
     * 返回指定的时间格式的系统时间
     * 
     * @param dateFormat
     *            指定的时间格式
     * @return String 返回指定的时间格式的系统时间
     */
    public static String getCurrDate(String dateFormat) {

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        return sdf.format(new Date());

    }

    /**
     * 
     * 得到当前日期<java.util.Date类型>
     * 
     * @param dateFormat
     *            日期格式
     * @return 当前日期<java.util.Date类型>
     * 
     */

    public static Date getCurrentDate(String dateFormat) {

        return strToDate(getCurrDate(dateFormat), dateFormat);

    }

    /**
     * String To Date
     * 
     * @param date
     *            待转换的字符串型日期；
     * @param format
     *            转化的日期格式
     * @return 返回该字符串的日期型数据；
     */
    public static Date strToDate(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将date对像按"yyyy-MM-dd"行格式化
     * 
     * @param date
     *            Date对像
     * @return String 按"yyyy-MM-dd"格式化后的date字符串
     */
    public static String dataFormat(Date date) {
        return dataToString(date, "yyyy-MM-dd");
    }

    /**
     * 将date对像按"yyyy-MM-dd HH:mm:ss"行格式化
     * 
     * @param date
     *            Date对像
     * @return String 按yyyy-MM-dd HH:mm:ss格式化后的date字符串
     */
    public static String dataTimeFormat(Date date) {
        return dataToString(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将date日期对像按dateFormat格式进行格式化
     * 
     * @param date
     *            Date对像
     * @param dateFormat
     *            格式化字符串
     * @return String 格式化后的date字符串
     */
    public static String dataToString(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return sdf.format(date);
    }

    /**
     * 日期时间转换成文字
     * 
     * @param date
     *            时间类型
     * @return 日期
     */
    public static String getDateTimeString(Date date) {
        if (date == null) {
            throw new NullPointerException();
        }

        Date currentDate = new Date();
        long cha = Math.abs(date.getTime() - currentDate.getTime());
        // System.out.println("cha="+cha);
        long hours = cha / HOUR;
        // System.out.println(hours);
        //
        String ret = "";
        if (hours < 1) {
            if (cha / M <= 0) {
                ret = "刚刚";
            }
            ret = cha / M + "分钟前";
        } else if (hours >= 1 && hours < 24) {
            ret = cha / HOUR + "小时前";
        } else if (hours >= 24 && hours <= 72) {
            int nn = Integer.valueOf(cha / DAY + "");
            if (cha % DAY > 0) {
                nn++;
            }
            ret = nn + "天前";
        } else {
            ret = sdf.format(date);
        }
        return ret;
    }

    /**
     * 返回当前日期个年月日字符串形式
     * 
     * @return yyyyMMdd
     */
    public static String getCurDateString() {
        String ymd = yyyyMMdd.format(new Date());
        return ymd;
    }

    /**
     * 
     * @param d1
     *            时间类型
     * @param d2
     *            时间类型
     * @return return
     */
    public static boolean compareDateTime(Date d1, Date d2) {
        return d1.getTime() > d2.getTime();
    }

    /**
     * 获取促销商品活动的结束时间的字符串
     * 
     * @param activityEndDateTime
     *            TC
     * @return TC
     * @throws Exception
     */
    public static String getActivityEndDateTimeString(String activityEndDateTime) {
        // 一天的毫秒数
        long nd = 1000 * 24 * 60 * 60;
        // 一小时的毫秒数
        long nh = 1000 * 60 * 60;
        // 一分钟的毫秒数
        long nm = 1000 * 60;
        // 一秒钟的毫秒数
        long ns = 1000;
        long diff;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        // 获得两个时间的毫秒时间差异
        try {
            diff = sdf.parse(activityEndDateTime).getTime() - (new Date()).getTime();
            // 计算差多少天
            day = diff / nd;
            // 计算差多少小时
            hour = diff % nd / nh + day * 24;
            // 计算差多少分钟
            min = diff % nd % nh / nm + day * 24 * 60;
            // 计算差多少秒
            sec = diff % nd % nh % nm / ns;

            StringBuilder buff = new StringBuilder();

            if (day > 0) {
                buff.append(day).append("天");
            }

            if ((hour - day * 24) > 0) {
                buff.append(hour - day * 24).append("小时");
            }

            if ((min - day * 24 * 60) > 0) {
                buff.append(min - day * 24 * 60).append("分钟");
            }

            if (sec > 0) {
                buff.append(sec).append("秒");
            }

            // String cha = day + "天" + (hour - day * 24) + "小时" + (min - day *
            // 24 * 60) + "分钟" + sec + "秒";

            return buff.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 时间加，只支持HOUR,MINUTE,SECOND,MILLISECOND,
     * 返回date添加value值之后的时间
     * @param date 传入的时间
     * @param value 以冒号分隔的字符串，前面代表添加的值是数字、后面代表添加的单位
     * 为 HOUR,MINUTE,SECOND,MILLISECOND的其中之一
     * @return Date 返回date添加value值之后的时间
     */
    public static Date dateAdd(Date date, String value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String[] values = value.split(":");
        int time = 0;
        String unit = "MILLISECOND";
        if (values.length > 1) {
            time = Integer.parseInt(values[0]);
            unit = values[1];
        }
        if ("HOUR".equals(unit)) {
            calendar.add(Calendar.HOUR, time);
        }
        if ("MINUTE".equals(unit)) {
            calendar.add(Calendar.MINUTE, time);
        }
        if ("SECOND".equals(unit)) {
            calendar.add(Calendar.SECOND, time);
        }
        if ("MILLISECOND".equals(unit)) {
            calendar.add(Calendar.MILLISECOND, -time);
        }
        return calendar.getTime();
    }

    /**
     * Main函数，可做测试
     * 
     * @param args
     *            参数
     */
    public static void main(String[] args) {
        System.out.println("getSysDateStr\t\t" + DateTimeUtils.getSysDateStr());

    }

}
