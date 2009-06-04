/* !!LICENSE PENDING!!
 *
 * Copyright (C) 2008 University of Twente
 */
package trese.archmodel.dr.ui;

import gnu.prolog.io.WriteOptions;
import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;
import gnu.prolog.vm.TermConstants;

/**
 * @author Michiel Hendriks
 * 
 */
public class Predicate_designrationale_query implements PrologCode
{
	public Predicate_designrationale_query()
	{}

	/*
	 * (non-Javadoc)
	 * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
	 * gnu.prolog.term.Term[])
	 */
	public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args) throws PrologException
	{
		interpreter.environment.getUserOutput().putCodeSequence(null, interpreter, "% Design Rationale Query: ");
		WriteOptions options = new WriteOptions();
		options.quoted = true;
		options.operatorSet = interpreter.environment.getOperatorSet();
		interpreter.environment.getUserOutput().writeTerm(null, interpreter, options, args[0]);
		interpreter.environment.getUserOutput().putCodeSequence(null, interpreter, "\n");
		interpreter.environment.getUserOutput().flushOutput(null);
		return SUCCESS_LAST;
	}

	/*
	 * (non-Javadoc)
	 * @see gnu.prolog.vm.PrologCode#install(gnu.prolog.vm.Environment)
	 */
	public void install(Environment env)
	{}

	/*
	 * (non-Javadoc)
	 * @see gnu.prolog.vm.PrologCode#uninstall(gnu.prolog.vm.Environment)
	 */
	public void uninstall(Environment env)
	{}
}
