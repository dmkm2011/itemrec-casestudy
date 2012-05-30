function [ dist ] = computeUserItemDistance( userIds, itemIds, recLogTrain )
%COMPUTEUSERITEMDISTANCE Return a matrix whose cell (i, j) containing the
% similarity between user i and item j.
% So the size of the result matrix is n x m
% where n is the total number of users and m = # of items.

    dist = zeros(length(userIds), length(itemIds));
    for i=1:length(userIds)
        subData = recLogTrain(recLogTrain(:, 1) == userIds(i), :);
        
        if(isempty(subData))
            continue;
        end
        
        subDataPlus = subData(subData(:, 3) == 1, :);
        %subDataMinus = subData(subData(:, 3) == -1, :);
        dist(i, ismember(itemIds, subDataPlus(:, 2))) = 0.001;
        %dist(i, ismember(itemIds, subDataMinus(:, 2))) = -1;
    end
    
    % should also include the keyword-based distance
    % but...
end

