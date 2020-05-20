package com.wenyu7980.statemachine;

/**
 *
 * @author:wenyu
 * @date:2019/12/26
 */
public class Data {
    private State state;

    public State getState() {
        return state;
    }

    public State setState(State state) {
        this.state = state;
        return this.state;
    }
}
