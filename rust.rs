use std::time::Instant;
use std::hint::black_box;

#[repr(u8)]
#[derive(Copy, Clone, PartialEq)]
enum Symbol{
    NONE,
    CROSS,
    CIRCLE
}

fn flip_symbol(symbol: Symbol) -> Symbol{
    return if symbol == Symbol::CROSS {Symbol::CIRCLE} else {Symbol::CROSS}
}

fn get_winner(board: &[Symbol; 9]) -> Symbol{
    for i in 0..3 {
        let i3 = i*3;
        if board[i3] == board[i3+1] && board[i3] == board[i3+2] && board[i3] != Symbol::NONE {
            return board[i3]
        }
        if board[i] == board[i+3] && board[i] == board[i+6] && board[i] != Symbol::NONE {
            return board[i]
        }
    }
    if board[0] == board[4] && board[0] == board[8] && board[0] != Symbol::NONE {
        return board[0]
    }
    if board[2] == board[4] && board[2] == board[6] && board[2] != Symbol::NONE {
        return board[2]
    }

    return Symbol::NONE;
}

fn calc_score(board: &[Symbol; 9], playing_as: Symbol, depth: u8) -> i8 {
    let winner = get_winner(board);
    if winner == Symbol::NONE{
        return 0i8;
    } else if winner == playing_as {
        return 10i8 - depth as i8;
    }

    return depth as i8 - 10i8;
}

fn minimax(board: &mut [Symbol; 9], current_player: Symbol, playing_as: Symbol, depth: u8) -> (i8, u8){
    if get_winner(&board) != Symbol::NONE {return (calc_score(&board, playing_as, depth), 255)}

    let using_max = current_player == playing_as;

    let mut result: (i8, u8) = (if using_max {-128} else {127}, 255);

    let mut draw = true;

    for i in 0..9{
        if board[i] == Symbol::NONE{
            draw = false;
            board[i] = current_player;
            let score = minimax(board, flip_symbol(current_player), playing_as, depth+1).0;
            board[i] = Symbol::NONE;
            if using_max{
                if score > result.0{
                    result.0 = score;
                    result.1 = i as u8;
                }
            } else {
                if score < result.0 {
                    result.0 = score;
                }
            }
        }
    }

    if draw {
        return (0, 255)
    }

    return result
}

fn find_best_move(board: &mut [Symbol; 9], current_player: Symbol, playing_as: Symbol) -> u8 {
    return minimax(board, current_player, playing_as, 0).1
}

fn main() {
    let mut board = [Symbol::NONE; 9];

    board[0] = Symbol::CIRCLE;

    //let mut sum = 0;

    let now = Instant::now();
    for _i in 0..1000 {
        black_box(find_best_move(&mut board, Symbol::CROSS, Symbol::CROSS));
    }
    let elapsed = now.elapsed().as_secs_f64();

    println!("minimax took {:}s (1000 searches) {:}s per search on average", elapsed, elapsed/1000.0);
}