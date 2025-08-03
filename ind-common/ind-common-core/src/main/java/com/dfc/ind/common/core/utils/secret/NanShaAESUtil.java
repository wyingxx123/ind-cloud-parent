package com.dfc.ind.common.core.utils.secret;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;


public class NanShaAESUtil {

    public static String encrypt(String content,String keyStr) {
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), keyStr.getBytes()).getEncoded();
        AES aes = SecureUtil.aes(key);
        return aes.encryptHex(content);
    }

    public static String decrypt(String content,String keyStr) {
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), keyStr.getBytes()).getEncoded();
        AES aes = SecureUtil.aes(key);
        return aes.decryptStr(content);
    }
}
