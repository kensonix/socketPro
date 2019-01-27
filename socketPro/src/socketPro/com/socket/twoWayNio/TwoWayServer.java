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
	//�������ݻ�����
	private ByteBuffer sBuffer = ByteBuffer.allocate(1024);
	//�������ݻ�����
	private ByteBuffer rBuffer = ByteBuffer.allocate(1024);
	//ӳ��ͻ���channel
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
	 * �����������ˣ�����Ϊ���������󶨶˿ڣ�ע��accept�¼�
	 * Accept�¼�����������յ��ͻ�����������ʱ���������¼�
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
	 * ����������ѯ������select������һֱ����ֱ��������¼�������ʱ
	 */
	public void listen(){
		while(true){
			try{
				selector.select();
				//����ֵΪ���δ������¼���
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
			 *�ͻ������������¼���serverSocketΪ�ͻ��˽���socket���ӣ�����socketע��READ�¼���
			 *�����ͻ�������
			 *READ�¼������ͻ��˷������ݣ����ѱ������������߳���ȷ��ȡʱ���������¼� 
			 */
			severChannel = (ServerSocketChannel) selectionKey.channel();
			clientChannel = severChannel.accept();
			clientChannel.configureBlocking(false);
			clientsMap.put(clientChannel.getLocalAddress().toString().substring(1) + i++, clientChannel);
			clientChannel.register(selector, SelectionKey.OP_READ);
		}else if(selectionKey.isReadable()){
			/*
			 *Read �¼����յ��ͻ��˷������ݣ���ȡ���ݺ����ע������ͻ��� 
			 */
			clientChannel = (SocketChannel) selectionKey.channel();
			rBuffer.clear();
			count = clientChannel.read(rBuffer);
			if(count > 0){
				rBuffer.flip();
				receiveText = charset.decode(rBuffer.asReadOnlyBuffer()).toString();
				System.out.println(clientChannel.getLocalAddress().toString().substring(1)+ " : "+ receiveText);
				sBuffer.clear();
				sBuffer.put((sdf.format(new Date()) + "���������յ������Ϣ")
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
										//�����ͨ��
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






















