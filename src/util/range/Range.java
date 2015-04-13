package util.range;

import java.util.NoSuchElementException;
import java.util.Set;

public interface Range<E> {
	boolean isEmpty();

	boolean isSet();

	Set<E> toSet();

	boolean contains(E e);

	boolean intersects(final Range<E> r);

	boolean intersects(Interval<E> i);

	boolean hasInfimum();

	E infimum();

	boolean isInfimumIncluded();

	boolean hasSupremum();

	E supremum();

	boolean isSupremumIncluded();

	boolean isLowerBound(E e);

	boolean isUpperBound(E e);

	boolean isStrictLowerBound(E e);

	boolean isStrictUpperBound(E e);

	public class NoSuchExtremumException extends NoSuchElementException {
		/**
		 *
		 */
		private static final long serialVersionUID = -1730765096824659819L;

		public NoSuchExtremumException() {
			super();
		}

		public NoSuchExtremumException(final String s) {
			super(s);
		}
	}

	SeparateIntervalSet<E> toSeparateIntervalSet();
}
