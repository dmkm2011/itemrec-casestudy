function [ trainSet testSet ] = createTestSet( recLogTrain )
%CREATETESTSET Summary of this function goes here
%   Detailed explanation goes here

    trainFraction = 0.7;
    
    users = unique(recLogTrain(:, 1));
    trainSet = zeros(size(recLogTrain));
    testSet = zeros(size(recLogTrain));
    trainCnt = 1;
    testCnt = 1;
    
    for i=1:length(users)
        
        % filter the data of this user, then
        % sort in ascending order of timestamp
        userData = recLogTrain(recLogTrain(:, 1) == users(i), :);
        userData = sortrows(userData, 4);
        
        dataCount = size(userData, 1);
        userTrainCount = round(dataCount * trainFraction);
        userTestCount = dataCount - userTrainCount;
        
        trainSet(trainCnt:trainCnt+userTrainCount-1, :) = ...
            userData(1:userTrainCount, :);
        testSet(testCnt:testCnt+userTestCount-1, :) = ...
            userData(userTrainCount+1:end, :);
        
        trainCnt = trainCnt + userTrainCount;
        testCnt = testCnt + userTestCount;
    end
    trainSet = trainSet(1:trainCnt-1, :);
    testSet = testSet(1:testCnt-1, :);
end

