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

import net.sf.kpex.io.CharReader;
import net.sf.kpex.io.CharWriter;
import net.sf.kpex.io.ClauseReader;
import net.sf.kpex.io.ClauseWriter;
import net.sf.kpex.io.IO;
import net.sf.kpex.prolog.Clause;
import net.sf.kpex.prolog.Cons;
import net.sf.kpex.prolog.Const;
import net.sf.kpex.prolog.ConstBuiltin;
import net.sf.kpex.prolog.Fluent;
import net.sf.kpex.prolog.Fun;
import net.sf.kpex.prolog.FunBuiltin;
import net.sf.kpex.prolog.Int;
import net.sf.kpex.prolog.IntegerSource;
import net.sf.kpex.prolog.JavaObject;
import net.sf.kpex.prolog.JavaSource;
import net.sf.kpex.prolog.LazyList;
import net.sf.kpex.prolog.ListSource;
import net.sf.kpex.prolog.MultiVar;
import net.sf.kpex.prolog.Nonvar;
import net.sf.kpex.prolog.Num;
import net.sf.kpex.prolog.Prog;
import net.sf.kpex.prolog.Real;
import net.sf.kpex.prolog.Sink;
import net.sf.kpex.prolog.Source;
import net.sf.kpex.prolog.SourceLoop;
import net.sf.kpex.prolog.SourceMerger;
import net.sf.kpex.prolog.StringSink;
import net.sf.kpex.prolog.Term;
import net.sf.kpex.prolog.TermCollector;
import net.sf.kpex.prolog.TermSource;
import net.sf.kpex.prolog.Unfolder;
import net.sf.kpex.prolog.Var;
import net.sf.kpex.util.HashDict;
import net.sf.kpex.util.Trail;

/**
 * This class contains a dictionary of all builtins i.e. Java based classes
 * callable from Prolog. They should provide a constructor and an exec method.
 * 
 * @author Paul Tarau
 */
public class Builtins extends HashDict
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8061409367924631427L;

	/**
	 * This constructor registers builtins. Please put a header here if you add
	 * a builtin at the bottom of this file.
	 */
	public Builtins()
	{
		// add a line here for each new builtin
		// basics
		register(new is_builtin());
		register(Const.TRUE);
		register(Const.FAIL);
		register(new halt());
		register(new compute());

		// I/O and trace related
		register(new get_stdin());
		register(new get_stdout());
		register(new set_max_answers());
		register(new set_trace());
		register(new stack_dump());
		register(new consult());
		register(new reconsult());
		register(new reconsult_again());

		// database		register(new at_key());
		register(new pred_to_string());
		register(new db_to_string());

		register(new new_db());
		register(new get_default_db());
		register(new db_remove());
		register(new db_add());
		register(new db_collect());
		register(new db_source());

		// data structure builders/converters
		register(new arg());
		register(new new_fun());
		register(new get_arity());
		register(new name_to_chars());
		register(new chars_to_name());
		register(new numbervars());

		// fluent constructors
		register(new unfolder_source());
		register(new answer_source());

		register(new source_list());
		register(new list_source());

		register(new term_source());
		register(new source_term());

		register(new integer_source());
		register(new source_loop());

		// Fluent Modifiers

		register(new set_persistent());
		register(new get_persistent());

		// Input Sources
		register(new file_char_reader());
		register(new char_file_writer());

		register(new file_clause_reader());
		register(new clause_file_writer());

		// writable Sinks
		register(new term_string_collector());
		register(new term_collector());

		register(new string_char_reader());
		register(new string_clause_reader());

		// fluent controllers
		register(new get());
		register(new put());
		register(new stop());
		register(new collect());

		// fluent combinators
		register(new split_source());
		register(new merge_sources());
		// see compose_sources,append_sources,merge_sources in lib.pro
		// discharges a Source to a Sink
		register(new discharge());

		// multi-var operations		register(new def());
		register(new set());
		register(new val());

		// lazy list operations		register(new source_lazy_list());
		register(new lazy_head());
		register(new lazy_tail());

		// OS and process interface
		register(new system());
		register(new ctime());
	}

	/**
	 * registers a symbol as name of a builtin
	 */
	public void register(Const proto)
	{
		String key = proto.name() + "/" + proto.getArity();
		// IO.mes("registering builtin: "+key);
		put(key, proto);
	}

	/**
	 * Creates a new builtin
	 */
	public Const newBuiltin(Const S)
	{
		String name = S.name();
		int arity = S.getArity();
		String key = name + "/" + arity;
		Const b = (Const) get(key);
		return b;
	}
} // end Builtins

