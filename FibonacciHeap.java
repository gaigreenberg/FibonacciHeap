/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap {

	private static final double phi = 1.62;

	private HeapNode minimum = null;
	private HeapNode firstRoot = null;
	private int HeapSize = 0;

	private static int linkActions = 0;
	private static int cutActions = 0;
	private int numOfMarked = 0;
	private int numOfTrees = 0;

	public FibonacciHeap() {

	}

	/**
	 * public boolean isEmpty()
	 *
	 * precondition: none
	 * 
	 * The method returns true if and only if the heap is empty.
	 * 
	 */
	public boolean isEmpty() {
		return (HeapSize == 0); // should be replaced by student code
	}

	/**
	 * public HeapNode insert(int key)
	 *
	 * Creates a node (of type HeapNode) which contains the given key, and inserts
	 * it into the heap.
	 */
	public HeapNode insert(int key) {
		HeapNode node = new HeapNode(key);
		if (firstRoot == null) {
			firstRoot = node;
			minimum = node;
		} else {
			if (node.key < minimum.key) {
				minimum = node;
			}
			firstRoot.addSibling(node);
			firstRoot = node;
		}
		HeapSize++;
		numOfTrees++;
		return node;
	}

	public void insert(HeapNode node) { // special insert for the KMin method
		if (firstRoot == null) {
			firstRoot = node;
			minimum = node;
		} else {
			if (node.key < minimum.key) {
				minimum = node;
			}
			firstRoot.addSibling(node);
			firstRoot = node;
		}
		HeapSize++;
		numOfTrees++;

	}

	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 */
	public void deleteMin() {
		if (this.HeapSize <= 1) { // the heap only got one node - and its the min node or the heap is empty
			this.firstRoot = null;
			this.minimum = null;
		}
		HeapNode minNode = this.findMin();
		HeapNode prevTomin = minNode.prev;
		HeapNode nextTomin = minNode.next;
		HeapNode minChild = minNode.child;
		if (minChild == null) { // min got no childs but for sure have siblings because of first if
			prevTomin.next = nextTomin;
			nextTomin.prev = prevTomin;
		} else if (prevTomin == minNode) { // Min is the only root in the heap but for sure got childs
			this.firstRoot = minNode.child;
			minChild.setParent(null);
			HeapNode currNode = minChild.next;
			while (currNode != minChild) { // setting all minchild's parent to be null
				currNode.setParent(null);
				currNode = currNode.next;
			}
		} else { // min got childs and siblings
			prevTomin.next = minChild;
			minChild.setParent(null);
			HeapNode currNode = minChild.next;
			while (currNode.next != minChild) { // running on all min child's seting parent to null and updating last
												// one's next
				currNode.setParent(null);
				currNode = currNode.next;
			}
			currNode.setParent(null);
			currNode.next = nextTomin;

		} // from here the heap is ready for the bucket
		this.SuccessiveLinking();
		this.minimum = this.locateMin();

		/**
		 * remove the tree with min node from the heap delete the node and create a 2nd
		 * heap with it's children as roots merge the heaps fix the heap -> merge trees
		 * of same rank update min: minimum = locateMin()
		 */
		if (this.HeapSize > 0) {
			HeapSize--;
		}

	}

	private void SuccessiveLinking() {
		HeapNode[] fixedRoots = bucketTheTrees();
		unBucket(fixedRoots);
	}

	private HeapNode[] bucketTheTrees() {
		int b = (int) (Math.log(HeapSize) / Math.log(phi)); // log_p (size)
		HeapNode[] buckets = new HeapNode[b];
		HeapNode current = firstRoot;
		current.prev.next = null; // disconnect link

		while (current != null) {
			HeapNode node = current;
			int rank = node.rank;
			current = current.getnext();
			while (buckets[rank] != null) {
				node = link(node, buckets[rank]);
				rank++;
				buckets[rank] = null;
			}
			buckets[rank] = node;
		}
		return buckets;
	}

	private void unBucket(HeapNode[] buckets) {
		firstRoot = null;
		for (HeapNode root : buckets) {
			if (root != null) {
				if (firstRoot == null) {
					firstRoot = root;
					root.next = root;
					root.prev = root;
				} else {
					firstRoot.addlast(root);

				}
			}
		}

	}

	private HeapNode link(HeapNode node1, HeapNode node2) {
		linkActions++;
		int k1 = node1.getKey();
		int k2 = node2.getKey();
		if (k1 < k2) {
			node1.insertChild(node2);
			return node1;
		}
		node2.insertChild(node1);
		return node2;

	}

	/**
	 * public HeapNode findMin()
	 *
	 * Return the node of the heap whose key is minimal.
	 *
	 */
	public HeapNode findMin() {
		return minimum;
	}

	public HeapNode locateMin() {
		HeapNode current = firstRoot.getnext();
		HeapNode min = firstRoot;
		while (!current.equals(firstRoot)) {
			if (min.getKey() < current.getKey()) {
				min = current;
			}
		}
		return min;
	}

	/**
	 * public void meld (FibonacciHeap heap2)
	 *
	 * Meld the heap with heap2
	 *
	 */
	public void meld(FibonacciHeap heap2) {

		HeapNode oldLastRoot = firstRoot.prev;
		HeapNode first = firstRoot;
		HeapNode otherFirst = heap2.firstRoot;
		HeapNode last = heap2.firstRoot.prev;

		oldLastRoot.next = otherFirst;
		otherFirst.prev = oldLastRoot;
		first.prev = last;
		last.next = first;
		HeapSize += heap2.HeapSize;

		if (heap2.findMin().getKey() < this.findMin().getKey()) {
			this.minimum = heap2.findMin();
		}

	}

	/**
	 * public int size() Return the number of elements in the heap
	 */
	public int size() {
		return HeapSize;
	}

	/**
	 * public int[] countersRep()
	 *
	 * Return a counters array, where the value of the i-th entry is the number of
	 * trees of order i in the heap.
	 * 
	 */
	public int[] countersRep() {
		if (this.isEmpty()) { // return empty arr - forum
			int[] emptyArr = new int[0];
			return emptyArr;
		}
		int size = (int) (Math.log(HeapSize) / Math.log(phi)); // log_p (size)
		int[] arr = new int[size];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = 0;
		}
		arr[this.firstRoot.rank]++;
		HeapNode currNode = this.firstRoot.next;
		while (currNode != this.firstRoot) {
			arr[currNode.rank]++;
			currNode = currNode.next;
		}
		return arr;
	}

	/**
	 * public void delete(HeapNode x)
	 *
	 * Deletes the node x from the heap.
	 *
	 */
	public void delete(HeapNode x) {
		int delta = (x.getKey() - minimum.getKey()) + 1993; // new key will be minimum
		decreaseKey(x, delta);
		deleteMin();

	}

	/**
	 * public void decreaseKey(HeapNode x, int delta)
	 *
	 * The function decreases the key of the node x by delta. The structure of the
	 * heap should be updated to reflect this chage (for example, the cascading cuts
	 * procedure should be applied if needed).
	 */
	public void decreaseKey(HeapNode x, int delta) {
		HeapNode parent = x.getParent();
		x.decreaseBy(delta);

		if (x.getKey() < parent.getKey()) {
			cascadingCut(x, parent);
			if (x.getKey() < this.findMin().getKey()) {
				this.minimum = x;
			}
		}
	}

	private void cascadingCut(HeapNode node, HeapNode parent) {
		cut(node, parent);
		if (parent.getParent() != null) {
			if (parent.isMarked()) {
				cascadingCut(parent, parent.getParent());
			} else {
				parent.mark();
				numOfMarked++;
			}
		}

	}

	private void cut(HeapNode node, HeapNode parent) {

		node.setParent(null);

		if (node.isMarked()) {
			node.unMark();
			numOfMarked--;
		}

		parent.rank--;
		if (node.isLonelyChild()) {
			parent.child = null;
		} else {
			node.deleteFromSiblings();
		}
		putRoot(node);
		cutActions++;

	}

	private void putRoot(HeapNode node) { // adding the cutted tree to the heap
		firstRoot.addSibling(node);
		firstRoot = node;
		numOfTrees++;
	}

	/**
	 * public int potential()
	 *
	 * This function returns the current potential of the heap, which is: Potential
	 * = #trees + 2*#marked The potential equals to the number of trees in the heap
	 * plus twice the number of marked nodes in the heap.
	 */
	public int potential() {
		return 2 * numOfMarked + numOfTrees; // should be replaced by student code
	}

	/**
	 * public static int totalLinks()
	 *
	 * This static function returns the total number of link operations made during
	 * the run-time of the program. A link operation is the operation which gets as
	 * input two trees of the same rank, and generates a tree of rank bigger by one,
	 * by hanging the tree which has larger value in its root on the tree which has
	 * smaller value in its root.
	 */
	public static int totalLinks() {
		return linkActions; // should be replaced by student code
	}

	/**
	 * public static int totalCuts()
	 *
	 * This static function returns the total number of cut operations made during
	 * the run-time of the program. A cut operation is the operation which
	 * diconnects a subtree from its parent (during decreaseKey/delete methods).
	 */
	public static int totalCuts() {
		return cutActions; // should be replaced by student code
	}

	/**
	 * public static int[] kMin(FibonacciHeap H, int k)
	 *
	 * This static function returns the k minimal elements in a binomial tree H. The
	 * function should run in O(k(logk + deg(H)).
	 */
	public static int[] kMin(FibonacciHeap H, int k) {
		int[] arr = new int[k];
		FibonacciHeap kFibo = new FibonacciHeap();
		HeapNode hMin = H.findMin();
		KNode minNode = H.new KNode(hMin);
		kFibo.insert(minNode);
		for (int i = 0; i < arr.length; i++) { // running k times inserting the Min node from the new fibo tree to the
												// arr
			minNode = (KNode) kFibo.findMin();
			kFibo.enterSons(minNode);
			arr[i] = minNode.getKey();
			kFibo.deleteMin();

		}
		return arr; // should be replaced by student code
	}

	public void enterSons(KNode x) {
		HeapNode currChild = x.child;
		this.insert(currChild);
		currChild = currChild.next;
		while (currChild != x.child) {
			this.insert(currChild);
		}
	}

	/**
	 * public class HeapNode
	 * 
	 * If you wish to implement classes other than FibonacciHeap (for example
	 * HeapNode), do it in this file, not in another file
	 * 
	 */
	public class HeapNode {

		public int key;
		protected HeapNode parent = null;
		protected HeapNode child = null;
		protected HeapNode prev = this;
		protected HeapNode next = this;

		protected int rank = 0;
		protected boolean mark = false;

		public HeapNode(int key) {
			this.key = key;
		}

		// getters

		/**
		 * 
		 * @return key of node
		 */
		public int getKey() {
			return key;
		}

		/**
		 * return the number of children of this node
		 */
		public int getrank() {
			return rank;
		}

		/**
		 * return the next sibling
		 */
		public HeapNode getnext() {
			return next;
		}

		/**
		 * return the prev sibling
		 */
		public HeapNode getprev() {
			return prev;
		}

		/**
		 * return the parent of this node
		 */
		public HeapNode getParent() {
			return parent;
		}

		/**
		 * return the first child of this node
		 */
		public HeapNode getchild() {
			return child;
		}

		/**
		 * return true if the node is marked (not a root && one child has been deleted)
		 */
		public boolean isMarked() {
			return mark;
		}

		// setters & modifiers

		/**
		 * mark the node in cut method
		 */
		protected void mark() {
			mark = true;
		}

		/**
		 * un-mark the node if became a root
		 */
		protected void unMark() {
			mark = false;
		}

		/**
		 * set the parent of this node to be adopter
		 */
		protected void setParent(HeapNode adopter) {
			parent = adopter;
		}

		/**
		 * set rank to actual number of children (children.size()) add child / remove
		 * child is updating!
		 */
		protected void updateRank() {
			if (child == null) {
				rank = 0;
			}
			int r = 1;
			HeapNode current = child.next;
			while (!(current.equals(child))) {
				r++;
			}
			rank = r;
		}

		/**
		 * set sib as the first node in the same level of this node
		 */
		protected void addSibling(HeapNode sibling) {

			HeapNode last = prev;
			prev = sibling;
			sibling.next = this;
			last.next = sibling;
			sibling.prev = last;
		}

		/**
		 * set sib as the last node in the same level of this node
		 */
		protected void addlast(HeapNode node) {
			HeapNode oldLast = prev;
			this.prev = node;
			node.next = this;
			oldLast.next = node;
			node.prev = oldLast;

		}

		/**
		 * add newNode as the first child of the node
		 */
		protected void insertChild(HeapNode newNode) {
			if (child == null) {
				child = newNode;
			} else {
				HeapNode last = child.prev;
				newNode.parent = this;

				newNode.next = child;
				newNode.prev = last;
				child.prev = newNode;
				last.next = newNode;
			}

			rank++;

		}

		/**
		 * remove childNode from the children of the node
		 */
		protected void deleteChild(HeapNode childNode) {

			if (getchild().equals(childNode)) {
				child = child.next;

			}
			HeapNode left = childNode.prev;
			HeapNode right = childNode.next;

			right.prev = left;
			left.next = right;
		}

		/**
		 * remove sibling from the the nodes chain of same level
		 */
		protected void deleteFromSiblings() {
			if (getParent().getchild().equals(this)) {
				getParent().child = getnext();
			}

			HeapNode succesor = getnext();
			HeapNode predessecor = getprev();

			succesor.prev = predessecor;
			predessecor.next = succesor;

		}

		/**
		 * Decrease node.key key = @prev(key) - delta
		 */
		protected void decreaseBy(int delta) {
			this.key = key - delta;
		}

		// other funcs
		public void printlevel() {
			HeapNode current = this;
			System.out.println(current);
			while (!current.next.equals(this)) {
				current = current.next;
				System.out.println("->" + current);
			}
		}

		@Override
		public String toString() {
			return "{" + key + "}";
		}

		protected boolean isLonelyChild() {
			return (this.next == this);

		}

		public boolean equals(HeapNode other) {

			if (this.key != other.key)
				return false;
			return true;
		}

		protected HeapNode findMinSibling() {
			HeapNode current = getnext();
			HeapNode min = this;
			while (!(current.equals(this))) {
				if (current.getKey() < getKey()) {
					min = current;
				}
			}
			return min;
		}
	}

	public class KNode extends HeapNode {
		public HeapNode originNode;

		public KNode(HeapNode x) {
			super(x.key);
			this.originNode = x;
			this.child = x.child;
			this.prev = x.prev;
			this.next = x.next;

		}

	}
}
