package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractSolution;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.Arc;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;

import java.util.ArrayList;
import java.util.Collections;

public class DijkstraAlgorithm extends ShortestPathAlgorithm {

    public DijkstraAlgorithm(ShortestPathData data) {
        super(data);
    }


    final ShortestPathData data = getInputData();
    Graph graph = data.getGraph();

    @Override
    protected ShortestPathSolution doRun() { // O(m log(n))
        ShortestPathSolution solution = null;

        final int nbNodes = graph.size();

        Node origin = data.getOrigin();
        int originID = origin.getId();

        Node destination = data.getDestination();
        int destinationID = destination.getId();

        notifyOriginProcessed(origin);


        Label[] labels = new Label[nbNodes]; //Initialize array of Labels


        BinaryHeap<Label> labelsHeap = new BinaryHeap<Label>(); //Initialize heap of Labels

        for(int i = 0; i < nbNodes; i++){ //Initialize all the labels indexed by Node ID // O(n)
            createLabel(graph, labels, i);
        }

        labels[originID].setCurrentCost(0);
        labelsHeap.insert(labels[originID]);
        Label w = null;

        while (!labelsHeap.isEmpty() && labels[destinationID].getMark() == false){ // O(m)
            Label x = labelsHeap.deleteMin();
            x.Mark();
            notifyNodeMarked(x.getCurrentNode());
            for(Arc z : x.getCurrentNode().getSuccessors()){
                if (!data.isAllowed(z)) {
                    continue;
                }
                Label y = labels[z.getDestination().getId()]; // Label of successor
                if(!y.getMark()){ // If successor of x not marked

                    double oldDistance = y.getCurrentCost();
                    double newDistance = x.getCurrentCost() + data.getCost(z);


                    if (Double.isInfinite(oldDistance) && Double.isFinite(newDistance)) {
                        notifyNodeReached(z.getDestination());
                    }
                    if(oldDistance > newDistance){ // If new distance shorter

                        if(Double.isFinite(oldDistance)) {// Update or insert y in the heap
                            labelsHeap.remove(y);
                        }
                        y.setCurrentCost(newDistance);
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

    void createLabel(Graph graph, Label[] labels, int i){
        labels[graph.getNodes().get(i).getId()] = new Label(graph.getNodes().get(i),null);
    }


}
