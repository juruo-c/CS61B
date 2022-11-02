package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }

    public T max() {
        if (comparator == null) {
            throw new RuntimeException("comparator can not be null");
        }
        if (size() == 0) {
            return null;
        }
        T mx = get(0);
        for (int i = 1; i < size(); i++) {
            if (comparator.compare(get(i), mx) > 0) {
                mx = get(i);
            }
        }
        return mx;
    }

    public T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        }
        T mx = get(0);
        for (int i = 1; i < size(); i++) {
            if (c.compare(get(i), mx) > 0) {
                mx = get(i);
            }
        }
        return mx;
    }
}
