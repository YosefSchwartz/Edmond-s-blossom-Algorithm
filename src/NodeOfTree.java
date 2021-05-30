import java.util.LinkedList;
import java.util.List;

public class NodeOfTree {
    NodeData n;
    List<NodeOfTree> neighbors;

    public NodeOfTree(NodeData n){
        this.n = n;
        neighbors = new LinkedList<>();
    }

    public void addNi(NodeOfTree n){
        this.neighbors.add(n);
    }
}
