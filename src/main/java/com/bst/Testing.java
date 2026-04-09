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
import java.util.*;

public class Testing {
    enum TreeType {
        SIMPLE_BST,
        RB_TREE
    }

    static void runCLITest(int width) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the size of the array (Max is 1e7)");
        System.out.println("-".repeat(10));
        int size = (int) Math.clamp(sc.nextDouble(), 1, 1e7);

        System.out.println("Enter the array type (1) Sorted, (2) Reversed, (3) Random, (4) Partly Sorted");
        System.out.println("-".repeat(10));
        int mode = Math.clamp(sc.nextInt(), 1, 4);

        double percent = 0;
        if (mode == 4) {
            System.out.println("Enter the Sorted percentage");
            System.out.println("-".repeat(10));
            percent = sc.nextDouble();
        }

        System.out.println("Enter the tree type (1) Simple Tree.BST, (2) Red-Black Tree.BST");
        System.out.println("-".repeat(10));
        TreeType treeType = Math.clamp(sc.nextInt(), 1, 2) == 1 ? TreeType.SIMPLE_BST : TreeType.RB_TREE;

        List<Integer> arr = switch (mode) {
            case 1 -> ArrayGenerator.generateSortedArray(size);
            case 2 -> ArrayGenerator.generateSortedArray(size, true);
            case 3 -> ArrayGenerator.generateRandomArray(size);
            case 4 -> ArrayGenerator.generatePartiallySortedArray(percent, size);
            default -> throw new IllegalStateException("Unexpected value: " + mode);
        };

        System.out.println();
        System.out.println("RB Tree Performance Analysis");
        System.out.println("-".repeat(width));
        System.out.println("|" + String.format("%-" + (width - 2) / 2 + "s", "Algorithm") + "|"
                + String.format("%" + (width - 2) / 2 + "s", "Time(ms)") + "|");

        long start, end;
        start = System.currentTimeMillis();
        BST<Integer> bst = treeType == TreeType.SIMPLE_BST ? new SimpleBST<>() : new RedBlackTree<>();
        for (Integer ele : arr) {
            bst.insert(ele);
        }
        bst.inOrder();
        end = System.currentTimeMillis();
        System.out.println("|" + String.format("%-" + (width - 2) / 2 + "s", "TreeSort") + "|"
                + String.format("%" + (width - 2) / 2 + "s", (end - start) + "ms") + "|");

