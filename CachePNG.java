
public class CachePNG implements ICachePNG{
	final boolean[] cache;
	int size;
	public CachePNG(int size){
		this.cache = new boolean[size];
		this.size = size;
	}

	@Override
	public void add(int address, boolean value) {		
		this.cache[address  & (this.size-1)] = value;
	}

	@Override
	public boolean isBad(int address) {		
		return this.cache[address&(this.size-1)];
	}

}
