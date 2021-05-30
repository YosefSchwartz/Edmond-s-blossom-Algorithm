import java.util.Collection;
import java.util.HashMap;

/**
     * NodeData class implements node_data interface, that displays a one node.
     * this class use a dynamic data structure, which allow to contain a lot of neighbors nodes.
     * In addition contains index that raise at 1 when create new node, that allow each node have a unique key.
     * and private string data (info) and int data (tag).
     */
    public class NodeData {
        static int index = 0; // static variable, to be sure that is unique key
        int key;
        boolean match;

    public NodeData(int key){
        this.key = key;
        this.match = false;
    }
    public NodeData(){
        this.key = ++index;
        this.match = false;
    }

    public int getKey(){
        return key;
    }
    public boolean isMatch(){
        return match;
    }
    }