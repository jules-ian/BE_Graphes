package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractSolution;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.*;

import java.util.ArrayList;
import java.util.Collections;

import static org.insa.graphs.algorithm.AbstractInputData.Mode.LENGTH;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }

    void createLabel(Graph graph, Label[] labels, int i){

        Node destination = data.getDestination();

        double maxGraphSpeed = 130;

        if(data.getMode() == LENGTH){
            labels[graph.getNodes().get(i).getId()] = new LabelStar(graph.getNodes().get(i),null, Point.distance(destination.getPoint(), graph.getNodes().get(i).getPoint()));
        }else{
            if (graph.getGraphInformation().hasMaximumSpeed()){
            maxGraphSpeed = graph.getGraphInformation().getMaximumSpeed();
        }
            labels[graph.getNodes().get(i).getId()] = new LabelStar(graph.getNodes().get(i),null, Point.distance(destination.getPoint(), graph.getNodes().get(i).getPoint())/(maxGraphSpeed*3.6));
        }
    }
}
