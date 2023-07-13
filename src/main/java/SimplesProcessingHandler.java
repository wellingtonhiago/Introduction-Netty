import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

                                              //Manipulador de entrada do canal
public class SimplesProcessingHandler extends ChannelInboundHandlerAdapter {

    private ByteBuf tmp;

    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Handler added");
        tmp = ctx.alloc().buffer(4);
    }

    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Handler removed");
        tmp.release();
        tmp = null;
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m =  (ByteBuf) msg;
        tmp.writeBytes(m);
        m.release();

        if (tmp.readableBytes() >= 4) {
            //pedido de processamento
            RequestData requestData = new RequestData();
            requestData.setIntValue(tmp.readInt());

            ResponseData responseData = new ResponseData();
            responseData.setIntValue(requestData.getIntValue() * 2);

            ChannelFuture future = ctx.writeAndFlush(responseData);
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
