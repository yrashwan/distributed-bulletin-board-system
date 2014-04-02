import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Random;

public final class Client {

	private static final int SLEEP_TIME = 10000;

	private final int id;
	private final String type;
	@SuppressWarnings("unused")
	private final String address;
	private final String serverAddress;
	private final int serverPortNumber;
	private final int accessNumber;

	public Client(int id, String type, String address, String serverAddress,
			int serverPortNumber, int accessNumber) {
		this.id = id;
		this.type = type;
		this.address = address;
		this.serverAddress = serverAddress;
		this.serverPortNumber = serverPortNumber;
		this.accessNumber = accessNumber;
	}

	public void run() throws InterruptedException, UnknownHostException,
			IOException {
		// TODO send/receive to/from server each random time

		PrintWriter logWriter = new PrintWriter(new File("log" + id + ".log"));
		logWriter.write("Client Type: " + type + "\n");
		logWriter.write("Client Name: " + id + "\n");
		logWriter.write("rSeq\tsSeq\toVal\n");

		int accessCounter = accessNumber;
		while (accessCounter-- > 0) {
			System.out.println("Client: try to connect to Server "
					+ serverAddress + " " + serverPortNumber);
			Socket clientSocket = new Socket(serverAddress, serverPortNumber);
			System.out.println("Client: initialized");
			DataOutputStream outToServer = new DataOutputStream(
					clientSocket.getOutputStream());

			final String sendLine;
			if(type.equalsIgnoreCase(ServerClientUtils.READER)){
				// reader
				sendLine = type + " " + id;
			}else{
				// writer
				sendLine = type + " " + id + " " + new Random().nextInt(100);
			}
			System.out.println("Client: write line " + sendLine);
			outToServer.writeBytes(sendLine+"\n");
			
			System.out.println("Client: try to read");
			String readLine = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream())).readLine();
			String ln[] = readLine.split(" ");
			String seqNo = ln[0];
			String news = ln[1];
			logWriter.write(readLine.replaceAll(" ", "\t\t") + "\n");

			clientSocket.close();
			if (accessCounter != 0) {
				Thread.sleep(new Random().nextInt(SLEEP_TIME));
			}
		}
		logWriter.close();
	}

	public static void main(String[] args) {
		try {
			System.out.println("Client: Receiving Args "
					+ Arrays.toString(args));
			Client client = new Client(Integer.valueOf(args[0]), args[1],
					args[2], args[3], Integer.valueOf(args[4]),
					Integer.valueOf(args[5]));
			client.run();
		} catch (Exception e) {
			System.err.print(e.getMessage());
			throw new RuntimeException();
		}
	}

}
