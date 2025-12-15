package ru.diasoft.spring.util;

import org.springframework.context.MessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Locale;

public class LocalizationUtil {

    public static String getLocalizedFileName(String baseName, String pattern, Locale locale) {
        if (pattern != null && pattern.contains("{locale}")) {
            return pattern.replace("{locale}", locale.getLanguage());
        } else {
            return baseName + "_" + locale.getLanguage() + ".csv";
        }
    }

    public static boolean resourceExists(String fileName) {
        Resource resource = new ClassPathResource(fileName);
        return resource.exists();
    }

    public static Locale getSafeLocale(Locale requestedLocale, String defaultLocale) {
        if (requestedLocale != null && !requestedLocale.getLanguage().isEmpty()) {
            return requestedLocale;
        }
        return Locale.forLanguageTag(defaultLocale);
    }
}