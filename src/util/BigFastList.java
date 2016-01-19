package util;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.RandomAccess;

import org.magicwerk.brownies.collections.GapList;

/**
 * A high performance disk caching ordered N-elements List (not limited to
 * Integer.MAX_VALUE)
 * 
 * Keeps the specified number of items in memory. Caches the rest to disk.
 * 
 * Volatile caching! Cache directory is erased upon restart of application.. Do
 * not use for long term storage of elements!
 * 
 * Also known as BFL (hehe, no pun intended).
 * 
 * @author Amir Eslampanah
 */
public class BigFastList<E> extends AbstractList<E> implements RandomAccess,
	Cloneable, Serializable {

    List<E> items = null;

    /**
     * 
     */
    public BigFastList() {
	// TODO Auto-generated constructor stub
    }

    /**
     * Returns a shortened list of size val
     * 
     * Using the first val elements of this list
     * 
     * Remember to call clear() and set the old object reference to null
     * manually avoid overhead
     * 
     * Because BFL uses disk caching. The garbage collector does not remove the
     * file instances even when the object reference is set to null.
     * 
     * @return
     *
     */
    public BigFastList<E> prune(BigInteger val) {
	BigFastList<E> pruned = new BigFastList<E>();

	BigInteger count = BigInteger.ZERO;

	for (E i : this) {
	    pruned.add(i);
	    count = count.add(BigInteger.ONE);

	    if (count.compareTo(val) == 0) {
		break;
	    }
	}

	return pruned;
    }

    @Override
    public boolean add(Object toAdd) {
	return false;
    }

    private class List<E> {
	private final GapList<E> internalList = new GapList<E>();
	/**
	 * Once this is set to true it should never be set back to false. This
	 * is done to preserve the order of inserted elements. Otherwise a
	 * subsequent remove operation would change the position of the next add
	 * operation.
	 * 
	 * This also means that if all items are removed from a list, it will
	 * essentially act as overhead keeping the pointer to the next list.
	 * 
	 * However the number of these should be relatively small considering
	 * the number of elements needed to force the creation of another list.
	 * 
	 * @TODO Improve this.
	 */
	private boolean isFull = false;

	private List<E> next = null;

	public List() {
	}

	public List<E> getNext() {
	    if (this.next == null) {
		this.next = new List<E>();
	    }
	    return this.next;
	}

	/**
	 * @return the internalList
	 */
	public GapList<E> getInternalList() {
	    return this.internalList;
	}

	/**
	 * Returns whether or not this list has reached maximum capacity.
	 * 
	 * @return the isFull
	 */
	public boolean isFull() {
	    return this.isFull;
	}

	/**
	 * Sets whether or not this list has been filled.
	 * 
	 * @param isFull
	 */
	public void setFull(boolean isFull) {
	    this.isFull = isFull;
	}

	/**
	 * @param next
	 *            the next to set
	 */
	public void setNext(List<E> next) {
	    this.next = next;
	}

	public boolean contains(Object item) {
	    List<E> temp = this;

	    while (temp != null && !temp.getInternalList().contains(item)) {
		temp = temp.getNext();
	    }

	    if (temp == null) {
		return false;
	    }
	    // TODO: Finish this
	    return false;

	}

	/**
	 * Returns the first index of the specified object or null if not found
	 * 
	 * @param item
	 *            Object to search for
	 * @return ListIndex containing both a reference to the list and
	 *         position of the object or null if not found
	 */
	public ListIndex getIndexOf(Object item) {
	    List<E> temp = this;

	    // If our current List does not contain the item then check the next
	    // list
	    while (!temp.getInternalList().contains(item)) {
		temp = temp.getNext();
		// If the next list has a size of zero and hasn't been filled up
		// in the past
		// We can stop looking
		if (temp.getNext().getInternalList().size() == 0
			&& temp.isFull() != true) {
		    break;
		}
	    }

	    // If the position of the item can be found in the current List
	    // Then assign both a reference to the list as well as the index at
	    // which the object exists in that List
	    // and return the ListIndex
	    if (temp.getInternalList().indexOf(item) > -1) {
		ListIndex index = new ListIndex();
		index.setIndex(BigInteger.valueOf(temp.getInternalList()
			.indexOf(item)));
		index.setList(temp);
		return index;
	    }

	    // Otherwise the item was not found and we return null
	    return null;
	}

	/**
	 * Adds the specified item to the end of the list
	 * 
	 * @param item
	 *            Object to add to end of list
	 */
	public void add(Object item) {
	    List<E> curList = this;

	    /*
	     * If we approach Integer.MAX_VALUE then it is time to use the next
	     * list
	     */
	    while (curList.getInternalList().size() >= (Integer.MAX_VALUE - 12)
		    || curList.isFull) {
		curList.setFull(true);
		curList = curList.getNext();
	    }

	    curList.add(item);
	}

	/**
	 * Removes the first instance of the specified object.
	 * 
	 * @param item
	 *            Item to remove
	 * @return Returns true if the operation succeeded; false if it
	 *         failed(item not found or null).
	 */
	public boolean remove(Object item) {

	    List<E> curList = this;

	    while (!curList.getInternalList().remove(item)) {
		if (curList.getNext().getInternalList().size() != 0) {
		    curList = curList.getNext();
		} else {
		    return false;
		}

	    }
	    return true;
	}
    }

    private class ListIndex {
	private BigInteger index;
	private List list;

	public ListIndex() {

	}

	/**
	 * @return the index
	 */
	public BigInteger getIndex() {
	    return this.index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(BigInteger index) {
	    this.index = index;
	}

	/**
	 * @return the list
	 */
	public List getList() {
	    return this.list;
	}

	/**
	 * @param list
	 *            the list to set
	 */
	public void setList(List list) {
	    this.list = list;
	}

    }

    @Override
    public boolean isEmpty() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public E get(int intValueExact) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    /**
     * The size of this list 
     * 
     * Returns -1 if the size of this list beyond Integer.MAX_SIZE
     */
    public int size() {
	// TODO Auto-generated method stub
	return null;
    }

    /**
     * Clears the list and deletes all files made to cache this list
     * 
     * This should be called before nullifying a BFL reference
     */
    @Override
    public void clear() {
	// TODO: Code to delete files and clear list here
    }

    @Override
    public void remove(int intValueExact) {
	// TODO Auto-generated method stub

    }

    @Override
    public Iterator iterator() {
	// TODO Auto-generated method stub
	return null;
    }

    /**
     * @param sub
     */
    public void remove(E sub) {
	// TODO Auto-generated method stub

    }

    /**
     * @return our list of items
     */
    public List<E> getItems() {
	return this.items;
    }

    /**
     * @param ourListofItems
     */
    public void setItems(List<E> ourListofItems) {
	this.items = ourListofItems;
    }

}
