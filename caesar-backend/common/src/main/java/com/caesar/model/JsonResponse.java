package com.caesar.model;

import lombok.Data;
import java.io.Serializable;

@Data
public class JsonResponse<T> implements Serializable {

    private Integer code;
    private String status;
    private String message;
    private Data<T> data;
    private Metadata metadata;

    private JsonResponse(Integer code, String status, String message,String tag,T items, Integer total) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = new Data<T>(items);
        this.metadata = new Metadata(tag);
        this.data.pagination = new Pagination(total);
    }


    private static <T> JsonResponse<T> result(Integer code, String status, String message, String tag, T items, Integer total) {
        return new JsonResponse<>(code, status, message, tag, items, total);
    }

    public static <T> JsonResponse<T> fail() {
        return result(400, "fail", "失败","no tag", null,0);
    }

    public static <T> JsonResponse<T> fail(String message) {
        return result(400, "fail", message,"no tag", null,0);
    }

    public static <T> JsonResponse<T> success() {
        return result(200, "success","成功", "no tag", null,0);
    }

    public static <T> JsonResponse<T> success(T data) {
        return result(200, "success","成功", "no tag", data, null);
    }

    public static <T> JsonResponse<T> success(T data, Integer total) {
        return result(200, "success","成功", "no tag", data, total);
    }



    @lombok.Data
    private static class Data<T>{
        private T items;
        private Pagination pagination;

        private Data(T items){
            this.items = items;
        }

    }


    @lombok.Data
    private static class Pagination{

        private Integer totalItems;
        private Integer totalPages;
        private Integer pageNumber;
        private Integer pageSize;

        private Pagination(Integer totalItems){
            this.totalItems = totalItems;
        }

        private Pagination(Integer totalItems,Integer totalPages,Integer pageNumber,Integer pageSize){
            this.totalItems = totalItems;
            this.totalPages = totalPages;
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
        }

    }

    @lombok.Data
    private static class Metadata{
        private String tag;

        private Metadata(String tag){
            this.tag = tag;
        }
    }


}
