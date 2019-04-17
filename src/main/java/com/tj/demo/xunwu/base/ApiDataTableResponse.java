package com.tj.demo.xunwu.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datables相应结果
 */
@Setter
@Getter
@NoArgsConstructor

public class ApiDataTableResponse extends  ApiResponse {

    /**
     * 以下为dataTables的固定模式
     */
    private int draw;//用于验证结果
    private long recordsTotal; //总数
    private long recordsFiltered;//分页


    public ApiDataTableResponse(ApiResponse.Status status){
        this(status.getCode(),status.getStandardMessage(),null);
    }
    public ApiDataTableResponse(int code, String message, Object data) {
        super(code, message, data);
    }

}
