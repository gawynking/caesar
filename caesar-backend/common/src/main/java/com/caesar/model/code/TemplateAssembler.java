package com.caesar.model.code;

import com.caesar.model.code.model.dto.TaskTemplateDto;
import com.caesar.util.BeanUtils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TemplateAssembler {

    public static String taskTemplateAssembler(String template, TaskTemplateDto templateDto){

        Map<String, String> valuesMap = BeanUtils.toDBNamedMap(templateDto);

        // Regular expression to match placeholders
        Pattern pattern = Pattern.compile("\\{\\{\\s*(.*?)\\s*\\}\\}");
        Matcher matcher = pattern.matcher(template);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String key = matcher.group(1);
            String replacement = valuesMap.getOrDefault(key, matcher.group(0));
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);

        return result.toString();

    }

}
