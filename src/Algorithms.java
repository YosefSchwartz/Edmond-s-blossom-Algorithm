import javax.swing.*;
import java.util.*;
import java.util.List;

public class Algorithms {

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


    public static void EdmondBlossom(Undirected_Graph g, JFrame f) throws InterruptedException {
        LinkedList<NodeData> F =new LinkedList<>(g.get_all_V());
        Collections.sort(F);
        while (F.size()>1){
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
                        if(cyc.size()%2==0 ) { //even cyc
                            T.removeEdge(v.getKey(), nei.getKey());
                        }else{ //odd cyc
                            NodeData SuperNodeForG = new NodeData();
                            NodeData SuperNodeForT= new NodeData(SuperNodeForG.getKey());
                            g.zipCycle(SuperNodeForG, cyc);
                            T.zipCycle(SuperNodeForT, cyc);
                            q.removeAll(cyc);
                            q.add(SuperNodeForG);
                            break;
                        }
                    }
                    else if(F.contains(nei)){
                        g.UnzipCycles();
                        List<NodeData> path = g.FindAugmentingPath(root.getKey(), nei.getKey());
                        g.setCurrAugmentingPath(path);
                        f.repaint();
                        Thread.sleep(1500);
                        SetAugmentingPath(g, path);
                        f.repaint();
                        Thread.sleep(1000);
                        F.remove(nei);
                        q.clear();
                        break;
                    }
                }
            }
        }
        g.currAugmentingPath = new LinkedList<>();
        g.UnzipCycles();
        f.repaint();
    }


    public static void MinimumEdgeCover(Undirected_Graph g) throws InterruptedException {
        JFrame f =new JFrame();
        setFrame(g, f, true);

        EdmondBlossom(g,f);

        LinkedList<NodeData> unMatched =g.getUnMatchedNodes();
        for(NodeData n: unMatched){
            for(NodeData nei: g.getNi(n)){
                if(nei.getMatch()){
                    g.getEdge(n.getKey(), nei.getKey()).setEdgeCover(true);
                    g.getEdge(nei.getKey(), n.getKey()).setEdgeCover(true);
                    f.repaint();
                    Thread.sleep(500);
                    break;
                }
            }
        }

        LinkedList<EdgeData> Edge_cover =new LinkedList<>();
        Edge_cover.addAll(g.getAllEdgesCover());
        Edge_cover.addAll(g.getAllMatchedEdges());

//        g.clearUnCovered();
//        f.repaint();

        System.out.println("Edges in Edge cover:\n"+ Edge_cover.toString());
    }


    public static void setFrame(Undirected_Graph g, JFrame f, boolean b){
        int w=1100;
        NodeData n=g.get_all_V().stream().findFirst().get();
        if(n.getP().getX()==0 && n.getP().getY()==0){
            w=800;
        }
        f.setSize(w,600);
        GUI gui=new GUI(g);
        if(b){
            gui.setEdgeCover(true);
        }
        f.add(gui);
        f.setVisible(true);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public static void TestEdmondBlossom(Undirected_Graph g) throws InterruptedException {

        JFrame f =new JFrame();
        setFrame(g, f, false);
        Thread.sleep(1500);
        EdmondBlossom(g, f);

//        g.clearUnCovered();
//        f.repaint();

        System.out.println("Nodes: \n"+g.getAllMatchedNodes().toString());
        System.out.println("Edges: \n"+g.getAllMatchedEdges().toString());
    }
    public static Undirected_Graph graphCreator(int V, int E, boolean connected) throws Exception {
        if(connected && E<(V-1)){throw new Exception("This graph cannot be connected!");}

        if(E > V*(V-1)){ throw new Exception("This graph, with "+V+" nodes cannot have "+E+" edges");}
        Undirected_Graph g = new Undirected_Graph();
        //Add nodes to the graph
        for(int i = 0;i<V;++i){
            g.addNode(new NodeData());
        }

        if(connected) {
            List<NodeData> l = new LinkedList<>(g.get_all_V());
            Random r = new Random();
            for (NodeData n : g.get_all_V()) {
                if (g.get_all_E(n.getKey()).size() == 0) {
                    int dest = r.nextInt(l.size());
                    dest = l.get(dest).getKey();
                    g.addEdge(n.getKey(), dest);
                    E--;
                }
            }
        }

        List<NodeData> nodes = new LinkedList<>(g.get_all_V());
        Random r = new Random();
        //Connect more random edges to the graph
        while  (E>0) {
            int src = r.nextInt(nodes.size());
            int dest = r.nextInt(nodes.size());
            while (src == dest) {
                dest = r.nextInt(nodes.size());
            }
            if (g.getEdge(src, dest) == null) {
                g.addEdge(src, dest);
                E--;
            }
        }
        return g;
    }

    public static void main(String[] args) throws Exception {
        Undirected_Graph g = new Undirected_Graph();
        /*
        For edge cover we need connected graph.
        so, if you want run Minimum Edge Cover, pass "true", else, "false".
         */
//        g = graphCreator(30,50,true);

//       g.load("Graphs/Graphs_with_Points/PentagonsWithOut20.json");
//        g.load("Graphs/Graphs_with_Points/Pentagons.json");
//        g.load("Graphs/Graphs_with_Points/Vane.json");
        g.load("Graphs/Graphs_with_Points/Triangles and squares.json");

//        TestEdmondBlossom(g);
        MinimumEdgeCover(g);

    }
}
