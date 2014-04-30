import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class SerialSTMWorker implements Runnable{
	final SerialSTMFireWall STM;
	final WaitFreeQueue<Packet>[] queueBank;
	PaddedPrimitiveNonVolatile<Boolean> done;
	Random ranGen;
	final int threadID;
	AtomicInteger inflight;
	public SerialSTMWorker(
			AtomicInteger inflight,
			PaddedPrimitiveNonVolatile<Boolean> done,
			WaitFreeQueue<Packet>[] queueBank,
			SerialSTMFireWall STM,
			int threadID) {
		this.done = done;
		this.queueBank = queueBank;
		this.STM = STM;
		this.ranGen = new Random();
		this.threadID = threadID;
		this.inflight = inflight;
	}
	 
	 private boolean runWrapper(int queueNum) {
		Packet pkt;
		if (!this.queueBank[queueNum].lock.tryLock()) {
			return false;
		} else {
			this.queueBank[queueNum].lock.unlock();
			while (true) {
				try {
					this.queueBank[queueNum].lock.lock();
					pkt = this.queueBank[queueNum].deq();
					if (pkt != null) {
						inflight.getAndDecrement();
						STM.addPacket(pkt);
					}
				} catch (EmptyException e) {			
					return true;
				} finally {
					this.queueBank[queueNum].lock.unlock();
				}
			}
		}

	}
		
	
	 
	
	@Override
	 public void run() {
	    while( !done.value ) {
	    	int randQueue = this.ranGen.nextInt(this.queueBank.length);
	    	runWrapper(randQueue);
	    }	    
	    boolean empty = false;
	    while( !empty ) {
	    	empty = runWrapper(threadID);	       
	    }
	    
	 }	 
				
}
