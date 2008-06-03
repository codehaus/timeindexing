Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
// DoubleLinkedList.java

package com.timeindexing.util;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * A doubly linked list.
 * This is similar to java.util.LinkedList but it keeps 
 * its own current position.
 * This allows access to consecutive nodes to be faster,
 * particularly in large lists.
 * LinkedList always starts a getItem() at node 0.
 */
public class DoubleLinkedList {
    Entry header = null;
    Entry current = null;
    long position = -1;
    long size = 0;

    /**
     * Construct a DoubleLinkedList.
     */
    public DoubleLinkedList() {
	header = new Entry(null, null, null);
	header.next = header.previous = header;
	reset();
    }

    /**
     * Returns the first element in this list.
     *
     * @return the first element in this list.
     * @throws    NoSuchElementException if this list is empty.
     */
    public Object getFirst() {
	if (size==0)
	    throw new NoSuchElementException();

	return header.next.element;
    }

    /**
     * Returns the last element in this list.
     *
     * @return the last element in this list.
     * @throws    NoSuchElementException if this list is empty.
     */
    public Object getLast()  {
	if (size==0)
	    throw new NoSuchElementException();

	return header.previous.element;
    }

    /**
     * Removes and returns the first element from this list.
     *
     * @return the first element from this list.
     * @throws    NoSuchElementException if this list is empty.
     */
    public Object removeFirst() {
	Object first = header.next.element;
	remove(header.next);
	return first;
    }

    /**
     * Removes and returns the last element from this list.
     *
     * @return the last element from this list.
     * @throws    NoSuchElementException if this list is empty.
     */
    public Object removeLast() {
	Object last = header.previous.element;
	remove(header.previous);
	return last;
    }

    /**
     * Inserts the given element at the beginning of this list.
     * 
     * @param o the element to be inserted at the beginning of this list.
     */
    public void addFirst(Object o) {
	Entry e = addBefore(o, header.next);
	current = e;
	position = 0;
    }

    /**
     * Appends the given element to the end of this list.  (Identical in
     * function to the <tt>add</tt> method; included only for consistency.)
     * 
     * @param o the element to be inserted at the end of this list.
     */
    public void addLast(Object o) {
	add(o);
    }

    /**
     * Returns the number of elements in this list.
     *
     * @return the number of elements in this list.
     */
    public long size() {
	return size;
    }

    /**
     * Appends the specified element to the end of this list.
     *
     * @param o element to be appended to this list.
     * @return <tt>true</tt> (as per the general contract of
     * <tt>Collection.add</tt>).
     */
    public boolean add(Object o) {
	Entry e = addBefore(o, header);
	current = e;
	position = size-1;
        return true;
    }

    /**
     * Removes the first occurrence of the specified element in this list.  If
     * the list does not contain the element, it is unchanged.  More formally,
     * removes the element with the lowest index <tt>i</tt> such that
     * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt> (if such an
     * element exists).
     *
     * @param o element to be removed from this list, if present.
     * @return <tt>true</tt> if the list contained the specified element.
     */
    public boolean remove(Object o) {
	long pos = 0;

	// If its a null, remove first null
        if (o==null) {
            for (Entry e = header.next; e != header; e = e.next) {
                if (e.element==null) {
		    if (pos == position) {
			// we are removing where position is pointing to
			current = e.next;
		    } else if (pos < position) {
			// if the entry to be removed is before position
			// then position has to be adjusted
			position--;
		    }

		    // found a null, so remove it
                    remove(e);

                    return true;
                }
		pos++;
            }
        } else {
            for (Entry e = header.next; e != header; e = e.next) {
                if (o.equals(e.element)) {
		    if (pos == position) {
			// we are removing where position is pointing to
			current = e.next;
		    } else if (pos < position) {
			// if the entry to be removed is before position
			// then position has to be adjusted
			position--;
		    }

		    // found the element, so remove it
                    remove(e);

                    return true;
                }
		pos++;
            }
        }
        return false;
    }

    /**
     * Removes all of the elements from this list.
     */
    public void clear() {
	header.next = header.previous = header;
	reset();
    }


    /**
     * Resets the local variables.
     */
    private void reset() {
	current = null;
	position = -1;
	size = 0;
    }


    // Positional Access Operations

    /**
     * Returns the element at the specified position in this list.
     *
     * @param index index of element to return.
     * @return the element at the specified position in this list.
     * 
     * @throws IndexOutOfBoundsException if the specified index is is out of
     * range (<tt>index &lt; 0 || index &gt;= size()</tt>).
     */
    public Object get(long index) {
        return entry(index).element;
    }

    /**
     * Replaces the element at the specified position in this list with the
     * specified element.
     *
     * @param index index of element to replace.
     * @param element element to be stored at the specified position.
     * @return the element previously at the specified position.
     * @throws IndexOutOfBoundsException if the specified index is out of
     *		  range (<tt>index &lt; 0 || index &gt;= size()</tt>).
     */
    public Object set(long index, Object element) {
        Entry e = entry(index);
        Object oldVal = e.element;
        e.element = element;
        return oldVal;
    }

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any
     * subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted.
     * @param element element to be inserted.
     * 
     * @throws IndexOutOfBoundsException if the specified index is out of
     *		  range (<tt>index &lt; 0 || index &gt; size()</tt>).
     */
    public void add(long index, Object element) {
        addBefore(element, (index==size ? header : entry(index)));
    }

