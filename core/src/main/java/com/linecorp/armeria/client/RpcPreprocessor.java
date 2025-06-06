/*
 * Copyright 2024 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.linecorp.armeria.client;

import static java.util.Objects.requireNonNull;

import com.linecorp.armeria.client.endpoint.EndpointGroup;
import com.linecorp.armeria.common.RpcRequest;
import com.linecorp.armeria.common.RpcResponse;
import com.linecorp.armeria.common.SessionProtocol;
import com.linecorp.armeria.common.annotation.UnstableApi;

import io.netty.channel.EventLoop;

/**
 * An RPC-based preprocessor that intercepts an outgoing request and allows users to
 * customize certain properties before entering the decorating chain. The following
 * illustrates a sample use-case:
 * <pre>{@code
 * RpcPreprocessor rpcPreprocessor1 = (delegate, ctx, req) -> {
 *     ctx.setSessionProtocol(SessionProtocol.HTTP);
 *     ctx.setEndpointGroup(Endpoint.of("overriding-host"));
 *     return delegate.execute(ctx, req);
 * };
 * RpcPreprocessor rpcPreprocessor2 = ...
 * Iface iface = ThriftClients.builder(rpcPreprocessor1)
 *                            .rpcPreprocessor(rpcPreprocessor2)
 *                            .build(Iface.class);
 * }</pre>
 * Note that clients created solely using a {@link RpcPreprocessor} must set the {@link SessionProtocol}
 * and {@link EndpointGroup} at the very least.
 */
@UnstableApi
@FunctionalInterface
public interface RpcPreprocessor extends Preprocessor<RpcRequest, RpcResponse> {

    /**
     * A simple {@link RpcPreprocessor} which overwrites the {@link SessionProtocol},
     * {@link EndpointGroup}, and {@link EventLoop} for a request.
     */
    static RpcPreprocessor of(SessionProtocol sessionProtocol, EndpointGroup endpointGroup,
                              EventLoop eventLoop) {
        requireNonNull(sessionProtocol, "sessionProtocol");
        requireNonNull(endpointGroup, "endpointGroup");
        requireNonNull(eventLoop, "eventLoop");
        return (delegate, ctx, req) -> {
            ctx.setSessionProtocol(sessionProtocol);
            ctx.setEndpointGroup(endpointGroup);
            ctx.setEventLoop(eventLoop);
            return delegate.execute(ctx, req);
        };
    }

    /**
     * A simple {@link RpcPreprocessor} which overwrites the {@link SessionProtocol},
     * {@link EndpointGroup}, and {@link EventLoop} for a request.
     */
    static RpcPreprocessor of(SessionProtocol sessionProtocol, EndpointGroup endpointGroup) {
        requireNonNull(sessionProtocol, "sessionProtocol");
        requireNonNull(endpointGroup, "endpointGroup");
        return (delegate, ctx, req) -> {
            ctx.setSessionProtocol(sessionProtocol);
            ctx.setEndpointGroup(endpointGroup);
            return delegate.execute(ctx, req);
        };
    }
}
