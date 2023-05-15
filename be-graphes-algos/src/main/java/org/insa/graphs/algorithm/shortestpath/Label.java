package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Node;

public class Label implements Comparable<Label>{
    private Node currentNode;
    private boolean mark;
    private Double currentCost;
    private Arc parent;
    //private final int id;

    public Label(Node node, Arc parent){
        this.currentNode = node;
        this.parent = parent;
        this.mark = false;
        this.currentCost = Double.POSITIVE_INFINITY;
    }

    public String toString(){
        return "ID : " + this.currentNode.getId() + "  Mark : " + mark + "  Cost : " + currentCost;
    }

    public Node getCurrentNode(){
        return this.currentNode;
    }

    public int getID(){
        return this.currentNode.getId();
    }

    public boolean getMark(){
        return this.mark;
    }

    public double getCurrentCost(){
        return this.currentCost;
    }

    public Arc getParent() {
        return this.parent;
    }

    public double getCost(){
        return this.currentCost;
    }

    public void setCurrentCost(double cost){
        this.currentCost = cost;
    }

    public void setParent(Arc parent){
        this.parent = parent;
    }

    public void Mark(){
        this.mark = true;
    }


    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof Label) {
            return this.currentNode.getId() == ((Label) other).getCurrentNode().getId();
        }
        return false;
    }

    /**
     * Compare the ID of this label with the ID of the given label.
     *
     * @param other Node to compare this node with.
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Label other) {
        int result = Double.compare(this.getCost(), other.getCost());
            if(result == 0 && this.getCostToDestination() < other.getCostToDestination()){
                result = -1;
            }else if(result == 0 && this.getCostToDestination() > other.getCostToDestination()){
                result = 1;
            }
        return result;
    }

    public double getCostToDestination() { //necessary to make compareTo work
        return 0.0;
    }

}
