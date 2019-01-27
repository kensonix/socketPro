package io.netty.example.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter{
	/**
	 * ���������Ϳͻ��˽�������
	 * ��ͻ��˷���һ��32λ�ĳ����������Ϲر�����
	 */
	@Override
	public void channelActive(final ChannelHandlerContext ctx){
		final ByteBuf time = ctx.alloc().buffer(4);
		time.writeInt((int) (System.currentTimeMillis() /100L + 2208988800L));
		
		//writeAndFlushд���filp��buffer��λ
		//����ֵ��future���첽����ģʽ
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
