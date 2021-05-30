
import org.w3c.dom.Node;

import java.util.*;

/**
 * Graph class implements graph interface, that displays a undirectional unweighted graph.
 * this class use a dynamic data structure, which allow to contain a large graph.
 * In addition contains the total actions performed, and the number of edges.
 */
public class Graph {
    HashMap<Integer,NodeData> vertices;
    Set<EdgeData> edges;

    Set<EdgeData> match;

    public Graph(){
        vertices = new HashMap<>();
        edges = new HashSet<>();

        match = new HashSet<>();
    }

    public void addNode(NodeData n){
        vertices.put(n.getKey(),n);
    }
}
