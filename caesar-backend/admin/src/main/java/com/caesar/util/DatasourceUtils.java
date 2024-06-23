package com.caesar.util;

import com.alibaba.fastjson.JSONObject;
import com.caesar.entity.CaesarDatasource;
import com.caesar.mapper.DatasourceMapper;

import javax.annotation.Resource;
import java.util.List;

public class DatasourceUtils {



    public static String getDatasourceInfo(List<CaesarDatasource> datasources){
        JSONObject datasourceInfo = new JSONObject();
        for(CaesarDatasource datasource :datasources){
            if(1 == datasource.getDatasourceType()){
                datasourceInfo.put("test",datasource.getId());
            }else if(2 == datasource.getDatasourceType()){
                datasourceInfo.put("pre",datasource.getId());
            }else if(3 == datasource.getDatasourceType()){
                datasourceInfo.put("prd",datasource.getId());
            }
        }
        return datasourceInfo.toJSONString();
    }


}
