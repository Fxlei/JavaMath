package util.range;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

public class SeparateIntervalSet<E> implements WritableRange<E> {
	private TreeNode root;
	
	public SeparateIntervalSet() {
		this.root = null;
	}
	
	@Override
	public boolean isEmpty() {
		return root == null;
	}
	
	@Override
	public boolean isSet() {
		return root == null || root.isSet();
	}
	
	@Override
	public Set<E> toSet() {
		Set<E> set = new HashSet<E>();
		if (root != null) {
			root.addToSet(set);
		}
		return set;
	}
	
	@Override
	public boolean contains(E e) {
		TreeNode node = root;
		while (node != null) {
			if (root.value.contains(e)) {
				return true;
			} else if (node.value.isLowerBound(e)) {
				node = node.left;
			} else {
				node = node.right;
			}
		}
		return false;
	}
	
	@Override
	public boolean intersects(Interval<E> i) {
		TreeNode node = root;
		while (node != null) {
			if (root.value.intersects(i)) {
				return true;
			} else if (node.value.isLowerBound(i.supremum())) {
				node = node.left;
			} else {
				node = node.right;
			}
		}
		return false;
	}
	
	@Override
	public boolean intersects(Range<E> r) {
		if (r.isEmpty() || this.isEmpty()) {
			return false;
		}
		Iterator<Interval<E>> il = r.toSeparateIntervalSet().iterateIntervals();
		Iterator<Interval<E>> ih = this.iterateIntervals();
		Interval<E> el = il.next();
		Interval<E> eh = ih.next();
		if (el.intersects(eh)) {
			return true;
		} else if (el.isLowerBound(eh.supremum())) {
			Iterator<Interval<E>> i = il;
			il = ih;
			ih = i;
			eh = el;
		}
		while (il.hasNext()) {
			el = il.next();
			if (el.intersects(eh)) {
				return true;
			} else if (el.isLowerBound(eh.supremum())) {
				Iterator<Interval<E>> i = il;
				il = ih;
				ih = i;
				eh = el;
			}
		}
		return false;
	}
	
	public Iterator<Interval<E>> iterateIntervals() {
		return new TreeIterator();
	}
	
	@Override
	public boolean hasInfimum() {
		if (root == null) {
			return false;
		} else {
			TreeNode node = root;
			while (node.left != null) {
				node = node.left;
			}
			return node.value.hasInfimum();
		}
	}
	
	@Override
	public E infimum() {
		if (root == null) {
			throw new NoSuchExtremumException("Infimum");
		} else {
			TreeNode node = root;
			while (node.left != null) {
				node = node.left;
			}
			return node.value.infimum();
		}
	}
	
	@Override
	public boolean isInfimumIncluded() {
		if (root == null) {
			throw new NoSuchExtremumException("Infimum");
		} else {
			TreeNode node = root;
			while (node.left != null) {
				node = node.left;
			}
			return node.value.isInfimumIncluded();
		}
	}
	
	@Override
	public boolean hasSupremum() {
		if (root == null) {
			return false;
		} else {
			TreeNode node = root;
			while (node.right != null) {
				node = node.right;
			}
			return node.value.hasSupremum();
		}
	}
	
	@Override
	public E supremum() {
		if (root == null) {
			throw new NoSuchExtremumException("Supremum");
		} else {
			TreeNode node = root;
			while (node.left != null) {
				node = node.left;
			}
			return node.value.supremum();
		}
	}
	
	@Override
	public boolean isSupremumIncluded() {
		if (root == null) {
			throw new NoSuchExtremumException("Supremum");
		} else {
			TreeNode node = root;
			while (node.left != null) {
				node = node.left;
			}
			return node.value.isSupremumIncluded();
		}
	}
	
	@Override
	public boolean isLowerBound(E e) {
		if (root == null) {
			return true;
		} else {
			TreeNode node = root;
			while (node.left != null) {
				node = node.left;
			}
			return node.value.isLowerBound(e);
		}
	}
	
	@Override
	public boolean isStrictLowerBound(E e) {
		if (root == null) {
			return true;
		} else {
			TreeNode node = root;
			while (node.left != null) {
				node = node.left;
			}
			return node.value.isStrictLowerBound(e);
		}
	}
	
