package Tree;

public class TreeNode<E> {
    public TreeNode<E> left;
    public TreeNode<E> right;
    public TreeNode<E> parent;
    public E data;
    TreeNode(E data) {
        this.data = data;
        this.right = null;
        this.left = null;
        this.parent = null;
    }
}