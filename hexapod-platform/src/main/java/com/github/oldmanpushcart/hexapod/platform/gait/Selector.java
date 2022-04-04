package com.github.oldmanpushcart.hexapod.platform.gait;

import io.github.oldmanpushcart.hexapod.api.Joint;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 关节选择器
 */
public interface Selector {

    /**
     * 过滤想要的关节
     *
     * @param filter 过滤器
     * @return this
     */
    Selector filter(Predicate<Joint> filter);

    /**
     * 选择最终结果
     *
     * @return 选定关节集合
     */
    Set<Joint> selected();

    /**
     * 选择指定关节
     *
     * @param joints 从关节列表中选择
     * @return 关节选择器
     */
    static Selector select(Joint... joints) {
        return new Selector() {

            private final Collection<Predicate<Joint>> filters = new LinkedList<>();

            @Override
            public Selector filter(Predicate<Joint> filter) {
                filters.add(filter);
                return this;
            }

            @Override
            public Set<Joint> selected() {
                return Stream.of(joints)
                        .filter(joint -> filters.stream().allMatch(filter -> filter.test(joint)))
                        .collect(Collectors.toUnmodifiableSet());
            }

        };
    }

    /**
     * 选择指定关节
     *
     * @return 关节选择器
     */
    static Selector select() {
        return select(Joint.values());
    }

}
