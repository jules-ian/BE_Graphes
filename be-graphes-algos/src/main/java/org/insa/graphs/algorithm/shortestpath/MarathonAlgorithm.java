package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractSolution;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.*;

import java.util.ArrayList;
import java.util.Collections;

public class MarathonAlgorithm extends ShortestPathAlgorithm {

    public MarathonAlgorithm(ShortestPathData data) {
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

        final double DIST = 42195;
        final double EPSILON = 100;

        //Node destination = data.getDestination();
        //int destinationID = destination.getId();
        //if(Point.distance(origin.getPoint(),destination.getPoint()) > 100){ // Si les sommets de départ et d'arrivé ne sont pas assez proches
        //    System.out.println("Veuillez choisir des sommets à proximité svp.");
        //
        //    return new ShortestPathSolution(data, AbstractSolution.Status.INFEASIBLE);
        //}

        notifyOriginProcessed(origin);


        Label[] labels = new Label[nbNodes]; //Initialize array of Labels


        BinaryHeap<Label> labelsHeap = new BinaryHeap<Label>(); //Initialize heap of Labels

        for(int i = 0; i < nbNodes; i++){ //Initialize all the labels indexed by Node ID // O(n)
            createLabel(graph, labels, i);
        }

        labels[originID].setCurrentCost(0);
        labelsHeap.insert(labels[originID]);

        while (!labelsHeap.isEmpty() ){ // O(m)
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

        ArrayList<Label> Isochrons = isochronSearch(labels, )

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

    private ArrayList<Label> isochronSearch(Label[] labels, double dist, double epsilon){
        ArrayList<Label> result = new ArrayList<>();
        for(Label l : labels){
            if(l.getCurrentCost() >= dist - epsilon && l.getCurrentCost() <= dist + epsilon){
                result.add(l);
            }
        }
        return result;
    }


}
