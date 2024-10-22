package com.caesar.operator.filter;

/**
 * 与运算
 */
public class And implements LogicalCondition{

    @Override
    public Boolean apply(Boolean... items) {
        Boolean flag = true;
        for(Boolean item:items){
            flag = flag && item;
        }
        return flag;
    }

}
