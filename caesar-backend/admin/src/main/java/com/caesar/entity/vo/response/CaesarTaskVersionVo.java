package com.caesar.entity.vo.response;

import com.caesar.entity.vo.CaesarTaskVo;
import lombok.Data;

import java.util.List;

@Data
public class CaesarTaskVersionVo {

    int currentVersion;
    List<CaesarTaskVo> caesarTaskVos;

}