	@Override
	public boolean isUpperBound(E e) {
		if (root == null) {
			return true;
		} else {
			TreeNode node = root;
			while (node.right != null) {
				node = node.right;
			}
			return node.value.isUpperBound(e);
		}
	}
	
	@Override
	public boolean isStrictUpperBound(E e) {
		if (root == null) {
			return true;
		} else {
			TreeNode node = root;
			while (node.right != null) {
				node = node.right;
			}
			return node.value.isStrictUpperBound(e);
		}
	}
	
	@Override
	public SeparateIntervalSet<E> toSeparateIntervalSet() {
		return this;
	}
	
	@Override
	public String toString() {
		if (root == null) {
			return "O";
		} else {
			Iterator<Interval<E>> it = iterateIntervals();
			StringBuilder sb = new StringBuilder(it.next().toString());
			while (it.hasNext()) {
				sb.append("U").append(it.next().toString());
			}
			return sb.toString();
		}
	}
	
	@Override
	public void add(E e) {
		TreeNode node = root;
		while (node != null) {
			if (node.value.connected(e)) {
				if (node.value.compare(node.value.infimum(), e) == 0) {
					if (!node.value.isInfimumIncluded()) {
						node.value = new Interval<E>(node.value.comparator(), e, node.value.supremum(),
								node.value.config | Interval.INFIMUM_EX_INCLUDED);
						
						TreeNode nl = node.left;
						if (nl != null) {
							if (nl.right == null) {
								if (nl.value.connected(e)) {
									node.right = nl.left;
								}
							} else {
								while (nl.right.right != null) {
									nl = nl.left;
								}
								if (nl.right.value.connected(e)) {
									node.value = Interval.connectedUnion(node.value, nl.right.value);
									nl.right = nl.right.left;
								}
							}
						}
					}
				} else if (node.value.compare(node.value.supremum(), e) == 0) {
					if (!node.value.isSupremumIncluded()) {
						node.value = new Interval<E>(node.value.comparator(), node.value.infimum(), e,
								node.value.config | Interval.SUPREMUM_EX_INCLUDED);
						
						TreeNode nr = node.right;
						if (nr != null) {
							if (nr.left == null) {
								if (nr.value.connected(e)) {
									node.right = nr.right;
								}
							} else {
								while (nr.left.left != null) {
									nr = nr.left;
								}
								if (nr.left.value.connected(e)) {
									node.value = Interval.connectedUnion(node.value, nr.left.value);
									nr.left = nr.left.right;
								}
							}
						}
					}
				}
			} else if (node.value.isLowerBound(e)) {
				if (node.left == null) {
					node.left = new TreeNode(null, new Interval<E>(root.value.comparator(), e, e,
							Interval.INFIMUM_EX_INCLUDED | Interval.SUPREMUM_EX_INCLUDED), null);
					return;
				} else {
					node = node.left;
				}
			} else {
				if (node.left == null) {
					node.right = new TreeNode(null, new Interval<E>(root.value.comparator(), e, e,
							Interval.INFIMUM_EX_INCLUDED | Interval.SUPREMUM_EX_INCLUDED), null);
					return;
				} else {
					node = node.left;
				}
			}
		}
		// Does not work if root == null / empty.
	}
	
	@Override
	public void clear() {
		root = null;
	}
	
	@Override
	public void union(Interval<E> i) {
		TreeNode node = root;
		if (!i.isEmpty()) {
			if (root == null) {
				root = new TreeNode(null, i, null);
			} else if (i.hasInfimum()) {
				if (i.hasSupremum()) {
					while (node != null) {
						if (node.value.intersects(i)) {
							node.value = Interval.connectedUnion(node.value, i);
							shutInLeft(node, i);
							shutInRight(node, i);
							return;
						} else if (node.value.isLowerBound(i.supremum())) {
							if (node.left == null) {
								node.left = new TreeNode(null, i, null);
								return;
							} else {
								node = node.left;
							}
						} else {
							if (node.right == null) {
								node.right = new TreeNode(null, i, null);
								return;
							} else {
								node = node.right;
							}
						}
					}
				} else {
					while (node != null) {
						if (node.value.intersects(i)) {
							node.value = Interval.connectedUnion(node.value, i);
							node.right = null;
							shutInRight(node, i);
							return;
						} else {
							if (node.right == null) {
								node.right = new TreeNode(null, i, null);
								return;
							} else {
								node = node.right;
							}
						}
					}
				}
			} else if (i.hasSupremum()) {
				while (node != null) {
					if (node.value.intersects(i)) {
						node.value = Interval.connectedUnion(node.value, i);
						node.left = null;
						shutInLeft(node, i);
						return;
					} else {
						if (node.left == null) {
							node.left = new TreeNode(null, i, null);
							return;
						} else {
							node = node.left;
						}
					}
				}
			} else {
				root.left = null;
				root.right = null;
				root.value = i;
			}
		}
	}
	
