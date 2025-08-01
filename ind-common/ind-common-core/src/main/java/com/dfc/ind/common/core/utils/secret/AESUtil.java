package com.dfc.ind.common.core.utils.secret;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AESUtil {



    /**
     * 偏移量，AES 128位数据块对应偏移量为16位
     * AES 128位数据块对应偏移量为16位
     */
    public static final String VIPARA = "0123456789Agfsda";

    /**
     * AES：加密方式   CBC：工作模式   PKCS5Padding：填充模式
     */
    private static final String CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";

    private static final String AES = "AES";

    /**
     * 编码方式
     */
    public static final String CODE_TYPE = "UTF-8";


    /**
     * AES 加密操作
     *
     * @param contents 待加密内容
     * @param key     加密密钥
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String contents, String key) {

        if (contents == null || "".equals(contents)) {
            return contents;
        }
        try {
            /*
             * 新建一个密码编译器的实例，由三部分构成，用"/"分隔，分别代表如下
             * 1. 加密的类型(如AES，DES，RC2等)
             * 2. 模式(AES中包含ECB，CBC，CFB，CTR，CTS等)
             * 3. 补码方式(包含nopadding/PKCS5Padding等等)
             * 依据这三个参数可以创建很多种加密方式
             */
            Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);

            //偏移量
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes(CODE_TYPE));

            byte[] byteContent = contents.getBytes(CODE_TYPE);

            //使用加密秘钥
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(CODE_TYPE), AES);
            //SecretKeySpec skeySpec = getSecretKey(key);

            // 初始化为加密模式的密码器
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, zeroIv);

            // 加密
            byte[] result = cipher.doFinal(byteContent);

            //通过Base64转码返回
            return Base64.encodeBase64String(result);
        } catch (Exception ex) {
            Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }


    /**
     * AES 解密操作
     *
     * @param content
     * @param key
     * @return
     */
    public static String decrypt(String content, String key) {
        if (content == null || "".equals(content)) {
            return content;
        }

        try {
            //实例化
            Cipher cipher = Cipher.getInstance(CBC_PKCS5_PADDING);
            IvParameterSpec zeroIv = new IvParameterSpec(VIPARA.getBytes(CODE_TYPE));

            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(CODE_TYPE), AES);
            //SecretKeySpec skeySpec = getSecretKey(key);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, zeroIv);

            byte[] result = cipher.doFinal(Base64.decodeBase64(content));

            return new String(result, CODE_TYPE);
        } catch (Exception ex) {
            Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }


    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final String key) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = null;
        try {
            kg = KeyGenerator.getInstance(AES);

            //AES 要求密钥长度为 128
            kg.init(128, new SecureRandom(key.getBytes()));

            //生成一个密钥
            SecretKey secretKey = kg.generateKey();

            // 转换为AES专用密钥
            return new SecretKeySpec(secretKey.getEncoded(), AES);
        } catch (Exception ex) {
            Logger.getLogger(AESUtil.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static void main(String[] args) throws Exception{

        String key = new String("abcgetEncode1245");

        System.out.println("key = " + key);
        String encrypt = encrypt("{\"equipmentNo\":\"6F-403\",\"equipmentName\":\"淬火槽\",\"stationName\":\"\",\"repairTime\":\"2021/12/21 09:01:11\",\"repairReason\":\"刮板电机变速箱故障，不动作。\",\"repairPersonnel\":\"易国泰\",\"picture\":[null],\"omaintainTime\":\"2021/12/23 08:52:30\",\"maintainRemark\":\"检查发现是减速机齿轮损坏，坏减速机已发厂家调换，配件回后立即修复。\",\"maintainPersonnel\":\"王卫东\"}", key);
        System.out.println(encrypt);
    }
}
