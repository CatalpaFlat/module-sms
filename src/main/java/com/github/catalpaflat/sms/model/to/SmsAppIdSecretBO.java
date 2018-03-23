package com.github.catalpaflat.sms.model.to;

import lombok.Getter;
import lombok.Setter;

/**
 * @author CatalpaFlat
 */
@Setter
@Getter
public class SmsAppIdSecretBO {
    private String appId;
    private String secret;

    public SmsAppIdSecretBO(String appId, String secret) {
        this.appId = appId;
        this.secret = secret;
    }
}
