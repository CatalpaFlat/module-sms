package com.github.catalpaflat.sms.supoort;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author CatalpaFlat
 */
public final class ValidateSupport {
    private ValidateSupport() {
    }

    /**
     * 校验手机号码
     */
    public static boolean isMobile(String mobiles) {
        Pattern p = compile("^[1][3,4,5,6,7,8,9][0-9]{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
}
