package com.tj.demo.xunwu.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DatatableSearch {

    /**
     * databases要求辉县字段
     */
    private int draw;
    /**
     * datatables规定分页字段
     */
    private int start;
    private int length;

    private Integer status;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeMin;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createTimeMax;

    private String city;
    private String title;
    private String direction;
    private String orderBy;

}