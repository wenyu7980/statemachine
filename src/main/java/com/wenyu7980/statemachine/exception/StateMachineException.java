package com.wenyu7980.statemachine.exception;

/**
 * 状态机异常
 * @author:wenyu
 * @date:2019/12/26
 */
public abstract class StateMachineException extends RuntimeException {
    public StateMachineException(String message) {
        super(message);
    }
}
