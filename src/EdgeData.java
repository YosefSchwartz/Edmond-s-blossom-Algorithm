public class EdgeData {
    private final NodeData src, dest;
    private boolean matched;
    private boolean inEdgeCover;


    public EdgeData(NodeData src, NodeData dest){
        this.src=src;
        this.dest=dest;
        matched=false;
        inEdgeCover=false;
    }

    public boolean getMatched() {
        return matched;
    }
    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean getEdgeCover(){
        return inEdgeCover;
    }

    public void setEdgeCover(boolean b){
        inEdgeCover=b;
    }


    public NodeData getSrc() {
        return src;
    }

    public NodeData getDest(){
        return dest;
    }

    @Override
    public String toString() {
            return "(" + src.getKey() +"," + dest.getKey() +")";
    }
}
