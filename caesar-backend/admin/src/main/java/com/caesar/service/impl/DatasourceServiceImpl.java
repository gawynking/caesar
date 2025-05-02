package com.caesar.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarDatasource;
import com.caesar.enums.DatasourceType;
import com.caesar.enums.EngineEnum;
import com.caesar.mapper.DatasourceMapper;
import com.caesar.service.DatasourceService;
import com.caesar.util.JSONUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DatasourceServiceImpl extends ServiceImpl<DatasourceMapper, CaesarDatasource> implements DatasourceService {

    @Resource
    DatasourceMapper datasourceMapper;

    @Override
    public boolean addDatasource(CaesarDatasource datasource) {
        if(
                EngineEnum.fromTag(datasource.getEngine()) == EngineEnum.DORIS
                        || EngineEnum.fromTag(datasource.getEngine()) == EngineEnum.MYSQL
                        || EngineEnum.fromTag(datasource.getEngine()) == EngineEnum.STARROCKS
        ){
            JSONObject datasourceInfo = JSONUtils.getJSONObjectFromString(datasource.getDatasourceInfo());
            datasourceInfo.put("driver","com.mysql.cj.jdbc.Driver");
            datasource.setDatasourceInfo(datasourceInfo.toJSONString());
        }
        return datasourceMapper.addDatasource(datasource);
    }

    @Override
    public boolean deleteDatasource(int id) {
        return datasourceMapper.deleteDatasource(id);
    }
}
