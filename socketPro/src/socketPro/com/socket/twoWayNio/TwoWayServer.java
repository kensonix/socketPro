package socketPro.com.socket.twoWayNio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TwoWayServer {
	private int port = 6001;
	//发送数据缓冲区
	private ByteBuffer sBuffer = ByteBuffer.allocate(1024);
	//接收数据缓存区
	private ByteBuffer rBuffer = ByteBuffer.allocate(1024);
	//映射客户端channel
	private String sendText;
	private Map<String,SocketChannel> clientsMap = new HashMap<String,SocketChannel>();
	private Charset charset = Charset.forName("UTF-8");
	private Selector selector;
	private SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss",java.util.Locale.US);
	int  i =0;
	public TwoWayServer(){
		try{
			init();
			listen();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 启动服务器端，配置为非阻塞，绑定端口，注册accept事件
	 * Accept事件：当服务端收到客户端连接请求时，触发该事件
	 * @throws Exception
	 */
	private void init() throws Exception{
		ServerSocketChannel ssc = ServerSocketChannel.open();
		ssc.configureBlocking(false);
		ServerSocket serverSocket = ssc.socket();
		serverSocket.bind(new InetSocketAddress(port));
		selector = Selector.open();
		ssc.register(selector, SelectionKey.OP_ACCEPT);
		System.out.println("server start on port : "+port);;
	}
	/**
	 * 服务器端轮询监听，select方法会一直阻塞直到有相关事件发生或超时
	 */
	public void listen(){
		while(true){
			try{
				selector.select();
				//返回值为本次触发的事件数
				Set<SelectionKey> selectionKeys = selector.selectedKeys();
				for(SelectionKey key : selectionKeys){
					handle(key);
				}
				selectionKeys.clear();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void handle(SelectionKey selectionKey) throws IOException{
		ServerSocketChannel severChannel = null;
		SocketChannel  clientChannel = null;
		String receiveText = null;
		int count =0;
		if(selectionKey.isAcceptable()){
			/*
			 *客户端请求连接事件，serverSocket为客户端建立socket连接，将此socket注册READ事件，
			 *监听客户端输入
			 *READ事件：当客户端发来数据，并已被服务器控制线程正确读取时，触发该事件 
			 */
			severChannel = (ServerSocketChannel) selectionKey.channel();
			clientChannel = severChannel.accept();
			clientChannel.configureBlocking(false);
			clientsMap.put(clientChannel.getLocalAddress().toString().substring(1) + i++, clientChannel);
			clientChannel.register(selector, SelectionKey.OP_READ);
		}else if(selectionKey.isReadable()){
			/*
			 *Read 事件，收到客户端发送数据，读取数据后继续注册监听客户端 
			 */
			clientChannel = (SocketChannel) selectionKey.channel();
			rBuffer.clear();
			count = clientChannel.read(rBuffer);
			if(count > 0){
				rBuffer.flip();
				receiveText = charset.decode(rBuffer.asReadOnlyBuffer()).toString();
				System.out.println(clientChannel.getLocalAddress().toString().substring(1)+ " : "+ receiveText);
				sBuffer.clear();
				sBuffer.put((sdf.format(new Date()) + "服务器接收到你的消息")
						.getBytes("UTF-8"));
				sBuffer.flip();
				clientChannel.write(sBuffer);
				
				new Thread(){
					@Override
					public void run(){
							while(true){
								try{
								InputStreamReader input = new InputStreamReader(System.in);
								BufferedReader br = new BufferedReader(input);
								sendText = br.readLine();
								
								if(!clientsMap.isEmpty()){
									for(Map.Entry<String, SocketChannel> entry : clientsMap.entrySet()){
										SocketChannel temp = entry.getValue();
										String name = entry.getKey();
										
										sBuffer.clear();
										sBuffer.put((name+":" +sendText).getBytes("UTF-8"));
										sBuffer.flip();
										//输出到通道
										temp.write(sBuffer);
									}
								}
								
							}catch(IOException e){
								e.printStackTrace();
								break;
						}
					  }
					}
				}.start();
				clientChannel = (SocketChannel) selectionKey.channel();
				clientChannel.register(selector,SelectionKey.OP_READ);
			}
		}
	}
	
	public static void main(String[] args) {
		new TwoWayServer();
	}
}






















