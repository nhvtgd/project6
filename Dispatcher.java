import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class Dispatcher implements Runnable {

	PaddedPrimitiveNonVolatile<Boolean> done;
	final PacketGenerator source;
	long fingerPrint = 0;
	long totalPackets = 0;
	final IQueue<Packet>[] queue;
	final int numQueues;
	AtomicInteger inFlight;
	Random rand;
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
		this.rand = new Random();
	}
	
	@Override
	public void run() {
		Packet nextPacket;
		while (!done.value){		
			nextPacket = source.getPacket();
			boolean deliver = false;
			while (!deliver) {
				try {
					if (nextPacket.type == Packet.MessageType.ConfigPacket) {
						queue[0].enq(nextPacket);
					} else {
						queue[1+this.rand.nextInt(this.numQueues-1)].enq(nextPacket);							
					}
					inFlight.getAndIncrement();
					deliver = true;
					totalPackets ++;
					while (inFlight.get() >= 256) {;}
				} catch (FullException e) {;}
			}			
		}
	}
}
