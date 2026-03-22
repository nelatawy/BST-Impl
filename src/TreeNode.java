public class TreeNode<E> {
    TreeNode<E> left;
    TreeNode<E> right;
    TreeNode<E> parent;
    E data;
    TreeNode(E data) {
        this.data = data;
        this.right = null;
        this.left = null;
        this.parent = null;
    }
}