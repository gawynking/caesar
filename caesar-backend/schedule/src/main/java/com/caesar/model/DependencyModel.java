package com.caesar.model;

import com.caesar.enums.SchedulingPeriod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependencyModel {

    String dependency; // 依赖的调度名称
    SchedulingPeriod period = SchedulingPeriod.DAY;
    String dateValue = "today";

}
