import java.util.*;

/**
 * Graph class implements graph interface, that displays a undirectional unweighted graph.
 * this class use a dynamic data structure, which allow to contain a large graph.
 * In addition contains the total actions performed, and the number of edges.
 */

public class Undirected_Graph {
    private HashMap<Integer, NodeData> vertices;
    HashMap<Integer, List<EdgeData>> edges;
    HashMap<Integer, List<NodeData>> Cycles;
    boolean flagForCycle = false;
    LinkedList<NodeData> currPath;
    List<LinkedList<NodeData>> allSimplePaths;

    int num_of_edges, num_of_nodes;

    public Undirected_Graph() {
        vertices = new HashMap<>();
        edges = new HashMap<>();
        Cycles = new HashMap<>();
        num_of_edges = 0;
        num_of_nodes = 0;
    }

    public NodeData getNode(int key) {
        return vertices.get(key);
    }

    public void addNode(NodeData n) {
        vertices.put(n.getKey(), n);
        edges.put(n.getKey(), new LinkedList<>());
        num_of_nodes++;
    }

    public void addEdge(int n1, int n2) {
        if(getEdge(n1,n2) == null){
            EdgeData e1 = new EdgeData(getNode(n1), getNode(n2));
            EdgeData e2 = new EdgeData(getNode(n2), getNode(n1));
            edges.get(n1).add(e1);
            edges.get(n2).add(e2);
            num_of_edges++;
        }
    }

    public void removeEdge(int n1, int n2) {
        EdgeData e = getEdge(n1,n2);
        if(e != null){
            edges.get(n1).remove(e);
            e = getEdge(n2,n1);
            edges.get(n2).remove(e);
        }
        num_of_edges--;
    }

    public void removeNode(int key) {
        vertices.remove(key);
        for (EdgeData e : edges.get(key)) {
            edges.get(e.getDest().getKey()).remove(e.getSrc());
            num_of_edges--;
        }
        edges.remove(key);
        num_of_nodes--;
    }

    public Collection<NodeData> get_all_V() {
        return vertices.values();
    }

    public Collection<EdgeData> get_all_E(int key) {
        return edges.get(key);
    }

    public void setPointForSuperNode(NodeData newNode,LinkedList<NodeData> cycle){
        NodeData first_in_cyc=cycle.get(0);
        for(NodeData n: cycle){
            if(n.getKey()!=first_in_cyc.getKey()) {
                if (getEdge(first_in_cyc.getKey(), n.getKey())!=null){
                    Point p1 = first_in_cyc.getP();
                    Point p2 = first_in_cyc.getP();
                    int x= (p1.getX()+p2.getX())/2;
                    int y= (p1.getY()+p2.getY())/2;
                    newNode.setP(x,y);
                }
            }
        }
    }

    public NodeData zipCycle(NodeData newNode,LinkedList<NodeData> cycle) {
//        System.out.println("zip cyc-> "+cycle.toString()+"\nthe superNode is "+newNode.getKey());
//        System.out.println("before Zip");
//        System.out.println(this);
        addNode(newNode);
        setPointForSuperNode(newNode,cycle);
        int key = newNode.getKey();
        for (NodeData node_in_cyc : cycle) {//go over all the nodes that in the cycle
            for(EdgeData e: get_all_E(node_in_cyc.getKey())){
                if(cycle.contains(e.getDest())){ continue; }
                int ni= e.getDest().getKey();
                addEdge(key, ni);
                edges.get(ni).remove(getEdge(ni, node_in_cyc.getKey()));
            }
        }
        Cycles.put(newNode.getKey(), cycle);
//        System.out.println("after Zip");
//        System.out.println(this);
        return newNode;
    }



    public EdgeData getEdge(int key1, int key2) {
        if(!vertices.containsKey(key1) || !vertices.containsKey(key2)) {
            return null;
        }else{
            for (EdgeData e : edges.get(key1)) {
                if (e.getDest().getKey() == key2)
                    return e;
            }
        }
        return null;
    }

    public void UnzipCycles() {
//        System.out.println("before UnZip");
//        System.out.println(this);
        List<Integer> order = new LinkedList<>();
        for(int k : Cycles.keySet()){
            order.add(k);
        }

        for(int i = order.size()-1;i>=0;i--){
            int key = order.get(i);//18
            List<NodeData> cycle = Cycles.get(key);
//            System.out.println("unzip "+key+"\ncyc= "+cycle.toString());
            for (NodeData node_in_cycle : cycle) {//go over all the nodes that in the cycle
                for (NodeData ni : getNi(node_in_cycle)) {//go over all the edges of the current node
                    if (!cycle.contains(ni)) {
                        if(edges.get(ni.getKey()).contains(getEdge(ni.getKey(), key))) {
                            removeEdge(ni.getKey(), key);
                        }
                        EdgeData tmp = new EdgeData(ni, node_in_cycle);
                        if(getEdge(node_in_cycle.getKey(), ni.getKey()).getMatched()){
                            tmp.setMatched(true);
                        }
                        edges.get(ni.getKey()).add(tmp);
                    }
                }
            }
            Cycles.remove(key);
            removeNode(key);
        }
//        System.out.println("after UnZip");
//        System.out.println(this);
    }

    private void resetVisit() {
        for (NodeData n : vertices.values()) {
            n.status = Status.undiscovered;
        }
    }

