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
		//读取数据
		ByteBuffer buf = ByteBuffer.allocate(1024);
		asc.read(buf,buf,new CompletionHandler<Integer,ByteBuffer>(){

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				//进行读取后，重置标志位
				attachment.flip();
				//获取读取的字节数
				String resultData = new String(attachment.array()).trim();
				System.out.println("Server ->" + "收到客户端的数据信息为" + resultData);
				String response = "服务器响应，收到了客户端发来的数据：" + resultData;
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









