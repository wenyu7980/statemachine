package com.wenyu7980.statemachine.listener;
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

/**
 * 事件监听
 * @author:wenyu
 * @date:2019/12/26
 * @param <T> 数据类型
 * @param <S> 状态类型
 * @param <E> 事件类型
 */
public interface StateMachineEventListener<T, S extends StateContainer, E> {
    /**
     * 是否是事件触发后
     * @return
     */
    boolean post();

    /**
     * 判断
     * @param event
     * @return
     */
    boolean compare(E event);

    /**
     * 监听
     * @param t 状态数据
     *          如果isPost为false,事件触发前数据
     *          如果isPost为true,事件触发后数据
     *          数据中的状态字段可能发生变更
     * @param s 状态：一直为源状态
     * @param context 事件数据
     */
    void listener(final T t, final S s, final Object context);

}
