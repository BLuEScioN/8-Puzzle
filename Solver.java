/******************************************************************************
 *  Name:    Nick Barnett
 *  NetID:   nrbarnet
 *  Precept: P04
 *
 *  Partner Name:    N/A
 *  Partner NetID:   N/A
 *  Partner Precept: N/A
 * 
 *  Description:  Creates an immutable solver data type with the API below
 ******************************************************************************/
    import edu.princeton.cs.algs4.MinPQ;
    import edu.princeton.cs.algs4.StdOut;
    import edu.princeton.cs.algs4.In;
    import edu.princeton.cs.algs4.Stack;
    import edu.princeton.cs.algs4.Stopwatch;
    
    public class Solver {
        
        private SearchNode end;
        
        private class SearchNode implements Comparable<SearchNode>
        {
            private Board board;
            private int moves;
            private int priority;
            private SearchNode previous;
            
            public SearchNode(Board board, int moves, SearchNode previous) {
                this.board = board;
                this.moves = moves;
                //priority = board.manhattan() + moves; //hamming or manhattan
                priority = board.hamming() + moves;
                this.previous = previous;
            }
            
            public int compareTo(SearchNode that) {
                if (this.priority > that.priority) return 1;
                if (this.priority < that.priority) return -1;
                return 0;
            }
        }
        
        // find a solution to the initial board (using the A* algorithm)
        public Solver(Board initial)       
        {
            //check if initial board is null
            if (initial == null) throw new NullPointerException();
            //check if board is solvable
            if (!initial.isSolvable()) throw new IllegalArgumentException();
            //create min priority queue
            MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
            //instantiate the root of the game tree
            SearchNode root = new SearchNode(initial, 0, null);
            pq.insert(root);
            //remove the node with the smallest priority from the priority queue
            //and processes it by adding its children to both the game tree and the
            //priority queue. Repeat until the solution's board is equal to the 
            //game board
            while (true) {
                SearchNode sn = pq.delMin();
                if (sn.board.isGoal()) {
                    end = sn;
                    //since the board has already been checked that it's 
                    //solvable, this if statement guarantees a break from the 
                    //while loop
                    break;
                }            
                //iterate through each of the search node's neighbors
                for (Board neighbor: sn.board.neighbors()) {
                    //check the base case and that the neighbor is not the same
                    //as the previous search node's board
                    if (sn.previous == null || 
                        !neighbor.equals(sn.previous.board)) {
                        SearchNode child =
                            new SearchNode(neighbor, sn.moves + 1, sn);
                        pq.insert(child);
                    }
                }
            }
        }
        
        // min number of moves to solve initial board
        public int moves()             
        {
            return end.moves;
        }
        
        // sequence of boards in a shortest solution
//    Since each search node records the previous search node to get there,
//    you can chase the pointers all the way back to the initial search node 
//    (and consider them in reverse order).
        public Iterable<Board> solution()     
        {
            Stack<Board> solution = new Stack<Board>();
            //check if the board has a solution
            
            if (end != null) {
                //follow the pointers from the end of the game tree back to the 
                //start
                SearchNode snTracer = end;
                while (snTracer != null) {
                    solution.push(snTracer.board);
                    snTracer = snTracer.previous;
                }
                return solution;
            }
            else {
                solution = null;
                return solution;
            }
        }
        
// solve a slider puzzle (given below) 
        public static void main(String[] args) 
        {
            // create initial board from file
            In in = new In(args[0]);
            int N = in.readInt();
            int[][] tiles = new int[N][N];
            for (int i = 0; i < N; i++)
                for (int j = 0; j < N; j++)
                tiles[i][j] = in.readInt();
            Board initial = new Board(tiles);
            
            //System.out.println(initial.inversions());
            
            // check if puzzle is solvable; if so, solve it and output solution
            Stopwatch stopwatch = new Stopwatch(); //Start timer
            if (initial.isSolvable()) {
                Solver solver = new Solver(initial);
                double time = stopwatch.elapsedTime(); //stop timer
                System.out.println("Time: " + time);
                StdOut.println("Minimum number of moves = " + solver.moves());
                for (Board board : solver.solution())
                    StdOut.println(board);
            }
            
            // if not, report unsolvable
            else {
                StdOut.println("Unsolvable puzzle");
            }
        }
    }