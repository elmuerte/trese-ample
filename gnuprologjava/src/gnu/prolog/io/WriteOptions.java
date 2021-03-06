/* GNU Prolog for Java
 * Copyright (C) 1997-1999  Constantine Plotnikov
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA. The text ol license can be also found
 * at http://www.gnu.org/copyleft/lgpl.html
 */
package gnu.prolog.io;

import gnu.prolog.term.Term;

import java.util.Map;

/**
 * ISO Prolog write options (Section 7.10.4)
 * 
 * @author Constantine Plotnikov
 * @version 0.0.1
 */
public class WriteOptions implements Cloneable
{
	/**
	 * If this option is true, each atom and functor is quoted if it would be
	 * necessary for to be input by read_term/3
	 */
	public boolean quoted;
	/**
	 * If this option is true each compound term is output in functional notation.
	 * Neither operator notation nor list notation is used when this write option
	 * is in force.
	 */
	public boolean ignoreOps;
	/**
	 * display terms of form '$VAR'(N) as ('A'+ N%26)+""+(N/26). this option
	 * requires that ignoreOps = false by ISO Standard checking this condition is
	 * left for user.
	 */
	public boolean numbervars;

	/**
	 * Current operator set to use in write term. User need to set this variable
	 * if ignore ops is true.
	 */
	public OperatorSet operatorSet;

	/**
	 * Number of used variables. This variable is used solely by Term Writer
	 */
	protected int numberOfVariables;

	/**
	 * Map from variables to names. This variable is used solely by Term Writer.
	 */
	protected Map<Term, String> variable2name;

	/**
	 * If true print the name of the variables as they were declared
	 */
	public boolean declaredVariableNames;

	/**
	 * Include JavaObjects in the output
	 */
	public boolean javaObjects = true;

	/**
	 * Use .toString() in the output
	 */
	public boolean javaObjectsToString;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			throw new RuntimeException("CloneNotSupportedException");
		}

	}
}
