package util.range;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Interval<E> implements Range<E> {
	private final Comparator<? super E> comparator;
	private final E infimum;
	private final E supremum;
	private final int config;
	public static final int INFIMUM_INCLUDED = 0b0001;
	public static final int SUPREMUM_INCLUDED = 0b0010;
	public static final int INFIMUM_EXISTS = 0b0100;
	public static final int SUPREMUM_EXISTS = 0b1000;
	public static final int EMPTY = 0b10000;
	
	public Interval(final Comparator<? super E> comparator, final E infimum, final E supremum, final int config) {
		this.comparator = comparator;
		if (config > EMPTY) {
			throw new IllegalArgumentException("Illegal configuration.");
		} else if (((INFIMUM_INCLUDED | INFIMUM_EXISTS) & config) == INFIMUM_INCLUDED) {
			throw new IllegalArgumentException("Illegal configuration.");
		} else if (((SUPREMUM_INCLUDED | SUPREMUM_EXISTS) & config) == SUPREMUM_INCLUDED) {
			throw new IllegalArgumentException("Illegal configuration.");
		}
		if (config == EMPTY) {
			this.infimum = null;
			this.supremum = null;
			this.config = EMPTY;
		} else {
			if ((config & SUPREMUM_EXISTS) == 0) {
				this.supremum = null;
				if ((config & INFIMUM_EXISTS) == 0) {
					this.infimum = null;
					this.config = 0;
				} else {
					this.infimum = infimum;
					this.config = config;
				}
			} else {
				if ((config & INFIMUM_EXISTS) == 0) {
					this.infimum = null;
					this.supremum = supremum;
					this.config = config;
				} else {
					final int c = this.compare(infimum, supremum);
					if ((config & (INFIMUM_INCLUDED | SUPREMUM_INCLUDED)) == (INFIMUM_INCLUDED | SUPREMUM_INCLUDED)) {
						if (c > 0) {
							this.infimum = null;
							this.supremum = null;
							this.config = EMPTY;
						} else if (c == 0) {
							this.infimum = infimum;
							this.supremum = infimum;
							this.config = config;
						} else {
							this.infimum = infimum;
							this.supremum = supremum;
							this.config = config;
						}
					} else {
						if (c >= 0) {
							this.infimum = null;
							this.supremum = null;
							this.config = EMPTY;
						} else {
							this.infimum = infimum;
							this.supremum = supremum;
							this.config = config;
						}
					}
				}
			}
		}
	}
	
	public Interval(final E infimum, final E supremum, final int config) {
		this(null, infimum, supremum, config);
	}
	
	@SuppressWarnings("unchecked")
	private int compare(E e1, E e2) {
		return e1 == e2 ? 0 : this.comparator == null ? e1 == null
				? -((Comparable<? super E>) e2).compareTo(e1)
				: ((Comparable<? super E>) e1).compareTo(e2) : this.comparator.compare(e1, e2);
	}
	
	public Comparator<? super E> comparator() {
		return this.comparator;
	}
	
	@Override
	public boolean isEmpty() {
		return this.config == EMPTY;
	}
	
	@Override
	public boolean isSet() {
		return this.infimum == this.supremum && this.config != 0;
	}
	
	@Override
	public Set<E> toSet() {
		if (this.isEmpty()) {
			return new HashSet<E>();
		} else if (this.isSet()) {
			final Set<E> result = new HashSet<E>();
			result.add(this.infimum);
			return result;
		}
		throw new UnsupportedOperationException("Is no Set.");
	}
	
	@Override
	public boolean contains(final E e) {
		if (this.isEmpty()) {
			return false;
		} else if (this.hasInfimum()) {
			if (this.hasSupremum()) {
				return this.compare(this.infimum, e) <= (this.isInfimumIncluded() ? 0 : -1)
						&& this.compare(this.supremum, e) >= (this.isSupremumIncluded() ? 0 : 1);
			} else {
				return this.compare(this.infimum, e) <= (this.isInfimumIncluded() ? 0 : -1);
			}
		} else {
			if (this.hasSupremum()) {
				return this.compare(this.supremum, e) >= (this.isSupremumIncluded() ? 0 : 1);
			} else {
				return true;
			}
		}
	}
	
	public boolean connected(final E e) {
		if (this.isEmpty()) {
			return true;
		} else if (this.hasInfimum()) {
			if (this.hasSupremum()) {
				return this.compare(this.infimum, e) <= 0 && this.compare(this.supremum, e) >= 0;
			} else {
				return this.compare(this.infimum, e) <= 0;
			}
		} else {
			if (this.hasSupremum()) {
				return this.compare(this.supremum, e) >= 0;
			} else {
				return true;
			}
		}
	}
	
	public boolean connected(final Interval<E> i) {
		if (this.comparator != i.comparator) {
			throw new IncompatibleComparatorException();
		} else if (this.isEmpty() || i.isEmpty()) {
			return true;
		} else if (this.hasInfimum()) {// TODO
			if (this.hasSupremum()) {
				if (i.hasInfimum()) {
					if (i.hasSupremum()) {
						return this.compare(i.infimum, this.supremum) <= (this.isSupremumIncluded()
								|| i.isInfimumIncluded() ? 0 : -1)
								|| this.compare(this.infimum, i.supremum) <= (this.isInfimumIncluded()
										|| i.isSupremumIncluded() ? 0 : -1);
					} else {
						return this.compare(i.infimum, this.supremum) <= (this.isSupremumIncluded()
								|| i.isInfimumIncluded() ? 0 : -1);
					}
				} else {
					return this.compare(this.infimum, i.supremum) <= (this.isInfimumIncluded()
							|| i.isSupremumIncluded() ? 0 : -1);
				}
			} else {
				if (i.hasSupremum()) {
					return this.compare(this.infimum, i.supremum) <= (this.isInfimumIncluded()
							|| i.isSupremumIncluded() ? 0 : -1);
				} else {
					return true;
				}
			}
		} else {
			if (this.hasSupremum() && i.hasInfimum()) {
				return this.compare(this.supremum, i.infimum) >= (this.isSupremumIncluded() || i.isInfimumIncluded()
						? 0
						: 1);
			} else {
				return true;
			}
		}
	}
	
	@Override
	public boolean intersects(final Interval<E> i) {
		if (this.comparator != i.comparator) {
			throw new IncompatibleComparatorException();
		} else if (this.isEmpty() || i.isEmpty()) {
			return false;
		} else if (this.hasInfimum()) {
			if (this.hasSupremum()) {
				if (i.contains(infimum)) {
					return (!i.hasSupremum() && comparator.compare(this.infimum, i.supremum) != 0)
							|| (this.isInfimumIncluded() && i.isSupremumIncluded());
				} else if (i.contains(supremum)) {
					return (!i.hasInfimum() && comparator.compare(this.supremum, i.supremum) != 0)
							|| (this.isSupremumIncluded() && i.isInfimumIncluded());
				} else {
					return this.contains(i.infimum);
				}
			} else {
				if (i.hasSupremum()) {
					return this.compare(this.infimum, i.supremum) <= (this.isInfimumIncluded()
							&& i.isSupremumIncluded() ? 0 : -1);
				} else {
					return true;
				}
			}
		} else {
			if (this.hasSupremum() && i.hasInfimum()) {
				return this.compare(this.supremum, i.infimum) >= (this.isSupremumIncluded() && i.isInfimumIncluded()
						? 0
						: 1);
			} else {
				return true;
			}
		}
	}
	
	@Override
	public boolean hasInfimum() {
		return (this.config & INFIMUM_EXISTS) != 0;
	}
	
	@Override
	public E infimum() {
		if (this.hasInfimum()) {
			return this.infimum;
		} else {
			throw new NoSuchExtremumException("Infimum");
		}
	}
	
	@Override
	public boolean isInfimumIncluded() {
		if (this.hasInfimum()) {
			return (this.config & INFIMUM_INCLUDED) != 0;
		} else {
			throw new NoSuchExtremumException("Infimum");
		}
	}
	
	@Override
	public boolean hasSupremum() {
		return (this.config & SUPREMUM_EXISTS) != 0;
	}
	
	@Override
	public E supremum() {
		if (this.hasSupremum()) {
			return this.supremum;
		} else {
			throw new NoSuchExtremumException("Supremum");
		}
	}
	
	@Override
	public boolean isSupremumIncluded() {
		if (this.hasSupremum()) {
			return (this.config & SUPREMUM_INCLUDED) != 0;
		} else {
			throw new NoSuchExtremumException("Infimum");
		}
	}
	
	@Override
	public boolean isLowerBound(final E e) {
		return this.hasInfimum() ? this.compare(this.infimum, e) >= 0 : this.isEmpty();
	}
	
	@Override
	public boolean isUpperBound(final E e) {
		return this.hasSupremum() ? this.compare(this.supremum, e) <= 0 : this.isEmpty();
	}
	
	@Override
	public boolean isStrictLowerBound(final E e) {
		if (this.hasInfimum()) {
			return this.compare(this.infimum, e) >= (this.isInfimumIncluded() ? 1 : 0);
		} else {
			return this.isEmpty();
		}
	}
	
	@Override
	public boolean isStrictUpperBound(final E e) {
		if (this.hasSupremum()) {
			return this.compare(this.supremum, e) <= (this.isSupremumIncluded() ? -1 : 0);
		} else {
			return this.isEmpty();
		}
	}
	
	@Override
	public String toString() {
		return this.isEmpty() ? "O" : (this.hasInfimum() ? (this.isInfimumIncluded() ? "[" : "(")
				+ this.infimum.toString() : "(")
				+ (this.hasInfimum() ? this.infimum.toString() + (this.isInfimumIncluded() ? "]" : ")") : ")");
	}
	
	@Override
	public SeparateIntervalSet<E> toSeparateIntervalSet() {
		final SeparateIntervalSet<E> dis = new SeparateIntervalSet<E>();
		dis.union(this);
		return null;
	}
	
	public static <E> Interval<? extends E> intersection(Interval<E> i1, Interval<? extends E> i2) {
		if (i1.comparator != i2.comparator) {
			throw new IncompatibleComparatorException();
		} else if (i1.isEmpty() || i2.isEmpty()) {
			return new Interval<E>(i1.comparator, null, null, EMPTY);
		} else {
			E inf = null;
			int infConf = 0;
			if (i1.hasInfimum()) {
				if (i2.hasInfimum()) {
					int c = i1.comparator.compare(i1.infimum, i2.infimum);
					if (c == 0) {
						inf = i1.infimum;
						infConf = i1.config & i2.config & INFIMUM_INCLUDED | INFIMUM_EXISTS;
					} else if (c < 0) {
						inf = i2.infimum;
						infConf = i2.config & INFIMUM_INCLUDED | INFIMUM_EXISTS;
					} else {
						inf = i1.infimum;
						infConf = i1.config & INFIMUM_INCLUDED | INFIMUM_EXISTS;
					}
				} else {
					inf = i1.infimum;
					infConf = i1.config & INFIMUM_INCLUDED | INFIMUM_EXISTS;
				}
			} else if (i2.hasInfimum()) {
				inf = i2.infimum;
				infConf = i2.config & INFIMUM_INCLUDED | INFIMUM_EXISTS;
			}
			E sup = null;
			int supConf = 0;
			if (i1.hasSupremum()) {
				if (i2.hasSupremum()) {
					int c = i1.comparator.compare(i1.supremum, i2.supremum);
					if (c == 0) {
						sup = i1.supremum;
						supConf = i1.config & i2.config & SUPREMUM_INCLUDED | SUPREMUM_EXISTS;
					} else if (c < 0) {
						sup = i1.supremum;
						supConf = i1.config & SUPREMUM_INCLUDED | SUPREMUM_EXISTS;
					} else {
						sup = i2.supremum;
						supConf = i2.config & SUPREMUM_INCLUDED | SUPREMUM_EXISTS;
					}
				} else {
					sup = i1.supremum;
					supConf = i1.config & SUPREMUM_INCLUDED | SUPREMUM_EXISTS;
				}
			} else if (i2.hasSupremum()) {
				sup = i2.supremum;
				supConf = i2.config & SUPREMUM_INCLUDED | SUPREMUM_EXISTS;
			}
			return new Interval<E>(i1.comparator, inf, sup, infConf | supConf);
		}
	}
	
	public static <E> Interval<? extends E> connectedUnion(Interval<E> i1, Interval<? extends E> i2) {
		if (i1.comparator != i2.comparator) {
			throw new IncompatibleComparatorException();
		} else if (i1.isEmpty()) {
			return new Interval<E>(i1.comparator, i2.infimum, i2.supremum, i2.config);
		} else if (i2.isEmpty()) {
			return new Interval<E>(i1.comparator, i1.infimum, i1.supremum, i1.config);
		} else {
			if (i1.hasSupremum() && i2.hasInfimum()) {
				if (i1.comparator.compare(i1.supremum, i2.infimum) <= (i1.isSupremumIncluded()
						|| i2.isInfimumIncluded() ? 1 : 0)) {
					throw new IllegalArgumentException("Intervals are not connected.");
				}
			}
			if (i2.hasSupremum() && i1.hasInfimum()) {
				if (i1.comparator.compare(i2.supremum, i1.infimum) <= (i2.isSupremumIncluded()
						|| i1.isInfimumIncluded() ? 1 : 0)) {
					throw new IllegalArgumentException("Intervals are not connected.");
				}
			}
			E inf = null;
			int infConf = 0;
			if (i1.hasInfimum() && i2.hasInfimum()) {
				int c = i1.comparator.compare(i1.infimum, i2.infimum);
				if (c == 0) {
					inf = i1.infimum;
					infConf = (i1.config | i2.config) & INFIMUM_INCLUDED | INFIMUM_EXISTS;
				} else if (c < 0) {
					inf = i1.infimum;
					infConf = i1.config & INFIMUM_INCLUDED | INFIMUM_EXISTS;
				} else {
					inf = i2.infimum;
					infConf = i2.config & INFIMUM_INCLUDED | INFIMUM_EXISTS;
				}
			}
			E sup = null;
			int supConf = 0;
			if (i1.hasInfimum() && i2.hasInfimum()) {
				int c = i1.comparator.compare(i1.supremum, i2.supremum);
				if (c == 0) {
					sup = i1.supremum;
					supConf = (i1.config | i2.config) & SUPREMUM_INCLUDED | SUPREMUM_EXISTS;
				} else if (c < 0) {
					sup = i2.supremum;
					supConf = i2.config & SUPREMUM_INCLUDED | SUPREMUM_EXISTS;
				} else {
					sup = i1.supremum;
					supConf = i1.config & SUPREMUM_INCLUDED | SUPREMUM_EXISTS;
				}
			}
			return new Interval<E>(i1.comparator, inf, sup, infConf | supConf);
		}
	}
	
	public boolean isConnected(Interval<? extends E> i) {
		if (this.comparator != i.comparator) {
			throw new IncompatibleComparatorException();
		} else if (this.isEmpty() || i.isEmpty()) {
			return false;
		} else {
			if (this.hasSupremum() && i.hasInfimum()) {
				if (this.comparator.compare(this.supremum, i.infimum) <= (this.isSupremumIncluded()
						|| i.isInfimumIncluded() ? 1 : 0)) {
					return false;
				}
			}
			if (i.hasSupremum() && this.hasInfimum()) {
				if (this.comparator.compare(i.supremum, this.infimum) <= (i.isSupremumIncluded()
						|| this.isInfimumIncluded() ? 1 : 0)) {
					return false;
				}
			}
			return true;
		}
	}
	
	@Override
	public boolean intersects(Range<E> r) {
		return r.intersects(this);
	}
}
