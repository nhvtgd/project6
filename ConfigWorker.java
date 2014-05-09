import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class ConfigWorker implements Runnable{
	final SerialFireWall FW;
	final WaitFreeQueue<Packet> configQueue;
	PaddedPrimitiveNonVolatile<Boolean> done;
	Random ranGen;
	final int threadID;
	AtomicInteger inflight;
	public ConfigWorker(
			AtomicInteger inflight,
			PaddedPrimitiveNonVolatile<Boolean> done,
			WaitFreeQueue<Packet> configQueue,
			SerialFireWall FW,
			int threadID) {
		this.done = done;
		this.configQueue = configQueue;
		this.FW = FW;
		this.ranGen = new Random();
		this.threadID = threadID;
		this.inflight = inflight;
	}
	 
	 private boolean runWrapper(int queueNum) {
		Packet pkt;
		while (true) {
			try {
				pkt = this.configQueue.deq();
				if (pkt != null) {
					inflight.getAndDecrement();
					FW.addPacket(pkt);
				}
			} catch (EmptyException e) {			
				return true;
			}
		}
	}

	
	@Override
	 public void run() {
	    while( !done.value ) {	    	
	    	runWrapper(threadID);
	    }	    
	    boolean empty = false;
	    while( !empty ) {
	    	empty = runWrapper(threadID);	       
	    }
	  }
				

}
