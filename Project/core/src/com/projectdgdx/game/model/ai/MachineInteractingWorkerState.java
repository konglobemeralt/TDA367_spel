package com.projectdgdx.game.model.ai;

import com.projectdgdx.game.utils.Timer;
import com.projectdgdx.game.utils.Config;
import com.projectdgdx.game.utils.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class MachineInteractingWorkerState implements iWorkerState {
    private Vector3d pos;

    public MachineInteractingWorkerState(Vector3d pos){
        this.pos = pos;
    }

    @Override
    public void reactOnUpdate(Worker worker) {
        if (worker.getPosition().isInRadius(pos, Config.HONEST_ACT_DISTANCE)){
            Timer timer = new Timer(3,1000);
            timer.start();
            if (timer.getTimerValue() > 0){
                //TODO animate working the machine
            }else{
                //TODO Functional decomposition
                Vector3d workerVector =  worker.getLastNode().getPosition().subtractVectorFrom(worker.getPosition());
                // Vector3d lastNodeVector = new Vector3d(0,0,0);
                Vector3d targetNodeVector = worker.getLastNode().getPosition().subtractVectorFrom(worker.getTargetNode().getPosition());

                Vector3d returnPoint = workerVector.projectOn(targetNodeVector);

                Vector3d nextTargetNodePosition = returnPoint.add(worker.getLastNode().getPosition());

                List<Integer> oldTargetID = new ArrayList<>();
                oldTargetID.add(worker.getTargetNode().getNodeId());

                worker.setTargetNode(new WorkerNode(nextTargetNodePosition, new Vector3d(1,1,1), new Vector3d(0,0,0),"", -1, oldTargetID ));
                worker.setState(new NormalWorkerState());
            }

        }else {
            worker.move(worker.getPosition().subtractVectorFrom(pos.normalised()));
        }

    }

    @Override
    public void beenCaught(Worker worker) {
        worker.setState(new StrikingWorkerState());
    }
}
