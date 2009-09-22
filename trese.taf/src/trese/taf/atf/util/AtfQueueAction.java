/*
 * !! LICENSE PENDING !! 
 *
 * Copyright (C) 2009 University of Twente.
 */
package trese.taf.atf.util;

/**
 * The action to perform on the repository
 * 
 * @author Michiel Hendriks
 */
public enum AtfQueueAction
{
	/**
	 * Add an element
	 */
	ADD,
	/**
	 * Update the element
	 */
	UPDATE,
	/**
	 * Remove the element
	 */
	REMOVE,
	/**
	 * No action at all, should only be used as the return value of functions
	 * when no real action exists.
	 */
	NONE
}
