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
package net.sf.kpex.prolog;

import java.util.Enumeration;

import net.sf.kpex.io.IO;
import net.sf.kpex.parser.Parser;
import net.sf.kpex.util.HashDict;
import net.sf.kpex.util.Trail;

/**
 * Datatype for a Prolog clause (H:-B) having a head H and a body b
 */
public class Clause extends Fun
{
	/**
	 * Extracts a clause from its String representation.
	 */

	public static Clause clauseFromString(String s)
	{
		return Parser.clsFromString(s);
	}

	public static Clause goalFromString(String line)
	{
		IO.trace("read string: <" + line + ">");

		if (null == line)
		{
			line = Const.EOF.name();
		}
		else if (0 == line.length())
		{
			return null;
		}

		Clause C = clauseFromString(line);
		if (null == C)
		{
			IO.errmes("warning (null Clause):" + line);
			return null;
		}

		// IO.trace("got goal:\n"+C.toGoal()); //OK
		return C.toGoal();
	}

	/**
	 * Concatenates 2 Conjunctions
	 * 
	 * @see Clause#unfold
	 */
	static final Term appendConj(Term x, Term y)
	{
		y = y.getRef();
		if (x instanceof True)
		{
			return y;
		}
		if (y instanceof True)
		{
			return x; // comment out if using getState
		}
		if (x instanceof Conj)
		{
			Term curr = ((Conj) x).args[0].getRef();
			Term cont = appendConj(((Conj) x).args[1], y);
			// curr.getState(this,cont);
			return new Conj(curr, cont);
		}
		else
		{
			return new Conj(x, y);
		}
	}

	protected int begins_at = 0;

	/**
	 * Variable dictionary
	 */
	public HashDict dict = null;

	protected int ends_at = 0;

	/**
	 * File name and line where sources start and end (if applicable)
	 */

	protected String fname = null;

	/**
	 * Remembers if a clause is ground.
	 */
	protected boolean ground = false;

	/**
	 * Constructs a clause by parsing its string representation. Note the
	 * building of a dictionary of variables, allowing listing of the clause
	 * with its original variable names.
	 */
	public Clause(String s)
	{
		super(":-");
		Clause C = clauseFromString(s);
		// IO.mes("CLAUSE:"+C.pprint()+"\nDICT:"+C.dict);
		args = C.args;
		dict = C.dict;
		setGround(C.isGround());
	}

	/**
	 * Builds a clause given ith head and its body
	 */
	public Clause(Term head, Term body)
	{
		super(":-", head, body);
	}

	/**
	 * Replaces variables with nice looking upper case constants for printing
	 * purposes
	 */
	synchronized public Clause cnumbervars(boolean replaceAnonymous)
	{
		if (dict == null)
		{
			return (Clause) numberVars();
		}
		if (provenGround())
		{
			return this;
		}
		Trail trail = new Trail();
		Enumeration e = dict.keys();

		while (e.hasMoreElements())
		{
			Object X = e.nextElement();
			if (X instanceof String)
			{
				Var V = (Var) dict.get(X);
				long occNb = ((Int) dict.get(V)).longValue();
				String s = occNb < 2 && replaceAnonymous ? "_" : (String) X;
				// bug: occNb not accurate when adding artif. '[]' head
				V.unify(new PseudoVar(s), trail);
			}
		}
		Clause NewC = (Clause) numberVars();
		trail.unwind(0);
		return NewC;
	}

	/**
	 * Extracts the body of a clause
	 */
	public final Term getBody()
	{
		return args[1].getRef();
	}

	/**
	 * Extracts the head of a clause (a Term).
	 */
	public final Term getHead()
	{
		return args[0].getRef();
	}

	/**
	 * Returns a key based on the principal functor of the head of the clause
	 * and its arity.
	 */
	@Override
	public String getKey()
	{
		return getHead().getKey();
	}

	// uncomment if you want this to be the default toString
	// procedure - it might create read-back problems, though
	// public String toString() {
	// return Clause2String(this);
	// }

	/**
	 * @return the ground
	 */
	public boolean isGround()
	{
		return ground;
	}

	/**
	 * Pretty prints a clause after replacing ugly variable names
	 */
	@Override
	public String prettyPrint()
	{
		return prettyPrint(false);
	}

	/**
	 * Pretty prints a clause after replacing ugly variable names
	 */
	@Override
	public String prettyPrint(boolean replaceAnonymous)
	{
		String s = Clause2String(cnumbervars(replaceAnonymous));
		// if(fname!=null) s="%% "+fname+":"+begins_at+"-"+ends_at+"\n"+s;
		return s;
	}

