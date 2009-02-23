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

% Fail if the first argument is not a Groove Graph
:-build_in(is_graph/1,'groove.prolog.builtin.graph.Predicate_is_graph').

% Fail if the first argument is not a Groove Node
:-build_in(is_node/1,'groove.prolog.builtin.graph.Predicate_is_node').

% Fail if the first argument is not a Groove Edge
:-build_in(is_edge/1,'groove.prolog.builtin.graph.Predicate_is_edge').

% Retrieve the current graph
% @param the graph
:-build_in(graph/1,'groove.prolog.builtin.graph.Predicate_graph').

% Get a node from the graph
% @param the graph
% @param the node
% @see groove.graph.GraphShape#nodeSet()
:-build_in(graph_node/2,'groove.prolog.builtin.graph.Predicate_graph_node').
graph_node(N):-graph(G),graph_node(G,N).

% Get the complete node set of the graph
% @param the graph
% @param the list of nodes
% @see groove.graph.GraphShape#nodeSet()
:-build_in(graph_node_set/2,'groove.prolog.builtin.graph.Predicate_graph_node_set').
graph_node_set(N):-graph(G),graph_node_set(G,N).

% Get the number of nodes in the graph
% @param the graph
% @param the number of nodes
% @see groove.graph.GraphShape#nodeCount()
:-build_in(graph_node_count/2,'groove.prolog.builtin.graph.Predicate_graph_node_count').
graph_node_count(N):-graph(G),graph_node_count(G,N).

% Get a edge from a graph
% @param the graph
% @param the edge
% @see groove.graph.GraphShape#edgeSet()
:-build_in(graph_edge/2,'groove.prolog.builtin.graph.Predicate_graph_edge').
graph_edge(E):-graph(G),graph_edge(G,E).

% Get a set of edges from either a graph
% @param the graph 
% @param the list of edges
% @see groove.graph.GraphShape#edgeSet()
:-build_in(graph_edge_set/2,'groove.prolog.builtin.graph.Predicate_graph_edge_set').
graph_edge_set(E):-graph(G),graph_edge_set(G,E).

% Get the number of edges in a graph
% @param the graph
% @param the number of edges
% @see groove.graph.GraphShape#edgeCount
:-build_in(graph_edge_count/2,'groove.prolog.builtin.graph.Predicate_graph_edge_count').
graph_edge_count(E):-graph(G),graph_edge_count(G,E).

% Get an edge from a node, can be incoming or outgoing
% @param the graph
% @param the node
% @param the edge
% @see groove.graph.GraphShape#edgeSet(Node,int)
:-build_in(node_edge/3,'groove.prolog.builtin.graph.Predicate_node_edge').
node_edge(N,E):-graph(G),node_edge(G,N,E).

% Get the set of edges for a single node. Both incoming and outgoing edges.
% @param the graph
% @param the node
% @param the list of edges
% @see groove.graph.GraphShape#edgeSet(Node,int)
:-build_in(node_edge_set__/3,'groove.prolog.builtin.graph.Predicate_node_edge_set').
node_edge_set(N,E):-graph(G),node_edge_set__(G,N,E).

% Get a certain set of edges for a node
% @param the graph
% @param the node 
% @param the position of the edge (integer)
% @param the list of edges
% @see groove.graph.GraphShape#edgeSet(Node,int)
:-build_in(node_edge_set/4,'groove.prolog.builtin.graph.Predicate_node_edge_set_pos').

% Call either node_edge_set__/3 or node_edge_set/4 depending on the first argument
node_edge_set(GN,A1,A2):-
	is_node(GN), graph(G), call(node_edge_set(G,GN,A1,A2)).
node_edge_set(GN,A1,A2):-
	call(node_edge_set__(GN,A1,A2)).

% Get an outgoing edge from a node
% @param the graph
% @param the node
% @param list of outgoing edges
% @see groove.graph.GraphShape#outEdgeSet(Node)
:-build_in(node_out_edge/3,'groove.prolog.builtin.graph.Predicate_node_out_edge').
node_out_edge(N,E):-graph(G),node_out_edge(G,N,E).

