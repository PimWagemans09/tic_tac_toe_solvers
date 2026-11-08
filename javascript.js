
const BOARD_SIZE = 9;
const TIMES_TO_RUN = 1000;

const NONE = 0;
const CROSS = 1;
const CIRCLE = 2;

function flipSymbol(symbol) {
    return symbol === CROSS ? CIRCLE : CROSS;
}

function getWinner(board) {
    // Check all possible win conditions
    for (let i = 0; i < 3; i++) {
        const i3 = i * 3;

        // Rows
        if (
            board[i3] === board[i3 + 1] &&
            board[i3] === board[i3 + 2] &&
            board[i3] !== NONE
        ) {
            return board[i3];
        }

        // Columns
        if (
            board[i] === board[i + 3] &&
            board[i] === board[i + 6] &&
            board[i] !== NONE
        ) {
            return board[i];
        }
    }

    // Diagonals
    if (
        board[0] === board[4] &&
        board[0] === board[8] &&
        board[0] !== NONE
    ) {
        return board[0];
    }

    if (
        board[2] === board[4] &&
        board[2] === board[6] &&
        board[2] !== NONE
    ) {
        return board[2];
    }

    return NONE;
}

function calcScore(board, playingAs, depth) {
    const winner = getWinner(board);

    if (winner === NONE) {
        return 0;
    }

    if (winner === playingAs) {
        return 10 - depth;
    }

    return depth - 10;
}

function minimax(board, currentPlayer, playingAs, depth, choice = null) {
    if (getWinner(board) !== NONE) {
        return calcScore(board, playingAs, depth);
    }

    const usingMax = currentPlayer === playingAs;

    let result = usingMax ? -128 : 127;
    let draw = true;

    for (let i = 0; i < BOARD_SIZE; i++) {
        if (board[i] === NONE) {
            draw = false;

            board[i] = currentPlayer;

            const score = minimax(
                board,
                flipSymbol(currentPlayer),
                playingAs,
                depth + 1
            );

            board[i] = NONE;

            if (usingMax) {
                if (score > result) {
                    result = score;

                    if (depth === 0 && choice !== null) {
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

function findBestMove(board, currentPlayer, playingAs) {
    const choice = {
        score: -128,
        move: 255
    };

    minimax(board, currentPlayer, playingAs, 0, choice);

    return choice.move;
}

// Main program
function main() {
    const board = Array(BOARD_SIZE).fill(NONE);

    board[0] = CIRCLE;

    const currentPlayer = CROSS;
    const playingAs = CROSS;

    let move = 255;

    const start = performance.now();

    for (let i = 0; i < TIMES_TO_RUN; i++) {
        move = findBestMove(board, currentPlayer, playingAs);
    }

    const end = performance.now();

    const cpuTimeUsed = (end - start) / 1000;

    console.log(
        `minimax took ${cpuTimeUsed.toFixed(6)}s ` +
        `(${TIMES_TO_RUN} searches) ` +
        `${(cpuTimeUsed / TIMES_TO_RUN).toFixed(9)}s per search on average.`
    );

    console.log(`Best move: ${move}`);
}

main();
