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

% Get the complete node set of the graph
% @param the graph
% @param the list of nodes
% @see groove.graph.GraphShape#nodeSet()
:-build_in(node_set/2,'groove.prolog.builtin.graph.Predicate_node_set').
node_set(N):-graph(G),node_set(G,N).

% Get the number of nodes in the graph
% @param the graph
% @param the number of nodes
% @see groove.graph.GraphShape#nodeCount()
:-build_in(node_count/2,'groove.prolog.builtin.graph.Predicate_node_count').
node_count(N):-graph(G),node_count(G,N).

% Get a set of edges from either a graph or a single node
% @param the graph or node
% @param the list of edges
% @see groove.graph.GraphShape#edgeSet()
% @see groove.graph.GraphShape#edgeSet(Node)
:-build_in(edge_set/2,'groove.prolog.builtin.graph.Predicate_edge_set').
edge_set(E):-graph(G),edge_set(G,E).

% Get the number of edges in a graph
% @param the graph
% @param the number of edges
% @see groove.graph.GraphShape#edgeCount
:-build_in(edge_count/2,'groove.prolog.builtin.graph.Predicate_edge_count').
edge_count(E):-graph(G),edge_count(G,E).

% Get a set of edges from a single node
% @param the node
% @param the list of edges
% @see groove.graph.GraphShape#edgeSet(Node)
:-build_in(node_edge_set/2,'groove.prolog.builtin.graph.Predicate_node_edge_set').

% Get a certain set of edges for a node
% @param the node 
% @param the position of the edge (integer)
% @param the list of edges
% @see groove.graph.GraphShape#edgeSet(Node,int)
:-build_in(node_edge_set/3,'groove.prolog.builtin.graph.Predicate_node_edge_set').

% Get the outgoing edges for a given node
% @param the node
% @param list of outgoing edges
% @see groove.graph.GraphShape#outEdgeSet(Node)
:-build_in(out_edge_set/2,'groove.prolog.builtin.graph.Predicate_out_edge_set').

% Get the edge set of a graph with a given label
% @param the graph
% @param the label
% @param the list of edges
% @see groove.graph.GraphShape#labelEdgeSet(int,Label)
:-build_in(label_edge_set/3,'groove.prolog.builtin.graph.Predicate_label_edge_set').
label_edge_set(L,E):-graph(G),label_edge_set(L,E).

