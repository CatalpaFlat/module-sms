package com.github.catalpaflat.sms.model.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * @author CatalpaFlat
 */
@Getter
@Setter
public class SmsSendDY {
    private String genre;
    private String template;

    public SmsSendDY(String genre, String template) {
        this.genre = genre;
        this.template = template;
    }
}
