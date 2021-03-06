package pl.regzand.contestserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {

	public final ContestServer server;
	public final Socket socket;
	public final int UID;

	protected PrintWriter out;
	protected BufferedReader in;
	protected boolean open = true;

	/**
	 * Creates new client on specific server and socket
	 * @param server - server that this client belongs to
	 * @param socket - socket that this client belongs to
	 */
	public Client(int UID, ContestServer server, Socket socket) {
		this.UID = UID;
		this.server = server;
		this.socket = socket;

		super.setName("ContestClient-"+UID);
		super.setPriority(Thread.NORM_PRIORITY+1);
	}

	/**
	 * Runs client thread - starts listening fo commands for client
	 */
	public void run(){
		
		// creating I/O streams
		try{
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}catch(IOException e){
			System.err.println("Could not create I/O streams for connection from " + socket.getRemoteSocketAddress().toString());
			return;
		}
		
		// waiting for commands
		try {
			String input;
			while(this.open && (input = in.readLine()) != null){
				server.getCommandsHandler().handleCommand(this, input);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Could not read line from connection");
			e.printStackTrace();
		}
		
		// closing connection
		this.server.closeConnetion(this);
	}

	/**
	 * Cleanly closes connection with client
	 */
	public void close(){
		System.out.println("Client "+this.socket.getRemoteSocketAddress().toString()+" disconnected");
		try {
			this.open = false;
			this.out.close();
			this.in.close();
			this.socket.close();
		} catch (IOException e) {
			System.err.println("An error occured during closing I/O streams");
			e.printStackTrace();
		}
	}

	/**
	 * Sends message to client
	 * @param text - message to send
	 */
	public void send(String text){
		if(open)
			out.println(text);
	}

}
