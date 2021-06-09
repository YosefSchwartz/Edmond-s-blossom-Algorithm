import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Algorithms {

    public static boolean save(Undirected_Graph g, String file) {
        //Create new Json object - graph
        JSONObject graph = new JSONObject();
        //Declare two Json arrays
        JSONArray edges = new JSONArray();
        JSONArray nodes = new JSONArray();
        try {
            //For each node
            for (NodeData n : g.get_all_V()) {
                //Scan all his edges
                for (EdgeData e : g.get_all_E(n.getKey())) {
                    //Declare Json object - edge
                    JSONObject edge = new JSONObject();
                    //Insert the data to this object
                    edge.put("src", e.getSrc());
                    edge.put("dest", e.getDest());
                    //Insert this object to edges array
                    edges.put(edge);
                }
                //Declare Json object - node
                JSONObject node = new JSONObject();
                //Insert the data to this object
                node.put("id", n.getKey());
                JSONObject point = new JSONObject();
                point.put("X",n.getP().getX());
                point.put("Y",n.getP().getY());
                node.put("point",point);
                //Insert this object to nodes array
                nodes.put(node);
            }
            //Insert this both arrays to the graph object
            graph.put("Edges", edges);
            graph.put("Nodes", nodes);

            PrintWriter pw = new PrintWriter(new File(file));
            pw.write(graph.toString());
            pw.close();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    public static boolean load(Undirected_Graph g, String file) {
        try {
            //JSONObject that represent the graph from JSON file
            JSONObject graph = new JSONObject(new String(Files.readAllBytes(Paths.get(file))));

            //Two JSONArray that represents the Edges and Nodes
            JSONArray edges = graph.getJSONArray("Edges");
            JSONArray nodes = graph.getJSONArray("Nodes");

            //Declare of the new graph
//            g = new Graph();
            //For each Node, get the data ,make new node and add him to the graph
            for (int i = 0; i < nodes.length(); i++) {
                JSONObject nJSON = nodes.getJSONObject(i);
                //Build node that contain the id an pos
                NodeData n = new NodeData(nJSON.getInt("id"));
                try {
                    JSONObject pointJSON = nJSON.getJSONObject("point");
                    int x = pointJSON.getInt("X");
                    int y = pointJSON.getInt("Y");
                    n.setP(x,y);
                }catch(Exception e){

                }
                //Add this node to the graph
                g.addNode(n);
            }
            //For each edge, get the data and connect two vertex by the data
            for (int i = 0; i < edges.length(); i++) {
                JSONObject edge = edges.getJSONObject(i);
                int src = edge.getInt("src");
                int dest = edge.getInt("dest");
                g.addEdge(src, dest);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    private static NodeData getMate(Undirected_Graph g, NodeData n2) {
        for (EdgeData e : g.get_all_E(n2.getKey())) {
            if (e.getMatched())
                return e.getDest();
        }
        return null;
    }

    public static void SetAugmentingPath(Undirected_Graph g, List<NodeData> path) {
        if (g.getNode(path.get(0).getKey()).getMatch() != g.getNode(path.get(path.size() - 1).getKey()).getMatch())
            return;
        for (int i = 0; i < path.size() - 1; i++) {
            EdgeData e1 = g.getEdge(path.get(i).getKey(), path.get(i + 1).getKey());
            EdgeData e2 = g.getEdge(path.get(i + 1).getKey(), path.get(i).getKey());
            if (e1 != null && e2 != null) {
                e1.setMatched(!e1.getMatched());
                e2.setMatched(!e2.getMatched());
            }
        }
        g.getNode(path.get(0).getKey()).setMatch(true);
        g.getNode(path.get(path.size() - 1).getKey()).setMatch(true);

    }


    public static void EdmondBlossom(Undirected_Graph g, JFrame f){
        LinkedList<NodeData> F =new LinkedList<>();
        F.addAll(g.get_all_V());
        //TODO - delete
        Collections.sort(F);
        while (!F.isEmpty()){
            NodeData root=F.pop();
            Undirected_Graph T=new Undirected_Graph();
            T.addNode(root);
            Queue<NodeData> q=new LinkedList<>();
            q.add(root);
            while (!q.isEmpty()){
                NodeData v=q.poll();
                for(NodeData nei: g.getNi(v)){
                    if(T.getNode(nei.getKey())==null && nei.getMatch()){
                        NodeData mate =getMate(g, nei);
                        T.addNode(nei);
                        T.addNode(mate);
                        T.addEdge(v.getKey(), nei.getKey());
                        T.addEdge(nei.getKey(), mate.getKey());
                        q.add(mate);
                    }
                    else if(T.getNode(nei.getKey())!=null && T.getEdge(v.getKey(), nei.getKey()) == null){
                        T.addEdge(v.getKey(), nei.getKey());
                        LinkedList<NodeData> cyc =T.checkCycle();
                        if(cyc.size()%2==1) {//even cyc
                            T.removeEdge(v.getKey(), nei.getKey());
                        }else{//odd cyc
                            NodeData SuperNodeForG = new NodeData();
                            NodeData SuperNodeForT= new NodeData(SuperNodeForG.getKey());
                            //TODO - same node? or diff?
                            g.zipCycle(SuperNodeForG, cyc);
                            T.zipCycle(SuperNodeForT, cyc);
                            //TODO- change the remove
                            q.removeAll(cyc);
                            q.add(SuperNodeForG);
                            break;
                        }
                    }
                    else if(F.contains(nei)){
                        T.addNode(nei);
                        T.addEdge(v.getKey(), nei.getKey());
                        g.UnzipCycles();
                        T.UnzipCycles();
                        SetAugmentingPath(g, T.allPath(root.getKey(), nei.getKey()));
                        f.repaint();
                        F.remove(nei);
                        q.clear();
                        break;
                    }
                }
            }

        }
    }


//        public static void MinimumEdgeCover(Undirected_Graph g, JFrame f) throws InterruptedException {
//        EdmondBlossom(g,f);
//        LinkedList<NodeData> unMatched =g.getUnMatchedNodes();
//        for(NodeData n: unMatched){
//            for(NodeData nei: g.getNi(n)){
//                if(nei.getMatch()){
//                    g.getEdge(n.getKey(), nei.getKey()).setEdgeCover(true);
//                    g.getEdge(nei.getKey(), n.getKey()).setEdgeCover(true);
//                    f.repaint();
//                    Thread.sleep(500);
//                    System.out.println();
//                    break;
//                }
//            }
//        }
//    }
    public static void runAllGraphs(){
        JFrame f =new JFrame();
        int i = 1;

        Undirected_Graph AnotherLargeGraph = new Undirected_Graph();
        load(AnotherLargeGraph, "Graphs/Another large graph.json");
        EdmondBlossom(AnotherLargeGraph, f);
        System.out.println("Graph #"+(i++)+": Another large graph");
        System.out.println("Nodes: \n"+AnotherLargeGraph.getAllMatchedNodes().toString());
        System.out.println("Edges: \n"+AnotherLargeGraph.getAllMatchedEdges().toString());
        System.out.println("\n*********************************************\n");

        f =new JFrame();
        Undirected_Graph HouseOfCards = new Undirected_Graph();
        load(HouseOfCards, "Graphs/House of cards.json");
        EdmondBlossom(HouseOfCards, f);
        System.out.println("Graph #"+(i++)+": House of cards");
        System.out.println("Nodes: \n"+HouseOfCards.getAllMatchedNodes().toString());
        System.out.println("Edges: \n"+HouseOfCards.getAllMatchedEdges().toString());
        System.out.println("\n*********************************************\n");

        f =new JFrame();
        Undirected_Graph LargeGraph = new Undirected_Graph();
        load(LargeGraph, "Graphs/Large graph.json");
        EdmondBlossom(LargeGraph, f);
        System.out.println("Graph #"+(i++)+": Large graph");
        System.out.println("Nodes: \n"+LargeGraph.getAllMatchedNodes().toString());
        System.out.println("Edges: \n"+LargeGraph.getAllMatchedEdges().toString());
        System.out.println("\n*********************************************\n");

        f =new JFrame();
        Undirected_Graph LargeHouseofCards = new Undirected_Graph();
        load(LargeHouseofCards, "Graphs/Large house of cards.json");
        EdmondBlossom(LargeHouseofCards, f);
        System.out.println("Graph #"+(i++)+": Large house of cards");
        System.out.println("Nodes: \n"+LargeHouseofCards.getAllMatchedNodes().toString());
        System.out.println("Edges: \n"+LargeHouseofCards.getAllMatchedEdges().toString());
        System.out.println("\n*********************************************\n");

        f =new JFrame();
        Undirected_Graph Pentagon = new Undirected_Graph();
        load(Pentagon, "Graphs/Pentagon.json");
        EdmondBlossom(Pentagon, f);
        System.out.println("Graph #"+(i++)+": Pentagon");
        System.out.println("Nodes: \n"+Pentagon.getAllMatchedNodes().toString());
        System.out.println("Edges: \n"+Pentagon.getAllMatchedEdges().toString());
        System.out.println("\n*********************************************\n");

        f =new JFrame();
        Undirected_Graph Triangle = new Undirected_Graph();
        load(Triangle, "Graphs/Triangle.json");
        EdmondBlossom(Triangle, f);
        System.out.println("Graph #"+(i++)+": Triangle");
        System.out.println("Nodes: \n"+Triangle.getAllMatchedNodes().toString());
        System.out.println("Edges: \n"+Triangle.getAllMatchedEdges().toString());
        System.out.println("\n*********************************************\n");
        f =new JFrame();
        Undirected_Graph TrianglesAndSquares = new Undirected_Graph();
        load(TrianglesAndSquares, "Graphs/Triangles and squares.json");
        EdmondBlossom(TrianglesAndSquares, f);
        System.out.println("Graph #"+(i++)+": Triangles and squares");
        System.out.println("Nodes: \n"+TrianglesAndSquares.getAllMatchedNodes().toString());
        System.out.println("Edges: \n"+TrianglesAndSquares.getAllMatchedEdges().toString());
    }

    public static void main(String[] args) {
       runAllGraphs();
//       load(g, "Graphs/Triangles and squares.json");
//        JFrame f =new JFrame();
////        f.setSize(1100,600);
////        GUI gui=new GUI(g);
////        f.add(gui);
//////        f.setVisible(true);
////        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        EdmondBlossom(g, f);
//        System.out.println("Triangles and squares:");
//        System.out.println("Nodes: \n"+g.getAllMatchedNodes().toString());
//        System.out.println("Edges: \n"+g.getAllMatchedEdges().toString());

//        f.repaint();





    }
}
