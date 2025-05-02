package com.caesar.entity.vo;

import com.caesar.entity.dto.CaesarGroupServiceDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CaesarGroupServiceVo {

    int groupId;
    String groupName;
    List<DbInfos> dbInfos;

    @Data
    public static class DbInfos{
        int id;
        String serviceTag;
        String levelTag;
        int isTest;
    }


    public List<CaesarGroupServiceVo> assembleData(List<CaesarGroupServiceDto> dbDatas) {
        // 使用 Map 来收集每个组的 DbInfos
        Map<String, List<DbInfos>> groupMap = new HashMap<>();

        for (CaesarGroupServiceDto row : dbDatas) {
            int groupId = row.getGroupId();
            String groupName = row.getGroupName();

            int id = row.getId();
            String serviceTag = row.getServiceTag();
            String levelTag = row.getLevelTag();
            int isTest = row.getIsTest();

            DbInfos dbInfo = new DbInfos();
            dbInfo.setId(id);
            dbInfo.setServiceTag(serviceTag);
            dbInfo.setLevelTag(levelTag);
            dbInfo.setIsTest(isTest);

            groupMap.computeIfAbsent(groupId+"__"+groupName, k -> new ArrayList<>()).add(dbInfo);
        }

        // 构建最终的 List<CaesarGroupServiceVo>
        List<CaesarGroupServiceVo> groupServiceVoList = new ArrayList<>();
        for (Map.Entry<String, List<DbInfos>> entry : groupMap.entrySet()) {
            CaesarGroupServiceVo groupServiceVo = new CaesarGroupServiceVo();
            String key = entry.getKey();
            String[] keys = key.split("__");
            groupServiceVo.setGroupId(Integer.valueOf(keys[0]));
            groupServiceVo.setGroupName(keys[1]);
            groupServiceVo.setDbInfos(entry.getValue());
            groupServiceVoList.add(groupServiceVo);
        }

        return groupServiceVoList;
    }


}
