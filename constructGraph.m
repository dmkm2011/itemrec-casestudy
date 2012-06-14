function [ G ] = constructGraph( users, items, params )
%CONSTRUCTGRAPH Construct a users-items graph.
%   This function will create the graph with all information.
%   The result is a sparse matrix represent this graph, which 
%   then will be used with MatlabBGL.

    numUsers = length(users.userIds);
    numItems = length(items.itemIds);
    numVertices = numUsers + numItems;
    
    matrix = zeros(numVertices);
    
    % user-user distance
    matrix(1:numUsers, 1:numUsers) = ...
        computeUserDistance(users, params.userDistanceWeight);
    disp('Done computing user-user distances');
    
    % item-item distance
    matrix(numUsers+1:end, numUsers+1:end) = ...
        computeItemDistance(items, params.itemDistanceWeight);
    disp('Done computing item-item distances');
    
    % user-item 
    userItemDist = computeUserItemDistance(users.userIds, items.itemIds, ...
                users.trainSet, users.keywords, items.normedKeywordsOfUser);
            
    matrix(1:numUsers, numUsers+1:end) = userItemDist;
    matrix(numUsers+1:end, 1:numUsers) = userItemDist';
    disp('Done computing user-item distances');
    
    G = sparse(matrix);
    disp('Done creating sparse matrix');
end

