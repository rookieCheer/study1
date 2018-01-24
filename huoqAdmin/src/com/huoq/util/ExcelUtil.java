package com.huoq.util;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.huoq.common.util.DateUtils;
import com.huoq.bean.FullScaleCompanyMessage;
import com.huoq.bean.InnerCompanyMessage;
import com.alibaba.fastjson.JSON;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;

/**
 * 导入导出Excel工具测试成功 导出类型只支持xls格式
 * 
 * @author zhuhaojie 2016年7月19日下午5:58:49
 * @param <T>
 */
public class ExcelUtil<T> {

    /**
     * 传送路径
     */
    public static String PATH;
    /**
     * 格式化日期是对应的key
     */
    public static String DATE_STYLE       = "java.util.Date";
    /**
     * 上传文件最大值8M
     */
    public static int    MAX_SIZE         = 8;
    /**
     * Excel2003文件类型
     */
    public static String EXCEL_STYLE2003  = "application/vnd.ms-excel";
    /**
     * Excel2007文件类型
     */
    public static String EXCEL_STYLE2007  = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    /**
     * Excel2003文件后缀
     */
    public static String EXCEL_SUFFIX2003 = "xls";
    /**
     * Excel2007文件后缀
     */
    public static String EXCEL_SUFFIX2007 = "xlsx";

    /**
     * 封闭默认构造方法，以免 调用者创建此类对象
     * 
     * @author zhuhaojie
     * @time 2016年12月27日 上午9:48:30
     */
    private ExcelUtil() {
    }

    /**
     * 将数据以Excel形式导出到外部文件
     * 
     * @author zhuhaojie
     * @time 2016年7月20日下午1:01:05
     * @param out 导出文件路径
     * @param title 导出excel工作表名称
     * @param fieldMap 导出时excel标题名称与实体类属性名对应关系 其中key为excel标题 value为实体类属性名
     * @param dataset 准备导出的数据集合对象
     * @param dataStyle 格式化特殊数据时的映射 其中键已经指定暂时只有：DATE_STYLE(本类的静态成员)，代表格式化日期时的键 value:为合法的日期格式如："yyyy-MM-dd"
     * @param <T> 符合外部调用者指定的任何可能对象
     * @throws NullPointerException 抛出此异常的情况： 1.参数列表中任意一个参数为null 2.headers或者dataset中每没有数据 3、title为空字符串
     * @throws IllegalAccessException 通过Class构造该类对象时可能抛出非法状态异常
     * @throws NoSuchMethodException 指定的方法在对应bean中没有找到
     * @throws IllegalArgumentException 通过Class构造该类对象时可能抛出非法参数异常
     * @throws IOException 写出excel时可能抛出的异常
     */

    public static <T> void exportExcelNew(OutputStream out, String title, LinkedHashMap<String, String> fieldMap, List<T> dataset,
                                          Map<String, String> dataStyle) throws NullPointerException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
                                                                         IOException {
        if (out == null) {
            throw new NullPointerException("指定的输出流为");
        }
        HSSFWorkbook book = exportExcel(title, fieldMap, dataset, dataStyle);
        if (book != null) {
            try {
                book.write(out);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }

    }

    /**
     * 将数据以Excel形式导出到外部文件
     * 
     * @author zhuhaojie
     * @time 2016年7月20日下午1:01:05
     * @param title 导出excel工作表名称
     * @param fieldMap 导出时excel标题名称与实体类属性名对应关系 其中key为excel标题 value为实体类属性名
     * @param dataset 准备导出的数据集合对象
     * @param dataStyle 格式化特殊数据时的映射 其中键已经指定暂时只有：DATE_STYLE(本类的静态成员)，代表格式化日期时的键 value:为合法的日期格式如："yyyy-MM-dd"
     * @param <T> 符合外部调用者指定的任何可能对象
     * @throws NullPointerException 抛出此异常的情况： 1.参数列表中任意一个参数为null 2.headers或者dataset中每没有数据 3、title为空字符串
     * @throws IllegalAccessException 通过Class构造该类对象时可能抛出非法状态异常
     * @throws NoSuchMethodException 指定的方法在对应bean中没有找到
     * @throws IllegalArgumentException 通过Class构造该类对象时可能抛出非法参数异常
     * @return HSSFWorkbook excel工作表对象 String[] headers, String[] include,
     */
    public static <T> HSSFWorkbook exportExcel(String title, LinkedHashMap<String, String> fieldMap, List<T> dataset,
                                               Map<String, String> dataStyle) throws NullPointerException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException {

        // 工作薄
        @SuppressWarnings("resource")
        HSSFWorkbook workbook = new HSSFWorkbook();

        // 表格
        HSSFSheet sheet = workbook.createSheet(title);

        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        Iterator<Map.Entry<String, String>> iter = fieldMap.entrySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            // System.out.println("key= " + entry.getKey() + " and value= " +
            // entry.getValue());
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(entry.getKey());
            cell.setCellValue(text);
            i++;
        }

        Iterator<T> it = dataset.iterator();
        int index = 0;
        while (it.hasNext()) {
            index++;
            row = sheet.createRow(index);
            // 获取单个数据
            T t = (T) it.next();
            // 遍历属性列
            int j = 0;
            for (Entry<String, String> entry : fieldMap.entrySet()) {

                HSSFCell cell = row.createCell(j); // 创建一个表格
                j++;
                cell.setCellStyle(style2); // 设置表格样式

                String fieldName = entry.getValue();
                if (fieldName == null) {
                    throw new NullPointerException("成员变量名不能为null");
                }
                fieldName = fieldName.trim();
                if (fieldName.equals("")) {
                    throw new NullPointerException("成员变量名不能为空字符串");
                }
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                try {
                    @SuppressWarnings("rawtypes")
                    Class tCls = t.getClass();
                    @SuppressWarnings("unchecked")
                    Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
                    Object value = null;
                    try {
                        value = getMethod.invoke(t, new Object[] {});
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    String textValue = null;

                    if (value instanceof Character) {
                        char charValue = (Character) value;
                        cell.setCellValue(charValue);
                    } else if (value instanceof Integer) {
                        int intValue = (Integer) value;
                        cell.setCellValue(intValue);
                    } else if (value instanceof Float) {
                        float fValue = (Float) value;

                        cell.setCellValue(fValue);
                    } else if (value instanceof Double) {
                        double dValue = (Double) value;

                        cell.setCellValue(dValue);
                    } else if (value instanceof Long) {
                        long longValue = (Long) value;
                        cell.setCellValue(longValue);
                    } else if (value instanceof Boolean) {
                        boolean booleanValue = (Boolean) value;
                        cell.setCellValue(booleanValue);
                    } else if (value instanceof Date) {
                        Date date = (Date) value;
                        if (dataStyle == null) {
                            throw new NullPointerException("dataStyle对象为null");
                        }
                        // if (!dataStyle.containsKey(ExcelUtil.DATE_STYLE)) {
                        // throw new RuntimeException("日期转换格式未指定");
                        // }
                        String fildNamedateStyle = dataStyle.get(fieldName);
                        if (fildNamedateStyle == null) {
                            throw new RuntimeException("属性名为:" + fieldName + "日期转换格式未指定");
                        }
                        SimpleDateFormat sdf = new SimpleDateFormat(fildNamedateStyle);
                        cell.setCellValue(sdf.format(date)); //

                    } else if (value instanceof byte[]) { // 是否是字节型数组
                        row.setHeightInPoints(60);
                        // // 设置图片所在列宽度为80px, 注意这里单位的一个换算
                        sheet.setColumnWidth(j, (short) (35.7 * 80));
                        sheet.autoSizeColumn(j);
                        byte[] bsValue = (byte[]) value;
                        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 6, index, (short) 6, index);
                        anchor.setAnchorType(2);
                        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
                        patriarch.createPicture(anchor, workbook.addPicture(bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));

                    } else if (value instanceof Collection) {

                    } else if (value instanceof Class) {

                    } else {
                        // 其它数据类型都当作字符串简单处理
                        if (value == null) {
                            textValue = "";
                        } else {
                            textValue = value.toString();
                        }

                    }
                    if (textValue != null) {
                        Pattern p = Pattern.compile("^//d+ (//.//d+)?$");
                        Matcher matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            // 是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            HSSFRichTextString richString = new HSSFRichTextString(textValue);
                            HSSFFont font3 = workbook.createFont();
                            font3.setColor(HSSFColor.BLUE.index);
                            richString.applyFont(font3);
                            cell.setCellValue(richString);
                        }
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    throw e;
                }

            }
        }

        return workbook;

    }

