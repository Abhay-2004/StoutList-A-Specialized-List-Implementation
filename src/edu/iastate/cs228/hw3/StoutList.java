
package edu.iastate.cs228.hw3;

import java.util.AbstractSequentialList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * @author Abhay Prasanna Rao
 */

/**
 * Implementation of the list interface based on linked nodes that store
 * multiple items per node. Rules for adding and removing elements ensure that
 * each node (except possibly the last one) is at least half full.
 */
public class StoutList<E extends Comparable<? super E>> extends AbstractSequentialList<E> {
	/**
	 * Default number of elements that may be stored in each node.
	 */
	private static final int DEFAULT_NODESIZE = 4;

	/**
	 * Number of elements that can be stored in each node.
	 */
	private final int nodeSize;

	/**
	 * Dummy node for head. It should be private but set to public here only for
	 * grading purpose. In practice, you should always make the head of a linked
	 * list a private instance variable.
	 */
	public Node head;

	/**
	 * Dummy node for tail.
	 */
	private Node tail;

	/**
	 * Number of elements in the list.
	 */
	private int size;

	/**
	 * Constructs an empty list with the default node size.
	 */
	public StoutList() {
		this(DEFAULT_NODESIZE);
	}

	/**
	 * Constructs an empty list with the given node size.
	 * 
	 * @param nodeSize number of elements that may be stored in each node, must be
	 *                 an even number
	 */
	public StoutList(int nodeSize) {
		if (nodeSize <= 0 || nodeSize % 2 != 0)
			throw new IllegalArgumentException();

		// dummy nodes
		head = new Node();
		tail = new Node();
		head.next = tail;
		tail.previous = head;
		this.nodeSize = nodeSize;
	}

	/**
	 * Constructor for grading only. Fully implemented.
	 * 
	 * @param head
	 * @param tail
	 * @param nodeSize
	 * @param size
	 */
	public StoutList(Node head, Node tail, int nodeSize, int size) {
		this.head = head;
		this.tail = tail;
		this.nodeSize = nodeSize;
		this.size = size;
	}

	/**
	 * Returns the number of elements in this list.
	 * 
	 * @return the number of elements in this list
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Adds a specified item to the list.
	 * 
	 * @param item the item to be added
	 * @return true if the item was successfully added, false otherwise
	 * @throws NullPointerException if the provided item is null
	 */

	@Override
	public boolean add(E item) {
		if (item == null) {
			throw new NullPointerException();
		}

		if (size >= 1) {
			Node temp = head.next;
			while (temp != tail) {
				for (int i = 0; i < temp.count; i++) {
					if (temp.data[i].equals(item))
						return false;
					temp = temp.next;
				}
			}
		}

		if (size == 0) {
			Node n = new Node();
			n.addItem(item);
			head.next = n;
			n.previous = head;
			n.next = tail;
			tail.previous = n;
		} else {

			if (tail.previous.count < nodeSize) {
				tail.previous.addItem(item);
			}

			else {
				Node n = new Node();
				n.addItem(item);
				Node temp = tail.previous;
				temp.next = n;
				n.previous = temp;
				n.next = tail;
				tail.previous = n;
			}
		}
		// increase the size of list, since item has been added
		size++;
		return true;
	}

	/**
	 * A helper class to represent a specific point in the list.
	 */
	private class Node_Data {

		/**
		 * The node at the specific point.
		 */
		public Node node;

		/**
		 * The offset within the node.
		 */
		public int offset;

		/**
		 * Constructor for creating a new Node_Data object.
		 *
		 * @param node   the node at the specific point
		 * @param offset the offset within the node
		 */
		public Node_Data(Node node, int offset) {
			this.node = node;
			this.offset = offset;
		}
	}

