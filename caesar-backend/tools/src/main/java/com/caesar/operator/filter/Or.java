package com.caesar.operator.filter;

/**
 * 或运算
 */
public class Or implements LogicalCondition{
    @Override
    public Boolean apply(Boolean... items) {
        Boolean flag = false;
        for(Boolean item:items){
            flag = flag || item;
        }
        return flag;
    }
}