    public static HSSFWorkbook getWorkbook(String title, LinkedHashMap<String, String> fieldMap, List<FullScaleCompanyMessage> data,
                                           Map<String, String> dataStyle) throws NullPointerException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException {

        // 工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();

        // 表格
        HSSFSheet sheet = workbook.createSheet(title);
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        //sheet.setColumnWidth((short) 0, (short) 2500);
        Iterator<Map.Entry<String, String>> iter = fieldMap.entrySet().iterator();
        int index = 0;
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            HSSFCell cell = row.createCell(index);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(entry.getKey());
            cell.setCellValue(text);
            index++;
        }

        Iterator<FullScaleCompanyMessage> it = data.iterator();
        index = 0;
        HSSFCellStyle style1 = workbook.createCellStyle();
        // 设置这些样式

        style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
        while (it.hasNext()) {
            index++;
            // 创建一行
            row = sheet.createRow(index);
            sheet.setDefaultColumnWidth(18); //修改by Gaoqs
            // 获取单个数据
            FullScaleCompanyMessage message = it.next();
            int childBidNumber = message.getChildBidNumber();
            List<InnerCompanyMessage> innerList = message.getInnerMessage();
            if (innerList != null) {
                int j = 5;
                int k = index * childBidNumber;
                if (childBidNumber > 1) {
                    k = k - 1;
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (InnerCompanyMessage innerMessage : innerList) {
                    HSSFRow innerRow = sheet.createRow(k);
                    k++;
                    HSSFCell cell6 = innerRow.createCell(j); //

                    cell6.setCellValue(innerMessage.getNumber());
                    cell6.setCellStyle(style1);
                    HSSFCell cell7 = innerRow.createCell(j + 1); //

                    Date fullTagTime = (Date) innerMessage.getFullTagDate();
                    cell7.setCellValue(sdf.format(fullTagTime));
                    cell7.setCellStyle(style1);
                    HSSFCell cell8 = innerRow.createCell(j + 2); //
                    Date expirTime = (Date) innerMessage.getExpiringDate();
                    cell8.setCellValue(sdf.format(expirTime));
                    cell8.setCellStyle(style1);
                    HSSFCell cell11 = innerRow.createCell(j + 5); //
                    Double virtualInvest = (Double) innerMessage.getVirtualInvest().doubleValue();
                    cell11.setCellValue(virtualInvest);
                    cell11.setCellStyle(style1);
                }
            }

            HSSFCell cell = row.createCell(0); // 序号

            HSSFRichTextString no = new HSSFRichTextString(message.getNo());
            // 四个参数分别是：起始行，结束行，开始列，结束列
            sheet.addMergedRegion(new CellRangeAddress(index, index + childBidNumber - 1, 0, 0));
            cell.setCellValue(no);
            cell.setCellStyle(style1);
            HSSFCell cell2 = row.createCell(1); // 借款公司名称

            HSSFRichTextString name = new HSSFRichTextString(message.getCompanyName());

            sheet.addMergedRegion(new CellRangeAddress(index, index + childBidNumber - 1, 1, 1));
            cell2.setCellValue(name);
            cell2.setCellStyle(style1);

            HSSFCell cell3 = row.createCell(2); // 借款额度
            cell3.setCellStyle(style1);
            double browLimit = message.getBrowLimit().doubleValue();

            sheet.addMergedRegion(new CellRangeAddress(index, index + childBidNumber - 1, 2, 2));
            cell3.setCellValue(browLimit);
            HSSFCell cell4 = row.createCell(3); // 标的类型
            cell4.setCellStyle(style1);
            HSSFRichTextString type = new HSSFRichTextString(message.getType());

            sheet.addMergedRegion(new CellRangeAddress(index, index + childBidNumber - 1, 3, 3));
            cell4.setCellValue(type);
            HSSFCell cell5 = row.createCell(4); // 创建第5个单元格

            sheet.addMergedRegion(new CellRangeAddress(index, index + childBidNumber - 1, 4, 4));
            cell5.setCellValue(childBidNumber);
            cell5.setCellStyle(style1);
            //
            HSSFCell cell9 = row.createCell(8); // 企业到期时间
            Date date = (Date) message.getCompanyDueTime();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            sheet.addMergedRegion(new CellRangeAddress(index, index + childBidNumber - 1, 8, 8));
            cell9.setCellStyle(style1);
            cell9.setCellValue(sdf.format(date));
            HSSFCell cell10 = row.createCell(9); // 企业回款时间
            Date dueTime = (Date) message.getBackMoneyTime();

            sheet.addMergedRegion(new CellRangeAddress(index, index + childBidNumber - 1, 9, 9));
            cell10.setCellValue(sdf.format(dueTime));
            cell10.setCellStyle(style1);
            HSSFCell cell12 = row.createCell(11); // 实际投资总额
            cell12.setCellValue(message.getRealInvest().doubleValue());
            sheet.addMergedRegion(new CellRangeAddress(index, index + childBidNumber - 1, 11, 11));
            cell12.setCellStyle(style1);

        }

        return workbook;

    }

