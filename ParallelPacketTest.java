import java.util.concurrent.atomic.AtomicInteger;


public class ParallelPacketTest {
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
		final int numSources = Integer.parseInt(args[11]);
		StopWatch timer = new StopWatch();
		PacketGenerator source = new PacketGenerator(numAddressesLog, numTrainsLog, meanTrainSize, meanTrainsPerComm,
				 meanWindow, meanCommsPerAddress, meanWork, configFraction, pngFraction, acceptingFraction);
		PaddedPrimitiveNonVolatile<Boolean> doneDispatch = new PaddedPrimitiveNonVolatile<Boolean>(false);
		PaddedPrimitiveNonVolatile<Boolean> doneWorker = new PaddedPrimitiveNonVolatile<Boolean>(false);
	    PaddedPrimitive<Boolean> memFence = new PaddedPrimitive<Boolean>(false);
	    IHashTable<Integer, Boolean> blackListTable = new JavaHashMapWrapper<Integer, Boolean>();
	    IHashTable<Integer, IHashSet<Integer>> acceptanceList = new JavaHashMapWrapper<Integer, IHashSet<Integer>>();
	    IHistorgram<Long> histogram = new SimpleHistogram<Long>();
	    
	    WaitFreeQueue<Packet>[] packetQueue = new WaitFreeQueue[numSources-1];
	    for(int i = 0; i < numSources-1; i++) {
	        packetQueue[i] = new WaitFreeQueue<Packet>(8);
	    }
	    WaitFreeQueue<Packet> configQueue = new WaitFreeQueue<Packet>(48);
	    
	    AtomicInteger inflight = new AtomicInteger(0);
	    Dispatcher dispatchData = new Dispatcher(inflight, numSources-1, doneDispatch, packetQueue, configQueue, source);
	    Thread dispatchThread = new Thread(dispatchData);
	    
	    SerialFireWall serialFW = new SerialFireWall(blackListTable, acceptanceList, histogram);
	    for (int i = 0; i < (1<<numAddressesLog); i++) {
	    	serialFW.addPacket(source.getConfigPacket());
	    }
	    
	    ParallelPacketWorker[] parallelFWWorker = new ParallelPacketWorker[numSources-1];
	    Thread[] workerThread = new Thread[numSources];
	    for (int i = 0; i < numSources-1; i++) {
	    	parallelFWWorker[i] = new ParallelPacketWorker(inflight, doneWorker, packetQueue, serialFW,i);
	    	workerThread[i+1] = new Thread(parallelFWWorker[i]);
	    }
	    
	    ConfigWorker configWorker = new ConfigWorker(inflight, doneWorker, configQueue, serialFW,0);
	    workerThread[0] = new Thread(configWorker);
	    for( int i = 0; i < numSources; i++ ) {
	        workerThread[i].start();
	    }
	    timer.startTimer();
	    dispatchThread.start();
	    try {
	      Thread.sleep(numMilliseconds);
	    } catch (InterruptedException ignore) {;}
	    doneDispatch.value = true;
	    memFence.value = true;
	    try {
	      dispatchThread.join();      
	    } catch (InterruptedException ignore) {;}

	    doneWorker.value = true;
	    memFence.value = false;
	    for( int i = 0; i < numSources; i++ ) {
	      try {
	        workerThread[i].join();
	      } catch (InterruptedException ignore) {;}      
	    }
	    timer.stopTimer();    

	    final long totalCount = dispatchData.totalPackets;
	    //System.out.println("count: " + totalCount);
	    //System.out.println("time: " + timer.getElapsedTime());
	    System.out.println(totalCount/timer.getElapsedTime() + " pkts / ms");
	    //System.out.println(timer.getElapsedTime());
	    //histogram.display();
	    //blackListTable.display();
	}
}