// Code for actual kernel Builtins:
// add your own builtins in UserBuiltins.java, by cloning the closest example:-)

/**
 * checks if something is a builtin
 */
class is_builtin extends FunBuiltin
{
	is_builtin()
	{
		super("is_builtin", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return getArg(0).isBuiltin() ? 1 : 0;
	}
}

/**
 * does its best to halt the program:-) to be thoroughly tested with Applets
 */
class halt extends ConstBuiltin
{
	halt()
	{
		super("halt");
	}

	@Override
	public int exec(Prog p)
	{
		if (IO.applet != null)
		{ // applet
			IO.peer.halt();
			p.stop();
		}
		else
		{
			Runtime.getRuntime().exit(0);
		}
		return 1;
	}
}

/**
 * Calls an external program
 */
class system extends FunBuiltin
{
	system()
	{
		super("system", 1);
	}

	@Override
	public int exec(Prog p)
	{
		String cmd = ((Const) getArg(0)).name();
		return IO.system(cmd);
	}
}

/**
 * opens a reader returning the content of a file char by char
 */
class file_char_reader extends FunBuiltin
{
	file_char_reader()
	{
		super("file_char_reader", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Term I = getArg(0);
		Fluent f;
		if (I instanceof CharReader)
		{
			f = new CharReader(((CharReader) I), p);
		}
		else
		{
			String s = ((Const) I).name();
			f = new CharReader(s, p);
		}
		return putArg(1, f, p);
	}
}

/**
 * opens a reader returning clauses from a file
 */
class file_clause_reader extends FunBuiltin
{
	file_clause_reader()
	{
		super("file_clause_reader", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Term I = getArg(0);
		Fluent f;
		if (I instanceof CharReader)
		{
			f = new ClauseReader((I), p);
		}
		else
		{
			String s = ((Const) getArg(0)).name();
			f = new ClauseReader(s, p);
		}
		return putArg(1, f, p);
	}
}

/**
 * opens a writer which puts characters to a file one by one
 */
class char_file_writer extends FunBuiltin
{
	char_file_writer()
	{
		super("char_file_writer", 2);
	}

	@Override
	public int exec(Prog p)
	{
		String s = ((Const) getArg(0)).name();
		Fluent f = new CharWriter(s, p);
		return putArg(1, f, p);
	}
}

/**
 * opens a writer which puts characters to a file one by one
 */
class clause_file_writer extends FunBuiltin
{
	clause_file_writer()
	{
		super("clause_file_writer", 2);
	}

	@Override
	public int exec(Prog p)
	{
		String s = ((Const) getArg(0)).name();
		Fluent f = new ClauseWriter(s, p);
		return putArg(1, f, p);
	}
}

/**
 * get the standard output (a reader)
 */
class get_stdin extends FunBuiltin
{
	get_stdin()
	{
		super("get_stdin", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new ClauseReader(p), p);
	}
}

/**
 * get standard output (a writer)
 */
class get_stdout extends FunBuiltin
{
	get_stdout()
	{
		super("get_stdout", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new ClauseWriter(p), p);
	}
}

/**
 * gets an arity for any term: n>0 for f(A1,...,An) 0 for a constant like a -1
 * for a variable like X -2 for an integer like 13 -3 for real like 3.14 -4 for
 * a wrapped JavaObject;
 * 
 * @see Term#getArity
 */
class get_arity extends FunBuiltin
{
	get_arity()
	{
		super("get_arity", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Int N = new Int(getArg(0).getArity());
		return putArg(1, N, p);
	}
}

/**
 * Dumps the current Java Stack
 */
class stack_dump extends FunBuiltin
{

