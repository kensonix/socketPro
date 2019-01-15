package socketPro.com.socket.nio.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
	
	public static void main(String[] args) {
		//1.�������ӵĵ�ַ
		InetSocketAddress address  = new InetSocketAddress("127.0.0.1",8765);
		//2.�������ӵ�ͨ��
		SocketChannel sc = null;
		//3.����������
		ByteBuffer writeBuf = ByteBuffer.allocate(1024);
		
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

}
