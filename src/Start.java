import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public final class Start {
	
	public static void startNewClient(Class<?> klass, int clientID, String clientType, String clientAddress, 
			String serverAddress, int portNumber, int numberOfAcesses)
			throws IOException, InterruptedException {
		String javaHome = System.getProperty("java.home");
		String javaBin = javaHome + File.separator + "bin" + File.separator
				+ "java";
		String classpath = System.getProperty("java.class.path");
		String className = klass.getCanonicalName();

		ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath,
				className, Integer.toString(clientID), clientType, clientAddress, serverAddress, 
				Integer.toString(portNumber), Integer.toString(numberOfAcesses));
		System.out.println("Start: Process started");
		builder.start();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		PropertiesReader propReader = new PropertiesReader();
		
		String serverAddress = propReader.getServerAddress();
		int portNumber = propReader.getServerPortNum();
		int numberOfAcesses = propReader.getAccessNum();
		int readers = propReader.getReadersNum();
		int writers = propReader.getWritersNum();
		
		// create Server
		Server server = new Server(serverAddress, portNumber, numberOfAcesses
				* (readers + writers));
		// run server as background thread
		new Thread(server).start();
		System.out.println("Start: Start Clients");
		int clientID =0;
		// create Clients
		for(int i =0;i < readers;i++){
			System.out.println("Start: Start new Client");
			startNewClient(Client.class, ++clientID, ServerClientUtils.READER,propReader.getNextReader(), 
					serverAddress, portNumber, numberOfAcesses);
		}
		for(int i =0;i < writers;i++){
			System.out.println("Start: Start new Client");
			startNewClient(Client.class, ++clientID, ServerClientUtils.WRITER, propReader.getNextWriter(), 
					serverAddress, portNumber, numberOfAcesses);
		}
		
	}
}
