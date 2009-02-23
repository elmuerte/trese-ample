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

% Success if the argument is a JavaObjectTerm with a Location
:-build_in(is_ruleevent/1,'groove.prolog.builtin.trans.Predicate_is_ruleevent').

% Success if the argument is a JavaObjectTerm with a Location
:-build_in(is_rulematch/1,'groove.prolog.builtin.trans.Predicate_is_rulematch').

% The label of a rule event
% @param the rule event
% @param the label
% @see groove.trans.RuleEvent#getLabel()
:-build_in(ruleevent_label/2,'groove.prolog.builtin.trans.Predicate_ruleevent_label').

% The rule associated with this event
% @param the rule event
% @param the rule
% @see groove.trans.RuleEvent#getRule()
:-build_in(ruleevent_rule/2,'groove.prolog.builtin.trans.Predicate_ruleevent_rule').

% The rule match
% @param the rule event
% @param the rule match
% @see groove.trans.RuleEvent#getMatch()
:-build_in(ruleevent_match/2,'groove.prolog.builtin.trans.Predicate_ruleevent_match').

