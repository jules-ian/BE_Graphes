package org.insa.graphs.gui.simple;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.insa.graphs.algorithm.AbstractSolution;
import org.insa.graphs.algorithm.ArcInspector;
import org.insa.graphs.algorithm.ArcInspectorFactory;
import org.insa.graphs.algorithm.shortestpath.*;
import org.insa.graphs.gui.drawing.Drawing;
import org.insa.graphs.gui.drawing.components.BasicDrawing;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.io.BinaryGraphReader;
import org.insa.graphs.model.io.BinaryPathReader;
import org.insa.graphs.model.io.GraphReader;
import org.insa.graphs.model.io.PathReader;

public class Launch {

    static final double EPSILON = 0.001;
    /**
     * Create a new Drawing inside a JFrame an return it.
     * 
     * @return The created drawing.
     * 
     * @throws Exception if something wrong happens when creating the graph.
     */
    public static Drawing createDrawing() throws Exception {
        BasicDrawing basicDrawing = new BasicDrawing();
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame("BE Graphes - Launch");
                frame.setLayout(new BorderLayout());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);
                frame.setSize(new Dimension(800, 600));
                frame.setContentPane(basicDrawing);
                frame.validate();
            }
        });
        return basicDrawing;
    }

    public static void main(String[] args) throws Exception {

        /*
        // Visit these directory to see the list of available files on Commetud.
        final String mapName = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/insa.mapgr";
        final String pathName = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Paths/path_fr31insa_rangueil_r2.path";

        // Create a graph reader.
        final GraphReader reader = new BinaryGraphReader(
                new DataInputStream(new BufferedInputStream(new FileInputStream(mapName))));

        // Read the graph.
        final Graph graph = reader.read();

        // Create the drawing:
        final Drawing drawing = createDrawing();

        // Draw the graph on the drawing.
        drawing.drawGraph(graph);

        // Create a PathReader.
        final PathReader pathReader = new BinaryPathReader(new DataInputStream(new BufferedInputStream(new FileInputStream(pathName))));

        // Read the path.
        final Path path = pathReader.readPath(graph);

        // Draw the path.
        drawing.drawPath(path, Color.RED);

         */

        int nbTests = 50;
        int algoToTest = 3; // 0 for Dijktra, 1 for A*, 2 for both, 3 for only Dijkstra and A*
        Random rand = new Random();
// On my computer use these :
        ///*
        String carteInsa = "C:\\Users\\jules\\Desktop\\Graphes\\BE_Graphes\\Maps/insa.mapgr";
        String carteTls = "C:\\Users\\jules\\Desktop\\Graphes\\BE_Graphes\\Maps/toulouse.mapgr";
        String carteBelgium = "C:\\Users\\jules\\Desktop\\Graphes\\BE_Graphes\\Maps/belgium.mapgr";
        String carteCarreDense = "C:\\Users\\jules\\Desktop\\Graphes\\BE_Graphes\\Maps/bordeaux.mapgr";

         //*/

// At insa use these :
        /*
        String carteInsa = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/insa.mapgr";
        String carteTls = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/toulouse.mapgr";
        String carteCarreDense = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/carre-dense.mapgr";
        String carteBordeaux = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/bordeaux.mapgr";
        */


        // Create a graph readers.
        final GraphReader readerInsa = new BinaryGraphReader(
                new DataInputStream(new BufferedInputStream(new FileInputStream(carteInsa))));
        final GraphReader readerTls = new BinaryGraphReader(
                new DataInputStream(new BufferedInputStream(new FileInputStream(carteTls))));
        final GraphReader readerCD = new BinaryGraphReader(
                new DataInputStream(new BufferedInputStream(new FileInputStream(carteCarreDense))));
        //final GraphReader readerBd = new BinaryGraphReader(
        //      new DataInputStream(new BufferedInputStream(new FileInputStream(carteBordeaux))));

        final GraphReader readerBelgium = new BinaryGraphReader(
              new DataInputStream(new BufferedInputStream(new FileInputStream(carteBelgium))));

        // Read the graph.
        final Graph graphInsa = readerInsa.read();
        final Graph graphTls = readerTls.read();
        final Graph graphCD = readerCD.read();
        final Graph graphBelgium = readerBelgium.read();
        //final Graph graphBd = readerBd.read();

        //Graph [] graphes = {graphInsa, graphTls, graphBd};
        //Graph [] graphes = {graphInsa, graphTls, graphCD};
        Graph [] graphes = {graphBelgium};

        int dijkstraOK = 0;
        int AStarOK = 0;
        int totalAStarOK = 0;
        int totalDijOK = 0;
        long DijkstraTime = 0;
        long AstarTime = 0;
        long TotalDijkstraTime = 0;
        long TotalAstarTime = 0;

        for (ArcInspector a : ArcInspectorFactory.getAllFilters()) {
            for(int i = 0; i < nbTests ; i++) {
                int map = rand.nextInt(graphes.length); // choose a random map
                int originID = rand.nextInt(graphes[map].size()); // choose a node id among those in the map (origin)
                int destID = rand.nextInt(graphes[map].size()); // choose a node id among those in the map (destination)

                // Initialisation of graphs and ShortestPathData
                Graph graph = graphes[map];
                Node origin = graph.get(originID);
                Node dest = graph.get(destID);
                ShortestPathData data = new ShortestPathData(graph, origin, dest, a);




                // Print Info
                System.out.println("Map : " + graph.getMapName());
                System.out.println("Origin : " + origin.getId());
                System.out.println("Destination : " + dest.getId());
                System.out.println();
                if(algoToTest == 3) {
                        AStarAlgorithm AStar = new AStarAlgorithm(data);
                        long startTime = System.currentTimeMillis();
                        ShortestPathSolution AStarSol = AStar.run();
                        long endTime = System.currentTimeMillis();
                        AstarTime = endTime - startTime;
                        TotalAstarTime += AstarTime;

                        System.out.println("======== A* ========");
                        System.out.println("Status of the solution : " + AStarSol.getStatus().name());

                        // Test if A* is correct
                        if ( AStarSol.getStatus() == AbstractSolution.Status.FEASIBLE || AStarSol.getStatus() == AbstractSolution.Status.OPTIMAL) {
                            System.out.println("Length of the solution : " + AStarSol.getPath().getLength());
                            System.out.println("Duration of the algorithm : " + (AstarTime) + "ms");
                    }

                        System.out.println();
                        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
                        long startTimeD = System.currentTimeMillis();
                        ShortestPathSolution dijkstraSol = dijkstra.run();
                        long endTimeD = System.currentTimeMillis();
                        DijkstraTime = endTimeD - startTimeD;
                        TotalDijkstraTime += DijkstraTime;

                        System.out.println("======= Dijkstra =======");
                        System.out.println("Status of the solution : " + dijkstraSol.getStatus().name());


                        // Test if Dijkstra is correct
                        if (dijkstraSol.getStatus() == AbstractSolution.Status.FEASIBLE || dijkstraSol.getStatus() == AbstractSolution.Status.OPTIMAL) {
                            System.out.println("Length of the solution : " + dijkstraSol.getPath().getLength());
                            System.out.println("Duration of the algorithm : " + (DijkstraTime) + "ms");

                        }
                        System.out.println();





                    if(algoToTest == 2 && dijkstraSol.getStatus() != AbstractSolution.Status.INFEASIBLE){
                        if(AstarTime < DijkstraTime){System.out.println("Cool");}
                        else if(AstarTime <= DijkstraTime){System.out.println("nice");}
                    }


                }else{

                    // Initialisation of the algorithms
                    BellmanFordAlgorithm BF = new BellmanFordAlgorithm(data);
                    ShortestPathSolution BFSol = BF.run();
                    System.out.println("====== BellmannFord ======");
                    System.out.println("Status of the solution : " + BFSol.getStatus().name());
                    if (BFSol.getStatus() == AbstractSolution.Status.FEASIBLE || BFSol.getStatus() == AbstractSolution.Status.OPTIMAL) {
                        System.out.println("Length of the solution : " + BFSol.getPath().getLength());
                    }
                    System.out.println();

                    if(algoToTest == 1 || algoToTest == 2) {
                        AStarAlgorithm AStar = new AStarAlgorithm(data);
                        long startTime = System.currentTimeMillis();
                        ShortestPathSolution AStarSol = AStar.run();
                        long endTime = System.currentTimeMillis();
                        AstarTime = endTime - startTime;
                        TotalAstarTime += AstarTime;

                        System.out.println("======== A* ========");
                        System.out.println("Status of the solution : " + AStarSol.getStatus().name());

                        // Test if A* is correct
                        if (BFSol.getStatus() == AStarSol.getStatus() && AStarSol.getStatus() == AbstractSolution.Status.FEASIBLE || AStarSol.getStatus() == AbstractSolution.Status.OPTIMAL) {
                            System.out.println("Length of the solution : " + AStarSol.getPath().getLength());
                            System.out.println("Duration of the algorithm : " + (AstarTime) + "ms");
                            if (Math.abs(AStarSol.getPath().getLength() - BFSol.getPath().getLength()) < EPSILON) {
                                System.out.println("===== A* Ok =====");
                                AStarOK++;
                                totalAStarOK++;
                            } else {
                                System.out.println("===== A* not ok =====");
                            }
                        } else if (BFSol.getStatus() == AStarSol.getStatus()) {
                            System.out.println("===== A* Ok =====");
                            AStarOK++;
                            totalAStarOK++;
                        } else {
                            System.out.println("===== A* not ok =====");
                        }
                        System.out.println();
                    }
                    if(algoToTest == 0 || algoToTest == 2) {
                        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
                        long startTimeD = System.currentTimeMillis();
                        ShortestPathSolution dijkstraSol = dijkstra.run();
                        long endTimeD = System.currentTimeMillis();
                        DijkstraTime = endTimeD - startTimeD;
                        TotalDijkstraTime += DijkstraTime;

                        System.out.println("======= Dijkstra =======");
                        System.out.println("Status of the solution : " + dijkstraSol.getStatus().name());


                        // Test if Dijkstra is correct
                        if (BFSol.getStatus() == dijkstraSol.getStatus() && dijkstraSol.getStatus() == AbstractSolution.Status.FEASIBLE || dijkstraSol.getStatus() == AbstractSolution.Status.OPTIMAL) {
                            System.out.println("Length of the solution : " + dijkstraSol.getPath().getLength());
                            System.out.println("Duration of the algorithm : " + (DijkstraTime) + "ms");
                            if (Math.abs(dijkstraSol.getPath().getLength() - BFSol.getPath().getLength()) < EPSILON) {
                                System.out.println("===== Dijkstra Ok =====");
                                dijkstraOK++;
                                totalDijOK++;
                            } else {
                                System.out.println("===== Dijkstra not ok =====");
                            }
                        } else if (BFSol.getStatus() == dijkstraSol.getStatus()) {
                            System.out.println("===== Dijkstra Ok =====");
                            dijkstraOK++;
                            totalDijOK++;
                        } else {
                            System.out.println("===== Dijkstra not ok =====");
                        }
                        System.out.println();
                    }




                    if(algoToTest == 2 && BFSol.getStatus() != AbstractSolution.Status.INFEASIBLE){
                        if(AstarTime < DijkstraTime){System.out.println("Cool");}
                        else if(AstarTime <= DijkstraTime){System.out.println("nice");}
                    }


                }}
            System.out.println("--------------------------------------------------------");
            System.out.println("Filter : " + a);
            if(algoToTest == 0 || algoToTest == 2) {
                System.out.println(dijkstraOK + "/" + nbTests + " Dijkstra are OK");
            }
            if(algoToTest == 1 || algoToTest == 2) {
                System.out.println(AStarOK + "/" + nbTests + " A* are OK");
            }
            dijkstraOK = 0;
            AStarOK = 0;
            //break;
        }

        System.out.println("========================= Total ============================");
        if(algoToTest == 1 || algoToTest == 2 || algoToTest == 3) {
            System.out.println(totalAStarOK + "/" + nbTests*ArcInspectorFactory.getAllFilters().size() + " A* are OK");
            System.out.println("Average execution time of A* : " + TotalAstarTime/(nbTests*ArcInspectorFactory.getAllFilters().size()) + " ms");
            System.out.println("Average initialization time of A* : " + AStarAlgorithm.initTime/(nbTests*ArcInspectorFactory.getAllFilters().size()) + " ms");
            System.out.println("Average finding time of A* : " + (TotalAstarTime-AStarAlgorithm.initTime)/(nbTests*ArcInspectorFactory.getAllFilters().size()) + " ms");
        }
        if(algoToTest == 0 || algoToTest == 2 || algoToTest == 3) {
            System.out.println(totalDijOK + "/" + nbTests*ArcInspectorFactory.getAllFilters().size() + " Dijkstra are OK");
            System.out.println("Average execution time of Dijkstra : " + TotalDijkstraTime/(nbTests*ArcInspectorFactory.getAllFilters().size()) + " ms");
            System.out.println("Average initialization time of Dijkstra : " + DijkstraAlgorithm.initTime/(nbTests*ArcInspectorFactory.getAllFilters().size()) + " ms");
            System.out.println("Average finding time of Dijkstra : " + (TotalDijkstraTime-DijkstraAlgorithm.initTime)/(nbTests*ArcInspectorFactory.getAllFilters().size()) + " ms");
        }
    }

}