% Get the outgoing edges for a given node
% @param the graph
% @param the node
% @param list of outgoing edges
% @see groove.graph.GraphShape#outEdgeSet(Node)
:-build_in(node_out_edge_set/3,'groove.prolog.builtin.graph.Predicate_node_out_edge_set').
node_out_edge_set(N,E):-graph(G),node_out_edge_set(G,N,E).

% Get an edge with a given label
% @param the graph
% @param the label
% @param the list of edges
% @see groove.graph.GraphShape#labelEdgeSet(int,Label)
:-build_in(label_edge/3,'groove.prolog.builtin.graph.Predicate_label_edge').
label_edge(L,E):-graph(G),label_edge(G,L,E).

% Get the edge set of a graph with a given label
% @param the graph
% @param the label
% @param the list of edges
% @see groove.graph.GraphShape#labelEdgeSet(int,Label)
:-build_in(label_edge_set/3,'groove.prolog.builtin.graph.Predicate_label_edge_set').
label_edge_set(L,E):-graph(G),label_edge_set(G,L,E).

% Will be success when the edge has the node as an end point. Fail otherwise
% @param the edge
% @param the node
% @see groove.graph.Edge#hasEnd(Node)
:-build_in(edge_end/2,'groove.prolog.builtin.graph.Predicate_edge_end').

% Get the node of a given end
% @param the edge
% @param the node
% @param the position
% @see groove.graph.Edge#end(int)
% @see groove.graph.Edge#endIndex(Node)
:-build_in(edge_end/3,'groove.prolog.builtin.graph.Predicate_edge_end').

% Get all ends for an edge (usually 2)
% @param the edge
% @param the set of nodes
% @see groove.graph.Edge#ends()
:-build_in(edge_end_set/2,'groove.prolog.builtin.graph.Predicate_edge_end_set').

% Get the number of ends for an edge (usually 2)
% @param the edge
% @param the the number of ends
% @see groove.graph.Edge#ends()
:-build_in(edge_end_count/2,'groove.prolog.builtin.graph.Predicate_edge_end_count').

% Get the source node of an edge
% @param the edge
% @param the node
% @see groove.graph.Edge#source()
:-build_in(edge_source/2,'groove.prolog.builtin.graph.Predicate_edge_sounce').

% Get the destination node of an edge (opposite of the source)
% @param the edge
% @param the node
% @see groove.graph.Edge#opposite()
:-build_in(edge_opposite/2,'groove.prolog.builtin.graph.Predicate_edge_opposite').

% Get the label of the edge
% @param the edge
% @param the label/Atom
% @see groove.graph.Edge#label()
:-build_in(edge_label/2,'groove.prolog.builtin.graph.Predicate_edge_label').

% Success if the argument is a JavaObjectTerm with a GraphState
:-build_in(is_graphstate/1,'groove.prolog.builtin.lts.Predicate_is_graphstate').

% Success if the argument is a JavaObjectTerm with a Transition
:-build_in(is_transition/1,'groove.prolog.builtin.lts.Predicate_is_transition').

% Success if the argument is a JavaObjectTerm with a Location
:-build_in(is_location/1,'groove.prolog.builtin.lts.Predicate_is_location').

% Success if the argument is a JavaObjectTerm with a Location
:-build_in(is_ruleevent/1,'groove.prolog.builtin.trans.Predicate_is_ruleevent').

% Success if the argument is a JavaObjectTerm with a Location
:-build_in(is_rulematch/1,'groove.prolog.builtin.trans.Predicate_is_rulematch').

% The current graph state. Might not always be available.
% @param the graph state
:-build_in(graphstate/1,'groove.prolog.builtin.lts.Predicate_graphstate').

% The graph of a graph state
% @param the graph state
% @param the graph 
% @groove.lts.GraphState#getGraph()
:-build_in(graphstate_graph/2,'groove.prolog.builtin.lts.Predicate_graphstate_graph').
graphstate_graph(G):-graphstate(GS),graphstate_graph(GS,G).

% Success if the graph state is closed (i.e. all transitions have been found)
% @param the graph state
% @groove.lts.GraphState#isClosed()
:-build_in(graphstate_is_closed/1,'groove.prolog.builtin.lts.Predicate_graphstate_is_closed').
graphstate_is_closed:-graphstate(GS),graphstate_is_closed(GS).

