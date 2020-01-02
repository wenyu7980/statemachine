package com.wenyu7980.statemachine.exception;

import com.wenyu7980.statemachine.StateContainer;

/**
 * 不存在异常
 * @author:wenyu
 * @date:2020/1/2
 */
public interface StateMachineNotFoundSupplier<T, S extends StateContainer, E, R extends RuntimeException> {
    /**
     * 异常
     * @param d
     * @param s
     * @param e
     * @return
     */
    R get(T d, S s, E e);
}
