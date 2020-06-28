package com.wenyu7980.statemachine;

import com.wenyu7980.statemachine.exception.StateMachineTooManyException;
import com.wenyu7980.statemachine.guard.StateMachineGuard;
import com.wenyu7980.statemachine.listener.StateMachineEventListener;
import com.wenyu7980.statemachine.listener.StateMachineListener;
import com.wenyu7980.statemachine.listener.StateMachineStateListener;
import com.wenyu7980.statemachine.listener.StateMachineTransformListener;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

/**
 *
 * @author:wenyu
 * @date:2019/12/26
 */
public class StateMachineTest {
    @Test(expected = RuntimeException.class)
    public void testNotFoundException() {
        StateMachine<Data, State, Event> stateMachine = new StateMachine<>(
                "stateMachine", Data::setState,
                (Data d, State state, Event event) -> new RuntimeException(
                        "不存在"));
        stateMachine.sendEvent(new Data(), State.S1, Event.E1, null);
    }

    @Test(expected = StateMachineTooManyException.class)
    public void testTooManyException() {
        StateMachine<Data, State, Event> stateMachine = new StateMachine<>(
                "stateMachine", Data::setState,
                (Data d, State state, Event event) -> new RuntimeException(
                        "不存在"));
        stateMachine
                .addStateMachineTransform(State.S1, Event.E1, State.S2, null);
        stateMachine
                .addStateMachineTransform(State.S1, Event.E1, State.S2, null);
        stateMachine.sendEvent(new Data(), State.S1, Event.E1, null);
    }

    @Test
    public void testSetState() {
        StateMachine<Data, State, Event> stateMachine = new StateMachine<>(
                "stateMachine", Data::setState,
                (Data d, State state, Event event) -> new RuntimeException(
                        "不存在"));
        stateMachine
                .addStateMachineTransform(State.S1, Event.E1, State.S2, null);
        Data data = new Data();
        stateMachine.sendEvent(data, State.S1, Event.E1, null);
        Assert.assertEquals(State.S2, data.getState());
    }

    @Test
    public void testGuard() {
        StateMachine<Data, State, Event> stateMachine = new StateMachine<>(
                "stateMachine", Data::setState,
                (Data d, State state, Event event) -> new RuntimeException(
                        "不存在"));
        Data data = new Data();
        // guard mock
        StateMachineGuard<Data, State, Event> guard1 = Mockito
                .mock(StateMachineGuard.class);
        when(guard1.guard(data, State.S1, Event.E1, null)).thenReturn(true);
        StateMachineGuard<Data, State, Event> guard2 = Mockito
                .mock(StateMachineGuard.class);
        when(guard2.guard(data, State.S1, Event.E1, "context"))
                .thenReturn(true);
        stateMachine
                .addStateMachineTransform(State.S1, Event.E1, State.S2, guard1);
        stateMachine
                .addStateMachineTransform(State.S1, Event.E1, State.S3, guard2);
        Assert.assertEquals(State.S2,
                stateMachine.sendEvent(data, State.S1, Event.E1, null));
        Assert.assertEquals(State.S3,
                stateMachine.sendEvent(data, State.S1, Event.E1, "context"));

    }

