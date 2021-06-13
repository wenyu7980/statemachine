package com.wenyu7980.statemachine;

/**
 *
 * @author wenyu
 */
public interface StateMachine<T, E> {
    /**
     * 触发事件
     * @param t
     * @param event
     * @param context
     */
    void sendEvent(final T t, final E event, final Object context);
}
