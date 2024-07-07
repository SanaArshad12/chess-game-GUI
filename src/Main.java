import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

enum Player { HUMAN, AI }

class Move {
    int startX, startY, endX, endY;

    Move(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
}

class Board {
    private static final int BOARD_SIZE = 8;
    private char[][] board;

    public Board() {
        String initialBoard =
                "rnbqkbnr" +
                        "pppppppp" +
                        "........" +
                        "........" +
                        "........" +
                        "........" +
                        "PPPPPPPP" +
                        "RNBQKBNR";

        board = new char[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < 64; i++) {
            board[i / BOARD_SIZE][i % BOARD_SIZE] = initialBoard.charAt(i);
        }
    }

    public void print() {
        System.out.println("  a b c d e f g h");
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println(8 - i);
        }
        System.out.println("  a b c d e f g h");
    }

    public boolean isValidMove(Move move) {
        return move.startX >= 0 && move.startX < BOARD_SIZE &&
                move.startY >= 0 && move.startY < BOARD_SIZE &&
                move.endX >= 0 && move.endX < BOARD_SIZE &&
                move.endY >= 0 && move.endY < BOARD_SIZE &&
                board[move.startY][move.startX] != '.';
    }

    public void applyMove(Move move) {
        board[move.endY][move.endX] = board[move.startY][move.startX];
        board[move.startY][move.startX] = '.';
    }

    public void undoMove(Move move, char capturedPiece) {
        board[move.startY][move.startX] = board[move.endY][move.endX];
        board[move.endY][move.endX] = capturedPiece;
    }

    public char pieceAt(int x, int y) {
        return board[y][x];
    }

    public List<Move> generateMoves() {
        List<Move> moves = new ArrayList<>();
        for (int y = 0; y < BOARD_SIZE; y++) {
            for (int x = 0; x < BOARD_SIZE; x++) {
                if (board[y][x] != '.') {
                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            int ny = y + dy;
                            int nx = x + dx;
                            if (ny >= 0 && ny < BOARD_SIZE && nx >= 0 && nx < BOARD_SIZE) {
                                moves.add(new Move(x, y, nx, ny));
                            }
                        }
                    }
                }
            }
        }
        return moves;
    }
}

class ChessAI {
    public Move findBestMove(Board board, int depth) {
        Move bestMove = null;
        int bestValue = Integer.MIN_VALUE;

        for (Move move : board.generateMoves()) {
            char capturedPiece = board.pieceAt(move.endX, move.endY);
            board.applyMove(move);
            int moveValue = minimax(board, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE, false);
            board.undoMove(move, capturedPiece);
            if (moveValue > bestValue) {
                bestMove = move;
                bestValue = moveValue;
            }
        }

        return bestMove;
    }

    private int evaluate(Board board) {
        return 0; // Simplified evaluation
    }

    private int minimax(Board board, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0) {
            return evaluate(board);
        }

        if (maximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : board.generateMoves()) {
                char capturedPiece = board.pieceAt(move.endX, move.endY);
                board.applyMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, false);
                board.undoMove(move, capturedPiece);
                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : board.generateMoves()) {
                char capturedPiece = board.pieceAt(move.endX, move.endY);
                board.applyMove(move);
                int eval = minimax(board, depth - 1, alpha, beta, true);
                board.undoMove(move, capturedPiece);
                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);
                if (beta <= alpha) {
                    break;
                }
            }
            return minEval;
        }
    }
}

 class ChessGame {
    public static void main(String[] args) {
        Board board = new Board();
        ChessAI ai = new ChessAI();
        Player currentPlayer = Player.HUMAN;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            board.print();

            if (currentPlayer == Player.HUMAN) {
                System.out.print("Enter your move (e.g., e2e4): ");
                String moveStr = scanner.nextLine();
                Move move = parseMove(moveStr);
                if (board.isValidMove(move)) {
                    board.applyMove(move);
                    currentPlayer = Player.AI;
                } else {
                    System.out.println("Invalid move!");
                }
            } else {
                Move bestMove = ai.findBestMove(board, 3);
                board.applyMove(bestMove);
                System.out.println("AI move: " + (char) (bestMove.startX + 'a') + (8 - bestMove.startY)
                        + (char) (bestMove.endX + 'a') + (8 - bestMove.endY));
                currentPlayer = Player.HUMAN;
            }
        }
    }

    private static Move parseMove(String moveStr) {
        return new Move(
                moveStr.charAt(0) - 'a',
                8 - (moveStr.charAt(1) - '0'),
                moveStr.charAt(2) - 'a',
                8 - (moveStr.charAt(3) - '0')
        );
    }
}
