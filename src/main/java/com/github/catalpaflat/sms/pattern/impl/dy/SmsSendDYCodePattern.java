package com.github.catalpaflat.sms.pattern.impl.dy;


import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.github.catalpaflat.sms.SmsRegisterEndpoint;
import com.github.catalpaflat.sms.cache.memory.Group;
import com.github.catalpaflat.sms.cache.memory.SmsCacheMemory;
import com.github.catalpaflat.sms.cache.redis.SmsCacheRedis;
import com.github.catalpaflat.sms.constant.SmsConstant;
import com.github.catalpaflat.sms.exception.SmsException;
import com.github.catalpaflat.sms.model.bo.SmsSendCodeDY;
import com.github.catalpaflat.sms.model.bo.SmsSendDY;
import com.github.catalpaflat.sms.model.to.SmsAppIdSecretBO;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

import static com.github.catalpaflat.sms.constant.SmsConstant.*;

/**
 * 阿里大鱼短信接口
 * 短信验证码/推广通知                           ·
 * <p>
 * https://help.aliyun.com/document_detail/55284.html?spm=a2c4g.11186623.6.556.xBlJnJ
 * <p>
 * #短信服务配置
 * #产品
 * sms.product=Dysmsapi
 * #产品域名
 * sms.domain=dysmsapi.aliyuncs.com
 * #产品区域ip
 * sms.region_id=cn-hangzhou
 * #产品端点
 * sms.endpoint=cn-hangzhou
 * #APP_KEY
 * sms.app_key=xxxx
 * #SECRET
 * sms.secret=xxxxx
 * #签名
 * sms.free_sign_name=阿里云短信测试专用
 * #短信模板
 * #用户注册验证码
 * sms.template=SMS_112785012
 * #短信长度
 * sms.size=4
 *
 * @author CatalpaFlat
 */
public class SmsSendDYCodePattern extends AbstractSmsSendDYPattern {


    public SmsSendDYCodePattern(SmsRegisterEndpoint smsRegisterEndpoint) {
        super(smsRegisterEndpoint);
    }

