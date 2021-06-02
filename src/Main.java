import org.w3c.dom.Node;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static NodeData getMate(Graph g, NodeData n2) {
        for (EdgeData e : g.get_all_E(n2.getKey())) {
            if (e.matched)
                return e.getN2();
        }
        return null;
    }

    public static void SetAugmentingPath(Graph g, List<NodeData> path) {
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


    public static void main(String[] args) throws InterruptedException {
        Graph g = new Graph();
        for (int i = 0; i < 18; i++) {
            g.addNode(new NodeData());
        }
        g.addEdge(0, 1);
        g.addEdge(0, 7);

        g.addEdge(1, 4);

        g.addEdge(2, 3);
        g.addEdge(2, 5);
        g.addEdge(2, 6);

        g.addEdge(3, 6);

        g.addEdge(4, 7);
        g.addEdge(4, 8);

        g.addEdge(5, 6);
        g.addEdge(5, 12);
        g.addEdge(5, 13);

        g.addEdge(6, 13);

        g.addEdge(7, 8);
        g.addEdge(7, 10);

        g.addEdge(8, 9);
        g.addEdge(8, 10);

        g.addEdge(9, 12);
        g.addEdge(9, 13);

        g.addEdge(10, 11);

        g.addEdge(11, 12);
        g.addEdge(11, 14);
        g.addEdge(11, 15);

        g.addEdge(12, 13);
        g.addEdge(12, 16);
        g.addEdge(12, 17);

        g.addEdge(13, 16);
        g.addEdge(13, 17);

        g.addEdge(14, 15);

        g.addEdge(15, 16);

        g.addEdge(16, 17);


        System.out.println(g);
//
//        System.out.println(g.getNode(7));
//        System.out.println(g.getNode(11));
//        System.out.println(g.allPath(7,11));

        Collection<NodeData> F = new HashSet<>();
        F.addAll(g.get_all_V());

        Graph T;

        Queue<NodeData> BFS = new LinkedList<>();


        while(!F.isEmpty()){
            NodeData r = F.stream().findFirst().get();
            F.remove(r);
            BFS.add(r);
            T = new Graph();
            T.addNode(r);
            while(!BFS.isEmpty()){
                NodeData vOriginal = BFS.poll();
                NodeData v = g.getConvertNode(vOriginal);

                for(NodeData w : g.getNi(v)){
                    System.out.println(
                            "\nr="+r.getKey()+
                                    "\nv="+v.getKey()+
                                    "\nw="+w.getKey()+
                                    "\nvOriginal="+vOriginal.getKey()+
                                    "\nEDGES IN G -"+g.edges.values().toString());
                    if(!T.get_all_V().contains(w) && w.getMatch()){
                        T.addNode(w);
                        NodeData mate = getMate(g, w);
                        T.addNode(mate);
                        T.addEdge(v.getKey(),w.getKey());
                        T.addEdge(w.getKey(),mate.getKey());
                        BFS.add(mate);
                    } else if(T.get_all_V().contains(w) && (T.getEdge(v.getKey(), w.getKey()) == null)){
                        T.addEdge(v.getKey(),w.getKey());

                        LinkedList<NodeData> cyc = T.checkCycle();
                        if(cyc.size() % 2 == 1 && cyc.size()>1){
                            NodeData newNode = new NodeData();
                            NodeData newNode2 = new NodeData(newNode.getKey());

                            T.zipCycle(newNode,cyc);
                            g.zipCycle(newNode2,cyc);
                            //remove the previous nodes from the queue
//                            int size = BFS.size();
//                            NodeData tmp;
//                            for(int i = 0; i<size;i++) {
//                                tmp = BFS.poll();
//                                if(!cyc.contains(tmp)){
//                                    BFS.add(tmp);
//                                }
//                            }
                            //BFS.add(newNode);
                            break;
                        }else{
                           T.removeEdge(v.getKey(),w.getKey());
                        }
                    } else if(F.contains(w) ){

                        T.addNode(w);
                        System.out.println("V="+v.getKey()+" W="+w.getKey());
                        System.out.println(T.get_all_V().toString());
//                        T.addEdge(v.getKey(),w.getKey());
                        T.addEdge(vOriginal.getKey(),w.getKey());
//                        System.out.println("T before :\n"+T);
//                        System.out.println("T after:\n" + T);

                        T.UnzipCycles();
                        g.UnzipCycles();
                        if(r.getKey() == 14 && w.getKey() == 2){
                            System.out.println("");
                        }
                        LinkedList<NodeData> res = T.allPath(r.getKey(),w.getKey());
                        System.out.println("The path is" + res.toString());
                        boolean before = w.getMatch();
                        SetAugmentingPath(g,res);
                        if(w.getMatch() != before) {
                            F.remove(w);
                            BFS.clear();
                            break;
                        }
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
        //*********************************************
//
//    private static Set<NodeData> ExtractNi(Set<NodeData> ni, Graph g, Graph t, NodeData v) {
//        if(!g.get_all_V().contains(v)){
//            for(NodeData tmp: t.Cycles.get(v.getKey())){
//                if(!g.get_all_V().contains(tmp)){
//                    ni = ExtractNi(ni,g,t,tmp);
//                }
//                ni.addAll(g.getNi(tmp));
//            }
//        } else {
//            ni = (Set) g.getNi(v);
//        }
//        return ni;
//    }

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
