package server;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import remote.RemoteInterface;

public class RmiServer {

	// in order not to be garbage collected, should be strong reference
	public static final RemoteInterface obj_strong_ref;

	static {
		RemoteInterface tmp;
		try {
			tmp = new RemoteNewsObject(1099);
		} catch (RemoteException e) {
			tmp = null;
			e.printStackTrace();
		}
		obj_strong_ref = tmp;
	}

	public RmiServer(String serverAddress, String objectName,
			int portNumber, int numberOfAccesses) throws RemoteException {
		try {
			// #using getRegistry
			// final Registry registry =
			// LocateRegistry.getRegistry(serverAddress,
			// portNumber);
			// RemoteInterface stub = (RemoteInterface) UnicastRemoteObject
			// .exportObject(obj_strong_ref, 1099);
			//
			
			// #using createRegistry & rebind directly the object
			final Registry registry = LocateRegistry.createRegistry(portNumber);
			// registry.rebind(objectName, stub);
			registry.rebind(objectName, obj_strong_ref);

			// Naming.rebind(objectName, obj_strong_ref);
			System.out.println("Server bound");
		} catch (Exception e) {
			System.err.println("Server exception:");
			e.printStackTrace();
		}
	}

	public void endServer() throws RemoteException, MalformedURLException,
			NotBoundException {
		// TODO
	}
}
