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
        final ShortestPathData data = getInputData();

        Node destination = data.getDestination();

        if(data.getMode() == LENGTH){
            labels[graph.getNodes().get(i).getId()] = new LabelStar(graph.getNodes().get(i),null, Point.distance(destination.getPoint(), graph.getNodes().get(i).getPoint()));
        }else{
            labels[graph.getNodes().get(i).getId()] = new LabelStar(graph.getNodes().get(i),null, Point.distance(destination.getPoint(), graph.getNodes().get(i).getPoint())/(80.0*3.6));
        }
    }
}
