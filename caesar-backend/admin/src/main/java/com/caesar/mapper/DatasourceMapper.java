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
            "where engine = #{engine}")
    List<CaesarDatasource> getDatasourceInfo(int engine);

}
