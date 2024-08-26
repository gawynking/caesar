package com.caesar.model;

import com.caesar.enums.SchedulingPeriod;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DependencyModel {

    String dependency;
    SchedulingPeriod period;
    String dateValue;

}
