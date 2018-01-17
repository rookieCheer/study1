package com.huoq.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.joda.time.DateTime;


/**生成流水号工具
 * @author 传虎欧巴
 * @createTime 2015-11-17下午4:42:32
 */
public class OrderNumerUtil {

    private static int num = (int) (Math.random() * 100);

    public static String generateRequestId() {
        int i = (int) (Math.random() * 100);
        StringBuilder sb = new StringBuilder();
        sb.append(DateTime.now().toDate().getTime() - DateTime.parse("2015-10-15").toDate().getTime()).append(String.format("%03d", num)).append(String.format("%03d", i));
        num++;
        if (num > 999) {
            num = (int) (Math.random() * 100);
        }
        return sb.toString();
    }
    
    public static synchronized String getSerialNumber(Integer itradeType, Integer id) {
        int machineId = 9;
        int hashCodeV = UUID.randomUUID().toString().hashCode();
        if (hashCodeV < 0) {
            hashCodeV = -hashCodeV;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%02d", itradeType));
        sb.append(getDate(new Date(), "yyyyMMdd"));
        sb.append(machineId + String.format("%010d", hashCodeV));
        sb.append(String.format("%0" + 5 + "d", id));
        return sb.toString();
    }

    public static final String getDate(Date date, String pattern) {
        SimpleDateFormat df = null;
        String returnValue = "";
        if (date != null) {
            df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }
        return (returnValue);
    }
}
