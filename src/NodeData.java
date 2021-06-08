/**
     * NodeData class implements node_data interface, that displays a one node.
     * this class use a dynamic data structure, which allow to contain a lot of neighbors nodes.
     * In addition contains index that raise at 1 when create new node, that allow each node have a unique key.
     * and private string data (info) and int data (tag).
     */
enum Status{
    undiscovered,
    discovered,
    finished
}
    public class NodeData implements  Comparable<Object> {

        static int index = 0; // static variable, to be sure that is unique key
        int key;
        boolean match;
        Status status;
        int tag;
        String info;
        Point p;

        public NodeData(){
            this.key = index++;
            this.match = false;
            p=new Point();
        }
        public NodeData(int key){
            this.key = key;
            this.match = false;
            p=new Point();
        }

        public Point getP(){
            return p;
        }
        public void setP(int x, int y){
            p.setX(x);
            p.setY(y);
        }

        public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public int getKey(){
        return key;
    }

        public boolean getMatch(){
        return match;
    }

        public void setMatch(boolean match) {
            this.match = match;
        }

        @Override
        public int compareTo(Object o) {
            NodeData n =(NodeData)(o);
            if(key-n.getKey()>0) return 1;
            return -1;
        }

        @Override
        public String toString() {
            return ""+key;

        }
    }