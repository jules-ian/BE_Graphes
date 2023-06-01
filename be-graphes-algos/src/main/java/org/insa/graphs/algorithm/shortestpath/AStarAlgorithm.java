package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractSolution;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.*;

import java.util.ArrayList;
import java.util.Collections;

import static org.insa.graphs.algorithm.AbstractInputData.Mode.LENGTH;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public static long initTime = 0;
    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }

    void createLabel(Graph graph, Label[] labels, int i){

        Node destination = data.getDestination();
        Node current = graph.getNodes().get(i);

        int maxGraphSpeed = 130;

        if(data.getMode() == LENGTH){
            labels[current.getId()] = new LabelStar(current,null, Point.distance(destination.getPoint(), current.getPoint()));
        }else{
            if (graph.getGraphInformation().hasMaximumSpeed()){
                maxGraphSpeed = graph.getGraphInformation().getMaximumSpeed(); //if Maximum speed available use it otherwise use 130km/h
            }
            labels[current.getId()] = new LabelStar(current,null, Point.distance(destination.getPoint(), current.getPoint())/(maxGraphSpeed*3.6));
        }
    }
    void addTime(long time){
        AStarAlgorithm.initTime+= time;
    }
}
