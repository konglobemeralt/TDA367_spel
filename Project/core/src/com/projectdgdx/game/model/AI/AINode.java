package com.projectdgdx.game.model.AI;

import com.projectdgdx.game.model.GameObject;
import com.projectdgdx.game.utils.Vector3d;

import java.util.ArrayList;

/**
 * Created by Emil Jansson on 2017-05-08.
 */
public abstract class AINode extends GameObject {

    private ArrayList<AINode> connectingNodes = new ArrayList<AINode>();
    private ArrayList<Double> connectionStrengths = new ArrayList<Double>();

    public AINode(Vector3d position, Vector3d scale, Vector3d rotation, String id) {
        super(position, scale, rotation, id);
    }


    public void addConnection(AINode node, double strength){
        connectingNodes.add(node);
        connectionStrengths.add(strength);
    }

    public void setConnections(AINode[] nodes, double[] strengths){
        connectingNodes.clear();
        connectionStrengths.clear();
        int i = 0;
        for (AINode node : nodes) {
            connectingNodes.add(node);
            connectionStrengths.add(strengths[i]);
            i++;
        }
    }

    public AINode getNextNode(){ //Returns a node based on connection strength.
        double strenghtSum = 0;
        for (double d : connectionStrengths){
            strenghtSum += d;
        }
        double threshold = strenghtSum*Math.random();
        strenghtSum = 0;
        int i = 0;
        for (double d : connectionStrengths){
            strenghtSum += d;
            if (strenghtSum > threshold){
                break;
            }
        }
        return connectingNodes.get(i);
    }

}
