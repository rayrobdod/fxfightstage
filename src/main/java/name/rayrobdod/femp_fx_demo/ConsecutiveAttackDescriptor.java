package name.rayrobdod.femp_fx_demo;

/**
 * Describes the relative position of a strike in a sequence of same-attacker strikes.
 */
public final class ConsecutiveAttackDescriptor {
	/** 1-based index of current strike in its sequence */
	public final int current;
	/** length of sequence of same-attacker strikes */
	public final int total;
	
	public ConsecutiveAttackDescriptor(int current, int total) {
		this.current = current;
		this.total = total;
	}
	
	/** Returns true if this strike is the last in its sequence */
	public final boolean isLast() {return current == total;}
	/** Returns true if this strike is the first in its sequence */
	public final boolean isFirst() {return current == 1;}
	/** Returns true if this strike is the only strike in its sequence */
	public final boolean isOnly() {return this.isLast() && this.isFirst();}
	
	public String toString() {
		return "ConsecAttacDesc[curr = " + current + ", total = " + total + "]";
	}
}
