package com.huoq.common.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 操作时间工具类
 * 
 * @author zhuhaojie 2016年8月19日下午3:37:37
 */
public class DateUtils {

    /**
     * 封闭无参构造方法
     * 
     * @author zhuhaojie
     * @time 2017年1月3日 上午11:55:19
     */
    private DateUtils() {

    }

    /**
     * YYYY-MM-DD 格式的正则表达式
     */
    public static String TimePattern = "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}"
            + "|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))"
            + "|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})"
            + "(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29)"
            + "(([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})"
            + "-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|"
            + "(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|"
            + "((0[48]|[2468][048]|[3579][26])00))-02-29)";

    /**
     * DD/MM/YYYY格式的正则验证表达式
     */
    public static String TimePatternSecond = "(((0[1-9]|[12][0-9]|3[01])/((0[13578]|1[02]))|"
            + "((0[1-9]|[12][0-9]|30)/(0[469]|11))|(0[1-9]|[1][0-9]|2[0-8])/(02))/([0-9]{3}[1-9]|"
            + "[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}))|(29/02/(([0-9]{2})"
            + "(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00)))";
    /**
     * HH:mm:ss正则表达式
     */
    public static String TimePatternThird = "^([0-1]?[0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";

    /**
     * 以java.sql.Date对象形式返回当前时间
     * 
     * @author zhuhaojie
     * @time 2016年8月19日下午3:38:15
     * @param
     * @return java.sql.Date
     */
    public static java.sql.Date getSqlDate() {
        java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());
        return sqlDate;
    }

    /**
     * @author zhuhaojie
     * @time 2016年11月23日下午3:25:24
     * @param time
     *            待检验的日期字符串
     * @param pattern
     *            日期正则表达式字符串
     * @return boolean 如果格式匹配返回true 否则返回false
     *
     */
    public static boolean isTrue(String time, String pattern) {
        if (time == null) {
            return false;
        }
        boolean t = time.matches(pattern);
        return t;
    }

    /**
     * 返回当前时间
     * 
     * @author zhuhaojie
     * @time 2016年8月19日下午3:39:29
     * @return Date 格式：yyyy-MM-dd HH:mm:ss
     */
    public static Date getNowDate() {

        Date currentTime = new Date();
        SimpleDateFormat formatter = getDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    /**
     * 获取当前时间的4位年
     * 
     * @author zhuhaojie
     * @time 2016年9月23日下午2:44:11
     * @return 返回当前时间前四位的年
     */
    public static String getYear() {
        String year = getNowDateNewShort();
        if (year == null) {
            return null;
        }
        year = year.trim();
        if ("".equals(year)) {
            return "";
        }
        int length = year.length();
        if (length != 10) {
            return "";
        }
        year = year.substring(0, 4);
        return year;
    }

    /**
     * 获取线程安全的SimpleDateFormat对象
     * 
     * @author zhuhaojie
     * @time 2017年1月3日 上午11:57:53
     * @param pattern
     *            指定的日期格式
     * @return SimpleDateFormat 根据指定格式创建的SimpleDateFormat对象
     */
    public static SimpleDateFormat getDateFormat(final String pattern) {
        ThreadLocal<SimpleDateFormat> df = new ThreadLocal<SimpleDateFormat>() {
            protected SimpleDateFormat initialValue() {
                return new SimpleDateFormat(pattern);
            }
        };
        SimpleDateFormat sdf = df.get();
        return sdf;
    }

    /**
     * 获取两个时间间隔几个月 同一个月返回1
     * 
     * @param begin
     *            开始时间
     * @param end
     *            结束时间
     * @return int 间隔的月数
     */
    public static int getMonthSpace(Date begin, Date end) {

        int result = 0;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(begin);
        c2.setTime(end);
        result = c2.get(Calendar.MONDAY) - c1.get(Calendar.MONTH);
        return result == 0 ? 1 : Math.abs(result);
    }

    /**
     * 按照格式pattern，返回当前时间的Date对象
     * 
     * @author zhuhaojie
     * @param pattern
     *            格式化好的Date格式 如："yyyy-MM-dd"
     * @time 2016年8月19日下午3:40:31
     * @return Date
     */
    public static Date getNowDateShort(String pattern) {
        Date currentTime = new Date();
        // SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        SimpleDateFormat formatter = getDateFormat(pattern);
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(0);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    /**
     * 返回当前时间
     * 
     * @author zhuhaojie
     * @time 2016年8月19日下午3:41:12
     * @return String 格式：yyyy-MM-dd
     */
    public static String getNowDateNewShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = getDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 返回时间戳对象
     * 
     * @author zhuhaojie
     * @time 2016年8月19日下午3:42:57
     * @param date
     *            日期对象
     * @return Timestamp 格式为Date对象的格式
     */
    public static Timestamp getNowTimeStampShort(Date date) {
        if (date != null) {
            Timestamp ts = new Timestamp(date.getTime());
            // DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            DateFormat sdf = getDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                if (null != ts) {
                    String s = sdf.format(ts);
                    ts = stringToTimestamp(s);
                    return ts;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @author zhuhaojie
     * @time 2016年11月24日上午9:32:32
     * @param s
     *            待转换的时间字符串
     * @return Timestamp 转换后的时间对象
     *
     */
    public static Timestamp stringToTimestamp(String s) {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        ts = Timestamp.valueOf(s);
        return ts;
    }

    /**
     * @author zhuhaojie
     * @time 2016年11月23日下午5:03:35
     * @param time
     *            待转换的字符串日期
     * @param pattern
     *            time按照pattern格式进行转换
     * @return Timestamp 时间戳类型
     *
     */
    public static Timestamp getTimestamp(String time, String pattern) {
        Timestamp begin = DateUtils.getNowTimeStampShort(DateTimeUtils.strToDate(time, pattern));
        return begin;
    }

    /**
     * 判断字符串是否是 YYYY-MM-DD HH:mm:ss格式的字符串
     * 
     * @author zhuhaojie
     * @time 2016年11月23日下午5:05:46
     * @param time
     *            待检验的字符串
     * @return boolean
     *
     */
    public static boolean isPatternTrue(String time) {
        // 按照空格进行切分
        String[] beginArray = time.split("\\s+");
        if (beginArray.length != 2) {
            return false;
        }
        String one = beginArray[0];
        if (!one.matches(DateUtils.TimePattern)) {
            return false;
        }
        String two = beginArray[1];
        if (!two.matches(DateUtils.TimePatternThird)) {
            return false;
        }
        return true;
    }

    /**
     * 获取当前日期上个月最后一天
     * 
     * @author zhuhaojie
     * @time 2016年9月27日下午1:53:18
     * @return 返回当前日期上个月最后一天日期对象
     */
    public static Date getLastMonthLastDay() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
                59, 59);
        Date d = calendar.getTime();
        return d;
    }

    /**
     * 返回当前时间
     * 
     * @author zhuhaojie
     * @time 2016年8月19日下午3:44:28
     * @param
     * @return String 格式:yyyy-MM-dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss");
        SimpleDateFormat formatter = getDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     * 
     * @return 返回短时间字符串格式yyyy-MM-dd
     * @author zhuhaojie
     * @time 2016年8月22日上午11:45:36
     * @param
     * 
     */
    public static String getTimeShort() {
        // SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat formatter = getDateFormat("HH:mm:ss");
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 将参数strDate转换为yyyy-MM-dd HH:mm:ss 格式的Date类型对象并返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日上午11:46:44
     * @param strDate
     *            将要转换的日期字符串
     * @return Date 转换好的Date对象
     */
    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = getDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 返回当天的开始时间 00:00:00
     * 
     * @author zhuhaojie
     * @time 2016年8月19日下午5:19:49
     * @param
     * @return long
     */
    public static Long getStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        Date d = todayStart.getTime();
        long time = d.getTime();
        return time;
    }

    /**
     * 根据date对象，获取此long对象 所指当天的开始时间
     * 
     * @author zhuhaojie
     * @time 2016年8月20日下午1:21:11
     * @param date
     *            指定的date对象
     * @return Long 此date对象代表的当天开始时间的long值表示
     */
    public static Long getStartTime(Date date) {
        if (date != null) {
            Calendar todayStart = Calendar.getInstance();
            todayStart.setTime(date);
            todayStart.set(Calendar.HOUR_OF_DAY, 0);
            todayStart.set(Calendar.MINUTE, 0);
            todayStart.set(Calendar.SECOND, 0);
            todayStart.set(Calendar.MILLISECOND, 0);
            Date d = todayStart.getTime();
            long time = d.getTime();
            return time;
        }
        return -1L;
    }

    /**
     * 获取当前日期 加day天后的日期 如果day<0就是 减去day天
     * 
     * @author zhuhaojie
     * @time 2016年11月23日下午1:17:43
     * @param day
     *            要加上或者减去的天数
     * @return Date 转换后的日期对象
     *
     */
    public static Date getAddDay(int day) {
        Date date = new Date(); // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day); // 当前时间减去一年，即一年前的时间
        date = calendar.getTime(); // 获取一年前的时间，或者一个月前的时间
        return date;

    }

    /**
     * 获取指定日期 加day天后的日期 如果day<0就是 减去day天
     * 
     * @author zhuhaojie
     * @time 2016年11月23日下午1:17:43
     * @param day
     *            要加上或者减去的天数
     * @param date
     *            指定的日期
     * @return Date 转换后的日期对象
     *
     */
    public static Date getAddDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day); // 当前时间减去一年，即一年前的时间
        date = calendar.getTime(); // 获取一年前的时间，或者一个月前的时间
        return date;

    }

    /**
     * 当前日期加上或者减month月，得到的时间
     * 
     * @author zhuhaojie
     * @time 2016年11月23日下午1:25:39
     * @param month
     *            要加上或者减去的月数
     * @return Date 转换后的日期对象
     *
     */
    public static Date getAddMonth(int month) {
        Date date = new Date(); // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month); // 当前时间前去一个月，即一个月前的时间
        date = calendar.getTime(); // 获取一年前的时间，或者一个月前的时间
        return date;

    }

    /**
     * 指定日期加上或者减month月，得到的时间
     * 
     * @author zhuhaojie
     * @time 2016年11月23日下午1:25:39
     * @param month
     *            要加上或者减去的月数
     * @param date
     *            指定的日期
     * @return Date 转换后的日期对象
     *
     */
    public static Date getAddMonth(Date date, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month); // 当前时间前去一个月，即一个月前的时间
        date = calendar.getTime(); // 获取一年前的时间，或者一个月前的时间
        return date;

    }

    /**
     * 将时间戳转换成字符串
     * 
     * @author zhuhaojie
     * @time 2016年11月29日下午3:30:13
     * @param time
     *            时间戳对象
     * @param style
     *            转换后的字符串格式
     * @return String 转换后的字符串
     *
     */
    public static String timeStampToString(Timestamp time, String style) {
        if (time == null || style == null) {
            return null;
        }
        style = style.trim();
        if ("".equals(style)) {
            return null;
        }
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat(style);
        result = format.format(time);
        return result;
    }

    /**
     * 当前时间加上或者减去多少年，得到的日期 正数代表加，负数代表减
     * 
     * @author zhuhaojie
     * @time 2016年11月23日下午1:26:52
     * @param year
     *            要减去或者加上的年数
     * @return Date 转换后的日期
     *
     */
    public static Date getAddYear(int year) {
        Date date = new Date(); // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);
        date = calendar.getTime(); // 获取一年前的时间，或者一个月前的时间
        return date;

    }

    /**
     * 当前时间加上或者减去多少年,天,月后得到的日期 正数代表加，负数代表减
     * 
     * @author zhuhaojie
     * @time 2016年11月23日下午1:30:04
     * @param year
     *            要加的年数
     * @param month
     *            要加的月数
     * @param day
     *            要加的天数
     * @return Date 转换后的日期
     *
     */
    public static Date getAddTime(int year, int month, int day) {
        Date date = new Date(); // 获取当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.YEAR, year);
        date = calendar.getTime(); // 获取一年前的时间，或者一个月前的时间
        return date;

    }

    /**
     * 获取当天时间的结束时间 的Long型表示 如：当前时间为2016-08-22 当天时间的结束时间：2016-08-22 23:59:59
     * 
     * @author zhuhaojie
     * @time 2016年8月20日下午1:21:11
     * @return Long 当前时间的结束时间 的Long型表示
     */
    public static Long getEndTime() {

        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 23);
        todayStart.set(Calendar.MINUTE, 59);
        todayStart.set(Calendar.SECOND, 59);
        todayStart.set(Calendar.MILLISECOND, 999);
        Date d = todayStart.getTime();
        long time = d.getTime();
        return time;

    }

    /**
     * 获取上个月最后一天
     * 
     * @author zhuhaojie
     * @time 2016年8月24日下午6:34:28
     * @param
     * @return DateUtils
     */
    public static String getPreviousMonthEnd() {
        String str = "";
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = getDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.add(Calendar.MONTH, -1); // 减一个月
        lastDate.set(Calendar.DATE, 1); // 把日期设置为当月第一天
        lastDate.roll(Calendar.DATE, -1); // 日期回滚一天，也就是本月最后一天
        str = sdf.format(lastDate.getTime());
        return str;
    }

    /**
     * 获取上个月第一天
     * 
     * @author zhuhaojie
     * @time 2016年8月24日下午6:36:48
     * @param
     * @return DateUtils
     */
    public static String getPreviousMonthFirst() {
        String str = "";
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = getDateFormat("yyyy-MM-dd");

        Calendar lastDate = Calendar.getInstance();
        lastDate.set(Calendar.DATE, 1); // 设为当前月的1号
        lastDate.add(Calendar.MONTH, -1); // 减一个月，变为下月的1号

        str = sdf.format(lastDate.getTime());
        return str;
    }

    /**
     * 根据date对象，获取此long对象 所指当天的结束时间
     * 
     * @author zhuhaojie
     * @time 2016年8月20日下午1:21:11
     * @param date
     *            指定的date对象
     * @return Long 此date对象代表的当天结束时间的long值表示
     */
    public static Long getEndTime(Date date) {
        if (date != null) {
            Calendar todayStart = Calendar.getInstance();
            todayStart.setTime(date);
            todayStart.set(Calendar.HOUR_OF_DAY, 23);
            todayStart.set(Calendar.MINUTE, 59);
            todayStart.set(Calendar.SECOND, 59);
            todayStart.set(Calendar.MILLISECOND, 999);
            Date d = todayStart.getTime();
            long time = d.getTime();
            return time;
        }
        return -1L;
    }

    /**
     * 将值为long的time，按照pattern的格式 转换为Date对象
     * 
     * @author zhuhaojie
     * @time 2016年8月22日上午11:53:26
     * @param time
     *            时间的long型表示
     * @param pattern
     *            转换后的Date类型表示 如："yyyy-MM-dd"
     * @return Date 转换后的date对象
     */
    public static Date getTime(long time, String pattern) {
        Date d = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        ParsePosition pos = new ParsePosition(0);
        String date = formatter.format(d);
        d = formatter.parse(date, pos);
        return d;
    }

    /**
     * 将date的时分秒及毫秒重新设置为参数后面的值 并以yyyy-MM-dd HH:mm:ss格式返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日上午11:55:22
     * @param date
     *            date对象
     * @param hour
     *            要修改成的hour值
     * @param minute
     *            新指定的minute值
     * @param second
     *            新指定的second值
     * @return Date 转换后的Date对象
     */
    public static Date getDate(Date date, int hour, int minute, int second) {

        // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss");
        SimpleDateFormat format = getDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);

        cal.set(Calendar.SECOND, second);

        cal.set(Calendar.MINUTE, minute);

        // cal.set(Calendar.MILLISECOND, milliSecond);

        Date date1 = new Date(cal.getTimeInMillis());

        try {

            return format.parse(format.format(date1));

        } catch (ParseException e) {

            e.printStackTrace();

        }

        return null;

    }

    /**
     * 将当前时间的时分秒及毫秒重新设置为参数后面的值 并以yyyy-MM-dd HH:mm:ss格式返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日上午11:55:22
     * @param hour
     *            要修改成的hour值
     * @param minute
     *            新指定的minute值
     * @param second
     *            新指定的second值
     * @param milliSecond
     *            新指定的毫秒值
     * @return Date 转换后的Date对象
     */
    public static Date getDate(int hour, int minute, int second, int milliSecond) {

        // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss");
        SimpleDateFormat format = getDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();

        cal.set(Calendar.HOUR_OF_DAY, hour);

        cal.set(Calendar.SECOND, second);

        cal.set(Calendar.MINUTE, minute);

        cal.set(Calendar.MILLISECOND, milliSecond);

        Date date = new Date(cal.getTimeInMillis());

        try {

            return format.parse(format.format(date));

        } catch (ParseException e) {

            e.printStackTrace();

        }

        return null;

    }

    /**
     * 将dateDate对象，转换为yyyy-MM-dd HH:mm:ss 格式的字符串返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日下午12:47:00
     * @param dateDate
     *            要转换的date对象
     * @return String 转换后的日期表示
     */
    public static String dateToStrLong(java.util.Date dateDate) {
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss");
        SimpleDateFormat formatter = getDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将dateDate对象，转换为yyyy-MM-dd 格式的字符串返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日下午12:47:00
     * @param dateDate
     *            要转换的date对象
     * @return String 转换后的日期表示
     */
    public static String dateToStr(Date dateDate) {
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter = getDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将strDate字符串按照pattern的格式 转换成Date对象并返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日下午12:50:32
     * @param strDate
     *            要转换的日期字符串
     * @param pattern
     *            转换成的目标格式
     * @return Date 转换完成的Date对象
     */
    public static Date strToDate(String strDate, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 获取原始格式的当前时间返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日下午12:49:43
     * @return Date
     */
    public static Date getNow() {
        Date currentTime = new Date();
        return currentTime;
    }

    /**
     * 提取一个月中的最后一天
     * 
     * @param day
     * @return
     * 
     *         public static Date getLastDate(long day) { Date date = new
     *         Date(); long date_3_hm = date.getTime() - 3600000 * 34 * day;
     *         Date date_3_hm_date = new Date(date_3_hm); return date_3_hm_date;
     *         }
     */
    /**
     * 获取当前时间，并按照yyyyMMdd HH:mm:ss 转换后返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日下午12:43:52
     * @return String 返回代表小时的字符串
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd
        // HH:mm:ss");
        SimpleDateFormat formatter = getDateFormat("yyyyMMdd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取当前时间，并按照yyyy-MM-dd HH:mm:ss 转换后，截取小时后返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日下午12:43:52
     * @return String 返回代表小时的字符串
     */
    public static String getHour() {
        Date currentTime = new Date();
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss");
        SimpleDateFormat formatter = getDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 13);
        return hour;
    }

    /**
     * 获取当前时间，并按照yyyy-MM-dd HH:mm:ss 转换后，截取分钟返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日下午12:43:52
     * @return String 返回代表分钟的字符串
     */
    public static String getTime() {
        Date currentTime = new Date();
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss");
        SimpleDateFormat formatter = getDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String min;
        min = dateString.substring(14, 16);
        return min;
    }

    /**
     * 根据指定的时间格式，将当前时间转换为 指定的时间格式并返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日下午12:41:23
     * @param sformat
     *            指定的时间格式
     * @return String 转换后的日期
     */
    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        // SimpleDateFormat formatter = new SimpleDateFormat(sformat);
        SimpleDateFormat formatter = getDateFormat(sformat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
     * 
     * public static String getTwoHour(String st1, String st2) { String[] kk =
     * null; String[] jj = null; kk = st1.split(":"); jj = st2.split(":"); if
     * (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0])) return "0"; else {
     * double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1]) / 60;
     * double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1]) / 60; if
     * ((y - u) > 0) return y - u + ""; else return "0"; } }
     */

    /**
     * 获取date1减去date2后相差的天数
     * 
     * @author zhuhaojie
     * @time 2016年8月22日下午12:36:49
     * @param date1
     *            日期
     * @param date2
     *            日期
     * @return DateUtils
     */
    public static String getTwoDay(String date1, String date2) {
        // SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormatter = getDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            Date date = myFormatter.parse(date1);
            Date mydate = myFormatter.parse(date2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }

    /**
     * 将date加减指定的分钟后转换为 yyyy-MM-dd HH:mm:ss格式的字符串返回
     * 
     * @author zhuhaojie
     * @time 2016年8月22日下午12:33:29
     * @param date
     *            原始日期
     * @param minute
     *            加减的分钟数 正表示加，负数表示减
     * @return DateUtils
     */
    public static String getPreTime(String date, int minute) {
        SimpleDateFormat format = getDateFormat("yyyy-MM-dd HH:mm:ss");
        // SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd
        // HH:mm:ss");
        String mydate1 = "";
        try {
            Date date1 = format.parse(date);
            long Time = (date1.getTime() / 1000) + minute * 60;
            date1.setTime(Time * 1000);
            mydate1 = format.format(date1);
        } catch (Exception e) {
        }
        return mydate1;
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     * 
     * public static String getNextDay(String nowdate, String delay) { try {
     * SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); String
     * mdate = ""; Date d = strToDate(nowdate); long myTime = (d.getTime() /
     * 1000) + Integer.parseInt(delay) * 24 60 * 60; d.setTime(myTime * 1000);
     * mdate = format.format(d); return mdate; } catch (Exception e) { return
     * ""; } } /** 获取时间d,加或减delay天后的日期 delay：整数为加，负数为减
     * 
     * @author zhuhaojie
     * @time 2016年8月19日下午6:14:50
     * @param d
     *            日期
     * @param delay
     *            参数
     * @return Date 返回的日期
     */
    public static Date getNextDayNew(Date d, String delay) {
        try {

            long myTime = (d.getTime() / 1000) + Integer.parseInt(delay) * 24 * 60 * 60;
            d.setTime(myTime * 1000);

            return d;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否润年
     * 
     * @param date
     *            要检查的日期字符串
     * @return boolean 是闰年返回true，否则false
     * 
     */
    public static boolean isLeapYear(String date) {
        /*
         * 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
         * 3.能被4整除同时能被100整除则不是闰年
         * 
         */
        Date d = strToDate(date, "yyyy-MM-dd");
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(d);
        int year = gc.get(Calendar.YEAR);
        boolean result = isRunNian(year);
        return result;

    }

    /**
     * 判断是否为闰年
     * 
     * @author zhuhaojie
     * @time 2016年9月23日下午2:59:40
     * @param year
     *            检验的年
     * @return boolean 如果是闰年返回true,否则返回false;
     */
    public static boolean isRunNian(int year) {
        if ((year % 400) == 0) {
            return true;
        } else {
            if ((year % 4) == 0) {
                if ((year % 100) == 0) {
                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * 
     * @author zhuhaojie
     * @time 2016年8月19日下午6:11:02
     * @param sdate
     * @param nd
     * @param yf
     * @param rq
     * @param format
     * @return DateUtils
     * @author zhuhaojie
     * @time 2016年8月19日下午5:36:09
     * @param sdate
     *            进行转换的字符串
     * @param m
     *            进行加减的月 整数代表加 负数代表减
     *
     */
    public static String getNextMonthDay(String sdate, int m) {
        sdate = getOKDate(sdate);
        int year = Integer.parseInt(sdate.substring(0, 4));
        int month = Integer.parseInt(sdate.substring(5, 7));
        month = month + m;
        if (month < 0) {
            month = month + 12;
            year = year - 1;
        } else if (month > 12) {
            month = month - 12;
            year = year + 1;
        }
        String smonth = "";
        if (month < 10)
            smonth = "0" + month;
        else
            smonth = "" + month;
        return year + "-" + smonth + "-10";
    }

    /**
     * 判断此字符串是否是合法的日期
     * 
     * @author zhuhaojie
     * @time 2016年8月19日下午5:34:30
     * @param sDate
     *            要验证的时间字符串
     * @return boolean
     */
    public static boolean isValidDate(String sDate) {

        String datePattern1 = "\\d{4}-\\d{2}-\\d{2}";
        String datePattern2 = "^((\\d{2}(([02468][048])|([13579][26]))"
                + "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
                + "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?"
                + "((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?("
                + "(((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
                + "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
        if (sDate != null) {
            Pattern pattern = Pattern.compile(datePattern1);
            Matcher match = pattern.matcher(sDate);
            if (match.matches()) {
                pattern = Pattern.compile(datePattern2);
                match = pattern.matcher(sDate);
                return match.matches();
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * 将sdate字符串转换为合适的时间格式
     * 
     * @author zhuhaojie
     * @time 2016年8月19日下午5:31:19
     * @param sdate
     *            时间参数
     * @return String
     */
    public static String getOKDate(String sdate) {
        if (sdate == null || sdate.equals(""))
            return getNowDateNewShort();
        if (!isValidDate(sdate)) {
            sdate = getNowDateNewShort();
        }
        // 将“/”转换为“-”
        sdate = sdate.replace("/", "-");
        // 如果只有8位长度，则要进行转换
        if (sdate.length() == 8)
            sdate = sdate.substring(0, 4) + "-" + sdate.substring(4, 6) + "-" + sdate.substring(6, 8);
        // SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter = getDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(sdate, pos);
        String dateString = formatter.format(strtodate);
        return dateString;
    }

    /**
     * 获取开始时间和结束时间段中所有的月
     * 
     * @author zhuhaojie
     * @time 2016年11月24日上午9:47:54
     * @param start
     *            开始时间
     * @param end
     *            结束时间
     * @return String[] 所有的月
     *
     */
    public static String[] getAllMonths(String start, String end) {
        String splitSign = "-";
        // ^\d+$
        String regex = "\\d{4}" + splitSign + "(([0][1-9])|([1][012]))"; // 判断YYYY-MM时间格式的正则表达式
        if (!start.matches(regex) || !end.matches(regex))
            return new String[0];

        List<String> list = new ArrayList<String>();
        if (start.compareTo(end) > 0) {
            // start大于end日期时，互换
            String temp = start;
            start = end;
            end = temp;
        }

        String temp = start; // 从最小月份开始
        while (temp.compareTo(start) >= 0 && temp.compareTo(end) <= 0) {
            list.add(temp); // 首先加上最小月份,接着计算下一个月份
            String[] arr = temp.split(splitSign);
            int year = Integer.valueOf(arr[0]);
            int month = Integer.valueOf(arr[1]) + 1;
            if (month > 12) {
                month = 1;
                year++;
            }
            if (month < 10) { // 补0操作
                temp = year + splitSign + "0" + month;
            } else {
                temp = year + splitSign + month;
            }
        }

        int size = list.size();
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    /**
     * 将开始时间和结束时间按天进行切割 并以数组字符串形式返回 yyy-MM-dd
     * 
     * @author zhuhaojie
     * @time 2016年10月14日上午10:12:27
     * @param startStr
     *            开始时间
     * @param endStr
     *            结束时间
     *
     * @return 切割后的时间字符串数组
     */
    public static String[] getAllDays(String startStr, String endStr) {
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = getDateFormat("yyyy-MM-dd");
        Date start = null;
        Date end = null;
        try {
            start = sdf.parse(startStr);
            end = sdf.parse(endStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        List<Date> lists = dateSplit(start, end);
        if (lists == null) {
            return null;
        }
        int size = lists.size();
        if (size == 0) {
            return null;
        }
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            Date date = lists.get(i);
            result[i] = sdf.format(date);
        }
        // 反转数组
        //result = ArrayUtils.reverseArray2(result);
        return result;

    }

    /**
     * 格式 YYYY
     * 
     * @author：zhuhaojie
     * @time：2017年3月30日 下午2:38:10
     * @param startStr
     *            开始年份
     * @param endStr
     *            结束年份
     * @return String[] 结束年份和开始年份之间的所有年份，包括开始和结束
     */
    public static String[] getAllYears(String startStr, String endStr) {
        String regex = "\\d{4}"; // 判断YYYY时间格式的正则表达式
        if (!startStr.matches(regex) || !endStr.matches(regex)) {
            return null;
        }
        startStr = startStr.trim();
        endStr = endStr.trim();
        if (startStr.equals(endStr)) {
            return new String[] { startStr };
        }
        int startStrInt = Integer.parseInt(startStr);
        int endStrInt = Integer.parseInt(endStr);
        if (endStrInt < startStrInt) {
            int t = endStrInt;
            endStrInt = startStrInt;
            startStrInt = t;
        }
        int cha = endStrInt - startStrInt;
        String[] result = new String[cha + 1];
        for (int i = cha; i >= 0; i--) {
            result[i] = endStrInt + "";
            endStrInt = endStrInt - 1;
        }
        return result;
    }

    /**
     * @param begin
     *            开始时间
     * @param end
     *            结束时间
     * @author：zhuhaojie
     * @time：2017年4月1日 上午10:14:41
     * @return map 按照日历切分的多个周 key为第几周 String[]对应当周对应的日期数组
     */
    public static Map<String, String[]> getAllWeeks(Date begin, Date end) {
        List<Date> listDates = dateSplit(begin, end);
        if (listDates == null) {
            return null;
        }
        int length = listDates.size(); // 总天数
        if (length == 0) {
            return null;
        }
        //返回一个有序的Map
        Map<String, String[]> result = new LinkedHashMap<String, String[]>();
        int m = 1;
        int k = 0;
        String[] weekDays = new String[7];
        for (int i = 0; i < length; i++) {

            Date d = listDates.get(i);
            int weekInt = dayOfWeekInt(d);
            if (weekInt == 7) {
                weekDays[k] = dateToStr(d);
                // 删除数组中的null
                weekDays = ArrayUtils.deleteOne(weekDays, null);
                result.put(m + "", weekDays);
                m++;
                weekDays = new String[7];
                k = 0;
            } else {
                weekDays[k] = dateToStr(d);
                k++;
            }
            if (i == length - 1 && weekInt != 7) {
                weekDays = ArrayUtils.deleteOne(weekDays, null);
                result.put(m + "", weekDays);
            }

        }
        return result;
    }

    /**
     * /** 根据日期得到星期几,得到数字。<br/>
     * 7, 1, 2, 3, 4, 5, 6
     *
     * @author：zhuhaojie
     * @param date
     *            日期
     * @time：2017年4月1日 上午10:48:51
     * @return Integer 日期对应的星期代表的时间
     */
    public static int dayOfWeekInt(Date date) {
        Integer[] dayNames = { 7, 1, 2, 3, 4, 5, 6 };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        // calendar.setFirstDayOfWeek(1);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0)
            dayOfWeek = 0;
        return dayNames[dayOfWeek];
    }

    /**
     * 将开始时间和结束时间按天进行切割
     * 
     * @author zhuhaojie
     * @time 2016年10月14日上午10:10:42
     * @param startDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @return List<Date> 按天切割后的date集合对象
     *
     */
    private static List<Date> dateSplit(Date startDate, Date endDate) {
        long startLong = startDate.getTime();
        long endLong = endDate.getTime();
        if (startLong > endLong) {
            return null;
        }
        Long spi = endLong - startLong;
        Long step = spi / (24 * 60 * 60 * 1000); // 相隔天数

        List<Date> dateList = new ArrayList<Date>();
        dateList.add(endDate);
        for (int i = 1; i <= step; i++) {
            dateList.add(new Date(dateList.get(i - 1).getTime() - (24 * 60 * 60 * 1000))); // 比上一天减一
        }
        // 反转集合元素
        Collections.reverse(dateList);
        return dateList;
    }

//    public static void main(String[] args) {
//      Date  date = DateUtils.getNowDateShort("yyyy-MM-dd");
//          System.out.println(date);
//       // long getStartTime();
//    }

}