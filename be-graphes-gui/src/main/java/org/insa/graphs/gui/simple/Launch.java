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

        int nbTests = 10;
        int algoToTest = 1; // 0 for Dijktra, 1 for A*, 2 for both
        Random rand = new Random();
// On my computer use these :
        String carteInsa = "C:\\Users\\jules\\Desktop\\Graphes\\BE_Graphes\\Maps/insa.mapgr";
        String carteTls = "C:\\Users\\jules\\Desktop\\Graphes\\BE_Graphes\\Maps/toulouse.mapgr";
        String carteCarreDense = "C:\\Users\\jules\\Desktop\\Graphes\\BE_Graphes\\Maps/bordeaux.mapgr";

// At insa use these :
        /*
        String carteInsa = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/insa.mapgr";
        String carteTls = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/toulouse.mapgr";
        String carteCarreDense = "/mnt/commetud/3eme Annee MIC/Graphes-et-Algorithmes/Maps/bordeaux.mapgr";
         */

        // Create a graph readers.
        final GraphReader readerInsa = new BinaryGraphReader(
                new DataInputStream(new BufferedInputStream(new FileInputStream(carteInsa))));
        final GraphReader readerTls = new BinaryGraphReader(
                new DataInputStream(new BufferedInputStream(new FileInputStream(carteTls))));
        final GraphReader readerCD = new BinaryGraphReader(
                new DataInputStream(new BufferedInputStream(new FileInputStream(carteCarreDense))));

        // Read the graph.
        final Graph graphInsa = readerInsa.read();
        final Graph graphTls = readerTls.read();
        final Graph graphCD = readerCD.read();

        Graph [] graphes = {graphCD, graphInsa, graphTls};

        int dijkstraOK = 0;
        int AStarOK = 0;

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

                // Initialisation of the algorithms
                BellmanFordAlgorithm BF = new BellmanFordAlgorithm(data);
                ShortestPathSolution BFSol = BF.run();



                // Print Info
                System.out.println("Map : " + graph.getMapName());
                System.out.println("Origin : " + origin.getId());
                System.out.println("Destination : " + dest.getId());
                System.out.println();

                System.out.println("====== BellmannFord ======");
                System.out.println("Status of the solution : " + BFSol.getStatus().name());
                if (BFSol.getStatus() == AbstractSolution.Status.FEASIBLE || BFSol.getStatus() == AbstractSolution.Status.OPTIMAL) {
                    System.out.println("Length of the solution : " + BFSol.getPath().getLength());
                }
                System.out.println();

                if(algoToTest == 1 || algoToTest == 2) {
                    AStarAlgorithm AStar = new AStarAlgorithm(data);
                    ShortestPathSolution AStarSol = AStar.run();

                    System.out.println("======== A* ========");
                    System.out.println("Status of the solution : " + AStarSol.getStatus().name());

                    // Test if A* is correct
                    if (BFSol.getStatus() == AStarSol.getStatus() && AStarSol.getStatus() == AbstractSolution.Status.FEASIBLE || AStarSol.getStatus() == AbstractSolution.Status.OPTIMAL) {
                        System.out.println("Length of the solution : " + AStarSol.getPath().getLength());
                        if (Math.abs(AStarSol.getPath().getLength() - BFSol.getPath().getLength()) < EPSILON) {
                            System.out.println("===== A* Ok =====");
                            AStarOK++;
                        } else {
                            System.out.println("===== A* not ok =====");
                        }
                    } else if (BFSol.getStatus() == AStarSol.getStatus()) {
                        System.out.println("===== A* Ok =====");
                        AStarOK++;
                    } else {
                        System.out.println("===== A* not ok =====");
                    }
                    System.out.println();
                }
                if(algoToTest == 0 || algoToTest == 2) {
                    DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(data);
                    ShortestPathSolution dijkstraSol = dijkstra.run();

                    System.out.println("======= Dijkstra =======");
                    System.out.println("Status of the solution : " + dijkstraSol.getStatus().name());


                    // Test if Dijkstra is correct
                    if (BFSol.getStatus() == dijkstraSol.getStatus() && dijkstraSol.getStatus() == AbstractSolution.Status.FEASIBLE || dijkstraSol.getStatus() == AbstractSolution.Status.OPTIMAL) {
                        System.out.println("Length of the solution : " + dijkstraSol.getPath().getLength());
                        if (Math.abs(dijkstraSol.getPath().getLength() - BFSol.getPath().getLength()) < EPSILON) {
                            System.out.println("===== Dijkstra Ok =====");
                            dijkstraOK++;
                        } else {
                            System.out.println("===== Dijkstra not ok =====");
                        }
                    } else if (BFSol.getStatus() == dijkstraSol.getStatus()) {
                        System.out.println("===== Dijkstra Ok =====");
                        dijkstraOK++;
                    } else {
                        System.out.println("===== Dijkstra not ok =====");
                    }
                    System.out.println();
                }







            }
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
        }
    }

}
