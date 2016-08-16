/******************************************************************************
 *  Name:    Nick Barnett
 *  NetID:   nrbarnet
 *  Precept: P04
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 * 
 *  Description:  Creates an immutable board data type with the API below
 ******************************************************************************/
import edu.princeton.cs.algs4.Stack;

public class Board {
    
    private final int N;
    private int[][] tiles;
    
    // construct a board from an N-by-N array of tiles
    // (where tiles[i][j] = tile at row i, column j)
    public Board(int[][] tiles)            
    {
        this.N = tiles.length;
        this.tiles = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                this.tiles[i][j] = tiles[i][j];
            }
        }  
    }
    
    // return tile at row i, column j (or 0 if blank)
    public int tileAt(int i, int j)        
    {
        if ((i < 0 || i > N-1) || (j < 0 || j > N-1))
            throw new IndexOutOfBoundsException();
        return tiles[i][j];
    }
    
    // board size N
    public int size()                      
    {
        return this.N;
    }
    
    // number of tiles out of place
    public int hamming()     
    {
        int count = 0;
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (tileAt(row, col) != 0 && 
                    tileAt(row, col) != gbTileAt(row, col)) count++; 
            }
        }
        return count;
    }
    
    // sum of Manhattan distances between tiles and goal
    public int manhattan()                 
    {
        int count = 0;
        for (int y = 0; y < N; y++) {
            for (int x = 0; x < N; x++) {
                int tile = tiles[y][x];
                if (tileAt(y, x) != 0 && tileAt(y, x) != gbTileAt(y, x)) {
                    int gbX = (tile - 1) % N; // expected x-coordinate (row)
                    int gbY = (tile - 1) / N; // expected y-coordinate (col)
                    int dx = x - gbX; // distance to expected x-coordinate
                    int dy = y - gbY; // distance to expected y-coordinate
                    count += Math.abs(dx) + Math.abs(dy);
                }
            }
        }
        return count;
    }
    
    // is this board the goal board?
    public boolean isGoal()         
    {
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (tileAt(row, col) != gbTileAt(row, col)) return false; 
            }
        }
        return true;
    }
    
    //returns the tile of the goal board at row i and col j
    private int gbTileAt(int i, int j)
    {
        if (i == N-1 && j == N-1) return 0;
        return i*N + j + 1;
    }
    
    // is this board solvable?
    public boolean isSolvable()   
    {
        //if the board size is odd and the # of inversions is odd, then the
        //board is unsolvable
        if (N % 2 != 0 && inversions() % 2 != 0) return false;
        
        //find the row of the blankspace
        int blankRow = -1;
        boolean blankFound = false;
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (tiles[row][col] == 0) {
                    blankRow = row;
                    blankFound = true;
                    break;
                }
            }
            if (blankFound) break;
        } 
        //assert (blankFound == true && blankRow != -1);
        
        //System.out.println("blankRow: " + blankRow);
            
        //if the board size is even and the number of inversions is odd, then 
        //the board is unsolvable
        if (N % 2 == 0 && ((blankRow + inversions()) % 2 == 0)) return false;
        return true;        
    }

//    Definition: For any other configuration besides the goal,
//    whenever a tile with a greater number on it precedes a
//    tile with a smaller number, the two tiles are said to be inverted
    private int inversions()
    {
        //for me, it was easier to detect an inversion in a 1-D array than it
        //was for a 2-D array
        int[] array = new int[N*N];
        int inversions = 0;
        
        //copy the elements in the tiles array into the new 1-D array by
        //row-major order
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                array[row*N+col] = tiles[row][col];
            }
        }
        
        for (int i = 0; i < N*N; i++) {
            for (int j = i+1; j < N*N; j++) {
                //don't count the inversion if the jth element is the blank
                //space
                
                if (array[j] != 0 && array[j] < array[i]) {
                    //System.out.println(array[j] + " < " + array[i] + " but "
                    //+ array[i] + " comes before " + array[j]);
                    //System.out.println(array[i] + "-" + array[j]);
                    inversions++;
                }
            }
        }
        return inversions;
    }
    
    // does this board equal y?
    public boolean equals(Object y) 
    {
        if (y == this) return true; //optimize for true object equality
        if (y == null) return false; //check for null
        //objects must be in the same class
        if (y.getClass() != this.getClass()) return false;
        
        Board that = (Board) y; //cast is guaranteed to succeed
        if (that.N != N) return false;
        boolean tilesEqual = true;
        //check that all significant fields are the same
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (this.tiles[i][j] != that.tiles[i][j]) tilesEqual = false;
            }
        }
        
        return this.N == that.N && tilesEqual;
    }
    
    // all neighboring boards
    public Iterable<Board> neighbors()    
    { 
        int blankRow = -1;
        int blankCol = -1;
        boolean blankFound = false;
        for (int row = 0; row < N; row++) {
            for (int col = 0; col < N; col++) {
                if (tiles[row][col] == 0) {
                    blankRow = row;
                    blankCol = col;
                    blankFound = true;
                    break;
                }
            }
            if (blankFound) break;
        }
        //assert (blankFound == true && (blankRow != -1 && blankRow != -1));
        
        Stack<Board> boards = new Stack<Board>();
        
        //moving the blankspace up won't result in a NullPointerException
        if (blankRow != 0) {
            Board board = new Board(tiles);
            board.exch(blankRow, blankCol, blankRow - 1, blankCol);
            
            boards.push(board);
        }
        //moving the blankspace down won't result in a NullPointerException
        if (blankRow != N-1) {
            Board board = new Board(tiles);
            board.exch(blankRow, blankCol, blankRow + 1, blankCol);
            boards.push(board);
        }
        //moving the blankspace right won't result in a NullPointerException
        if (blankCol != N-1) {
            Board board = new Board(tiles);
            board.exch(blankRow, blankCol, blankRow, blankCol + 1);
            boards.push(board);
        }
        //moving the blankspace left won't result in a NullPointerException
        if (blankCol != 0) {
            Board board = new Board(tiles);
            board.exch(blankRow, blankCol, blankRow, blankCol - 1);
            boards.push(board);
        }
        return boards;
    }
    
    //helper method for making moves happen on the board
    private void exch(int i, int j, int a, int b) {
        int temp = tiles[i][j];
        tiles[i][j] = tiles[a][b];
        tiles[a][b] = temp;
    }
    
    // string representation of this board (in the output format specified below)
    public String toString()  
    {
        StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        return s.toString();
    }
    
    // unit testing (not graded)
    public static void main(String[] args) 
    { }
}