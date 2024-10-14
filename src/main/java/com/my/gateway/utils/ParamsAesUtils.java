package com.my.gateway.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.my.gateway.enums.BizExceptionEnum;
import com.my.gateway.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.List;

/**
 * @Description:
 * @Author: 温陆城
 * @Date: 2024-10-14 14:41:46
 */
@Slf4j
public class ParamsAesUtils {

    private static final List<String> RANDOM_LIST_AES_KEY = Lists.newArrayList("1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";
    private static final String KEY_ALGORITHM = "AES";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 功能描述: 加密
     *
     * @param content
     * @param key
     * @param ivPara
     * @return {@link String }
     * @author 温陆城
     * @date 2024-10-14 14:42:05
     */
    public static String encrypt(String content, String key, String ivPara) {
        String encryptStr = "";
        try {
            encryptStr = Base64.encodeBase64String(getCipher(key, ivPara, 1).doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.info("AES加密失败：" ,e);
            BizException.error(BizExceptionEnum.SYS_ERROR);
        }
        return encryptStr;
    }

    /**
     * 功能描述: 解密
     *
     * @param content
     * @param key
     * @param ivPara
     * @return {@link String }
     * @author 温陆城
     * @date 2024-10-14 14:42:45
     */
    public static String decrypt(String content, String key, String ivPara) {
        String decryptStr = "";
        try {
            decryptStr = new String(getCipher(key, ivPara, 2).doFinal(Base64.decodeBase64(content)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.info("AES解密失败：" ,e);
            BizException.error(BizExceptionEnum.SYS_ERROR);
        }
        return decryptStr;
    }

    /**
     * 功能描述:
     *
     * @param key
     * @param ivParameter
     * @param encryptMode
     * @return {@link Cipher }
     * @author 温陆城
     * @date 2024-10-14 14:44:49
     */
    private static Cipher getCipher(String key, String ivParameter, int encryptMode) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());
            cipher.init(encryptMode, new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM), iv);
        } catch (Exception e) {
            log.info("AES.getCipher获取密码失败：" ,e);
            BizException.error(BizExceptionEnum.SYS_ERROR);
        }
        return cipher;
    }

    public static String getAesKey() {
        return StrUtil.join("", new Object[]{RandomUtil.randomEleSet(RANDOM_LIST_AES_KEY, 16)});
    }

    public static String getLengthKey(Integer length) {
        return StrUtil.join("", new Object[]{RandomUtil.randomEleSet(RANDOM_LIST_AES_KEY, length)});
    }

}
