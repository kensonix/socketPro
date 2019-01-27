package io.netty.example.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

//服务端管道处理器
public class DiscardServerHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		((ByteBuf)msg).release();
		
//		ByteBuf in = (ByteBuf) msg;
//		try{
//			//代码逻辑处理
//			while(in.isReadable()){
//				System.out.print((char) in.readByte());
//				System.out.flush();
//			}
		
			ctx.write(msg);
			ctx.flush();
			//上面的可替换代码
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
