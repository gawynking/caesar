package com.caesar.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.caesar.entity.CaesarTask;
import com.caesar.entity.dto.CaesarTaskDto;
import com.caesar.entity.vo.CaesarTaskParameterVo;
import com.caesar.entity.vo.CaesarTaskVo;
import com.caesar.entity.vo.response.CaesarTaskVersionVo;
import com.caesar.enums.EngineEnum;
import com.caesar.model.MenuModel;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;


public interface DevelopCenterService extends IService<CaesarTask> {

//    String defaultTaskScript = IOUtils.readFileToBuffer("TaskTemplate.code");

    String defaultTaskScript = readTaskTemplate();

    static String readTaskTemplate() {
        Resource resource = new ClassPathResource("template/DefaultTemplate.code");
        try {
            return IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    String getCodeTemplate(EngineEnum engine);

    List<MenuModel> listTaskToMenu(String partten);

    boolean addTask(CaesarTaskDto task);

    CaesarTaskVo getCurrentTaskInfo(String taskName);

    List<CaesarTaskVo> getTaskInfos(String taskName);

    Boolean deleteTaskFromTaskName(String taskName);

    Boolean markDeletedTaskFromTaskName(String taskName);

    String getTaskChecksumFromVersion(String taskName, int lastVersion);

    int saveTask(CaesarTaskDto caesarTaskDto);

    CaesarTaskVersionVo getTaskVersions(String taskName, int currentVersion);

    List<CaesarTaskParameterVo> getParams();

    CaesarTaskVo getCurrentTaskInfoWithVersion(String taskName, int version);

    Boolean releaseTask(int taskId);

    Boolean currentVersionTaskOffline(int taskId);

    CaesarTask getTaskOnlineVersionInfoFromReviewTaskId(int taskId);

    Boolean validateTaskPublish(int taskId);

    void taskOnline(int taskId);
}
