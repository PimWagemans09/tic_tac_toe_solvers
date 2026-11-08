NONE = 0
CROSS = 1
CIRCLE = 2

function flip_symbol(symbol)
    if symbol == CROSS then
        return CIRCLE
    else
        return CROSS
    end
end

function get_winner(board)
    for i = 1, 3 do
        local i3 = (i-1) * 3 +1
        if board[i3] == board[i3+1] and board[i3] == board[i3+2] and board[i3] ~= NONE then
            return board[i3]
        end
        if board[i] == board[i+3] and board[i] == board[i+6] and board[i] ~= NONE then
            return board[i]
        end
    end

    if board[1] == board[5] and board[1] == board[9] and board[1] ~= NONE then
            return board[1]
    end

    if board[3] == board[5] and board[3] == board[7] and board[3] ~= NONE then
            return board[3]
    end

    return NONE
end

function calc_score(board, playing_as, depth)
    local winner = get_winner(board)
    if winner == NONE then
        return 0 --no winner
    elseif winner == playing_as then
        return 10 - depth --we win
    else
        return depth - 10 --we lose
    end
end

function minimax(board, current_player, playing_as, depth, choice)
    if get_winner(board) ~= NONE then
        return calc_score(board, playing_as, depth)
    end

    local using_max = current_player == playing_as

    local result = using_max and -math.huge or math.huge

    local draw = true

    for i = 1,9 do
        if board[i] == NONE then
            draw = false
            board[i] = current_player
            local score = minimax(board, flip_symbol(current_player), playing_as, depth + 1, nil)
            board[i] = NONE

            if using_max then
                if score > result then
                    result = score
                    if depth == 0 then
                        choice.score = score
                        choice.move = i
                    end          
                end
            else
                if score < result then
                    result = score
                end
            end
        end
    end

    if draw then
        result = 0
    end

    return result
    
end

function find_best_move(board, current_player, playing_as)
    local choice = {}
    minimax(board, current_player, playing_as, 0, choice)
    return choice.move
end

local board = {NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE}


board[1] = CIRCLE

local start_time = os.clock()
for i = 1, 1000 do
    move = find_best_move(board, CROSS, CROSS)
end
local end_time = os.clock()
local time_taken = end_time - start_time
print("minimax took " .. (time_taken) .. "s (" .. 1000 .. " searches) " .. (time_taken) / 1000 .. "s per search on average.")
print("Best move: " .. move)