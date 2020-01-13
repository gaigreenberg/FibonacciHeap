import java.util.Arrays;

public class etc {
	
	public static void main(String[] args) {
		FibonacciHeap heap1 = new FibonacciHeap();
		FibonacciHeap heap2 = new FibonacciHeap();

		FibonacciHeap.HeapNode node= null;
		FibonacciHeap.HeapNode node3= null;

		for (int i=-1;i<21;i++) {
			if (i==15) {
				node = heap1.insert(i);
			}
			else {
				heap1.insert(i);
			}
		}
		heap1.deleteMin();
		heap1.delete(node);
		printHeapDetails(heap1,1);

		
		for (int i=99;i<200;i++) {
			if (i==140) {
				node3 = heap2.insert(i);
			}
			else {
				heap2.insert(i);
			}		
		}
		FibonacciHeap.HeapNode node4 = heap2.insert(-1);
		FibonacciHeap.HeapNode node5 = heap2.insert(500);

		heap2.deleteMin();
		heap2.deleteMin();
		heap2.decreaseKey(node3, 200);
		heap2.decreaseKey(node4, 20);
		heap2.decreaseKey(node5, 1000);

		
		
		
		printHeapDetails(heap2,2);
		heap1.meld(heap2);
		System.out.println("\n~~   melding   ~~\n");
		
		printHeapDetails(heap1,3);
		//heap.delete(node);
		//printHeapDetails(heap);


		
	}
	
	
	private static void printHeapDetails(FibonacciHeap heap) {
		System.out.println("~~ heap info ~~");
		System.out.println("size:          " + heap.size());
		System.out.println("minimum node:  " + heap.findMin());
		System.out.println("first root:    " + heap.firstRoot);
		System.out.println("num of trees:  " + heap.numOfTrees);
		System.out.println("num of marked: " + heap.numOfMarked);
		System.out.println("trees count:   " + Arrays.toString(heap.countersRep()));
		System.out.println("potential:     " + heap.potential());
		System.out.println("total links:  " + heap.totalLinks());
		System.out.println("total cuts:   " + heap.totalCuts());
		System.out.println("~~~~~~~~~~~~~~~~~~");

	}

	private static void printHeapDetails(FibonacciHeap heap,int num) {
		System.out.println("~~ heap"+num+" info ~~");
		System.out.println("size:          " + heap.size());
		System.out.println("minimum node:  " + heap.findMin());
		System.out.println("first root:    " + heap.firstRoot);
		System.out.println("num of trees:  " + heap.numOfTrees);
		System.out.println("num of marked: " + heap.numOfMarked);
		System.out.println("trees count:   " + Arrays.toString(heap.countersRep()));
		System.out.println("potential:     " + heap.potential());
		System.out.println("total links:  " + heap.totalLinks());
		System.out.println("total cuts:   " + heap.totalCuts());
		System.out.println("~~~~~~~~~~~~~~~~~~");
	}
}
