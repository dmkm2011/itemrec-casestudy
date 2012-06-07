function [ dist ] = computeUserActionDistance( userId, userIdList, actions, sns )
%COMPUTEUSERACTIONDISTANCE Summary of this function goes here
%   Detailed explanation goes here

    dist = zeros(length(userIdList), 1);
    
    userActions = actions(actions(:, 1) == userId, :);
    
    if isempty(userActions)
        return;
    end
    
    % normalization within this group of actions
    % only for this user
    for i=3:size(userActions, 2)
        userActions(:, i) = userActions(:, i) ./ max(userActions(:, i));
    end
    
    for i=1:size(userActions, 1)
        
        % we compute the similarity between userId and userB
        
        userB = userActions(i, 2);
        userBIdx = find(userIdList == userB, 1);
        if (isempty(userBIdx))
            continue;
        end
        
        % the similarity is computed from normalized
        % number of @-actions, # of retweet and # of comments
        % between those 2 users
        s = sum(userActions(i, 3:end));
        if (any(ismember(sns, [userId userB], 'rows')))
            s = s+1;
        end
        
        % normalization
        %s = s/(size(userActions, 2));
        s = s/4.01;
        
        % subtracted from 1 because d is kind of similarity measure
        % the bigger d is, the more "similar" users are
        % Therefore, we subtract d from 1 in order to get the distance
        % the bigger the distance is, the more "far away" users are
        dist(userBIdx) = 1 - s;
    end
    
    % the distance to itself is always zero
    dist(userIdList == userId) = 0;
end
