function [result] = parseAdsorptionResult(adsorptionOutput, trainSet)

    recommendedItemCount = (size(adsorptionOutput, 2) - 1)/2;
    itemCount = size(adsorptionOutput, 1);
    
    adsorption = zeros(itemCount, 1 + recommendedItemCount);
    for i=1:recommendedItemCount
        adsorption(:,i+1)=adsorptionOutput(:, 2*i);
    end
    adsorption(:, 1) = adsorptionOutput(:, 1);
    
    result = [];
    
    for i=1:itemCount
        if(~any(trainSet(:, 1) == adsorption(i, 1)))
            continue;
        end
        
        rowI = zeros(1, 4);
        rowI(1) = adsorption(i, 1);
        k = 2;
        for j=2:recommendedItemCount+1
            if (~ismember(trainSet(:, 1:2), adsorption(i, [1, j]), 'rows'))
                rowI(k) = adsorption(i, j);
                k = k + 1;
                if (k > 4)
                    break;
                end
            end
        end
        result(end+1, :) = rowI;
    end
end