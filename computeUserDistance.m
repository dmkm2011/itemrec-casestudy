function [ dist ] = computeUserDistance( users, userDistanceWeight)
%COMPUTEUSERDISTANCE Return a matrix whose cell (i, j) containing the distance
% from user i to user j.
% So the size of the result matrix is n x n
% where n is the total number of users

    numUsers = length(users.userIds);
    actDistance = zeros(numUsers);
    kwDistance = zeros(numUsers);
    
    for i=1:numUsers
        
        % the action distance
        actDist = computeUserActionDistance(users.userIds(i), ...
                users.userIds, users.actions, users.sns);
        actDistance(i, :) = actDistance(i, :) + actDist';
        actDistance(:, i) = actDistance(:, i) + actDist;
        
        % the keyword distance
        %kwDist = computeKeywordDistance(users.keywords(i, :), ...
        %        users.keywords(i+1:end, :));
        kwDist = computeKeywordDistance(users.normedKeywords(i, :), ...
                users.normedKeywords(i+1:end, :));
        kwDist = kwDist ./ max(kwDist);
        kwDistance(i, i + 1:end) = kwDist';
        kwDistance(i + 1:end, i) = kwDist;
    end
    
    % normalize
    actDistance = actDistance ./ 2;
    %kwDistance = kwDistance ./ max(max(kwDistance), [], 2);
    %kwDistance = kwDistance ./ 2;
    
    dist = userDistanceWeight * actDistance + ...
        (1 - userDistanceWeight) * kwDistance;
    
    if (any(dist > 1))
        disp('WTF');
    end
end

