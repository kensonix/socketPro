package socketPro.com.socket.nio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server implements Runnable{
	//1.多路复用器（管理所有的channel）
	private Selector selector;
	//2.建立缓冲区
	private ByteBuffer readBuf= ByteBuffer.allocate(1024);
	
	//建立写缓冲区
	private ByteBuffer  writeBuf = ByteBuffer.allocate(1024);
	
	public Server(int port){
		try{
			//1.打开多路复用器
			this.selector = Selector.open();
			//2.打开服务器通道
			ServerSocketChannel ssc = ServerSocketChannel.open();
			//3.设置服务器通道为非阻塞模式
			ssc.configureBlocking(false);
			//4.绑定地址
			ssc.bind(new InetSocketAddress(port));
			//5.把服务器通道注册到多路复用器上，并且监听阻塞事件
			ssc.register(this.selector, SelectionKey.OP_ACCEPT);
			System.out.println("Server start, port : "+ port);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		while(true){
			try{
				//1.必须让多路复用器开始监听
				this.selector.select();
				//2.返回多路复用器已经选择的结果集
				Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
				//3.进行遍历
				while(keys.hasNext()){
					//4.获取一个选择的元素
					SelectionKey key = keys.next();
					//5.直接从容器中移除
					keys.remove();
					//6.如果是有效的
					if(key.isValid()){
						if(key.isAcceptable()){
							this.accept(key);
						}else if(key.isReadable()){
							this.read(key);
						}else if(key.isWritable()){
							this.write(key);
						}
					}
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
  private void write(SelectionKey key){
	  		//1.创建连接的地址
			InetSocketAddress address  = new InetSocketAddress("127.0.0.1",8766);
	  		//2.声明连接的通道
			SocketChannel sc = null;
			
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
	
	private void read(SelectionKey key){
		try{
			//1.清空缓冲区旧的数据
			this.readBuf.clear();
			//2,获取之前注册的socket通道对象
			SocketChannel sc = (SocketChannel) key.channel();
			//3.读取数据
			int count = sc.read(this.readBuf);
			//4.如果没有数据
			if(count == -1){
				key.channel().close();
				key.cancel();
				return;
			}
			//5.有数据则进行读取 读取之前需要进行buffer的复位
			this.readBuf.flip();
			//6.根据缓存区大小建立相应大小的byte数组，接收缓冲区的数据
			byte[] bytes = new byte[this.readBuf.remaining()];
			//7.接收缓冲区数据
			this.readBuf.get(bytes);
			//8.打印结果
			String body = new String(bytes).trim();
			System.out.println("Server : " + body);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void accept(SelectionKey key){
		try{
			//1.获取服务通道
			ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
			//2.执行阻塞方法
			SocketChannel sc = ssc.accept();
			//设置阻塞模式
			sc.configureBlocking(false);

			//1.清空缓冲区旧的数据
			this.readBuf.clear();
			//3.读取数据
			int count = sc.read(this.readBuf);
			//4.如果没有数据
			if(count == -1){
				//4.将客户端管道注册到多路复用器上面
				sc.register(this.selector, SelectionKey.OP_READ);
			}else{
				sc.register(this.selector, SelectionKey.OP_WRITE);
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new Thread(new Server(8765)).start();
	}
	
}
