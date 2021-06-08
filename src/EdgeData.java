public class EdgeData {
    NodeData src, dest;
    boolean matched;

    public EdgeData(NodeData src, NodeData dest){
        this.src=src;
        this.dest=dest;
        matched=false;
    }

    public boolean getMatched() {
        return matched;
    }
    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public NodeData getSrc() {
        return src;
    }

    public NodeData getDest(){
        return dest;
    }

    @Override
    public String toString() {
        if(matched) {
            return "{" +
                    "(" + src.getKey() +
                    "," + dest.getKey() +
                    ") matched=" + matched +
                    '}';
        }else{
            return "";
        }
    }
}
