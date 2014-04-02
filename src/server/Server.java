package server;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import utils.ServerClientUtils;

public final class Server implements Runnable {

	@SuppressWarnings("unused")
	private final String address;
	private final ServerSocket socket;
	private final Lock readLock;
	private final Lock writeLock;
	private final int maxReads;
	private final ReentrantReadWriteLock readWriteLock;
	private AtomicInteger sequenceNumber;

	private int news = -1;

	// Vector is synchronized
	private Vector<String> readerLog = new Vector<String>();
	private Vector<String> writerLog = new Vector<String>();

	public Server(String address, int portNumber, int maxReads) throws IOException {
		this.address = address;
		this.maxReads = maxReads;
		this.socket = new ServerSocket(portNumber);
		this.readWriteLock = new ReentrantReadWriteLock(true);
		this.readLock = readWriteLock.readLock();
		this.writeLock = readWriteLock.writeLock();

		this.sequenceNumber = new AtomicInteger(1);
	}

	@Override
	public void run() {
		System.out.println("\n... Start Server ...\n");
		
		ArrayList<Thread> threads = new ArrayList<Thread>();

		readerLog.add("Readers");
		readerLog.add("sSeq\toVal\trID\t\trNum");
		writerLog.add("Writers");
		writerLog.add("sSeq\toVal\twID");
		// System.out.println("Server: starting server ");

		int requestNumber = 1;

		try {
			while (requestNumber <= maxReads) {
				// System.out.println(requestNumber+ " " + maxReads);
				// System.out.println("Server: try to get request for "
				// + requestNumber);
				Socket connectionSocket = socket.accept();

				Thread thread = new Thread(new ClientHandler(connectionSocket, requestNumber));
				thread.start();
				threads.add(thread);
				++requestNumber;
			}

			// wait for all threads to finish
			for (Thread thread : threads)
				thread.join();

			// write to log file
			PrintWriter logWriter = new PrintWriter(new File("server.log"));
			for (String value : readerLog)
				logWriter.write(value + "\n");
			for (String value : writerLog)
				logWriter.write(value + "\n");
			logWriter.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("... Server Ends ...");
	}

	private final class ClientHandler implements Runnable {

		private final Socket connectionSocket;
		private final int requestNumber;

		public ClientHandler(Socket connectionSocket, int requestNumber) {
			this.connectionSocket = connectionSocket;
			this.requestNumber = requestNumber;
		}

		@Override
		public void run() {
			System.out.println(requestNumber + " ... started");
			try {
				final String readLine = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()))
						.readLine();
				DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
				String ln[] = readLine.split(" ");
				System.out.println(requestNumber + " " + readLine);
				String type = ln[0];
				Integer clientID = Integer.valueOf(ln[1]);

				if (type.equalsIgnoreCase(ServerClientUtils.READER)) {
					// reader
					// write back to client
					int tmp;
					try {
						readLock.lock();
						tmp = news;

						try {
							System.out.println("Reading...");
							Thread.sleep(new Random().nextInt(10000));
						} catch (InterruptedException e1) {
						}

						int current;
						synchronized (sequenceNumber) {
							current = sequenceNumber.getAndIncrement();

							// write to log
							readerLog.add(current + "\t\t" + tmp + "\t\t" + clientID + "\t\t"
									+ readWriteLock.getReadLockCount());
							System.out.println("waiting for read are " + readWriteLock.getReadLockCount());
						}
						outToClient.writeBytes(requestNumber + " " + current + " " + String.valueOf(tmp) + "\n");
					} finally {
						readLock.unlock();
					}
				} else {
					// writer
					int tmp;
					try {
						writeLock.lock();
						tmp = news = Integer.valueOf(ln[2]);

						try {
							System.out.println("writing...");
							Thread.sleep(new Random().nextInt(10000));
						} catch (InterruptedException e1) {
						}

						// write back to client
						int current = sequenceNumber.getAndIncrement();
						// write to log
						outToClient.writeBytes(requestNumber + " " + current + "\n");
						writerLog.add(current + "\t\t" + tmp + "\t\t" + clientID);
					} finally {
						writeLock.unlock();
					}
				}
				connectionSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
			System.out.println(requestNumber + " ... finished");
		}
	}
}