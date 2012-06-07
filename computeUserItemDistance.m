function [ dist ] = computeUserItemDistance( userIds, itemIds, recLogTrain, normedUserKws, normedItemKws )
%COMPUTEUSERITEMDISTANCE Return a matrix whose cell (i, j) containing the
% similarity between user i and item j.
% So the size of the result matrix is n x m
% where n is the total number of users and m = # of items.

    numItems = length(itemIds);
    
    dist = zeros(length(userIds), numItems );

    % norm will make 2 points lie on the unit circle
    % so the maximum distance is 2
    % we have to divide the distance by 2
    % in order to normalize the distance into [0, 1].
    
    for i=1:length(userIds)
        %disp(i);
        
        % the keyword distance
        userKw = normedUserKws(i, :);
        distRow = zeros(1, numItems);
        for j=1:numItems 
            t = userKw - normedItemKws(j);
            distRow(j) = sqrt(sum(t.^2, 2))./2;
        end
        
        subData = recLogTrain(recLogTrain(:, 1) == userIds(i), :);
        
        if(isempty(subData))
            continue;
        end
        
        %subDataPlus = subData(subData(:, 3) == 1, :);
        %dist(i, ismember(itemIds, subDataPlus(:, 2))) = 0.001;
        
        % items which are not recommended have further distances
        tmp = ~ismember(itemIds, subData(:, 2));
        distRow(tmp) = ((distRow(tmp) ./ 2) + 0.5);
        
        % those items were denied, so the distance is 0 (Inf)
        subDataMinus = subData(subData(:, 3) == -1, :);
        distRow(ismember(itemIds, subDataMinus(:, 2))) = 0;
        
        dist(i, :) = distRow;
    end
end

