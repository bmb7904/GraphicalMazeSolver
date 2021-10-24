/**
 * Custom class that will represent a Maze. This has functions to solve the Maze.
 */
package bernardi;

import java.util.ArrayList;

public class Maze {
    private char[][] maze;
    private int startX;
    private int startY;
    private long counter;
    private int numRows;
    private int numCols;
    private ArrayList<char[][]> listOfSols;

    /**
     * Constructor. This constructor will depend on a maze
     * already created and is passed in.
     * @param m
     */
    public Maze(char[][] m)
    {
        this.maze = m;
        this.numRows = maze.length;
        this.numCols = maze[0].length;
        findStartX();
        findStartY();
        listOfSols = new ArrayList<>();
        this.counter = 0;

    }

    // Methods to find coordinates of the starting cell
    private void findStartX()
    {
        for(int i = 0; i < numRows; i++)
        {
            for(int j = 0; j < numCols; j++)
            {
                if(maze[i][j] == 'b')
                {
                    this.startX = i;
                }
            }
        }
    }
    private void findStartY()
    {
        for(int i = 0; i < numRows; i++)
        {
            for(int j = 0; j < numCols; j++)
            {
                if(maze[i][j] == 'b')
                {
                    this.startY = j;
                }
            }
        }
    }

    /**
     * Wrapper method to call the recursive findNext() that will
     * solve the maze
     */
    public void solve()
    {
        findNext(startX, startY);
    }

    private void findNext(int x, int y)
    {
        counter++;
        // if current specified cell is the ending,
        // create copy of current state of the maze
        // and add it to list of solutions. Return.
        if(maze[x][y] == 'e') {
            listOfSols.add(copyCurrentMaze());
            return;
        }

        char currentChar = this.maze[x][y];

        // if we arent' currently on 'b'
        if(this.maze[x][y] != 'b') {
            // place 'p'
            this.maze[x][y] = 'p';
        }

        // Now, recurse with it in 4 directions

        // up
        if(validMove(x-1, y)) {
            findNext(x -1, y);
        }

        // down
        if(validMove(x+1, y)) {
            findNext(x+1, y);
        }

        //left
        if(validMove(x, y-1)) {
            findNext(x, y-1);
        }

        //right
        if(validMove(x, y+1)) {
            findNext(x, y+1);
        }

        // after recursing, remove the 'p'
        this.maze[x][y] = currentChar;

    }

    /**
     * private helper method that will create a new maze array that is a
     * deep copy of the current state of the maze attribute.
     * @return
     */
    private char[][] copyCurrentMaze() {
        char[][] newMaze = new char[numRows][numCols];
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++)
            {
                newMaze[i][j] = this.maze[i][j];
            }
        }

        return newMaze;
    }

    /**
     * Checks if position at specified index in maze is a valid move to place a 'p'. It checks
     * if the indices are within bounds and also if the spot was already visited or if is 'b' or
     * also if it a wall or '1'.
     * @param x
     * @param y
     * @return
     */
    private boolean validMove(int x, int y)
    {
        return ( (x < numRows) && (x >= 0) && (y < numCols) && (y >= 0) &&
                (this.maze[x][y] != 'p') && (this.maze[x][y] != 'b') && (this.maze[x][y] != '1') );
    }

    /**
     * Will return a reference to the listOfSols attribute for further processing.
     * @return
     */
    public ArrayList<char[][]> getListOfSols()
    {
        return this.listOfSols;
    }

    public int getStartX()
    {
        return this.startX;
    }

    public int getStartY()
    {
        return this.startY;
    }

    public long getCounter()
    {
        return counter;
    }

}
