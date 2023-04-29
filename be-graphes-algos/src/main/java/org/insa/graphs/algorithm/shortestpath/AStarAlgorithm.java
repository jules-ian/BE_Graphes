package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractSolution;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.*;

import java.util.ArrayList;
import java.util.Collections;

public class AStarAlgorithm extends DijkstraAlgorithm {

    public AStarAlgorithm(ShortestPathData data) {
        super(data);
    }

    protected ShortestPathSolution doRun() { // O(m log(n))
        final ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;

        Graph graph = data.getGraph();
        final int nbNodes = graph.size();

        Node origin = data.getOrigin();
        int originID = origin.getId();

        Node destination = data.getDestination();
        int destinationID = destination.getId();

        notifyOriginProcessed(origin);


        LabelStar[] labels = new LabelStar[nbNodes]; //Initialize array of Labels


        BinaryHeap<Label> labelsHeap = new BinaryHeap<Label>(); //Initialize heap of Labels

        for(int i = 0; i < nbNodes; i++){ //Initialize all the labels indexed by Node ID // O(n)
            labels[graph.getNodes().get(i).getId()] = new LabelStar(graph.getNodes().get(i),null, Point.distance(destination.getPoint(), graph.getNodes().get(i).getPoint()));
        }

        labels[originID].setCurrentCost(0f);
        labelsHeap.insert(labels[originID]);

        while (!labelsHeap.isEmpty() && labels[destinationID].getMark() == false){ // O(m)
            Label x = labelsHeap.deleteMin();
            x.Mark();
            for(Arc z : x.getCurrentNode().getSuccessors()){
                if (!data.isAllowed(z)) {
                    continue;
                }
                Label y = labels[(z.getDestination().getId())]; // Label of successor
                if(y.getMark() == false){ // If successor of x not marked

                    double oldDistance = y.getCurrentCost();
                    double newDistance = x.getCurrentCost() + data.getCost(z);


                    if (Double.isInfinite(oldDistance) && Double.isFinite(newDistance)) {
                        notifyNodeReached(z.getDestination());
                    }

                    if(oldDistance > newDistance){ // If new distance shorter
                        y.setCurrentCost(newDistance);
                        try{ // Update or insert y in the heap
                            labelsHeap.remove(y);
                        }catch(ElementNotFoundException e){
                            ;
                        }
                        labelsHeap.insert(y);
                        y.setParent(z);

                    }

                }
            }
        }

        // Destination has no predecessor, the solution is infeasible...
        if (labels[data.getDestination().getId()].getParent() == null) {
            solution = new ShortestPathSolution(data, AbstractSolution.Status.INFEASIBLE);
        }
        else {

            // The destination has been found, notify the observers.
            notifyDestinationReached(data.getDestination());

            // Create the path from the array of predecessors...
            ArrayList<Arc> arcs = new ArrayList<>();
            Arc arc = labels[data.getDestination().getId()].getParent();
            while (arc != null) {
                arcs.add(arc);
                arc = labels[arc.getOrigin().getId()].getParent();
            }

            // Reverse the path...
            Collections.reverse(arcs);

            // Create the final solution.
            solution = new ShortestPathSolution(data, AbstractSolution.Status.OPTIMAL, new Path(graph, arcs));
        }


        return solution;
    }
}
