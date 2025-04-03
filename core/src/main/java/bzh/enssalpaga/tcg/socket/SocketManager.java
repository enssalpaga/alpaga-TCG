package bzh.enssalpaga.tcg.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

//v: e.c.1.5 (beta cannot be copied and release cannot be modified) (last version is on the notikal project)
public class SocketManager {
	private Socket socket;
	private SocketListener socketListener;
	private ClientThread clientThread;
	private String ip;
	private int port;

	private CryptoManager cryptoManager;
	// random nonce to avoid repetition in symetric cryptography
	// this nonce change for every coding msg and it send with the msg
	private byte[] nonce;

	private boolean isClosing = false;

	public void startClient(SocketListener socketListener, String ip, int port, InputStream keyFile) {
		boolean work = true;
		// GENERATE NEW KEYPAIR
		try {
			cryptoManager = new CryptoManager(keyFile);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			work = false;
		}

		// connect to the server
		if(work){
			this.socketListener = socketListener;
			this.ip = ip;
			this.port = port;
			this.clientThread = new ClientThread();
			this.clientThread.start();
		}
	}

	// Listener part
	public void onQuit() {
		if(!this.isClosing) {
			this.isClosing = true;
			try {
                if(socket != null){
                    socket.close();
                }
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
                this.socketListener.onQuit();
            }
		}
	}

	private void onJoin(){
		this.socketListener.onJoin();
	}

	private void onMessage(String event, String message) {
		this.socketListener.onMessage(event, message);
	}

	private class ClientThread extends Thread{

		public void run() {
			try {
				socket = new Socket(ip, port);
			} catch (IOException e) {
				e.printStackTrace();
				onQuit();
                return;
			}
			// send public key
			byte[] ping = cryptoManager.generatePublic();
			try {
				socket.getOutputStream().write(ping);
			} catch (IOException e) {
				e.printStackTrace();
				onQuit();
			};

			// check that this is the server and save the asymmetric key
			// split the byte into the nonce and the rest
			//the deprypt function need 1024 bytes so we need to set exactly 1057 bytes
			byte[] read = new byte[1057];
			try {
				socket.getInputStream().read(read);
				read = this.slice(read);
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				cryptoManager.saveKey(read);
			} catch (Exception e) {
				e.printStackTrace();
				onQuit();
			}

			onJoin();
			while(socket.isConnected()){
				String readString = this.read();
				this.processMessage(readString);
			}
		}

		// let the read methode decoding it-self the message
		private String read(){
			byte[] read = new byte[2048];
			String readString = null;
			try {
				socket.getInputStream().read(read);
				readString = new String(read).trim();
			} catch (IOException e) {
				if(!isClosing) {
					e.printStackTrace();
					onQuit();
				}
			}
			return readString;
		}

		private byte[] slice(byte[] original) {
			// slice from index 5 to index 9
			nonce = Arrays.copyOfRange(original, 0, 32);
			return Arrays.copyOfRange(original, 32, original.length);
		}

		private void processMessage(String readString) {
			if(readString != null && readString.length() > 0) {
				// we decrypt the msg if it is crypted
				if(readString.charAt(0) == '*') {
					readString = readString.substring(1);
					try {
						readString = cryptoManager.decrypt(readString, nonce);
					} catch (Exception e) {
						e.printStackTrace();
						onQuit();
					}
				}
				if(readString.contains("[") && readString.contains("]")) {
	    			readString = readString.substring(1);
	    			int index = readString.indexOf(']');
	    			String event = readString.substring(0, index);
	    			String message = readString.substring(index+1);
					onMessage(event, message);
	    		}else {
	    			onQuit();
	    		}
			}else {
				onQuit();
			}
		}
	}

	// Sender part
	public void send(String event, String request){
		send(event, request, false);
	}

	public void send(String event, String request, boolean encoded){
		String msg = "[" + event + "]" + request;
		// encoded part
		if (encoded){
			try {
				// I add a star to say that it is an encoded message
				msg = "*" + cryptoManager.encrypt(msg, nonce);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		}
		SendThread sendThread = new SendThread((msg).getBytes());
		sendThread.start();
	}

	private class SendThread extends Thread{
		private byte[] bytes;

		private SendThread(byte[] bytes){
			this.bytes = bytes;
		}

		public void run() {
			if (socket != null && !socket.isClosed()){
				try {
					socket.getOutputStream().write(this.bytes);
				} catch (IOException e) {
					e.printStackTrace();
					onQuit();
				}
			}else {
				onQuit();
			}
		}
	}
}
