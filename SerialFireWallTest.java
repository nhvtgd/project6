
public class SerialFireWallTest {
	public static void main(String[] args) {
		final int numMilliseconds = Integer.parseInt(args[0]);
		final int numAddressesLog = Integer.parseInt(args[1]);
		final int numTrainsLog  = Integer.parseInt(args[2]);
		final int meanTrainSize = Integer.parseInt(args[3]);
		final int meanTrainsPerComm = Integer.parseInt(args[4]);
		final int meanWindow = Integer.parseInt(args[5]);
		final int meanCommsPerAddress = Integer.parseInt(args[6]);
		final int meanWork = Integer.parseInt(args[7]);
		final double  configFraction = Double.parseDouble(args[8]);
		final double pngFraction = Double.parseDouble(args[9]); 
		final double acceptingFraction = Double.parseDouble(args[10]);
		StopWatch timer = new StopWatch();
		 PacketGenerator source = new PacketGenerator(numAddressesLog, numTrainsLog, meanTrainSize, meanTrainsPerComm,
				 meanWindow, meanCommsPerAddress, meanWork, configFraction, pngFraction, acceptingFraction);
	    PaddedPrimitiveNonVolatile<Boolean> done = new PaddedPrimitiveNonVolatile<Boolean>(false);
	    PaddedPrimitive<Boolean> memFence = new PaddedPrimitive<Boolean>(false);
	    IHashTable<Integer, Boolean> blackListTable = new JavaHashMapWrapper<Integer, Boolean>();
	    IHashTable<Integer, IHashSet<Integer>> acceptanceList = new JavaHashMapWrapper<Integer, IHashSet<Integer>>();
	    IHistorgram<Long> histogram = new SimpleHistogram<Long>();
	    CachePNG cache = new CachePNG(128);
	    SerialFireWall serialFW = new SerialFireWall(blackListTable, acceptanceList, histogram,cache);
	    for (int i = 0; i < (1<<numAddressesLog); i++) {
	    	serialFW.addPacket(source.getConfigPacket());
	    }
	    SerialFireWallWorker serialFWWorker = new SerialFireWallWorker(done, serialFW, source);
	    Thread workerThread = new Thread(serialFWWorker);
	    
	    workerThread.start();
	    timer.startTimer();
	    try {
	      Thread.sleep(numMilliseconds);
	    } catch (InterruptedException ignore) {;}
	    done.value = true;
	    memFence.value = true;
	    try {
	      workerThread.join();
	    } catch (InterruptedException ignore) {;}      
	    timer.stopTimer();
	    final long totalCount = serialFWWorker.totalPackets;
	    System.out.println("count: " + totalCount);
	    System.out.println("time: " + timer.getElapsedTime());
	    System.out.println(totalCount/timer.getElapsedTime() + " pkts / ms");
	    //histogram.display();
	    //blackListTable.display();
	}
}