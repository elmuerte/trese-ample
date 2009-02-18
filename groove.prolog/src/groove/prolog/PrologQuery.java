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

import gnu.prolog.database.PrologTextLoaderError;
import gnu.prolog.io.OperatorSet;
import gnu.prolog.io.ParseException;
import gnu.prolog.io.ReadOptions;
import gnu.prolog.io.TermReader;
import gnu.prolog.io.TermWriter;
import gnu.prolog.io.WriteOptions;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;
import gnu.prolog.vm.Interpreter.Goal;
import groove.graph.Graph;

import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public class PrologQuery
{
	public static final String GROOVE_PRO = "/groove/prolog/builtin/groove.pro";

	/**
	 * The graph that will be queried.
	 */
	protected Graph graph;

	/**
	 * Will be true when the interface has been initialized.
	 */
	protected boolean initialized;

	protected GrooveEnvironment env;

	protected Interpreter interpreter;

	public PrologQuery(Graph queryGraph)
	{
		graph = queryGraph;
	}

	protected void init()
	{
		if (initialized)
		{
			return;
		}
		initialized = true;
		env = new GrooveEnvironment();
		env.setGraph(graph);
		CompoundTerm term = new CompoundTerm(AtomTerm.get("resource"), new Term[] { AtomTerm.get(GROOVE_PRO) });
		env.ensureLoaded(term);
		interpreter = env.createInterpreter();
		env.runIntialization(interpreter);
		for (PrologTextLoaderError err : (List<PrologTextLoaderError>) env.getLoadingErrors())
		{
			// TODO: make invalid
			System.err.println(err);
			// err.printStackTrace();
		}
	}

	/**
	 * Execute a prolog query
	 * 
	 * @param term
	 * @return
	 * @throws GroovePrologException
	 */
	public Object query(String term) throws GroovePrologException
	{
		if (!initialized)
		{
			init();
		}
		ReadOptions readOpts = new ReadOptions();
		readOpts.operatorSet = env.getOperatorSet();
		TermReader termReader = new TermReader(new StringReader(term));
		try
		{
			Term goalTerm = termReader.readTermEof(readOpts);
			Goal goal = interpreter.prepareGoal(goalTerm);
			int result;
			do
			{
				result = interpreter.execute(goal);
				TermWriter out = new TermWriter(new OutputStreamWriter(System.out));
				WriteOptions wr_ops = new WriteOptions();
				wr_ops.operatorSet = new OperatorSet();
				Iterator ivars = readOpts.variableNames.keySet().iterator();
				while (ivars.hasNext())
				{
					String name = (String) ivars.next();
					out.print(name + " = ");
					out.print(wr_ops, ((Term) readOpts.variableNames.get(name)).dereference());
					out.print("; ");
					out.println();
				}
				out.println();
				out.flush();
			} while (result != PrologCode.SUCCESS);

			return null;
		}
		catch (ParseException e)
		{
			throw new GroovePrologException(e);
		}
		catch (PrologException e)
		{
			throw new GroovePrologException(e);
		}
	}
}
