function [ p ] = getPath( P, i, j )
%GETPATH Summary of this function goes here
%   Detailed explanation goes here

    p=[];
    while j~=0
        p(end+1)=j;
        j=P(i,j);
    end;
    p=fliplr(p);
end

