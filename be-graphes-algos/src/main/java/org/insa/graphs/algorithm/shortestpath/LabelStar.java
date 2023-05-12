package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class LabelStar extends Label implements Comparable<Label> {

    private Double costToDestination;

    public LabelStar(Node node, Arc parent, double costToDestination){
        super(node, parent);
        this.costToDestination = costToDestination;
    }
    @Override
    public double getCostToDestination(){
        return this.costToDestination;
    }

    public double getCost(){
        return this.costToDestination + this.getCurrentCost();
    }


}
