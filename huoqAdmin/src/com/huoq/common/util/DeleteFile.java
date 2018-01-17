package com.huoq.common.util;

import java.io.*;
import java.text.ParseException;
import java.util.Date;

public class DeleteFile {

	/*
	 * 删除文件 
	 * deleteDate 时间  Date (2016/11/11 15:18:00)
	 * deleteUrl 路径
	 */
	public static String deleteFile(Date deleteDate, String deleteUrl) {
		File delfile = new File(deleteUrl);
		File[] f5 = delfile.listFiles();
		File f = new File(deleteUrl);
		if (f.isDirectory()) {
			if(f.length()==0){
			f.deleteOnExit();
			}
		}
		// 第一层循环文件夹内容
		for (int i = 0; i < f5.length; i++) {
			Date lastdate = new Date(f5[i].lastModified());
			// 判断是否为文件
			if (!f5[i].isFile()) {
				if (f5[i].length() == 0) {
					f5[i].deleteOnExit();
				}
				String url = f5[i].getPath();
				deleteFile(deleteDate, url);

			} else {
				// 是文件类型，对比时间时间不符合的删除
				if (deleteDate.after(lastdate)) {
					f5[i].delete();
				}
			}

		}
		return "";
	}

	public static String deleteFile(String deleteUrl) {
		File delfiles = new File(deleteUrl);
		File[] f6 = delfiles.listFiles();
		if(!QwyUtil.isNullAndEmpty(f6))
		for (int i = 0; i < f6.length; i++) {
			// 判断是否为文件
			File f= f6[i];
			if (f.isDirectory() && f.exists()) {
				f.delete();
				String url = f.getPath();
				deleteFile(url);
			} 
		}
		else{
			String lastParent = deleteUrl.substring(0, deleteUrl.lastIndexOf("\\"));
			deleteFile(lastParent);
		}
		return "";
	}
 
}
