
public class SerialFireWallTest {
	public static void main(String[] args) {
		final int numMilliseconds = Integer.parseInt(args[0]);
		StopWatch timer = new StopWatch();
	    PacketGenerator source = new PacketGenerator(16, 14, 15, 12, 9, 5, 8840, 0.04d, 0.19d, 0.76d);
	    PaddedPrimitiveNonVolatile<Boolean> done = new PaddedPrimitiveNonVolatile<Boolean>(false);
	    PaddedPrimitive<Boolean> memFence = new PaddedPrimitive<Boolean>(false);
	    IHashTable<Integer, Integer> blackListTable = new JavaHashMapWrapper<Integer, Integer>();
	    IHashTable<Integer, SerialSet<Integer>> acceptanceList = new JavaHashMapWrapper<Integer, SerialSet<Integer>>();
	    IHistorgram<Integer> histogram = new SimpleHistogram<Integer>();
	    
	    SerialFireWall workerData = new SerialFireWall(done, source, blackListTable, acceptanceList, histogram);
	    Thread workerThread = new Thread(workerData);
	    
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
	    final long totalCount = workerData.totalPackets;
	    System.out.println("count: " + totalCount);
	    System.out.println("time: " + timer.getElapsedTime());
	    System.out.println(totalCount/timer.getElapsedTime() + " pkts / ms");
	    histogram.display();
	    //blackListTable.display();
	}
}
