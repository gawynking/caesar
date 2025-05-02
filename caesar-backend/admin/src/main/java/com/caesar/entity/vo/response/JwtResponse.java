package com.caesar.entity.vo.response;

import lombok.Data;

@Data
public class JwtResponse {

    private String token;
    public JwtResponse(String token) {
        this.token = token;
    }

}
