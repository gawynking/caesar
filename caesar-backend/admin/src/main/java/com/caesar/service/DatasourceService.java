package com.caesar.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarDatasource;

public interface DatasourceService extends IService<CaesarDatasource> {


    boolean addDatasource(CaesarDatasource datasource);

    boolean deleteDatasource(int id);
}