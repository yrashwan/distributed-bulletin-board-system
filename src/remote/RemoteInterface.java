package remote;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote{
	
	void setNews(int id, int newNews) throws RemoteException;
	
	int getNews(int id) throws RemoteException;
}
