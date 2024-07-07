package com.caesar.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.caesar.entity.CaesarDatasource;
import com.caesar.mapper.DatasourceMapper;
import com.caesar.service.DatasourceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class DatasourceServiceImpl extends ServiceImpl<DatasourceMapper, CaesarDatasource> implements DatasourceService {

    @Resource
    DatasourceMapper datasourceMapper;

    @Override
    public boolean addDatasource(CaesarDatasource datasource) {
        return datasourceMapper.addDatasource(datasource);
    }

    @Override
    public boolean deleteDatasource(int id) {
        return datasourceMapper.deleteDatasource(id);
    }
}
