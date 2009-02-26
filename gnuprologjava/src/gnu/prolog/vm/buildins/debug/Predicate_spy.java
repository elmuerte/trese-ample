/**
 * 
 */
package gnu.prolog.vm.buildins.debug;

import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.Interpreter;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;

/**
 * Set a trace point
 * 
 * @author Michiel Hendriks
 */
public class Predicate_spy implements PrologCode
{
	public Predicate_spy()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gnu.prolog.vm.PrologCode#execute(gnu.prolog.vm.Interpreter, boolean,
	 * gnu.prolog.term.Term[])
	 */
	public int execute(Interpreter interpreter, boolean backtrackMode, Term[] args) throws PrologException
	{
		// TODO
		return SUCCESS_LAST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gnu.prolog.vm.PrologCode#install(gnu.prolog.vm.Environment)
	 */
	public void install(Environment env)
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gnu.prolog.vm.PrologCode#uninstall(gnu.prolog.vm.Environment)
	 */
	public void uninstall(Environment env)
	{
	}
}
