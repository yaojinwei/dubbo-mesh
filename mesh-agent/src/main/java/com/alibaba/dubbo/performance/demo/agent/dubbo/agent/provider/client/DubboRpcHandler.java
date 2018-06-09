package com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.client;

import com.alibaba.dubbo.performance.demo.agent.dubbo.agent.provider.server.ProviderAgentHandler;
import com.alibaba.dubbo.performance.demo.agent.protocol.dubbo.DubboRpcResponse;
import com.alibaba.dubbo.performance.demo.agent.protocol.pb.DubboMeshProto;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DubboRpcHandler extends SimpleChannelInboundHandler<DubboRpcResponse> {

    static final Logger logger = LoggerFactory.getLogger(DubboRpcHandler.class);

    public DubboRpcHandler() {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DubboRpcResponse msg) throws Exception {
        process(msg);
    }

    private void process(DubboRpcResponse msg) {
        Promise<DubboMeshProto.AgentResponse> promise = ProviderAgentHandler.promiseHolder.get().remove(msg.getRequestId());
        if (promise != null) {
            promise.trySuccess(messageToMessage(msg));
        }
    }

    private DubboMeshProto.AgentResponse messageToMessage(DubboRpcResponse dubboRpcResponse) {
        return DubboMeshProto.AgentResponse.newBuilder()
                .setRequestId(dubboRpcResponse.getRequestId())
                .setHash(ByteString.copyFrom(dubboRpcResponse.getBytes()))
                .build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
