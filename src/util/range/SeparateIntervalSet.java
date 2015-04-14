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
			if (root.middle.contains(e)) {
				return true;
			} else if (node.middle.isLowerBound(e)) {
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
			if (root.middle.intersects(i)) {
				return true;
			} else if (node.middle.isLowerBound(i.supremum())) {
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
			return node.middle.hasInfimum();
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
			return node.middle.infimum();
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
			return node.middle.isInfimumIncluded();
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
			return node.middle.hasSupremum();
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
			return node.middle.supremum();
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
			return node.middle.isSupremumIncluded();
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
			return node.middle.isLowerBound(e);
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
			return node.middle.isStrictLowerBound(e);
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
			return node.middle.isUpperBound(e);
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
			return node.middle.isStrictUpperBound(e);
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
			if (node.middle.connected(e)) {
				// TODO
				
				if (node.middle.compare(node.middle.infimum(), e) == 0) {
					if (!node.middle.isInfimumIncluded()) {
						node.middle = new Interval<E>(node.middle.comparator(), e, node.middle.supremum(),
								node.middle.config | Interval.INFIMUM_EX_INCLUDED);
						
						TreeNode nl = node.left;
						if (nl != null) {
							if (nl.right == null) {
								if (nl.middle.connected(e)) {
									node.right = nl.left;
								}
							} else {
								while (nl.right.right != null) {
									nl = nl.left;
								}
								if (nl.right.middle.connected(e)) {
									node.middle = Interval.connectedUnion(node.middle, nl.right.middle);
									nl.right = nl.right.left;
								}
							}
						}
					}
				} else if (node.middle.compare(node.middle.supremum(), e) == 0) {
					if (!node.middle.isSupremumIncluded()) {
						node.middle = new Interval<E>(node.middle.comparator(), node.middle.infimum(), e,
								node.middle.config | Interval.SUPREMUM_EX_INCLUDED);
						
						TreeNode nr = node.right;
						if (nr != null) {
							if (nr.left == null) {
								if (nr.middle.connected(e)) {
									node.right = nr.right;
								}
							} else {
								while (nr.left.left != null) {
									nr = nr.left;
								}
								if (nr.left.middle.connected(e)) {
									node.middle = Interval.connectedUnion(node.middle, nr.left.middle);
									nr.left = nr.left.right;
								}
							}
						}
					}
				}
				
			} else if (node.middle.isLowerBound(e)) {
				if (node.left == null) {
					node.left = new TreeNode(null, new Interval<E>(root.middle.comparator(), e, e,
							Interval.INFIMUM_EX_INCLUDED | Interval.SUPREMUM_EX_INCLUDED), null);
					return;
				} else {
					node = node.left;
				}
			} else {
				if (node.left == null) {
					node.right = new TreeNode(null, new Interval<E>(root.middle.comparator(), e, e,
							Interval.INFIMUM_EX_INCLUDED | Interval.SUPREMUM_EX_INCLUDED), null);
					return;
				} else {
					node = node.left;
				}
			}
		}
	}
	
	private class TreeNode {
		protected TreeNode left;
		protected Interval<E> middle;
		protected TreeNode right;
		
		public TreeNode(TreeNode left, Interval<E> middle, TreeNode right) {
			this.left = left;
			this.middle = middle;
			this.right = right;
		}
		
		public void addToSet(Set<E> set) {
			set.addAll(middle.toSet());
			if (left != null) {
				left.addToSet(set);
			}
			if (right != null) {
				right.addToSet(set);
			}
		}
		
		public boolean isSet() {
			return middle.isSet() && (left == null || left.isSet()) && (right == null || right.isSet());
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
					return path.peek().middle;
				} else {
					if (node.right == null) {
						path.pop();
						return path.peek().middle;
					} else {
						node = node.right;
						path.pop();
						path.push(node);
						while (node.left != null) {
							node = node.left;
							path.push(node);
						}
						return node.middle;
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
