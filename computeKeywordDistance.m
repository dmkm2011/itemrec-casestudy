function [ dist ] = computeKeywordDistance( kw1, kws )
%COMPUTEKEYWORDDISTANCE Summary of this function goes here
%   Detailed explanation goes here
    d = repmat(kw1, size(kws, 1), 1) - kws;
    dist = sqrt(sum(d.^2, 2));
end

