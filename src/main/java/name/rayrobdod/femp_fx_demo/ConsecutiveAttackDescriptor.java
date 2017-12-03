package name.rayrobdod.femp_fx_demo;

/**
 * Describes the 
 */
public final class ConsecutiveAttackDescriptor {
	public final int current;
	public final int total;
	
	public ConsecutiveAttackDescriptor(int current, int total) {
		this.current = current;
		this.total = total;
	}
	
	public final boolean isLast() {return current == total;}
	public final boolean isFirst() {return current == 1;}
	public final boolean isOnly() {return this.isLast() && this.isFirst();}
	
	public String toString() {
		return "ConsecAttacDesc[curr = " + current + ", total = " + total + "]";
	}
}
