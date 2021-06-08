import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Algorithms {

    private static NodeData getMate(Undiricted_Graph g, NodeData n2) {
        for (EdgeData e : g.get_all_E(n2.getKey())) {
            if (e.matched)
                return e.getDest();
        }
        return null;
    }

    public static void SetAugmentingPath(Undiricted_Graph g, List<NodeData> path) {
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

    public static NodeData zipCycle(Undiricted_Graph G, Undiricted_Graph T, LinkedList<NodeData> cycle) {
        NodeData newNode = new NodeData();
        T.addNode(newNode);

        int key = newNode.getKey();
        for (NodeData n : cycle) {//go over all the nodes that in the cycle
            for (EdgeData e : T.edges.get(n.getKey())) {//go over all the edges of the current node
                if(cycle.contains(e.getDest())){
                    break;
                }
                else{
                    T.addEdge(e.getDest().getKey(), key);//new connection between the new node and the neighbor
                    T.edges.get(e.getDest().getKey()).remove(T.getEdge(e.getDest().getKey(), e.getSrc().getKey()));//remove the curr node from his neighbor's list
                }
            }
        }
        T.Cycles.put(key, cycle);
        G.Cycles.put(key, cycle);
        List<EdgeData> neighborsOfTheNewCycle= new LinkedList<>();
        for(NodeData n : cycle){
            for(EdgeData e : G.get_all_E(n.getKey())){
                if(!cycle.contains(e.getDest())){
                    neighborsOfTheNewCycle.add(e);
                }
            }
        }
        G.edges.put(newNode.getKey(), neighborsOfTheNewCycle);
        return newNode;
    }

    public static void UnzipCycles(Undiricted_Graph G, Undiricted_Graph T) {
        List<Integer> order = new LinkedList<>();
        for( int k : T.Cycles.keySet()){
            order.add(k);
        }

        for(int i = order.size()-1;i>=0;i--){
            int key = order.get(i); //Key = the index of cycle in Hashmap Cycles
            List<NodeData> cycle = T.Cycles.get(key); //Cycle hold a one cycle on T
            System.out.println("Unzip node "+key+" that contain the cycle "+cycle.toString());
            System.out.println("T before unzip this cycle\n"+T);
            NodeData CycleNode = T.getNode(key);
            for(NodeData n : T.getNi(CycleNode)){
                NodeData tmpNeighbor = null;
                for (NodeData nInCyc : cycle) {
                    if (T.getEdge(nInCyc.getKey(),n.getKey()) != null){
                        tmpNeighbor = nInCyc;
                    }
                }
                if(tmpNeighbor == null){
//                    for(EdgeData e : G.get_all_E())
                } else {
                    T.addEdge(tmpNeighbor.getKey(), n.getKey());
                    T.removeEdge(n.getKey(),CycleNode.getKey());
                }
            }
            T.Cycles.remove(key);
            T.removeNode(key);
            System.out.println("T after unzip this cycle\n"+T);
            G.edges.remove(key);
        }
        System.out.println("Preparing to the next cycle..");
    }

    public static boolean save(Undiricted_Graph g, String file) {
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



    public static boolean load(Undiricted_Graph g, String file) {
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


    public static void main(String[] args) {

    }
}