	stack_dump()
	{
		super("stack_dump", 1);
	}

	@Override
	public int exec(Prog p)
	{
		String s = getArg(0).toString();
		IO.errmes("User requested dump", new Exception(s));
		return 1;
	}
}

/**
 * returns the real time spent up to now
 */
class ctime extends FunBuiltin
{

	ctime()
	{
		super("ctime", 1);
	}

	private final static long t0 = System.currentTimeMillis();

	@Override
	public int exec(Prog p)
	{
		Term T = new Int(System.currentTimeMillis() - t0);
		return putArg(0, T, p);
	}
}

/**
 * sets max answer counter for toplevel query if == 0, it will prompt the user
 * for more answers if > 0 it will not print more than IO.maxAnswers if < 0 it
 * will print them out all
 */
class set_max_answers extends FunBuiltin
{
	set_max_answers()
	{
		super("set_max_answers", 1);
	}

	@Override
	public int exec(Prog p)
	{
		IO.maxAnswers = getIntArg(0);
		return 1;
	}
}

/**
 * reconsults a file of clauses while overwriting old predicate definitions
 * 
 * @see consult
 */

class reconsult extends FunBuiltin
{
	reconsult()
	{
		super("reconsult", 1);
	}

	@Override
	public int exec(Prog p)
	{
		String f = ((Const) getArg(0)).name();
		return DataBase.fromFile(f) ? 1 : 0;
	}
}

/**
 * consults a file of clauses while adding clauses to existing predicate
 * definitions
 * 
 * @see reconsult
 */
class consult extends FunBuiltin
{
	consult()
	{
		super("consult", 1);
	}

	@Override
	public int exec(Prog p)
	{
		String f = ((Const) getArg(0)).name();
		IO.trace("consulting: " + f);
		return DataBase.fromFile(f, false) ? 1 : 0;
	}
}

/**
 * shorthand for reconsulting the last file
 */
class reconsult_again extends ConstBuiltin
{
	reconsult_again()
	{
		super("reconsult_again");
	}

	@Override
	public int exec(Prog p)
	{
		return DataBase.fromFile() ? 1 : 0;
	}
}

/**
 * gets default database
 */
class get_default_db extends FunBuiltin
{
	get_default_db()
	{
		super("get_default_db", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new JavaObject(p.getDatabase()), p);
	}
}

// databse operations

/**
 * creates new database
 */
class new_db extends FunBuiltin
{
	new_db()
	{
		super("new_db", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new JavaObject(new DataBase()), p);
	}
}

/**
 * Puts a term on the local blackboard
 */
class db_add extends FunBuiltin
{

	db_add()
	{
		super("db_add", 2);
	}

	@Override
	public int exec(Prog p)
	{
		DataBase db = (DataBase) ((JavaObject) getArg(0)).toObject();
		Term X = getArg(1);
		// IO.mes("X==>"+X);
		String key = X.getKey();
		// IO.mes("key==>"+key);
		if (null == key)
		{
			return 0;
		}
		db.out(key, X);
		// IO.mes("res==>"+R);
		return 1;
	}
}

/**
 * removes a matching term if available, fails otherwise
 */
class db_remove extends FunBuiltin
{

	db_remove()
	{
		super("db_remove", 3);
	}

	@Override
	public int exec(Prog p)
	{
		DataBase db = (DataBase) ((JavaObject) getArg(0)).toObject();
		Term X = getArg(1);
		Term R = db.cin(X.getKey(), X);
		return putArg(2, R, p);
	}
}

/**
 * collects all matching terms in a (possibly empty) list
 * 
 * @see out
 * @see in
 */
class db_collect extends FunBuiltin
{

	db_collect()
	{
		super("db_collect", 3);
	}

	@Override
	public int exec(Prog p)
	{
		DataBase db = (DataBase) ((JavaObject) getArg(0)).toObject();
		Term X = getArg(1);
		Term R = db.all(X.getKey(), X);
		return putArg(2, R, p);
	}
}

/**
 * Maps a DataBase to a Source enumerating its elements
 */
class db_source extends FunBuiltin
{

