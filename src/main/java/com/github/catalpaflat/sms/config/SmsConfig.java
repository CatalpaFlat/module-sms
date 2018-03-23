package com.github.catalpaflat.sms.config;

import com.github.catalpaflat.sms.model.to.SmsTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author CatalpaFlat
 */
@ConfigurationProperties(prefix = "catalpaflat")
public class SmsConfig {
    @Getter
    @Setter
    private List<SmsTO> sms;
    /**
     *
     * catalpaflat:
     *  sms:
     *     - app_key:
     *       secret:
     *       genre:
     *       sign_names:
     *          - name:
     *          - name:
     *       templates:
     *          - template:
     *            variables:
     *              - variable:
     *              - variable:
     *            genre:
     *            msg:
     *            size:
     * 阿里短信服务：
     *  1. 短信验证码发送
     *     只有唯一变量：code
     *     而且 template.genre：code
     *  2. 推广通知：
     *     template.genre:generalize
     *     无变量
     *  3. 短信发送
     *     多变量
     *     template.genre：sms
     *  4. 批量发送
     *     多变量
     *     多签名
     *     template.genre：sms_batch
     *
     *
     */

}
