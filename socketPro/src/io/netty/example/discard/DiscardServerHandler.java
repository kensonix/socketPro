package io.netty.example.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

//����˹ܵ�������
public class DiscardServerHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		((ByteBuf)msg).release();
		
//		ByteBuf in = (ByteBuf) msg;
//		try{
//			//�����߼�����
//			while(in.isReadable()){
//				System.out.print((char) in.readByte());
//				System.out.flush();
//			}
		
			ctx.write(msg);
			ctx.flush();
			//����Ŀ��滻����
//			System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII));
			
//		}finally{
//			ReferenceCountUtil.release(msg);
//		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
}
