function [ dist ] = computeItemCategoryDistance( cat1, cats )
%COMPUTEITEMCATEGORYDISTANCE Compute the category distance from cat1
% to other categories in cats.
% the result is a column vector of length(cats)

    if (size(cat1, 2) ~= size(cats, 2))
        error('Fck, they must have the same length.');
    end
    
    n = length(cat1);
    dist = zeros(size(cats, 1), 1);
    for i=1:n
       id = and(cats(:, i) ~= cat1(i), dist == 0);
       dist(id) = (n+1-i);
    end
       
    % then normalize the distance
    dist = dist ./ (n);
end

