package com.caesar.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.caesar.entity.CaesarDatasource;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DatasourceMapper extends BaseMapper<CaesarDatasource> {

    @Select("select * from caesar_datasource where engine = #{engine}")
    List<CaesarDatasource> getDatasourceInfoFromEngine(int engine);

    @Insert("insert into caesar_datasource(\n" +
            "\tdatasource_name,\n" +
            "\tdatasource_type,\n" +
            "\tengine,\n" +
            "\tdatasource_info,\n" +
            "\towner_id\n" +
            ") values (\n" +
            "\t#{datasourceName},\n" +
            "\t#{datasourceType},\n" +
            "\t#{engine},\n" +
            "\t#{datasourceInfo},\n" +
            "\t#{ownerId}\n" +
            ")")
    boolean addDatasource(CaesarDatasource datasource);

    @Delete("delete from caesar_datasource where id = #{id}")
    boolean deleteDatasource(int id);

    @Select("select datasource_info from caesar_datasource where engine = #{engine} and datasource_type = #{datasourceType}")
    String findDatasourceInfoByEnvAndEngine(int engine, int datasourceType);

}