    public LinkedList<NodeData> checkCycle() {
        flagForCycle = false;
        LinkedList<NodeData> path = new LinkedList<>();
        this.resetVisit();

        NodeData n = this.vertices.values().stream().findFirst().get();
        DFS_move(n, this, path);

        return path;
    }

    private void DFS_move(NodeData n, Undirected_Graph g, LinkedList<NodeData> p) {
        if (flagForCycle || n == null)
            return;

        n.status = Status.discovered;
        if (p.contains(n)) {
            flagForCycle = true;
            while (p.getFirst() != n) {
                p.removeFirst();
            }
            return;
        }
        p.add(n);
        for (EdgeData e : g.edges.get((n.getKey()))) {
            if (p.size() < 2 || e.getDest() != p.get(p.size() - 2)) { // to ignore return to the node that we came from
                DFS_move(e.getDest(), g, p);
            }
        }
        if (!flagForCycle) {
            n.status = Status.finished;
            p.remove(n);
        }
    }

    public Collection<NodeData> getNi(NodeData n) {
        Collection<NodeData> res = new HashSet<>();
        for (EdgeData e : this.get_all_E(n.getKey())) {
            res.add(e.getDest());
        }
        return res;
    }

    private void resetTagAndInfo() {
        for(NodeData n : vertices.values()){
            n.setInfo("");
            n.setTag(0);
        }
    }

    public NodeData getConvertNode(NodeData n){
        for(int key: Cycles.keySet()){
            if(Cycles.get(key).contains(n)){
                return getNode(key);
            }
        }
        return n;
    }

    public LinkedList<EdgeData> getAllEdgesCover() {
        LinkedList<EdgeData> EdgesCover=new LinkedList<>();
        for(NodeData n:vertices.values()){
            for (EdgeData e: get_all_E(n.getKey())){
                if(e.getEdgeCover()){
                    if(!EdgesCover.contains(getEdge(e.getDest().getKey(), e.getSrc().getKey())))
                        EdgesCover.add(e);
                }
            }
        }
        return EdgesCover;
    }


    @Override
    public String toString() {
        String s = "Graph:\n";
                for(int key : this.edges.keySet()){
                    s+="key: "+key+" | ";

                    for(EdgeData e : edges.get(key)){
                        s+=""+e.getDest().getKey()+" ";
                    }
                    s+="\n";
                }
                return s;
    }
    public LinkedList<NodeData> FindAugmentingPath(int src, int dest){
        resetTagAndInfo();
        allSimplePaths = new LinkedList<>();
        currPath = new LinkedList<>();
        DFS(getNode(src),getNode(dest));
        for(LinkedList<NodeData> list : allSimplePaths){
            if(list.size()%2 == 0){
                if(isAugmenting(list)){
                    return list;
                }
            }
        }
        return null;
    }

    public boolean isAugmenting(LinkedList<NodeData> path){
        for (int i = 0; i < path.size() - 2; i++) {
            EdgeData e1 = getEdge(path.get(i).getKey(), path.get(i + 1).getKey());
            EdgeData e2 = getEdge(path.get(i + 1).getKey(), path.get(i+2).getKey());
            if (e1.getMatched()==e2.getMatched()) {
               return false;
            }
        }
        return true;
    }

    private void DFS(NodeData src, NodeData dest) {
        if(src.getTag() == 1){
            return;
        }
        src.setTag(1);
        currPath.add(src);
        if(src == dest){
            LinkedList<NodeData> path = new LinkedList<>(currPath);
            allSimplePaths.add(path);
            src.setTag(0);
            currPath.removeLast();
            return;
        }
        for(NodeData n : getNi(src)){
            DFS(n,dest);
        }
        currPath.removeLast();
        src.setTag(0);
    }

    public LinkedList<EdgeData> getAllMatchedEdges() {
        LinkedList<EdgeData> MatchedEdges=new LinkedList<>();
        for(NodeData n:vertices.values()){
            for (EdgeData e: get_all_E(n.getKey())){
                if(e.getMatched() &&
                    !MatchedEdges.contains(getEdge(e.getDest().getKey(), e.getSrc().getKey())))
                    MatchedEdges.add(e);
            }
        }
        return MatchedEdges;
    }

    public LinkedList<NodeData> getAllMatchedNodes() {
        LinkedList<NodeData> matchedNodes=new LinkedList<>();
        for(NodeData n:vertices.values()){
            if(n.getMatch()){
                matchedNodes.add(n);
            }
        }
        return matchedNodes;
    }

    public LinkedList<NodeData> getUnMatchedNodes() {
        LinkedList<NodeData> UnmatchedNodes=new LinkedList<>();
        for(NodeData n:vertices.values()){
            if(!n.getMatch()){
                UnmatchedNodes.add(n);
            }
        }
        return UnmatchedNodes;
    }


    public void clearUnCovered() {
        LinkedList<EdgeData> UnCovered=new LinkedList<>();
        for(NodeData n: get_all_V()){
            for(EdgeData e: get_all_E(n.getKey())){
                if(!e.getMatched()) {
                    if (!e.getEdgeCover()) {
//                        System.out.println("remove ("+e.getSrc()+", "+e.getDest()+")");
                        UnCovered.add(e);
                    }
                }
            }
        }
        for(EdgeData e: UnCovered){
            removeEdge(e.getSrc().getKey(), e.getDest().getKey());
        }
    }
}
