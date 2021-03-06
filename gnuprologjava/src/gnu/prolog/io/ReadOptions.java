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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * this class contains representation of ISO prolog read options.
 *
 * @author Constantine Plotnikov
 * @versiom 0.0.1
 */
public class ReadOptions
{
	/** 'variables' ISO Prolog options */
	public final List<Term> variables = new ArrayList<Term>();
	/** 'variable_names' ISO Prolog options */
	public final Map<String, Term> variableNames = new HashMap<String, Term>();
	/** operator set */
	public OperatorSet operatorSet;
	/*
	 * 'sigletons' ISO Prolog options it is not used currently.
	 */
	public final Map<String, Term> singletons = new HashMap<String, Term>();
}
