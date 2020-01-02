package com.wenyu7980.statemachine.exception;

import java.text.MessageFormat;

/**
 * 匹配出多条状态迁移路径异常
 * @author:wenyu
 * @date:2019/12/26
 */
public class StateMachineTooManyException extends StateMachineException {
    public StateMachineTooManyException(Object machine, Object state,
            Object event) {
        super(MessageFormat
                .format("状态机{0}中有超过一条状态{1}和事件{2}的状态迁移", machine, state, event));
    }
}
