package com.dfc.ind.auth.utils;



/*
* ljl
* 解决渗透安全 -前端登录时密码使用Jsencrypt加密后端解密工具类
* */


import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {


       private  static  String  iv ="0102030405060708";


       private static String Algorithm="AES";

       private static String AlgorithmProvider="AES/CBC/PKCS5Padding";

       public static  String decrypt(String enc ,String uniqueKey) throws UnsupportedEncodingException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, DecoderException, BadPaddingException, IllegalBlockSizeException {
           byte[] bytes = uniqueKey.getBytes();
           SecretKey secretKeySpec = new SecretKeySpec(bytes, Algorithm);
           IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("utf-8"));
           Cipher instance = Cipher.getInstance(AlgorithmProvider);
           instance.init(Cipher.DECRYPT_MODE,secretKeySpec,ivParameterSpec);
           byte[] hexBytes=hexStringToBytes(enc);
           byte[] plainBytes=instance.doFinal(hexBytes);
           return new String(plainBytes,"UTF-8");
       }

       private  static byte[] hexStringToBytes(String hexString) throws DecoderException {
           return Hex.decodeHex(hexString);
       }


}
