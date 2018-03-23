# sms
# 短信服务
## 手机短信模版封装：
### 1.SmsConfig 配置获取调用者配置与yml的相关参数属性值  
eg:  
```yml

catalpaflat:
  sms:
    - app_key: XXXX1
      secret: XXX11
      sign_names:
        - name: 阿里云短信测试专用
      genre: Dysmsapi-1
      templates:
        - template: SMS_112785012
          size: 4
          remark: 用户注册验证码
          genre: code
          variables:
            - variable: code

    - app_key: XXXX2
      secret: XXX22
      sign_names:
        - name: 阿里云短信测试专用1
        - name: 阿里云短信测试专用2
      genre: Dysmsapi-2
      templates:
        - template: SMS_126460444
          remark: 短信通知
          genre: sms
          variables:
            - variable: number
        - template: SMS_126460453
          remark: 推广通知
          genre: generalize
```
## 2. 注入配置
```java
@Configuration
public class SmsResourceConfig {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    public SmsConfig smsConfig() {
        return new SmsConfig();
    }

    @Bean
    public SmsCacheRedis smsCacheRedis() {
        return new SmsCacheRedis(redisTemplate);
    }

    @Bean
    public SmsRegisterEndpoint smsRegisterEndpoint(SmsCacheRedis smsCacheRedis) {
        return new SmsRegisterEndpoint(smsConfig())
                    .setSmsCacheMold(smsCacheRedis)
                    .setCacheExist(3000);
    }

    @Bean
    public SmsSendDYCodePattern smsSendDYPattern(SmsRegisterEndpoint smsRegisterEndpoint) {
        return new SmsSendDYCodePattern(smsRegisterEndpoint);
    }
    
    
    @Bean
    public SmsSendDYBatchPattern sendDYBatchPattern(SmsRegisterEndpoint smsRegisterEndpoint) {
        return new SmsSendDYBatchPattern(smsRegisterEndpoint);
    }
}
```
### 3. 调用
```java
public class TestController{
    @Resource
    private SmsSendDYCodePattern smsSendDYCodePattern;
    @Resource
    private SmsSendDYBatchPattern sendDYBatchPattern;
    /**
    * 短信通知
    */
    @GetMapping("sms/{phone}")
    public ResponseEntity<Object> sms(@PathVariable String phone) {
       JSONObject json = new JSONObject();
       json.put("number",4);
       JSONObject send = smsSendDYCodePattern.send(
                new SmsSendCodeDY("Dysmsapi-2",phone,"阿里云短信测试专用2","SMS_126460444").setVariables(json));
       return ResponseUtil.success(send);
    }
    /**
    * 短信验证码
    */
    @GetMapping("sms/{phone}")
    public ResponseEntity<Object> sms(@PathVariable String phone) {
        SmsSendCodeDY smsSendCodeDY = new SmsSendCodeDY("Dysmsapi-1",phone,"阿里云短信测试专用","SMS_112785012").isCode(true);
        JSONObject send = smsSendDYCodePattern.send(smsSendCodeDY);
        return ResponseUtil.success(send);
    }
    
    /**
    * 推广通知
    */
    @GetMapping("sms/{phone}")
    public ResponseEntity<Object> sms(@PathVariable String phone) {
        SmsSendCodeDY smsSendCodeDY = new SmsSendCodeDY("Dysmsapi-2",phone,"阿里云短信测试专用2","SMS_126460453").isGeneralize(true);
        JSONObject send = smsSendDYCodePattern.send(smsSendCodeDY);
        return ResponseUtil.success(send);
    }
    
   /**
    * 批量短信通知
    */
    @GetMapping("sms")
    public ResponseEntity<Object> sms(@PathVariable String phone) {
        JSONArray phones = new JSONArray();
        phones.add("15989554874");
        phones.add("18475525887");
        phones.add("15602956632");
        JSONArray signNames = new JSONArray();
        signNames.add("阿里云短信测试专用2");
        signNames.add("阿里云短信测试专用");
        signNames.add("阿里云短信测试专用2");
        JSONObject variables1 = new JSONObject();
        variables1.put("number",6);
        variables2.put("code",4231);
        JSONObject variables2 = new JSONObject();
        variables2.put("number",4);
        JSONObject variables3 = new JSONObject();
        variables3.put("number",5);
        variables2.put("code",4231222);

        JSONArray variables = new JSONArray();
        variables.add(variables1);
        variables.add(variables2);
        variables.add(variables3);

        SmsSendDY smsSendDYBatchDY = new SmsSendDYBatchDY("Dysmsapi-2", "SMS_126460444")
                .setPhones(phones)
                .setSignNames(signNames)
                .setVariables(variables);
        JSONObject send = sendDYBatchPattern.send(smsSendDYBatchDY);
        return ResponseUtil.success(send);
    }
    /**
    * 校验验证码
    */
    @GetMapping("sms/{code}/{temp}/{phone}")
    public ResponseEntity<Object> sms(@PathVariable String code, @PathVariable String temp, @PathVariable String phone) {
        JSONObject json = new JSONObject();
        json.put(SmsConstant.SMS_CODE, code);
        json.put(SmsConstant.SMS_TEMP_CODE, temp);
        json.put(SmsConstant.SMS_PHONE, phone);
        boolean verify = smsSendDYCodePattern.verify(json);
        json.clear();
        json.put("is_true", verify);
        return ResponseUtil.success(json);
    }
}
``` 
