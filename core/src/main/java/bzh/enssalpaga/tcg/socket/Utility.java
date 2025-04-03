package bzh.enssalpaga.tcg.socket;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Utility {
	public static byte[] concatenate(byte[] byte0, byte[] byte1) {
		byte[] allByteArray = new byte[byte0.length + byte1.length];

		ByteBuffer buff = ByteBuffer.wrap(allByteArray);
		buff.put(byte0);
		buff.put(byte1);

		return buff.array();
	}

	public static byte[] concatenate(byte byte0, byte[] byte1) {
		byte[] allByteArray = new byte[1 + byte1.length];

		ByteBuffer buff = ByteBuffer.wrap(allByteArray);
		buff.put(byte0);
		buff.put(byte1);

		return buff.array();
	}

	public static byte[] get_after(byte[] original, int i) {
		// slice from index 5 to index 9
		return Arrays.copyOfRange(original, i, original.length);
	}

	public static byte[] get_before(byte[] original, int i) {
		// slice from index 5 to index 9
		return Arrays.copyOfRange(original, 0, i);
	}
}
