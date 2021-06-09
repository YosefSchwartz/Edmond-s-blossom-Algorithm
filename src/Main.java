//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.io.PrintWriter;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.*;
//
//
//public class Main {
//
//    private static NodeData getMate(Undirected_Graph g, NodeData n2) {
//        for (EdgeData e : g.get_all_E(n2.getKey())) {
//            if (e.getMatched())
//                return e.getDest();
//        }
//        return null;
//    }
//
//    public static void SetAugmentingPath(Undirected_Graph g, List<NodeData> path) {
//        if (g.getNode(path.get(0).getKey()).getMatch() != g.getNode(path.get(path.size() - 1).getKey()).getMatch())
//            return;
//        for (int i = 0; i < path.size() - 1; i++) {
//            EdgeData e1 = g.getEdge(path.get(i).getKey(), path.get(i + 1).getKey());
//            EdgeData e2 = g.getEdge(path.get(i + 1).getKey(), path.get(i).getKey());
//            if (e1 != null && e2 != null) {
//                e1.setMatched(!e1.getMatched());
//                e2.setMatched(!e2.getMatched());
//            }
//        }
//        g.getNode(path.get(0).getKey()).setMatch(true);
//        g.getNode(path.get(path.size() - 1).getKey()).setMatch(true);
//
//    }
//
//    public static NodeData zipCycle(Undirected_Graph G, Undirected_Graph T, LinkedList<NodeData> cycle) {
//        NodeData newNode = new NodeData();
//        T.addNode(newNode);
//
//        int key = newNode.getKey();
//        for (NodeData n : cycle) {//go over all the nodes that in the cycle
//            for (EdgeData e : T.edges.get(n.getKey())) {//go over all the edges of the current node
//                if(cycle.contains(e.getDest())){
//                    break;
//                }
//                else{
//                    T.addEdge(e.getDest().getKey(), key);//new connection between the new node and the neighbor
//                    T.edges.get(e.getDest().getKey()).remove(T.getEdge(e.getDest().getKey(), e.getSrc().getKey()));//remove the curr node from his neighbor's list
//                }
//            }
//        }
//        T.Cycles.put(key, cycle);
//        G.Cycles.put(key, cycle);
//        List<EdgeData> neighborsOfTheNewCycle= new LinkedList<>();
//        for(NodeData n : cycle){
//            for(EdgeData e : G.get_all_E(n.getKey())){
//                if(!cycle.contains(e.getDest())){
//                  neighborsOfTheNewCycle.add(e);
//                }
//            }
//        }
//        G.edges.put(newNode.getKey(), neighborsOfTheNewCycle);
//        return newNode;
//    }
//
//    public static void UnzipCycles(Undirected_Graph G, Undirected_Graph T) {
//        List<Integer> order = new LinkedList<>();
//        for( int k : T.Cycles.keySet()){
//            order.add(k);
//        }
//
//        for(int i = order.size()-1;i>=0;i--){
//            int key = order.get(i); //Key = the index of cycle in Hashmap Cycles
//            List<NodeData> cycle = T.Cycles.get(key); //Cycle hold a one cycle on T
//            System.out.println("Unzip node "+key+" that contain the cycle "+cycle.toString());
//            System.out.println("T before unzip this cycle\n"+T);
//            NodeData CycleNode = T.getNode(key);
//            for(NodeData n : T.getNi(CycleNode)){
//                NodeData tmpNeighbor = null;
//                for (NodeData nInCyc : cycle) {
//                    if (T.getEdge(nInCyc.getKey(),n.getKey()) != null){
//                        tmpNeighbor = nInCyc;
//                    }
//                }
//                if(tmpNeighbor == null){
////                    for(EdgeData e : G.get_all_E())
//                } else {
//                    T.addEdge(tmpNeighbor.getKey(), n.getKey());
//                    T.removeEdge(n.getKey(),CycleNode.getKey());
//                }
//            }
//            T.Cycles.remove(key);
//            T.removeNode(key);
//            System.out.println("T after unzip this cycle\n"+T);
//            G.edges.remove(key);
//        }
//        System.out.println("Preparing to the next cycle..");
//    }
//
//    public static boolean save(Undirected_Graph g, String file) {
//        //Create new Json object - graph
//        JSONObject graph = new JSONObject();
//        //Declare two Json arrays
//        JSONArray edges = new JSONArray();
//        JSONArray nodes = new JSONArray();
//        try {
//            //For each node
//            for (NodeData n : g.get_all_V()) {
//                //Scan all his edges
//                for (EdgeData e : g.get_all_E(n.getKey())) {
//                    //Declare Json object - edge
//                    JSONObject edge = new JSONObject();
//                    //Insert the data to this object
//                    edge.put("src", e.getSrc());
//                    edge.put("dest", e.getDest());
//                    //Insert this object to edges array
//                    edges.put(edge);
//                }
//                //Declare Json object - node
//                JSONObject node = new JSONObject();
//                //Insert the data to this object
//                node.put("id", n.getKey());
//                //Insert this object to nodes array
//                nodes.put(node);
//            }
//            //Insert this both arrays to the graph object
//            graph.put("Edges", edges);
//            graph.put("Nodes", nodes);
//
//            PrintWriter pw = new PrintWriter(new File(file));
//            pw.write(graph.toString());
//            pw.close();
//            return true;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//
//    public static boolean load(Undirected_Graph g, String file) {
//        try {
//            //JSONObject that represent the graph from JSON file
//            JSONObject graph = new JSONObject(new String(Files.readAllBytes(Paths.get(file))));
//
//            //Two JSONArray that represents the Edges and Nodes
//            JSONArray edges = graph.getJSONArray("Edges");
//            JSONArray nodes = graph.getJSONArray("Nodes");
//
//            //Declare of the new graph
////            g = new Graph();
//            //For each Node, get the data ,make new node and add him to the graph
//            for (int i = 0; i < nodes.length(); i++) {
//                JSONObject nJSON = nodes.getJSONObject(i);
//                //Build node that contain the id an pos
//                NodeData n = new NodeData(nJSON.getInt("id"));
//                //Add this node to the graph
//                g.addNode(n);
//            }
//            //For each edge, get the data and connect two vertex by the data
//            for (int i = 0; i < edges.length(); i++) {
//                JSONObject edge = edges.getJSONObject(i);
//                int src = edge.getInt("src");
//                int dest = edge.getInt("dest");
//                g.addEdge(src, dest);
//            }
//            return true;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
////    public static void MinimumEdgeCover(Undirected_Graph g, JFrame f) throws InterruptedException {
////        Hungarian_m(g, f);
////        LinkedList<NodeData> unMatched =g.getUnMatchedNodes();
////        for(NodeData n: unMatched){
////            for(NodeData nei: g.getNi(n)){
////                if(nei.getMatch()){
////                    g.getEdge(n.getKey(), nei.getKey()).setEdgeCover(true);
////                    g.getEdge(nei.getKey(), n.getKey()).setEdgeCover(true);
////                    f.repaint();
////                    Thread.sleep(500);
////                    System.out.println();
////                    break;
////                }
////            }
////        }
////    }
//
////    public static void TestEdgeCover(Undirected_Graph g) throws InterruptedException {
////        JFrame f =new JFrame();
////        f.setSize(1100,600);
////        GUI gui=new GUI(g);
////        gui.setEdgeCover(true);
////        f.add(gui);
////        f.setVisible(true);
////        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
////
////        Thread.sleep(1500);
////        Hungarian_m(g,f);
////        MinimumEdgeCover(g,f);
////        g.clearUnCovered();
////        f.repaint();
////        LinkedList<edgeData> Edge_cover =new LinkedList<>();
////        Edge_cover.addAll(g.getAllEdgesCover());
////        Edge_cover.addAll(g.getAllMatchedEdges());
////        System.out.println("Edges in Edge cover:\n"+ Edge_cover.toString());
////    }
//
//
//
//
//    public static void main(String[] args) throws InterruptedException {
//        Undirected_Graph g = new Undirected_Graph();
//        load(g,"./Graphs/House of cards.json");
//
//
//        System.out.println(g);
//
////        save("./Graphs/Triangle.json");
//
//
//        LinkedList<NodeData> F = new LinkedList<>();
//        F.addAll(g.get_all_V());
//
//        Collections.sort(F);
//
//        Undirected_Graph T;
//
//        Queue<NodeData> BFS = new LinkedList<>();
//
//
//        while(!F.isEmpty()){
//            NodeData r = F.stream().findFirst().get();
//            System.out.println("R = "+r.getKey());
//            F.remove(r);
//            BFS.add(r);
//            T = new Undirected_Graph();
//            T.addNode(r);
//            while(!BFS.isEmpty()){
//                NodeData vOriginal = BFS.poll();
//                NodeData v = T.getConvertNode(vOriginal);
//                System.out.println(
//                                "V="+v.getKey()+
//                                "\nvOriginal="+vOriginal.getKey());
//                for(NodeData w : g.getNi(v)){
//                    System.out.println("W="+w.getKey());
//                    System.out.println("Check what going on with w");
//                    if(!T.get_all_V().contains(w) && w.getMatch()){
//                        T.addNode(w);
//                        NodeData mate = getMate(g, w);
//                        System.out.println("w isn't in T, and w=("+w.getKey()+") is matched to mate=("+mate.getKey()+")");
//                        System.out.println("Adding w to T");
//                        T.addNode(mate);
//                        System.out.println("Adding mate to T");
//                        if(vOriginal != v) {
//                            System.out.println("vOriginal different than v");
//                            T.addEdge(vOriginal.getKey(), w.getKey());
//                            T.edges.get(w.getKey()).remove(T.getEdge(w.getKey(), vOriginal.getKey()));
//                            System.out.println("Connecting vOriginal to w and delete the edge from w to vOriginal" +
//                                    "\n(w know just the cyc!");
//                        }
//                        T.addEdge(v.getKey(),w.getKey());
//                        System.out.println("Connect v to w");
//                        T.addEdge(w.getKey(),mate.getKey());
//                        System.out.println("Connect w to mate");
//
//
//                        BFS.add(mate);
//                        System.out.println("Add mate to BFS queue");
//                    } else if(T.get_all_V().contains(w) && (T.getEdge(v.getKey(), w.getKey()) == null)){
//                        if(vOriginal != v) {
//                            System.out.println("vOriginal different than v");
//                            T.addEdge(vOriginal.getKey(), w.getKey());
//                            T.edges.get(w.getKey()).remove(T.getEdge(w.getKey(), vOriginal.getKey()));
//                            System.out.println("Connecting vOriginal to w and delete the edge from w to vOriginal" +
//                                    "\n(w know just the cyc!");
//                        }
//                        T.addEdge(v.getKey(),w.getKey());
//                        System.out.println("Connect v to w");
//
//                        System.out.println("T before \n"+T);
//                        LinkedList<NodeData> cyc = T.checkCycle();
//                        System.out.println("Get the cycle: " + cyc.toString());
//                        if(cyc.size() % 2 == 1 && cyc.size()>1){
//                            System.out.println("The cycle is odd!");
//                            System.out.println("Get zip the cycle");
//                            zipCycle(g,T,cyc);
//
//                            System.out.println("T after zip\n"+T);
//                            break;
//                        }else{
//                           T.removeEdge(v.getKey(),w.getKey());
//                            System.out.println("the cycle is even, will remove (v,w)");
//                            System.out.println("T after \n"+T);
//                            if(vOriginal != v) {
//                                System.out.println("vOriginal different than v");
//                                T.edges.get(w.getKey()).remove(T.getEdge(vOriginal.getKey(),w.getKey()));
//                                System.out.println("remove vOriginal to w");
//                            }
//                        }
//                    } else if(F.contains(w) ){
//                        System.out.println("w is in F!");
//                        T.addNode(w);
//                        System.out.println("Add w to T");
//                        System.out.println(T.get_all_V().toString());
//                        if(vOriginal != v) {
//                            System.out.println("vOriginal different than v");
//                            T.addEdge(vOriginal.getKey(), w.getKey());
//                            T.edges.get(w.getKey()).remove(T.getEdge(w.getKey(), vOriginal.getKey()));
//                            System.out.println("Connecting vOriginal to w and delete the edge from w to vOriginal" +
//                                    "\n(w know just the cyc!");
//                        }
//                        T.addEdge(v.getKey(),w.getKey());
//                        System.out.println("Connect v to w");
//
//                        System.out.println("Unzip the cycles!");
//                        UnzipCycles(g,T);
//                        System.out.println("T after remove all cycles \n"+T );
//
//                        LinkedList<NodeData> res = T.allPath(r.getKey(),w.getKey());
//                        System.out.println("The path is" + res.toString());
//                        boolean before = w.getMatch();
//                        SetAugmentingPath(g,res);
//                        if(w.getMatch() != before) {
//                            F.remove(w);
//                            BFS.clear();
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        System.out.println(g);
//        System.out.println("MATCHED!!: ");
//        for(List<EdgeData> e : g.edges.values()){
//            for (EdgeData edge : e) {
//                if (edge.getMatched()) {
//                    if (edge.getSrc().getKey() < edge.getDest().getKey())
//                        System.out.println("(" + edge.getSrc().getKey() + "," + edge.getDest().getKey() + ")");
//                }
//            }
//        }
//    }
//}
