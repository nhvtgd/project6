import org.deuce.Atomic;

public class SerialSTMFireWall extends SerialFireWall {

	public SerialSTMFireWall(IHashTable<Integer, Boolean> blackListTable,
			IHashTable<Integer, IHashSet<Integer>> acceptanceList,
			IHistorgram<Long> histogram) {
		super(blackListTable, acceptanceList, histogram);
	}
	
	@Atomic
	@Override
	public void addPacket(Packet pkt) {
		super.addPacket(pkt);
	}

}
