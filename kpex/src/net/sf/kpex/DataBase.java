/*
 * KernelProlog Expanded - Pure Java based Prolog Engine
 * Copyright (C) 1999  Paul Tarau (original KernelProlog)
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
package net.sf.kpex;

import java.io.Reader;
import java.util.Enumeration;
import java.util.Vector;

import net.sf.kpex.io.IO;
import net.sf.kpex.parser.Parser;
import net.sf.kpex.prolog.Clause;
import net.sf.kpex.prolog.Cons;
import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.Fun;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Term;
import net.sf.kpex.util.HashDict;
import net.sf.kpex.util.Queue;

/**
 * Implements a Term and Clause objects based blackboard (database).
 */
public class DataBase extends BlackBoard
{

	private static String lastFile = "tarau/jinni/lib.pro";

	private static Const no = Const.NO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3030128958993551856L;
	private static Const yes = Const.YES;

	/**
	 * adds a Clause to the joint Linda and Predicate table
	 */
	static public void addClause(Clause C, HashDict ktable)
	{
		String k = C.getKey();
		// overwrites previous definitions
		if (null != ktable && null != ktable.get(k))
		{
			ktable.remove(k);
			Init.default_db.remove(k);
		}
		Init.default_db.out(k, C, false);
	}

	/**
	 * reconsults the last reconsulted file
	 */
	static public boolean fromFile()
	{
		IO.println("begin('" + lastFile + "')");
		boolean ok = fromFile(lastFile);
		if (ok)
		{
			IO.println("end('" + lastFile + "')");
		}
		return ok;
	}

	/**
	 * reconsults a file by overwritting similar predicates in memory
	 */
	static public boolean fromFile(String f)
	{
		return fromFile(f, true);
	}

	/**
	 * consults or reconsults a Prolog file by adding or overriding existing
	 * predicates to be extended to load from URLs transparently
	 */
	static public boolean fromFile(String f, boolean overwrite)
	{
		IO.trace("last consulted file was: " + lastFile);
		boolean ok = fileToProg(f, overwrite);
		if (ok)
		{
			IO.trace("last consulted file set to: " + f);
			lastFile = f;
		}
		else
		{
			IO.errmes("error in consulting file: " + f);
		}
		return ok;
	}

	/**
	 * adds a Clause to the joint Linda and Predicate table
	 * 
	 * @see Clause
	 */
	static public void processClause(Clause C, HashDict ktable)
	{
		if (C.getHead().matches(new Const("init")))
		{
			// IO.mes("init: "+C.getBody());
			Prog.firstSolution(C.getHead(), C.getBody());
		}
		else
		{
			// IO.mes("ADDING= "+C.pprint());
			addClause(C, ktable);
		}
	}

	/**
	 * Reads a set of clauses from a stream and adds them to the blackboard.
	 * Overwrites old predicates if asked to. Returns true if all went well.
	 */
	static public boolean streamToProg(Reader sname, boolean overwrite)
	{
		return streamToProg(sname.toString(), sname, overwrite);
	}

	static private void apply_parser(Parser p, String fname, BlackBoard ktable)
	{
		for (;;)
		{
			if (p.atEOF())
			{
				return;
			}
			int begins_at = p.lineno();
			Clause C = p.readClause();
			if (null == C)
			{
				return;
			}
			if (Parser.isError(C))
			{
				Parser.showError(C);
			}
			else
			{
				// IO.mes("ADDING= "+C.pprint());
				processClause(C, ktable);
				C.setFile(fname, begins_at, p.lineno());
			}
		}
	}

	static private boolean fileToProg(String fname, boolean overwrite)
	{
		Reader sname = IO.toFileReader(fname);
		if (null == sname)
		{
			return false;
		}
		return streamToProg(fname, sname, overwrite);
	}

