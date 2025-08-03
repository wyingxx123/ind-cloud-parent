package com.dfc.ind.common.core.utils.secret;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
@Slf4j
public class RSAUtils {
	
	/**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";
    
    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "SHA1withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";
    
    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    
    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 245;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 256;

    public static final String PARAM_EQUAL = "=";

    public static final String PARAM_AND = "&";


    /**
     * 生成密钥对(公钥和私钥)
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 用私钥对信息生成数字签名
     * @param body body传参内容（进行指定排序处理）
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static String signByMapSort(JSONObject body, String privateKey) throws Exception {
        String param = getMapStr(body);
        log.info("加签参数: " + param);
        // 参数加签
        String sign = getSign(param, privateKey);
        // 加签sign值
        log.info("sign值: " + sign);
        // 根据sign 解签结果
//        log.info(verifySign(body, PUBLIC_KEY, sign));
        return sign;
    }

    /**
     * 获取map字符串。以key=value&...形式返回
     *
     * @param map
     * @return
     */
    private static String getMapStr(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder();
        map = sortMapByKey(map);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object obj = entry.getValue();
            String key = entry.getKey();
            if (null != obj && !"".equals(obj) && !key.equals("sign")) {
                sb.append(key);
                sb.append(PARAM_EQUAL);
                if (obj instanceof Map) {
                    sb.append(PARAM_AND);
                    sb.append(getMapStr((Map<String, Object>) obj));
                } else {
                    sb.append(obj);
                }
                sb.append(PARAM_AND);
            }
        }

