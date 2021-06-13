package com.wenyu7980.statemachine.impl;
/**
 * Copyright wenyu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.wenyu7980.statemachine.StateContainer;
import com.wenyu7980.statemachine.StateMachine;
import com.wenyu7980.statemachine.exception.StateMachineNotFoundSupplier;
import com.wenyu7980.statemachine.exception.StateMachineTooManyException;
import com.wenyu7980.statemachine.guard.StateMachineGuard;
import com.wenyu7980.statemachine.listener.StateMachineEventListener;
import com.wenyu7980.statemachine.listener.StateMachineListener;
import com.wenyu7980.statemachine.listener.StateMachineStateListener;
import com.wenyu7980.statemachine.listener.StateMachineTransformListener;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 状态机
 * @author:wenyu
 * @date:2019/12/26
 */
public class AbstractStateMachine<T, S extends StateContainer, E> implements StateMachine<T, E> {
    private String name;
    /** 状态机列表 */
    private List<MachineContainer<T, S, E>> machineContainers = new ArrayList<>();
    /** 在状态数据中设定状态 */
    private final BiConsumer<T, S> setState;
    /** 在状态数据中设定状态 */
    private final Function<T, S> getState;
    /** 状态迁移不存在异常 */
    private final StateMachineNotFoundSupplier<T, S, E, RuntimeException> notFoundException;
    /** 事件监听 */
    private List<StateMachineEventListener<T, S, E>> eventListeners = new ArrayList<>();
    /** 状态监听 */
    private List<StateMachineStateListener<T, S, E>> stateListeners = new ArrayList<>();
    /** 状态迁移监听 */
    private List<StateMachineTransformListener<T, S, E>> transformListeners = new ArrayList<>();
    /** 状态机监听 */
    private List<StateMachineListener<T, S, E>> listeners = new ArrayList<>();

    /**
     *
     * @param name
     * @param getState
     * @param setState
     */
    public AbstractStateMachine(String name, Function<T, S> getState, BiConsumer<T, S> setState,
      StateMachineNotFoundSupplier<T, S, E, RuntimeException> notFoundException) {
        this.name = name;
        this.setState = setState;
        this.getState = getState;
        this.notFoundException = notFoundException;
    }

    /**
     * 添加状态迁移路径
     * @param source
     * @param event
     * @param target
     * @param guard
     * @return
     */
    public AbstractStateMachine<T, S, E> addStateMachineTransform(S source, E event, S target,
      StateMachineGuard<T, S, E> guard) {
        this.machineContainers.add(new AbstractStateMachine.MachineContainer<>(source, event, target, guard));
        return this;
    }

    /**
     * 添加事件监听
     * @param listeners
     * @return
     */
    public AbstractStateMachine<T, S, E> addEventListener(StateMachineEventListener<T, S, E>... listeners) {
        this.eventListeners.addAll(Arrays.asList(listeners));
        return this;
    }

    /**
     * 添加事件监听
     * @param listeners
     * @return
     */
    public AbstractStateMachine<T, S, E> addEventListeners(Collection<StateMachineEventListener<T, S, E>> listeners) {
        this.eventListeners.addAll(listeners);
        return this;
    }

    /**
     * 添加状态监听
     * @param listeners
     * @return
     */
    public AbstractStateMachine<T, S, E> addStateListener(StateMachineStateListener<T, S, E>... listeners) {
        this.stateListeners.addAll(Arrays.asList(listeners));
        return this;
    }

    /**
     * 添加状态监听
     * @param listeners
     * @return
     */
    public AbstractStateMachine<T, S, E> addStateListeners(Collection<StateMachineStateListener<T, S, E>> listeners) {
        this.stateListeners.addAll(listeners);
        return this;
    }

    /**
     * 添加状态机监听
     * @param listeners
     * @return
     */
    public AbstractStateMachine<T, S, E> addListener(StateMachineListener<T, S, E>... listeners) {
        this.listeners.addAll(Arrays.asList(listeners));
        return this;
    }

    /**
     * 添加状态机监听
     * @param listeners
     * @return
     */
    public AbstractStateMachine<T, S, E> addListeners(Collection<StateMachineListener<T, S, E>> listeners) {
        this.listeners.addAll(listeners);
        return this;
    }

    /**
     * 添加状态迁移监听
     * @param listeners
     * @return
     */
    public AbstractStateMachine<T, S, E> addTransformListener(StateMachineTransformListener<T, S, E>... listeners) {
        this.transformListeners.addAll(Arrays.asList(listeners));
        return this;
    }

    /**
     * 添加状态迁移监听
     * @param listeners
     * @return
     */
    public AbstractStateMachine<T, S, E> addTransformListeners(
      Collection<StateMachineTransformListener<T, S, E>> listeners) {
        this.transformListeners.addAll(listeners);
        return this;
    }

    /**
     * 触发事件
     * @param t
     * @param event
     * @param context
     */
    @Override
    public void sendEvent(final T t, final E event, final Object context) {
        final S state = getState.apply(t);
        // 状态机开始
        this.listeners.stream().filter(StateMachineListener::start)
          .forEach(listener -> listener.listener(t, state, event, context));
        // 事件触发前准备
        this.eventListeners.stream().filter(listener -> !listener.post() && listener.compare(event))
          .forEach(listener -> listener.listener(t, state, event, context));
        // 状态迁移路径匹配
        final List<MachineContainer<T, S, E>> matches = this.machineContainers.stream()
          .filter(container -> container.match(t, state, event, context)).collect(Collectors.toList());
        // 路径确认
        if (matches.size() == 0) {
            throw notFoundException.get(t, state, event);
        } else if (matches.size() > 1) {
            throw new StateMachineTooManyException(this.name, state, event);
        }
        // 目标状态
        final S to = matches.get(0).target;
        // 设定状态
        this.setState.accept(t, to);
        final S target = this.getState.apply(t);
        // 事件触发后
        this.eventListeners.stream().filter(listener -> listener.post() && listener.compare(event))
          .forEach(listener -> listener.listener(t, state, event, context));
        // 离开状态
        this.stateListeners.stream().filter(
          listener -> listener.exit() && listener.compare(state) && (!Objects.equals(to, state) || !listener.strict()))
          .forEach(listener -> listener.listener(t, state, event));
        // 进入状态
        this.stateListeners.stream().filter(listener -> (!listener.exit()) && listener.compare(getState.apply(t)) && (
          !Objects.equals(state, getState.apply(t)) || !listener.strict()))
          .forEach(listener -> listener.listener(t, state, event));
        // 状态迁移
        this.transformListeners.stream().filter(listener -> listener.source(state) && listener.target(target))
          .forEach(listener -> listener.listener(t, state, event));
        // 状态机结束
        this.listeners.stream().filter(listener -> !listener.start())
          .forEach(listener -> listener.listener(t, state, event, context));
    }

    /**
     * 状态机条目
     * @param <T>
     * @param <S>
     * @param <E>
     */
    protected static class MachineContainer<T, S extends StateContainer, E> {
        private S source;
        private E event;
        private StateMachineGuard<T, S, E> guard;
        private S target;

        public MachineContainer(S source, E event, S target, StateMachineGuard<T, S, E> guard) {
            this.source = source;
            this.event = event;
            this.target = target;
            this.guard = guard;
        }

        public boolean match(T t, S source, E event, Object object) {
            return this.source.match(source) && Objects.equals(this.event, event) && (this.guard == null || this.guard
              .guard(t, source, event, object));
        }

        public S getTo() {
            return this.target;
        }
    }
}
