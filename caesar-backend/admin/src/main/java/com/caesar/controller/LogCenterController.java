package com.caesar.controller;

import com.caesar.model.JsonResponse;
import com.mysql.cj.log.Log;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
@RequestMapping("/logger")
public class LogCenterController {

    private static final Logger logger = Logger.getLogger(LogCenterController.class.getName());

    @GetMapping("/fetchCaesarSystemLog")
    private String fetchCaesarSystemLog(){
        return "";
    }


    @GetMapping("/fetchTaskExecuteLog")
    private String fetchTaskExecuteLog(@RequestParam int id){
        return "";
    }


}
