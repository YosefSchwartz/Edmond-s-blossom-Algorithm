import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GUI extends JPanel {

    private static final int SIZE = 1000;
    private int a = SIZE / 2;
    private int b = a;
    private int r = 4 * SIZE / 5;
    private int n;
    private Undirected_Graph graph;
    boolean flag;
    boolean EdgeCover;


    public GUI(Undirected_Graph gr) {
        super(true);
        n=gr.get_all_V().size();
        NodeData n=gr.get_all_V().stream().findFirst().get();
        this.setPreferredSize(new Dimension(SIZE, SIZE));

        flag= n.getP().getX() != 0 && n.getP().getY() != 0;
        graph=gr;
        EdgeCover=false;

    }

    public void setEdgeCover(boolean b){
        EdgeCover=b;
    }


    @Override
    public void paint(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.black);
        a = getWidth() / 2;
        b = getHeight() / 2;
        int m = Math.min(a, b);
        r = 4 * m / 5;

        drawDetails(g2d);
        if(graph.getCurrAugmentingPath().size() > 0){
            drawAugmentingPath(g2d);
        }
        if(flag){
            drawEdges(g2d);
            g2d.setStroke(new BasicStroke(3));
            drawNodesWithLocations(g2d);
        }else {
            g2d.setStroke(new BasicStroke(3));
            drawNodes(g2d, m, r);
            drawEdges(g2d);
        }

    }

    private void drawAugmentingPath(Graphics2D g2d) {
        g2d.setStroke(new BasicStroke(10));
        g2d.setColor(new Color(150,150,150));
        List<NodeData> path = graph.getCurrAugmentingPath();
        for(int i = 0;i<path.size()-1;i++){
            int x1=path.get(i).getP().getX(),
                    y1=path.get(i).getP().getY(),
                    x2=path.get(i+1).getP().getX(),
                    y2=path.get(i+1).getP().getY();
            g2d.drawLine(x1,y1,x2,y2);
        }
    }

    private void drawDetails(Graphics2D g) {
        Font f=new Font("SansSerif", Font.BOLD, 18);
        g.setFont(f);
        g.setColor(Color.black);
        if(EdgeCover) {
            int size=graph.getAllEdgesCover().size()+graph.getAllMatchedEdges().size();
            g.drawString("τ(G): "+size, 100, 20);
        }
        g.drawString("α(G): "+graph.getAllMatchedEdges().size(), 100, 50);
        g.drawString("ν(G): "+graph.get_all_V().size(), 100, 80);
        if(+graph.getAllMatchedEdges().size()*2==graph.get_all_V().size()){
            f=new Font("SansSerif", Font.BOLD, 22);
            g.setFont(f);
            g.drawString("Perfect match!", 25, 110);
        }
    }

    private void drawEdges(Graphics2D g2d) {
        for(NodeData node: graph.get_all_V()){
            for(NodeData ni: graph.getNi(node)){
                int x1=node.getP().getX(),
                        y1=node.getP().getY(),
                        x2=ni.getP().getX(),
                        y2=ni.getP().getY();
                EdgeData e=graph.getEdge(node.getKey(), ni.getKey());
                if(e==null){
                    System.out.println("("+node.getKey()+","+ni.getKey()+")");
                    System.out.println(graph);
                }
                if(e.getMatched()){
                    g2d.setStroke(new BasicStroke(4));
                    g2d.setColor(new Color(201,62,7));
                }
                else if(e.getEdgeCover()){
                    g2d.setStroke(new BasicStroke(4));
                    g2d.setColor(new Color(0,79,139));
                }
                else {
                    g2d.setStroke(new BasicStroke(2));
                    g2d.setColor(Color.black);
                }
                g2d.drawLine(x1, y1, x2, y2);
            }
        }
    }
    private void drawNodesWithLocations(Graphics2D g2d) {
        for(NodeData node: graph.get_all_V()){
            int x = node.getP().getX();
            int y =  node.getP().getY();
            g2d.setColor(Color.white);
            g2d.fillOval(x-20, y-20, 40, 40);
            g2d.setColor(Color.BLACK);
            if(node.getMatch()){
                g2d.setColor(new Color(201,62,7));
            }
            g2d.drawOval(x-20 , y-20, 40, 40);
            Font f=new Font("SansSerif", Font.BOLD,15);
            g2d.setFont(f);
            int key=node.getKey();
            g2d.setColor(Color.black);
            g2d.drawString(""+key,x-6 ,y+5);
        }
    }

    private void drawNodes(Graphics2D g2d, int m, int r) {
        int r2=20;
        int i=0;
        for(NodeData node: graph.get_all_V()){
            double t = 2 * Math.PI * i / n;
            int x = (int) Math.round(a + r * Math.cos(t));
            int y = (int) Math.round(b + r * Math.sin(t));
            g2d.fillOval(x - r2, y - r2, 2 * r2, 2 * r2);
            g2d.setColor(Color.BLACK);
            int px, py;
            int d= (int)Math.sqrt((x-m)*(x-m)+(y-m)*(y-m));
            int k= d-r2;
            px=(m*r2+x*k)/(d);
            py=(m*r2+y*k)/(d);
            node.setP(px, py);
            g2d.setColor(Color.white);
            g2d.fillOval(x - r2, y - r2, 2 * r2, 2 * r2);
            g2d.setColor(Color.black);
            if(node.getMatch()){
                g2d.setColor(new Color(201,62,7));
            }
            g2d.drawOval(x - r2, y - r2, 2 * r2, 2 * r2);
            Font f=new Font("SansSerif", Font.BOLD,13);
            g2d.setFont(f);
            int key=node.getKey();
            g2d.setColor(Color.BLACK);
            g2d.drawString(""+key,(int)(x+0.038*(2 * r2)) , (int)(y+0.038*(2 * r2)));
            i++;
        }
    }
}