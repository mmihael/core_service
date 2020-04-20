package dev.mm.core.coreservice.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.Map;

import static dev.mm.core.coreservice.constants.Common.DEFAULT_LANGUAGE;
import static dev.mm.core.coreservice.util.SecurityUtil.getUserLangIfPresentOrDefault;

@Service
public class TranslationUtil {

    private static final Map<String, Map<String, String>> translations;

    private static final Map<String, String> defaultTranslations;

    static {
        try {
            translations = new ObjectMapper().readValue(
                new ClassPathResource("translations/translations.json").getInputStream().readAllBytes(),
                new TypeReference<Map<String, Map<String, String>>>(){}
            );
            defaultTranslations = translations.get(DEFAULT_LANGUAGE);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public static String translate(String key) {
        String lang = getUserLangIfPresentOrDefault();
        Map<String, String> langTranslations = translations.getOrDefault(lang, defaultTranslations);
        return langTranslations.getOrDefault(key, key);
    }

    public static String translate(String key, Map<String, Object> parameters) {
        String translation = translate(key);
        return TemplateUtil.renderTemplate(translation, parameters);
    }

}
