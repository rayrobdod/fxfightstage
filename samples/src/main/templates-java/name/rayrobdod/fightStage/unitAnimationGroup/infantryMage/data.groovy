import java.util.stream.Stream
import java.util.stream.Collectors

class BoneName {
	final String from;
	final String to;
	public BoneName(String from, String to) {this.from = from; this.to = to;}
	public String methodName() { from + "To" + toTitleCase(to) }
	public String methodNameTitle() { toTitleCase(from) + "To" + toTitleCase(to) }
	public Stream<String> polarFields() { Stream.of( this.methodName() + "Length", this.methodName() + "Angle") }
	private static String toTitleCase(String value) { value.substring(0, 1).toUpperCase() + value.substring(1) }
	public boolean equals(Object rhs) {
		if (rhs instanceof BoneName) {
			this.from == ((BoneName) rhs).from &&
			this.to == ((BoneName) rhs).to
		} else {
			false
		}
	}
}

List<BoneName> boneNames = [
	new BoneName("leftFoot", "leftKnee"),
	new BoneName("leftKnee", "leftPelvic"),
	new BoneName("leftPelvic", "rightPelvic"),
	new BoneName("rightPelvic", "rightKnee"),
	new BoneName("rightKnee", "rightFoot"),
	new BoneName("centerPelvic", "neck"),
	new BoneName("neck", "leftShoulder"),
	new BoneName("leftShoulder", "leftElbow"),
	new BoneName("leftElbow", "leftHand"),
	new BoneName("neck", "rightShoulder"),
	new BoneName("rightShoulder", "rightElbow"),
	new BoneName("rightElbow", "rightHand"),
]

return boneNames
