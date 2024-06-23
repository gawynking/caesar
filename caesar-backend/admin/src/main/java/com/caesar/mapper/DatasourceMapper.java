package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarDatasource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DatasourceMapper extends BaseMapper<CaesarDatasource> {

    @Select("select * \n" +
            "from caesar_datasource \n" +
            "where exec_engine = #{execEngine}")
    List<CaesarDatasource> getDatasourceInfo(int execEngine);

}
