function [ ids, keywords, keywordDict ] = readUserKeywords( file, skipLines, readLines, userIdIn, keywordDictIn )
   
    fid = fopen(file);
    
    for i=1:skipLines
        fgetl(fid);
    end
    
    kwRowIdx = zeros(1);
    kwColIdx = zeros(1);
    kwVal = zeros(1);
    kwIdx = 0;    
    keywordDict = keywordDictIn;
    kwCount = length(keywordDict);
    
    c = textscan(fid, '"%d"\t%q\r\n');
       
    % user ID
    idsTmp = c{1, 1};
    ids = zeros(length(idsTmp), 1);
    userKws = c{1, 2};
    
    for lineCnt = 1:readLines
              
       % user keywords
       kws = regexp(char(userKws(lineCnt)), ';', 'split');
       
       idPos = find(userIdIn == idsTmp(lineCnt));
       ids(idPos(1)) = idsTmp(lineCnt);
       
       for i=1:length(kws)
           kwCell = regexp(char(kws{1, i}), ':', 'split');
           kw = str2double(kwCell{1, 1});
           weight = str2double(kwCell{1, 2});
           
           idx = find(keywordDict == kw);
           if (isempty(idx))
               kwCount = kwCount + 1;
               keywordDict(kwCount) = kw;
               idx(1) = kwCount;
           end
           
           kwIdx = kwIdx + 1;
           
           %kwRowIdx(kwIdx) = lineCnt;
           kwRowIdx(kwIdx) = idPos(1);
           kwColIdx(kwIdx) = idx(1);
           kwVal(kwIdx) = weight;
       end
    end
    
    keywords = sparse(kwRowIdx, kwColIdx, kwVal);
    
    fclose(fid);
end

