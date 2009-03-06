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
% is_graph(@Graph)
:-build_in(is_graph/1,'groove.prolog.builtin.graph.Predicate_is_graph').

% Fail if the first argument is not a Groove Node
% is_graph(@Node)
:-build_in(is_node/1,'groove.prolog.builtin.graph.Predicate_is_node').

% Fail if the first argument is not a Groove Edge
% is_graph(@Edge)
:-build_in(is_edge/1,'groove.prolog.builtin.graph.Predicate_is_edge').

% Retrieve the current graph
% graph(-Graph)
% @param the graph
:-build_in(graph/1,'groove.prolog.builtin.graph.Predicate_graph').

% Get a node from the graph
% graph_node(+Graph,?Node)
% @param the graph
% @param the node
% @see groove.graph.GraphShape#nodeSet()
:-build_in(graph_node/2,'groove.prolog.builtin.graph.Predicate_graph_node').
graph_node(N):-graph(G),graph_node(G,N).

% Get the complete node set of the graph
% graph_node_set(+Graph,?NodeSet)
% @param the graph
% @param the list of nodes
% @see groove.graph.GraphShape#nodeSet()
:-build_in(graph_node_set/2,'groove.prolog.builtin.graph.Predicate_graph_node_set').
graph_node_set(N):-graph(G),graph_node_set(G,N).

% Get the number of nodes in the graph
% graph_node_count(+Graph,?Count)
% @param the graph
% @param the number of nodes
% @see groove.graph.GraphShape#nodeCount()
:-build_in(graph_node_count/2,'groove.prolog.builtin.graph.Predicate_graph_node_count').
graph_node_count(N):-graph(G),graph_node_count(G,N).

% Get a edge from a graph
% graph_edge(+Graph,?Edge)
% @param the graph
% @param the edge
% @see groove.graph.GraphShape#edgeSet()
:-build_in(graph_edge/2,'groove.prolog.builtin.graph.Predicate_graph_edge').
graph_edge(E):-graph(G),graph_edge(G,E).

% Get a set of edges from either a graph
% graph_edge_set(+Graph,?EdgeSet)
% @param the graph 
% @param the list of edges
% @see groove.graph.GraphShape#edgeSet()
:-build_in(graph_edge_set/2,'groove.prolog.builtin.graph.Predicate_graph_edge_set').
graph_edge_set(E):-graph(G),graph_edge_set(G,E).

% Get the number of edges in a graph
% graph_edge_count(+Graph,?Count)
% @param the graph
% @param the number of edges
% @see groove.graph.GraphShape#edgeCount
:-build_in(graph_edge_count/2,'groove.prolog.builtin.graph.Predicate_graph_edge_count').
graph_edge_count(E):-graph(G),graph_edge_count(G,E).

% Get an edge from a node, can be incoming or outgoing
% node_edge(+Graph,+Node,?Edge)
% @param the graph
% @param the node
% @param the edge
% @see groove.graph.GraphShape#edgeSet(Node,int)
:-build_in(node_edge/3,'groove.prolog.builtin.graph.Predicate_node_edge').
node_edge(N,E):-graph(G),node_edge(G,N,E).

% Get the set of edges for a single node. Both incoming and outgoing edges.
% node_edge_set(+Graph,+Node,?EdgeSet)
% @param the graph
% @param the node
% @param the list of edges
% @see groove.graph.GraphShape#edgeSet(Node,int)
:-build_in(node_edge_set__/3,'groove.prolog.builtin.graph.Predicate_node_edge_set').
node_edge_set(N,E):-graph(G),node_edge_set__(G,N,E).

% Get a certain set of edges for a node
% node_edge_set(+Graph,+Node,?EdgeSet,?Position)
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
% node_out_edge(+Graph,+Node,?Edge)
% @param the graph
% @param the node
% @param list of outgoing edges
% @see groove.graph.GraphShape#outEdgeSet(Node)
:-build_in(node_out_edge/3,'groove.prolog.builtin.graph.Predicate_node_out_edge').
node_out_edge(N,E):-graph(G),node_out_edge(G,N,E).

