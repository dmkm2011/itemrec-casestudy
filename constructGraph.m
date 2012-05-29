function [ G ] = constructGraph( users, items )
%CONSTRUCTGRAPH Construct a bipartite users-items graph.
%   This function will create the graph with all information.
%   The result is a sparse matrix represent this graph, which 
%   then will be used with MatlabBGL.

% maybe after constructing the graph,
% we will store the graph into a *.mat file,
% to avoid creating the graph again and again.

    numUsers = length(users.userIds);
    numItems = length(items.itemIds);
    numVertices = numUsers + numItems;
    
    rowIndices = zeros(3*numVertices, 1);
    colIndices = zeros(3*numVertices, 1);
    edgeValues = zeros(3*numVertices, 1);
    
    % user-user distance
    for i=1:numUsers
        for j=i+1:numUsers
            computeUserDistance(users, i, j);
        end
    end
    
    % item-item distance
    for i=1:numItems
        for j=i+1:numItems
            computeItemDistance(items, i, j);
        end
    end
    
    % user-item 
    
    G = sparse(rowIndices, colIndices, edgeValues);
end

