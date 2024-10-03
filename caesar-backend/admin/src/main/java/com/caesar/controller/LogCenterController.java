package com.caesar.controller;

import com.caesar.model.JsonResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logger")
public class LogCenterController {

    @GetMapping("/fetchCaesarSystemLog")
    private String fetchCaesarSystemLog(){
        return "";
    }


    @GetMapping("/fetchTaskExecuteLog")
    private String fetchTaskExecuteLog(@RequestParam int id){
        return "";
    }


}
