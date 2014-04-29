import deuce.Atomic;

public class SerialFireWall {
	PaddedPrimitiveNonVolatile<Boolean> done;
	final IHashTable<Integer, Boolean> blackListTable;
	final IHashTable<Integer, SerialSet<Integer>> acceptanceList;
	final IHistorgram<Long> historgram;
	long residue = 0;
	Fingerprint fingerprint;
	
	public SerialFireWall(IHashTable<Integer, Boolean> blackListTable,
			IHashTable<Integer, SerialSet<Integer>> acceptanceList,
			IHistorgram<Long> histogram){
		this.blackListTable = blackListTable;
		this.acceptanceList = acceptanceList;
		this.historgram = histogram;
		fingerprint = new Fingerprint();
	}
	
	
	public void addPacket(Packet pkt){	
		switch(pkt.type) {
			case ConfigPacket:
				processConfigPacket(pkt);
				break;
			case DataPacket:
				processDataPacket(pkt);
				break;
			default:
				break;		
		
		}
	}	
	
	
	private void processConfigPacket(Packet pkt) {
		blackListTable.add(pkt.config.address, pkt.config.personaNonGrata);
		if (!acceptanceList.contains(pkt.config.address)) {
			acceptanceList.add(pkt.config.address, new SerialSet<Integer>());
		}
		SerialSet<Integer> bucket = acceptanceList.get(pkt.config.address);
		if (pkt.config.acceptingRange) {
			for (int i = pkt.config.addressBegin; i < pkt.config.addressEnd;i++) {
				if (!bucket.contains(i)){
					bucket.add(i);
				}
			}
		} else {
			for (int i = pkt.config.addressBegin; i < pkt.config.addressEnd;i++) {
				if (bucket.contains(i)){
					bucket.remove(i);
				}
			}
		}
	}
	
	
	private void processDataPacket(Packet pkt) {
		long checkSum = 0;
		if (blackListTable.contains(pkt.header.source) && 
				!blackListTable.get(pkt.header.source) 	&& 
				acceptanceList.get(pkt.header.dest) != null  && 
				acceptanceList.get(pkt.header.dest).contains(pkt.header.source)) {
			checkSum = fingerprint.getFingerprint(pkt.body.iterations, pkt.body.seed);
			historgram.add(checkSum);
		}
	}
}
