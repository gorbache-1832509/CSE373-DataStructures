package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;


/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ArrayMap<K, V> extends AbstractIterableMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 10;
    private int size;
    /*
    Warning:
    You may not rename this field or change its type.
    We will be inspecting it in our secret tests.
     */
    SimpleEntry<K, V>[] entries;

    // You may add extra fields or helper methods though!

    public ArrayMap() {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    public ArrayMap(int initialCapacity) {
        this.entries = this.createArrayOfEntries(initialCapacity);
        size = 0;
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code Entry<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     */
    @SuppressWarnings("unchecked")
    private SimpleEntry<K, V>[] createArrayOfEntries(int arraySize) {
        /*
        It turns out that creating arrays of generic objects in Java is complicated due to something
        known as "type erasure."

        We've given you this helper method to help simplify this part of your assignment. Use this
        helper method as appropriate when implementing the rest of this class.

        You are not required to understand how this method works, what type erasure is, or how
        arrays and generics interact.
        */
        return (SimpleEntry<K, V>[]) (new SimpleEntry[arraySize]);
    }

    @Override
    public V get(Object key) {
        for (int i = 0; i < size; i++) {
            if (Objects.equals(entries[i].getKey(), key)) {
                return entries[i].getValue();
            }
        }
        return null;
    }

    @Override
    public V put(K key, V value) {
        if (size >= entries.length) {
            SimpleEntry<K, V>[] newEntry = new SimpleEntry[entries.length * 2];
            for (int i = 0; i < entries.length; i++) {
               newEntry[i] = entries[i];
            }
            entries = newEntry;
        }

        int index = 0;
        while (index < size) {
            if (Objects.equals(entries[index].getKey(), key)) {
                V oldValue = entries[index].getValue();
                entries[index].setValue(value);
                return oldValue;
            } else {
                index++;
            }
        }
        size++;
        entries[index] = new SimpleEntry<>(key, value);
        return null;
    }

    @Override
    public V remove(Object key) {
        V value;
        for (int i = 0; i < size; i++) {
            if (Objects.equals(entries[i].getKey(), key)) {
                value = entries[i].getValue();
                if (i != size - 1) {
                    entries[i] = entries[size - 1];
                    entries[size - 1] = null;
                } else {
                    entries[i] = null;
                }
                size--;
                return value;
            }
        }
        return null;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            entries[i] = null;
        }
        size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        for (int i = 0; i < size; i++) {
            if ((entries[i].getKey() == null && entries[i].getValue() != null)
                || entries[i].getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() { return new ArrayMapIterator<>(this.entries);
    }

    private static class ArrayMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private final SimpleEntry<K, V>[] entries;
        public int i;

        public ArrayMapIterator(SimpleEntry<K, V>[] entries) {
            this.entries = entries;
            i = 0;
        }

        @Override
        public boolean hasNext() {
            return (i != entries.length) && (entries[i] != (null));
        }

        @Override
        public Map.Entry<K, V> next() {
            if (hasNext()) {
                return entries[i++];
            }
            throw new NoSuchElementException();

        }
    }
}
