package com.tj.demo.xunwu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupportAddressDto {
    private Integer id;
    private String belongTo;
    private String enName;
    private String cnName;
    private String level;
}
