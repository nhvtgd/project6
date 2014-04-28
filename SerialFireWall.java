
public class SerialFireWall<T> implements Runnable{
	PaddedPrimitiveNonVolatile<Boolean> done;
	final PacketGenerator source;
	final IHashTable<Integer, Boolean> blackListTable;
	final IHashTable<Integer, IHashSet<Integer>> acceptanceList;
	final IHistorgram<Long> historgram;
	long totalPackets = 0;
	long residue = 0;
	Fingerprint fingerprint;
	
	public SerialFireWall(
			PaddedPrimitiveNonVolatile<Boolean> done,
			PacketGenerator source,
			IHashTable<Integer, Boolean> blackListTable,
			IHashTable<Integer, IHashSet<Integer>> acceptanceList,
			IHistorgram<Long> histogram){
		this.done = done;
		this.source = source;
		this.blackListTable = blackListTable;
		this.acceptanceList = acceptanceList;
		this.historgram = histogram;
		fingerprint = new Fingerprint();
	}
	@Override
	public void run() {
		Packet pkt;
		while (!done.value) {
			totalPackets ++;
			pkt = source.getPacket();
			System.out.println(pkt.type);
			switch(pkt.type) {
				case ConfigPacket:
					blackListTable.add(pkt.config.address, pkt.config.personaNonGrata);
					if (!acceptanceList.contains(pkt.config.address)) {
						acceptanceList.add(pkt.config.address, new SerialSet<Integer>());
					}
					if (pkt.config.acceptingRange) {
						for (int i = pkt.config.addressBegin; i < pkt.config.addressEnd;i++) {
							if (!acceptanceList.get(pkt.config.address).contains(i)){
								acceptanceList.get(pkt.config.address).add(i);
							}
						}
					} else {
						for (int i = pkt.config.addressBegin; i < pkt.config.addressEnd;i++) {
							if (acceptanceList.get(pkt.config.address).contains(i)){
								acceptanceList.get(pkt.config.address).remove(i);
							}
						}
					}
					break;
				case DataPacket:
					long checkSum = 0;
					
//					System.out.println("Contain " + blackListTable.contains(pkt.header.source));
//					if (blackListTable.contains(pkt.header.source)) {
//						System.out.println(!blackListTable.get(pkt.header.source));
//					}
//					if (acceptanceList.get(pkt.header.dest) != null) {
//						System.out.println("Accept" + acceptanceList.get(pkt.header.dest).contains(pkt.header.source));
//					}
					
					if (blackListTable.contains(pkt.header.source) && 
							!blackListTable.get(pkt.header.source) 	&& 
							acceptanceList.get(pkt.header.dest) != null  && 
							acceptanceList.get(pkt.header.dest).contains(pkt.header.source)) {
						checkSum = fingerprint.getFingerprint(pkt.body.iterations, pkt.body.seed);
						historgram.add(checkSum);
					}
					break;
				default:
					break;
				
			}
		}
		//acceptanceList.display();
	}	
}
