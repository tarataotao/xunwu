package com.tj.demo.xunwu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubwayStationDto {

    private Integer id;

    private Integer subwayId;

    private String name;
}
