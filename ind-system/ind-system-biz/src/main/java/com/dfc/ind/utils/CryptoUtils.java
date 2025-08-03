package com.dfc.ind.utils;



/*
* ljl
* 解决渗透安全
* */
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

       //加密
       public static String encrypt(String src) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, UnsupportedEncodingException {
           byte[] key = iv.getBytes();
           SecretKey secretKey = new SecretKeySpec(key, Algorithm);
           IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes("utf-8"));
           Cipher cipher = Cipher.getInstance(AlgorithmProvider);
           cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
           byte[] cipherBytes = cipher.doFinal(src.getBytes("UTF-8"));
           return byteToHexString(cipherBytes);
       }


       //将byte数组转换为16进制字符串
         private static String byteToHexString(byte[] src){
           return Hex.encodeHexString(src);
         }


}