        start = System.currentTimeMillis();
        new MergeSort<Integer>().sort(arr, Comparator.naturalOrder());
        end = System.currentTimeMillis();
        System.out.println("|" + String.format("%-" + (width - 2) / 2 + "s", "MergeSort") + "|"
                + String.format("%" + (width - 2) / 2 + "s", (end - start) + "ms") + "|");
        sc.close();

    }

    static void populateAnalysisDataFile(Path path,
                                         int sizeIncrements, int fromSize, int toSize,
                                         double sortedPercent) throws IOException {
        if (!Files.exists(path)) {
            Files.createFile(path);
            Files.writeString(path, "Normal Algorithm, Size, SortedPercent, Normal Runtime, RB Runtime, BST Runtime\n");
        }
        int arrSize = fromSize;
        while (arrSize <= toSize) {
            double treeStart, treeEnd;
            double RBtreeStart, RBtreeEnd;
            double normStart, normEnd;

            double RBtreeRuntime, normRuntime, treeRuntime;
            List<Integer> arr = ArrayGenerator.generatePartiallySortedArray(sortedPercent, arrSize);
            // RB tree
            BST<Integer> bst = new RedBlackTree<>();

            RBtreeStart = System.nanoTime();
            for (int i : arr) {
                bst.insert(i);
            }
            bst.inOrder();// sorting
            RBtreeEnd = System.nanoTime();
            RBtreeRuntime = (RBtreeEnd - RBtreeStart) / 1e6;

            // Simple BST
            bst = new SimpleBST<>();
            treeStart = System.nanoTime();
            for (int i : arr) {
                bst.insert(i);
            }
            bst.inOrder();// sorting
            treeEnd = System.nanoTime();
            treeRuntime = (treeEnd - treeStart) / 1e6;

            normStart = System.nanoTime();
            new MergeSort<Integer>().sort(arr, Comparator.naturalOrder());
            normEnd = System.nanoTime();
            normRuntime = (normEnd - normStart) / 1e6;

            Files.writeString(
                    path,
                    "MergeSort, " + arrSize + ", " + sortedPercent + ", " + normRuntime + ", " + RBtreeRuntime + ", "
                            + treeRuntime + "\n",
                    StandardOpenOption.APPEND);

            arrSize += sizeIncrements;
        }
    }

    static void runSortBenchmarks(List<Integer> arr, int num_itrs) {
        System.out.println("Sorting Benchmarks\n" + "-".repeat(20));

        List<Double> RBRunningTimes = new ArrayList<>();
        List<Double> BSTRunningTimes = new ArrayList<>();
        List<Double> NormRunningTimes = new ArrayList<>();

        for (int i = 0; i < num_itrs; i++) {

            double treeStart, treeEnd;
            double RBtreeStart, RBtreeEnd;
            double normStart, normEnd;

            // RB tree
            BST<Integer> bst = new RedBlackTree<>();

            RBtreeStart = System.nanoTime();
            for (int ele : arr) {
                bst.insert(ele);
            }
            bst.inOrder();// sorting
            RBtreeEnd = System.nanoTime();
            RBRunningTimes.add((RBtreeEnd - RBtreeStart) / 1e6);

            // Simple BST
            bst = new SimpleBST<>();
            treeStart = System.nanoTime();
            for (int ele : arr) {
                bst.insert(ele);
            }
            bst.inOrder();// sorting
            treeEnd = System.nanoTime();
            BSTRunningTimes.add((treeEnd - treeStart) / 1e6);

            List<Integer> temp = new ArrayList<>(arr);
            // since the following sorting method sorts in place and i don't want to mess
            // with the og array
            normStart = System.nanoTime();
            new MergeSort<Integer>().sort(temp, Comparator.naturalOrder());
            normEnd = System.nanoTime();
            NormRunningTimes.add((normEnd - normStart) / 1e6);

        }

        double median, standardDeviation, BSTmean, RBmean, normMean;

        // NORMAL ---------
        {
            System.out.println("Normal (Merge Sort)\n" + "-".repeat(20));

            Collections.sort(NormRunningTimes);

            normMean = NormRunningTimes.stream().mapToDouble(e -> e).average().orElse(0.0);
            median = (NormRunningTimes.get(num_itrs / 2 - 1) + NormRunningTimes.get(num_itrs / 2)) / 2.0;

            standardDeviation = Math.sqrt(NormRunningTimes.stream()
                    .mapToDouble(e -> Math.pow(e - normMean, 2))
                    .average()
                    .orElse(0.0));

            System.out.println("Mean Runtime : " + normMean + "ms");
            System.out.println("Median Runtime : " + median + "ms");
            System.out.println("Standard Deviation : " + standardDeviation + "ms");
            System.out.println("-".repeat(20));
        }

        // BST --------
        {
            System.out.println("BST (Naive)\n" + "-".repeat(20));

            Collections.sort(BSTRunningTimes);
            BSTmean = BSTRunningTimes.stream().mapToDouble(e -> e).average().orElse(0.0);
            median = (BSTRunningTimes.get(num_itrs / 2 - 1) + BSTRunningTimes.get(num_itrs / 2)) / 2.0;
            standardDeviation = Math.sqrt(BSTRunningTimes.stream()
                    .mapToDouble(e -> Math.pow(e - BSTmean, 2))
                    .average()
                    .orElse(0.0));

            System.out.println("Mean Runtime : " + BSTmean + "ms");
            System.out.println("Median Runtime : " + median + "ms");
            System.out.println("Standard Deviation : " + standardDeviation + "ms");
            System.out.println("-".repeat(20));

        }

        // BST RB-TREE ---------
        {
            System.out.println("BST (Red-Black)\n" + "-".repeat(20));

            Collections.sort(RBRunningTimes);

            RBmean = RBRunningTimes.stream().mapToDouble(e -> e).average().orElse(0.0);
            median = (RBRunningTimes.get(num_itrs / 2 - 1) + RBRunningTimes.get(num_itrs / 2)) / 2.0;
            standardDeviation = Math.sqrt(RBRunningTimes.stream()
                    .mapToDouble(e -> Math.pow(e - RBmean, 2))
                    .average()
                    .orElse(0.0));

            System.out.println("Mean Runtime : " + RBmean + "ms");
            System.out.println("Median Runtime : " + median + "ms");
            System.out.println("Standard Deviation : " + standardDeviation + "ms");

            System.out.println("-".repeat(20));

            System.out.println("RB Speedup : " + (BSTmean / RBmean) + "x");
        }
        System.out.println("\n\n");
    }

    static void runInsertDeleteLookUpBenchmarks(List<Integer> arrayToInsert, int num_itrs) {

        System.out.println("Run-Insert-Delete Benchmarks \n " + "-".repeat(20));

        List<Double> RBInsertTimes = new ArrayList<>();
        List<Double> BSTInsertTimes = new ArrayList<>();

        List<Double> RBLookupTimes = new ArrayList<>();
        List<Double> BSTLookupTimes = new ArrayList<>();

        List<Double> RBDeleteTimes = new ArrayList<>();
        List<Double> BSTDeleteTimes = new ArrayList<>();
        BST<Integer> bst;

        for (int i = 0; i < num_itrs; i++) {

            double treeStart, treeEnd;
            double RBtreeStart, RBtreeEnd;

            // RB tree
            {
                // INSERT
                bst = new RedBlackTree<>();
                RBtreeStart = System.nanoTime();
                for (int ele : arrayToInsert) {
                    bst.insert(ele);
                }
                RBtreeEnd = System.nanoTime();
                RBInsertTimes.add((RBtreeEnd - RBtreeStart) / 1e6);
                System.out.println("Tree height after insertions : " + bst.height());

                // LOOKUP
                RBtreeStart = System.nanoTime();
                for (int ele = 0; ele < arrayToInsert.size() / 2; ele++) { // exists
                    bst.contains(ele);
                }
                for (int ele = arrayToInsert.size(); ele < arrayToInsert.size() + arrayToInsert.size() / 2; ele++) { // don't
                    // exist
                    bst.contains(ele);
                }
                RBtreeEnd = System.nanoTime();
                RBLookupTimes.add((RBtreeEnd - RBtreeStart) / 1e6);

                // DELETE
                RBtreeStart = System.nanoTime();
                for (int ele = 0; ele < arrayToInsert.size() * 0.2; ele++) { // delete random 20%
                    bst.delete((int) (Math.random() * arrayToInsert.size()));
                }
                RBtreeEnd = System.nanoTime();
                RBDeleteTimes.add((RBtreeEnd - RBtreeStart) / 1e6);

            }

            // Simple BST
            {
                // INSERT
                bst = new SimpleBST<>();
                treeStart = System.nanoTime();
                for (int ele : arrayToInsert) {
                    bst.insert(ele);
                }
                treeEnd = System.nanoTime();
                BSTInsertTimes.add((treeEnd - treeStart) / 1e6);
                System.out.println("Tree height after insertions : " + bst.height());

                // LOOKUP
                treeStart = System.nanoTime();
                for (int ele = 0; ele < arrayToInsert.size() / 2; ele++) { // exists
                    bst.contains(ele);
                }
                for (int ele = arrayToInsert.size(); ele < arrayToInsert.size() + arrayToInsert.size() / 2; ele++) { // don't
                    // exist
                    bst.contains(ele);
                }
                treeEnd = System.nanoTime();
                BSTLookupTimes.add((treeEnd - treeStart) / 1e6);

                // DELETE
                treeStart = System.nanoTime();
                for (int ele = 0; ele < arrayToInsert.size() * 0.2; ele++) { // delete random 20%
                    bst.delete((int) (Math.random() * arrayToInsert.size()));
                }
                treeEnd = System.nanoTime();
                BSTDeleteTimes.add((treeEnd - treeStart) / 1e6);

            }

        }

        System.out.println("Red-Black Tree\n" + "-".repeat(20));
        double RBRuntime = (RBInsertTimes.stream().mapToDouble(e -> e).sum() +
                RBLookupTimes.stream().mapToDouble(e -> e).sum() +
                RBDeleteTimes.stream().mapToDouble(e -> e).sum()) / num_itrs;

        System.out.println("Mean time for Insertions/Lookups/Deletes : " + RBRuntime + "ms\n" + "-".repeat(20));

        System.out.println("Simple BST\n" + "-".repeat(20));
        double BSTRuntime = (BSTInsertTimes.stream().mapToDouble(e -> e).sum() +
                BSTLookupTimes.stream().mapToDouble(e -> e).sum() +
                BSTDeleteTimes.stream().mapToDouble(e -> e).sum()) / num_itrs;

        System.out.println("Mean time for Insertions/Lookups/Deletes : " + BSTRuntime + "ms\n" + "-".repeat(20));

        System.out.println("Speed Up by Red-Black Tree : " + BSTRuntime / RBRuntime + "x");
        System.out.println("\n\n");

    }

    static void runBenchmarks() {
        int size = (int) 1e5;
        int num_itrs = 5;
        List<Integer> sortedPercentages = List.of(90, 95, 99);

        for (int sortedPercentage : sortedPercentages) {
            List<Integer> arr = ArrayGenerator.generatePartiallySortedArray(sortedPercentage, size);
            System.out.println("Array with " + (100 - sortedPercentage) + "% randomization");
            System.out.println("-".repeat(20));

            runInsertDeleteLookUpBenchmarks(arr, num_itrs);
            runSortBenchmarks(arr, num_itrs);

        }
    }

    static void main() throws IOException {

//        runBenchmarks();
//         populateAnalysisDataFile(Paths.get("runtime_data.csv"), 100, (int)1,
//         (int)1e4, 5);
        runCLITest(30);

    }

}
