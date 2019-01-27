package socketPro.com.socket.twoWayNio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class TwoWayClient {
	//发送数据缓冲区
	private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);
	//接收数据缓冲区
	private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);
	//服务器端地址
	private InetSocketAddress serverAddr;
	private Selector selector;
	private SocketChannel clientChannel;
	private String receiveText;
	private String sendText;
	private int count = 0;
	private Charset charset = Charset.forName("UTF-8");
	private SimpleDateFormat sdf = new SimpleDateFormat(""
			+ "YYYY-MM-dd HH:mm:ss",java.util.Locale.US);
	
	public TwoWayClient(){
		serverAddr = new InetSocketAddress("localhost",6001);
		init();
	}
	public void init(){
		try{
			/*
			 * 客户端向服务器发起连接请求
			 */
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			selector = Selector.open();
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
			socketChannel.connect(serverAddr);
			/*
			 * 轮询监听客户端上注册事件的发生
			 */
			while(true){
				selector.select();
				Set<SelectionKey> keySet = selector.selectedKeys();
				for(final SelectionKey key : keySet)
					handle(key);
				keySet.clear();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void handle(SelectionKey selectionKey) throws IOException,ParseException{
		if(selectionKey.isConnectable()){
			//连接建立事件，已成功连接至服务器
			clientChannel = (SocketChannel) selectionKey.channel();
			if(clientChannel.isConnectionPending()){
				clientChannel.finishConnect();
				System.out.println("connect success!");
				sBuffer.clear();
				sBuffer.put((sdf.format(new Date()) 
						+ "connected!你好").getBytes("UTF-8"));
				sBuffer.flip();
				//发送信息至服务器
				clientChannel.write(sBuffer);
				/*
				 * 启动线程一直监听客户端输入，有信息输入则发送到服务器端
				 * 因为输入流是阻塞的，所以单独线程监听
				 */
				new Thread( () -> {
					while(true){
						try{
							sBuffer.clear();
							InputStreamReader input = new InputStreamReader(System.in);
							BufferedReader br = new BufferedReader(input);
							sendText = br.readLine();
							sBuffer.put(sendText.trim().getBytes("UTF-8"));
							sBuffer.flip();
							clientChannel.write(sBuffer);
						}catch(IOException e){
							e.printStackTrace();
							break;
						}
					}
				}).start();
			}
			//注册读事件
			clientChannel.register(selector,SelectionKey.OP_READ);
		}else if(selectionKey.isReadable()){
			/*
			 * 读事件触发
			 * 有从服务器端发送过来的信息，读取输出到屏幕后，继续注册读事件
			 * 监听服务器发送信息
			 */
			clientChannel = (SocketChannel) selectionKey.channel();
			count = clientChannel.read(rBuffer);
			if(count > 0){
//				 receiveText = new String( rBuffer.array(),0,count); 
				 
					rBuffer.flip();
					receiveText = charset.decode(rBuffer.asReadOnlyBuffer()).toString();
				 
				System.out.println(receiveText);
				clientChannel = (SocketChannel) selectionKey.channel();
				clientChannel.register(selector,SelectionKey.OP_READ);
				rBuffer.clear();
 			}
		}
	}
	public static void main(String[] args) {
		new TwoWayClient();
	}
}






