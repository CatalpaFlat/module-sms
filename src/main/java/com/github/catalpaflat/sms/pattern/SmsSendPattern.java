package com.github.catalpaflat.sms.pattern;

import com.github.catalpaflat.sms.model.bo.SmsSendDY;
import net.sf.json.JSONObject;

/**
 * @author CatalpaFlat
 */
public interface SmsSendPattern {
    JSONObject send(SmsSendDY info);

    boolean verify(JSONObject info);
}
