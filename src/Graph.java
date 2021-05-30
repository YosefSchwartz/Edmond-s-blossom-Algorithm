
import org.w3c.dom.Node;

import java.util.*;

/**
 * Graph class implements graph interface, that displays a undirectional unweighted graph.
 * this class use a dynamic data structure, which allow to contain a large graph.
 * In addition contains the total actions performed, and the number of edges.
 */
public class Graph {
    HashMap<Integer,NodeData> vertices;
    HashMap<Integer,List<EdgeData>> edges;
    HashMap<Integer,List<NodeData>> Cycles;

    int num_of_edges, num_of_nodes;

    public Graph(){
        vertices = new HashMap<>();
        edges= new HashMap<>();
        Cycles = new HashMap<>();
        num_of_edges=0;
        num_of_nodes=0;
    }
    public NodeData getNode(int key){
        return vertices.get(key);
    }
    public void addNode(int key){
        NodeData tmp=new NodeData(key);
        vertices.put(key, tmp);
        edges.put(key, new LinkedList<>());
        num_of_nodes++;
    }
    public void addEdge(int n1, int n2){
        EdgeData e1 =new EdgeData(getNode(n1), getNode(n2));
        EdgeData e2 =new EdgeData(getNode(n2), getNode(n1));
        edges.get(n1).add(e1);
        edges.get(n2).add(e2);
        num_of_edges++;
    }

    public void removeEdge(int n1, int n2){
        EdgeData tmp = null;
        int count=0;
        for (EdgeData e: edges.get(n1)){
            if(e.getN2().getKey()==n2){
                tmp = e;
                break;
            }
        }
        if(tmp!=null) {
            edges.get(n1).remove(tmp);
            count++;
        }
        tmp=null;
        for (EdgeData e: edges.get(n2)){
            if(e.getN1().getKey()==n1){
                tmp = e;
                break;
            }
        }
        if(tmp!=null) {
            count++;
            edges.get(n2).remove(tmp);
        }
        if(count==0){
            num_of_edges--;
        }
    }

    public void removeNode(int key){
        vertices.remove(key);
        for (EdgeData e: edges.get(key)){
            edges.get(e.getN2().getKey()).remove(e.getN1());
            num_of_edges--;
        }
        edges.remove(key);
        num_of_nodes--;
    }

    public Collection<NodeData> get_all_V(){
        return vertices.values();
    }

    public Collection<EdgeData> get_all_E(int key){
        return edges.get(key);
    }

    //zip cycle
    public void zipCycle(LinkedList<NodeData> cycle){
        NodeData newNode=new NodeData();
        int key= newNode.getKey();
        addNode(key);
        for(NodeData n:cycle){//go over all the nodes that in the cycle
            for (EdgeData e: edges.get(n.getKey())){//go over all the edges of the current node
               if(!cycle.contains(e.getN2())) {
                   addEdge(e.getN2().getKey(), key);//new connection between the new node and the neighbor
                   edges.get(e.getN2()).remove(e.getN1());//remove the curr node from his neighbor's list
               }
            }
        }
        Cycles.put(key, cycle);
    }

    public void UnzipCycle(int key){
        List<NodeData> cycle= Cycles.get(key);
        for(NodeData n:cycle) {//go over all the nodes that in the cycle
            for (EdgeData e : edges.get(n.getKey())) {//go over all the edges of the current node
                if (!cycle.contains(e.getN2())) {
                    removeEdge(e.getN2().getKey(), key);//remove the connection between the new node and the neighbor
                    EdgeData tmp = new EdgeData(e.getN2(), e.getN1());
                    edges.get(e.getN2().getKey()).add(tmp);
                    for (EdgeData e1 : edges.get(e.getN2())) { //remove the curr node from his neighbor's list
                        if (e1.getN2() == e.getN1())
                            tmp = e1;
                    }
                    edges.get(e.getN2()).remove(tmp);
                }
            }
        }
        Cycles.remove(key);
    }

}
