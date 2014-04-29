import java.util.Random;


public class SerialSTMWorker implements Runnable{
	final SerialSTMFireWall STM;
	final WaitFreeQueue<Packet>[] queueBank;
	PaddedPrimitiveNonVolatile<Boolean> done;
	Random ranGen;
	final int threadID;
	public SerialSTMWorker(
			PaddedPrimitiveNonVolatile<Boolean> done,
			WaitFreeQueue<Packet>[] queueBank,
			SerialSTMFireWall STM,
			int threadID) {
		this.done = done;
		this.queueBank = queueBank;
		this.STM = STM;
		this.ranGen = new Random();
		this.threadID = threadID;
	}
	 
	 private boolean runWrapper(int queueNum) {
		Packet pkt;
		while (true) {
			try {
				pkt = this.queueBank[queueNum].deq();
				if (pkt != null) {
					STM.addPacket(pkt);
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