    /**
     * 将外部excel导入到程序后转换成指定的bean集合
     * 
     * @param in 输入流对象
     * @param entityClass 实体类Class对象
     * @param fieldMap 标题名称对应的实体类属性名
     * @param dataStyle 格式化特殊数据时的映射 其中键已经指定暂时只有：DATE_STYLE(本类的静态成员)，代表格式化日期时的键 value:为合法的日期格式如："yyyy-MM-dd"
     * @param <T> 符合外部调用者指定的任何可能对象
     * @return List 实体对象列表
     * @throws IOException 当将输入流转换成WorkBook对象时可能抛出的异常
     * @throws ParseException 按照dataStyle格式化数据时可能抛出的异常
     * @throws IllegalArgumentException 通过Class构造该类对象时可能抛出的异常
     * @throws IllegalAccessException 通过Class构造该类对象时可能抛出的异常
     * @throws InstantiationException 通过Class构造该类对象时可能抛出的异常
     * @throws InvalidFormatException 格式化日期或其他类型时可能抛出的异常
     */
    public static <T> List<T> importExcel(InputStream in, Class<T> entityClass, LinkedHashMap<String, String> fieldMap,
                                          Map<String, String> dataStyle) throws IOException, InstantiationException, IllegalAccessException, IllegalArgumentException,
                                                                         ParseException, InvalidFormatException {

        if (in == null) {
            throw new NullPointerException("输入流对象为null");
        }

        if (entityClass == null) {
            throw new NullPointerException("实体类Class对象为null");
        }
        if (fieldMap == null) {
            throw new NullPointerException("fieldMap为null");
        }
        if (fieldMap.isEmpty()) {
            throw new NullPointerException("fieldMap中没有数据");
        }

        // 定义要返回的list
        List<T> resultList = new ArrayList<T>();
        // 将文件转换成了Workbook
        Workbook wb = readExcelNew(in);
        resultList = readNew(wb, entityClass, fieldMap, dataStyle);
        return resultList;
    }

    /**
     * 判断工作表中是否有有效数据
     * 
     * @param sheet 单元格对象
     * @param style 单元格样式
     * @author zhuhaojie
     * @time 2016年9月21日下午7:15:44
     * @return int 有效数据行数
     */
    private static int isEmpty(Sheet sheet, Map<String, String> style) {

        int realRows = 0;

        int line2 = sheet.getLastRowNum() + 1; // 总行数可能中间包括空白行
        for (int j = 0; j < line2; j++) {
            int nullCols = 0;
            // 循环获取每一行数据
            Row row = sheet.getRow(j);
            if (row == null) {
                continue;
            }
            int number = row.getPhysicalNumberOfCells();

            if (number == 0) {
                continue;
            }
            Iterator<Cell> cells = row.cellIterator(); // 获取每一行的格子
            // 记录格子个数
            int columnsNum = 0;
            while (cells.hasNext()) {
                columnsNum++;
                Cell cell = cells.next();
                String value = getCellValue(cell, style);
                if (value == null) {
                    // 记录无效格子个数
                    nullCols++;
                    continue;
                }
                value = value.trim();
                if (value.equals("")) {
                    nullCols++;
                }

            }
            // 如果相等表明这一行没有任何数据
            if (nullCols == columnsNum) {
                break;
            } else {
                // 如果不相等，将有效行加1
                realRows++;
            }
        }
        return realRows;
    }

