package com.github.catalpaflat.sms.model.bo;

import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONObject;

/**
 * @author CatalpaFlat
 */
@Getter
@Setter
public class SmsSendCodeDY extends SmsSendDY {
    private String phone;
    private String signName;
    private JSONObject variables;
    private Boolean code = false;
    private Boolean generalize = false;

    public SmsSendCodeDY(String genre, String phone, String signName, String template) {
        super(genre, template);
        this.phone = phone;
        this.signName = signName;
    }

    public SmsSendCodeDY isCode(Boolean isCode) {
        this.code = isCode;
        return this;
    }

    public SmsSendCodeDY isGeneralize(Boolean generalize) {
        this.generalize = generalize;
        return this;
    }

    public SmsSendCodeDY setVariables(JSONObject variables) {
        this.variables = variables;
        return this;
    }
}
