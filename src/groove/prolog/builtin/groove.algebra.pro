% Groove Prolog Interface
% Copyright (C) 2009 Michiel Hendriks, University of Twente
% 
% This library is free software; you can redistribute it and/or
% modify it under the terms of the GNU Lesser General Public
% License as published by the Free Software Foundation; either
% version 2.1 of the License, or (at your option) any later version.
% 
% This library is distributed in the hope that it will be useful,
% but WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
% Lesser General Public License for more details.
% 
% You should have received a copy of the GNU Lesser General Public
% License along with this library; if not, write to the Free Software
% Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

% Succeeds if the given term is a value node
:-build_in(is_valuenode/1,'groove.prolog.builtin.algebra.Predicate_is_valuenode').

% Converts the value node's value to a prolog term. A string value is converted to an 
% AtomicTerm, and integer and double value are converted to a IntegerTerm and FloatTerm
% respectively. All other values are converted to a JavaObjectTerm 
% @param the value node
% @param the term
:-build_in(convert_valuenode/2,'groove.prolog.builtin.algebra.Predicate_convert_valuenode').

% Only convert when it is a value node
try_convert_valuenode(Node,Term):-(is_valuenode(Node) -> convert_valuenode(Node,Term)).
