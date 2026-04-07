import Generator.ArrayGenerator;
import SortingStrats.MergeSort;

void main() {
    Scanner sc = new Scanner(System.in);
    System.out.println("Enter the size of the array (Max is 1e7)");
    System.out.println("-".repeat(10));
    int size = (int) Math.clamp(sc.nextDouble(), 1, 1e7);

    System.out.println("Enter the array type (1) Sorted, (2) Reversed, (3) Random");
    System.out.println("-".repeat(10));
    int mode = Math.clamp(sc.nextInt(), 1, 3);

    System.out.println("Enter the tree type (1) Simple BST, (2) Red-Black BST");
    System.out.println("-".repeat(10));
    int treeType = Math.clamp(sc.nextInt(), 1, 2);


    List<Integer> arr = switch (mode){
        case 1 -> ArrayGenerator.generateSortedArray(size);
        case 2 -> ArrayGenerator.generateSortedArray(size, true);
        case 3-> ArrayGenerator.generateRandomArray(size);
        default -> throw new IllegalStateException("Unexpected value: " + mode);
    };


    System.out.println();
    System.out.println("RB Tree Performance Analysis");
    int width = 30;
    System.out.println("-".repeat(width));
    System.out.println("|" + String.format("%-" + (width - 2)/2 + "s","Algorithm") + "|" + String.format("%" + (width - 2)/2 + "s","Time(ms)") + "|");


    long start, end;
    start = System.currentTimeMillis();
    BST<Integer> bst = treeType == 1? new SimpleBST<>(): new RedBlackTree<>();
    for (Integer ele : arr){
        bst.insert(ele);
    }
    bst.inOrder();
    end = System.currentTimeMillis();
    System.out.println("|" + String.format("%-" + (width - 2)/2 + "s","TreeSort") + "|" + String.format("%" + (width - 2)/2 + "s",(end - start) + "ms") + "|");


    start = System.currentTimeMillis();
    new MergeSort<Integer>().sort(arr, Comparator.naturalOrder());
    end = System.currentTimeMillis();
    System.out.println("|" + String.format("%-" + (width - 2)/2 + "s","MergeSort") + "|" + String.format("%" + (width - 2)/2 + "s",(end - start) + "ms") + "|");

}
