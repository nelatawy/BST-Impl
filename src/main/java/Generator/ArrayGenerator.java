package Generator;

import SortingStrats.MergeSort;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ArrayGenerator {

    private static void shufflePartition(int l, int r, List<Integer> arr){
        for (int i = l; i < r; i++){
            int swapIdx = (int) ((Math.random()*(r - i)) + i);
            Collections.swap(arr, i, swapIdx);
        }
    }
    public static List<Integer> generatePartiallySortedArray(double percent, int size){
        List<Integer> arr = generateSortedArray(size);
        int shuffledCnt = (int) (((100 - percent) / 100)*size);
        //pick starting idx
        int startIdx = (int) (Math.random()*(size - shuffledCnt));
        shufflePartition(startIdx, startIdx + shuffledCnt, arr);
        return arr;
    }
    public static List<Integer> generateRandomArray(int size, int from, int to) {
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            list.add(from + (int)(Math.random() * (to - from)));
        }
        return list;
    }

    public static List<Integer> generateRandomArray(int size) {
        return generateRandomArray(size, 0, size);
    }

    public static List<Integer> generateSortedArray(int size, int from, int to, boolean descending) {
        List<Integer> list = generateRandomArray(size, from, to);
        return new MergeSort<Integer>().sort(list, (descending) ? Comparator.reverseOrder() : Comparator.naturalOrder());
    }

    public static List<Integer> generateSortedArray(int size) {
        return generateSortedArray(size, 0, size, false);
    }

    public static List<Integer> generateSortedArray(int size, boolean descending) {
        return generateSortedArray(size, 0, size, descending);
    }

    public static void main(){
        List<Integer> arr = generatePartiallySortedArray(100, 20);
        for (int i  : arr){
            System.out.println(i);
        }
    }

}
