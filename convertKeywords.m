function [ newKw ] = convertKeywords( itemKeywords, itemKwDict, userKwDict )
%CONVERTKEYWORDS Summary of this function goes here
%   Detailed explanation goes here

    newKw = zeros(size(itemKeywords, 1), length(userKwDict));
    
    for i=1:size(itemKeywords, 1)
        kwList = itemKeywords(i, :);
        [~, nnzIdx] = find(kwList > 0);
        for j = 1:length(nnzIdx)
            kw = itemKwDict(nnzIdx(j));
            [~, idx] = find(userKwDict == kw);
            newKw(i, idx) = kwList(nnzIdx(j));
        end
    end
end

