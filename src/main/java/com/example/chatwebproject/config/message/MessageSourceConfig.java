package com.example.chatwebproject.config.message;

import com.example.chatwebproject.constant.Constants;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Locale;

@Configuration
public class MessageSourceConfig {
    @Bean
    MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasenames(Constants.MESSAGE_SOURCE_BASE_NAMES);
        messageSource.setDefaultEncoding(Constants.DEFAULT_ENCODING);
        messageSource.setDefaultLocale(Locale.forLanguageTag(Constants.VI_LANG));
        return messageSource;
    }

    @Bean
    LocalValidatorFactoryBean getValidator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        validatorFactoryBean.setValidationMessageSource(messageSource());
        return validatorFactoryBean;
    }

//    @Bean
//    LocaleResolver localeResolver() {
//        return new CustomLocaleResolver();
//    }
}

