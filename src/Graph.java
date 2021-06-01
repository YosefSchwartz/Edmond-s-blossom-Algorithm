
import org.w3c.dom.Node;

import java.util.*;

/**
 * Graph class implements graph interface, that displays a undirectional unweighted graph.
 * this class use a dynamic data structure, which allow to contain a large graph.
 * In addition contains the total actions performed, and the number of edges.
 */

public class Graph {
    HashMap<Integer, NodeData> vertices;
    HashMap<Integer, List<EdgeData>> edges;
    HashMap<Integer, List<NodeData>> Cycles;
    boolean flagForCycle = false;

    int num_of_edges, num_of_nodes;

    public Graph() {
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
//        for(EdgeData tmp : edges.get(n1)){
//            if(tmp.n2.getKey() == n2);
//            return;
//        }
        EdgeData e1 = new EdgeData(getNode(n1), getNode(n2));
        EdgeData e2 = new EdgeData(getNode(n2), getNode(n1));

        edges.get(n1).add(e1);
        edges.get(n2).add(e2);
        num_of_edges++;
    }

    public void removeEdge(int n1, int n2) {
        EdgeData tmp = null;
        int count = 0;
        for (EdgeData e : edges.get(n1)) {
            if (e.getN2().getKey() == n2) {
                tmp = e;
                break;
            }
        }
        if (tmp != null) {
            edges.get(n1).remove(tmp);
            count++;
        }
        tmp = null;
        for (EdgeData e : edges.get(n2)) {
            if (e.getN1().getKey() == n1) {
                tmp = e;
                break;
            }
        }
        if (tmp != null) {
            count++;
            edges.get(n2).remove(tmp);
        }
        if (count == 0) {
            num_of_edges--;
        }
    }

    public void removeNode(int key) {
        vertices.remove(key);
        for (EdgeData e : edges.get(key)) {
            edges.get(e.getN2().getKey()).remove(e.getN1());
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

    //zip cycle
    public NodeData zipCycle(LinkedList<NodeData> cycle) {
        NodeData newNode = new NodeData();
        int key = newNode.getKey();
        addNode(newNode);
        for (NodeData n : cycle) {//go over all the nodes that in the cycle
            for (EdgeData e : edges.get(n.getKey())) {//go over all the edges of the current node
                if (!cycle.contains(e.getN2())) {
                    addEdge(e.getN2().getKey(), key);//new connection between the new node and the neighbor
                    edges.get(e.getN2().getKey()).remove(getEdge(e.getN2().getKey(), e.getN1().getKey()));//remove the curr node from his neighbor's list
                }
            }
        }
        Cycles.put(key, cycle);
        return newNode;
    }

    public EdgeData getEdge(int key1, int key2) {
        for (EdgeData e : edges.get(key1)) {
            if (e.getN2().getKey() == key2)
                return e;
        }
        return null;
    }

    public void UnzipCycles() {
        for (int key : Cycles.keySet()) {
            List<NodeData> cycle = Cycles.get(key);
            for (NodeData n : cycle) {//go over all the nodes that in the cycle
                for (EdgeData e : edges.get(n.getKey())) {//go over all the edges of the current node
                    if (!cycle.contains(e.getN2())) {
                        removeEdge(e.getN2().getKey(), key);//remove the connection between the new node and the neighbor
                        EdgeData tmp = new EdgeData(e.getN2(), e.getN1());
                        edges.get(e.getN2().getKey()).add(tmp);
//                    for (EdgeData e1 : edges.get(e.getN2())) { //remove the curr node from his neighbor's list
//                        if (e1.getN2() == e.getN1())
//                            tmp = e1;
//                    }
                        tmp = getEdge(e.getN2().getKey(), key);//
                        edges.get(e.getN2().getKey()).remove(tmp);
                    }
                }
            }
            Cycles.remove(key);
            removeNode(key);
        }
    }
////*****************************************************

    private void resetVisit() {
        for (NodeData n : vertices.values()) {
            n.status = Status.undiscovered;
        }
    }

    public LinkedList<NodeData> checkCycle() {
        flagForCycle = false;
        LinkedList<NodeData> path = new LinkedList<>();
        this.resetVisit();

        NodeData n = this.vertices.values().stream().findAny().get();
        DFS_move(n, this, path);

        return path;

    }

    private void DFS_move(NodeData n, Graph g, LinkedList<NodeData> p) {
        if (flagForCycle) return;

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
            if (p.size() < 2 || e.n2 != p.get(p.size() - 2)) { // to ignore return to the node that we came from
                DFS_move(e.n2, g, p);
            }
        }
        if (!flagForCycle) {
            n.status = Status.finished;
            p.remove(n);
        }
    }

    public List<NodeData> shortestPath(int src, int dest) {
        resetTagAndInfo();

        List<NodeData> path = new ArrayList<>();
        if (src == dest)
            return path; // case that the path is from node to himself (no edge)

        NodeData srcNode = vertices.get(src);
        NodeData destNode = vertices.get(dest);

        final String beHere = "we have been here!";
        LinkedList<NodeData> queue = new LinkedList<>();
        queue.add(srcNode);
        srcNode.setInfo(beHere);
        srcNode.tag = -1;

        boolean flag = false;

        while (!queue.isEmpty() && !flag) {
            NodeData tmp = queue.remove();
            for (NodeData tmpNi : getNi(tmp)) {
                if (tmpNi.getInfo() != beHere) {
                    tmpNi.setInfo(beHere);
                    tmpNi.setTag(tmp.getKey());
                    queue.add(tmpNi);
                }
                if (tmpNi == destNode) {
                    flag = true;
                    break;
                }
            }
        }
        while(destNode.getTag()!=-1) {
            path.add(destNode);
            destNode=getNode(destNode.getTag());
        }
        // it stop when the tag is -1 (source node), so we need to add it.
        path.add(destNode);

        /*
        we get list:
        dest -> dest-1 ->...-> src+1 -> src
        and we need opposite!
         */
        //reverse the path
        List<NodeData> pathCorrectDirection = new ArrayList<>();
        for(int i=path.size()-1;i>=0;i--)
            pathCorrectDirection.add(path.get(i));

        return pathCorrectDirection;
    }

    public Collection<NodeData> getNi(NodeData n) {
        Collection<NodeData> res = new HashSet<>();
        for (EdgeData e : this.get_all_E(n.getKey())) {
            res.add(e.getN2());
        }
        return res;
    }

    private void resetTagAndInfo() {
        for(NodeData n : vertices.values()){
            n.setInfo("");
            n.setTag(-1);
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


    @Override
    public String toString() {
        String s = "Graph:\n";
                for(int key : this.edges.keySet()){
                    s+="key: "+key+" | ";

                    for(EdgeData e : edges.get(key)){
                        s+=""+e.n2.getKey()+" ";
                    }
                    s+="\n";
                }
                return s;
    }

}
