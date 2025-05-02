package com.caesar.model.code.model;


public interface CaesarParams {

    ParamsType type = ParamsType.Non;

    public ParamsType getType();

    enum ParamsType{

        Non("set","无效参数"),
        Pair("pair","key:value对"),
        customFunction("function","自定义函数");

        private final String tag;
        private final String desc;

        ParamsType(String tag, String desc) {
            this.tag = tag;
            this.desc = desc;
        }
    }
}
