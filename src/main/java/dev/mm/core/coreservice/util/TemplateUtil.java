package dev.mm.core.coreservice.util;

import liqp.Template;

import java.util.Collections;
import java.util.Map;

public class TemplateUtil {

    public static String renderTemplate(String template, Map<String, Object> parameters) {
        Template liquidTemplate = Template.parse(template);
        return liquidTemplate.render(parameters != null ? parameters : Collections.emptyMap());
    }

}
