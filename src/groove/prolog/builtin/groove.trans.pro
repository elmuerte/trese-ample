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

% Success if the argument is a JavaObjectTerm with a RuleEvent
:-build_in(is_ruleevent/1,'groove.prolog.builtin.trans.Predicate_is_ruleevent').

% Success if the argument is a JavaObjectTerm with a RuleMatch
:-build_in(is_rulematch/1,'groove.prolog.builtin.trans.Predicate_is_rulematch').

% Success if the argument is a JavaObjectTerm with a Rule
:-build_in(is_rule/1,'groove.prolog.builtin.trans.Predicate_is_rule').

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
% @param the graph to match against
% @param the rule match
% @see groove.trans.RuleEvent#getMatch()
:-build_in(ruleevent_match/3,'groove.prolog.builtin.trans.Predicate_ruleevent_match').
ruleevent_match(RE,RM):-graphstate(GS),graphstate_graph(GS,G),ruleevent_match(RE,G,RM).

% Get all current rule matches
rulematch(RM):-gts(GTS),graphstate(GS),graphstate_graph(GS,G),gts_match(GTS,GS,RE),ruleevent_match(RE,G,RM).

% The edges in a rule match
% @param the rulematch
% @param the edge in the match
:-build_in(rulematch_edge/2,'groove.prolog.builtin.trans.Predicate_rulematch_edge').

% The nodes in a rule match
% @param the rulematch
% @param the node in the match
:-build_in(rulematch_node/2,'groove.prolog.builtin.trans.Predicate_rulematch_node').

% The rule which was used in this match
% @param the rulematch
% @param the rule
:-build_in(rulematch_rule/2,'groove.prolog.builtin.trans.Predicate_rulematch_rule').
