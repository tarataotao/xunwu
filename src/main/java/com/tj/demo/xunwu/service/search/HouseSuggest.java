package com.tj.demo.xunwu.service.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HouseSuggest {

    private String input;

    private int weight=10;//默认权重

}
