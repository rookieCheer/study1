package com.huoq.common.util;

import java.io.*;

import org.apache.log4j.Logger;

import com.huoq.common.lianlian.pay.utils.security.Base64;

/**
 * Created by zf on 2017/6/15.
 */

/**
 * 序列化工具类
 */
public class ObjectTranscoder {
	
	private static Logger log = Logger.getLogger(ObjectTranscoder.class);
	
    public static byte[] serialize(Object value) {
        if (value == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv=null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(value);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        } catch (IOException e) {
            throw new IllegalArgumentException("Non-serializable object", e);
        } finally {
            try {
                if(os!=null)os.close();
                if(bos!=null)bos.close();
            }catch (Exception e2) {
            	log.error("操作异常",e2);
            }
        }
        return rv;
    }

    public static Object deserialize(byte[] in) {
        Object rv=null;
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if(in != null) {
                bis=new ByteArrayInputStream(in);
                is=new ObjectInputStream(bis);
                rv=is.readObject();
                is.close();
                bis.close();
            }
        } catch (Exception e) {
            log.error("操作异常: ",e);
        }finally {
            try {
                if(is!=null)is.close();
                if(bis!=null)bis.close();
            } catch (Exception e2) {
            	log.error("操作异常",e2);
            }
        }
        return rv;
    }
}
