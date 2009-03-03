/* GNU Prolog for Java
 * Copyright (C) 1997-1999  Constantine Plotnikov
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
package gnu.prolog.vm;
/** prolog code 
  */
public interface PrologCode
{
   /** predicate was returned with success, bactrack info was created, and reexcute is possible. */
   public final static int SUCCESS      = 0;
   /** predicate was returned with success, bactrack info was not created */
   public final static int SUCCESS_LAST = 1;
   /** predicate failed */
   public final static int FAIL         = -1;
   /** returned by the interpreter when it was halted, shown never be returned by prolog code */
   public static final int HALT 				= -2;
   /** this method is used for execution of code
     * @param interpreter interpreter in which context code is executed 
     * @param backtrackMode true if predicate is called on backtracking and false otherwise
     * @param args arguments of code
     * @return either SUCCESS, SUCCESS_LAST, or FAIL.
     */
   public int execute(Interpreter interpreter, boolean backtrackMode, gnu.prolog.term.Term args[]) 
          throws PrologException;

   /** this method is called when code is installed to the environment
     * code can be installed only for one environment.
     * @param environment environemnt to install the predicate
     */
   public void install(Environment env);

   /** this method is called when code is uninstalled from the environment
     * @param environment environemnt to install the predicate
     */
   public void uninstall(Environment env);
     
}
