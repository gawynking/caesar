package com.caesar.entity.vo.response;

import com.caesar.entity.BaseEntity;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class CaesarTaskTestCaseVo {

    String uuid;
    int TaskId;
    String taskName;
    int taskVersion;
    int userId;
    String username;
    String testCode;
    int testResult = 0;
    String auditMessage = "";
    Timestamp createTime;

}
