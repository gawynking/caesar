package com.caesar.model.code;

import com.caesar.model.code.model.TaskContentModel;

public class TemplateUtils {


    public static String transformSqlTemplate(TaskContentParser taskContentParser){
        return taskContentParser.generateExecuteScript();
    }

}
