#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <sys/types.h>

#define BOARD_SIZE 9
#define TIMES_TO_RUN 1000

typedef enum : uint8_t{
    NONE,
    CROSS,
    CIRCLE
} Symbol;

typedef struct {
    int8_t score;
    uint8_t move;
} Choice;

static Symbol flip_symbol(const Symbol symbol) {
    return symbol == CROSS ? CIRCLE : CROSS;
}

static Symbol get_winner(const Symbol board[9]) {
    // check all possible win conditions
    for (uint8_t i = 0; i < 3; i++) {
        const uint8_t i3 = i*3;
        if (board[i3] == board[i3+1] && board[i3] == board[i3+2] && board[i3] != NONE) {
            return board[i3];
        }
        if (board[i] == board[i+3] && board[i] == board[i+6] && board[i] != NONE) {
            return board[i];
        }
    }
    if (board[0] == board[4] && board[0] == board[8] && board[0] != NONE) {
        return board[0];
    }
    if (board[2] == board[4] && board[2] == board[6] && board[2] != NONE) {
        return board[2];
    }

    return NONE;
}


static int8_t calc_score(const Symbol board[9], const Symbol playing_as, const uint8_t depth) {
    const Symbol winner = get_winner(board);
    if (winner == NONE) {
        return 0; // no winner
    }
    if (winner == playing_as) {
        return 10 - depth; // current player won
    }
    return depth - 10; // current player lost
}

static int8_t minimax(Symbol board[BOARD_SIZE], const Symbol current_player, const Symbol playing_as, const uint8_t depth, Choice* choice) {
    if (get_winner(board) != NONE) return calc_score(board, playing_as, depth);

    const bool using_max = current_player == playing_as;

    int8_t result = using_max ? -128 : 127;

    bool draw = true;

    for (uint8_t i = 0; i < 9; i++) {
        if (board[i] == NONE) {
            draw = false;
            board[i] = current_player;
            const int8_t score = minimax(board, flip_symbol(current_player), playing_as, depth+1, NULL);
            board[i] = NONE;
            if (using_max) {
                if (score > result) {
                    result = score;
                    if (depth == 0) {
                        choice->score = score;
                        choice->move = i;
                    }
                }
            } else {
                if (score < result) {
                    result = score;
                }
            }
        }
    }

    if (draw) return 0;

    return result;
}

static uint8_t find_best_move(Symbol board[BOARD_SIZE], const Symbol current_player, const Symbol playing_as) {
    Choice choice = {.score = -128,.move = 255};

    minimax(board, current_player, playing_as,0, &choice);

    return choice.move;
}

int main() {
    Symbol board[BOARD_SIZE] = {};
    board[0] = CIRCLE;

    const Symbol current_player = CROSS;

    const Symbol playing_as = CROSS;

    uint8_t move = 255;

    const clock_t start = clock();
    for (int i = 0; i < TIMES_TO_RUN; i++) {
        move = find_best_move(board, current_player, playing_as);
    }
    const clock_t end = clock();

    const double cpu_time_used = ((double) (end - start)) / CLOCKS_PER_SEC;

    //printf("%i\n", move);
    printf("minimax took %fs (%i searches) %fs per search on average.\n", cpu_time_used, TIMES_TO_RUN, cpu_time_used/TIMES_TO_RUN);

    return 0;
}

