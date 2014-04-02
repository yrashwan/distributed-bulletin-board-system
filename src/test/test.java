package test;

import java.util.Hashtable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;

public class test {
	static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	static void ttt(int x) throws InterruptedException {
		readWriteLock.readLock().lock();

		System.out.println("Thread : " + x + " Starts");
		Thread.sleep(1000 * 5);
		System.out.println("Thread : " + x + " ends");

		readWriteLock.readLock().unlock();
	}

	public static void main(String[] args) throws NamingException {

		Hashtable<String, String> env = new Hashtable<>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
			    "com.sun.jndi.rmi.registry.RegistryContextFactory");
		env.put(Context.PROVIDER_URL, "rmi://localhost:1099");

		Context ictx = new InitialContext(env);
		System.out.println(ictx.list(""));

//		System.out.println(System.getProperty("user.dir"));
//		for(int i = 0; i < 10; i++)
//		{
//			Thread t = new Thread(new MyThread(i));
//			t.start();
//		}
	
	}
	
	static class MyThread implements Runnable{
		int idx;
		public MyThread(int i) {
			idx = i;
		}
		
		@Override
		public void run() {
			try {
				ttt(idx);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
