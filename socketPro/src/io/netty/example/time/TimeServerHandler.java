package io.netty.example.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter{
	/**
	 * 当服务器和客户端建立连接
	 * 向客户端发送一个32位的长整数后，马上关闭连接
	 */
	@Override
	public void channelActive(final ChannelHandlerContext ctx){
		final ByteBuf time = ctx.alloc().buffer(4);
		time.writeInt((int) (System.currentTimeMillis() /100L + 2208988800L));
		
		//writeAndFlush写完后，filp将buffer复位
		//返回值是future，异步调用模式
		final ChannelFuture f = ctx.writeAndFlush(time);
		f.addListener(new ChannelFutureListener(){

			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				assert f == future;
				ctx.close();
			}
			
		});
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}
}
