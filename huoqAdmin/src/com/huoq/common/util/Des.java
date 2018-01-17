package com.huoq.common.util;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class Des {

    private final static String iv        = "85031429";

    private final static String encoding  = "utf-8";

    public final static String  secretKey = "fjgI0uoF5nUPbesKwNYRrWsD";// 密钥

    /**
     * DES加密
     * 
     * @param secretKey 密匙
     * @param plainText 普通文本
     * @return
     * @throws Exception
     */
    public static String encode(String secretKey, String plainText) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] encryptData = cipher.doFinal(plainText.getBytes(encoding));
        return Base64s.encode(encryptData);
    }

    /**
     * DES解密
     * 
     * @param secretKey 密匙
     * @param encryptText 加密文本
     * @return
     * @throws Exception
     */
    public static String decode(String secretKey, String encryptText) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(secretKey.getBytes());
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
        byte[] decryptData = cipher.doFinal(Base64s.decode(encryptText));
        return new String(decryptData, encoding);
    }

    public static void main(String[] args) throws Exception {
        try {
            // 密钥
            String secretKey = "fjgI0uoF5nUPbesKwNYRrWsD"; // 24个字符
            String content = "{\r\n" + 
                    "    \"source\":\"caiwei\",\r\n" + 
                    "\"params\":[{\r\n" + 
                    "        \"payMentTransaction\":\"XH0000000000005\",\r\n" + 
                    "        \"repayAmount\":\"100\",\r\n" + 
                    "        \"stages\":1\r\n" + 
                    "  },{\r\n" + 
                    "        \"payMentTransaction\":\"XH0000000000008\",\r\n" + 
                    "        \"repayAmount\":\"100\",\r\n" + 
                    "        \"stages\":1\r\n" + 
                    "  }],\r\n" + 
                    "\"sign\":\"ksrtZb848Qe8lD1WOlNKeQ==\"\r\n" + 
                    "}\r\n" + 
                    "";
            System.out.println("加密前：" + content);

            String encrypt = encode(secretKey, content);
            System.out.println("加密后：" + encrypt);

            String decrypt = decode(secretKey, encrypt);
            System.out.println("解密后：" + decrypt);
            // System.out.println("qqqq====="+SHAUtil.encode("11111111"));
        } catch (Exception e) {
            System.out.println("dd=====" + e);
        }
    }
}