    /**
     * Removes the element at the specified position in this list.  Shifts any
     * subsequent elements to the left (subtracts one from their indices).
     * Returns the element that was removed from the list.
     *
     * @param index the index of the element to removed.
     * @return the element previously at the specified position.
     * 
     * @throws IndexOutOfBoundsException if the specified index is out of
     * 		  range (<tt>index &lt; 0 || index &gt;= size()</tt>).
     */
    public Object remove(long index) {
        Entry e = entry(index);
        remove(e);
        return e.element;
    }

    /**
     * Return the indexed entry.
     */
    private Entry entry(long index) {
	Entry e = null;

        if (index < 0 || index >= size)
            throw new IndexOutOfBoundsException("Index: "+index+
                                                ", Size: "+size);

	// if position and current still unset
	// refer them to the header
	if (position < 0 && current == null) {
	    position = 0;
	    current = header;
	}

	//System.err.println("Entry: >> index = " + index + " position = " + position);

	if (index == position) { // at the right place
	    //System.err.println("Entry: at index = " + index + " position = " + position);
	    return current;
	} else if (index == position+1) { // the next element is the most likely candidate
	    // get the next element
	    e = current.next;
	    current = e;
	    position = index;
	    //System.err.println("Entry: next index = " + index + " position = " + position);
	    return e;
	} else if (index == position-1) { // the previous element
	    // get the previous element
	    e = current.previous;
	    current = e;
	    position = index;
	    //System.err.println("Entry: previous index = " + index + " position = " + position);
	    return e;
	} else {
	    // it's not the next element so
	    // determine the best place to iterate from
	    long halfway = size >> 1;
	    long distance = Math.abs(position - index);
	    
	    if (distance < halfway) { // position is nearer than the header
		if (position < index) {
		    // iterate up from position
		    e = current;
		    for (long i = 0; i < distance; i++) {
			e = e.next;
		    }
		} else {
		    // iterate down from position
		    e = current;
		    for (long i = position; i > index; i--) {
			e = e.previous;
		    }
		}
	    } else if (index < halfway) {
		// iterate from header
		e = header;
		// the value needed is in the first half so iterate from start
		for (long i = 0; i <= index; i++) {
		    e = e.next;
		}
	    } else {
		// iterate from header
		e = header;
		// the value needed is in the second half so iterate from end
		for (long i = size; i > index; i--) {
		    e = e.previous;
		}
	    }
	    current = e;
	    position = index;
	    //System.err.println("Entry: << index = " + index + " position = " + position);
	    return e;
	}

    }


    // Search Operations

    /**
     * Does the list containt the specified Object.
     */
    public boolean contains(Object o) {
 	long index = indexOf(o);
 
 	if (index == -1) {
 	    // the object is not in the list
 	    return false;
 	} else {
 	    return true;
 	}
    }
 
    /**
     * Returns the index in this list of the first occurrence of the
     * specified element, or -1 if the List does not contain this
     * element.  More formally, returns the lowest index i such that
     * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>, or -1 if
     * there is no such index.
     *
     * @param o element to search for.
     * @return the index in this list of the first occurrence of the
     * 	       specified element, or -1 if the list does not contain this
     * 	       element.
     */
    public long indexOf(Object o) {
        long index = 0;
        if (o==null) {
            for (Entry e = header.next; e != header; e = e.next) {
                if (e.element==null)
                    return index;
                index++;
            }
        } else {
            for (Entry e = header.next; e != header; e = e.next) {
                if (o.equals(e.element))
                    return index;
                index++;
            }
        }
        return -1;
    }

    /**
     * Returns the index in this list of the last occurrence of the
     * specified element, or -1 if the list does not contain this
     * element.  More formally, returns the highest index i such that
     * <tt>(o==null ? get(i)==null : o.equals(get(i)))</tt>, or -1 if
     * there is no such index.
     *
     * @param o element to search for.
     * @return the index in this list of the last occurrence of the
     * 	       specified element, or -1 if the list does not contain this
     * 	       element.
     */
    public long lastIndexOf(Object o) {
        long index = size;
        if (o==null) {
            for (Entry e = header.previous; e != header; e = e.previous) {
                index--;
                if (e.element==null)
                    return index;
            }
        } else {
            for (Entry e = header.previous; e != header; e = e.previous) {
                index--;
                if (o.equals(e.element))
                    return index;
            }
        }
        return -1;
    }


    private Entry addBefore(Object o, Entry e) {
	Entry newEntry = new Entry(o, e, e.previous);
	newEntry.previous.next = newEntry;
	newEntry.next.previous = newEntry;
	size++;
	return newEntry;
    }

    private void remove(Entry e) {
	if (e == header)
	    throw new NoSuchElementException();

	e.previous.next = e.next;
	e.next.previous = e.previous;
	size--;
    }


    /**
     * A link list entry.
     */
    private static class Entry {
        Object element;
        Entry next;
        Entry previous;

        Entry(Object element, Entry next, Entry previous) {
            this.element = element;
            this.next = next;
            this.previous = previous;
        }
    }

}
