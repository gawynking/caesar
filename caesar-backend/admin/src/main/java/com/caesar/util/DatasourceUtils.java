package com.caesar.util;

import com.alibaba.fastjson.JSONObject;
import com.caesar.entity.CaesarDatasource;
import com.caesar.enums.DatasourceType;

import java.util.List;

public class DatasourceUtils {



    public static String getDatasourceInfo(List<CaesarDatasource> datasources){
        JSONObject datasourceInfo = new JSONObject();
        for(CaesarDatasource datasource :datasources){
            if(DatasourceType.TEST.getValue() == datasource.getDatasourceType()){
                datasourceInfo.put("test",datasource.getId());
            }else if(DatasourceType.PRE_PRODUCTION.getValue() == datasource.getDatasourceType()){
                datasourceInfo.put("pre",datasource.getId());
            }else if(DatasourceType.PRODUCTION.getValue() == datasource.getDatasourceType()){
                datasourceInfo.put("prd",datasource.getId());
            }
        }
        return datasourceInfo.toJSONString();
    }


}
