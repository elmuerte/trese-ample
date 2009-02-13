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
package tarau.jinni;

/**
 * Initializes Jinni. Sets up shared data areas. Ensures that lib.class,
 * obtained from lib.pro->lib.java is loaded.
 */
public class Init
{
	public static final int version = 84;

	public static final String getInfo()
	{
		String s = "Kernel Prolog " + version / 100.0 + "\n" + "Copyright (c) Paul Tarau && BinNet Corp. 1999\n"
				+ "Open Source Edition, under GNU General Public License.\n"
				+ "Download latest version from: http://www.binnetcorp.com/kprolog/Main.html\n"
				+ "For commercial licensing, related service or support contracts\n"
				+ "and commercial extensions in binary form, contact BinNet Corporation at:\n"
				+ "binnetcorp@binnetcorp.com, http://www.binnetcorp.com\n";
		return s;
	}

	public static DataBase default_db;
	public static Builtins builtinDict;

	public static Clause getGoal(String line)
	{
		Clause G = Clause.goalFromString(line);
		// IO.mes("getGoal: "+G+" DICT: "+G.dict); //OK
		return G;
	}

	public static void run_query(String query)
	{
		Clause Goal = getGoal(query);
		timeGoal(Goal);
	}

	/**
	 * reads a query from input strea
	 */
	static Clause getGoal()
	{
		return getGoal(IO.promptln("?- "));
	}

	/**
	 * evalutes a query
	 */
	public static void evalGoal(Clause Goal)
	{
		Clause NamedGoal = Goal.cnumbervars(false);
		Term Names = NamedGoal.getHead();
		if (!(Names instanceof Fun))
		{ // no vars in Goal
			Term Result = Prog.firstSolution(Goal.getHead(), Goal.getBody());
			if (!Const.aNo.eq(Result))
			{
				Result = Const.aYes;
			}
			IO.println(Result.toString());
			return;
		}

		Prog E = new Prog(Goal, null);

		for (int i = 0;; i++)
		{
			Term R = Prog.ask_engine(E);
			// IO.mes("GOAL:"+Goal+"\nANSWER: "+R);
			if (R == null)
			{
				IO.println("no");
				break;
			}
			if (Names instanceof Fun)
			{
				Fun NamedR = (Fun) R.numbervars();
				for (int j = 0; j < Names.getArity(); j++)
				{
					IO.println(((Fun) Names).getArg(j) + "=" + NamedR.getArg(j));
				}
				// IO.println(";");
				if (!moreAnswers(i))
				{
					E.stop();
					break;
				}
			}
		}
	}

	static boolean moreAnswers(int i)
	{
		if (IO.maxAnswers == 0)
		{ // under user control
			String more = IO.promptln("; for more, <enter> to stop: ");
			return more.equals(";");
		}
		else if (i < IO.maxAnswers || IO.maxAnswers < 0)
		{
			IO.println(";"); // print all remaining
			return true;
		}
		else
		{ // i >= ...}
			IO.println(";");
			IO.println("No more answers computed, max reached! (" + IO.maxAnswers + ")");
			return false;
		}
	}

	/**
	 * evaluates and times a Goal querying program P
	 */

	public static void timeGoal(Clause Goal)
	{
		long t1 = System.currentTimeMillis();
		try
		{
			evalGoal(Goal);
		}
		catch (Throwable e)
		{
			IO.errmes("Execution error in goal:\n  " + Goal.pprint() + ".\n", e);
		}
		long t2 = System.currentTimeMillis();
		IO.println("Time: " + (t2 - t1) / 1000.0 + " sec, threads=" + Thread.activeCount());
	}

	/**
	 * (almost) standard Prolog-like toplevel in Java (will) print out variables
	 * and values
	 */
	public static void standardTop()
	{
		standardTop("?- ");
	}

	public static void standardTop(String prompt)
	{
		for (;;)
		{
			Clause G = getGoal(IO.promptln(prompt));
			if (null == G)
			{
				continue;
			}
			IO.peer = null;
			timeGoal(G);
		}
	}

	/**
	 * Asks Jinni a query Answer, Goal and returns the first solution of the
	 * form "the(Answer)" or the constant "no" if no solution exists
	 */
	public static Term askJinni(Term Answer, Term Body)
	{
		return Prog.firstSolution(Answer, Body);
	}

	/**
	 * Asks Jinni a query Goal and returns the first solution of the form
	 * "the(Answer)" , where Answer is an instance of Goal or the constant "no"
	 * if no solution exists
	 */
	public static Term askJinni(Term Goal)
	{
		return askJinni(Goal, Goal);
	}

	/**
	 * Asks Jinni a String query and gets back a string Answer of the form
	 * "the('[]'(VarsOfQuery))" containing a binding of the variables or the
	 * first solution to the query or "no" if no such solution exists
	 */
	public static String askJinni(String query)
	{
		Clause Goal = getGoal(query);
		Term Body = Goal.getBody();
		return askJinni(Body).pprint();
	}

	public static boolean run(String[] args)
	{
		if (null != args)
		{
			for (String arg : args)
			{
				String result = askJinni(arg);
				IO.trace(result);
				if ("no".equals(result.intern()))
				{
					IO.errmes("failing cmd line argument: " + arg);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Initialises key data areas. Runs a first query, which, if suceeeds a
	 * true, otherwise false is returned
	 */
	public static final boolean startJinni()
	{
		// should be final for expiration mechanism (it should avoid
		// overriding!)
		IO.println(getInfo());
		default_db = new DataBase();
		return true;
	}

}
