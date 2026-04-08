package Tree;

public class SimpleBST<E extends Comparable<E>> extends BST<E> {

    @Override
    public boolean insert(E data) {
        if (root == null){
            root = new TreeNode<>(data);
            size++;
            return true;
        }
        TreeNode<E> itr = root;
        TreeNode<E> scanItr = root;
        while (scanItr != null) {
            itr = scanItr;
            if (data.compareTo(scanItr.data) == 0) {
                return false; //already exists
            } else if (data.compareTo(scanItr.data) > 0){
                scanItr = scanItr.right;
            } else {
                scanItr = scanItr.left;
            }
        }
        TreeNode<E> newNode = new TreeNode<>(data);
        newNode.parent = itr;

        if (data.compareTo(itr.data) < 0) {
            itr.left = newNode;
        } else  {
            itr.right = newNode;
        }
        size++;
        return true;
    }


    @Override
    public boolean delete(E data) {
        if (root == null)
            return false;
        TreeNode<E> itr = root;
        while (itr != null) {
            if (data.compareTo(itr.data) > 0) {
                itr = itr.right;
            }
            else if (data.compareTo(itr.data) < 0) {
                itr = itr.left;
            }
            else {
                //handle delete
                if  (itr.left != null && itr.right != null) { // has 2 children
                    TreeNode<E> min = getMin(itr.right);
                    if (min != itr.right) {
                        transplant(min, min.right); // take min out of it's place
                        min.right = itr.right;
                        min.right.parent = min;
                    }
                    transplant(itr, min);
                    min.left = itr.left;
                    min.left.parent = min;
                } else if (itr.left != null) {
                    transplant(itr, itr.left);
                } else {
                    transplant(itr, itr.right);
                }
                size--;
                return true;
            }
        }
        return false;
    }

}
