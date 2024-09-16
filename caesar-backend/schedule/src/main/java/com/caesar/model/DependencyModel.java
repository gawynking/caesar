package com.caesar.model;

import com.caesar.enums.SchedulingPeriod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DependencyModel {

    String dependency;
    SchedulingPeriod period = SchedulingPeriod.DAY;
    String dateValue = "today";

}