	db_source()
	{
		super("db_source", 2);
	}

	@Override
	public int exec(Prog p)
	{
		DataBase db = (DataBase) ((JavaObject) getArg(0)).toObject();
		Source S = new JavaSource(db.toEnumeration(), p);
		return putArg(1, S, p);
	}
}

/**
 * collects all matching terms in a (possibly empty) list
 */
class at_key extends FunBuiltin
{

	at_key()
	{
		super("at_key", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Term R = p.getDatabase().all(getArg(0).getKey(), new Var());
		return putArg(1, R, p);
	}
}

/**
 * Returns a representation of predicate as a string constant
 */
class pred_to_string extends FunBuiltin
{

	pred_to_string()
	{
		super("pred_to_string", 2);
	}

	@Override
	public int exec(Prog p)
	{
		String key = getArg(0).getKey();
		String listing = p.getDatabase().pred_to_string(key);
		if (null == listing)
		{
			return 0;
		}
		Const R = new Const(listing);
		return putArg(1, R, p);
	}
}

/**
 * lists all the local blackboard to a string (Linda terms + clauses)
 */
class db_to_string extends FunBuiltin
{
	db_to_string()
	{
		super("db_to_string", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new Const(p.getDatabase().prettyPrint()), p);
	}
}

/**
 * arg(I,Term,X) unifies X with the I-the argument of functor T
 */
class arg extends FunBuiltin
{
	arg()
	{
		super("arg", 3);
	}

	@Override
	public int exec(Prog p)
	{
		int i = getIntArg(0);
		Fun F = (Fun) getArg(1);
		Term A = i == 0 ? new Const(F.name()) : i == -1 ? new Int(F.getArity()) : F.args[i - 1];
		return putArg(2, A, p);
	}
}

/**
 * new_fun(F,N,T) creates a term T based on functor F with arity N and new free
 * varables as arguments
 */
class new_fun extends FunBuiltin
{
	new_fun()
	{
		super("new_fun", 3);
	}

	@Override
	public int exec(Prog p)
	{
		String s = ((Const) getArg(0)).name();
		int i = getIntArg(1);
		Term T;
		if (i == 0)
		{
			T = new Const(s).toBuiltin();
		}
		else
		{
			Fun F = new Fun(s);
			F.init(i);
			T = F.toBuiltin();
		}
		return putArg(2, T, p);
	}
}

/**
 * converts a name to a list of chars
 */
class name_to_chars extends FunBuiltin
{
	name_to_chars()
	{
		super("name_to_chars", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Term Cs = ((Nonvar) getArg(0)).toChars();
		return putArg(1, Cs, p);
	}
}

/**
 * converts a name to a list of chars
 */
class chars_to_name extends FunBuiltin
{
	chars_to_name()
	{
		super("chars_to_name", 3);
	}

	@Override
	public int exec(Prog p)
	{
		int convert = getIntArg(0);
		String s = charsToString((Nonvar) getArg(1));
		Nonvar T = new Const(s);
		if (convert > 0)
		{
			try
			{
				double r = Double.valueOf(s).doubleValue();
				if (Math.floor(r) == r)
				{
					T = new Int((long) r);
				}
				else
				{
					T = new Real(r);
				}
			}
			catch (NumberFormatException e)
			{}
		}
		return putArg(2, T, p);
	}
}

/**
 * returns a copy of a Term with variables uniformly replaced with constants
 */
class numbervars extends FunBuiltin
{
	numbervars()
	{
		super("numbervars", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Term T = getArg(0).numberVars();
		return putArg(1, T, p);
	}
}

/**
 * Performs simple arithmetic operations like compute('+',1,2,Result)
 */
class compute extends FunBuiltin
{
	compute()
	{
		super("compute", 4);
	}

