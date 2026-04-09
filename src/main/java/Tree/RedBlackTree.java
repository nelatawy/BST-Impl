package Tree;

import java.util.List;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


public class RedBlackTree<E extends Comparable<E>> extends BST<E> {

    public enum  Color {
        RED,
        BLACK
    }

    public static class RedBlackNode<E> extends TreeNode<E> {
        public Color color;
        RedBlackNode(E data, Color color) {
            super(data);
            this.color = color;
        }
    }

    private static class RBValidator<E> {
        private static final Logger logger = LoggerFactory.getLogger(RBValidator.class);

        boolean isValidTree(RedBlackNode<E> root, RedBlackNode<E> nil) {
            if (root == null || root == nil)
                return true;
            if (root.color == Color.RED || nil.color == Color.RED ){
                logger.error("Root color or Nil color violation");
                return false;
            }
            if (hasDoubleReds(root, nil, Color.BLACK)){
                logger.error("A Red node has a Red child");
                return false;
            }
            if (getBlackHeight(root, nil) == -1){
                logger.error("Black height mismatch between children");
                return false;
            }
            logger.info("validation is successful");
            return true;
        }

        private boolean hasDoubleReds(RedBlackNode<E> root, RedBlackNode<E> nil, Color parentColor){
            if (root == null || root == nil)
                return false;

            if (parentColor == Color.RED && root.color == Color.RED){
                logger.error("Double Red Violation at node: {}", root.data);
                return true;
            }

            return (hasDoubleReds((RedBlackNode<E>) root.left, nil, root.color) ||
                    hasDoubleReds((RedBlackNode<E>) root.right, nil, root.color));
        }

        private int getBlackHeight(RedBlackNode<E> root, RedBlackNode<E> nil){
            if (root == null || root == nil)
                return 0;

            int lheight = getBlackHeight((RedBlackNode<E>) root.left, nil);
            int rheight = getBlackHeight((RedBlackNode<E>) root.right, nil);

            if (lheight == -1 || rheight == -1) return -1;

            if (lheight != rheight){
                logger.error("Black height mismatch at node {}. Left: {}, Right: {}", root.data, lheight, rheight);
                return -1;
            }

            return ((root.color == Color.BLACK)? 1 : 0) + lheight;
        }
    }


    RedBlackNode<E> nil ;
    RBValidator<E> validator;
    public RedBlackTree() {
        nil = new RedBlackNode<>(null, Color.BLACK);
        root = nil;
        nil.left = nil;
        nil.right = nil;
        nil.parent = nil;

        validator = new RBValidator<>();

    }

    @Override
    public TreeNode<E> getMin(TreeNode<E> root){
        return super.getMin(root, nil);
    }

    @Override
    public int height(){
        return super.height(root, nil);
    }

    @Override
    public List<E> inOrder() {
        return super.inOrder(root, nil);
    }

    @Override
    public boolean contains(E data){
        return super.contains(data, nil);
    }

    // to make the code cleaner, we use those as wrappers for the general-purpose methods in the base class
    @Override
    protected void transplant(TreeNode<E> toBeReplaced, TreeNode<E> toBeInserted){
        super.transplant(toBeReplaced, toBeInserted, nil);
    }

    private void rotateLeft(TreeNode<E> node) {
        super.rotateLeft(node, nil);
    }

    private void rotateRight(TreeNode<E> node) {
        super.rotateRight(node, nil);
    }



    @Override
    public boolean insert(E data) {
        if (root == nil) {
            root = new RedBlackNode<>(data, Color.BLACK);
            root.parent = nil;
            root.left = root.right = nil;
            size++;
            return true;
        }
        
        TreeNode<E> itr = root;
        TreeNode<E> scanItr = root;
        while (scanItr != nil) {
            itr = scanItr;
            if (data.compareTo(scanItr.data) == 0) {
                return false; //already exists
            } else if (data.compareTo(scanItr.data) > 0) {
                scanItr = scanItr.right;
            } else  {
                scanItr = scanItr.left;
            }

        }
        TreeNode<E> newNode = new RedBlackNode<>(data, Color.RED);
        newNode.parent = itr;
        newNode.left = newNode.right = nil;
        if (data.compareTo(itr.data) < 0) {
            itr.left = newNode;
        } else  {
            itr.right = newNode;
        }
        insertFixUp(newNode);
        size++;
        return true;
    }

    protected boolean insert(E data, boolean debugMode){
        boolean inserted = insert(data);
        if (inserted && debugMode && validator.isValidTree((RedBlackNode<E>) root, nil)){
            System.out.println("invalid Tree");
            return false;
        }
        return inserted;
    }

    private void insertFixUp(TreeNode<E> node) {
        RedBlackNode<E> parent = (RedBlackNode<E>) node.parent;
        RedBlackNode<E> uncle ;
        RedBlackNode<E> grandparent;
        while (parent.color == Color.RED) {
            parent = (RedBlackNode<E>) node.parent;
            grandparent = (RedBlackNode<E>) parent.parent;

            if (parent == grandparent.left) {
                uncle = (RedBlackNode<E>) grandparent.right;
                if (uncle.color == Color.RED) { // case 1
                    parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    node = grandparent;

                    parent = (RedBlackNode<E>) node.parent; // for next check (necessary for correctness)
                } else {
                    if (parent.right == node) { // case 2
                        rotateLeft(parent);
                        node = parent; // change into next case
                        parent = (RedBlackNode<E>) node.parent; //update pointers
                        grandparent = (RedBlackNode<E>) parent.parent;
                    }
                    parent.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    rotateRight(grandparent);
                }
            } else {
                uncle = (RedBlackNode<E>) grandparent.left;
                if (uncle.color == Color.RED) { // case 1
                    parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    node = grandparent;
                    parent = (RedBlackNode<E>) node.parent; // for next check (necessary for correctness)
                } else {
                    if (parent.left == node) { // case 2
                        rotateRight(parent);
                        node = parent; // change into next case
                        parent = (RedBlackNode<E>) node.parent; //update pointers
                        grandparent = (RedBlackNode<E>) parent.parent;
                    }
                    parent.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    rotateLeft(grandparent);
                }
            }
        }
        ((RedBlackNode<E>) root).color = Color.BLACK;
    }



