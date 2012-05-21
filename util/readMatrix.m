function [ mat, lines ] = readMatrix( file, numSkipLines, numCols, format )
%READMATRIX Summary of this function goes here
%   Detailed explanation goes here
    fid = fopen(file);
    for i=1:numSkipLines
        fgetl(fid);
    end
    [mat, c] = fscanf(fid, format, [numCols inf]);
    fclose(fid);
    mat = mat';
    lines = c / numCols;
end

