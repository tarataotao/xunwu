package com.tj.demo.xunwu.base;

import com.google.common.collect.Sets;
import org.springframework.data.domain.Sort;

import java.util.Optional;
import java.util.Set;

/**
 * 排序生成器
 */
public class HouseSort {
    public static final String DEFAULT_SORT_KEY = "lastUpdateTime";

    public static final String DISTANCE_TO_SUBWAY_KEY = "distanceToSubway";


    private static final Set<String> SORT_KEYS = Sets.newHashSet(
            DEFAULT_SORT_KEY,
            "createTime",
            "price",
            "area",
            DISTANCE_TO_SUBWAY_KEY
    );

    public static Sort generateSort(String key, String directionKey) {
        key = getSortKey(key);

        Optional<Sort.Direction> direction1= Sort.Direction.fromOptionalString(directionKey);
        Sort.Direction direction= direction1.get();
        if (direction == null) {
            direction = Sort.Direction.DESC;
        }

        return new Sort(direction, key);
    }

    public static String getSortKey(String key) {
        if (!SORT_KEYS.contains(key)) {
            key = DEFAULT_SORT_KEY;
        }

        return key;
    }
}
