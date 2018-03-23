package com.github.catalpaflat.sms.model.bo;

import lombok.Getter;
import lombok.Setter;
import net.sf.json.JSONArray;

/**
 * @author CatalpaFlat
 */
@Getter
@Setter
public class SmsSendDYBatchDY extends SmsSendDY {
    private JSONArray signNames;
    private JSONArray phones;
    private JSONArray variables;

    public SmsSendDYBatchDY(String genre, String template) {
        super(genre, template);
    }

    public SmsSendDYBatchDY setSignNames(JSONArray signNames) {
        this.signNames = signNames;
        return this;
    }

    public SmsSendDYBatchDY setPhones(JSONArray phones) {
        this.phones = phones;
        return this;
    }

    public SmsSendDYBatchDY setVariables(JSONArray variables) {
        this.variables = variables;
        return this;
    }

}
