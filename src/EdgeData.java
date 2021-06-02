public class EdgeData {
    NodeData n1, n2;
    boolean matched;

    public boolean getMatched() {
        return matched;
    }

    public EdgeData(NodeData n1, NodeData n2){
        this.n1=n1;
        this.n2=n2;
        matched=false;
    }
    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public NodeData getN1() {
        return n1;
    }

    public NodeData getN2(){
        return n2;
    }

    @Override
    public String toString() {
        if(matched) {
            return "{" +
                    "(" + n1.getKey() +
                    "," + n2.getKey() +
                    ") matched=" + matched +
                    '}';
        }else{
            return "";
        }
    }
}
