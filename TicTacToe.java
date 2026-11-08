private static final int BOARD_SIZE = 9;
private static final int TIMES_TO_RUN = 1000;

enum Symbol {
    NONE,
    CROSS,
    CIRCLE
}

static class Choice {
    int score;
    int move;

    Choice(int score, int move) {
        this.score = score;
        this.move = move;
    }
}

private static Symbol flipSymbol(Symbol symbol) {
    return symbol == Symbol.CROSS
            ? Symbol.CIRCLE
            : Symbol.CROSS;
}

private static Symbol getWinner(Symbol[] board) {
    // Check all possible win conditions
    for (int i = 0; i < 3; i++) {
        final int i3 = i * 3;

        // Check rows
        if (board[i3] == board[i3 + 1]
                && board[i3] == board[i3 + 2]
                && board[i3] != Symbol.NONE) {
            return board[i3];
        }

        // Check columns
        if (board[i] == board[i + 3]
                && board[i] == board[i + 6]
                && board[i] != Symbol.NONE) {
            return board[i];
        }
    }

    // Check diagonals
    if (board[0] == board[4]
            && board[0] == board[8]
            && board[0] != Symbol.NONE) {
        return board[0];
    }

    if (board[2] == board[4]
            && board[2] == board[6]
            && board[2] != Symbol.NONE) {
        return board[2];
    }

    return Symbol.NONE;
}

private static int calcScore(
        Symbol[] board,
        Symbol playingAs,
        int depth) {

    Symbol winner = getWinner(board);

    if (winner == Symbol.NONE) {
        return 0;
    }

    if (winner == playingAs) {
        return 10 - depth;
    }

    return depth - 10;
}

private static int minimax(
        Symbol[] board,
        Symbol currentPlayer,
        Symbol playingAs,
        int depth,
        Choice choice) {

    if (getWinner(board) != Symbol.NONE) {
        return calcScore(board, playingAs, depth);
    }

    boolean usingMax = currentPlayer == playingAs;

    int result = usingMax ? Integer.MIN_VALUE : Integer.MAX_VALUE;

    boolean draw = true;

    for (int i = 0; i < BOARD_SIZE; i++) {
        if (board[i] == Symbol.NONE) {
            draw = false;

            board[i] = currentPlayer;

            int score = minimax(
                    board,
                    flipSymbol(currentPlayer),
                    playingAs,
                    depth + 1,
                    null
            );

            board[i] = Symbol.NONE;

            if (usingMax) {
                if (score > result) {
                    result = score;

                    if (depth == 0) {
                        choice.score = score;
                        choice.move = i;
                    }
                }
            } else {
                if (score < result) {
                    result = score;
                }
            }
        }
    }

    if (draw) {
        return 0;
    }

    return result;
}

private static int findBestMove(
        Symbol[] board,
        Symbol currentPlayer,
        Symbol playingAs) {

    Choice choice = new Choice(Integer.MIN_VALUE,  Integer.MAX_VALUE);

    minimax(board, currentPlayer, playingAs, 0, choice);

    return choice.move;
}

void main() {

    Symbol[] board = new Symbol[BOARD_SIZE];

    Arrays.fill(board, Symbol.NONE);

    board[0] = Symbol.CIRCLE;

    final Symbol currentPlayer = Symbol.CROSS;
    final Symbol playingAs = Symbol.CROSS;

    long start = System.nanoTime();

    int move = Integer.MAX_VALUE;

    for (int i = 0; i < TIMES_TO_RUN; i++) {
        move = findBestMove(board, currentPlayer, playingAs);
    }

    long end = System.nanoTime();

    double cpuTimeUsed = (end - start) / 1_000_000_000.0;

    System.out.printf(
            "minimax took %.6fs (%d searches) %.9fs per search on average.%n",
            cpuTimeUsed,
            TIMES_TO_RUN,
            cpuTimeUsed / TIMES_TO_RUN
    );

    System.out.printf(
            "best move: %s", move
    );
}
