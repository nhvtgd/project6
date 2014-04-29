import deuce.Atomic;

public class SerialSTMFireWall extends SerialFireWall {

	public SerialSTMFireWall(IHashTable<Integer, Boolean> blackListTable,
			IHashTable<Integer, SerialSet<Integer>> acceptanceList,
			IHistorgram<Long> histogram) {
		super(blackListTable, acceptanceList, histogram);
	}
	
	@Atomic
	@Override
	public void addPacket(Packet pkt) {
		super.addPacket(pkt);
	}

}
