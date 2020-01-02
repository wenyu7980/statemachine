package com.wenyu7980.statemachine;

/**
 *
 * @author:wenyu
 * @date:2019/12/26
 */
public enum State implements StateContainer<State> {
    S1,
    S2,
    S3;

    @Override
    public boolean match(StateContainer<State> s) {
        return this.equals(s);
    }
}