	private void shutInLeft(TreeNode node, Interval<E> i) {
		TreeNode sub = node;
		while (sub.right != null) {
			if (sub.right.value.intersects(i)) {
				node.value = new Interval<E>(node.value.comparator(), null, sub.right.value.supremum(),
						sub.right.value.config & Interval.SUPREMUM_EX_INCLUDED);
				sub.right = sub.right.right;
			} else {
				TreeNode ls = sub.right;
				while (ls.left != null) {
					if (ls.left.value.intersects(i)) {
						new Interval<E>(node.value.comparator(), null, ls.left.value.supremum(), sub.right.value.config
								& Interval.SUPREMUM_EX_INCLUDED);
						sub = ls.left;
						ls.left = ls.left.right;
						break;
					} else {
						ls = ls.left;
					}
				}
			}
		}
	}
	
	private void shutInRight(TreeNode node, Interval<E> i) {
		TreeNode sub = node;
		while (sub.left != null) {
			if (sub.left.value.intersects(i)) {
				node.value = new Interval<E>(node.value.comparator(), sub.left.value.infimum(), null,
						sub.left.value.config & Interval.INFIMUM_EX_INCLUDED);
				sub.left = sub.left.left;
			} else {
				TreeNode ls = sub.left;
				while (ls.right != null) {
					if (ls.right.value.intersects(i)) {
						new Interval<E>(node.value.comparator(), ls.right.value.infimum(), null, sub.left.value.config
								& Interval.INFIMUM_EX_INCLUDED);
						sub = ls.right;
						ls.right = ls.right.left;
						break;
					} else {
						ls = ls.right;
					}
				}
			}
		}
	}
	
	@Override
	public void remove(E e) {
		if (root.value.hasSupremum() || root.value.hasInfimum()) {
			TreeNode node = root;
			while (node != null) {
				if (node.value.contains(e)) {
					if (node.value.isLowerBound(e)) {
						node.value = new Interval<E>(node.value.comparator(), e, node.value.supremum(),
								node.value.config & ~Interval.INFIMUM_INCLUDED);
					} else if (node.value.isUpperBound(e)) {
						node.value = new Interval<E>(node.value.comparator(), node.value.infimum(), e,
								node.value.config & ~Interval.SUPREMUM_INCLUDED);
					} else {
						if (node.left == null) {
							node.left = new TreeNode(null, new Interval<E>(node.value.comparator(),
									node.value.hasInfimum() ? node.value.infimum() : null, e, node.value.config
											& ~Interval.SUPREMUM_INCLUDED), null);
							node.value = new Interval<E>(node.value.comparator(), e, node.value.hasSupremum()
									? node.value.supremum()
									: null, node.value.config & ~Interval.INFIMUM_INCLUDED);
						} else if (node.right == null) {
							node.right = new TreeNode(null, new Interval<E>(node.value.comparator(), e,
									node.value.hasSupremum() ? node.value.supremum() : null, node.value.config
											& ~Interval.INFIMUM_INCLUDED), null);
							node.value = new Interval<E>(node.value.comparator(), node.value.hasInfimum()
									? node.value.infimum()
									: null, e, node.value.config & ~Interval.SUPREMUM_INCLUDED);
						} else {
							TreeNode n = node.right;
							while (n.left != null) {
								n = n.left;
							}
							n.left = new TreeNode(null, new Interval<E>(node.value.comparator(), e,
									node.value.hasSupremum() ? node.value.supremum() : null, node.value.config
											& ~Interval.INFIMUM_INCLUDED), null);
							node.value = new Interval<E>(node.value.comparator(), node.value.hasInfimum()
									? node.value.infimum()
									: null, e, node.value.config & ~Interval.SUPREMUM_INCLUDED);
						}
					}
				} else if (node.value.isLowerBound(e)) {
					node = node.left;
				} else {
					node = node.right;
				}
			}
		} else {
			root.value = new Interval<E>(root.value.comparator(), null, e, Interval.SUPREMUM_EXISTS);
			root.right = new TreeNode(null, new Interval<E>(root.value.comparator(), e, null, Interval.INFIMUM_EXISTS),
					null);
		}
	}
	
