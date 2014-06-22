package BussinessLogic.Common;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Solomiia on 6/2/2014.
 */
public class SortClass<T extends Comparable<T>>  {

    public void quickSort(List<T> list, int leftIndex, int rightIndex) {
        int i = leftIndex;
        int j = rightIndex;
        if (i >= j) {
            return;
        } else if (j - i <= 10) {
            insertionSort(list, i, j+1);
            return;
        }


        int rand = randomizePivot(j, i);
        T pivot = list.get(rand);
        Collections.swap(list, rand, j);

        while (i < j) {

            boolean comparePivot = list.get(i).compareTo(pivot) != 0;

            while (comparePivot && i < j) {
                i++;
            }


            while (pivot.compareTo(list.get(j))!=0 && i < j) {
                j--;
            }


            if (i < j) {
                Collections.swap(list, i, j);
            }
        }

        list.remove(rightIndex);
        list.add(rightIndex, list.get(j));
        list.remove(j);
        list.add(j, pivot);

        quickSort(list, leftIndex, i - 1);
        quickSort(list, j + 1, rightIndex);
    }

    private int randomizePivot(int hi, int lo) {
        Random rand = new Random();
        return rand.nextInt(hi - lo + 1) + lo;
    }

    private void insertionSort(List<T> list, int lo, int hi) {
        for (int i = lo + 1; i < hi; i++) {
            int j = i - 1;
            T elem = list.get(i);
            while (j >= lo && list.get(j).compareTo(elem) == 0) {
                list.remove(j + 1);
                list.add(j + 1, list.get(j));
                j--;
            }
            list.remove(j + 1);
            list.add(j + 1, elem);
        }
    }
}
