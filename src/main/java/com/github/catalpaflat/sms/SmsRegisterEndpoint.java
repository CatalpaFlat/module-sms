package com.github.catalpaflat.sms;

import com.github.catalpaflat.sms.cache.SmsCache;
import com.github.catalpaflat.sms.config.SmsConfig;
import com.github.catalpaflat.sms.exception.SmsException;
import com.github.catalpaflat.sms.model.to.SmsSignNameTO;
import com.github.catalpaflat.sms.model.to.SmsTO;
import com.github.catalpaflat.sms.model.to.SmsTemplateTO;
import com.github.catalpaflat.sms.model.to.SmsVariableTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CatalpaFlat
 */
public class SmsRegisterEndpoint {

    @Getter
    @Setter
    private SmsConfig smsConfig;
    @Getter
    @Setter
    private List<SmsTO> sms;
    @Getter
    @Setter
    private SmsCache smsCache;
    @Getter
    @Setter
    private Integer exist;
    @Getter
    @Setter
    private Map<String, List<SmsSignNameTO>> signNames;
    @Getter
    @Setter
    private Map<String, List<String>> templates;
    @Getter
    @Setter
    private Map<String, Map<String, List<SmsVariableTO>>> variables;
    @Getter
    @Setter
    private Map<String, Map<String, Integer>> codeSizes;

    public SmsRegisterEndpoint(SmsConfig smsConfig) {
        this.smsConfig = smsConfig;

        if (templates == null) {
            templates = new HashMap<String, List<String>>();
        }
        if (signNames == null) {
            signNames = new HashMap<String, List<SmsSignNameTO>>();
        }
        if (variables == null) {
            variables = new HashMap<String, Map<String, List<SmsVariableTO>>>();
        }
        if (codeSizes == null) {
            codeSizes = new HashMap<String, Map<String, Integer>>();
        }
        sms = smsConfig.getSms();

        initTemplates();
        initSignName();
        initVariables();
        initSMSCodeSize();
    }

    private void initSMSCodeSize() {
        for (SmsTO smsTO : sms) {
            String genre = smsTO.getGenre();
            List<SmsTemplateTO> templates = smsTO.getTemplates();
            Map<String, Integer> map = new HashMap<String, Integer>();
            for (SmsTemplateTO smsTemplateTO : templates) {
                Integer size = smsTemplateTO.getSize();
                String template = smsTemplateTO.getTemplate();
                map.put(template, size);
            }
            codeSizes.put(genre, map);
        }

    }

    private void initVariables() {
        for (SmsTO smsTO : sms) {
            String genre = smsTO.getGenre();
            List<SmsTemplateTO> templates = smsTO.getTemplates();
            Map<String, List<SmsVariableTO>> map = new HashMap<String, List<SmsVariableTO>>();
            for (SmsTemplateTO smsTemplateTO : templates) {
                List<SmsVariableTO> variables = smsTemplateTO.getVariables();
                String template = smsTemplateTO.getTemplate();
                map.put(template, variables);
            }
            variables.put(genre, map);
        }
    }

    private void initSignName() {
        for (SmsTO smsTO : sms) {
            String genre = smsTO.getGenre();
            signNames.put(genre, smsTO.getSign_names());
        }

    }


    private void initTemplates() {
        if (smsConfig == null) {
            throw new SmsException("smsConfig is empty");
        }
        for (SmsTO smsTO : sms) {
            String genre = smsTO.getGenre();
            List<SmsTemplateTO> template = smsTO.getTemplates();
            List<String> list = new ArrayList<String>();
            for (SmsTemplateTO smsTemplateTO : template) {
                list.add(smsTemplateTO.getTemplate());
            }
            templates.put(genre, list);
        }
    }

    public SmsRegisterEndpoint setSmsCacheMold(SmsCache smsCache) {
        this.smsCache = smsCache;
        return this;
    }

    public SmsRegisterEndpoint setCacheExist(int exist) {
        this.exist = exist;
        return this;
    }

}
