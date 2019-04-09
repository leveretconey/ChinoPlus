package leveretconey.chino.dataStructures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import leveretconey.chino.util.Util;

public class ODTree {
    /** i choose these very strange symbols because i want my output to be compatible with metanome result file*/
    public static final String INPUT_DELIMETER="~>\\[<=,lex]";
    public static final String OUTPUT_DELIMETER="~>[<=,lex]";

    private int countAttribute;
    private ODTreeNode root;

    public static ODTree createFromODFile(String odFilePath,int countAttribute){
        return createFromGivenODs(
                readFromMetanomeStyleOdResult(odFilePath),countAttribute);
    }

    public ODTreeNode getRoot() {
        return root;
    }


    private static String readFromMetanomeStyleOdResult(String odFilePath){
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(odFilePath)));
            while (!METANOME_OD_BEGIN_SIGN.equals(reader.readLine())) ;
            String line;
            boolean fistLine = true;
            StringBuilder result = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (fistLine)
                    fistLine = false;
                else
                    result.append("\n");
                result.append(line);
            }
            reader.close();
            return result.toString();
        }catch (Exception e){
            return "";
        }
    }


    public static final String METANOME_OD_BEGIN_SIGN="# RESULTS";

    @Override
    public String toString() {
        List<ODCandidate> ods= getAllOdsOrderByBFS();
        StringBuilder sb=new StringBuilder();
        sb.append(METANOME_OD_BEGIN_SIGN);
        for(ODCandidate od:ods){
            sb.append('\n');
            sb.append(od.toString());
        }
        return sb.toString();
    }
    public int getNodeCount(Predicate<ODTreeNode> filter){
        return root.getNodeCount(filter);
    }
    public void toFile(String filePath){
        Util.toFile(toString(),filePath);
    }
    public String getStatistics(){
        StringBuilder sb=new StringBuilder();
        sb.append("tree size:").append(getNodeCount(node->node.minimal)).append('\n');
        sb.append("valid size:").append(getNodeCount(node->node.status==ODTreeNodeStatus.VALID)).append('\n');
        sb.append("split size:").append(getNodeCount(node->node.status==ODTreeNodeStatus.SPLIT)).append('\n');
        sb.append("swap size:").append(getNodeCount(node->node.status==ODTreeNodeStatus.SWAP)).append('\n');
        sb.append("split not confirmed size:").append(getNodeCount(node->node.status==ODTreeNodeStatus.SPLIT && !node.confirm));
        return sb.toString();
    }
    public List<ODCandidate> getAllOdsOrderByBFS(){
        List<ODCandidate> result=getAllOdsOrderByDFS();
        Collections.sort(result);
        return result;
    }
    public List<ODCandidate> getAllOdsOrderByDFS(){
        return getAllNodesDFS(node -> node.status==ODTreeNodeStatus.VALID && !node.isRoot() && node.minimal);
    }

    public List<ODCandidate> getAllNodesBFS(Predicate<ODTreeNode> filter){
        List<ODCandidate> result=getAllNodesDFS(filter);
        Collections.sort(result);
        return result;
    }
    public List<ODCandidate> getAllNodesDFS(Predicate<ODTreeNode> filter){
        List<ODCandidate> result=new ArrayList<>();
        root.getAllODsOrderByDFSRecursion(result,filter);
        return result;
    }

    public ODTree(int countAttribute) {
        this.countAttribute = countAttribute;
        root=new ODTreeNode(null,-1,ODTreeNodeStatus.VALID);
        root.confirm();
        for (int i = 0; i < countAttribute; i++) {
            ODTreeNode node=new ODTreeNode(root,i,ODTreeNodeStatus.SPLIT);
            node.confirm();
            root.children[i]=node;
        }
    }

    private static final int FRAME_WIDTH=1200;
    private static final int FRAME_HEIGHT=800;
    private static final int ROOT_Y=20;
    private JPanel jp;

    public void toPic(String fileName){
        toPic(fileName,"jpg",true);
    }
    public void toPic(String fileName,String format,boolean simplify){
        try {
            BufferedImage image=new BufferedImage(2500,1000,BufferedImage.TYPE_INT_RGB);
            Graphics pic=image.createGraphics();
            root.draw(pic, 1250,
                    ROOT_Y, FRAME_WIDTH, -1, -1,simplify);
            ImageIO.write(image,format,new File(fileName+"."+format));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(boolean simplify){

        if (jp==null) {
            JFrame jf = new JFrame("OD Tree");
            jf.setVisible(true);
            jf.setSize(FRAME_WIDTH + 15, FRAME_HEIGHT + 40);
            jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            jf.setLayout(null);
            jf.setResizable(true);
            jp = new JPanel() {
                private static final long serialVersionUID = 1L;

                @Override
                public void paint(Graphics pic) {
                    super.paint(pic);
                    root.draw(pic, FRAME_WIDTH / 2,
                            ROOT_Y, FRAME_WIDTH, -1, -1,simplify);
                }
            };
            jf.add(jp);
            jp.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        }
        jp.repaint();
    }

    public void insertODByAttributeList(ODByLeftRightAttributeList od){
        root.insertODByAttributeList(od,0,0);
    }

    private static ODTree createFromGivenODs(String odsString,int countAttribute){
        ODTree tree=new ODTree(countAttribute);
        for(String line:odsString.split("\\n")){
            String[] parts=line.split(INPUT_DELIMETER);
            ODByLeftRightAttributeList od=new ODByLeftRightAttributeList(
                     Util.parseIntegerList(parts[0],",")
                    ,Util.parseIntegerList(parts[1],","));
            Util.listEachElementPlus(od.left,-1);
            Util.listEachElementPlus(od.right,-1);
            tree.insertODByAttributeList(od);
        }
        return tree;
    }

    public enum ODTreeNodeStatus{
        VALID,SWAP,SPLIT,UNKNOWN;

        @Override
        public String toString() {
            if(equals(VALID))
                return "O";
            else if(equals(SWAP))
                return "W";
            else if(equals(SPLIT))
                return "L";
            else if(equals(UNKNOWN))
                return "N";
            return "?";
        }
    }

    public class ODTreeNode{
        public ODTreeNode parent;
        public ODTreeNodeStatus status;
        public int attribute;
        public ODTreeNode[] children;
        public boolean confirm;
        public boolean minimal;

        public ODTreeNode getRoot(){
            return root;
        }
        @Override
        public String toString() {
            return "ODTreeNode{" +
                    "status=" + status +
                    ", attribute=" + attribute +
                    ", confirm=" + confirm +
                    ", minimal=" + minimal +
                    ", path=" +new ODCandidate(this).odByPath +
                    '}';
        }

        private void insertODByAttributeList(ODByLeftRightAttributeList od, int nextLeft, int nextRight){
            int position=0;
            if(status==ODTreeNodeStatus.UNKNOWN){
                if(nextLeft<od.left.size()){
                    status=ODTreeNodeStatus.SPLIT;
                }else {
                    status=ODTreeNodeStatus.VALID;
                    return;
                }
            }
            switch (status){
                case SWAP:
                    throw new RuntimeException("invalid od insertion");
                case SPLIT:
                    position=od.left.get(nextLeft++);
                    break;
                case VALID:
                    position=od.right.get(nextRight++);
                    break;
            }
            if(children[position]==null)
                children[position]=new ODTreeNode(this,position);
            children[position].insertODByAttributeList(od,nextLeft,nextRight);

        }
        public ODTreeNode(ODTreeNode parent, int attribute , ODTreeNodeStatus status) {
            this.parent = parent;
            this.status = status;
            this.attribute = attribute;
            this.minimal=true;
            children=new ODTreeNode[countAttribute];
            if(parent!=null) {
                parent.children[attribute] = this;
            }
            confirm =  status==ODTreeNodeStatus.SWAP;
        }

        public void confirm() {
            this.confirm = true;
        }

        public ODTreeNode(ODTreeNode parent, int attribute) {
            this(parent,attribute,ODTreeNodeStatus.UNKNOWN);
        }

        private static final int NODE_INTERVAL=50;
        private static final int NODE_HEIGHT=12;
        private static final int GRID_WIDTH=4;

        private int getChildrenCount(boolean simplify){
            int result=0;
            for (ODTreeNode child : children) {
                if(child!=null && (!simplify ||
                        (child.status==ODTreeNodeStatus.SPLIT || child.status==ODTreeNodeStatus.VALID)))
                    result++;
            }
            return result;
        }

        private void draw(Graphics pic, int centerx, int centery
                , int givenWidth,int parentx,int parenty,boolean simplyfy){
            pic.setColor(Color.WHITE);
            if (isRoot()){
                pic.fillRect(0,0,9999,9999);
            }
            int childrenCount= getChildrenCount(simplyfy);
            int width=childrenCount*GRID_WIDTH+NODE_HEIGHT*2;
            int left=centerx-width/2;
            int height=NODE_HEIGHT;
            int top=centery-height/2;
            int down=centery+height/2;
            Color frameColor=(confirm && !isRoot() && !parent.isRoot())?Color.RED:Color.BLACK;
            Color statusColor=minimal?Color.RED:Color.BLACK;

            pic.setColor(frameColor);
            pic.drawRect(left,top,width,height);
            pic.setColor(Color.BLACK);
            if(parentx!=-1){
                pic.drawLine(parentx,parenty,centerx,top);
            }
            int offset=left;
            pic.setColor(statusColor);
            pic.drawString(status.toString(),offset+2,down-1);
            pic.setColor(Color.BLACK);
            offset+=NODE_HEIGHT;
            pic.drawLine(offset,top,offset,down);
            pic.setColor(statusColor);
            pic.drawString(String.valueOf(attribute+1),offset+2,down-1);
            pic.setColor(Color.BLACK);

            offset+=NODE_HEIGHT-GRID_WIDTH;
            if(childrenCount==0)
                return;
            int childrenWidth=givenWidth / childrenCount;
            childrenWidth=Math.max(14,childrenWidth);
            int childrenOffset=centerx-givenWidth/2+childrenWidth/2;
            int realChildrenIndex=0;
            for (int i = 0; i < countAttribute; i++) {
                if (!(children[i] != null && (!simplyfy || !
                        (children[i].status== ODTreeNodeStatus.UNKNOWN
                                || children[i].status==ODTreeNodeStatus.SWAP))))
                    continue;
                offset+=GRID_WIDTH;
                pic.drawLine(offset,top,offset,down);
                children[i].draw(pic, childrenOffset, centery + NODE_INTERVAL + 2 * realChildrenIndex * GRID_WIDTH,
                    childrenWidth, offset + GRID_WIDTH / 2, centery,simplyfy);
                childrenOffset+=childrenWidth;
                realChildrenIndex++;
            }
        }

        private boolean hasValidChild(){
            for (ODTreeNode child : children) {
                if(child!=null && child.status==ODTreeNodeStatus.VALID)
                    return true;
            }
            return false;
        }
        public void getAllODsOrderByDFSRecursion(List<ODCandidate> ods,Predicate<ODTreeNode> filter){
            if(!minimal)
                return;
            if(filter.test(this)){
                ods.add(new ODCandidate(this));
            }
            for (int attribute = 0; attribute < countAttribute; attribute++) {
                ODTreeNode child=children[attribute];
                if(child != null) {
                    child.getAllODsOrderByDFSRecursion(ods,filter);
                }
            }
        }

        public void cutChildren(){
            for (int i = 0; i < countAttribute; i++) {
                if(children[i]!=null){
                    children[i].parent=null;
                    children[i]=null;
                }
            }
        }
        public int getNodeCount(Predicate<ODTreeNode> filter){
            int result=0;
            if (filter==null || filter.test(this))
                result++;
            for (ODTreeNode child : children) {
                if(child!=null)
                    result+=child.getNodeCount(filter);
            }
            return result;
        }
        public boolean isRoot(){
            return root==this;
        }

        public boolean accessible(){
            ODTreeNode node=this;
            while (node.parent!=null){
                node=node.parent;
            }
            return node.isRoot();
        }

        public void setStatus(ODTreeNodeStatus status){
            switch (status){
                case SWAP:
                    confirm();
                    if(this.status!=ODTreeNodeStatus.SWAP)
                        cutChildren();
                    break;
                case SPLIT:
                    if(this.status==ODTreeNodeStatus.SWAP)
                        throw new RuntimeException("illegal node status update");
                    if(this.status!=ODTreeNodeStatus.SPLIT)
                        cutChildren();
                    break;
                case VALID:
                    if(this.status==ODTreeNodeStatus.SWAP || this.status==ODTreeNodeStatus.SPLIT)
                        throw new RuntimeException("illegal node status update");
                    break;
                case UNKNOWN:
                    if(this.status!=ODTreeNodeStatus.UNKNOWN)
                        throw new RuntimeException("illegal node status update");
                    break;
            }
            this.status=status;
        }

    }
    private void getCountNodeInEachLevelRecursion(ODTreeNode node, List<Integer> result
            , int depth,Predicate<ODTreeNode> filter){
        if(node==null)
            return;
        if(depth==result.size()){
            result.add(0);
        }
        if(filter.test(node)){
            result.set(depth,result.get(depth)+1);
        }
        for (ODTreeNode child : node.children) {
            getCountNodeInEachLevelRecursion(child,result,depth+1,filter);
        }
    }
    public List<Integer> getCountNodeInEachLevel(Predicate<ODTreeNode> filter){
        List<Integer> result=new ArrayList<>();
        getCountNodeInEachLevelRecursion(root,result,0,filter);
        return result;
    }

    public List<Integer> getCountNodeInEachLevel(){
        return getCountNodeInEachLevel((node)->{return true;});
    }

}
