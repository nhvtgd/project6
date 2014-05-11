import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;


public class Dispatcher implements Runnable {

	PaddedPrimitiveNonVolatile<Boolean> done;
	final PacketGenerator source;
	long fingerPrint = 0;
	long totalPackets = 0;
	final IQueue<Packet>[] packetQueue;
	final IQueue<Packet> configQueue;
	final int numQueues;
	AtomicInteger inFlight;
	Random rand;
	public Dispatcher(
			AtomicInteger inFlight,
			int numQueues,
			PaddedPrimitiveNonVolatile<Boolean> done, 
			WaitFreeQueue<Packet>[] packetQueue,
			WaitFreeQueue<Packet> configQueue,
			PacketGenerator source) {
		this.done = done;
		this.packetQueue= packetQueue;
		this.configQueue = configQueue;
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
						configQueue.enq(nextPacket);
					} else {
						packetQueue[this.rand.nextInt(this.numQueues)].enq(nextPacket);							
					}
					
					deliver = true;
					totalPackets ++;					
				} catch (FullException e) {;}
			}			
		}
	}
}
