package com.wenyu7980.statemachine.triple;

import com.wenyu7980.statemachine.StateMachine;
import com.wenyu7980.statemachine.listener.StateMachineStateListener;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

/**
 * @author wenyu
 */
public class StateMachineTripleTest {

    @Test
    public void testTriple() {
        final StateMachine<Data, StateTriple, String> stateMachine = new StateMachine<>(
                "Triple",
                (d) -> new StateTriple(d.getS1(), d.getS2(), d.getS3()),
                (d, s) -> {
                    if (Objects.nonNull(s.getS1())) {
                        d.setS1(s.getS1());
                    }
                    if (Objects.nonNull(s.getS2())) {
                        d.setS2(s.getS2());
                    }
                    if (Objects.nonNull(s.getS3())) {
                        d.setS3(s.getS3());
                    }
                }, (d, s, e) -> new RuntimeException("NOT FOUNT"));
        stateMachine.addStateMachineTransform(new StateTriple("11", "21", "31"),
                "E0", new StateTriple("11", "21", "31"), null);
        stateMachine.addStateMachineTransform(new StateTriple("11", "21", null),
                "E1", new StateTriple("11", "22", null), null);
        stateMachine.addStateMachineTransform(new StateTriple("11", null, "31"),
                "E2", new StateTriple("11", null, "32"), null);
        stateMachine.addStateListener(
                new StateMachineStateListener<Data, StateTriple, String>() {
                    @Override
                    public boolean exit() {
                        return false;
                    }

                    @Override
                    public StateTriple state() {
                        return new StateTriple("11", "21", null);
                    }

                    @Override
                    public void listener(Data data, String s) {
                        stateMachine.sendEvent(data, "E1", null);
                    }
                });
        stateMachine.addStateListener(
                new StateMachineStateListener<Data, StateTriple, String>() {
                    @Override
                    public boolean exit() {
                        return false;
                    }

                    @Override
                    public StateTriple state() {
                        return new StateTriple("11", null, "31");
                    }

                    @Override
                    public void listener(Data data, String s) {
                        Assert.assertEquals(data.getS1(), "11");
                        Assert.assertEquals(data.getS3(), "31");
                        stateMachine.sendEvent(data, "E2", null);
                    }
                });
        stateMachine.sendEvent(new Data("11", "21", "31"), "E0", null);
    }
}
