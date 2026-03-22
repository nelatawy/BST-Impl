public class RedBlackTree<E extends Comparable<E>> extends BST<E> {

    enum  Color {
        RED,
        BLACK
    }

    static class RedBlackNode<E> extends TreeNode<E> {
        Color color;
        RedBlackNode(E data, Color color) {
            super(data);
            this.color = color;
        }
    }

    RedBlackNode<E> nil ;
    public RedBlackTree() {
        nil = new RedBlackNode<>(null, Color.BLACK);

    }
    @Override
    public boolean insert(E data) {
        if (root == null) {
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
            System.out.println(scanItr.data);
            if (scanItr.data.compareTo(data) == 0) {
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

        if (itr.left == nil) {
            itr.left = newNode;
        } else  {
            itr.right = newNode;
        }
        insertFixUp(newNode);
        size++;
        return true;
    }

    private void insertFixUp(TreeNode<E> node) {
        RedBlackNode<E> parent = (RedBlackNode<E>) node.parent;
        RedBlackNode<E> uncle ;
        RedBlackNode<E> grandparent;
        while (parent.color == Color.RED) {
            parent = (RedBlackNode<E>) node.parent;
            grandparent = (RedBlackNode<E>) parent.parent;

            if (parent == parent.parent.left) {
                uncle = (RedBlackNode<E>) node.parent.parent.right;
                if (uncle.color == Color.RED) { // case 1
                    parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    node = grandparent;
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
                uncle = (RedBlackNode<E>) node.parent.parent.left;
                if (uncle.color == Color.RED) { // case 1
                    parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    grandparent.color = Color.RED;
                    node = grandparent;
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
        if (node == root){
            ((RedBlackNode<E>)node).color = Color.BLACK;
        }
    }

    @Override
    public boolean delete(E data) {
        if (root == null) {
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
                } else {
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

    private void deleteFixUp(RedBlackNode<E> rootToFix) {
        RedBlackNode<E> sibling ;
        while (rootToFix.color != Color.RED && rootToFix != root){
            if (rootToFix.parent.left == rootToFix) {
                sibling = (RedBlackNode<E>) rootToFix.parent.right;
                if (sibling.color == Color.RED) { //case 1
                    ((RedBlackNode<E>)rootToFix.parent).color = Color.RED;
                    sibling.color = Color.BLACK;
                    rotateLeft(rootToFix.parent);
                    sibling = (RedBlackNode<E>) rootToFix.parent.right; // new sibling to fall to case 2
                }
                if (((RedBlackNode<E>) sibling.left).color == Color.BLACK && ((RedBlackNode<E>) sibling.right).color == Color.BLACK) {
                    sibling.color = Color.RED;
                    // make it the parent's problem
                    rootToFix = (RedBlackNode<E>) rootToFix.parent;
                }
                else {
                    if (((RedBlackNode<E>)sibling.right).color == Color.BLACK) {
                        rotateRight(sibling);
                        sibling.color = Color.RED;
                        //update sibling pointer
                        sibling =  (RedBlackNode<E>) rootToFix.parent.right;
                        sibling.color = Color.BLACK;
                    }
                    // now we are sure right sibling is RED
                    rotateLeft(rootToFix.parent);
                    //sibling will take be the node's grandparent (wild to say out loud) pos
                    sibling.color = (((RedBlackNode<E>) rootToFix.parent).color);
                    ((RedBlackNode<E>) rootToFix.parent).color = Color.BLACK;

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

    static void main() {
        BST<Integer> bst = new RedBlackTree<>();
        bst.insert(1);
        bst.insert(2);

//        bst.insert(3);
        System.out.println(bst.height());
        System.out.println(bst.delete(1));
        System.out.println(bst.contains(1));

    }
}
