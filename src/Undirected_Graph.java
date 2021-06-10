import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Undirected_Graph {
    private HashMap<Integer, NodeData> vertices;
    HashMap<Integer, List<EdgeData>> edges;
    HashMap<Integer, List<NodeData>> Cycles;
    boolean flagForCycle = false;
    LinkedList<NodeData> currPath;
    List<LinkedList<NodeData>> allSimplePaths;
    List<NodeData> currAugmentingPath;

    int num_of_edges, num_of_nodes;

    public Undirected_Graph() {
        vertices = new HashMap<>();
        edges = new HashMap<>();
        Cycles = new HashMap<>();
        currAugmentingPath = new LinkedList<>();
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
        if(getEdge(n1,n2) != null || n1==n2){return;}
        EdgeData e1 = new EdgeData(getNode(n1), getNode(n2));
        EdgeData e2 = new EdgeData(getNode(n2), getNode(n1));
        edges.get(n1).add(e1);
        edges.get(n2).add(e2);
        num_of_edges++;

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
            edges.get(e.getDest().getKey()).remove(getEdge(e.getDest().getKey(),key));
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
        LinkedList<Integer> cycleKeys=new LinkedList<>();
        for(NodeData n: cycle){
            cycleKeys.add(n.getKey());
        }
        addNode(newNode);
        setPointForSuperNode(newNode,cycle);
        int key = newNode.getKey();
        for (NodeData node_in_cyc : cycle) {//go over all the nodes that in the cycle
            for(EdgeData e: get_all_E(node_in_cyc.getKey())){
                if(cycleKeys.contains(e.getDest().getKey())){
                    continue;
                }
                int ni= e.getDest().getKey();
                addEdge(key, ni);
                getEdge(key,ni).setMatched(e.getMatched());
                getEdge(ni,key).setMatched(e.getMatched());
                edges.get(ni).remove(getEdge(ni, node_in_cyc.getKey()));

            }
        }
        Cycles.put(newNode.getKey(), cycle);
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
        List<Integer> cycleKeys = new LinkedList<>(Cycles.keySet());
        Collections.sort(cycleKeys);

        for(int i = cycleKeys.size()-1;i>=0;i--) {
            int key = cycleKeys.get(i);
            List<NodeData> cycle = Cycles.get(key);
            for (NodeData node_in_cycle : cycle) {//go over all the nodes that in the cycle
                for (NodeData ni : getNi(node_in_cycle)) {//go over all the edges of the current node
                    if (!cycle.contains(ni)) {
                        if (edges.get(ni.getKey()).contains(getEdge(ni.getKey(), key))) {
                            removeEdge(ni.getKey(), key);
                        }
                        EdgeData tmp = new EdgeData(ni, node_in_cycle);
                        if (getEdge(node_in_cycle.getKey(), ni.getKey()).getMatched()) {
                            tmp.setMatched(true);
                        }
                        edges.get(ni.getKey()).add(tmp);
                    }
                }
            }
            Cycles.remove(key);
            removeNode(key);
        }
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

    public void setCurrAugmentingPath(List<NodeData> path){
        this.currAugmentingPath = path;
    }


    public void clearUnCovered() {
        LinkedList<EdgeData> UnCovered=new LinkedList<>();
        for(NodeData n: get_all_V()){
            for(EdgeData e: get_all_E(n.getKey())){
                if(!e.getMatched()) {
                    if (!e.getEdgeCover()) {
                        UnCovered.add(e);
                    }
                }
            }
        }
        for(EdgeData e: UnCovered){
            removeEdge(e.getSrc().getKey(), e.getDest().getKey());
        }
    }
    public boolean load(String file) {
        try {
            //JSONObject that represent the graph from JSON file
            JSONObject graph = new JSONObject(new String(Files.readAllBytes(Paths.get(file))));

            //Two JSONArray that represents the Edges and Nodes
            JSONArray edges = graph.getJSONArray("Edges");
            JSONArray nodes = graph.getJSONArray("Nodes");

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
                addNode(n);
            }
            //For each edge, get the data and connect two vertex by the data
            for (int i = 0; i < edges.length(); i++) {
                JSONObject edge = edges.getJSONObject(i);
                int src = edge.getInt("src");
                int dest = edge.getInt("dest");
                addEdge(src, dest);
            }
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean save(String file) {
        //Create new Json object - graph
        JSONObject graph = new JSONObject();
        //Declare two Json arrays
        JSONArray edges = new JSONArray();
        JSONArray nodes = new JSONArray();
        try {
            //For each node
            for (NodeData n : get_all_V()) {
                //Scan all his edges
                for (EdgeData e : get_all_E(n.getKey())) {
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

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("Graph:\n");
        for(int key : this.edges.keySet()){
            s.append("key: ").append(key).append(" | ");

            for(EdgeData e : edges.get(key)){
                s.append(e.getDest().getKey()).append(" ");
            }
            s.append("\n");
        }
        return s.toString();
    }

    public List<NodeData> getCurrAugmentingPath() {
        return currAugmentingPath;
    }
}
