package com.definesys.mdm.connector.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright: Shanghai Definesys Company.All rights reserved.
 * @Description:
 * @author: zhaozhanyang
 * @since: 2021/9/22 11:45
 * @history: 1.2021/9/22 created by zhaozhanyang
 */
public class AESCoderUtils {
    /**
     * 加密并转换成16进制字符串
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    public static String encrypt2HexString(String content, String password) {
        byte[] bytes = encrypt(content, password);
        if (bytes != null) {
            return bytes2HexString(bytes);
        }
        return null;
    }

    /**
     * 解密经encrypt2HexString方法加密的字符串
     *
     * @param content  待解密内容，内容必须是encrypt2HexString方法加密的串
     * @param password 解密密钥
     * @return
     */
    public static String decryptHexString2String(String content, String password) {
        if (content == null) {
            return null;
        }
        byte[] bytes = hexString2Bytes(content);

        bytes = decrypt(bytes, password);
        try {
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    private static byte[] encrypt(String content, String password) {
        byte[] bytes;
        try {
            bytes = content.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return encrypt(bytes, password);
    }

    /**
     * 加密
     *
     * @param content  需要加密的内容
     * @param password 加密密码
     * @return
     */
    private static byte[] encrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(password.getBytes());
            kgen.init(128, sr);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
// Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解密
     *
     * @param content  待解密内容
     * @param password 解密密钥
     * @return
     */
    private static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(password.getBytes());
            kgen.init(128, sr);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
// Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字节数组转换成16进制字符串
     *
     * @param bytes
     * @return
     */
    private static String bytes2HexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexString
     * @return
     */
    private static byte[] hexString2Bytes(String hexString) {
        if (hexString == null) {
            return null;
        }
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = (byte) (Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16));
        }
        return result;
    }

    public void ABC(List<String> list){
        list.add("321");
    }

    public static void main(String[] args) throws Exception {
        String content = "17386355797";
        String result = encrypt2HexString(content, "123456");
        System.out.println(result);

        String content1 = decryptHexString2String(result, "123456");
        System.out.println(content1);
        System.out.println(content.equals(content1));
    }
}