	@Override
	public void remove(Interval<E> i) {
		if (root != null && !i.isEmpty()) {
			if (i.hasInfimum()) {
				if (i.hasSupremum()) {
					
					// TODO
					
				} else {
					TreeNode node = root;
					TreeNode parent = null;
					while (node != null && node.value.intersects(i)) {
						parent = node;
						node = node.right;
					}
					if (node != null) {
						if (isGlobalyLower(i, node.value)) {
							shutOutLeft(node, i);
						} else {
							node.value = nonEmptyUpperRemove(i, node.value);
							node.right = null;
						}
						if (parent == null) {
							root = node.left;
						} else {
							parent.left = node.left;
						}
					}
				}
			} else if (i.hasSupremum()) {
				TreeNode node = root;
				TreeNode parent = null;
				while (node != null && node.value.intersects(i)) {
					parent = node;
					node = node.left;
				}
				if (node != null) {
					if (isGlobalyHigher(i, node.value)) {
						shutOutRight(node, i);
					} else {
						node.value = nonEmptyLowerRemove(i, node.value);
						node.left = null;
					}
					if (parent == null) {
						root = node.right;
					} else {
						parent.left = node.right;
					}
				}
			} else {
				root = null;
			}
		}
	}
	
	private boolean isGlobalyLower(Interval<E> i, Interval<E> than) {
		return i.isInfimumIncluded() || !than.isInfimumIncluded() ? than.isLowerBound(i.infimum()) : than
				.isStrictLowerBound(i.infimum());
	}
	
	private boolean isGlobalyHigher(Interval<E> i, Interval<E> than) {
		return i.isSupremumIncluded() || !than.isSupremumIncluded() ? than.isUpperBound(i.supremum()) : than
				.isStrictUpperBound(i.supremum());
	}
	
	private Interval<E> nonEmptyLowerRemove(Interval<E> i, Interval<E> from) {
		return new Interval<E>(from.comparator(), i.supremum(), from.supremum(),
				(Interval.INFIMUM_INCLUDED & (~i.config >> 1)) | Interval.INFIMUM_EXISTS
						| (from.config & Interval.SUPREMUM_EX_INCLUDED));
	}
	
	private Interval<E> nonEmptyUpperRemove(Interval<E> i, Interval<E> from) {
		return new Interval<E>(from.comparator(), from.infimum(), i.infimum(),
				(Interval.SUPREMUM_INCLUDED & (~i.config << 1)) | Interval.SUPREMUM_EXISTS
						| (from.config & Interval.INFIMUM_EX_INCLUDED));
	}
	
	/**
	 * Removes any Intervals to the left of node, that are contained i and
	 * removes i from any Interval intersecting i. Asserts that node itself
	 * intersects i.
	 * 
	 * @param node
	 *            A node intersecting i.
	 * @param i
	 *            An {@link Interval} to remove.
	 */
	private void shutOutLeft(TreeNode node, Interval<E> i) {
		while (node.left != null && node.left.value.intersects(i)) {
			if (isGlobalyLower(i, node.left.value)) {
				node.left = node.left.left;
			} else {
				node.left.right = null;
				node.left.value = nonEmptyUpperRemove(i, node.left.value);
				return;
			}
		}
		if (node.left != null) {
			node = node.left;
			while (node.right != null) {
				if (node.right.value.intersects(i)) {
					if (isGlobalyLower(i, node.right.value)) {
						node.right = node.right.left;
					} else {
						node.right.right = null;
						node.right.value = nonEmptyUpperRemove(i, node.right.value);
						return;
					}
				} else {
					node = node.right;
				}
			}
		}
	}
	
