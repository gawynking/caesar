package com.caesar.controller;

import com.caesar.entity.CaesarDatasource;
import com.caesar.model.JsonResponse;
import com.caesar.service.DatasourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/datasource")
public class DatasourceManagerController {

    @Autowired
    DatasourceService datasourceService;

    @PostMapping("/addDatasource")
    public JsonResponse<Boolean> addDatasource(@RequestBody CaesarDatasource datasource){
        return JsonResponse.success(datasourceService.addDatasource(datasource));
    }

    @GetMapping("/deleteDatasource")
    public JsonResponse<Boolean> deleteDatasource(@RequestParam int id){
        return JsonResponse.success(datasourceService.deleteDatasource(id));
    }

    @GetMapping("/getDatasourceList")
    public JsonResponse<List<CaesarDatasource>> getDatasourceList(){
        return JsonResponse.success(datasourceService.list());
    }

}