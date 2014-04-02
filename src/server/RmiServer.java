package server;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

import remote.RemoteInterface;

public class RmiServer {

	// in order not to be garbage collected, should be strong reference
	public static RemoteInterface obj_strong_ref;

	private static Registry registry;
	private final String objectName;
	
	// static {
	// RemoteInterface tmp;
	// try {
	// // TODO let it use parameters given!
	// tmp = new RemoteNewsObject(1099,18);
	// } catch (RemoteException e) {
	// tmp = null;
	// e.printStackTrace();
	// }
	// obj_strong_ref = tmp;
	// }

	public RmiServer(String serverAddress, String objectName, int portNumber,
			int numberOfAccesses) throws RemoteException {

		this.objectName = objectName;
		obj_strong_ref = new RemoteNewsObject(portNumber,
				numberOfAccesses);

		// option#1 ============ using getRegistry
		LocateRegistry.createRegistry(portNumber);
		registry = LocateRegistry.getRegistry(serverAddress, portNumber);
		RemoteInterface stub;
		try {
			stub = (RemoteInterface) UnicastRemoteObject.exportObject(
					obj_strong_ref, portNumber);
		} catch (ExportException e) {
			stub = (RemoteInterface) UnicastRemoteObject.toStub(obj_strong_ref);
		}
		registry.rebind(objectName, stub);
		System.out.println("*** registry " + Arrays.toString(registry.list()));

		// option#2 ============ getRegistry only
		// // #using createRegistry & rebind directly the object
		// final Registry registry = LocateRegistry.createRegistry(portNumber);
		// registry.rebind(objectName, obj_strong_ref);
		System.out.println("Server bound");
	}

	public void end() throws RemoteException, MalformedURLException,
			NotBoundException {
		System.out.println(Arrays.toString(registry.list()));
		registry.unbind(objectName);
		System.out.println("server ended");
	}
}
