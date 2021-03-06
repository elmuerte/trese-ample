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
 
%
% Debugging extensions
%

% Retrieve all open eclipse projects in the current workspace. This returns
% an eclipse Project instance, not an atom.
% eclipse_project(?Project)
:-build_in(eclipse_project/1,'gnu.prolog.eclipse.predicates.Predicate_project'). 

% Get the name of an eclipse project
% eclipse_project(+Project, ?Name)
:-build_in(eclipse_project_name/2,'gnu.prolog.eclipse.predicates.Predicate_resource_name').

% Get the location of an eclipse project
% eclipse_project(+Project, ?Location)
:-build_in(eclipse_project_location/2,'gnu.prolog.eclipse.predicates.Predicate_resource_location').

% Enumerate the natures associated with the project.
% eclipse_project(+Project, ?NatureId)
:-build_in(eclipse_project_nature/2,'gnu.prolog.eclipse.predicates.Predicate_project_nature').