	/**
	 * Locates the node and offset for a given position in the list.
	 *
	 * @param pos the position in the list to locate
	 * @return the Node_Data object containing the node and offset for the given
	 *         position, or null if the position is not in the list
	 */
	private Node_Data find(int pos) {
		Node temp = head.next;
		int currPos = 0;
		while (temp != tail) {
			if (currPos + temp.count <= pos) {
				currPos += temp.count;
				temp = temp.next;
				continue;
			}

			Node_Data nodeInfo = new Node_Data(temp, pos - currPos);
			return nodeInfo;

		}
		return null;
	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 *
	 * @param pos  the position at which the specified element is to be inserted
	 * @param item the element to be inserted
	 * @throws IndexOutOfBoundsException if the position is out of range (pos < 0 ||
	 *                                   pos > size)
	 */
	@Override
	public void add(int pos, E item) {
		if (pos < 0 || pos > size)
			throw new IndexOutOfBoundsException();

		if (head.next == tail)
			add(item);

		Node_Data nodeInfo = find(pos);
		Node temp = nodeInfo.node;
		int offset = nodeInfo.offset;

		if (offset == 0) {

			if (temp.previous.count < nodeSize && temp.previous != head) {
				temp.previous.addItem(item);
				size++;
				return;
			}

			else if (temp == tail) {
				add(item);
				size++;
				return;
			}
		}

		if (temp.count < nodeSize) {
			temp.addItem(offset, item);
		}

		else {
			Node newSuccesor = new Node();
			int halfPoint = nodeSize / 2;
			int count = 0;
			while (count < halfPoint) {
				newSuccesor.addItem(temp.data[halfPoint]);
				temp.removeItem(halfPoint);
				count++;
			}

			Node oldSuccesor = temp.next;

			temp.next = newSuccesor;
			newSuccesor.previous = temp;
			newSuccesor.next = oldSuccesor;
			oldSuccesor.previous = newSuccesor;

			if (offset <= nodeSize / 2) {
				temp.addItem(offset, item);
			}

			if (offset > nodeSize / 2) {
				newSuccesor.addItem((offset - nodeSize / 2), item);
			}

		}

		size++;
	}

	/**
	 * Removes the element at the specified position in this list.
	 *
	 * @param pos the position of the element to be removed
	 * @return the element that was removed from the list
	 * @throws IndexOutOfBoundsException if the position is out of range (pos < 0 ||
	 *                                   pos >= size)
	 */
	@Override
	public E remove(int pos) {

		if (pos < 0 || pos > size)
			throw new IndexOutOfBoundsException();
		Node_Data nodeInfo = find(pos);
		Node temp = nodeInfo.node;
		int offset = nodeInfo.offset;
		E node_value = temp.data[offset];

		if (temp.next == tail && temp.count == 1) {
			Node previous_one = temp.previous;
			previous_one.next = temp.next;
			temp.next.previous = previous_one;
			temp = null;
		}

		else if (temp.next == tail || temp.count > nodeSize / 2) {
			temp.removeItem(offset);
		}

		else {
			temp.removeItem(offset);
			Node succesor = temp.next;

			if (succesor.count > nodeSize / 2) {
				temp.addItem(succesor.data[0]);
				succesor.removeItem(0);
			} else if (succesor.count <= nodeSize / 2) {
				for (int i = 0; i < succesor.count; i++) {
					temp.addItem(succesor.data[i]);
				}
				temp.next = succesor.next;
				succesor.next.previous = temp;
				succesor = null;
			}
		}

		size--;
		return node_value;
	}

	/**
	 * Sort all elements in the stout list in the NON-DECREASING order. You may do
	 * the following. Traverse the list and copy its elements into an array,
	 * deleting every visited node along the way. Then, sort the array by calling
	 * the insertionSort() method. (Note that sorting efficiency is not a concern
	 * for this project.) Finally, copy all elements from the array back to the
	 * stout list, creating new nodes for storage. After sorting, all nodes but
	 * (possibly) the last one must be full of elements.
	 * 
	 * Comparator<E> must have been implemented for calling insertionSort().
	 */
	public void sort() {
		E[] sortDataList = (E[]) new Comparable[size];

		int tempIndex = 0;
		Node temp = head.next;
		while (temp != tail) {
			for (int i = 0; i < temp.count; i++) {
				sortDataList[tempIndex] = temp.data[i];
				tempIndex++;
			}
			temp = temp.next;
		}

		head.next = tail;
		tail.previous = head;

		insertionSort(sortDataList, new compare_element());
		size = 0;
		for (int i = 0; i < sortDataList.length; i++) {
			add(sortDataList[i]);
		}

	}

	/**
	 * Sorts all elements in the stout list in NON-INCREASING order. The process
	 * involves copying elements into an array, deleting each visited node, sorting
	 * the array using bubbleSort(), and then copying the sorted elements back into
	 * the stout list. After sorting, all nodes except possibly the last one should
	 * be full.
	 * <p>
	 * This method requires that the Comparable<? super E> is implemented for
	 * calling bubbleSort().
	 * </p>
	 */
	public void sortReverse() {
		E[] revsortlist = (E[]) new Comparable[size];

		int tempIndex = 0;
		Node temp = head.next;
		while (temp != tail) {
			for (int i = 0; i < temp.count; i++) {
				revsortlist[tempIndex] = temp.data[i];
				tempIndex++;
			}
			temp = temp.next;
		}

		head.next = tail;
		tail.previous = head;

		bubbleSort(revsortlist);
		size = 0;
		for (int i = 0; i < revsortlist.length; i++) {
			add(revsortlist[i]);
		}
	}

	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 *
	 * @return an iterator over the elements in this list in proper sequence
	 */
	@Override
	public Iterator<E> iterator() {
		return new StoutListIterator();
	}

	/**
	 * Returns a list iterator over the elements in this list (in proper sequence).
	 *
	 * @return a list iterator over the elements in this list (in proper sequence)
	 */
	@Override
	public ListIterator<E> listIterator() {
		return new StoutListIterator();
	}

	/**
	 * Returns a list iterator over the elements in this list (in proper sequence),
	 * starting at the specified position in the list.
	 *
	 * @param index index of the first element to be returned from the list iterator
	 *              (by a call to the next method)
	 * @return a list iterator over the elements in this list (in proper sequence)
	 * @throws IndexOutOfBoundsException if the index is out of range (index < 0 ||
	 *                                   index > size())
	 */
	@Override
	public ListIterator<E> listIterator(int index) {
		return new StoutListIterator(index);
	}

	/**
	 * Provides a string representation of this list, showcasing the internal
	 * structure of the nodes.
	 * 
	 * @return a string representation of this list with its internal node structure
	 */
	public String toStringInternal() {
		return toStringInternal(null);
	}

	/**
	 * Returns a string representation of this list showing the internal structure
	 * of the nodes and the position of the iterator.
	 *
	 * @param iter an iterator for this list
	 */
	public String toStringInternal(ListIterator<E> iter) {
		int count = 0;
		int position = -1;
		if (iter != null) {
			position = iter.nextIndex();
		}

		StringBuilder sb = new StringBuilder();
		sb.append('[');
		Node current = head.next;
		while (current != tail) {
			sb.append('(');
			E data = current.data[0];
			if (data == null) {
				sb.append("-");
			} else {
				if (position == count) {
					sb.append("| ");
					position = -1;
				}
				sb.append(data.toString());
				++count;
			}

			for (int i = 1; i < nodeSize; ++i) {
				sb.append(", ");
				data = current.data[i];
				if (data == null) {
					sb.append("-");
				} else {
					if (position == count) {
						sb.append("| ");
						position = -1;
					}
					sb.append(data.toString());
					++count;

					// iterator at end
					if (position == size && count == size) {
						sb.append(" |");
						position = -1;
					}
				}
			}
			sb.append(')');
			current = current.next;
			if (current != tail)
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	/**
	 * Node type for this list. Each node holds a maximum of nodeSize elements in an
	 * array. Empty slots are null.
	 */
	private class Node {
		/**
		 * Array of actual data elements.
		 */
		public E[] data = (E[]) new Comparable[nodeSize];

		/**
		 * Link to next node.
		 */
		public Node next;

		/**
		 * Link to previous node;
		 */
		public Node previous;

		/**
		 * Index of the next available offset in this node, also equal to the number of
		 * elements in this node.
		 */
		public int count;

		/**
		 * Adds an item to this node at the first available offset. Precondition: count
		 * < nodeSize
		 * 
		 * @param item element to be added
		 */
		void addItem(E item) {
			if (count >= nodeSize) {
				return;
			}
			data[count++] = item;
			// useful for debugging
			// System.out.println("Added " + item.toString() + " at index " + count + " to
			// node " + Arrays.toString(data));
		}

		/**
		 * Adds an item to this node at the indicated offset, shifting elements to the
		 * right as necessary.
		 * 
		 * Precondition: count < nodeSize
		 * 
		 * @param offset array index at which to put the new element
		 * @param item   element to be added
		 */
		void addItem(int offset, E item) {
			if (count >= nodeSize) {
				return;
			}
			for (int i = count - 1; i >= offset; --i) {
				data[i + 1] = data[i];
			}
			++count;
			data[offset] = item;
			// useful for debugging
//      System.out.println("Added " + item.toString() + " at index " + offset + " to node: "  + Arrays.toString(data));
		}

		/**
		 * Deletes an element from this node at the indicated offset, shifting elements
		 * left as necessary. Precondition: 0 <= offset < count
		 * 
		 * @param offset
		 */
		void removeItem(int offset) {
			E item = data[offset];
			for (int i = offset + 1; i < nodeSize; ++i) {
				data[i - 1] = data[i];
			}
			data[count - 1] = null;
			--count;
		}
	}

	/**
	 * An iterator for the StoutList that implements the ListIterator interface.
	 * This iterator provides mechanisms to traverse the list in both directions,
	 * retrieve items, modify them, remove them, and add new items.
	 */
	private class StoutListIterator implements ListIterator<E> {

		/**
		 * Constant used to indicate the last move was a backward step.
		 */
		final int PREVIOUS_FIX = 0;

		/**
		 * Constant used to indicate the last move was a forward step.
		 */
		final int NEXT_FIX = 1;

		/**
		 * The current position of the iterator in the list.
		 */
		int currentPosition;

		/**
		 * An array representation of the StoutList's data for easier iteration.
		 */
		public E[] dlist;

		/**
		 * Indicates the last action (next or previous) performed. This is primarily
		 * used in the remove() and set() methods to determine the item to be removed or
		 * replaced.
		 */
		int finalstep;

		/**
		 * Default constructor. Initializes the iterator at the beginning of the list.
		 */
		public StoutListIterator() {
			currentPosition = 0;
			finalstep = -1;
			list();
		}

		/**
		 * Constructor finds node at a given position. Sets the pointer of iterator to
		 * the specific index of the list
		 * 
		 * @param pos
		 */
		public StoutListIterator(int pos) {
			currentPosition = pos;
			finalstep = -1;
			list();
		}

		/**
		 * Helper method to populate the dlist array representation.
		 */
		private void list() {
			dlist = (E[]) new Comparable[size];

			int temp_i = 0;
			Node pending = head.next;
			while (pending != tail) {
				for (int i = 0; i < pending.count; i++) {
					dlist[temp_i] = pending.data[i];
					temp_i++;
				}
				pending = pending.next;
			}
		}

		/**
		 * Checks if the iterator has more elements when traversing the list in the
		 * forward direction.
		 * 
		 * @return true if there are more elements, false otherwise.
		 */
		@Override
		public boolean hasNext() {
			if (currentPosition >= size)
				return false;
			else
				return true;
		}

		/**
		 * Returns the next element in the list and advances the iterator by one
		 * position.
		 * 
		 * @return Next element in the list.
		 * @throws NoSuchElementException if the iteration has no next element.
		 */
		@Override
		public E next() {
			if (!hasNext())
				throw new NoSuchElementException();
			finalstep = NEXT_FIX;
			return dlist[currentPosition++];
		}

		/**
		 * Removes the last element returned by the iterator (next() or previous()).
		 * This method can only be called once per call to next() or previous().
		 * 
		 * @throws IllegalStateException if neither next nor previous have been called,
		 *                               or remove or add have been called after the
		 *                               last call to next or previous.
		 */
		@Override
		public void remove() {
			if (finalstep == NEXT_FIX) {
				StoutList.this.remove(currentPosition - 1);
				list();
				finalstep = -1;
				currentPosition--;
				if (currentPosition < 0)
					currentPosition = 0;
			} else if (finalstep == PREVIOUS_FIX) {
				StoutList.this.remove(currentPosition);
				list();
				finalstep = -1;
			} else {
				throw new IllegalStateException();
			}
		}

		/**
		 * Checks if the iterator has more elements when traversing the list in the
		 * reverse direction.
		 * 
		 * @return true if there are more elements, false otherwise.
		 */
		@Override
		public boolean hasPrevious() {
			// TODO Auto-generated method stub
			if (currentPosition <= 0)
				return false;
			else
				return true;
		}

		/**
		 * Returns the index of the element that would be returned by a subsequent call
		 * to next().
		 * 
		 * @return Index of the next element, or size if the list iterator is at the end
		 *         of the list.
		 */
		@Override
		public int nextIndex() {
			// TODO Auto-generated method stub
			return currentPosition;
		}

		/**
		 * Returns the previous element in the list and moves the iterator back by one
		 * position.
		 * 
		 * @return Previous element in the list.
		 * @throws NoSuchElementException if the iteration has no previous element.
		 */
		@Override
		public E previous() {
			if (!hasPrevious())
				throw new NoSuchElementException();
			finalstep = PREVIOUS_FIX;
			currentPosition--;
			return dlist[currentPosition];
		}

		/**
		 * Returns the index of the element that would be returned by a subsequent call
		 * to previous().
		 * 
		 * @return Index of the previous element, or -1 if the list iterator is at the
		 *         beginning of the list.
		 */
		@Override
		public int previousIndex() {
			// TODO Auto-generated method stub
			return currentPosition - 1;
		}

		/**
		 * Replaces the last element returned by next() or previous() with the specified
		 * element.
		 * 
		 * @param item The element with which to replace the last element returned by
		 *             next or previous.
		 * @throws IllegalStateException if neither next nor previous have been called,
		 *                               or remove or add have been called after the
		 *                               last call to next or previous.
		 */
		@Override
		public void set(E item) {
			if (finalstep == NEXT_FIX) {
				Node_Data nodeInfo = find(currentPosition - 1);
				nodeInfo.node.data[nodeInfo.offset] = item;
				dlist[currentPosition - 1] = item;
			} else if (finalstep == PREVIOUS_FIX) {
				Node_Data nodeInfo = find(currentPosition);
				nodeInfo.node.data[nodeInfo.offset] = item;
				dlist[currentPosition] = item;
			} else {
				throw new IllegalStateException();
			}

		}

		/**
		 * Inserts the specified element into the list. The element is inserted
		 * immediately before the next element that would be returned by next(), if any,
		 * and after the next element that would be returned by previous(), if any.
		 * 
		 * @param item The element to insert into the list.
		 * @throws NullPointerException if the specified element is null.
		 */
		@Override
		public void add(E item) {
			if (item == null)
				throw new NullPointerException();

			StoutList.this.add(currentPosition, item);
			currentPosition++;
			list();
			finalstep = -1;

		}
	}

	/**
	 * Sort an array arr[] using the insertion sort algorithm in the NON-DECREASING
	 * order.
	 * 
	 * @param arr  array storing elements from the list
	 * @param compar comparator used in sorting
	 */
	private void insertionSort(E[] arr, Comparator<? super E> compar) {
		int n = arr.length;
		for (int i = 1; i < n; ++i) {
			E key = arr[i];
			int j = i - 1;

			while (j >= 0 && compar.compare(arr[j], key) > 0) {
				arr[j + 1] = arr[j];
				j = j - 1;
			}
			arr[j + 1] = key;
		}
	}

	/**
	 * Sort arr[] using the bubble sort algorithm in the NON-INCREASING order. For a
	 * description of bubble sort please refer to Section 6.1 in the project
	 * description. You must use the compareTo() method from an implementation of
	 * the Comparable interface by the class E or ? super E.
	 * 
	 * @param arr array holding elements from the list
	 */
	private void bubbleSort(E[] arr) {
		int n = arr.length;
		for (int i = 0; i < n - 1; i++)
			for (int j = 0; j < n - i - 1; j++)
				if (arr[j].compareTo(arr[j + 1]) < 0) {
					E temp = arr[j];
					arr[j] = arr[j + 1];
					arr[j + 1] = temp;
				}
	}

	/**
	 * A comparator class for comparing elements of type E or its subclasses. The
	 * comparison is done based on the natural order established by the compareto()}
	 * method of the comparable interface.
	 */
	class compare_element<E extends Comparable<E>> implements Comparator<E> {
		/**
		 * Compares its two arguments for order.
		 *
		 * @param item0 The first object to be compared.
		 * @param item1 The second object to be compared.
		 * @return a negative integer, zero, or a positive integer as the first argument
		 *         is less than, equal to, or greater than the second.
		 */
		@Override
		public int compare(E item0, E item1) {
			return item0.compareTo(item1);
		}

	}

}