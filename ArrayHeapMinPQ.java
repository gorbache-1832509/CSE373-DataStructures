package priorityqueues;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @see ExtrinsicMinPQ
 */
public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
    // IMPORTANT: Do not rename these fields or change their visibility.
    // We access these during grading to test your code.
    static final int START_INDEX = 0;
    List<PriorityNode<T>> items;
    HashMap<T, Integer> newItems;
    private int size;

    public ArrayHeapMinPQ() {
        items = new ArrayList<>();
        size = 0;
        newItems = new HashMap<>();
    }

    // Here's a method stub that may be useful. Feel free to change or remove it, if you wish.
    // You'll probably want to add more helper methods like this one to make your code easier to read.
    /**
     * A helper method for swapping the items at two indices of the array heap.
     */
    private void swap(int a, int b) {
        PriorityNode<T> item1 = items.get(a);
        PriorityNode<T> item2 = items.get(b);
        newItems.replace(item1.getItem(), b);
        newItems.replace(item2.getItem(), a);
        items.set(b, item1);
        items.set(a, item2);
    }

    @Override
    public void add(T item, double priority) {
        if (item == null || newItems.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        items.add(new PriorityNode<>(item, priority));
        size++;
        int index = 0;
        if (size > 1) {
            index = percolateUp(size - 1);
        }
        newItems.put(item, index);
    }

    // helper method for add
    private int percolateUp(int index) {
        if (items.get(index).getPriority() < items.get((index - 1) / 2).getPriority()) {
            swap(index, (index - 1) / 2);
            index = percolateUp((index - 1) / 2);
        }
        return index;
    }

    private int percolateDown(int index) {
        int leftChild = index * 2 + 1;
        int rightChild = index * 2 + 2;
        int realIndex;
        if (rightChild >= size) {
            if (leftChild < size) {
                realIndex = leftChild;
            } else {
                return index;
            }
        } else {
            if (items.get(leftChild).getPriority() <= items.get(rightChild).getPriority()) {
                realIndex = leftChild;
            } else {
                realIndex = rightChild;
            }
        }
        if (items.get(index).getPriority() > items.get(realIndex).getPriority()) {
            swap(index, realIndex);
            index = percolateDown(realIndex);
        }
        return index;
    }

    @Override
    public boolean contains(T item) {
        return newItems.containsKey(item);
    }

    @Override
    public T peekMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return items.get(0).getItem();
    }

    @Override
    public T removeMin() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        size--;
        T min = items.get(0).getItem();
        newItems.remove(min);
        items.set(0, items.get(size));
        items.remove(size);
        if (size > 0) {
            percolateDown(0);
        }
        return min;
    }

    @Override

    public void changePriority(T item, double priority) {
        if (!contains(item)) {
            throw new NoSuchElementException();
        }

        int index = newItems.get(item);
        items.get(index).setPriority(priority);
        percolateUp(index);
        percolateDown(index);
    }

    @Override
    public int size() {
        return size;
    }
}
