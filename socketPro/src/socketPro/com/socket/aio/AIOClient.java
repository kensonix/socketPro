package socketPro.com.socket.aio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class AIOClient implements Runnable{
	private AsynchronousSocketChannel asc;
	public AIOClient() throws Exception{
		asc = AsynchronousSocketChannel.open();
	}
	public void connect(){
		asc.connect(new InetSocketAddress("127.0.0.1",8765));
	}
	public void run(){
		while(true){
			
		}
	}
	public void write(String request){
		try{
			asc.write(ByteBuffer.wrap(request.getBytes())).get();
			read();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void read(){
		ByteBuffer buf = ByteBuffer.allocate(1024);
		try{
			asc.read(buf).get();
			buf.flip();
			byte[] respByte = new byte[buf.remaining()];
			buf.get(respByte);
			System.out.println(new String(respByte,"utf-8").trim());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		AIOClient c1  = new AIOClient();
		c1.connect();
		
		AIOClient c2  = new AIOClient();
		c2.connect();
		
		AIOClient c3  = new AIOClient();
		c3.connect();
		
		c1.write("aaa");
		c2.write("bbbb");
		c3.write("ccccc");
	}
}







