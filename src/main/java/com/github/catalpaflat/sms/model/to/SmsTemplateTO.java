package com.github.catalpaflat.sms.model.to;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author CatalpaFlat
 */
@Getter
@Setter
public class SmsTemplateTO {
    private String template;
    private String remark;
    private String genre;
    private Integer size;
    private List<SmsVariableTO> variables;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
