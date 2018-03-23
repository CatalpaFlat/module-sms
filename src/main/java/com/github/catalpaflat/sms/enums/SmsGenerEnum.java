package com.github.catalpaflat.sms.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author CatalpaFlat
 */
public enum SmsGenerEnum {
    VERIFICATION_CODE("code"), SMS_BATCH_NOTIFICATION("sms_batch"), SMS_NOTIFICATION("sms"),GENERALIZE_NOTIFICATION("generalize");
    @Getter
    @Setter
    private String gener;

    SmsGenerEnum(String gener) {
        this.gener = gener;
    }
}
