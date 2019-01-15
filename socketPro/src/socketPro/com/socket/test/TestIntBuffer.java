package socketPro.com.socket.test;

import java.nio.IntBuffer;

public class TestIntBuffer {
	public static void main(String[] args) {
		IntBuffer intBuffer = IntBuffer.allocate(10);
		int[] arr =new int[] {1,2,3,4,5};
		intBuffer.put(arr, 1, 3);
		System.out.println(intBuffer);
		intBuffer.position(2);
		intBuffer.flip(); 
		System.out.println(intBuffer);
		
	}
}