        String params = sb.toString();
        if (sb.toString().endsWith(PARAM_AND)) {
            params = sb.substring(0, sb.length() - 1);
        }
        return params;
    }

    /**
     * 对Object进行List<NameValuePair>转换后按key进行升序排序
     *
     * @param
     * @return
     */
    public static Map<String, Object> sortMapByKey(Map<String, Object> order) {
        if (order == null) {
            return null;
        }

        Map<String, Object> parameters = new TreeMap<String, Object>(new Comparator<String>() {
            public int compare(String obj1, String obj2) {
                return obj1.compareToIgnoreCase(obj2);
            }
        });
        parameters.putAll(order);

        return parameters;
    }

    /**
     * 用私钥对信息生成数字签名
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String getSign(String content, String privateKey) throws Exception {
        byte[] data = content.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.getEncoder().encodeToString(signature.sign());
    }

    private static String sortFieldString(String data) {
        if (Objects.isNull(data) || "".equals(data)) {
            return "";
        }

        if (data.startsWith("[")) {
            return data;
        }

        JSONObject jsonObject = JSONObject.parseObject(data);
        SortedMap<String, Object> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        for (String key : jsonObject.keySet()) {
            if ("extMap".equals(key)) {
                map.put(key, sortExtMapField(JSONObject.toJSONString(jsonObject.get(key))));
            } else {
                String value = jsonObject.getString(key);
                map.put(key, value);
            }
        }

        log.info("排序后字段内容: " + JSONObject.toJSONString(map));
        return JSONObject.toJSONString(map);
    }

    private static Object sortExtMapField(String data) {
        if (Objects.isNull(data) || "".equals(data)) {
            return "";
        }

        JSONObject jsonObject = JSONObject.parseObject(data);
        SortedMap<String, Object> map = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });

        for (String key : jsonObject.keySet()) {
            String value = jsonObject.getString(key);
            map.put(key, value);
        }

        log.info("extMap 排序后字段内容: " + JSONObject.toJSONString(map));
        return map;
    }

    /**
     * 校验数字签名
     * @param data 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign 数字签名
     * @return
     * @throws Exception
     * 
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.getDecoder().decode(sign));
    }

    /**
     * 私钥解密
     * @param encryptedData 已加密数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥解密
     * @param encryptedData 已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥加密
     * @param data 源数据
     * @param publicKey 公钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 私钥加密
     * @param data 源数据
     * @param privateKey 私钥(BASE64编码)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
            throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 获取私钥
     * @param keyMap 密钥对
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getPublicKey(Map<String, Object> keyMap)
            throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }



    /**
     * 私钥加密
     * @param data 加密数据
     * @return privateKey
     * @throws Exception
     */
    public static String encryptByPrivateKey(String data, String privateKey) throws Exception {
        byte[] encryptArr = encryptByPrivateKey(Base64.getEncoder().encode(data.getBytes("UTF-8")), privateKey);
        return Base64.getEncoder().encodeToString(encryptArr);
    }

    /**
     * 私钥解密
     * @param encryptedData 加密数据
     * @return privateKey
     * @throws Exception
     */
    public static String decryptByPrivateKey(String encryptedData, String privateKey)
            throws Exception {
        byte[] decryptArr = decryptByPrivateKey(Base64.getDecoder().decode(encryptedData), privateKey);
        return new String(Base64.getDecoder().decode(decryptArr), "UTF-8");
    }

    /**
     * 公钥加密
     * @param data 源数据
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    public static String encryptByPublicKey(String data, String publicKey)
            throws Exception {
        byte[] encryptArr = encryptByPublicKey(Base64.getEncoder().encode(data.getBytes("UTF-8")), publicKey);
        return Base64.getEncoder().encodeToString(encryptArr);
    }

    /**
     * 公钥解密
     * @param encryptedData 已加密数据
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    public static String decryptByPublicKey(String encryptedData, String publicKey)
            throws Exception {
        byte[] decryptArr = decryptByPublicKey(Base64.getDecoder().decode(encryptedData), publicKey);
        return new String(Base64.getDecoder().decode(decryptArr), "UTF-8");
    }
    
    public static void main(String[] args) throws Exception {
//        Map<String,Object> map = genKeyPair();
//        String privateKey = getPrivateKey(map);
//        String publicKey = getPublicKey(map);

//    	String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAK4QzvU8gHsX0ZJoxCJKitmjJyMU0TnP0XURjaWOFSw3k1WWfS/eKE433BFLhxdG8rfrE7ql1bUETWh1RjK9PFoio/3wpLzdS7ZoboP4ITGTy4zSDeZwcCtXE/fsKTiy1wPnBOuEHPCdt2lu4yoi0kS17UvzfO5g0ardwk2LdA9/AgMBAAECgYBRRJu7t8GsttQr7SoVcIQfVKNDJ8b/nN2IMOfXMd0ExfXN8fME1E4xJrdig8bQwVk1MVYGwMJkP1v8tzRNIDj6fqKe9Ewa1SKKdT63fEMzSn0Pa43ppNjJWr3oIzciLxIxPKjjtKPh8nBAAuupq49jgfP22S6JumlIQ342yw/kMQJBAOOGSEhhjUHHfhrvpwF3H/Ugai11tEuQB7S+NlH/nwZuVdEnkG735PhptzqvZ2R2tGbU1A7ajWUoA7TbO/OGP+UCQQDD2b470q4EDbPg69b/k8z/9/l9tlsM19rINU2qbuWscHENdq2l0pz8phOhjkx2spVvT65Q/IM2CT5e2KAqa/OTAkBwId7/5SwD7jilN9U78KTMX1RU4TyhPPO/TTtiQDP0rG4Y7YHOXtf24csO3iF7rtEMGPoF9ApZf1YMTTwHsfNNAkBKcmqtstgTEmJeDUgcvsIeStS7xKW3rBWuJRTwxFbpxZQz2fkIH5ctMrQjpUPLmvbS6ScKAfKeh8T9qLq5ZW+hAkARefA/jJt1tcZpBYUCNzxX8dbNYkH5nmIaO4kfKkLkiIObltqlSvTVXsHiazCCCXk1iY+x9kRz184tmPbUBKYf";
//		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCuEM71PIB7F9GSaMQiSorZoycjFNE5z9F1EY2ljhUsN5NVln0v3ihON9wRS4cXRvK36xO6pdW1BE1odUYyvTxaIqP98KS83Uu2aG6D+CExk8uM0g3mcHArVxP37Ck4stcD5wTrhBzwnbdpbuMqItJEte1L83zuYNGq3cJNi3QPfwIDAQAB";
		//body的值jsonarray
//		String s = "[{\"needPdf\":\"1\",\"nsrsbh\":\"913201023532898821\"}]";
//		log.info("明文：" + s);
		/*String sign = RSAUtils.sign(s.getBytes(), privateKey);
		boolean b = RSAUtils.verify(s.getBytes("UTF-8"), publicKey, sign);
		log.info(b);*/

        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKeA4ysPqkGbkbHKjM2E/n/ZMpTeqrcx9yIH/91o6x2+l6EzJnnT6eFETpJWN8Tek9rPaPKpeYG3gY4HxVhm+Syjc251wyAsiFWBxASk39qgFr+uE6XAyM0tqo/zftQbWRa/VSsJew7zfrc6jScWEqDYAZ4mjPc+QGcbolpuAfWtAgMBAAECgYBFz4/eOI8q/N2CDfsVBOLVAf700MCxzV9EjbTz4HBtWyvzAVB94fZN7pwYnVps8J8KyPrieAOuLn8OZOq452HdrbfDbX+Zly5pm9HFLouJyk7KpSLSkfqJzoZvgIofBUKltg1fmh7txTPBvo26CVXou4QJsWHvJJTvFQatyX8cgQJBAN/TOhcZ0ygrmWraa5ulXI6Act+EIAqwGdxYBUIVLwEWT6fcu9iq3xhWeKCVU9j3VaCFx1AowLPFgITfGo50m/ECQQC/lQWW2V8ZPn3fbucRgcuP1Dl2dttP43d+FUhwbWqy9eBycsbPNAYNt4Dhd2ENm31wnHZ+Z/UFvwq1+a1hM+F9AkAWKVryGJuAubhqDRBki937OhqlqPZnOIKG/6wdm+1YhTYD3+Y1kM2gIke1VrPDotG2oChY9oAGDMMp5NFDU6ZxAkBFopf2faoYVeOQrBHnBiOEcuI0Ef2jKw3K0VeULeEjjUV4tAlZVRKCN9nrmeW3+XV90hEr3wNrhEYTYN5JP39NAkEAyjM18FCWAYd1lXEEJuF5/uSqMBC2PLIOTGWzqGXpKT+ly7fIktEYLPjgWdq9zdI4mGSYa6afhsZGom5O35X95g==";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCngOMrD6pBm5GxyozNhP5/2TKU3qq3MfciB//daOsdvpehMyZ50+nhRE6SVjfE3pPaz2jyqXmBt4GOB8VYZvkso3NudcMgLIhVgcQEpN/aoBa/rhOlwMjNLaqP837UG1kWv1UrCXsO8363Oo0nFhKg2AGeJoz3PkBnG6JabgH1rQIDAQAB";

        /*byte[] jiami =  RSAUtils.encryptByPublicKey(Base64.getEncoder().encode(s.getBytes("UTF-8")), publicKey);
        byte[] jiemi = RSAUtils.decryptByPrivateKey(jiami, privateKey);
        log.info("公钥加密，私钥解密----解密后：" + new String(Base64.getDecoder().decode(jiemi), "UTF-8"));*/


        /*byte[] jiami1 =  RSAUtils.encryptByPrivateKey(Base64.getEncoder().encode(s.getBytes("UTF-8")), privateKey);
        byte[] jiemi1 = RSAUtils.decryptByPublicKey(jiami1, publicKey);
        log.info("私钥加密，公钥解密----解密后：" + new String(Base64.getDecoder().decode(jiemi1), "UTF-8"));*/


        String s = "abcgetEncode1245";
        log.info("明文：" + s);

        byte[] jiami1 =  RSAUtils.encryptByPrivateKey(Base64.getEncoder().encode(s.getBytes("UTF-8")), privateKey);
        String str = Base64.getEncoder().encodeToString(jiami1);
        log.info("加密后:"+str);
        // 模拟接到请求参数进行解密
        byte[] jiemi1 = RSAUtils.decryptByPublicKey(Base64.getDecoder().decode(str), publicKey);
        log.info("私钥加密，公钥解密----解密后：" + new String(Base64.getDecoder().decode(jiemi1), "UTF-8"));
    }

}
