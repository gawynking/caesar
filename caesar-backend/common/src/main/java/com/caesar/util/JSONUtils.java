package com.caesar.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * json工具类
 *
 */
public class JSONUtils {

    private static final Logger logger = Logger.getLogger(JSONUtils.class);


    public final static ObjectMapper JSON_MAPPER =
            new ObjectMapper()
                    .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                    .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                    .configure(JsonParser.Feature.ALLOW_COMMENTS, true);


    /**
     * 获得JSONObject实例化对象
     *
     * @return
     */
    public static JSONObject getJSONObject(){
        return new JSONObject();
    }

    /**
     * 获得JSONArray实例化对象
     *
     * @return
     */
    public static JSONArray getJSONArray(){
        return new JSONArray();
    }

    /**
     * json字符串转JSON对象
     *
     * 以修复版本，可用
     *
     * @param jsonStr
     * @return
     */
    public static JSONObject getJSONObjectFromString(String jsonStr){
        try {
            return JSONObject.parseObject(jsonStr);
        }catch (Exception e){
            logger.info("String conversion json object failed : " + jsonStr );
            return null;
        }
    }

    /**
     * javabean 转换为 JSONObject
     *
     * @param javaBean
     * @param <T>
     * @return
     */
    public static <T> JSONObject getJSONObjectFromJavabean(Class<?> javaBean){
        try{
            return JSONObject.parseObject(JSON.toJSONString(javaBean));
        }catch (Exception e){
            logger.info("JavaBean conversion json object failed : " + JSON.toJSONString(javaBean));
            return null;
        }
    }


    /**
     * 字符串转json数组
     *
     * @param jsonStr
     * @return
     */
    public static JSONArray getJSONArrayfromString(String jsonStr){
        try{
            return JSONArray.parseArray(JSON.toJSONString(jsonStr));
        }catch (Exception e){
            logger.info("String conversion json array failed : " + jsonStr );
            return null;
        }
    }


    /**
     * map to JSONObject
     *
     * @param map
     * @return
     */
    public static JSONObject getJSONObjectFromMap(Map map){
        try {
            return JSONObject.parseObject(JSON.toJSONString(map));
        }catch (Exception e) {
            logger.info("Map conversion JSON object failed : " + map.toString() );
            return null;
        }
    }

    /**
     * JSONObject to Map
     *
     * @param jsonObject
     * @return
     */
    public static Map getMapFromJSONObject(JSONObject jsonObject){
        try {
            return JSONObject.toJavaObject(jsonObject, Map.class);
        }catch (Exception e){
            logger.info("JSON object conversion Map failed : " + jsonObject);
            return null;
        }
    }

    /**
     * List to JSONArray
     *
     * @param list
     * @return
     */
    public static JSONArray getJSONArrayFromList(List list){
        try {
            return JSONArray.parseArray(JSON.toJSONString(list));
        }catch (Exception e){
            logger.info("List conversion JSON object failed : " + list);
            return null;
        }
    }

    /**
     *
     * @param jsonArray
     * @return
     */
    public static List getListFromJSONArray(JSONArray jsonArray){
        try {
            return JSONArray.parseObject(jsonArray.toJSONString(), List.class);
        }catch (Exception e){
            logger.info("JSONArray conversion list object failed : " + jsonArray);
            return null;
        }
    }


    /**
     * 获取json字段值：模拟get_json_object(json,'$.key1.key2')函数
     * @param jsonObject
     * @param fieldStr
     * @return
     */
    public static Object getJSONItem(JSONObject jsonObject,String fieldStr){
        try {
            String[] fields = fieldStr.split("\\.");
            Object obj = null;
            int arraySize = fields.length;
            if("$".equals(fields[0])) {
                for (int i = 1; i < arraySize; i++) {
                    if (i == arraySize-1) {
                        obj = jsonObject.get(fields[i]);
                    }else {
                        jsonObject = jsonObject.getJSONObject(fields[i]);
                    }
                }
            }

            return obj;

        }catch (Exception e){
            return null;
        }
    }

}
