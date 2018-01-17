package com.huoq.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.util.CharArrayBuffer;

public class IoUtils {

	 public static String convertStream2String(InputStream inputStream) {
	        String str = "";
	        // ByteArrayOutputStream相当于内存输出流
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        byte[] buffer = new byte[1024];
	        int len = 0;
	        // 将输入流转移到内存输出流中
	        try{
	            while ((len = inputStream.read(buffer, 0, buffer.length)) != -1){
	                out.write(buffer, 0, len);
	            }
	            // 将内存流转换为字符串
	            str = new String(out.toByteArray(),"utf-8");
	        }catch (IOException e){
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        return str;
	    }
	 
	 /**
	 * 读取request流
	 * @throws IOException 
	 */
	public static String getInputStream(HttpServletRequest request) throws IOException {
		InputStream instream = request.getInputStream();
		if (instream == null) {
			return null;
		}
		try {
			int i = (int) request.getContentLength();
			if (i < 0) {
				i = 4096;
			}
			Reader reader = new InputStreamReader(instream, Charset.forName("UTF-8"));
			CharArrayBuffer buffer = new CharArrayBuffer(i);
			char[] tmp = new char[1024];
			int l;
			while ((l = reader.read(tmp)) != -1) {
				buffer.append(tmp, 0, l);
			}
			return buffer.toString();
		}
		finally {
			instream.close();
		}
	}
}
