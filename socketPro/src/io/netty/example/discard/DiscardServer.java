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
		 * NioEventLoopGroup ����io�����Ķ��¼�ѭ��
		 * bossGroup������������
		 * workerGroup���������
		 */
		EventLoopGroup bossGroup =  new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			//netty�з�������������������server
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup,workerGroup)
			//��ʼ��һ���µĹܵ����տͻ�����������
			.channel(NioServerSocketChannel.class)
			//���÷���˵Ĵ�����
			.childHandler(new ChannelInitializer<SocketChannel>(){
				@Override
				public void initChannel(SocketChannel ch) throws Exception{
					ch.pipeline().addLast(new DiscardServerHandler());
				}
			})
			//�������˹ܵ�����
			.option(ChannelOption.SO_BACKLOG, 128)
			//�ͻ��˹ܵ�����
			.childOption(ChannelOption.SO_KEEPALIVE, true);
			
			//�������˿ںͽ����������������һֱ����
			ChannelFuture f =b.bind(port).sync();
			//�ȴ�ֱ����������socket�ر�
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





