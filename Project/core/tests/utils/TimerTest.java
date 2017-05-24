package utils;

import com.projectdgdx.game.model.machineStates.UnusedMachineState;
import com.projectdgdx.game.model.staticInteractable.Machine;
import com.projectdgdx.game.utils.Vector3d;
import com.projectdgdx.game.utils.iTimerListener;
import org.junit.Test;

/**
 * Created by Emil Jansson on 2017-05-10.
 */
public class TimerTest {

    @Test
    public void testAddListener(){
        iTimerListener listerer = new Machine(new Vector3d(1,1,1), new Vector3d(1,1,1), new Vector3d(1,1,1), "MACHINE", new UnusedMachineState());

    }
}
