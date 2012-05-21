%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% load USER_ACTION, USER_SNS and REC_LOG_TRAIN
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

folder = '../data/';
files = {'USER_ACTION.tsv'; 'USER_SNS.tsv'; 'REC_LOG_TRAIN.tsv'; ...
        'USER_PROFILES.tsv'};
numCols = [5; 2; 4; 4];
formats = { '"%g"\t"%g"\t"%g"\t"%g"\t"%g"\r\n'; ...
            '"%g"\t"%g"\r\n'; ...
            '"%g"\t"%g"\t"%g"\t"%g"\r\n'; ...
            '"%g"\t"%g"\t"%g"\t"%g"\t%*s\r\n'};
output = './users.mat';

i = 1;
path = [folder files{i, :}];
fprintf('Reading %s...\r\n', path);
[actions, lines] = readMatrix(path, 1, numCols(i), formats{i, :});
fprintf('Reading completed: %d lines.\r\n', lines);

i = 2;
path = [folder files{i, :}];
fprintf('Reading %s...\r\n', path);
[sns, lines] = readMatrix(path, 1, numCols(i), formats{i, :});
fprintf('Reading completed: %d lines.\r\n', lines);

i = 3;
path = [folder files{i, :}];
fprintf('Reading %s...\r\n', path);
[recLogTrain, lines] = readMatrix(path, 1, numCols(i), formats{i, :});
fprintf('Reading completed: %d lines.\r\n', lines);

i = 4;
path = [folder files{i, :}];
fprintf('Reading %s...\r\n', path);
[profiles, lines] = readMatrix(path, 1, numCols(i), formats{i, :});
fprintf('Reading completed: %d lines.\r\n', lines);

save(output, 'actions', 'sns', 'recLogTrain', 'profiles');