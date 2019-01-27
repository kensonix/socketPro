package io.netty.example.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {
	private int port;
	public DiscardServer(int port){
		this.port = port;
	}
	
	public void run() throws Exception{
		/*
		 * NioEventLoopGroup 处理io操作的多事件循环
		 * bossGroup接收连接请求
		 * workerGroup处理穷操作
		 */
		EventLoopGroup bossGroup =  new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			//netty中服务器的配置器，配置server
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup,workerGroup)
			//初始化一个新的管道接收客户端连接请求
			.channel(NioServerSocketChannel.class)
			//设置服务端的处理器
			.childHandler(new ChannelInitializer<SocketChannel>(){
				@Override
				public void initChannel(SocketChannel ch) throws Exception{
					ch.pipeline().addLast(new DiscardServerHandler());
				}
			})
			//服务器端管道设置
			.option(ChannelOption.SO_BACKLOG, 128)
			//客户端管道设置
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			//绑定启动端口和接收连接请求，这里会一直阻塞
			ChannelFuture f =b.bind(port).sync();
			//等待直到服务器端socket关闭
			f.channel().closeFuture().sync();
		}finally{
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
	
	public static void main(String[] args) throws Exception {
		int port = 8080;
		if(args.length > 0)
			port = Integer.parseInt(args[0]);
		new DiscardServer(port).run();
	}
}





