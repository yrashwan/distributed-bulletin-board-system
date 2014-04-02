package client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.Random;

import remote.RemoteInterface;

import utils.ServerClientUtils;

public final class Client {

	private static final int SLEEP_TIME = 10000;

	private final int id;
	private final String type;
	@SuppressWarnings("unused")
	private final String address;
	private final String serverAddress;
	private final int serverPortNumber;
	private final int accessNumber;

	private final RemoteInterface remoteObject;

	public Client(int id, String type, String address, String serverAddress,
			int serverPortNumber, int accessNumber, String objectName)
			throws RemoteException, NotBoundException {
		this.id = id;
		this.type = type;
		this.address = address;
		this.serverAddress = serverAddress;
		this.serverPortNumber = serverPortNumber;
		this.accessNumber = accessNumber;

		// get registry on the serverAdress 
		Registry registry = LocateRegistry.getRegistry(serverAddress, serverPortNumber);
//		Registry registry = LocateRegistry.getRegistry(serverPortNumber);
		System.out.println(registry);
		// currently we use the same reference, try to process it every time
		this.remoteObject = (RemoteInterface) registry.lookup(objectName);
	}

	public void run() throws InterruptedException, UnknownHostException,
			IOException {

		// PrintWriter logWriter = new PrintWriter(new File("log" + id +
		// ".log"));
		// logWriter.write("Client Type: " + type + "\n");
		// logWriter.write("Client Name: " + id + "\n");
		// logWriter.write("rSeq\tsSeq\toVal\n");

		int accessCounter = accessNumber;
		while (accessCounter-- > 0) {
			System.out.println("Client: try to connect to Server "
					+ serverAddress + " " + serverPortNumber);

			final int proccessedNews;
			// TODO get string 'response' from the server just like the version#1
			if (type.equalsIgnoreCase(ServerClientUtils.READER)) {
				// reader
				proccessedNews = remoteObject.getNews(id);
				System.out.println("Client " + id + " read " + proccessedNews);
			} else {
				// writer
				proccessedNews = new Random().nextInt(100);
				remoteObject.setNews(id, proccessedNews);
				System.out.println("Client " + id + " write " + proccessedNews);
			}

			// System.out.println("Client: try to read");
			// String readLine = new BufferedReader(new InputStreamReader(
			// clientSocket.getInputStream())).readLine();
			// logWriter.write(readLine.replaceAll(" ", "\t\t") + "\n");

			if (accessCounter != 0) {
				Thread.sleep(new Random().nextInt(SLEEP_TIME));
			}
		}
		// logWriter.close();
	}

	public static void main(String[] args) {
		try {
			System.out.println("Client: Receiving Args "
					+ Arrays.toString(args));
			Client client = new Client(Integer.valueOf(args[0]), args[1],
					args[2], args[3], Integer.valueOf(args[4]),
					Integer.valueOf(args[5]), args[6]);
			client.run();
		} catch (Exception e) {
			System.out.print("WTF "+ e.getMessage());
			throw new RuntimeException();
		}
	}

}
