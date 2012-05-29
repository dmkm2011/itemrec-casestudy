function [ ids, cats, keywords, keywordDict ] = readItems( file, skipLines, readLines )
%READITEMS Summary of this function goes here
%   Detailed explanation goes here
    
    fid = fopen(file);
    
    for i=1:skipLines
        fgetl(fid);
    end
    
    %lineCnt = 1;
    %ids = zeros(readLines, 1);
    cats = zeros(readLines, 4);
    kwRowIdx = zeros(1);
    kwColIdx = zeros(1);
    kwIdx = 0;
    kwCount = 0;
    keywordDict = zeros(1);
    
    %while (~feof(fid) && lineCnt <= readLines)
    c = textscan(fid, '"%d"\t%q\t%q\r\n');
       
    % item ID
    ids = c{1, 1};
    
    itemCats = c{1, 2};
    itemKws = c{1, 3};
    
    for lineCnt = 1:readLines
       % item category
       cat = sscanf(char(itemCats(lineCnt)), '%d.%d.%d.%d');
       cats(lineCnt, 1:length(cat)) = cat';
       
       % item keywords
       kws = str2double(regexp(char(itemKws(lineCnt)), ';', 'split'));
       for i=1:length(kws)
           idx = find(keywordDict == kws(i));
           if (isempty(idx))
               kwCount = kwCount + 1;
               keywordDict(kwCount) = kws(i);
               idx(1) = kwCount;
           end
           
           kwIdx = kwIdx + 1;
           kwRowIdx(kwIdx) = lineCnt;
           kwColIdx(kwIdx) = idx(1);
       end
    end
    
    keywords = sparse(kwRowIdx, kwColIdx, ones(length(kwRowIdx), 1));
    
    fclose(fid);
end

