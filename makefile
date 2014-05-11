JC= "/usr/bin/javac" -source 1.6 -target 1.6
.SUFFIXES: .java .class
.java.class:
	$(JC) $*.java

CLASSES = \
	RandomGenerator.java \
	PacketGenerator.java \
	WaitFreeQueue.java \
	IHashSet.java \
	IHashTable.java	\
        IQueue.java \
        IHistorgram.java \
        JavaHashMapWrapper.java	\
	SerialSet.java \
        PaddedPrimitive.java \
	PaddedPrimitive.java \
	StopWatch.java \
	Fingerprint.java \
	RandomGenerator.java \
	ParallelPacketWorker.java \
	ConfigWorker.java \
	ParallelPacketTest.java \
	SimpleHistogram.java \
	SerialFireWall.java \
	SerialFireWallWorker.java \
	SerialFireWallTest.java \

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