    @Override
    public boolean delete(E data) {
        if (root == nil) {
            return false;
        }
        TreeNode<E> scanItr = root;
        while (scanItr != nil) {
            if (data.compareTo(scanItr.data) > 0) {
                scanItr = scanItr.right;
            } else if (data.compareTo(scanItr.data) < 0) {
                scanItr = scanItr.left;
            } else {
                Color replacingNodeColor = ((RedBlackNode<E>) scanItr).color;
                TreeNode<E> rootToFix ;
                if (scanItr.left != nil && scanItr.right != nil) {
                    RedBlackNode<E> min = (RedBlackNode<E>) getMin(scanItr.right);
                    rootToFix = min.right;
                    replacingNodeColor = min.color;
                    if (min != scanItr.right) {
                        // the problem will be moved to min.right
                        transplant(min, min.right);
                        min.right = scanItr.right;
                        min.right.parent = min;
                    }
                    transplant(scanItr, min);
                    root = (scanItr == root)?min:root;

                    min.left = scanItr.left;
                    min.left.parent = min;
                    min.color = ((RedBlackNode<E>) scanItr).color;

                } else if (scanItr.left != nil) {
                    transplant(scanItr, scanItr.left);
                    root = (scanItr == root)?scanItr.left:root;
                    rootToFix = scanItr.left;
                } else{
                    transplant(scanItr, scanItr.right);
                    root = (scanItr == root)?scanItr.right:root;
                    rootToFix = scanItr.right;
                }
                
                if (replacingNodeColor == Color.BLACK) { //make up for the lost black
                    deleteFixUp((RedBlackNode<E>) rootToFix);
                }
                size--;
                return true;
            }
        }
        return false;
    }

    protected boolean delete(E data, boolean debugMode){
        boolean deleted = delete(data);
        if (deleted && debugMode && validator.isValidTree((RedBlackNode<E>) root, nil)){
            System.out.println("invalid Tree");
            return false;
        }
        return deleted;
    }

    private void deleteFixUp(RedBlackNode<E> rootToFix) {
        RedBlackNode<E> sibling, parent;
        while (rootToFix.color != Color.RED && rootToFix != root){
            if (rootToFix.parent.left == rootToFix) {
                parent = (RedBlackNode<E>) rootToFix.parent;
                sibling = (RedBlackNode<E>) parent.right;

                if (sibling.color == Color.RED) { //case 1
                    parent.color = Color.RED;
                    sibling.color = Color.BLACK;
                    rotateLeft(rootToFix.parent);
                    sibling = (RedBlackNode<E>)parent.right; // new sibling to fall to case 2
                }
                if (((RedBlackNode<E>) sibling.left).color == Color.BLACK && ((RedBlackNode<E>) sibling.right).color == Color.BLACK) {
                    sibling.color = Color.RED;
                    // make it the parent's problem
                    rootToFix = parent;
                }
                else {
                    if (((RedBlackNode<E>)sibling.right).color == Color.BLACK) {
                        ((RedBlackNode<E>)sibling.left).color = Color.BLACK;
                        sibling.color = Color.RED;
                        rotateRight(sibling);
                        //update sibling pointer
                        sibling =  (RedBlackNode<E>) parent.right;
                    }
                    // now we are sure right sibling is RED
                    rotateLeft(parent);
                    //sibling will take be the node's grandparent (wild to say out loud) pos
                    sibling.color = parent.color;
                    parent.color = Color.BLACK;

                    ((RedBlackNode<E>) sibling.right).color = Color.BLACK; // also update the other branch to make the black heights balanced
                    break; // we are done already
                }

            } else {
                sibling = (RedBlackNode<E>) rootToFix.parent.left;
                if (sibling.color == Color.RED) { //case 1
                    ((RedBlackNode<E>)rootToFix.parent).color = Color.RED;
                    sibling.color = Color.BLACK;
                    rotateRight(rootToFix.parent);
                    sibling = (RedBlackNode<E>) rootToFix.parent.left; // new sibling to fall to case 2
                }
                if (((RedBlackNode<E>) sibling.left).color == Color.BLACK && ((RedBlackNode<E>) sibling.right).color == Color.BLACK) {
                    sibling.color = Color.RED;
                    // make it the parent's problem
                    rootToFix = (RedBlackNode<E>) rootToFix.parent;
                }
                else {
                    if (((RedBlackNode<E>)sibling.left).color == Color.BLACK) {
                        rotateLeft(sibling);
                        sibling.color = Color.RED;
                        //update sibling pointer
                        sibling =  (RedBlackNode<E>) rootToFix.parent.left;
                        sibling.color = Color.BLACK;
                    }
                    // now we are sure right sibling is RED
                    rotateRight(rootToFix.parent);
                    //sibling will take be the node's grandparent (wild to say out loud) pos
                    sibling.color = (((RedBlackNode<E>) rootToFix.parent).color);
                    ((RedBlackNode<E>) rootToFix.parent).color = Color.BLACK;

                    ((RedBlackNode<E>) sibling.left).color = Color.BLACK; // also update the other branch to make the black heights balanced
                    break; // we are done already
                }
            }


        }
        rootToFix.color = Color.BLACK;
    }

    public boolean isNil(TreeNode<E> node){
        return node == nil;
    }


}
