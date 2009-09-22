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
package gnu.prolog.eclipse;

import gnu.prolog.term.AtomTerm;
import gnu.prolog.term.CompoundTerm;
import gnu.prolog.term.Term;
import gnu.prolog.vm.EnvInitializer;
import gnu.prolog.vm.Environment;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * This EnvInitializer will further delegate initialization to
 * {@link IEnvironmentInitializer} implementations registered through the
 * gnuprologjava.environment.initialization extension point. This is because the
 * service registry doesn't work well with OSGi.
 * 
 * @author Michiel Hendriks
 */
public class EclipseEnvInitializer extends EnvInitializer
{
	public static final String ENV_INIT_EXT_ID = "gnuprologjava.environment.initialization";

	public EclipseEnvInitializer()
	{}

	/*
	 * (non-Javadoc)
	 * 
	 * @see gnu.prolog.vm.EnvInitializer#initialize(gnu.prolog.vm.Environment)
	 */
	@Override
	public void initialize(Environment environment)
	{
		if (environment == null)
		{
			throw new NullPointerException("Environment cannot be null");
		}
		if (Platform.getExtensionRegistry() == null)
		{
			return;
		}
		environment.ensureLoaded(new CompoundTerm(AtomTerm.get("resource"), new Term[] { AtomTerm
				.get("/gnu/prolog/eclipse/eclipse.pro") }));
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(ENV_INIT_EXT_ID);
		for (IConfigurationElement e : config)
		{
			Object o;
			try
			{
				o = e.createExecutableExtension("class");
			}
			catch (CoreException e1)
			{
				e1.printStackTrace();
				continue;
			}
			if (o instanceof IEnvironmentInitializer)
			{
				((IEnvironmentInitializer) o).initialize(environment);
			}
		}
	}
}
