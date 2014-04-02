package server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import remote.RemoteInterface;

public class RemoteNewsObject extends UnicastRemoteObject implements RemoteInterface{

	// TODO move Server stuff here
	
	
	private int news;

	public RemoteNewsObject(int portNumber) throws RemoteException{
		super(portNumber);
		news = -1;
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public void setNews(int id, int newNews) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("setting news");
		news = newNews;
	}

	@Override
	public int getNews(int id) throws RemoteException {
		// TODO Auto-generated method stub
		System.out.println("getting news "+news);
		return news;
	}

	
	
	
}
