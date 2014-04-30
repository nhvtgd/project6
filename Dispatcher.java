import java.util.concurrent.atomic.AtomicInteger;


public class Dispatcher implements Runnable {

	PaddedPrimitiveNonVolatile<Boolean> done;
	final PacketGenerator source;
	long fingerPrint = 0;
	long totalPackets = 0;
	final IQueue<Packet>[] queue;
	final int numQueues;
	AtomicInteger inFlight;
	public Dispatcher(
			AtomicInteger inFlight,
			int numQueues,
			PaddedPrimitiveNonVolatile<Boolean> done, 
			WaitFreeQueue<Packet>[] queue, 
			PacketGenerator source) {
		this.done = done;
		this.queue = queue;
		this.source = source;
		this.numQueues = numQueues;
		this.inFlight = inFlight;
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
						inFlight.getAndIncrement();
						deliver = true;
						totalPackets ++;
						while (inFlight.get() >= 256) {;}
					} catch (FullException e) {;}
				}
			} 
		}
	}
}
