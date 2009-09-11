package gnu.prolog.eclipse.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A filtering iterator.
 * 
 * @author Michiel Hendriks
 */
public abstract class FilteringIterator<T> implements Iterator<T>
{
	/**
	 * The base iterator
	 */
	protected Iterator<T> it;

	/**
	 * Holds the next element
	 */
	protected T next;

	/**
	 * True if the next object has been set
	 */
	protected boolean nextSet;

	/**
	 * Create a new repository iterator using a given filter
	 * 
	 * @param filter
	 */
	public FilteringIterator(Iterator<T> iterator)
	{
		it = iterator;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#hasNext()
	 */
	public boolean hasNext()
	{
		if (nextSet)
		{
			return true;
		}
		else
		{
			return getNext();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#next()
	 */
	public T next()
	{
		if (!nextSet)
		{
			if (!getNext())
			{
				throw new NoSuchElementException();
			}
		}
		nextSet = false;
		return next;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Iterator#remove()
	 */
	public void remove()
	{
		if (nextSet)
		{
			// nextSet is false after next() was called
			// only allow remove after next() was called
			throw new IllegalStateException("next() has not been called yet");
		}
		it.remove();
	}

	/**
	 * Get the next element
	 * 
	 * @return True when there is a next element.
	 */
	protected boolean getNext()
	{
		while (it.hasNext())
		{
			T item = it.next();
			if (accept(item))
			{
				next = item;
				nextSet = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * Return true if this item is accepted
	 * 
	 * @param item
	 * @return
	 */
	public abstract boolean accept(T item);
}
