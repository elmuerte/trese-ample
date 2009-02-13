/*
 * KernelProlog Expanded - Pure Java based Prolog Engine
 * Copyright (C) 1999  Paul Tarau (Original KernelProlog)
 * Copyright (C) 2009  Michiel Hendriks
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.sf.kpex.prolog;

import java.util.HashMap;
import java.util.Map;

/**
 * Register containing information about builtin prolog functions.
 * 
 * @author Michiel Hendriks
 */
public class BuiltinRegistry
{
	/**
	 * Contains the entries in the register
	 */
	protected Map<String, Const> entries;

	public BuiltinRegistry()
	{
		entries = new HashMap<String, Const>();
	}

	public BuiltinRegistry(BuiltinRegistry copyFrom)
	{
		this();
		entries.putAll(copyFrom.entries);
	}

	/**
	 * Register a new builtin functions. Returns the replaced builtin.
	 * 
	 * @param entry
	 * @return
	 * @throws IllegalArgumentException
	 *             Thrown when the given entry is not a builtin
	 * @throws NullPointerException
	 *             Thrown when the entry is null
	 */
	public Const register(Const entry) throws IllegalArgumentException, NullPointerException
	{
		if (entry == null)
		{
			throw new NullPointerException("Entry can not be null");
		}
		String key = String.format("%s/%d", entry.getName(), entry.getArity());
		if (!entry.isBuiltin())
		{
			throw new IllegalArgumentException(String.format("%s is not a builtin", key));
		}
		return entries.put(key, entry);
	}

	/**
	 * Register a library with builtins
	 * 
	 * @param library
	 * @throws IllegalArgumentException
	 * @throws NullPointerException
	 */
	public void register(BuiltinLibrary library) throws IllegalArgumentException, NullPointerException
	{
		if (library == null)
		{
			throw new NullPointerException("BuiltinLibrary can not be null");
		}
		for (Const entry : library.getBuiltins())
		{
			register(entry);
		}
	}

	/**
	 * Get a builtin using a signature
	 * 
	 * @param signature
	 * @return
	 * @throws NullPointerException
	 *             Thrown when the signature is null or empty
	 */
	public Const get(String signature) throws NullPointerException
	{
		if (signature == null)
		{
			throw new NullPointerException("Signature cannot be null");
		}
		if (signature.length() == 0)
		{
			throw new NullPointerException("Signature cannot be empty");
		}
		return entries.get(signature);
	}

	/**
	 * Get a builtin using a {@link Const} as signature. Used during parsing to
	 * of statements to retrieve the real builtins.
	 * 
	 * @param signature
	 * @return
	 * @throws NullPointerException
	 */
	public Const get(Const signature) throws NullPointerException
	{
		if (signature == null)
		{
			throw new NullPointerException("Signature cannot be null");
		}
		return get(String.format("%s/%d", signature.getName(), signature.getArity()));
	}
}
