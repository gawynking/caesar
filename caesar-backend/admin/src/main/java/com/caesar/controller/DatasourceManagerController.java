package com.caesar.controller;

import com.caesar.entity.CaesarDatasource;
import com.caesar.entity.vo.CaesarDatasourceVo;
import com.caesar.model.JsonResponse;
import com.caesar.service.DatasourceService;
import com.caesar.service.UserManagerService;
import com.caesar.tool.BeanConverterTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/datasource")
public class DatasourceManagerController {

    @Autowired
    DatasourceService datasourceService;

    @Autowired
    UserManagerService userManagerService;

    @PostMapping("/addDatasource")
    public JsonResponse<String> addDatasource(@RequestBody CaesarDatasourceVo datasource){
        try {
            CaesarDatasource caesarDatasource = BeanConverterTools.convert(datasource, CaesarDatasource.class);
            caesarDatasource.setOwnerId(userManagerService.getUserIdFromUserName(datasource.getOwnerName()));
            if (datasourceService.addDatasource(caesarDatasource)) {
                return JsonResponse.success("添加数据源信息成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("添加数据源信息失败");
    }

    @GetMapping("/deleteDatasource")
    public JsonResponse<String> deleteDatasource(@RequestParam int id){
        try {
            if (datasourceService.deleteDatasource(id)) {
                return JsonResponse.success("删除数据源信息成功");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("删除数据源信息失败");
    }

    @GetMapping("/getDatasourceList")
    public JsonResponse<List<CaesarDatasource>> getDatasourceList(){
        try {
            return JsonResponse.success(datasourceService.list());
        }catch (Exception e){
            e.printStackTrace();
        }
        return JsonResponse.fail("获取数据源列表信息失败");
    }

}