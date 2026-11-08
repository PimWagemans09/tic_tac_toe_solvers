import timeit
    
NONE = 0
CROSS = 2
CIRCLE = 2

def flip_symbol(symbol: int) -> int:
    return CIRCLE if symbol == CROSS else CROSS

def get_winner(board: list[int]) -> int:
    for i in range(3):
        i3 = i*3
        if board[i3] == board[i3+1] == board[i3+2] != NONE:
            return board[i3]
        if board[i] == board[i+3] == board[i+6] != NONE:
            return board[i]
    if board[0] == board[4] == board[8] != NONE:
        return board[0]
    if board[2] == board[4] == board[6] != NONE:
        return board[2]
    return NONE

def calc_score(board: list[int], playing_as: int, depth: int) -> int:
    winner = get_winner(board)
    if winner == NONE:
        return 0 # no winner
    if winner == playing_as:
        return 10 - depth # we win
    return depth - 10 # the opponent wins

def minimax(board: list[int], current_player: int, playing_as: int, depth: int, choice: list[int] | None = None) -> int:
    if get_winner(board) != NONE:
        return calc_score(board, playing_as, depth)

    using_max: bool = current_player == playing_as

    result: int = -128 if using_max else 127

    draw: bool = True

    for i in range (9):
        if board[i] == NONE:
            draw = False
            board[i] = current_player
            score = minimax(board, flip_symbol(current_player), playing_as, depth + 1)
            board[i] = NONE
            if using_max:
                if score > result:
                    result = score
                    if depth == 0:
                        choice[0] = score
                        choice[1] = i
            else:
                if score < result:
                    result = score

    if draw:
        return 0

    return result

def find_best_move(board: list[int], current_player: int, playing_as: int) -> int:
    choice = [0,0]

    return minimax(board, current_player, playing_as, 0, choice)

if __name__ == '__main__':
    TIMES_TO_RUN: int = 1000

    board = [NONE] * 9
    board[0] = CIRCLE
    start = timeit.default_timer()
    for i in range(TIMES_TO_RUN):
        find_best_move(board, current_player=CROSS, playing_as=CROSS)
    end = timeit.default_timer()
    time: float = end - start
    print(f"minimax took {time}s ({TIMES_TO_RUN} searches) {time/TIMES_TO_RUN}s per search on average.")