	@Override
	public int exec(Prog p)
	{

		Term o = getArg(0);
		Term a = getArg(1);
		Term b = getArg(2);
		if (!(o instanceof Const) || !(a instanceof Num) || !(b instanceof Num))
		{
			IO.errmes("bad arithmetic operation (" + o + "): " + a + "," + b + "\nprog: " + p.toString());
		}
		String opname = ((Const) o).name();
		double x = ((Num) a).getValue();
		double y = ((Num) b).getValue();
		double r;
		char op = opname.charAt(0);
		switch (op)
		{
			case '+':
				r = x + y;
				break;
			case '-':
				r = x - y;
				break;
			case '*':
				r = x * y;
				break;
			case '/':
				r = x / y;
				break;
			case ':':
				r = (int) (x / y);
				break;
			case '%':
				r = x % y;
				break;
			case '?':
				r = x < y ? -1 : x == y ? 0 : 1;
				break; // compares!
			case 'p':
				r = Math.pow(x, y);
				break;
			case 'l':
				r = Math.log(y) / Math.log(x);
				break;
			case 'r':
				r = x * Math.random() + y;
				break;
			case '<':
				r = (long) x << (long) y;
				break;
			case '>':
				r = (long) x >> (long) y;
				break;

			default:
				IO.errmes("bad arithmetic operation <" + op + "> on " + x + " and " + y);
				return 0;
		}
		Num R = Math.floor(r) == r ? (Num) new Int((long) r) : (Num) new Real(r);
		return putArg(3, R, p);
	}
}

/**
 * controls trace levels for debugging
 */
class set_trace extends FunBuiltin
{
	set_trace()
	{
		super("set_trace", 1);
	}

	@Override
	public int exec(Prog p)
	{
		Prog.tracing = getIntArg(0);
		return 1;
	}
}

/**
 * Explores a finite iterator and return its successive values as a list.
 */

class source_list extends FunBuiltin
{
	source_list()
	{
		super("source_list", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Source S = (Source) getArg(0);
		Term Xs = S.toList();
		return putArg(1, Xs, p);
	}
}

/*
 * maps a Source to a Java Enumeration class JinniEnumeration extends
 * SystemObject implements Enumeration { JinniEnumeration(Source I) { this.I=I;
 * this.current=this.I.getElement(); } private Source I; private Term current;
 * public boolean hasMoreElements() { if(null==current) { I=null; return false;
 * } return true; } public Object nextElement() { Term next=current;
 * current=I.getElement(); return next; } }
 */

/**
 * maps a List to a Source
 */
class list_source extends FunBuiltin
{

	list_source()
	{
		super("list_source", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Source E = new ListSource((Const) getArg(0), p);
		return putArg(1, E, p);
	}
}

/**
 * maps a Term to a Source
 */
class term_source extends FunBuiltin
{

	term_source()
	{
		super("term_source", 2);
	}

	@Override
	public int exec(Prog p)
	{
		TermSource E = new TermSource((Nonvar) getArg(0), p);
		return putArg(1, E, p);
	}
}

/**
 * Creates an Integer Source which advances at most Fuel (infinite if Fule==0)
 * Steps computing each time x:= a*x+b. Called as:
 * integer_source(Fuel,A,X,B,NewSource)
 */
class integer_source extends FunBuiltin
{

	integer_source()
	{
		super("integer_source", 5);
	}

	@Override
	public int exec(Prog p)
	{
		IntegerSource E = new IntegerSource(((Int) getArg(0)).longValue(), ((Int) getArg(1)).longValue(),
				((Int) getArg(2)).longValue(), ((Int) getArg(3)).longValue(), p);
		return putArg(4, E, p);
	}
}

/**
 * Builds a Looping Source from a Source.
 */
class source_loop extends FunBuiltin
{
	source_loop()
	{
		super("source_loop", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Source s = (Source) getArg(0);
		return putArg(1, new SourceLoop(s, p), p);
	}
}

/**
 * Builds a Source from a Term
 */
class source_term extends FunBuiltin
{