    @Test
    public void testListener() {
        StateMachine<Data, State, Event> stateMachine = new StateMachine<>(
                "stateMachine", Data::setState,
                (Data d, State state, Event event) -> new RuntimeException(
                        "不存在"));
        Data data = new Data();
        String context = "test";
        stateMachine
                .addStateMachineTransform(State.S1, Event.E1, State.S2, null);
        // 状态机前监听
        StateMachineListener<Data, State, Event> preListener = Mockito
                .mock(StateMachineListener.class);
        when(preListener.start()).thenReturn(true);
        stateMachine.addListener(preListener);
        // 事件前监听
        StateMachineEventListener<Data, State, Event> preEvent = Mockito
                .mock(StateMachineEventListener.class);
        when(preEvent.post()).thenReturn(false);
        when(preEvent.event()).thenReturn(Event.E1);
        stateMachine.addEventListener(preEvent);
        // 状态变化监听,离开状态
        StateMachineStateListener<Data, State, Event> exitState = Mockito
                .mock(StateMachineStateListener.class);
        when(exitState.exit()).thenReturn(true);
        when(exitState.state()).thenReturn(State.S1);
        when(exitState.strict()).thenReturn(true);
        stateMachine.addStateListener(exitState);
        // 事件后监听
        StateMachineEventListener<Data, State, Event> postEvent = Mockito
                .mock(StateMachineEventListener.class);
        when(postEvent.post()).thenReturn(true);
        when(postEvent.event()).thenReturn(Event.E1);
        stateMachine.addEventListener(postEvent);
        // 迁移监听
        StateMachineTransformListener<Data, State, Event> transformListener = Mockito
                .mock(StateMachineTransformListener.class);
        when(transformListener.source()).thenReturn(State.S1);
        when(transformListener.target()).thenReturn(State.S2);
        stateMachine.addTransformListener(transformListener);
        // 状态变化监听,进入状态
        StateMachineStateListener<Data, State, Event> enterState = Mockito
                .mock(StateMachineStateListener.class);
        when(enterState.exit()).thenReturn(false);
        when(enterState.state()).thenReturn(State.S2);
        when(enterState.strict()).thenReturn(true);
        stateMachine.addStateListener(enterState);
        // 状态机后监听
        StateMachineListener<Data, State, Event> postListener = Mockito
                .mock(StateMachineListener.class);
        when(postListener.start()).thenReturn(false);
        stateMachine.addListener(postListener);
        // 状态迁移
        stateMachine.sendEvent(data, State.S1, Event.E1, context);
        // 确认调用
        InOrder inOrder = inOrder(preListener, preEvent, exitState, postEvent,
                enterState, transformListener, postListener);
        inOrder.verify(preListener).listener(data, State.S1, Event.E1, context);
        inOrder.verify(preEvent).listener(data, State.S1, context);
        inOrder.verify(exitState).listener(data, Event.E1);
        inOrder.verify(postEvent).listener(data, State.S1, context);
        inOrder.verify(enterState).listener(data, Event.E1);
        inOrder.verify(transformListener).listener(data, Event.E1);
        inOrder.verify(postListener)
                .listener(data, State.S1, Event.E1, context);
    }

    @Test
    public void testListenerStrict() {
        StateMachine<Data, State, Event> stateMachine = new StateMachine<>(
                "stateMachine", Data::setState,
                (Data d, State state, Event event) -> new RuntimeException(
                        "不存在"));
        Data data = new Data();
        String context = "test";
        stateMachine
                .addStateMachineTransform(State.S1, Event.E1, State.S1, null);
        // 状态机前监听
        StateMachineListener<Data, State, Event> preListener = Mockito
                .mock(StateMachineListener.class);
        when(preListener.start()).thenReturn(true);
        stateMachine.addListener(preListener);
        // 事件前监听
        StateMachineEventListener<Data, State, Event> preEvent = Mockito
                .mock(StateMachineEventListener.class);
        when(preEvent.post()).thenReturn(false);
        when(preEvent.event()).thenReturn(Event.E1);
        stateMachine.addEventListener(preEvent);
        // 状态变化监听,离开状态
        StateMachineStateListener<Data, State, Event> exitState = Mockito
                .mock(StateMachineStateListener.class);
        when(exitState.exit()).thenReturn(true);
        when(exitState.state()).thenReturn(State.S1);
        when(exitState.strict()).thenReturn(false);
        stateMachine.addStateListener(exitState);
        // 事件后监听
        StateMachineEventListener<Data, State, Event> postEvent = Mockito
                .mock(StateMachineEventListener.class);
        when(postEvent.post()).thenReturn(true);
        when(postEvent.event()).thenReturn(Event.E1);
        stateMachine.addEventListener(postEvent);
        // 迁移监听
        StateMachineTransformListener<Data, State, Event> transformListener = Mockito
                .mock(StateMachineTransformListener.class);
        when(transformListener.source()).thenReturn(State.S1);
        when(transformListener.target()).thenReturn(State.S1);
        stateMachine.addTransformListener(transformListener);
        // 状态变化监听,进入状态
        StateMachineStateListener<Data, State, Event> enterState = Mockito
                .mock(StateMachineStateListener.class);
        when(enterState.exit()).thenReturn(false);
        when(enterState.state()).thenReturn(State.S1);
        when(enterState.strict()).thenReturn(false);
        stateMachine.addStateListener(enterState);
        // 状态机后监听
        StateMachineListener<Data, State, Event> postListener = Mockito
                .mock(StateMachineListener.class);
        when(postListener.start()).thenReturn(false);
        stateMachine.addListener(postListener);
        // 状态迁移
        stateMachine.sendEvent(data, State.S1, Event.E1, context);
        // 确认调用
        InOrder inOrder = inOrder(preListener, preEvent, exitState, postEvent,
                enterState, transformListener, postListener);
        inOrder.verify(preListener).listener(data, State.S1, Event.E1, context);
        inOrder.verify(preEvent).listener(data, State.S1, context);
        inOrder.verify(exitState).listener(data, Event.E1);
        inOrder.verify(postEvent).listener(data, State.S1, context);
        inOrder.verify(enterState).listener(data, Event.E1);
        inOrder.verify(transformListener).listener(data, Event.E1);
        inOrder.verify(postListener)
                .listener(data, State.S1, Event.E1, context);
    }