	/**
	 * Removes any Intervals to the right of node, that are contained i and
	 * removes i from any Interval intersecting i. Asserts that node itself
	 * intersects i.
	 * 
	 * @param node
	 *            A node intersecting i.
	 * @param i
	 *            An {@link Interval} to remove.
	 */
	private void shutOutRight(TreeNode node, Interval<E> i) {
		while (node.right != null && node.right.value.intersects(i)) {
			if (isGlobalyHigher(i, node.right.value)) {
				node.right = node.right.right;
			} else {
				node.right.left = null;
				node.right.value = nonEmptyLowerRemove(i, node.right.value);
				return;
			}
		}
		if (node.right != null) {
			node = node.right;
			while (node.left != null) {
				if (node.left.value.intersects(i)) {
					if (isGlobalyHigher(i, node.left.value)) {
						node.left = node.left.right;
					} else {
						node.left.left = null;
						node.left.value = nonEmptyLowerRemove(i, node.left.value);
						return;
					}
				} else {
					node = node.left;
				}
			}
		}
	}
	
	private class TreeNode {
		protected TreeNode left;
		protected Interval<E> value;
		protected TreeNode right;
		
		public TreeNode(TreeNode left, Interval<E> middle, TreeNode right) {
			this.left = left;
			this.value = middle;
			this.right = right;
		}
		
		public void addToSet(Set<E> set) {
			set.addAll(value.toSet());
			if (left != null) {
				left.addToSet(set);
			}
			if (right != null) {
				right.addToSet(set);
			}
		}
		
		public boolean isSet() {
			return value.isSet() && (left == null || left.isSet()) && (right == null || right.isSet());
		}
	}
	
	private class TreeIterator implements Iterator<Interval<E>> { // TODO test
		/**
		 * path is a list of TreeNodes. It goes from the root to the last node
		 * that was returned. TreeNodes that are left-parents in the path are
		 * skipped. null can be appended, if it has already been calculated and
		 * removing is forbidden.
		 */
		private Stack<TreeNode> path;
		
		public TreeIterator() {
			path = new Stack<TreeNode>();
			TreeNode node = root;
			while (node != null) {
				path.push(node);
				node = node.left;
			}
			if (!path.isEmpty()) {
				path.push(null);
			}
		}
		
		@Override
		public boolean hasNext() {
			if (path.isEmpty()) {
				return false;
			} else {
				TreeNode last = path.peek();
				if (path.size() > 1 || last == null || last.right != null) {
					return true;
				}
				return false;
			}
		}
		
		@Override
		public Interval<E> next() {
			if (path.empty()) {
				throw new NoSuchElementException();
			} else {
				TreeNode node = path.peek();
				if (node == null) {
					path.pop();
					return path.peek().value;
				} else {
					if (node.right == null) {
						path.pop();
						return path.peek().value;
					} else {
						node = node.right;
						path.pop();
						path.push(node);
						while (node.left != null) {
							node = node.left;
							path.push(node);
						}
						return node.value;
					}
				}
			}
		}
		
		@Override
		public void remove() {
			if (path.isEmpty()) {
				throw new IllegalStateException();
			}
			TreeNode removing = path.pop();
			if (removing == null) {
				path.push(null);
				throw new IllegalStateException();
			} else {
				TreeNode subroot = path.peek();
				if (subroot.left == removing) {
					if (removing.right == null) {
						subroot.left = removing.left;
						path.push(null);
					} else {
						subroot.left = removing.left;
						subroot = subroot.left;
						while (subroot.right != null) {
							subroot = subroot.right;
						}
						subroot.right = removing.right;
						subroot = subroot.right;
						path.push(subroot);
						while (subroot.left != null) {
							subroot = subroot.left;
							path.push(subroot);
						}
						path.push(null);
					}
				} else {
					if (removing.left == null) {
						subroot.right = removing.right;
						if (subroot.right != null) {
							path.push(subroot.right);
						}
						path.push(null);
					} else {
						subroot.right = removing.left;
						if (removing.right != null) {
							while (subroot.right != null) {
								subroot = subroot.right;
							}
							subroot.right = removing.right;
							subroot = subroot.right;
							path.push(subroot);
							while (subroot.left != null) {
								subroot = subroot.left;
								path.push(subroot);
							}
						}
						path.push(null);
					}
				}
			}
		}
	}
}
