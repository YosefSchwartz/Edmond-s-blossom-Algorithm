import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static NodeData getMate(Graph g ,NodeData n2) {
        for (EdgeData e : g.get_all_E(n2.getKey())) {
            if (e.matched)
                return e.getN2();
        }
        return null;
    }

    public static void SetAugmentingPath(Graph g, List<NodeData> path){
        for(int i=0; i<path.size()-1; i++){
            EdgeData e1 = g.getEdge(path.get(i).getKey(), path.get(i+1).getKey());
            EdgeData e2 = g.getEdge(path.get(i+1).getKey(), path.get(i).getKey());
            if(e1!=null && e2!=null){
                e1.setMatched(!e1.getMatched());
                e2.setMatched(!e2.getMatched());
            }
        }
        g.getNode(path.get(0).getKey()).setMatch(true);
        g.getNode(path.get(path.size()-1).getKey()).setMatch(true);

    }



    public static void main(String[] args) {
        Graph g = new Graph();
        for(int i = 0; i<4;i++){
            g.addNode(new NodeData());
        }
        g.addEdge(0,1);
        g.addEdge(0,2);
        g.addEdge(1,2);
        g.addEdge(1,3);

        System.out.println(g);

        Collection<NodeData> F = new HashSet<>();
        F.addAll(g.get_all_V());

        Graph T;

        Queue<NodeData> BFS = new LinkedList<>();


        while(!F.isEmpty()){
            NodeData r = F.stream().findAny().get();
            F.remove(r);
            BFS.add(r);
            T = new Graph();
            T.addNode(r);
            while(!BFS.isEmpty()){
                NodeData v = g.getConvertNode(BFS.poll());
                for(NodeData w : g.getNi(v)){
                    if(!T.get_all_V().contains(w) && w.getMatch()){
                        T.addNode(w);
                        NodeData mate = getMate(g, w);
                        T.addNode(mate);
                        T.addEdge(v.getKey(),w.getKey());
                        T.addEdge(w.getKey(),mate.getKey());
                        BFS.add(mate);
                    } else if(T.get_all_V().contains(w)){
                        T.addEdge(v.getKey(),w.getKey());
                        LinkedList<NodeData> cyc = T.checkCycle();
                        if(cyc.size() % 2 == 1 && cyc.size()>1){
                            NodeData newNode = T.zipCycle(cyc);
                            BFS.add(newNode);
                            break;
                        }
                    }
                    else if(F.contains(w)){
                        //TODO create the augmenting path
                        F.remove(w);
                        T.UnzipCycles();
                        T.addNode(w);
                        T.addEdge(v.getKey(),w.getKey());
                        List<NodeData> res = T.shortestPath(v.getKey(),w.getKey());
                        if(r!=v) {
                            List<NodeData> R_to_V = T.shortestPath(r.getKey(),v.getKey());
                            for(int i = 1; i< res.size();i++){
                                R_to_V.add(res.get(i));
                            }
                            res = R_to_V;
                        }
                        SetAugmentingPath(g,res);
                    }
                }
            }
        }
        System.out.println(g);
        System.out.println("MATCHED!!: ");
        for(List<EdgeData> e : g.edges.values()){
            for(EdgeData edge: e){
                if(edge.getMatched()) {
                    if (edge.getN1().getKey() < edge.getN2().getKey())
                        System.out.println("("+edge.getN1().getKey()+","+edge.getN2().getKey()+")");
                }
            }

        }
    }

//
//
//
//        Graph g = new Graph();
//        for (int i = 1; i <= 13; ++i) {
//            NodeData n = new NodeData();
//            g.addNode(n);
//        }
//
//        g.addEdge(1, 2);
//        g.addEdge(2, 3);
//        g.addEdge(3, 4);
//        g.addEdge(3, 8);
//        g.addEdge(4, 12);
//        g.addEdge(4, 5);
//        g.addEdge(5, 6);
//        g.addEdge(6, 7);
//        g.addEdge(6, 10);
//        g.addEdge(7, 8);
////        g.addEdge(9,11);
//        g.addEdge(8, 9);
//        g.addEdge(10, 11);
//        g.addEdge(12, 13);
//
//        LinkedList<NodeData> res = g.checkCycle();
//        System.out.println(res.toString() + "\n\n");
//
//        System.out.println(g);
//        g.zipCycle(res);
//
//        System.out.println(g);
//
//        g.UnzipCycle(14);
//
//        System.out.println(g);

}
