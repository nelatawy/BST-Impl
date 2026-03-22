import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BST<E extends Comparable<E>> {
    int size = 0;
    TreeNode<E> root;



    public abstract boolean insert(E data);

    public abstract boolean delete(E data);

    public boolean contains(E data) {
        TreeNode<E> scanItr = root;
        while (scanItr != null && scanItr.data != null) {
            if (data.compareTo(scanItr.data) == 0)
                return true;
            else if (data.compareTo(scanItr.data) > 0){
                scanItr = scanItr.right;
            }
            else {
                scanItr = scanItr.left;
            }
        }
        return false;
    }

    public List<E> inOrder() {
        return inOrder(root);
    }

    private List<E> inOrder(TreeNode<E> root) {
        if (root == null){
            return Collections.emptyList();
        }
        List<E> res = new ArrayList<>(inOrder(root.left));
        res.add(root.data);
        res.addAll(inOrder(root.right));
        return res;
    }

    public TreeNode<E> getMin(TreeNode<E> root) {
        if (root == null)
            return null;
        TreeNode<E> itr = root;
        while (itr.left != null) {
            itr = itr.left;
        }
        return itr;
    }

    public void transplant(TreeNode<E> toBeReplaced, TreeNode<E> toBeInserted) {
        if (toBeReplaced.parent == null) {
            root = toBeInserted;
        } else if (toBeReplaced.parent.left == toBeReplaced) {
            toBeReplaced.parent.left = toBeInserted;
        } else {
            toBeReplaced.parent.right = toBeInserted;
        }

        toBeInserted.parent = toBeReplaced.parent;

    }

    public void rotateLeft(TreeNode<E> node) {
        if (node == null || node.right == null)
            return;

        TreeNode<E> newRoot = node.right;
        node.right = newRoot.left;
        node.right.parent = node;

        transplant(node, newRoot); //parent-child links updated
        newRoot.left = node;
        node.parent = newRoot;

    }

    public void rotateRight(TreeNode<E> node) {
        if (node == null || node.left == null)
            return;
        TreeNode<E> newRoot = node.left;
        node.left = newRoot.right;
        node.left.parent = node;
        transplant(node, newRoot);
        newRoot.right = node;
        node.parent = newRoot;
    }

    public int height() {
        return height(root);
    }

    private int height(TreeNode<E> root) {
        if (root == null)
            return 0;
        return 1 + Math.max(height(root.left), height(root.right));
    }

    public int size() {
        return this.size;
    }

}
