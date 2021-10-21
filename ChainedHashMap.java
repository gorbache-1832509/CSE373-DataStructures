package maps;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * @see AbstractIterableMap
 * @see Map
 */
public class ChainedHashMap<K, V> extends AbstractIterableMap<K, V> {
    private static final double DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD = 1;
    private static final int DEFAULT_INITIAL_CHAIN_COUNT = 10;
    private static final int DEFAULT_INITIAL_CHAIN_CAPACITY = 6;

    /*
    Warning:
    You may not rename this  field or change its type.
    We will be inspecting it in our secret tests.
     */
    AbstractIterableMap<K, V>[] chains;
    Iterator<Map.Entry<K, V>> itr;
    // number of elements
    int size;
    // load capacity
    double loadCapacity;
    // number of chains
    int chainAmount;
    // You're encouraged to add extra fields (and helper methods) though!
    int defaultChainCapacity;

    public ChainedHashMap() {
        this(DEFAULT_RESIZING_LOAD_FACTOR_THRESHOLD, DEFAULT_INITIAL_CHAIN_COUNT, DEFAULT_INITIAL_CHAIN_CAPACITY);
    }

    public ChainedHashMap(double resizingLoadFactorThreshold, int initialChainCount, int chainInitialCapacity) {
        size = 0;
        loadCapacity = resizingLoadFactorThreshold;
        this.chains = this.createArrayOfChains(initialChainCount);
        chainAmount = initialChainCount;
        defaultChainCapacity = chainInitialCapacity;
        for (int i = 0; i < initialChainCount; i++) {
            chains[i] = createChain(chainInitialCapacity);
        }
    }

    /**
     * This method will return a new, empty array of the given size that can contain
     * {@code AbstractIterableMap<K, V>} objects.
     *
     * Note that each element in the array will initially be null.
     *
     * Note: You do not need to modify this method.
     * @see ArrayMap createArrayOfEntries method for more background on why we need this method
     */
    @SuppressWarnings("unchecked")
    private AbstractIterableMap<K, V>[] createArrayOfChains(int arraySize) {
        return (AbstractIterableMap<K, V>[]) new AbstractIterableMap[arraySize];
    }

    /**
     * Returns a new chain.
     *
     * This method will be overridden by the grader so that your ChainedHashMap implementation
     * is graded using our solution ArrayMaps.
     *
     * Note: You do not need to modify this method.
     */
    protected AbstractIterableMap<K, V> createChain(int initialSize) {
        return new ArrayMap<>(initialSize);
    }

    @Override
    public V get(Object key) {
        int x = reformHash(key);
        if (chains[x].containsKey(key)) {
            return chains[x].get(key);
        } else {
            return null;
        }
    }

    @Override
    public V put(K key, V value) {
        size++;
        int x = reformHash(key);
        if (size / chainAmount >= loadCapacity) {
            resize(chainAmount * 2);
        }
        if (chains[x].containsKey(key)) {
            size--;
        }
        return chains[x].put(key, value);
    }



    @Override
    public V remove(Object key) {
        int x = reformHash(key);
        if (chains[x].containsKey(key)) {
            size--;
            return chains[x].remove(key);
        } else {
            return null;
        }
    }

    @Override
    public void clear() {
        size = 0;
        for (int i = 0; i < DEFAULT_INITIAL_CHAIN_COUNT; i++) {
            chains[i].clear();
        }
        chainAmount = DEFAULT_INITIAL_CHAIN_COUNT;
    }

    @Override
    public boolean containsKey(Object key) {
        int x = reformHash(key);
        return chains[x].containsKey(key);
    }

    @Override
    public int size() {
        return size;
    }

    private int reformHash(Object key) {
        if (Objects.equals(null, key)) {
            return 0;
        }

        int x = key.hashCode();
        if (x < 0) {
            x = -x;
        }
        return x % chainAmount;
    }

    private void resize(int newChainSize) {
        AbstractIterableMap<K, V>[] newChains = createArrayOfChains(newChainSize);
        for (int i = 0; i < newChainSize; i++) {
            newChains[i] = createChain(defaultChainCapacity);
        }

        chainAmount *= 2;
        for (int i = 0; i < chainAmount / 2; i++) {
            if (!Objects.equals(chains[i], null)) {
                Iterator<Map.Entry<K, V>> iterator1 = chains[i].iterator();
                while (iterator1.hasNext()) {
                    Map.Entry<K, V> element = iterator1.next();
                    int x = reformHash(element.getKey().hashCode());
                    newChains[x].put(element.getKey(), element.getValue());
                }
            }
        }

        chains = newChains;
    }


    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        // Note: you won't need to change this method (unless you add more constructor parameters)
        return new ChainedHashMapIterator<>(this.chains, this.itr);
    }

    /*
    See the assignment webpage for tips and restrictions on implementing this iterator.
     */
    private static class ChainedHashMapIterator<K, V> implements Iterator<Map.Entry<K, V>> {
        private AbstractIterableMap<K, V>[] chains;
        private Iterator<Map.Entry<K, V>> itr;
        private int index;

        public ChainedHashMapIterator(AbstractIterableMap<K, V>[] chains, Iterator<Map.Entry<K, V>> itr) {
            this.chains = chains;
            index = -1;
            this.nextChain();


        }

        @Override
        public boolean hasNext() {
            while (index < chains.length) {
                if (itr.hasNext()) {
                    return true;
                } else {
                    nextChain();
                }
            }
            return false;
        }

        @Override
        public Map.Entry<K, V> next() {
            // use nextChain
            while (hasNext()) {
                if (itr.hasNext()) {
                    return itr.next();
                } else {
                    nextChain();
                }
            }
            throw new NoSuchElementException();
        }

        private void nextChain() {
            index++;
            while (index < chains.length && chains[index] == null && chains[index].size() < 1) {
                index++;
            }
            if (index < chains.length) {
                this.itr = chains[index].iterator();
            } else {
                this.itr = null;
            }
        }
    }
}
