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
:-build_in(node_edge_set/3,'groove.prolog.builtin.graph.Predicate_node_edge_set').
node_edge_set(N,E):-graph(G),node_edge_set(G,N,E).

% Get a certain set of edges for a node
% @param the graph
% @param the node 
% @param the position of the edge (integer)
% @param the list of edges
% @see groove.graph.GraphShape#edgeSet(Node,int)
:-build_in(node_edge_set/4,'groove.prolog.builtin.graph.Predicate_node_edge_set_pos').

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
