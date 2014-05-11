
public class SerialFireWall {
	PaddedPrimitiveNonVolatile<Boolean> done;
	final IHashTable<Integer, Boolean> blackListTable;
	final IHashTable<Integer, IHashSet<Integer>> acceptanceList;
	final ICachePNG cache;
	final IHistorgram<Long> historgram;
	long residue = 0;
	Fingerprint fingerprint;
	
	public SerialFireWall(IHashTable<Integer, Boolean> blackListTable,
			IHashTable<Integer, IHashSet<Integer>> acceptanceList,
			IHistorgram<Long> histogram, ICachePNG cache){
		this.blackListTable = blackListTable;
		this.acceptanceList = acceptanceList;
		this.historgram = histogram;
		fingerprint = new Fingerprint();
		this.cache = cache;
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
	
	
	protected void processConfigPacket(Packet pkt) {
		blackListTable.add(pkt.config.address, pkt.config.personaNonGrata);
		this.cache.add(pkt.config.address, pkt.config.personaNonGrata);
		if (!acceptanceList.contains(pkt.config.address)) {
			acceptanceList.add(pkt.config.address, new SerialSet<Integer>());
		}
		IHashSet<Integer> bucket = acceptanceList.get(pkt.config.address);
		if (pkt.config.acceptingRange) {
			for (int i = pkt.config.addressBegin; i < pkt.config.addressEnd;i++) {
				bucket.add(i);
			}
		} else {
			for (int i = pkt.config.addressBegin; i < pkt.config.addressEnd;i++) {
				bucket.remove(i);
			}
		}
	}
	
	
	protected void processDataPacket(Packet pkt) {
		if (this.cache.isBad(pkt.header.source)) {
			return;
		}
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
