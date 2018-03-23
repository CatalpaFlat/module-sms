package com.github.catalpaflat.sms.model.to;

import lombok.Getter;
import lombok.Setter;

/**
 * @author CatalpaFlat
 */
@Getter
@Setter
public class SmsSendTO {
    private String mobile;
    private long expiresTime;
}
