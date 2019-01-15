package socketPro.com.socket.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
	
	public static void main(String[] args) {
		//1.创建连接的地址
		InetSocketAddress address  = new InetSocketAddress("127.0.0.1",8765);
		//2.声明连接的通道
		SocketChannel sc = null;
		//3.建立缓冲区
		ByteBuffer writeBuf = ByteBuffer.allocate(1024);
		
		try{
			//打开通道
			sc = SocketChannel.open();
			//连接通道
			sc.connect(address);
			while(true){
				//1.定义一个字节数组，然后使用系统录入功能
				byte[] bytes = new byte[1024];
				System.in.read(bytes);
				
				//2.把数据放到缓存区
				writeBuf.put(bytes);
				//3.对缓存区进行复位
				writeBuf.flip();
				//4.写数据到服务端
				sc.write(writeBuf);
				//5.清空缓存区数据
				writeBuf.clear();
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(sc != null){
				try {
					sc.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
