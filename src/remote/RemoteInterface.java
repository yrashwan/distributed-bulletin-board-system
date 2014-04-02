package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote{
	
	String setNews(int id, int newNews) throws RemoteException;
	
	String getNews(int id) throws RemoteException;
}
