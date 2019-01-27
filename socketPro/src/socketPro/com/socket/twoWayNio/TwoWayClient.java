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
	//�������ݻ�����
	private static ByteBuffer sBuffer = ByteBuffer.allocate(1024);
	//�������ݻ�����
	private static ByteBuffer rBuffer = ByteBuffer.allocate(1024);
	//�������˵�ַ
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
			 * �ͻ����������������������
			 */
			SocketChannel socketChannel = SocketChannel.open();
			socketChannel.configureBlocking(false);
			selector = Selector.open();
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
			socketChannel.connect(serverAddr);
			/*
			 * ��ѯ�����ͻ�����ע���¼��ķ���
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
			//���ӽ����¼����ѳɹ�������������
			clientChannel = (SocketChannel) selectionKey.channel();
			if(clientChannel.isConnectionPending()){
				clientChannel.finishConnect();
				System.out.println("connect success!");
				sBuffer.clear();
				sBuffer.put((sdf.format(new Date()) 
						+ "connected!���").getBytes("UTF-8"));
				sBuffer.flip();
				//������Ϣ��������
				clientChannel.write(sBuffer);
				/*
				 * �����߳�һֱ�����ͻ������룬����Ϣ�������͵���������
				 * ��Ϊ�������������ģ����Ե����̼߳���
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
			//ע����¼�
			clientChannel.register(selector,SelectionKey.OP_READ);
		}else if(selectionKey.isReadable()){
			/*
			 * ���¼�����
			 * �дӷ������˷��͹�������Ϣ����ȡ�������Ļ�󣬼���ע����¼�
			 * ����������������Ϣ
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






