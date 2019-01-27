package socketPro.com.socket.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;

public class ServerCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,AIOServer>{

	@Override
	public void completed(AsynchronousSocketChannel asc, AIOServer attachment) {
		attachment.assc.accept(attachment,this);
		read(asc);
	}

	@Override
	public void failed(Throwable exc, AIOServer attachment) {
		exc.printStackTrace();
	}
	private void read(final AsynchronousSocketChannel asc){
		//��ȡ����
		ByteBuffer buf = ByteBuffer.allocate(1024);
		asc.read(buf,buf,new CompletionHandler<Integer,ByteBuffer>(){

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				//���ж�ȡ�����ñ�־λ
				attachment.flip();
				//��ȡ��ȡ���ֽ���
				String resultData = new String(attachment.array()).trim();
				System.out.println("Server ->" + "�յ��ͻ��˵�������ϢΪ" + resultData);
				String response = "��������Ӧ���յ��˿ͻ��˷��������ݣ�" + resultData;
				write(asc,response);
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				exc.printStackTrace();
			}
		});
	}
	
	private void write(AsynchronousSocketChannel asc,String response){
		try{
			ByteBuffer buf = ByteBuffer.allocate(1024);
			buf.put(response.getBytes());
			buf.flip();
			asc.write(buf).get();
		}catch(InterruptedException e){
			e.printStackTrace();
		}catch(ExecutionException e){
			e.printStackTrace();
		}
	}

}









