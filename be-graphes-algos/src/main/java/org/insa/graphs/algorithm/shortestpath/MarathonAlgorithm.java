package org.insa.graphs.algorithm.shortestpath;

import org.insa.graphs.algorithm.AbstractSolution;
import org.insa.graphs.algorithm.utils.BinaryHeap;
import org.insa.graphs.algorithm.utils.ElementNotFoundException;
import org.insa.graphs.model.*;

import java.util.ArrayList;
import java.util.Arrays;
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
        final double EPSILON = data.getDestination().getId();



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
        double minDist = 0;
        labels[originID].setCurrentCost(0);
        labelsHeap.insert(labels[originID]);

        while (!labelsHeap.isEmpty() && minDist <= DIST/3 + EPSILON){ // O(m)
            Label x = labelsHeap.deleteMin();
            x.Mark();
            minDist = x.getCurrentCost();
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


        Label [] isochrons = isochronSearch(labels, DIST/3, EPSILON ); // contents all the isochrons from the origin O(n)
        //int nbchemins = 0;
        Label iso1 = null;
        Label iso2 = null;
        for(Label l : isochrons) {// iterate over all the isochrons l as the origin
            for (Label ll : labels) {// reinitialize the labels
                ll.Unmark();
                ll.setParent(null);
                ll.setCurrentCost(Double.POSITIVE_INFINITY);
            }
            labelsHeap = new BinaryHeap<Label>(); //Initialize heap of Labels

            l.setCurrentCost(0);
            labelsHeap.insert(l);


            minDist = 0;
            while (!labelsHeap.isEmpty() && minDist <= DIST / 3 + EPSILON) { // O(m) Search isochrons of origin
                Label x = labelsHeap.deleteMin();
                x.Mark();
                minDist = x.getCurrentCost();
                notifyNodeMarked(x.getCurrentNode());
                for (Arc z : x.getCurrentNode().getSuccessors()) {
                    if (!data.isAllowed(z)) {
                        continue;
                    }
                    Label y = labels[z.getDestination().getId()]; // Label of successor
                    if (!y.getMark()) { // If successor of x not marked

                        double oldDistance = y.getCurrentCost();
                        double newDistance = x.getCurrentCost() + data.getCost(z);


                        if (Double.isInfinite(oldDistance) && Double.isFinite(newDistance)) {
                            notifyNodeReached(z.getDestination());
                        }
                        if (oldDistance > newDistance) { // If new distance shorter

                            if (Double.isFinite(oldDistance)) {// Update or insert y in the heap
                                labelsHeap.remove(y);
                            }
                            y.setCurrentCost(newDistance);
                            labelsHeap.insert(y);
                            y.setParent(z);

                        }

                    }
                }
            }
            Label[] isochronsB = isochronSearch(labels, DIST / 3, EPSILON); // isochrons of isochron l
            for (Label ll : isochrons) {//O(n log(n))
                if (dichotomySearch(ll, isochronsB)) {// if ll is in isochrons and isochronsB
                    iso1 = ll;
                    iso2 = l;
                    //nbchemins++;
                    break;
                }
            }
            if (iso1 != null) {
                break;
            }
        }
        //System.out.println(nbchemins);

        // no solution
        if (iso1 == null) {
            System.out.println("no solution");
            solution = new ShortestPathSolution(data, AbstractSolution.Status.INFEASIBLE);
        }
        else {


            ShortestPathData data1 = new ShortestPathData(graph, origin, iso1.getCurrentNode(), data.getArcInspector());
            DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data1);
            ShortestPathSolution l1 = dijkstra.run();

            ShortestPathData data2 = new ShortestPathData(graph, iso1.getCurrentNode(), iso2.getCurrentNode(), data.getArcInspector());
            dijkstra = new DijkstraAlgorithm(data2);
            ShortestPathSolution l2 = dijkstra.run();

            ShortestPathData data3 = new ShortestPathData(graph, iso2.getCurrentNode(), origin, data.getArcInspector());
            dijkstra = new DijkstraAlgorithm(data3);
            ShortestPathSolution l3 = dijkstra.run();

            Path finalPath = Path.concatenate(l1.getPath(), l2.getPath(), l3.getPath());

            // The destination has been found, notify the observers.
            //notifyDestinationReached(data.getDestination());

            // Create the path from the array of predecessors...
            //

            // Reverse the path...
            //Collections.reverse(arcs);

            // Create the final solution.
            solution = new ShortestPathSolution(data, AbstractSolution.Status.OPTIMAL, finalPath);
        }


        return solution;
    }

    void createLabel(Graph graph, Label[] labels, int i){
        labels[graph.getNodes().get(i).getId()] = new Label(graph.getNodes().get(i),null);
    }

    private Label[] isochronSearch(Label[] labels, double dist, double epsilon){//O(n)
        ArrayList<Label> result = new ArrayList<>();
        for(Label l : labels){
            if(l.getCurrentCost() >= dist - epsilon && l.getCurrentCost() <= dist + epsilon){
                result.add(l);
            }
        }
        Label[] isochrons = new Label[result.size()];
        isochrons = result.toArray(isochrons);
        return isochrons;
    }

    boolean dichotomySearch(Label l, Label[] labels){// O(log(n))
        if(labels.length ==0){return false;}
        if(l.getID() == labels[labels.length/2].getID()){
            return true;
        }else if(l.getID() < labels[labels.length/2].getID()){
            return dichotomySearch(l, Arrays.copyOfRange(labels, 0, labels.length/2));
        }else{
            return dichotomySearch(l, Arrays.copyOfRange(labels, labels.length/2, labels.length-1));
        }
    }


}
