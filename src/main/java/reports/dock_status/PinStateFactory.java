package reports.dock_status;

import java.util.Arrays;
import java.util.List;

import redleaf.PinState;

public class PinStateFactory {
	
	public static List<PinState> getData() {
		PinState state1 = new PinState();
		state1.setDevice("01040382"); state1.setIo5(true); state1.setIo6(false); state1.setFriendlyId(69); state1.setStateString("Locked");
		PinState state2 = new PinState();
		state2.setDevice("01040372"); state2.setIo5(false); state2.setIo6(true); state2.setFriendlyId(68); state2.setStateString("Unlocked");
		PinState state3 = new PinState();
		state3.setDevice("01040344"); state3.setIo5(true); state3.setIo6(true); state3.setFriendlyId(67); state3.setStateString("Flipping");
		PinState state4 = new PinState();
		state4.setDevice("01040383"); state4.setIo5(false); state4.setIo6(false); state4.setFriendlyId(66); state4.setStateString("Flipping");
		PinState state5 = new PinState();
		state4.setDevice("0104036f"); state5.setFriendlyId(65); state5.setStateString("No Comms");
		return Arrays.asList(state1,state2,state3,state4,state5);
		
	}

}
