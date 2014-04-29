
public class Dispatcher implements Runnable {

	PaddedPrimitiveNonVolatile<Boolean> done;
	final PacketGenerator source;
	long fingerPrint = 0;
	long totalPackets = 0;
	final IQueue<Packet>[] queue;
	final int numQueues;
	public Dispatcher(
			int numQueues,
			PaddedPrimitiveNonVolatile<Boolean> done, 
			WaitFreeQueue<Packet>[] queue, 
			PacketGenerator source) {
		this.done = done;
		this.queue = queue;
		this.source = source;
		this.numQueues = numQueues;
	}
	
	@Override
	public void run() {
		Packet nextPacket;
		while (!done.value){
			for (int i = 0; i < numQueues; i++) {
				nextPacket = source.getPacket();
				boolean deliver = false;
				while (!deliver) {
					try {
						queue[i].enq(nextPacket);
						deliver = true;
						totalPackets ++;
					} catch (FullException e) {;}
				}
			} 
		}
	}
}