    /**
     * @author zhuhaojie
     * @time 2016年9月21日下午7:22:51
     * @param wb excel对象
     * @param entityClass 准备转换成的实体类Class对象
     * @param fieldMap 要导入的工作表标题与实体类属性名的对应关系 这里注意：key为工作表标题名称 value:实体类对应的成员变量名称
     * @param dataStyle 格式化特殊数据时的映射 其中键已经指定暂时只有：DATE_STYLE(本类的静态成员)，代表格式化日期时的键 value:为合法的日期格式如："yyyy-MM-dd" 如果没有可以传null
     * @param <T> 符合外部调用者指定的任何可能对象
     * @return List 转换后的实体对象集合
     * @throws IllegalAccessException 反射获取实体类对象时可能返回的异常
     * @throws InstantiationException 反射获取实体类对象时可能返回的异常
     * @throws ParseException 日期转换时可能抛出的异常
     * @throws IllegalArgumentException 反射获取实体类对象时可能返回的异常
     */
    private static <T> List<T> readNew(Workbook wb, Class<T> entityClass, LinkedHashMap<String, String> fieldMap,
                                       Map<String, String> dataStyle) throws InstantiationException, IllegalAccessException, IllegalArgumentException, ParseException {
        if (wb != null) {
            // 获取文件中工作表个数
            int numberSheet = wb.getNumberOfSheets();
            if (numberSheet > 0) {
                for (int i = 0; i < numberSheet; i++) {
                    // 循环获取每一个工作表
                    Sheet sheet = wb.getSheetAt(i);
                    if (sheet != null) {

                        // 获取此工作表的有效数据行数
                        int line = sheet.getPhysicalNumberOfRows();
                        int line2 = sheet.getLastRowNum() + 1;
                        int realRows = 0;
                        if (line > 0) {
                            realRows = isEmpty(sheet, dataStyle);
                            if (realRows <= 1) {
                                throw new RuntimeException("Excel文件中没有任何数据");
                            }
                            // 获取第一行数据，也就是标题
                            Row row = sheet.getRow(0);
                            // Iterator<Cell>
                            // cells=row.cellIterator();//获取第一行的格子
                            int number = row.getPhysicalNumberOfCells(); // 获取有效单元格
                            int fieldMapLength = fieldMap.size();
                            if (number < fieldMapLength) {
                                throw new RuntimeException("Excel中缺少必要的字段，或字段名称有误");
                            }
                            // 获取列名
                            String[] filedName = getCellValues(row);
                            if (filedName == null) {
                                throw new RuntimeException("列名不存在");
                            }

                            // 判断数组是否有重复值
                            boolean t = hasChongFu(filedName);
                            if (t) {
                                throw new RuntimeException("Excel标题不允许重复");
                            }
                            // 判断所需字段在Excel中是否都存在
                            boolean isExist = true;
                            List<String> excelFieldList = Arrays.asList(filedName);
                            System.out.println(JSON.toJSONString(excelFieldList));
                            for (String cnName : fieldMap.keySet()) {
                                if (!excelFieldList.contains(cnName)) {
                                    System.out.println(cnName);
                                    isExist = false;
                                    break;
                                }
                            }
                            // 如果有列名不存在，则抛出异常，提示错误
                            if (!isExist) {
                                throw new RuntimeException("Excel中缺少必要的字段，或字段名称有误");
                            }

                            // 将有效的列名和列号放入Map中,这样通过列名就可以拿到列号
                            LinkedHashMap<String, Integer> colMap = new LinkedHashMap<String, Integer>();
                            // fieldMap.keySet()
                            int excelLength = filedName.length;
                            for (int j = 0; j < excelLength; j++) {
                                String fieldName = filedName[j]; // 获取名称
                                for (String cnName : fieldMap.keySet()) {
                                    if (fieldName.equals(cnName)) {
                                        // int columnNumber=row.
                                        colMap.put(fieldName, j);
                                    }
                                }

                            }
                            List<T> resultList = new ArrayList<T>();
                            // 循环数据不包括标题
                            for (int j = 1; j < line2; j++) {
                                Row row1 = sheet.getRow(j);
                                if (row1 == null) {
                                    continue;
                                }

                                T entity = entityClass.newInstance();
                                for (Entry<String, String> entry : fieldMap.entrySet()) {
                                    // 获取中文字段名
                                    String cnNormalName = entry.getKey();
                                    // 获取英文字段名
                                    String enNormalName = entry.getValue();
                                    // 根据中文字段名获取列号
                                    int col = colMap.get(cnNormalName);
                                    Cell cell = row1.getCell(col); // 获取指定的格子
                                    if (cell != null) {
                                        String content = getCellValue(cell, dataStyle);
                                        // 给对象赋值,并指定日期转换格式,如果没有返回
                                        setFieldValueByName(enNormalName, content, entity, dataStyle);
                                    }
                                }
                                // 将实体对象加入集合
                                resultList.add(entity);

                            }
                            // 返回集合
                            return resultList;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取单个单元格中的数据并以String形式返回
     * 
     * @param cell 单元格对象
     * @param mapStyle 单元格样式
     * @author zhuhaojie
     * @time 2016年9月21日下午7:29:41
     * @return String
     */
    public static String getCellValue(Cell cell, Map<String, String> mapStyle) {
        String cellValue = "";

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                cellValue = cell.getRichStringCellValue().getString().trim();
                break;
            case Cell.CELL_TYPE_NUMERIC: // 数值型
                int timeValue = cell.getCellStyle().getDataFormat();
                if (HSSFDateUtil.isCellDateFormatted(cell)) { // 处理日期格式、时间格式
                    SimpleDateFormat sdf = null;
                    if (timeValue == HSSFDataFormat.getBuiltinFormat("h:mm")) {
                        // sdf = new SimpleDateFormat("HH:mm");
                        sdf = DateUtils.getDateFormat("HH:mm");
                    } else { // 日期
                        // sdf = new SimpleDateFormat("yyyy-MM-dd");
                        sdf = DateUtils.getDateFormat("yyyy-MM-dd");
                    }
                    Date date = cell.getDateCellValue();
                    cellValue = sdf.format(date);
                } else if (timeValue == 58) { // 月日
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    DateUtils.getDateFormat("MM-dd");
                    // SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
                    SimpleDateFormat sdf = DateUtils.getDateFormat("MM-dd");
                    double value1 = cell.getNumericCellValue();
                    Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value1);
                    cellValue = sdf.format(date);
                } else if (timeValue == 31) { // 年月日
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    SimpleDateFormat sdf = new SimpleDateFormat(mapStyle.get(DATE_STYLE));
                    double value1 = cell.getNumericCellValue();
                    Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value1);
                    cellValue = sdf.format(date);
                } else if (timeValue == 32) { // 时分
                    // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)
                    // SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    SimpleDateFormat sdf = DateUtils.getDateFormat("HH:mm");
                    ;
                    double value1 = cell.getNumericCellValue();
                    Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value1);
                    cellValue = sdf.format(date);
                } else {
                    double value1 = cell.getNumericCellValue();
                    CellStyle style = cell.getCellStyle();
                    DecimalFormat format = new DecimalFormat();
                    String temp = style.getDataFormatString();
                    // 单元格设置成常规
                    if (temp.equals("General")) {
                        format.applyPattern("#");
                    }
                    cellValue = format.format(value1);
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue()).trim();
                break;
            case Cell.CELL_TYPE_FORMULA:
                cellValue = cell.getCellFormula();
                break;
            default:
                cellValue = "";
        }
        return cellValue;
    }

    /**
     * 通过成员变量名称为对象成员变量赋值
     * 
     * @author zhuhaojie
     * @time 2016年9月22日上午9:23:12
     * @param fieldName 成员变量名称
     * @param fieldValue 准备赋给成员变量的值
     * @param o 准备赋值的对象
     * @param dataStyle 格式化特殊数据时的映射 其中键已经指定暂时只有：DATE_STYLE(本类的静态成员)，代表格式化日期时的键 value:为合法的日期格式如："yyyy-MM-dd"
     * @throws IllegalAccessException 获取实体对象时可能抛出的异常
     * @throws IllegalArgumentException 获取实体对象时可能抛出的异常
     * @throws ParseException 转换格式时如果出错抛出此异常
     */
    private static void setFieldValueByName(String fieldName, Object fieldValue, Object o, Map<String, String> dataStyle) throws IllegalArgumentException, IllegalAccessException,
                                                                                                                          ParseException {

        Field field = getFieldByName(fieldName, o.getClass());
        if (field != null) {
            field.setAccessible(true);
            // 获取字段类型
            Class<?> fieldType = field.getType();

            // 根据字段类型给字段赋值
            if (String.class == fieldType) {
                field.set(o, String.valueOf(fieldValue));
            } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
                if (fieldValue == null || fieldValue.equals("")) {
                    fieldValue = "0";
                }
                field.set(o, Integer.parseInt(fieldValue.toString()));
            } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
                field.set(o, Long.valueOf(fieldValue.toString()));
            } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
                field.set(o, Float.valueOf(fieldValue.toString()));
            } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
                field.set(o, Short.valueOf(fieldValue.toString()));
            } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
                if (fieldValue == "") {
                    fieldValue = 0;
                }
                field.set(o, Double.valueOf(fieldValue.toString()));
            } else if (Character.TYPE == fieldType) {
                if ((fieldValue != null) && (fieldValue.toString().length() > 0)) {
                    field.set(o, Character.valueOf(fieldValue.toString().charAt(0)));
                }
            } else if (Date.class == fieldType) {
                if (dataStyle == null) {
                    throw new NullPointerException("dataStyle对象为null");
                }
                if (!dataStyle.containsKey(ExcelUtil.DATE_STYLE)) {
                    throw new RuntimeException("日期转换格式未指定");
                }
                String dateStyle = dataStyle.get(ExcelUtil.DATE_STYLE);
                SimpleDateFormat sdf = new SimpleDateFormat(dateStyle);
                Date d;
                try {
                    d = sdf.parse(fieldValue.toString());
                } catch (Exception e) {
                    d = null;
                }
                field.set(o, d);
            } else {
                field.set(o, fieldValue);
            }
        } else {
            throw new RuntimeException(o.getClass().getSimpleName() + "类不存在字段名 " + fieldName);
        }
    }

    /**
     * 根据成员变量名称及对象的Class对象获取此 成员变量所属的Field对象
     * 
     * @author zhuhaojie
     * @time 2016年9月22日上午9:27:01
     * @param fieldName 成员变量名称
     * @param clazz 对象对应的Class类型对象
     * @return 成员变量所属的Field对象
     */
    private static Field getFieldByName(String fieldName, Class<?> clazz) {
        // 拿到本类的所有字段
        Field[] selfFields = clazz.getDeclaredFields();
        for (Field field : selfFields) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        // 否则，查看父类中是否存在此字段，如果有则返回
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null && superClazz != Object.class) {
            return getFieldByName(fieldName, superClazz);
        }
        // 如果本类和父类都没有，则返回空
        return null;
    }

    /**
     * 判断数组中是否有重复数据，如果有返回true，否则返回false
     * 
     * @author zhuhaojie
     * @time 2016年9月22日上午9:29:54
     * @param strs 需要判断的数组
     * @return boolean 是否含有重复元素的判断结果 true:含有重复元素 false:没有重复元素
     */
    public static boolean hasChongFu(String[] strs) {
        boolean result = false;
        // 从第一个元素开始比较元素是不是有相同的出现
        int length = strs.length;
        for (int i = 0; i < length; i++) {
            for (int j = i + 1; j < length; j++) {
                // 如果元素相同，保存到set中
                if (strs[i].equals(strs[j])) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 获取单行中所有数据并以字符串数组形式返回
     * 
     * @author zhuhaojie
     * @time 2016年9月22日上午9:12:02
     * @param row excel表格的行对象
     * @return 单行中的所有数据
     */
    private static String[] getCellValues(Row row) {
        if (row == null) {
            return null;
        }
        int number = row.getPhysicalNumberOfCells(); // 获取有效单元格
        if (number == 0) {
            return null;
        }
        // 获取格子中的列名
        String[] excelFieldNames = new String[number];
        int j = 0;
        // 获取Excel中的列名
        for (int i = 0; i < number; i++) {
            Cell cell = row.getCell(i);
            if (cell != null) {
                String value = cell.getStringCellValue();
                if (value != null) {
                    value = value.trim();
                    if (!value.equals("")) {
                        excelFieldNames[j] = value;
                        j++;
                    }
                }
            }
        }
        if (j > 0) {
            String[] result = new String[j];
            System.arraycopy(excelFieldNames, 0, result, 0, j);
            return result;
        }
        return null;
    }

    /**
     * 将输入流对象解析成Workbook对象返回
     * 
     * @author zhuhaojie
     * @time 2016年9月21日下午7:31:20
     * @param inputStream excel文件输入流对象
     * @throws IOException 当解析excel失败时抛出此异常
     * @return Workbook 代表单个excel文件
     */
    public static Workbook readExcel(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            Workbook wb = null;

            try {
                wb = new HSSFWorkbook(inputStream);
                // 先用2003的读取
            } catch (Exception e) {
                try {
                    String path = ExcelUtil.PATH; // 获取当前对象设置好的路径
                    inputStream = getIn(path);
                    if (inputStream != null) {
                        wb = new XSSFWorkbook(inputStream); // 再用2007的读取
                    }

                } catch (IOException e1) {
                    e1.printStackTrace();
                    throw e1;
                } finally {
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                            inputStream = null;
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }

            return wb;
        }
        return null;
    }

    /**
     * 将输入流对象解析成Workbook对象返回
     * 
     * @author zhuhaojie
     * @time 2016年9月21日下午7:31:20
     * @param inputStream excel文件输入流对象
     * @throws IOException 当解析excel失败时抛出此异常
     * @return Workbook 代表单个excel文件
     * @throws EncryptedDocumentException 异常
     * @throws InvalidFormatException 如果不是一个excel文件或者格式有问题
     */
    public static Workbook readExcelNew(InputStream inputStream) throws EncryptedDocumentException, InvalidFormatException, IOException {
        if (inputStream != null) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            return workbook;

        }
        return null;
    }

    /**
     * 通过excel输入流检验excel版本
     * 
     * @author zhuhaojie
     * @time 2016年9月23日下午3:12:20
     * @param in excel文件转换后的输入流对象
     * @throws IOException 检验时可能抛出的IOException
     * @return String excel版本 2003或者2007
     */
    public static String checkExcelVersion(InputStream in) throws IOException {

        String version = "";
        if (!in.markSupported()) {
            in = new PushbackInputStream(in, 8);
        }

        if (POIFSFileSystem.hasPOIFSHeader(in)) {
            System.out.println("2003及以下");
            version = "2003";
        }
        if (POIXMLDocument.hasOOXMLHeader(in)) {
            System.out.println("2007及以上");
            version = "2007";
        }
        return version;
    }

    /**
     * 文件后缀检查 看是否是Excel后缀
     * 
     * @author zhuhaojie
     * @time 2016年7月20日下午3:13:25
     * @param filePath 文件路径
     * @return boolean
     */
    public static boolean validateExcel(String filePath) {
        if (filePath != null) {
            filePath = filePath.trim();
            if (!"".equals(filePath)) {
                int index = filePath.lastIndexOf(".");
                int length = filePath.length();
                if (index != -1 && index < (length - 1)) {
                    // 截取
                    String s = filePath.substring(index + 1);
                    if ("xls".equalsIgnoreCase(s) || "xlsx".equalsIgnoreCase(s)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @author zhuhaojie
     * @time 2016年7月20日下午3:27:57
     * @param filePath 指定的文件路径
     * @throws FileNotFoundException 文件未找到时抛出此异常
     * @return FileInputStream
     */
    public static FileInputStream getIn(String filePath) throws FileNotFoundException {
        if (filePath != null) {
            filePath = filePath.trim();
            if (!filePath.equals("")) {
                if (validateExcel(filePath)) {
                    File file = new File(filePath);
                    if (file != null) {
                        if (file.exists()) {
                            if (file.canRead()) {
                                FileInputStream in = new FileInputStream(file);
                                return in;
                            }
                        }
                    }
                } else {
                    throw new RuntimeException("不是合法的excel文件");
                }
            }
        }

        return null;
    }

    /**
     * 根据路径获取输出流对象
     * 
     * @author zhuhaojie
     * @time 2016年7月20日下午1:10:58
     * @param filePath 指定的文件绝对路径
     * @return FileOutputStream 返回文件对应的输出流对象
     * @throws IOException 当创建流对象时可能抛出此异常
     */
    public static FileOutputStream getFile(String filePath) throws IOException {
        if (filePath != null) {
            filePath = filePath.trim();
            if (!filePath.equals("")) {
                File file = new File(filePath);
                if (!file.exists()) {
                    File pfile = file.getParentFile();
                    if (pfile != null) {
                        boolean t = pfile.exists();
                        if (!t) {
                            boolean tt = pfile.mkdirs();
                            if (!tt) {
                                throw new RuntimeException("创建父目录失败!");
                            }
                        }
                        boolean tt = file.exists();
                        if (!tt) {
                            boolean m = file.createNewFile();
                            if (!m) {
                                throw new RuntimeException("创建子目录失败!");
                            }
                        }
                    }
                }
                System.out.println("file:" + file.getPath());
                if (!file.canRead()) {
                    throw new RuntimeException("文件不可读!");
                }
                if (!file.canWrite()) {
                    throw new RuntimeException("文件不可写!");
                }
                FileOutputStream stream = new FileOutputStream(file);
                return stream;
            }
        }
        return null;
    }

    /**
     * @author zhuhaojie
     * @time 2016年11月30日下午2:53:32
     * @param headers 标题
     * @param data 数据
     * @param title 文件和excel工作表名
     * @throws IOException 写出时可能抛出的异常
     * @return HSSFWorkbook 包含指定excel数据的对象
     */
    public static HSSFWorkbook exportExcelNew(String[] headers, List<String[]> data, String title) throws IOException {
        if (headers == null || headers.length == 0) {
            throw new NullPointerException("表头不能为null或者无元素");
        }
        if (data == null) {
            throw new NullPointerException("数据不能为null");
        }
        int size = data.size();
        if (size == 0) {
            throw new RuntimeException("集合不能没有元素");
        }
        if (title == null || title.trim().equals("")) {
            throw new RuntimeException("title不能为null或者空字符串");
        }

        // 工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();

        // 表格
        HSSFSheet sheet = workbook.createSheet(title);

        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        // 产生表格标题行
        HSSFRow row = sheet.createRow(0);
        int headersLength = headers.length;
        // 向标题行写出数据
        for (int i = 0; i < headersLength; i++) {
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            // HSSFRichTextString text = new HSSFRichTextString(headers[i]);
            // cell.setCellValue(text);
            cell.setCellValue(headers[i]);
        }
        // 写入其他行
        for (int i = 1; i <= size; i++) {
            row = sheet.createRow(i);
            String[] str = data.get(i - 1);
            int length = str.length;
            for (int j = 0; j < length; j++) {
                HSSFCell cell = row.createCell(j); // 创建一个表格
                cell.setCellStyle(style2); // 设置表格样式
                cell.setCellValue(str[j]);
            }
        }

        return workbook;

    }

    /*
     * 
     */

    /**
     * 重载一下导出方法 添加搜索条件 统计页面导出公共方法
     * 
     * @param out 输出流对象
     * @param checkParams 查询出此数据所使用的搜索条件集合
     * @param fieldMap 数据标题和实体类字段对应关系
     * @param title 工作表名称
     * @param dataStyle 时间格式
     * @param dataset 要导出的数据集合
     * @param <T> 代表任意对象
     * @throws NullPointerException 导出时可能出现的异常
     * @throws IllegalAccessException 导出时可能出现的异常
     * @throws IllegalArgumentException 导出时可能出现的异常
     * @throws IOException 导出时可能出现的异常
     * @throws NoSuchMethodException 导出时可能出现的异常
     * @author：zhuhaojie
     * @time：2017年4月14日 上午10:39:06
     */
    public static <T> void exportExcelNew(OutputStream out, String title, LinkedHashMap<String, String> fieldMap, List<T> dataset, Map<String, String> dataStyle,
                                          Map<String, String> checkParams) throws NullPointerException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
                                                                           IOException {
        if (out == null) {
            throw new NullPointerException("指定的输出流为");
        }
        HSSFWorkbook book = exportExcel(title, fieldMap, dataset, dataStyle, checkParams);
        if (book != null) {
            try {
                book.write(out);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }

    }

    /**
     * 将数据以Excel形式导出到外部文件
     * 
     * @author zhuhaojie
     * @time 2016年7月20日下午1:01:05
     * @param title 导出excel工作表名称
     * @param fieldMap 导出时excel标题名称与实体类属性名对应关系 其中key为excel标题 value为实体类属性名
     * @param dataset 准备导出的数据集合对象
     * @param dataStyle 格式化特殊数据时的映射 其中键已经指定暂时只有：DATE_STYLE(本类的静态成员)，代表格式化日期时的键 value:为合法的日期格式如："yyyy-MM-dd"
     * @param checkParams 导出数据时的搜索条件
     * @param <T> 符合外部调用者指定的任何可能对象
     * @throws NullPointerException 抛出此异常的情况： 1.参数列表中任意一个参数为null 2.headers或者dataset中每没有数据 3、title为空字符串
     * @throws IllegalAccessException 通过Class构造该类对象时可能抛出非法状态异常
     * @throws NoSuchMethodException 指定的方法在对应bean中没有找到
     * @throws IllegalArgumentException 通过Class构造该类对象时可能抛出非法参数异常
     * @return HSSFWorkbook excel工作表对象 String[] headers, String[] include,
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> HSSFWorkbook exportExcel(String title, LinkedHashMap<String, String> fieldMap, List<T> dataset, Map<String, String> dataStyle,
                                               Map<String, String> checkParams) throws NullPointerException, NoSuchMethodException, IllegalAccessException,
                                                                                IllegalArgumentException {
        // 工作薄
        @SuppressWarnings("resource")
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 表格
        HSSFSheet sheet = workbook.createSheet(title);
        // 设置自适应列宽
        // sheet.autoSizeColumn(1, true);
        HSSFCellStyle style = workbook.createCellStyle();
        // 设置这些样式
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        // 生成一个字体
        HSSFFont font = workbook.createFont();
        font.setColor(HSSFColor.BLACK.index);
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        // 把字体应用到当前的样式
        style.setFont(font);
        // 生成并设置另一个样式
        HSSFCellStyle style2 = workbook.createCellStyle();
        style2.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        style2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        style2.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        style2.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        style2.setBorderRight(HSSFCellStyle.BORDER_THIN);
        style2.setBorderTop(HSSFCellStyle.BORDER_THIN);
        style2.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        // 生成另一个字体
        HSSFFont font2 = workbook.createFont();
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
        // 把字体应用到当前的样式
        style2.setFont(font2);
        int number = 0;
        // 判断是否有搜索条件
        if (checkParams != null && checkParams.size() > 0) {
            // 打印搜索条件
            Set<Entry<String, String>> set = checkParams.entrySet();
            int i = 0;
            HSSFRow firstRow = null;
            for (Entry<String, String> one : set) {
                int begin = 0;
                int end = 2;
                if (i > 0) {
                    begin = i * 2 + 1;
                    end = i * 2 + 2;
                }
                String key = one.getKey();
                String value = one.getValue();
                // 创建一行
                HSSFRow row = sheet.createRow(begin);
                if (i == 0) {
                    firstRow = row;
                }
                // 单元格合并
                // 四个参数分别是：起始行，结束行，开始列，结束列
                sheet.addMergedRegion(new CellRangeAddress(begin, end, 0, 1)); // 合并第一个单元格
                sheet.addMergedRegion(new CellRangeAddress(begin, end, 2, 6)); // 合并第二个单元格
                HSSFCell first = row.createCell(0);
                first.setCellValue(key);
                HSSFCell second = row.createCell(2);
                second.setCellValue(value);
                i++;
                number = end;
                first.setCellStyle(style);
                second.setCellStyle(style2);
            }
            // 合并第三个单元格
            sheet.addMergedRegion(new CellRangeAddress(0, number, 7, 8)); // 合并第三个单元格
            HSSFCell first = firstRow.createCell(7);
            first.setCellStyle(style);
            String str = "筛选条件";
            StringBuilder sb = new StringBuilder();
            for (String s : str.split("")) {
                sb.append(s);
                sb.append("\r\n");
            }
            first.setCellValue(sb.toString());
        }

        // 产生表格标题行
        HSSFRow row = sheet.createRow(number + 1);
        Iterator<Map.Entry<String, String>> iter = fieldMap.entrySet().iterator();
        int i = 0;
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            HSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(entry.getKey());
            cell.setCellValue(text);

            // sheet.setColumnWidth(i, entry.getKey().length()*2*256);
            // 设置自适应列宽
            sheet.autoSizeColumn(i, true);
            i++;
        }

        Iterator<T> it = dataset.iterator();
        int index = number + 2;
        while (it.hasNext()) {

            row = sheet.createRow(index);
            index++;
            // 获取单个数据
            T t = (T) it.next();
            // 遍历属性列
            int j = 0;
            for (Entry<String, String> entry : fieldMap.entrySet()) {

                HSSFCell cell = row.createCell(j); // 创建一个表格
                j++;
                cell.setCellStyle(style2); // 设置表格样式

                String fieldName = entry.getValue();
                if (fieldName == null) {
                    throw new NullPointerException("成员变量名不能为null");
                }
                fieldName = fieldName.trim();
                if (fieldName.equals("")) {
                    throw new NullPointerException("成员变量名不能为空字符串");
                }
                String getMethodName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                try {

                    Class tCls = t.getClass();

                    Method getMethod = tCls.getMethod(getMethodName, new Class[] {});
                    Object value = null;
                    try {
                        value = getMethod.invoke(t, new Object[] {});
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    String textValue = null;

                    if (value instanceof Character) {
                        char charValue = (Character) value;
                        cell.setCellValue(charValue);
                    } else if (value instanceof Integer) {
                        int intValue = (Integer) value;
                        cell.setCellValue(intValue);
                    } else if (value instanceof Float) {
                        float fValue = (Float) value;

                        cell.setCellValue(fValue);
                    } else if (value instanceof Double) {
                        double dValue = (Double) value;

                        cell.setCellValue(dValue);
                    } else if (value instanceof Long) {
                        long longValue = (Long) value;
                        cell.setCellValue(longValue);
                    } else if (value instanceof Boolean) {
                        boolean booleanValue = (Boolean) value;
                        cell.setCellValue(booleanValue);
                    } else if (value instanceof Date) {
                        Date date = (Date) value;
                        if (dataStyle == null) {
                            throw new NullPointerException("dataStyle对象为null");
                        }
                        if (!dataStyle.containsKey(ExcelUtil.DATE_STYLE)) {
                            throw new RuntimeException("日期转换格式未指定");
                        }
                        String dateStyle = dataStyle.get(ExcelUtil.DATE_STYLE);
                        SimpleDateFormat sdf = new SimpleDateFormat(dateStyle);
                        cell.setCellValue(sdf.format(date)); //

                    } else if (value instanceof byte[]) { // 是否是字节型数组
                        row.setHeightInPoints(60);
                        // // 设置图片所在列宽度为80px, 注意这里单位的一个换算
                        sheet.setColumnWidth(j, (short) (35.7 * 80));
                        sheet.autoSizeColumn(j);
                        byte[] bsValue = (byte[]) value;
                        HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 1023, 255, (short) 6, index, (short) 6, index);
                        anchor.setAnchorType(2);
                        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
                        patriarch.createPicture(anchor, workbook.addPicture(bsValue, HSSFWorkbook.PICTURE_TYPE_JPEG));

                    } else if (value instanceof Collection) {

                    } else if (value instanceof Class) {

                    } else {
                        // 其它数据类型都当作字符串简单处理
                        if (value == null) {
                            textValue = "";
                        } else {
                            textValue = value.toString();
                        }

                    }
                    if (textValue != null) {
                        Pattern p = Pattern.compile("^//d+ (//.//d+)?$");
                        Matcher matcher = p.matcher(textValue);
                        if (matcher.matches()) {
                            // 是数字当作double处理
                            cell.setCellValue(Double.parseDouble(textValue));
                        } else {
                            HSSFRichTextString richString = new HSSFRichTextString(textValue);
                            HSSFFont font3 = workbook.createFont();
                            font3.setColor(HSSFColor.BLUE.index);
                            richString.applyFont(font3);
                            cell.setCellValue(richString);
                        }
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    throw e;
                }

            }
        }
        return workbook;
    }

    public static void exportExcelSecond(ServletOutputStream outputStream, String title, LinkedHashMap<String, String> fieldMap, List<FullScaleCompanyMessage> companyList,
                                         Map<String, String> dataStyle) throws NullPointerException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException,
                                                                        IOException {
        if (outputStream == null) {
            throw new NullPointerException("指定的输出流为");
        }
        HSSFWorkbook book = getWorkbook(title, fieldMap, companyList, dataStyle);
        if (book != null) {
            try {
                book.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
}
