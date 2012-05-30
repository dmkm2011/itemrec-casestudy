function [ dist ] = computeItemDistance( items, itemDistanceWeight )
%COMPUTEITEMDISTANCE Return a matrix whose cell (i, j) containing the distance
% from item i to item j.
% So the size of the result matrix is n x n
% where n is the total number of items

    numItems = length(items.itemIds);
    %dist = zeros(numItems);
    catDistance = zeros(numItems);
    kwDistance = zeros(numItems);
    
    % normalize the keyword vectors
    itemKeywords = items.keywords;
    %itemKeywords = itemKeywords ./ max(max(itemKeywords), [], 2);
    %itemKeywords = items.keywords > 0;
    
    for i=1:numItems-1
        
        % category-based distance
        catDist = computeItemCategoryDistance(items.categories(i, :), ...
                                items.categories(i+1:end, :));
        catDistance(i, i + 1:end) = catDist';
        catDistance(i + 1:end, i) = catDist;
        
        % keyword-based distance
        kwDist = computeKeywordDistance(itemKeywords(i, :), ...
                                    itemKeywords(i+1:end, :));
        kwDistance(i, i + 1:end) = kwDist';
        kwDistance(i + 1:end, i) = kwDist;
    end
    
    % normalize the keyword distance
    kwDistance = kwDistance ./ max(max(kwDistance), [], 2);
    
    % weighted distance
    dist = itemDistanceWeight * kwDistance + ...
       (1 - itemDistanceWeight) * catDistance;
   
    %if (any(dist > 1))
    %    disp('WTF');
    %end
    
end