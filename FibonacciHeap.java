/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap {

	private static final double phi = 1.62;

	private HeapNode minimum = null;
	public HeapNode firstRoot = null;
	private int HeapSize = 0;

	private static int linkActions = 0;
	private static int cutActions = 0;
	public int numOfMarked = 0;
	public int numOfTrees = 0;

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
		return node; // should be replaced by student code
	}

	/**
	 * public void deleteMin()
	 *
	 * Delete the node containing the minimum key.
	 *
	 */
	public void deleteMin() {
		if (this.HeapSize <= 1) { // the heap only got one node - and its the min node or the heap is empty
			//System.out.println("if 1 (delete min)");
			this.firstRoot = null;
			this.minimum = null;
			HeapSize = 0;
			numOfTrees = 0;
			return;
		}
		HeapNode minNode = this.findMin();
		HeapNode prevTomin = minNode.prev;
		HeapNode nextTomin = minNode.next;
		HeapNode minChild = minNode.child;
		//System.out.println("min : "+ minNode + "/n min child:" + minChild + "/n prev:" + prevTomin
		//		+ " /n next :" + nextTomin);
		if (minChild == null) { // min got no childs but for sure have siblings because of first if
			//System.out.println("if 2 (delete min)");
			if(minNode == this.firstRoot) {
				this.firstRoot = minNode.next;
			}
			prevTomin.next = nextTomin;
			nextTomin.prev = prevTomin;
		}
		else if (prevTomin == minNode) { // Min is the only root in the heap but for sure got childs
			//System.out.println("if 3 (delete min)");
			
			this.firstRoot = minNode.child;
			minChild.setParent(null);
			//System.out.print("minimim's children: ")
			//minChild.printlevel();
			
			HeapNode currNode = minChild.next;
			//System.out.println("curr: " +currNode);
			//wrong while && missing algorithem
			while (currNode != minChild) { // setting all minchild's parent to be null
				currNode.setParent(null);
				currNode = currNode.next;
			}
		}
		else { // min got childs and siblings
			//System.out.println("else (delete min)");
			if (firstRoot.equals(minimum)) {
				firstRoot = minimum.getnext();
			}
			minNode.deleteFromSiblings();
			minChild.setParent(null);
			HeapNode currNode = minChild.next;
			while (currNode != minChild) { // running on all min child's seting parent to null and updating last
												// one's next
				currNode.setParent(null);
				currNode = currNode.next;
			}
			
			nextTomin.mergeSiblings(minChild);
			

		} // from here the heap is ready for the bucket
		
		//System.out.println("start linking");
		this.SuccessiveLinking();
		//System.out.println("relocating min");
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
		//firstRoot.printlevel();
		//System.out.print("bucketing ->");

		HeapNode[] fixedRoots = bucketTheTrees();
		//System.out.print("Un-bucketing ->");
		unBucket(fixedRoots);
		//System.out.println("unbucketed");
	}

	private HeapNode[] bucketTheTrees() {
		int b = 1+(int) (Math.log(HeapSize) / Math.log(phi)); // log_p (size)
		//System.out.println("size:" + b);
		HeapNode[] buckets = new HeapNode[b];
		HeapNode current = firstRoot;
		//System.out.println("current:" + current +"prev:" + current.prev);
		current.prev.next = null; // disconnect link
		while (current != null) {	
			HeapNode node = current;
			//current.deleteFromSiblings();
			//System.out.println(current);
			int rank = node.rank;
			current = current.getnext();
			//System.out.println(node + "->" + current);
			while (buckets[rank] != null) {
				//System.out.println("unsuccesfull put of: " + node + "in bucket:" + rank);
				node = link(node, buckets[rank]);
				buckets[rank] = null;
				rank++;
			}
			buckets[rank] = node;
			//System.out.println("put: " + node + " in bucket:" + rank);
		}
		//System.out.println("\nbuckets: " + Arrays.toString(buckets));
		return buckets;
	}

	private void unBucket(HeapNode[] buckets) {
		int newTreeCount =0;
		firstRoot = null;
		for (HeapNode root : buckets) {
			if (root != null) {
				newTreeCount++;
				if (firstRoot == null) {
					firstRoot = root;
					root.next = root;
					root.prev = root;
				} else {
					firstRoot.addlast(root);

				}
			}
		}
		numOfTrees = newTreeCount;
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
		return minimum;// should be replaced by student code
	}

	public HeapNode locateMin() {
		HeapNode first = firstRoot;
		HeapNode current = firstRoot.getnext();
		HeapNode min = first;
		while (!current.equals(first)) {
			
			if (min.getKey() > current.getKey()) {
				min = current;
			}
			current = current.getnext();
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
		updateHeapInfo(heap2);


		

	}

	private void updateHeapInfo(FibonacciHeap heap2) {
		HeapNode oldMin = this.minimum;
		HeapNode optinalMin = heap2.minimum;
		if (optinalMin.getKey() < oldMin.getKey()) {
			this.minimum = optinalMin;
		}
		HeapSize += heap2.HeapSize;
		numOfTrees += heap2.numOfTrees;
		numOfMarked += heap2.numOfMarked;
	
		
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
		//System.out.println(x.getParent());;
		if (parent !=null && x.getKey() < parent.getKey()) {
			cascadingCut(x, parent);
			if (x.getKey() < this.findMin().getKey()) {
				this.minimum = x;
			}
		}
		if (x.getKey() < minimum.getKey()) {
			minimum = x;
		}
	}

	private void cascadingCut(HeapNode node, HeapNode parent) {
		//System.out.println("call cut");
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
		//System.out.println("increase cut");
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
		int[] arr = new int[42];
		return arr; // should be replaced by student code
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
		private HeapNode parent = null;
		private HeapNode child = null;
		private HeapNode prev = this;
		private HeapNode next = this;

		private int rank = 0;
		private boolean mark = false;

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
			HeapNode oldLast = this.prev;
			this.prev = node;
			node.next = this;
			oldLast.next = node;
			node.prev = oldLast;

		}

		protected void mergeSiblings(HeapNode head2) {
			HeapNode head1 = this;
			HeapNode tail1 = this.prev;
			HeapNode tail2 = head2.prev;
			
			head1.prev = tail2;
			tail2.next = head1;
			
			head2.prev = tail1;
			tail1.next = head2;
		}
		
		/**
		 * add newNode as the first child of the node
		 */
		protected void insertChild(HeapNode newNode) {
			if (this.child == null) {
				newNode.parent = this;
				this.child = newNode;
				newNode.next = newNode;
				newNode.prev = newNode;
			}
			else {
				HeapNode last = child.prev;
				newNode.parent = this;

				newNode.next = child;
				newNode.prev = last;
				child.prev = newNode;
				last.next = newNode;
				this.child = newNode;
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
			if (getParent()!= null && getParent().getchild().equals(this)) {
				getParent().child = this.getnext();
			}

			HeapNode succesor = getnext();
			HeapNode predessecor = getprev();

			succesor.prev = predessecor;
			predessecor.next = succesor;
			
			this.next = null;
			this.prev = null;

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
			System.out.print(current);
			while (!current.next.equals(this)) {
				current = current.next;
				System.out.print("->" + current);
			}
			System.out.print("\n");
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
		
		public boolean isNotEq(HeapNode other) {
			return key!=other.getKey();
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
}