	source_term()
	{
		super("source_term", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Source S = (Source) getArg(0);
		Term Xs = ((Const) S.toFun()).toBuiltin();
		return putArg(1, Xs, p);
	}
}

// Solvers and iterators over clauses

/**
 * When called as answer_source(X,G,R), it builds a new clause and maps it to an
 * AnswerSource LD-resolution interpreter which will return one answer at a time
 * of the form "the(X)" using G as initial resolvent and "no" when no more
 * answers are available.
 */
class answer_source extends FunBuiltin
{
	answer_source()
	{
		super("answer_source", 3);
	}

	@Override
	public int exec(Prog p)
	{
		Clause goal = new Clause(getArg(0), getArg(1));
		Prog U = new Prog(goal, p);
		return putArg(2, U, p);
	}
}

/**
 * Builds a new clause H:-B and maps it to an iterator
 */
class unfolder_source extends FunBuiltin
{
	unfolder_source()
	{
		super("unfolder_source", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Clause goal = getArg(0).toClause();
		Prog newp = new Prog(goal, p);
		Unfolder S = new Unfolder(goal, newp);
		return putArg(1, S, p);
	}
}

/**
 * generic Source advancement step, similar to an iterator's nextElement
 * operation, gets one element from the Source
 */

class get extends FunBuiltin
{
	get()
	{
		super("get", 2);
	}

	@Override
	public int exec(Prog p)
	{
		// IO.mes("<<"+getArg(0)+"\n"+p+p.getTrail().pprint());
		Source S = (Source) getArg(0);
		Term A = Const.the(S.getElement());
		// if(null==A) A=Const.NO;
		// else A=new Fun("the",A);
		// IO.mes(">>"+A+"\n"+p+p.getTrail().pprint());
		return putArg(1, A, p);
	}
}

/**
 * generic Sink advancement step, sends one element to the Sink
 */

class put extends FunBuiltin
{
	put()
	{
		super("put", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Sink S = (Sink) getArg(0);
		Term X = getArg(1);
		if (0 == S.putElement(X))
		{
			IO.errmes("error in putElement: " + X);
		}
		return 1;
	}
}

/**
 * frees a Fluent's resources and ensures it cannot produce/consume any new
 * values
 */
class stop extends FunBuiltin
{
	stop()
	{
		super("stop", 1);
	}

	@Override
	public int exec(Prog p)
	{
		Fluent S = (Fluent) getArg(0);
		S.stop();
		return 1;
	}
}

/**
 * Splits a (finite) Source in two new ones which inherit the current state of
 * the parent.
 */
class split_source extends FunBuiltin
{
	split_source()
	{
		super("split_source", 3);
	}

	@Override
	public int exec(Prog p)
	{
		Source original = (Source) getArg(0);
		Const Xs = original.toList();
		return putArg(1, new ListSource(Xs, p), p) > 0 && putArg(2, new ListSource(Xs, p), p) > 0 ? 1 : 0;
	}
}

/**
 * Merges all Sources contained in a List into one Source.
 */
class merge_sources extends FunBuiltin
{
	merge_sources()
	{
		super("merge_sources", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Const list = (Const) getArg(0);
		return putArg(1, new SourceMerger(list, p), p);
	}
}

/**
 * Flushes to a Sink the content of a Source Fluent
 */
class discharge extends FunBuiltin
{
	discharge()
	{
		super("discharge", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Source from = (Source) getArg(0);
		Sink to = (Sink) getArg(1);
		for (;;)
		{
			Term X = from.getElement();
			if (null == X)
			{
				to.stop();
				break;
			}
			else
			{
				to.putElement(X);
			}
		}
		return 1;
	}
}

/**
 * Collects a reference to or the content of a Sink
 */

class collect extends FunBuiltin
{
	collect()
	{
		super("collect", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Sink s = (Sink) getArg(0);
		Term X = s.collect();
		if (null == X)
		{
			X = Const.NO;
		}
		else
		{
			X = new Fun("the", X);
		}
		return putArg(1, X, p);
	}
}

/**
 * Builds a StringSink which concatenates String representations of Terms with
 * put/1 and the return their concatenation with collect/1
 */
class term_string_collector extends FunBuiltin
{
	term_string_collector()
	{
		super("term_string_collector", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new StringSink(p), p);
	}
}

/**
 * Builds a TermCollector Sink which accumulates Terms with put/1 and the return
 * them with collect/1
 */
class term_collector extends FunBuiltin
{
	term_collector()
	{
		super("term_collector", 1);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(0, new TermCollector(p), p);
	}
}

/**
 * Creates a char reader from a String.
 */
class string_char_reader extends FunBuiltin
{
	string_char_reader()
	{
		super("string_char_reader", 2);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(1, new CharReader(getArg(0), p), p);
	}
}

/**
 * Creates a clause reader from a String.
 */
class string_clause_reader extends FunBuiltin
{
	string_clause_reader()
	{
		super("string_clause_reader", 2);
	}

