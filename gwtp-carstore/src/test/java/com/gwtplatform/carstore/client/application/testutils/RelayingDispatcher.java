/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.carstore.client.application.testutils;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.TypeLiteral;
import com.gwtplatform.dispatch.client.CompletedDispatchRequest;
import com.gwtplatform.dispatch.shared.Action;
import com.gwtplatform.dispatch.shared.DispatchAsync;
import com.gwtplatform.dispatch.shared.DispatchRequest;
import com.gwtplatform.dispatch.shared.NoResult;
import com.gwtplatform.dispatch.shared.Result;

/**
 * Class used to replace a real implementation of the Dispatcher. When executing
 * a request for an action, a predefined result will be sent back immediately.
 *
 * To assign a result to an action, simply do: dispatcher.when({@link Action}
 * ).willReturn({@link Result});
 *
 * @author Christian Goudreau
 */
public class RelayingDispatcher implements DispatchAsync {
    private Map<TypeLiteral<? extends Action<?>>, Result> registry =
            new HashMap<TypeLiteral<? extends Action<?>>, Result>();

    private TypeLiteral<? extends Action<?>> currentAction = null;

    /**
     * This method must be used at least once before being able to use relaying
     * dispatcher. It will create an entry inside the registry and wait that the
     * use assign a result to it.
     *
     * @param <A>
     *            The {@link Action} type.
     * @param action
     *            The class definition of the {@link Action}.
     * @return {@link RelayingDispatcher} instance.
     */
    public <A extends Action<?>> RelayingDispatcher given(TypeLiteral<A> action) {
        registry.put(action, new NoResult());

        currentAction = action;

        return this;
    }

    /**
     * Once you've called at least one time {@link #given(Class)}, then calling
     * this function will assign a {@link Result} to the last {@link Action} you
     * assigned.
     *
     * @param <R>
     *            The {@link Result} type.
     * @param result
     *            the {@link Result} to add inside the registry.
     */
    public <R extends Result> void willReturn(R result) {
        registry.put(currentAction, result);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest execute(A action, AsyncCallback<R> callback) {
        assert action instanceof ActionImpl;

        callback.onSuccess((R) registry.get(((ActionImpl) action).getTypeLiteral()));

        return new CompletedDispatchRequest();
    }

    @Override
    public <A extends Action<R>, R extends Result> DispatchRequest undo(A action, R result, AsyncCallback<Void> callback) {
        callback.onSuccess(null);

        return new CompletedDispatchRequest();
    }
}
