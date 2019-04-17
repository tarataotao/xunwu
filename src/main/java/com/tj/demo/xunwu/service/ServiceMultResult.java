package com.tj.demo.xunwu.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 通用多结果Service返回结构
 * @param <T>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMultResult<T> {
    private long total;
    private List<T> result;

    public int getResultSize(){
        if(this.result==null){
            return 0;
        }
        return result.size();
    }

}