	@Override
	public int exec(Prog p)
	{
		return putArg(1, new ClauseReader(getArg(0), p), p);
	}
}

/**
 * def(Var,Val) Initializes a Multi_Variable Var to a value Val.
 */
class def extends FunBuiltin
{
	def()
	{
		super("def", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Var X = (Var) getArg(0);
		MultiVar V = new MultiVar(getArg(1), p);
		X.bindTo(V, p.getTrail());
		return 1;
	}
}

/**
 * set(Var,Val) Sets a Multi_Variable Var to a value Val.
 */
class set extends FunBuiltin
{
	set()
	{
		super("set", 2);
	}

	@Override
	public int exec(Prog p)
	{
		MultiVar V = (MultiVar) getArg(0);
		V.set(getArg(1), p);
		return 1;
	}
}

/**
 * val(Var,Val) gets the value Val of Multi_Variable Var.
 */
class val extends FunBuiltin
{
	val()
	{
		super("val", 2);
	}

	@Override
	public int exec(Prog p)
	{
		MultiVar V = (MultiVar) getArg(0);
		return putArg(1, V.val(), p);
	}
}

/**
 * set_persistent(Fluent,yes) makes a Fluent persistent - i.e. likely to keep
 * its state on backtracking. This assumes that the Fluent remains accessible by
 * being saved in a Database or as element of a Fluent with longer life span.
 * 
 * set_persistent(Fluent,no) makes the Fluent perish on backtracking (default
 * behavior)
 */
class set_persistent extends FunBuiltin
{
	set_persistent()
	{
		super("set_persistent", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Fluent F = (Fluent) getArg(0);
		Const R = (Const) getArg(1);
		boolean yesno = !R.eq(Const.NO);
		F.setPersistent(yesno);
		return 1;
	}
}

/**
 * Gets the yes/no persistentcy value of a Fluent.
 */
class get_persistent extends FunBuiltin
{
	get_persistent()
	{
		super("get_persistent", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Fluent F = (Fluent) getArg(0);
		Term R = F.isPersistent() ? Const.YES : Const.NO;
		return putArg(1, R, p);
	}
}

/**
 * Converts Source into a Lazy List which will memorize its elements as it
 * grows.
 */
class source_lazy_list extends FunBuiltin
{
	source_lazy_list()
	{
		super("source_lazy_list", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Source S = (Source) getArg(0);
		// S.setPersistent(true);
		Term X = S.getElement();
		Term Xs = Const.NIL;
		if (null != X)
		{
			Xs = new LazyList(X, S, new Trail());
			p.getTrail().push(Xs);
		}
		return putArg(1, Xs, p);
	}
}

/**
 * returns the first element of a lazy list
 */
class lazy_head extends FunBuiltin
{
	lazy_head()
	{
		super("lazy_head", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Cons L = (Cons) getArg(0);
		return putArg(1, L.getHead(), p);
	}
}

/**
 * returns the tail if a lazy list after making it grow, if possible
 */
class lazy_tail extends FunBuiltin
{
	lazy_tail()
	{
		super("lazy_tail", 2);
	}

	@Override
	public int exec(Prog p)
	{
		Cons L = (Cons) getArg(0);
		return putArg(1, L.getTail(), p);
	}
}
