package com.kwezal.bearinmind.translation.utils;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionUtils {

    public static <K1, K2, V> Map<K2, Map<K1, V>> swapMapKeys(final Map<K1, Map<K2, V>> mapOfMaps) {
        final var result = new HashMap<K2, Map<K1, V>>();

        mapOfMaps.forEach((k1, k2Map) ->
            k2Map.forEach((k2, value) -> {
                result.computeIfAbsent(k2, k -> new HashMap<>()).put(k1, value);
            })
        );

        return result;
    }
}
