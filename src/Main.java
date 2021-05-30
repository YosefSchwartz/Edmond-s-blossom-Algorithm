import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) {
        Graph g = new Graph();

        Collection<NodeData> F = new HashSet<>();
        Collection<NodeData> T = new HashSet<>();

        Queue<NodeData> BFS = new LinkedList<>();


        while(!F.isEmpty()){
            NodeData r = F.stream().findAny().get();
            BFS.add(r);
            T = new HashSet<>();
            T.add(r);
            while(!BFS.isEmpty()){
                NodeData v = BFS.poll();
                for(NodeData w : v.getAllNeighbors()){
                    if(!T.contains(w) && w.isMatch()){
                        T.add(w);
                        NodeData mate = w.getMate();
                        T.add(mate);
                        BFS.add(mate);
                    } else if(T.contains(w)){
                        checkCycle(T);
                        //if(ODD)
                    }
                }
            }
        }
    }

    private static void checkCycle(Collection<NodeData> t) {
    }
}
