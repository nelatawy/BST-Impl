package com.bst;

import Generator.ArrayGenerator;
import SortingStrats.MergeSort;
import Tree.BST;
import Tree.RedBlackTree;
import Tree.SimpleBST;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Testing {
    enum TreeType{
        SIMPLE_BST,
        RB_TREE
    }


    static void runCLITest(int width){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the size of the array (Max is 1e7)");
        System.out.println("-".repeat(10));
        int size = (int) Math.clamp(sc.nextDouble(), 1, 1e7);

        System.out.println("Enter the array type (1) Sorted, (2) Reversed, (3) Random, (4) Partly Sorted");
        System.out.println("-".repeat(10));
        int mode = Math.clamp(sc.nextInt(), 1, 3);

        double percent = 0;
        if (mode == 4){
            System.out.println("Enter the Sorted percentage");
            System.out.println("-".repeat(10));
            percent = sc.nextDouble();
        }

        System.out.println("Enter the tree type (1) Simple Tree.BST, (2) Red-Black Tree.BST");
        System.out.println("-".repeat(10));
        TreeType treeType = Math.clamp(sc.nextInt(), 1, 2) == 1? TreeType.SIMPLE_BST : TreeType.RB_TREE;


        List<Integer> arr = switch (mode){
            case 1 -> ArrayGenerator.generateSortedArray(size);
            case 2 -> ArrayGenerator.generateSortedArray(size, true);
            case 3 -> ArrayGenerator.generateRandomArray(size);
            case 4 -> ArrayGenerator.generatePartiallySortedArray(percent, size);
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };

        System.out.println();
        System.out.println("RB Tree Performance Analysis");
        System.out.println("-".repeat(width));
        System.out.println("|" + String.format("%-" + (width - 2)/2 + "s","Algorithm") + "|" + String.format("%" + (width - 2)/2 + "s","Time(ms)") + "|");

        long start, end;
        start = System.currentTimeMillis();
        BST<Integer> bst = treeType == TreeType.SIMPLE_BST? new SimpleBST<>(): new RedBlackTree<>();
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
        sc.close();

    }

    static void populateAnalysisDataFile(Path path,
                                         int sizeIncrements, int fromSize, int toSize,
                                         double sortedPercent,TreeType treeType) throws IOException {
        if (!Files.exists(path)){
            Files.createFile(path);
            Files.writeString(path, "Normal Algorithm, TreeType, Size, SortedPercent, Normal Runtime, Tree Runtime\n");
        }
        int arrSize = fromSize;
        while (arrSize <= toSize){
            double treeStart, treeEnd, normStart, normEnd;
            double treeRuntime, normRuntime;
            List<Integer> arr = ArrayGenerator.generatePartiallySortedArray(sortedPercent, arrSize);
            BST<Integer> bst = treeType == TreeType.RB_TREE ? new RedBlackTree<>() : new SimpleBST<>();

            treeStart = System.nanoTime();
            for (int i : arr){
                bst.insert(i);
            }
            bst.inOrder();//sorting
            treeEnd = System.nanoTime();
            treeRuntime = (treeEnd - treeStart)/1e6;

            normStart = System.nanoTime();
            new MergeSort<Integer>().sort(arr, Comparator.naturalOrder());
            normEnd = System.nanoTime();
            normRuntime = (normEnd - normStart)/1e6;

            Files.writeString(
                    path,
                    "MergeSort, " + treeType.toString() + ", " + arrSize + ", " + sortedPercent + ", " + normRuntime + ", " + treeRuntime + "\n",
                    StandardOpenOption.APPEND);

            arrSize += sizeIncrements;
        }
    }


    static void main() throws IOException {


        populateAnalysisDataFile(Paths.get("runtime_data.csv"), 100, (int)1, (int)1e4, 75, Testing.TreeType.RB_TREE);

    }

}
