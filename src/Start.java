import java.io.File;
import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

import client.Client;

import remote.RemoteInterface;
import server.RemoteNewsObject;
import server.RmiServer;
import utils.PropertiesReader;
import utils.ServerClientUtils;

public final class Start {

	// used just for test
	public static void normalClient(Class<?> klass, int clientID,
			String clientType, String clientAddress, String serverAddress,
			int portNumber, int numberOfAcesses, String objectName)
			throws IOException, InterruptedException, NotBoundException {

		Client client = new Client(clientID, clientType, clientAddress,
				serverAddress, portNumber, numberOfAcesses, objectName);
		client.run();
	}

	public static void startNewClient(Class<?> klass, int clientID,
			String clientType, String clientAddress, String serverAddress,
			int portNumber, int numberOfAcesses, String objectName)
			throws IOException, InterruptedException {

		String javaBin = System.getProperty("java.home") + File.separator
				+ "bin" + File.separator + "java";
		String classpath = System.getProperty("java.class.path");
		String className = klass.getCanonicalName();

		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath,
				className, Integer.toString(clientID), clientType,
				clientAddress, serverAddress, Integer.toString(portNumber),
				Integer.toString(numberOfAcesses), objectName);
		Process process = builder.start();
		process.waitFor();
		Scanner in = new Scanner(process.getInputStream());
		while (in.hasNext())
			System.out.println("-- " + in.nextLine());
		in.close();
		System.out.println(" process done with exit Value " + process.exitValue());
	}


	public static void main(String[] args) throws IOException,
			InterruptedException, AlreadyBoundException, NotBoundException {

		PropertiesReader propReader = new PropertiesReader();

		String serverAddress = propReader.getServerAddress();
		int portNumber = propReader.getRmiRegisteryPortNum(); // read RMI port
																// number
																// instead of
																// server's
		int numberOfAcesses = propReader.getAccessNum();
		int readers = propReader.getReadersNum();
		int writers = propReader.getWritersNum();

		// create Server
		new RmiServer(serverAddress, ServerClientUtils.REGISTRY_NAME, portNumber,
				numberOfAcesses * (readers + writers));

		System.out.println("Start: Start Clients");
		int clientID = 0;
		// create Clients
		for (int i = 0; i < readers; i++) {
			System.out.println("Start: Start new Client");
			startNewClient(Client.class, ++clientID, ServerClientUtils.READER,
					propReader.getNextReader(), serverAddress, portNumber,
					numberOfAcesses, ServerClientUtils.REGISTRY_NAME);
		}
		for (int i = 0; i < writers; i++) {
			System.out.println("Start: Start new Client");
			startNewClient(Client.class, ++clientID, ServerClientUtils.WRITER,
					propReader.getNextWriter(), serverAddress, portNumber,
					numberOfAcesses, ServerClientUtils.REGISTRY_NAME);
		}

	}
}
