package com.github.catalpaflat.sms.model.to;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 *
 * @author CatalpaFlat
 */
@Getter
@Setter
public class SmsTO {
    private String app_key;
    private String secret;
    private List<SmsSignNameTO> sign_names;
    private String genre;
    private List<SmsTemplateTO> templates;
}
