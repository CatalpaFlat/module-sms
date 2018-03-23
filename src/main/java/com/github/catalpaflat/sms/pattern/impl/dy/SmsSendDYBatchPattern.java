package com.github.catalpaflat.sms.pattern.impl.dy;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendBatchSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendBatchSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.github.catalpaflat.sms.SmsRegisterEndpoint;
import com.github.catalpaflat.sms.constant.SmsConstant;
import com.github.catalpaflat.sms.exception.SmsException;
import com.github.catalpaflat.sms.model.bo.SmsSendDY;
import com.github.catalpaflat.sms.model.bo.SmsSendDYBatchDY;
import com.github.catalpaflat.sms.model.to.SmsAppIdSecretBO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * @author CatalpaFlat
 */
public class SmsSendDYBatchPattern extends AbstractSmsSendDYPattern {
    public SmsSendDYBatchPattern(SmsRegisterEndpoint smsRegisterEndpoint) {
        super(smsRegisterEndpoint);
    }

    /**
     * {
     * "genre":"",
     * "template":"",
     * "phone":[
     * "18475525887",
     * "18475525886"
     * ],
     * "variable":[
     * {“code”:”1234”,”product”:”ytx1”},
     * {“code”:”5678”,”product”:”ytx2”}
     * ]
     * "sign_name":[
     * “云通信”,
     * ”云通信”
     * ]
     * }
     *
     * @param body 信息
     * @return 结果
     */
    public JSONObject send(SmsSendDY body) {
        SmsSendDYBatchDY info = (SmsSendDYBatchDY) body;

        detectionJsonInfo(info);

        String genre = info.getGenre();

        // 获取app_id和secret
        SmsAppIdSecretBO appIdWithSecret = getAppIdWithSecret(genre);
        String appId = appIdWithSecret.getAppId();
        String secret = appIdWithSecret.getSecret();

        String template = info.getTemplate();
        JSONArray signNames = info.getSignNames();
        JSONArray phones = info.getPhones();
        JSONArray variables = info.getVariables();

        //初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile(SmsConstant.SMS_DY_REGION_ID, appId, secret);

        try {
            DefaultProfile.addEndpoint(SmsConstant.SMS_DY_ENDPOINT, SmsConstant.SMS_DY_REGION_ID, SmsConstant.SMS_DY_PRODUCT, SmsConstant.SMS_DY_DOMAIN);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendBatchSmsRequest request = new SendBatchSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持JSON格式的批量调用，批量上限为100个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumberJson(phones.toString());
            //必填:短信签名-支持不同的号码发送不同的短信签名
            request.setSignNameJson(signNames.toString());
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(template);
            //必填:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParamJson(variables.toString());
            //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCodeJson("[\"90997\",\"90998\"]");


            SendBatchSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            return JSONObject.fromObject(sendSmsResponse);
        } catch (ClientException e) {
            //请求失败这里会抛ClientException异常
            throw new SmsException(e.getMessage());
        }
    }

    private void detectionJsonInfo(SmsSendDYBatchDY info) {
        String genre = info.getGenre();
        if (StringUtils.isBlank(genre)) {
            throw new SmsException("genre is blank");
        }
        String template = info.getTemplate();
        if (StringUtils.isBlank(template)) {
            throw new SmsException("template is blank");
        }
        JSONArray variables = info.getVariables();
        JSONArray signNames = info.getSignNames();
        JSONArray phones = info.getPhones();
        if (variables.size() < 1) {
            throw new SmsException("variables is error");
        }
        if (signNames.size() < 1) {
            throw new SmsException("signNames is error");
        }
        if (phones.size() < 1) {
            throw new SmsException("phones is error");
        }
        if (variables.size() != signNames.size() && variables.size() != phones.size()) {
            throw new SmsException("The phone and variable do not correspond to the size of the sign Name.");
        }
    }

    public boolean verify(JSONObject info) {
        return false;
    }
}
