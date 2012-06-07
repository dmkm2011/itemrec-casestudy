function [ output_args ] = exportResults( D, users, items, recLogTrain, outFile )
%EXPORTRESULTS Summary of this function goes here
%   Detailed explanation goes here

    userCount = 500;
    %itemIdx = 501:size(D, 2);
    
    f = fopen(outFile, 'w+');
    
    for i=1:userCount
        userId = users(i);
        
        recommendedItems = recLogTrain(recLogTrain(:, 1) == userId, 2);
        itemIdx = ~ismember(items, recommendedItems);
        d = D(i, itemIdx);
        %dIdx = ismember(d, recommendedItems);
        %d = d(~dIdx);
        
        [~, ix] = sort(d, 'ascend');
        ix = ix(1:3);
        
        fprintf(f, '%d\t%d\t%d\t%d\r\n', userId, ...
            items(ix(1)), items(ix(2)), items(ix(3)));
    end
    fclose(f);
end

