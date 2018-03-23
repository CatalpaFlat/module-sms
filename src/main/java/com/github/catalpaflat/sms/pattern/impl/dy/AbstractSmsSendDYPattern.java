package com.github.catalpaflat.sms.pattern.impl.dy;

import com.github.catalpaflat.sms.SmsRegisterEndpoint;
import com.github.catalpaflat.sms.cache.SmsCache;
import com.github.catalpaflat.sms.cache.memory.SmsCacheMemory;
import com.github.catalpaflat.sms.exception.SmsException;
import com.github.catalpaflat.sms.model.to.SmsAppIdSecretBO;
import com.github.catalpaflat.sms.model.to.SmsTO;
import com.github.catalpaflat.sms.model.to.SmsVariableTO;
import com.github.catalpaflat.sms.pattern.SmsSendPattern;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * @author CatalpaFlat
 */
public abstract class AbstractSmsSendDYPattern implements SmsSendPattern {

    protected List<SmsTO> sms;
    protected SmsRegisterEndpoint smsRegisterEndpoint;
    protected Map<String, List<String>> templates;
    protected Set<String> signs;
    protected SmsCache smsCache;
    protected int exist;
    private static final int DEFAULT_CACHE_EXIST = 3000;
    protected Map<String, Map<String, List<SmsVariableTO>>> variables;
    protected Map<String, Map<String, Integer>> codeSizes;

    public AbstractSmsSendDYPattern(SmsRegisterEndpoint smsRegisterEndpoint) {
        this.smsRegisterEndpoint = smsRegisterEndpoint;
        this.sms = smsRegisterEndpoint.getSms();
        this.templates = smsRegisterEndpoint.getTemplates();
        this.variables = smsRegisterEndpoint.getVariables();
        this.codeSizes = smsRegisterEndpoint.getCodeSizes();


        SmsCache smsCache = smsRegisterEndpoint.getSmsCache();
        if (smsCache == null) {
            this.smsCache = SmsCacheMemory.instance();
        } else {
            this.smsCache = smsCache;
        }

        Integer exist = smsRegisterEndpoint.getExist();
        if (exist == null) {
            this.exist = DEFAULT_CACHE_EXIST;
        } else {
            this.exist = exist;
        }
    }

    protected SmsAppIdSecretBO getAppIdWithSecret(String genre) {
        String appId = null;
        String secret = null;
        // 获取app_id和secret
        for (SmsTO smsTO : sms) {
            String genre1 = smsTO.getGenre();
            if (genre.equals(genre1)) {
                appId = smsTO.getApp_key();
                secret = smsTO.getSecret();
                break;
            }
        }
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(secret)) {
            throw new SmsException("secret or app_key is empty");
        }
        return new SmsAppIdSecretBO(appId, secret);
    }


    /**
     * 获取随机数
     */
    protected static String getRandomCode(Integer size) {
        StringBuilder charValue = new StringBuilder();
        for (int i = 0; i < size; i++) {
            char c = (char) (randomInt() + '0');
            charValue.append(String.valueOf(c));
        }
        return charValue.toString();
    }

    /**
     * 获取随机整数
     */
    protected static int randomInt() {
        //获取随机数
        Random r = new Random();
        return 1 + r.nextInt(10 - 1);
    }
}
