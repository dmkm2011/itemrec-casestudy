%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% run the algorithm
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

clear; clc;

% initialize
init;

G = constructGraph(USERS, ITEM, PARAMS);

% load the graph
% blah blah

% run shortest path using MatlabBGL
% blah blah

% do some stuff...
% [D P]=all_shortest_paths(G,struct('algname','floyd_warshall'));
% exportResults(D, USERS.userIds, ITEM.itemIds, USERS.trainSet, './result.txt')
% save('./result.mat', 'D', 'P')
% dlmwrite('./testSet.txt', USERS.testSet(:, 1:3), 'delimiter', '\t', 'precision', '%d', 'newline', 'pc')