    @Test
    public void testListenerStrict2NoInvoked() {
        StateMachine<Data, State, Event> stateMachine = new StateMachine<>(
                "stateMachine", Data::setState,
                (Data d, State state, Event event) -> new RuntimeException(
                        "不存在"));
        Data data = new Data();
        String context = "test";
        stateMachine
                .addStateMachineTransform(State.S1, Event.E1, State.S1, null);
        // 状态机前监听
        StateMachineListener<Data, State, Event> preListener = Mockito
                .mock(StateMachineListener.class);
        when(preListener.start()).thenReturn(true);
        stateMachine.addListener(preListener);
        // 事件前监听
        StateMachineEventListener<Data, State, Event> preEvent = Mockito
                .mock(StateMachineEventListener.class);
        when(preEvent.post()).thenReturn(false);
        when(preEvent.event()).thenReturn(Event.E1);
        stateMachine.addEventListener(preEvent);
        // 状态变化监听,离开状态
        StateMachineStateListener<Data, State, Event> exitState = Mockito
                .mock(StateMachineStateListener.class);
        when(exitState.exit()).thenReturn(true);
        when(exitState.state()).thenReturn(State.S1);
        when(exitState.strict()).thenReturn(true);
        stateMachine.addStateListener(exitState);
        // 事件后监听
        StateMachineEventListener<Data, State, Event> postEvent = Mockito
                .mock(StateMachineEventListener.class);
        when(postEvent.post()).thenReturn(true);
        when(postEvent.event()).thenReturn(Event.E1);
        stateMachine.addEventListener(postEvent);
        // 迁移监听
        StateMachineTransformListener<Data, State, Event> transformListener = Mockito
                .mock(StateMachineTransformListener.class);
        when(transformListener.source()).thenReturn(State.S1);
        when(transformListener.target()).thenReturn(State.S1);
        stateMachine.addTransformListener(transformListener);
        // 状态变化监听,进入状态
        StateMachineStateListener<Data, State, Event> enterState = Mockito
                .mock(StateMachineStateListener.class);
        when(enterState.exit()).thenReturn(false);
        when(enterState.state()).thenReturn(State.S1);
        when(enterState.strict()).thenReturn(true);
        stateMachine.addStateListener(enterState);
        // 状态机后监听
        StateMachineListener<Data, State, Event> postListener = Mockito
                .mock(StateMachineListener.class);
        when(postListener.start()).thenReturn(false);
        stateMachine.addListener(postListener);
        // 状态迁移
        stateMachine.sendEvent(data, State.S1, Event.E1, context);
        // 确认调用
        InOrder inOrder = inOrder(preListener, preEvent, exitState, postEvent,
                enterState, transformListener, postListener);
        inOrder.verify(preListener).listener(data, State.S1, Event.E1, context);
        inOrder.verify(preEvent).listener(data, State.S1, context);
        inOrder.verify(exitState, times(0)).listener(data, Event.E1);
        inOrder.verify(postEvent).listener(data, State.S1, context);
        inOrder.verify(enterState, times(0)).listener(data, Event.E1);
        inOrder.verify(transformListener).listener(data, Event.E1);
        inOrder.verify(postListener)
                .listener(data, State.S1, Event.E1, context);
    }
}
