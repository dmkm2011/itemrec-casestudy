function [ dist ] = computeItemDistance( items, itemDistanceWeight )
%COMPUTEITEMDISTANCE Return a matrix whose cell (i, j) containing the distance
% from item i to item j.
% So the size of the result matrix is n x n
% where n is the total number of items

    numItems = length(items.itemIds);
    
    catDistance = zeros(numItems);
    kwDistance = zeros(numItems);
    
    % normalize the keyword vectors
    %itemKeywords = items.keywords;
    itemKeywords = items.normedKeywordsOfUser;
    
    for i=1:numItems-1
        
        % category-based distance
        catDist = computeItemCategoryDistance(items.categories(i, :), ...
                                items.categories(i+1:end, :));
        catDistance(i, i + 1:end) = catDist';
        catDistance(i + 1:end, i) = catDist;
        
        % keyword-based distance
        kwDist = computeKeywordDistance(itemKeywords(i, :), ...
                                    itemKeywords(i+1:end, :));
        kwDist = kwDist ./ max(kwDist);
        kwDistance(i, i + 1:end) = kwDist';
        kwDistance(i + 1:end, i) = kwDist;
    end
    
    % weighted distance
    dist = itemDistanceWeight * kwDistance + ...
       (1 - itemDistanceWeight) * catDistance;
   
    if (any(dist > 1))
        disp('WTF Item');
    end
    
end