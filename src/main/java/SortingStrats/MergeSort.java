package SortingStrats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MergeSort<E extends Comparable<E>> {

    private List<E> merge(List<E> list, int start1, int start2, int end, Comparator<E> comparator) {
        List<E> merged = new ArrayList<>();
        int itr1 = start1;
        int itr2 = start2;
        while (itr1 < start2 && itr2 < end) {

            if (comparator.compare(list.get(itr1), list.get(itr2)) < 0) {
                merged.add(list.get(itr1));
                itr1++;
            } else {
                merged.add(list.get(itr2));
                itr2++;
            }
        }
        while (itr1 < start2) {
            merged.add(list.get(itr1));
            itr1++;
        }
        while (itr2 < end) {
            merged.add(list.get(itr2));
            itr2++;
        }
        return merged;
    }

    public List<E> sort(List<E> list, Comparator<E> comparator) {
        return mergesort(list, 0, list.size(), comparator);
    }

    private List<E> mergesort(List<E> list, int start, int end, Comparator<E> comparator) {
        if (list == null || start >= end - 1)
            return Collections.emptyList();

        int mid = (start + end) / 2;
        mergesort(list, start, mid, comparator);
        mergesort(list, mid, end, comparator);
        List<E> merged = merge(list, start, mid, end, comparator);

        for (int i = start; i < end; i++) {
            list.set(i, merged.get(i - start));
        }
        return list;
    }

}
