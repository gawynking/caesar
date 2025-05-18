package com.caesar.entity.vo.request;

import lombok.Data;

@Data
public class VerificationTestingVo {

    String uuid;
    int testResult = 0;
    String auditMessage = "";

}
