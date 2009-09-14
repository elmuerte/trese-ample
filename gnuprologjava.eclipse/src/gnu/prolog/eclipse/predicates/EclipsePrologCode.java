/* GNU Prolog for Java Eclipse Extensions
 * Copyright (C) 2009  Michiel Hendriks; University of Twente
 *
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
package gnu.prolog.eclipse.predicates;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.JavaObjectTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.Environment;
import gnu.prolog.vm.PrologCode;
import gnu.prolog.vm.PrologException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

/**
 * 
 * 
 * @author Michiel Hendriks
 */
public abstract class EclipsePrologCode implements PrologCode
{
	public static final AtomTerm PROJECT_ATOM = AtomTerm.get("eclipse_project");
	public static final AtomTerm RESOURCE_ATOM = AtomTerm.get("eclipse_resource");

	/**
	 * Get a IProject instance from a term
	 * 
	 * @param term
	 * @return
	 * @throws PrologException
	 */
	public static final IProject getProject(Term term) throws PrologException
	{
		if (!(term instanceof JavaObjectTerm))
		{
			PrologException.typeError(PROJECT_ATOM, term);
		}
		JavaObjectTerm jot = (JavaObjectTerm) term;
		if (!(jot.value instanceof IProject))
		{
			PrologException.typeError(PROJECT_ATOM, term);
		}
		return (IProject) jot.value;
	}

	/**
	 * Get a IProject instance from a term
	 * 
	 * @param term
	 * @return
	 * @throws PrologException
	 */
	public static final IResource getResource(Term term) throws PrologException
	{
		if (!(term instanceof JavaObjectTerm))
		{
			PrologException.typeError(RESOURCE_ATOM, term);
		}
		JavaObjectTerm jot = (JavaObjectTerm) term;
		if (!(jot.value instanceof IProject))
		{
			PrologException.typeError(RESOURCE_ATOM, term);
		}
		return (IProject) jot.value;
	}

	public EclipsePrologCode()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gnu.prolog.vm.PrologCode#install(gnu.prolog.vm.Environment)
	 */
	public void install(Environment env)
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gnu.prolog.vm.PrologCode#uninstall(gnu.prolog.vm.Environment)
	 */
	public void uninstall(Environment env)
	{}
}
