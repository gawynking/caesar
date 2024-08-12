package com.caesar;

import com.caesar.util.HttpUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpTest {

    public static String token = "86c184558e3e773655504a39e01a1eb6";


    @Test
    public void test01() throws IOException {

        String url = "http://localhost:12345/dolphinscheduler/projects/14576322429504/task-definition/gen-task-codes?genNum=1";

        String s = HttpUtils.doGet(url, null, token);
        System.out.println(s);

    }


    @Test
    public void test02() throws IOException {

        String url = "http://localhost:12345/dolphinscheduler/projects/14576322429504/task-definition/gen-task-codes";
        Map<String,String> params = new HashMap<>();
        params.put("genNum","2");

        String s = HttpUtils.doGet(url, params, token);
        System.out.println(s);

    }


    @Test
    public void test03() throws IOException {

        // 14580526742336
        String url = "http://localhost:12345/dolphinscheduler/projects/14576322429504/process-definition";
        Map<String,String> params = new HashMap<>();
        params.put("projectCode","14576322429504");
        params.put("name","post-httpclient-001");
        params.put("locations","[{\"taskCode\":14580526742336,\"x\":232,\"y\":50}]");
        params.put("taskRelationJson","[{\"name\":\"http-001\",\"preTaskCode\":0,\"preTaskVersion\":0,\"postTaskCode\":14580526742336,\"postTaskVersion\":0,\"conditionType\":\"NONE\",\"conditionParams\":{}}]");
        params.put("taskDefinitionJson","[{\"code\":14580526742336,\"delayTime\":\"0\",\"description\":\"\",\"environmentCode\":-1,\"failRetryInterval\":\"1\",\"failRetryTimes\":\"0\",\"flag\":\"YES\",\"isCache\":\"NO\",\"name\":\"http-001\",\"taskParams\":{\"localParams\":[],\"rawScript\":\"echo 555\",\"resourceList\":[]},\"taskPriority\":\"MEDIUM\",\"taskType\":\"SHELL\",\"timeout\":0,\"timeoutFlag\":\"CLOSE\",\"timeoutNotifyStrategy\":\"\",\"workerGroup\":\"default\",\"cpuQuota\":-1,\"memoryMax\":-1,\"taskExecuteType\":\"BATCH\"}]");


        String s = HttpUtils.doPostForm(url, params, token);
        System.out.println(s);

    }


}