% The location of a graph state
% @param the graph state
% @groove.lts.GraphState#getLocation()
:-build_in(graphstate_location/2,'groove.prolog.builtin.lts.Predicate_graphstate_location').
graphstate_location(L):-graphstate(GS),graphstate_location(GS,L).

% A transition in a state
% @param the state
% @param the transition
% @groove.lts.GraphState#getTransitionSet()
:-build_in(graphstate_transition/2,'groove.prolog.builtin.lts.Predicate_graphstate_transition').
graphstate_transition(T):-graphstate(GS),graphstate_transition(GS,T).

% All current transitions in a state
% @param the state
% @param the transition set
% @groove.lts.GraphState#getTransitionSet()
:-build_in(graphstate_transition_set/2,'groove.prolog.builtin.lts.Predicate_graphstate_transition_set').
graphstate_transition_set(T):-graphstate(GS),graphstate_transition_set(GS,T).

% A next state from this state
% @param the state
% @param the next state
% @groove.lts.GraphState#getNextState()
:-build_in(graphstate_next/2,'groove.prolog.builtin.lts.Predicate_graphstate_next').
graphstate_next(T):-graphstate(GS),graphstate_next(GS,T).

% All next states from this state
% @param the state
% @param the next state set
% @groove.lts.GraphState#getNextState()
:-build_in(graphstate_next_set/2,'groove.prolog.builtin.lts.Predicate_graphstate_next_set').
graphstate_next_set(T):-graphstate(GS),graphstate_next_set(GS,T).

% The source of a transition
% @param the transition
% @param the source state
% @groove.lts.GraphTransition#source()
:-build_in(transition_source/2,'groove.prolog.builtin.lts.Predicate_transition_source').

% The target of a transition
% @param the transition
% @param the target state
% @groove.lts.GraphTransition#target()
:-build_in(transition_target/2,'groove.prolog.builtin.lts.Predicate_transition_target').

% The rule event that caused this transition
% @param the transition
% @param the rule event
% @see groove.lts.GraphTransition#getEvent()
:-build_in(transition_event/2,'groove.prolog.builtin.lts.Predicate_transition_event').

% The rule match that caused this transition
% @param the transition
% @param the rule match
% @see groove.lts.GraphTransition#getMatch()
:-build_in(transition_match/2,'groove.prolog.builtin.lts.Predicate_transition_match').

% Success if the object is a GTS
:-build_in(is_gts/1,'groove.prolog.builtin.lts.Predicate_is_gts').

% Get the current GTS. This can fail when not GTS is active.
:-build_in(gts/1,'groove.prolog.builtin.lts.Predicate_gts').

% The start graph state of a GTS
% @param the gts
% @param the start GraphState
% @see groove.lts.LTS#startState()
:-build_in(gts_start_state/2,'groove.prolog.builtin.lts.Predicate_gts_start_state').
gts_start_state(GS):-gts(G),gts_start_state(G,GS).

% The final states of a GTS
% @param the gts
% @param the start GraphState
% @see groove.lts.LTS#getFinalStates()
% @see groove.lts.LTS#isFinal()
:-build_in(gts_final_state/2,'groove.prolog.builtin.lts.Predicate_gts_final_state').
gts_final_state(GS):-gts(G),gts_final_state(G,GS).

% The final states of a GTS
% @param the gts
% @param the start GraphState
% @see groove.lts.LTS#getFinalStates()
:-build_in(gts_final_state_set/2,'groove.prolog.builtin.lts.Predicate_gts_final_state_set').
gts_final_state_set(GS):-gts(G),gts_final_state_set(G,GS).

% Get a matching rule event for a given graph state
% @param the gts
% @param the graphstate
% @param the ruleevent
:-build_in(gts_match/3,'groove.prolog.builtin.lts.Predicate_gts_match').
gts_match(GS,RE):-gts(G),gts_match(G,GS,RE).
gts_match(RE):-gts(G),graphstate(GS),gts_match(G,GS,RE).

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
