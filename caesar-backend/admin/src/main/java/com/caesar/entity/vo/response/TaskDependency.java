package com.caesar.entity.vo.response;

import lombok.Data;

@Data
public class TaskDependency {
    String dependencyName;
    String joinTypeDesc = "自动识别"; // 1-自动识别 2-手动识别
}
