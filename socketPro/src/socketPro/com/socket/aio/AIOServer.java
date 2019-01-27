package socketPro.com.socket.aio;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AIOServer {
	//�̳߳�
	private ExecutorService executorService;
	//�߳���
	private AsynchronousChannelGroup threadGroup;
	//������ͨ��
	public AsynchronousServerSocketChannel assc;
	
	public AIOServer(int port){
		try{
			//����һ���̳߳�
			executorService = Executors.newCachedThreadPool();
			//�����߳���
			threadGroup = AsynchronousChannelGroup.withCachedThreadPool(executorService, 1);
			//����������ͨ��
			assc = AsynchronousServerSocketChannel.open(threadGroup);
			//���а�
			assc.bind(new InetSocketAddress(port));
			
			System.out.println("Server start, port : " + port);
			//��������
			assc.accept(this,new ServerCompletionHandler());
			//һֱ�������÷�����ֹͣ
			Thread.sleep(Integer.MAX_VALUE);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		AIOServer server = new AIOServer(8765);
	}
}
