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
	//1.��·���������������е�channel��
	private Selector selector;
	//2.����������
	private ByteBuffer readBuf= ByteBuffer.allocate(1024);
	
	//����д������
	private ByteBuffer  writeBuf = ByteBuffer.allocate(1024);
	
	public Server(int port){
		try{
			//1.�򿪶�·������
			this.selector = Selector.open();
			//2.�򿪷�����ͨ��
			ServerSocketChannel ssc = ServerSocketChannel.open();
			//3.���÷�����ͨ��Ϊ������ģʽ
			ssc.configureBlocking(false);
			//4.�󶨵�ַ
			ssc.bind(new InetSocketAddress(port));
			//5.�ѷ�����ͨ��ע�ᵽ��·�������ϣ����Ҽ��������¼�
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
				//1.�����ö�·��������ʼ����
				this.selector.select();
				//2.���ض�·�������Ѿ�ѡ��Ľ����
				Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
				//3.���б���
				while(keys.hasNext()){
					//4.��ȡһ��ѡ���Ԫ��
					SelectionKey key = keys.next();
					//5.ֱ�Ӵ��������Ƴ�
					keys.remove();
					//6.�������Ч��
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
	  		//1.�������ӵĵ�ַ
			InetSocketAddress address  = new InetSocketAddress("127.0.0.1",8766);
	  		//2.�������ӵ�ͨ��
			SocketChannel sc = null;
			
			try{
				//��ͨ��
				sc = SocketChannel.open();
				//����ͨ��
				sc.connect(address);
				while(true){
					//1.����һ���ֽ����飬Ȼ��ʹ��ϵͳ¼�빦��
					byte[] bytes = new byte[1024];
					System.in.read(bytes);
					
					//2.�����ݷŵ�������
					writeBuf.put(bytes);
					//3.�Ի��������и�λ
					writeBuf.flip();
					//4.д���ݵ������
					sc.write(writeBuf);
					//5.��ջ���������
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
			//1.��ջ������ɵ�����
			this.readBuf.clear();
			//2,��ȡ֮ǰע���socketͨ������
			SocketChannel sc = (SocketChannel) key.channel();
			//3.��ȡ����
			int count = sc.read(this.readBuf);
			//4.���û������
			if(count == -1){
				key.channel().close();
				key.cancel();
				return;
			}
			//5.����������ж�ȡ ��ȡ֮ǰ��Ҫ����buffer�ĸ�λ
			this.readBuf.flip();
			//6.���ݻ�������С������Ӧ��С��byte���飬���ջ�����������
			byte[] bytes = new byte[this.readBuf.remaining()];
			//7.���ջ���������
			this.readBuf.get(bytes);
			//8.��ӡ���
			String body = new String(bytes).trim();
			System.out.println("Server : " + body);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void accept(SelectionKey key){
		try{
			//1.��ȡ����ͨ��
			ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
			//2.ִ����������
			SocketChannel sc = ssc.accept();
			//��������ģʽ
			sc.configureBlocking(false);

			//1.��ջ������ɵ�����
			this.readBuf.clear();
			//3.��ȡ����
			int count = sc.read(this.readBuf);
			//4.���û������
			if(count == -1){
				//4.���ͻ��˹ܵ�ע�ᵽ��·����������
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