    /**
     * 发送验证码
     * {
     * "sign":"",
     * "template":"",
     * "phone":""
     * }
     *
     * @param body 必要信息
     * @return 发送结果
     */
    public JSONObject send(SmsSendDY body) {
        SmsSendCodeDY info = (SmsSendCodeDY) body;

        detectionJsonInfo(info);
        String genre = info.getGenre();
        String template = info.getTemplate();
        String phone = info.getPhone();
        String signName = info.getSignName();
        JSONObject variablesValue = info.getVariables();

        String appKey;
        String secret;

        // 获取app_id和secret
        SmsAppIdSecretBO appIdWithSecret = getAppIdWithSecret(genre);
        appKey = appIdWithSecret.getAppId();
        secret = appIdWithSecret.getSecret();

        Boolean isCode = info.getCode();
        Boolean generalize = info.getGeneralize();

        String regionId = SmsConstant.SMS_DY_REGION_ID;

        IClientProfile profile = DefaultProfile.getProfile(regionId, appKey, secret);

        try {
            DefaultProfile.addEndpoint(SmsConstant.SMS_DY_ENDPOINT, regionId, SmsConstant.SMS_DY_PRODUCT, SmsConstant.SMS_DY_DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(phone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName(signName);
            String code = null;
            if (isCode) {
                if (variablesValue == null) {
                    variablesValue = new JSONObject();
                }
                //获取验证码大小
                Integer size = DEFAULT_CODE_SIZE;
                for (Map.Entry<String, Map<String, Integer>> mapEntry : codeSizes.entrySet()) {
                    String key = mapEntry.getKey();
                    if (genre.equals(key)) {
                        Map<String, Integer> value = mapEntry.getValue();
                        for (Map.Entry<String, Integer> m : value.entrySet()) {
                            String key1 = m.getKey();
                            if (template.equals(key1)) {
                                size = m.getValue();
                                break;
                            }
                        }
                        break;
                    }
                }
                //获取验证码
                code = getRandomCode(size);
                variablesValue.put(SMS_CODE, code);
                request.setTemplateParam(variablesValue.toString());
            } else if (!generalize) {
                request.setTemplateParam(variablesValue.toString());
            }


            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(template);
            //响应
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            //校验短信响应
            detectionSmsResponse(sendSmsResponse);

            //存储
            return storageCode(sendSmsResponse, code, phone, isCode);
        } catch (ClientException e) {
            throw new SmsException(e.getMessage());
        }
    }

    /**
     * 校验验证码
     *
     * @param info 校验信息
     * @return 响应
     */
    public boolean verify(JSONObject info) {

        String requestId = info.getString(SMS_TEMP_CODE);
        String phone = info.getString(SmsConstant.SMS_PHONE);
        if (StringUtils.isBlank(phone)) {
            throw new SmsException("phone is empty");
        }
        String code = info.getString(SMS_CODE);
        if (StringUtils.isBlank(code)) {
            throw new SmsException("code is empty");
        }

        String key = requestId + code + phone;

        if (smsCache instanceof SmsCacheMemory) {
            Object group = smsCache.get(key);
            if (group instanceof Group) {
                Group g = (Group) group;
                boolean exist = g.exist(key);
                if (exist) {
                    return false;
                }
                Object value = g.getValue(key);
                if (value == null) {
                    return false;
                }
            }
            smsCache.del(key);
        }

        if (smsCache instanceof SmsCacheRedis) {
            SmsCacheRedis smsCacheRedis = (SmsCacheRedis) smsCache;
            Object o = smsCacheRedis.get(key);
            if (o == null) {
                return false;
            }
            smsCacheRedis.del(key);
        }
        return true;
    }

    /**
     * 存储验证码
     *
     * @param sendSmsResponse 短信响应
     * @param code            验证码
     * @param phone           手机号
     * @param isCode          是否验证码
     * @return 响应
     */
    private JSONObject storageCode(SendSmsResponse sendSmsResponse, String code, String phone, Boolean isCode) {
        JSONObject resultJson = new JSONObject();
        if (isCode) {
            String requestId = sendSmsResponse.getRequestId();
            resultJson.put(SMS_TEMP_CODE, requestId);
            resultJson.put(SMS_CODE, code);
            if (smsCache == null) {
                smsCache = SmsCacheMemory.instance();
            }
            String key = requestId + code + phone;
            if (smsCache instanceof SmsCacheMemory) {
                Object group = smsCache.get(key);
                if (group instanceof Group) {
                    Group g = (Group) group;
                    g.push(key, code, exist);
                }
            }
            if (smsCache instanceof SmsCacheRedis) {
                SmsCacheRedis smsCacheRedis = (SmsCacheRedis) smsCache;
                smsCacheRedis.set(key, code, exist);
            }
        } else {
            resultJson.put(SMS_RESULT, sendSmsResponse);
        }
        return resultJson;
    }

    /**
     * 校验SMS响应
     *
     * @param sendSmsResponse SMS 响应
     */
    private void detectionSmsResponse(SendSmsResponse sendSmsResponse) {
        if (sendSmsResponse == null) {
            throw new SmsException("send sms response is null");
        }
        if (!RESPONSE_OK.equals(sendSmsResponse.getCode())) {
            throw new SmsException(new Gson().toJson(sendSmsResponse));
        }
    }

    /**
     * 校验发送验证码请求
     *
     * @param info 请求信息
     */
    private void detectionJsonInfo(SmsSendCodeDY info) {
        String genre = info.getGenre();
        if (StringUtils.isBlank(genre)) {
            throw new SmsException("genre is blank");
        }
        String signName = info.getSignName();
        if (StringUtils.isBlank(signName)) {
            throw new SmsException("signName is blank");
        }
        String template = info.getTemplate();
        if (StringUtils.isBlank(template)) {
            throw new SmsException("template is blank");
        }
        String phone = info.getPhone();
        if (StringUtils.isBlank(phone)) {
            throw new SmsException("phone is blank");
        }

        Boolean isCode = info.getCode();
        Boolean generalize = info.getGeneralize();
        if (!isCode && !generalize) {
            JSONObject variables = info.getVariables();
            if (variables == null) {
                throw new SmsException("variables is error");
            }
        }
    }


}
