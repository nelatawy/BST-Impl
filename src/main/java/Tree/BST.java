package Tree;

import java.util.*;

public abstract class BST<E extends Comparable<E>> {
    int size = 0;
    TreeNode<E> root;



    public abstract boolean insert(E data);

    public abstract boolean delete(E data);


    public boolean contains(E data){
        return contains(data, null);
    }

    protected boolean contains(E data, TreeNode<E> nullNode) {
        TreeNode<E> scanItr = root;
        while (scanItr != nullNode) {
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
        return inOrder(root, null);
    }

//    protected List<E> inOrder(TreeNode<E> root, TreeNode<E> nullNode) {
//        if (root == nullNode){
//            return Collections.emptyList();
//        }
//        List<E> res = new ArrayList<>(inOrder(root.left, nullNode));
//        res.add(root.data);
//        res.addAll(inOrder(root.right, nullNode));
//        return res;
//    }


    // switched to iterative approach to avoid StackOverflow
    protected List<E> inOrder(TreeNode<E> root, TreeNode<E> nullNode) {
        if (root == null || root == nullNode)
            return Collections.emptyList();

        Stack<AbstractMap.SimpleEntry<TreeNode<E>, Boolean>> stack = new Stack<>();
        List<E> inorder = new ArrayList<>();
        stack.push(new AbstractMap.SimpleEntry<>(root, false));
        while (!stack.empty()) {
            AbstractMap.SimpleEntry<TreeNode<E>, Boolean> pair = stack.pop();
            TreeNode<E> node = pair.getKey();
            boolean isSecondVisit = pair.getValue();

            if (isSecondVisit) {
                inorder.add(node.data);
            } else {
                if (node.right != nullNode) {
                    stack.push(new AbstractMap.SimpleEntry<>(node.right, false));
                }
                stack.push(new AbstractMap.SimpleEntry<>(node, true));

                if (node.left != nullNode) {
                    stack.push(new AbstractMap.SimpleEntry<>(node.left, false));
                }
            }
        }

        return inorder;
    }


    public TreeNode<E> getMin(TreeNode<E> root){return getMin(root, null);}

    protected TreeNode<E> getMin(TreeNode<E> root, TreeNode<E> nullNode) {
        if (root == nullNode)
            return null;
        TreeNode<E> itr = root;
        while (itr.left != nullNode) {
            itr = itr.left;
        }
        return itr;
    }


    protected void transplant(TreeNode<E> toBeReplaced, TreeNode<E> toBeInserted){
        transplant(toBeReplaced, toBeInserted, null);
    }

    protected void transplant(TreeNode<E> toBeReplaced, TreeNode<E> toBeInserted, TreeNode<E> nullNode) {
        if (toBeReplaced.parent == nullNode) {
            root = toBeInserted;
        } else if (toBeReplaced.parent.left == toBeReplaced) {
            toBeReplaced.parent.left = toBeInserted;
        } else {
            toBeReplaced.parent.right = toBeInserted;
        }

        toBeInserted.parent = toBeReplaced.parent;

    }


    protected void rotateLeft(TreeNode<E> node, TreeNode<E> nullNode) {
        if (node == nullNode || node.right == nullNode)
            return;

        TreeNode<E> newRoot = node.right;
        node.right = newRoot.left;
        if (node.right != nullNode)
            node.right.parent = node;

        transplant(node, newRoot, nullNode); //parent-child links updated
        newRoot.left = node;
        node.parent = newRoot;

    }

    protected void rotateRight(TreeNode<E> node, TreeNode<E> nullNode) {
        if (node == nullNode || node.left == nullNode)
            return;
        TreeNode<E> newRoot = node.left;
        node.left = newRoot.right;
        if (node.left != nullNode)
            node.left.parent = node;

        transplant(node, newRoot, nullNode);
        newRoot.right = node;
        node.parent = newRoot;
    }

    public int height() {
        return height(root, null);
    }

//    protected int height(TreeNode<E> root, TreeNode<E> nullNode) {
//        if (root == nullNode)
//            return 0;
//        return 1 + Math.max(height(root.left, nullNode), height(root.right, nullNode));
//    }


    // migrated to an iterative approach
    protected int height(TreeNode<E> root, TreeNode<E> nullNode) {
        if (root == null || root == nullNode)
            return 0;

        Stack<AbstractMap.SimpleEntry<TreeNode<E>, Integer>> stack = new Stack<>();
        int maxHeight = (int) -1e9;
        stack.push(new AbstractMap.SimpleEntry<>(root, 0));
        while (!stack.empty()) {
            AbstractMap.SimpleEntry<TreeNode<E>, Integer> pair = stack.pop();
            TreeNode<E> node = pair.getKey();
            int nodeDepth = pair.getValue();
            maxHeight = Math.max(maxHeight, nodeDepth);

            if (node.right != nullNode) {
                stack.push(new AbstractMap.SimpleEntry<>(node.right, nodeDepth + 1));
            }

            if (node.left != nullNode) {
                stack.push(new AbstractMap.SimpleEntry<>(node.left, nodeDepth + 1));
            }

        }
        return maxHeight;
    }



    public int size() {
        return this.size;
    }

    public TreeNode<E> getRoot(){
        return root;
    }

}
