package disjointsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A quick-union-by-size data structure with path compression.
 * @see DisjointSets for more documentation.
 */
public class UnionBySizeCompressingDisjointSets<T> implements DisjointSets<T> {
    // Do NOT rename or delete this field. We will be inspecting it directly in our private tests.
    List<Integer> pointers;
    Map<T, T> ids;
    Map<T, Integer> rootIds;
    Map<T, Integer> sizes;
    Set<T> visited;
    /*
    However, feel free to add more fields and private helper methods. You will probably need to
    add one or two more fields in order to successfully implement this class.
    */

    public UnionBySizeCompressingDisjointSets() {
        pointers = new ArrayList<>();
        ids = new HashMap<>();
        rootIds = new HashMap<>();
        sizes = new HashMap<>();
        visited = new HashSet<>();
    }

    @Override
    public void makeSet(T item) {
        if (ids.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        ids.put(item, item);
        pointers.add(-1);
        rootIds.put(item, pointers.size() - 1);
        sizes.put(item, 1);
    }

    @Override
    public int findSet(T item) {
        if (!ids.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        visited.add(item);
        item = helperFind(item);
        for (T randomItem : visited) {
            T root = helperFind(randomItem);
            ids.put(randomItem, root);
            int realId = rootIds.get(root);
            pointers.add(rootIds.get(randomItem), realId);
        }
        visited.clear();
        return rootIds.get(item);
    }

    private T helperFind(T item) {
        if (Objects.equals(item, ids.get(item))) {
            return item;
        }
        visited.add(item);
        item = ids.get(item);
        return helperFind(item);
    }

    @Override
    public boolean union(T item1, T item2) {
        if (!ids.containsKey(item1) || !ids.containsKey(item2)) {
            throw new IllegalArgumentException();
        }
        T rootOne = helperFind(item1);
        T rootTwo = helperFind(item2);
        int id1 = findSet(item1);
        int id2 = findSet(item2);

        if (id1 == id2) {
            return false;
        }

        if (sizes.get(rootOne) >= sizes.get(rootTwo)) {
            pointers.add(rootIds.get(rootTwo), rootIds.get(rootOne));
            rootIds.put(rootTwo, rootIds.get(rootOne));
            ids.put(rootTwo, rootOne);
            sizes.put(rootOne, sizes.get(rootOne) + sizes.get(rootTwo));
            sizes.remove(rootTwo);
        } else {
            pointers.add(rootIds.get(rootOne), rootIds.get(rootTwo));
            rootIds.put(rootOne, rootIds.get(rootTwo));
            ids.put(rootOne, rootTwo);
            sizes.put(rootTwo, sizes.get(rootTwo) + sizes.get(rootOne));
            sizes.remove(rootOne);
        }

        return true;
    }
}
