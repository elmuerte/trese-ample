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

import net.sf.kpex.util.Trail;

/**
 * Top element of the Prolog term hierarchy. Describes a simple or compound ter
 * like: X,a,13,f(X,s(X)),[a,s(X),b,c], a:-b,c(X,X),d, etc.
 */
public abstract class Term implements Cloneable
{
	/**
	 * The arity returned by {@link Const}
	 */
	public final static int ARITY_CONST = 0;
	/**
	 * The arity returned by {@link Int}
	 */
	public final static int ARITY_INT = -2;
	/**
	 * The arity returned by {@link SystemObject}
	 */
	public final static int ARITY_JAVA = -4;
	/**
	 * The arity returned by {@link Real}
	 */
	public final static int ARITY_REAL = -3;
	/**
	 * The arity returned by {@link Var}
	 */
	public final static int ARITY_VAR = -1;

	// FIXME move to utility class
	public static Term fromString(String s)
	{
		return Clause.clauseFromString(s).toTerm();
	}

	/**
	 * Converts a list of character codes to a String.
	 */
	// FIXME move to utility class
	protected static String charsToString(Nonvar Cs)
	{
		StringBuffer s = new StringBuffer("");

		while (!(Cs instanceof Nil))
		{
			if (!(Cs instanceof Cons))
			{
				return null;
			}
			Nonvar head = (Nonvar) ((Cons) Cs).getArg(0);
			if (!(head instanceof Int))
			{
				return null;
			}
			char c = (char) ((Int) head).val;
			s.append(c);
			Cs = (Nonvar) ((Cons) Cs).getArg(1);
		}

		return s.toString();
	}

	// FIXME move to utility class
	static final Nonvar stringToChars(String s)
	{
		if (0 == s.length())
		{
			return Const.aNil;
		}
		Cons l = new Cons(new Int(s.charAt(0)), Const.aNil);
		Cons curr = l;
		for (int i = 1; i < s.length(); i++)
		{
			Cons tail = new Cons(new Int(s.charAt(i)), Const.aNil);
			curr.args[1] = tail;
			curr = tail;
		}
		return l;
	}

	/**
	 * Returns a copy of a term with variables standardized apart (`fresh
	 * variables').
	 */
	synchronized public Term copy()
	{
		return reaction(new Copier());
	}

	/**
	 * @param that
	 * @return True when this term is equal to the other term.
	 */
	// FIXME change to equals method? does it have the same semantics?
	public abstract boolean eq(Term that);

	/**
	 * returns or fakes an arity for all subtypes
	 * 
	 * @see #ARITY_CONST
	 * @see #ARITY_INT
	 * @see #ARITY_JAVA
	 * @see #ARITY_REAL
	 * @see #ARITY_VAR
	 */
	abstract public int getArity();

	/**
	 * Returns a string key used based on the string name of the term. Note that
	 * the key for a clause AL-B,C. is the key instead of ':-'.
	 */
	public String getKey()
	{
		return toString();
	}

	/**
	 * @return True if this is a builtin term (i.e. non user defined)
	 */
	public boolean isBuiltin()
	{
		return false;
	}

	/**
	 * Tests if this term unifies with that. Bindings are trailed and undone
	 * after the test. This should be used with the shared term as this and the
	 * new term as that. Synchronization makes sure that side effects on the
	 * shared term are not interfering, i.e as in:
	 * SHARED.matches(NONSHARED,trail).
	 */
	synchronized public boolean matches(Term that)
	{
		Trail trail = new Trail();
		boolean ok = unify(that, trail);
		trail.unwind(0);
		return ok;
	}

	/**
	 * Returns a copy of the result if the unification of this and that. Side
	 * effects on this and that are undone using trailing of bindings..
	 * Synchronization happens over this, not over that. Make sure it is used as
	 * SHARED.matching_copy(NONSHARED,trail).
	 */
	synchronized public Term matchingCopy(Term that)
	{
		Trail trail = new Trail();
		boolean ok = unify(that, trail);
		// if(ok) that=that.copy();
		if (ok)
		{
			that = copy();
		}
		trail.unwind(0);
		return ok ? that : null;
	}

	/**
	 * Replaces variables with uppercase constants named `V1', 'V2', etc. to be
	 * read back as variables.
	 */
	public Term numberVars()
	{
		return copy().reaction(new VarNumberer());
	}

	/**
	 * Prints out a term to a String with variables named in order V1, V2,....
	 */
	public String prettyPrint()
	{
		return numberVars().toString();
	}

	public String prettyPrint(boolean replaceAnonymous)
	{ // not used
		return prettyPrint();
	}

	/**
	 * Return the reference to the term value (Used by variables)
	 * 
	 * Dereferences if necessary. This should be synchronized otherwise vicious
	 * non-reentrancy problems may occur in the presence of GC and heavy
	 * multi-threading!!!
	 */
	public Term getRef()
	{ // synchronized !!!
		return this;
	}

	/**
	 * @return Convert the term to a sequence of characters
	 */
	public Term toChars()
	{
		return stringToChars(toUnquoted());
	}

	/**
	 * @return Convert the term to a clause
	 */
	public Clause toClause()
	{
		return new Clause(this, Const.aTrue);
	}

	// TODO what does this to
	public Term token()
	{
		return this;
	}

	/**
	 * Java Object wrapper. In particular, it is used to wrap a Thread to hide
	 * it inside a Prolog data object.
	 */
	public Object toObject()
	{
		return getRef();
	}

	/**
	 * Returns an unquoted string representation of this term.
	 */
	public String toUnquoted()
	{
		return prettyPrint();
	}

	/**
	 * Undo the (last) binding
	 */
	public void undoBinding()
	{ // does nothing
	}

	/**
	 * Returns '[]'(V1,V2,..Vn) where Vi is a variable occuring in this Term
	 */
	public Term varsOf()
	{
		return new Copier().getMyVars(this);
	}

	/**
	 * Executed when a builtin is called. Needs to be overriden. Returns a
	 * run-time warning if this is forgotten.
	 */
	protected int exec(Prog p)
	{
		return -1;
	}

	/** Dereference and unify_to */
	protected final boolean unify(Term that, Trail trail)
	{
		return getRef().unifyTo(that.getRef(), trail);
	}

	/**
	 * Identity action.
	 */
	protected Term action(Term that)
	{
		return that;
	}

	/**
	 * @param that
	 * @param trail
	 * @return
	 */
	protected abstract boolean bindTo(Term that, Trail trail);

	/*
	 * Just to catch the frequent error when the arg is forgotten while definig
	 * a builtin. Being final, it will generate a compile time error if this
	 * happens
	 */
	protected final int exec()
	{
		// FIXME: this is a bad way to enforce it
		return -1;
	}

	/**
	 * @return True if this term is a clause
	 */
	protected boolean isClause()
	{
		return false;
	}

	/**
	 * Defines the reaction to an agent recursing over the structure of a term.
	 * <b>This</b> is passed to the agent and the result of the action is
	 * returned. Through overriding, for instance, a Fun term will provide the
	 * recursion over its arguments, by applying the action to each of them.
	 * 
	 * @see Fun
	 */
	protected Term reaction(Term agent)
	{
		Term T = agent.action(this);
		return T;
	}

	/**
	 * @return Converts the Term to a Term.
	 * @see Clause#toTerm()
	 */
	protected Term toTerm()
	{
		return this;
	}

	/**
	 * Unify dereferenced
	 */
	protected abstract boolean unifyTo(Term that, Trail trail);
}
