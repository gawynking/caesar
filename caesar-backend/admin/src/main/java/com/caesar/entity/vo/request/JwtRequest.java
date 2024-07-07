package com.caesar.entity.vo.request;

import lombok.Data;

@Data
public class JwtRequest {
    private String username;
    private String password;
}