% Get the outgoing edges for a given node
% node_out_edge(+Graph,+Node,?EdgeSet)
% @param the graph
% @param the node
% @param list of outgoing edges
% @see groove.graph.GraphShape#outEdgeSet(Node)
:-build_in(node_out_edge_set/3,'groove.prolog.builtin.graph.Predicate_node_out_edge_set').
node_out_edge_set(N,E):-graph(G),node_out_edge_set(G,N,E).

% Get an edge with a given label
% label_edge(+Graph,+Label,?Edge)
% @param the graph
% @param the label
% @param the list of edges
% @see groove.graph.GraphShape#labelEdgeSet(int,Label)
:-build_in(label_edge/3,'groove.prolog.builtin.graph.Predicate_label_edge').
label_edge(L,E):-graph(G),label_edge(G,L,E).

% Get the edge set of a graph with a given label
% label_edge_set(+Graph,+Label,?EdgeSet)
% @param the graph
% @param the label
% @param the list of edges
% @see groove.graph.GraphShape#labelEdgeSet(int,Label)
:-build_in(label_edge_set/3,'groove.prolog.builtin.graph.Predicate_label_edge_set').
label_edge_set(L,E):-graph(G),label_edge_set(G,L,E).

% Will be success when the edge has the node as an end point. Fail otherwise
% edge_end(+Edge,?Node)
% @param the edge
% @param the node
% @see groove.graph.Edge#hasEnd(Node)
:-build_in(edge_end/2,'groove.prolog.builtin.graph.Predicate_edge_end').

% Get the node of a given end
% edge_end(+Edge,?Node,?Position)
% @param the edge
% @param the node
% @param the position
% @see groove.graph.Edge#end(int)
% @see groove.graph.Edge#endIndex(Node)
:-build_in(edge_end/3,'groove.prolog.builtin.graph.Predicate_edge_end').

% Get all ends for an edge (usually 2)
% edge_end_set(+Edge,?NodeSet)
% @param the edge
% @param the set of nodes
% @see groove.graph.Edge#ends()
:-build_in(edge_end_set/2,'groove.prolog.builtin.graph.Predicate_edge_end_set').

% Get the number of ends for an edge (usually 2)
% edge_end_count(+Edge,?Number)
% @param the edge
% @param the the number of ends
% @see groove.graph.Edge#ends()
:-build_in(edge_end_count/2,'groove.prolog.builtin.graph.Predicate_edge_end_count').

% Get the source node of an edge
% edge_source(+Edge,?Node)
% @param the edge
% @param the node
% @see groove.graph.Edge#source()
:-build_in(edge_source/2,'groove.prolog.builtin.graph.Predicate_edge_sounce').

% Get the destination node of an edge (opposite of the source)
% edge_opposite(+Edge,?Node)
% @param the edge
% @param the node
% @see groove.graph.Edge#opposite()
:-build_in(edge_opposite/2,'groove.prolog.builtin.graph.Predicate_edge_opposite').

% Get the label of the edge
% edge_label(+Edge,?Label)
% @param the edge
% @param the label/Atom
% @see groove.graph.Edge#label()
:-build_in(edge_label/2,'groove.prolog.builtin.graph.Predicate_edge_label').

% Helper predicate, stop processing when the start node is reached
node_path(Graph,From,From,[],_).

% Internal predicate which odes all the processing
node_path(Graph,From,To,[E|Path],Visited):-
	node_out_edge(Graph,From,E),
	\+ member(E,Visited),
	edge_opposite(E,N),
	From \= N, % to abolish self edges
	node_path(Graph,N,To,Path,[E|Visited]).

% Get the path from one node to an other
% node_path(+Graph,+Node,+Node,?Path)
% @param the graph that contains the nodes
% @param the starting node
% @param the destination node
% @param list of edges that define the path
node_path(Graph,From,To,Path):-
	node_path(Graph,From,To,Path,[]).

% Short hand to operate on the current graph
node_path(From,To,Path):-
	graph(G),node_path(G,From,To,Path,[]).
