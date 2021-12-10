package ru.siv.notes.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

  @Override
  public void addViewControllers(ViewControllerRegistry registry) {
    // Указываем страницы для которых контроллер не требуется.
    registry.addViewController("/login").setViewName("login");
  }

  // Нужное для интернационализации.

  // Базовая конфигурация интернационализации.
  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setCacheSeconds(5); //refresh cache once per 5 sec
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setFallbackToSystemLocale(false);
    messageSource.setBasenames("classpath:locale/messages");
    return messageSource;
  }

  /*
   * Итак, сейчас мы имеем такое же поведение как при AcceptHeaderLocaleResolver, но теперь к конкретной сессии можно насильно
   * назначить какую-то локаль, отличную от передаваемого Accept-Header. Если на момент назначения локали сессия не поднята
   * (отсутствует кука JSESSIONID), то она будет автоматически поднята и кука появится.
   * При сбросе сессии, соответственно, назначенная локаль сбрасывается.
   */
  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver slr = new SessionLocaleResolver();
    //Назначает локаль по умончанию, которая используется когда к сесии не прикреплена никакая локаль.
    //Если не назначать локаль по умолчанию, то локаль будет назначена согласно Accept-Language хэдера запроса
    //slr.setDefaultLocale(Locale.forLanguageTag("ru"));
    return slr;
  }
  /*
   * Перехватчик реализует возможность назначения произвольной локали для сессии.
   */
  @Bean
  public LocaleChangeInterceptor localeChangeInterceptor() {
    LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
    localeChangeInterceptor.setParamName("lang");
    return localeChangeInterceptor;
  }

  // Перехватчик надо ещё зарезистрировать.
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(localeChangeInterceptor());
  }

}