	static private boolean streamToProg(String fname, Reader sname, boolean overwrite)
	{
		BlackBoard ktable = overwrite ? (BlackBoard) Init.default_db.clone() : null;
		// Clause Err=new Clause(new Const("error"),new Var());
		try
		{
			Parser p = new Parser(sname);
			apply_parser(p, fname, ktable);
		}
		catch (Exception e)
		{ // already catched by readClause
			IO.errmes("unexpected error in streamToProg", e);
			return false;
		}
		return true;
	}

	public DataBase()
	{
		super();
	}

	/**
	 * Returns a (possibly empty) list of matching Term objects
	 */
	public Term all(String k, Term FX)
	{
		FX = all2(0, k, FX);
		return FX;
	}

	/**
	 * Removes a matching Term from the blackboards and signals failure if no
	 * such term is found.
	 */
	public Term cin(String k, Term pattern)
	{
		Term found = take(k, pattern);
		// if(found!=null) {
		// found=found.matching_copy(pattern);
		// }
		if (found == null)
		{
			found = no;
		}
		else
		{
			found = new Fun("the", found.copy());
		}
		return found;
	}

	/**
	 * Adds a copy of a Term to the blackboard
	 */

	synchronized public Term out(String key, Term pattern)
	{
		return out(key, pattern, true); // copies pattern
	}

	/**
	 * Adds a Term to the blackboard
	 */
	public Term out(String k, Term pattern, boolean copying)
	{
		add(k, copying ? pattern.copy() : pattern);
		return yes;
	}

	/**
	 * Returns a formatted String representation of this PrologBlackboard object
	 */
	public String pprint()
	{
		StringBuffer s = new StringBuffer(name());
		Enumeration e = keys();
		while (e.hasMoreElements())
		{
			s.append(pred_to_string((String) e.nextElement()));
			s.append("\n");
		}
		return s.toString();
	}

	public String pred_to_string(String key)
	{
		Queue Q = (Queue) get(key);
		if (null == Q)
		{
			return null;
		}
		Enumeration e = Q.toEnumeration();
		StringBuffer s = new StringBuffer("% " + key + "\n\n");
		while (e.hasMoreElements())
		{
			s.append(((Term) e.nextElement()).prettyPrint(true));
			s.append(".\n");
		}
		s.append("\n");
		return s.toString();
	}

	/**
	 * Gives an Enumeration view to the Queue of Term or Clause objects stored
	 * at key k
	 * 
	 * @see Queue
	 * @see Term
	 * @see Clause
	 */
	@Override
	public Enumeration toEnumerationFor(String k)
	{
		Enumeration E = super.toEnumerationFor(k);
		return E;
	}

	private void all0(int max, Vector To, String k, Term FXs)
	{
		if (0 == max)
		{
			max = -1;
		}
		Queue Q = (Queue) get(k);
		if (Q == null)
		{
			return;
		}
		// todo: use always the same "server's" trail
		for (Enumeration e = Q.toEnumeration(); e.hasMoreElements();)
		{
			Term t = (Term) e.nextElement();
			if (null == t)
			{
				break;
			}
			t = t.matchingCopy(FXs);
			if (t != null && 0 != max--)
			{
				To.addElement(t);
			}
		}
	}

	private Term all1(int max, Term FXs)
	{
		Vector To = new Vector();
		for (Enumeration e = keys(); e.hasMoreElements();)
		{
			all0(max, To, (String) e.nextElement(), FXs);
		}
		Fun R = new Fun("$", To.size());
		// IO.mes("RR"+R);
		To.copyInto(R.args);
		return ((Cons) R.listify()).args[1];
	}

	private Term all2(int max, String k, Term FXs)
	{
		if (k == null)
		{
			// IO.mes("expensive operation: all/2 with unknown key");
			return all1(max, FXs);
		}
		Vector To = new Vector();
		all0(max, To, k, FXs);
		if (To.size() == 0)
		{
			return Const.NIL;
		}
		Fun R = new Fun("$", To.size());
		To.copyInto(R.args);
		Term T = ((Cons) R.listify()).args[1];
		return T;
	}
}
