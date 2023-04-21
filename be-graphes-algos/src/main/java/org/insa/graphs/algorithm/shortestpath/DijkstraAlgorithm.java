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

    @Override
    protected ShortestPathSolution doRun() {
        final ShortestPathData data = getInputData();
        ShortestPathSolution solution = null;

        Graph graph = data.getGraph();
        final int nbNodes = graph.size();
        Node origin = data.getOrigin();
        int originID = origin.getId();
        int destinationID = data.getDestination().getId();

        notifyOriginProcessed(data.getOrigin());


        Label[] labels = new Label[nbNodes]; //Initialize array of Labels


        BinaryHeap<Label> labelsHeap = new BinaryHeap<Label>(); //Initialize heap of Labels

        for(int i = 0; i < nbNodes; i++){ //Initialize all the labels indexed by Node ID
            labels[graph.getNodes().get(i).getId()] = new Label(graph.getNodes().get(i),null);
        }

        labels[originID].setCurrentCost(0f);
        labelsHeap.insert(labels[originID]);

        System.out.println("Before while");
        while (!labelsHeap.isEmpty() && labels[destinationID].getMark() == false){
            System.out.println("Before deleteMin");
            Label x = labelsHeap.deleteMin();
            x.Mark();
            System.out.println("before for");
            for(Arc z : x.getCurrentNode().getSuccessors()){
                Label y = labels[(z.getDestination().getId())]; // Label of successor
                System.out.println("before if marked");
                if(y.getMark() == false){ // If successor of x not marked

                    double oldDistance = y.getCurrentCost();
                    double newDistance = x.getCurrentCost() + data.getCost(z);


                    System.out.println("Before if infinite distance");
                    if (Double.isInfinite(oldDistance) && Double.isFinite(newDistance)) {
                        notifyNodeReached(z.getDestination());
                    }

                    System.out.println("Before if oldDist > newDist");
                    if(oldDistance > newDistance){ // If new distance shorter
                        y.setCurrentCost(newDistance);
                        System.out.println("Before try");
                        try{ // Update or insert y in the heap
                            System.out.println("Before remove");
                            labelsHeap.remove(y);
                            System.out.println("after remove");
                        }catch(ElementNotFoundException e){
                            ;
                        }
                        labelsHeap.insert(y);
                        y.setParent(z);
                        System.out.println("After catch");

                    }

                    System.out.println("After if oldDist > newDist");
                }
            }
        }
        System.out.println("End of While");

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

        System.out.println("End of function");


        return solution;
    }

}
