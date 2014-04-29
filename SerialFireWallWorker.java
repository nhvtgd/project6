
public class SerialFireWallWorker implements Runnable{
	PaddedPrimitiveNonVolatile<Boolean> done;
	SerialFireWall serialFW;
	PacketGenerator source;
	long totalPackets = 0;
	public SerialFireWallWorker(PaddedPrimitiveNonVolatile<Boolean> done,
			SerialFireWall serialFW,PacketGenerator source) {
		this.done = done;
		this.serialFW = serialFW;
		this.source = source;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (!done.value) {
			Packet pkt = source.getPacket();
			totalPackets ++;
			serialFW.addPacket(pkt);
		}
	}

}
