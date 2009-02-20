/*
 * Groove Prolog Interface
 * Copyright (C) 2009 Michiel Hendriks, University of Twente
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package groove.prolog;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.CompoundTermTag;
import gnu.prolog.term.FloatTerm;
import gnu.prolog.term.IntegerTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Convert Prolog Terms to Java objects
 * 
 * @author Michiel Hendriks
 */
public final class TermConverter
{
	/**
	 * @param rawVars
	 * @return
	 */
	public static Map<String, Object> convert(Map<String, Term> rawVars)
	{
		HashMap<String, Object> result = new HashMap<String, Object>();
		for (Entry<String, Term> entry : rawVars.entrySet())
		{
			result.put(entry.getKey(), convert(entry.getValue()));
		}
		return result;
	}

	/**
	 * @param value
	 * @return
	 */
	public static Object convert(Term value)
	{
		value = value.dereference();
		if (value instanceof JavaObjectTerm)
		{
			return ((JavaObjectTerm) value).value;
		}
		else if (value instanceof IntegerTerm)
		{
			return ((IntegerTerm) value).value;
		}
		else if (value instanceof FloatTerm)
		{
			return ((FloatTerm) value).value;
		}
		else if (value instanceof AtomTerm)
		{
			return ((AtomTerm) value).value;
		}
		else if (value instanceof CompoundTerm)
		{
			CompoundTerm ct = (CompoundTerm) value;
			if (ct.tag == CompoundTermTag.list)
			{
				List<Object> compound = new ArrayList<Object>();
				while (ct != null)
				{
					value = ct.args[0].dereference();
					if (value == AtomTerm.emptyList)
					{
						// nop
					}
					else
					{
						compound.add(convert(value));
					}
					value = ct.args[1];
					if (value != null)
					{
						value = value.dereference();
					}
					if (value == AtomTerm.emptyList)
					{
						break;
					}
					else if (value instanceof CompoundTerm && ((CompoundTerm) value).tag == CompoundTermTag.list)
					{
						ct = (CompoundTerm) value;
						continue;
					}
					else
					{
						// something weird?
						break;
					}
				}
				return compound;
			}
		}
		return null;
	}

}
