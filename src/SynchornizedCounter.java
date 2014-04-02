
public class SynchornizedCounter {

	private int counter;
	
	public SynchornizedCounter(int initalValue){
		this.counter = initalValue;
	}
	
	public synchronized int get(){
		return counter;
	}
	
	public synchronized void increment(){
		counter++;
	}
	
	public synchronized void decrement(){
		counter--;
	}
}
