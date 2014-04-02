package server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import remote.RemoteInterface;

public class RemoteNewsObject extends UnicastRemoteObject implements RemoteInterface{

	private static final int OPERAITON_SLEEP_TIME = 10000;

	private final Lock readLock;
	private final Lock writeLock;
	private final int maxReads;
	private final ReentrantReadWriteLock readWriteLock;

	private AtomicInteger sequenceNumber;
	private AtomicInteger requestNumber;
	
	private int news = -1;

	// Vector is synchronized
	private Vector<String> readerLog = new Vector<String>();
	private Vector<String> writerLog = new Vector<String>();
	
	public RemoteNewsObject(int portNumber, int maxReads) throws RemoteException{
		super(portNumber);

		this.maxReads = maxReads;
		this.readWriteLock = new ReentrantReadWriteLock(true);
		this.readLock = readWriteLock.readLock();
		this.writeLock = readWriteLock.writeLock();

		this.sequenceNumber = new AtomicInteger(1);
		this.news = -1;
		this.requestNumber = new AtomicInteger(0);
		start();
	}

	void start(){
		System.out.println("starting remote Object");
		readerLog.add("Readers");
		readerLog.add("sSeq\toVal\trID\t\trNum");
		writerLog.add("Writers");
		writerLog.add("sSeq\toVal\twID");
	}

	boolean checkEnd(){
		System.out.println("***checking ... " + sequenceNumber.get());
		System.out.println( " readlockcount " + readWriteLock.getReadLockCount()  + " writehold count " +  readWriteLock.getWriteHoldCount());
		// TODO we could face problems with synchronization here, make sure there's no more activity
		return (sequenceNumber.get() > maxReads && readWriteLock.getReadLockCount() == 0  &&  readWriteLock.getWriteHoldCount() == 0);
	}
	
	void end(){
		System.out.println("ending remote Object");
		try{
		// write to log file
		PrintWriter logWriter = new PrintWriter(new File("server.log"));
		for (String value : readerLog)
			logWriter.write(value + "\n");
		for (String value : writerLog)
			logWriter.write(value + "\n");
		logWriter.close();
		}catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public String setNews(int id, int newNews) throws RemoteException {
		// TODO Auto-generated method stub
		int currentRequestNum = requestNumber.addAndGet(1);
		System.out.println("setting news");
		final String response;
		int tmp;
		try {
			writeLock.lock();
			tmp = this.news = newNews;

			try {
				System.out.println("writing...");
				Thread.sleep(new Random().nextInt(OPERAITON_SLEEP_TIME));
			} catch (InterruptedException e1) {
			}

			// write back to client
			int currentSeqNumber = sequenceNumber.getAndIncrement();
			// write to log
			response = currentRequestNum + " " + currentSeqNumber;
			writerLog.add(currentSeqNumber + "\t\t" + tmp + "\t\t" + id);
		} finally {
			writeLock.unlock();
		}
		if(checkEnd())
			end();
		return response;
	}

	@Override
	public String getNews(int id) throws RemoteException {
		// TODO Auto-generated method stub
		int currentRequestNum = requestNumber.addAndGet(1);
		System.out.println("getting news "+news);

		final String response;
		int tmp;
		try {
			readLock.lock();
			tmp = news;
			try {
				System.out.println("Reading...");
				Thread.sleep(new Random().nextInt(OPERAITON_SLEEP_TIME));
			} catch (InterruptedException e1) {
			}
			int currentSeqNumber;
			synchronized (sequenceNumber) {
				currentSeqNumber = sequenceNumber.getAndIncrement();
				// write to log
				readerLog.add(currentSeqNumber + "\t\t" + tmp + "\t\t" + id + "\t\t"
						+ readWriteLock.getReadLockCount());
				System.out.println("waiting for read are " + readWriteLock.getReadLockCount());
			}
			response = currentRequestNum + " " + currentSeqNumber + " " + String.valueOf(tmp);
		} finally {
			readLock.unlock();
		}
		if(checkEnd())
			end();
		return response;
	}
}
