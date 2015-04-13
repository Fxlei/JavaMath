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
		Iterator<Interval<E>> i1 = r.toSeparateIntervalSet().iterateIntervals();
		Iterator<Interval<E>> i2 = this.iterateIntervals();
		Iterator<Interval<E>> i = null;
		while (i.hasNext()) {
			
		}
		return false;
	}
	
	public Iterator<Interval<E>> iterateIntervals() {
		return new TreeIterator();
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
	
	private class TreeIterator implements Iterator<Interval<E>> {
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
						path.push(subroot.right);
						path.push(null);
					}
				} else {
					if (removing.left == null) {
						subroot.right = removing.right;
						path.push(null);
					} else {// TODO
						
					}
				}
			}
		}
	}
}
