package com.wenyu7980.statemachine.triple;

import com.wenyu7980.statemachine.StateContainer;

import java.util.Objects;

/**
 * @author wenyu
 */
public class StateTriple implements StateContainer {
    private String s1;
    private String s2;
    private String s3;

    public StateTriple(String s1, String s2, String s3) {
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
    }

    public String getS1() {
        return s1;
    }

    public String getS2() {
        return s2;
    }

    public String getS3() {
        return s3;
    }

    @Override
    public boolean match(StateContainer s) {
        StateTriple triple = (StateTriple) s;
        if (s1 != null && triple.s1 != null && !Objects.equals(s1, triple.s1)) {
            return false;
        }
        if (s2 != null && triple.s2 != null && !Objects.equals(s2, triple.s2)) {
            return false;
        }
        if (s3 != null && triple.s3 != null && !Objects.equals(s3, triple.s3)) {
            return false;
        }
        return true;
    }
}
