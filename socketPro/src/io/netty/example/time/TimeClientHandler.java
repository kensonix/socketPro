package io.netty.example.time;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {
	private ByteBuf buf;
	
	// channelHandler �������������ڼ�����������
	//handlerAdded �� handlerRemoved
	@Override
	public void handlerAdded(ChannelHandlerContext ctx){
		buf = ctx.alloc().buffer(4);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx){
		buf.release();
		buf = null;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx,Object msg){
		ByteBuf m = (ByteBuf) msg;
		//�����н��յ������ۼӵ�buf��
		buf.writeBytes(m);
		
		if(buf.readableBytes() >= 4){
			long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L; 
			System.out.println(new Date(currentTimeMillis));
			ctx.close();
		}
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		cause.printStackTrace();
		ctx.close();
	}
}
