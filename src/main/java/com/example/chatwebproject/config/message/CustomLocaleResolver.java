package com.example.chatwebproject.config.message;


import com.example.chatwebproject.constant.Constants;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CustomLocaleResolver extends AcceptHeaderLocaleResolver {
    private static final List<Locale> LOCALES = Arrays.asList(new Locale(Constants.EN_LANG), new Locale(Constants.VI_LANG));

    @NotNull
    public Locale resolveLocale(HttpServletRequest request) {
        String headerLang = request.getHeader(Constants.ACCEPT_LANGUAGE);
        return headerLang == null || headerLang.isEmpty()
                ? Locale.forLanguageTag(Constants.VI_LANG)
                : Locale.lookup(Locale.LanguageRange.parse(headerLang), LOCALES);
    }
}