	public void setFile(String fname, int begins_at, int ends_at)
	{
		this.fname = fname.intern();
		this.begins_at = begins_at;
		this.ends_at = ends_at;
	}

	/**
	 * @param ground
	 *            the ground to set
	 */
	public void setGround(boolean ground)
	{
		this.ground = ground;
	}

	/**
	 * Clause to Term converter: the joy of strong typing:-)
	 */
	@Override
	public Clause toClause()
	{ // overrides toClause in Term
		return this;
	}

	/**
	 * Reads a goal as a clause containing a dummy header with all veriables in
	 * it
	 */

	public Clause toGoal()
	{
		Clause G = new Clause(varsOf(), getHead());
		G.dict = dict;
		G.checkIfGround();
		IO.trace("conversion from clause to goal ignores body of: " + prettyPrint());
		return G;
	}

	/**
	 * Converts a clause to a term. Note that Head:-true will convert to the
	 * term Head.
	 */
	@Override
	public Term toTerm()
	{
		if (getBody() instanceof True)
		{
			return getHead();
		}
		return this;
	}

	/**
	 * Creates a copy of the clause with variables standardized apart, i.e.
	 * something like f(s(X),Y,X) becomes f(s(X1),Y1,X1)) with X1,Y1 variables
	 * garantted not to occurring in the the current resolvant.
	 */
	final Clause ccopy()
	{
		Clause C = (Clause) copy();
		C.dict = null;
		C.setGround(ground);
		return C;
	}

	/**
	 * Detects that a clause is ground (i.e. has no variables)
	 */
	final void checkIfGround()
	{
		setGround(varsOf().getArity() == 0);
	}

	/**
	 * Gets the leftmost (first) goal in the body of a clause, i.e. from
	 * H:-B1,B2,...,Bn it will extract B1.
	 */
	final Term getFirst()
	{
		Term body = getBody();
		if (body instanceof Conj)
		{
			return ((Conj) body).args[0].getRef();
		}
		else if (body instanceof True)
		{
			return null;
		}
		else
		{
			return body;
		}
	}

	/**
	 * Gets all but the leftmost goal in the body of a clause, i.e. from
	 * H:-B1,B2,...,Bn it will extract B2,...,Bn. Note that the returned Term is
	 * either Conj or True, the last one meaning an empty body.
	 * 
	 * @see True
	 * @see Conj
	 */
	final Term getRest()
	{
		Term body = getBody();
		if (body instanceof Conj)
		{
			return ((Conj) body).args[1].getRef();
		}
		else
		{
			return Const.TRUE;
		}
	}

	@Override
	protected final boolean isClause()
	{
		return true;
	}

	/**
	 * Checks if a Clause has been proven ground after beeing read in or
	 * created.
	 */
	final boolean provenGround()
	{
		return isGround();
	}

	/**
	 * Algebraic composition operation of 2 Clauses, doing the basic resolution
	 * step Jinni is based on. From A0:-A1,A2...An and B0:-B1...Bm it builds
	 * (A0:-B1,..Bm,A2,...An) mgu(A1,B0). Note that it returns null if A1 and B0
	 * do not unify.
	 * 
	 * @see Term#unify()
	 */
	Clause unfold(Clause that, Trail trail)
	{
		Clause result = null;
		Term first = getFirst();

		// this is the resolvent, that is the SHARED clause
		// threfore synchronization is over that, not this
		if (first != null && that.getHead().matches(first))
		{

			// IO.mes("UNFOLD: THIS: >>>:"+trail.name()+" "+that);

			if (!that.provenGround())
			{
				that = that.ccopy();
			}

			that.getHead().unify(first, trail);

			// IO.mes("UNFOLD<<<:"+trail.name()+": "+that);

			Term cont = appendConj(that.getBody(), getRest());
			result = new Clause(getHead(), cont);
		}
		return result;
	}

	synchronized final Clause unfold_with_goal(Clause goal, Trail trail)
	{
		return goal.unfold(this, trail);
	}

	synchronized Clause unfoldedCopy(Clause that, Trail trail)
	{
		int oldtop = trail.size();
		Clause result = unfold(that, trail);
		if (result == null)
		{
			return null;
		}
		result = result.ccopy();
		trail.unwind(oldtop);
		return result;
	}

	/**
	 * Prints out a clause as Head:-Body
	 */
	private String Clause2String(Clause c)
	{
		Term h = c.getHead();
		Term t = c.getBody();
		if (t instanceof Conj)
		{
			return h + ":-" + ((Conj) t).conjToString();
		}
		return h + ":-" + t;
	}
}
