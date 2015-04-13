package util.range;

import java.util.Iterator;
import java.util.Set;

public interface WritableRange<E> extends Range<E> {
	default void union(final Range<E> r) {
		final Iterator<Interval<E>> it = r.toSeparateIntervalSet().getIntervalSet().iterator();
		while (it.hasNext()) {
			union(it.next());
		}
	}
	
	default void union(final Set<E> s) {
		final Iterator<E> it = s.iterator();
		while (it.hasNext()) {
			add(it.next());
		}
	}
	
	void union(Interval<E> i);
	
	void add(E e);
	
	default void remove(final Range<E> r) {
		final Iterator<Interval<E>> it = r.toSeparateIntervalSet().getIntervalSet().iterator();
		while (it.hasNext()) {
			remove(it.next());
		}
	}
	
	default void remove(final Set<E> s) {
		final Iterator<E> it = s.iterator();
		while (it.hasNext()) {
			remove(it.next());
		}
	}
	
	void remove(Interval<E> i);
	
	void remove(E e);
	
	void clear();
}
