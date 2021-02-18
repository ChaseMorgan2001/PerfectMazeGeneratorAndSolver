package com.company;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Maze extends JPanel{
    private int width = 50, height = 50, speed = 95, cellsVisited = 0;
    double percentVisited = 0.0;
    Cell[][] cells;
    boolean generating = false, generated, solving = false, solved;
    Timer genTimer;
    Timer solveTimer;
    Timer showPathTimer;
    JLabel percent = new JLabel();

    Maze(){
        setBackground(Color.BLACK);
        cells = new Cell[50][50];
        initializeCells();
        setVisible(false);
        percent.setHorizontalAlignment(SwingConstants.CENTER);
        percent.setText("Percent Visited: 0%");
        generated = false;
        solved = false;
    }
    Maze(int w, int h, int s){
        setBackground(Color.BLACK);
        cells = new Cell[50][50];
        setWidth(w);
        setHeight(h);
        setSpeed(s);
        initializeCells();
        setVisible(false);
        percent.setHorizontalAlignment(SwingConstants.CENTER);
        percent.setText("Percent Visited: 0%");
        generated = false;
        solved = false;
    }

    void setWidth(int x){width = x;}
    void setHeight(int y){height = y;}
    void setSpeed(int s){speed = s;}

    void generateMaze(Random rand){
        genTimer = new Timer(200 - speed, null);
        Stack<Cell> genPath = new Stack<>();
        cells[0][0].genVisited = true;
        genPath.push(cells[0][0]);
        genTimer.addActionListener(actionEvent -> {
            percent.setText("Percent Visited: 0%");
            revalidate();
            if(!genPath.empty() && generating){
                Cell currentCell = genPath.pop();
                //calculate neighbors
                if (currentCell.x + 1 < width) {
                    if (!cells[currentCell.x + 1][currentCell.y].genVisited) {
                        currentCell.neighbors.add(cells[currentCell.x + 1][currentCell.y]);
                    }
                }
                if (currentCell.y + 1 < height) {
                    if (!cells[currentCell.x][currentCell.y + 1].genVisited) {
                        currentCell.neighbors.add(cells[currentCell.x][currentCell.y + 1]);
                    }
                }
                if (currentCell.x - 1 >= 0) {
                    if (!cells[currentCell.x - 1][currentCell.y].genVisited) {
                        currentCell.neighbors.add(cells[currentCell.x - 1][currentCell.y]);
                    }
                }
                if (currentCell.y - 1 >= 0) {
                    if (!cells[currentCell.x][currentCell.y - 1].genVisited) {
                        currentCell.neighbors.add(cells[currentCell.x][currentCell.y - 1]);
                    }
                }
                if (!currentCell.neighbors.isEmpty()) {
                    genPath.push(currentCell);
                    Cell chosenNeighbor = currentCell.neighbors.get(rand.nextInt(currentCell.neighbors.size()));

                    // remove wall between currentCell and chosenNeighbor
                    if (currentCell.x - chosenNeighbor.x < 0) { // right neighbor
                        currentCell.walls[1] = 0;
                        chosenNeighbor.walls[3] = 0;
                        repaint();
                    } else if (currentCell.x - chosenNeighbor.x > 0) { // left neighbor
                        currentCell.walls[3] = 0;
                        chosenNeighbor.walls[1] = 0;
                        repaint();
                    } else if (currentCell.y - chosenNeighbor.y < 0) { // bottom neighbor
                        currentCell.walls[2] = 0;
                        chosenNeighbor.walls[0] = 0;
                        repaint();
                    } else if (currentCell.y - chosenNeighbor.y > 0) { // top neighbor
                        currentCell.walls[0] = 0;
                        chosenNeighbor.walls[2] = 0;
                        repaint();
                    }

                    chosenNeighbor.genVisited = true;
                    cells[chosenNeighbor.x][chosenNeighbor.y] = chosenNeighbor;
                    cells[currentCell.x][currentCell.y] = currentCell;
                    genPath.push(cells[chosenNeighbor.x][chosenNeighbor.y]);
                    currentCell.neighbors.clear();
                }
            }
            else {
                generating = false;
                generated = true;
                genTimer.stop();
            }
        });

        if (generating) {
            genTimer.start();
        }
    }
    void resetMaze(){
        if (!solving) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    cells[i][j].isCrossroads = false;
                    cells[i][j].isValidPath = false;
                    cells[i][j].isCurrentCell = false;
                    cells[i][j].tremauxVisited = 0;
                    cells[i][j].numNeighbors = 0;
                    cells[i][j].neighbors.clear();
                    cells[i][j].paths.clear();
                    cells[i][j].solveVisited = false;
                    this.solving = false;
                    this.solved = false;
                    this.cellsVisited = 0;
                    repaint();
                }
            }
        }
    }
    void rightWallFollow(){
        solveTimer = new Timer(250 - speed, null);
        Stack<Cell> solvePath = new Stack<>();
        Stack<Cell> prevCells = new Stack<>();
        solvePath.push(cells[0][0]);
        cellsVisited++;
        prevCells.push(cells[0][0]);
        cells[0][0].solveVisited = true;
        cells[0][0].isValidPath = true;
        Cell destCell = cells[width - 1][height - 1];
        solveTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (!solvePath.empty() && !prevCells.empty() && solving) {
                    Cell currentCell = solvePath.peek();
                    currentCell.isCurrentCell = true;
                    Cell prevCell = prevCells.peek();

                    //calculate neighbors
                    if (currentCell.walls[0] == 0 && !cells[currentCell.x][currentCell.y - 1].solveVisited) {currentCell.neighbors.add(cells[currentCell.x][currentCell.y - 1]); } // north neighbor
                    if (currentCell.walls[1] == 0 && !cells[currentCell.x + 1][currentCell.y].solveVisited) {currentCell.neighbors.add(cells[currentCell.x + 1][currentCell.y]); } // east neighbor
                    if (currentCell.walls[2] == 0 && !cells[currentCell.x][currentCell.y + 1].solveVisited) {currentCell.neighbors.add(cells[currentCell.x][currentCell.y + 1]); } // south neighbor
                    if (currentCell.walls[3] == 0 && !cells[currentCell.x - 1][currentCell.y].solveVisited) {currentCell.neighbors.add(cells[currentCell.x - 1][currentCell.y]); } // west neighbor

                    if (currentCell.x == 0 && currentCell.y == 0) {
                        currentCell.isCurrentCell = false;
                        solvePath.push(cells[currentCell.neighbors.get(0).x][currentCell.neighbors.get(0).y]);
                        currentCell = cells[currentCell.neighbors.get(0).x][currentCell.neighbors.get(0).y];
                        currentCell.isCurrentCell = true;
                        currentCell.solveVisited = true;
                        currentCell.isValidPath = true;
                        repaint();
                    }
                    else {
                        cellsVisited++;
                        revalidate();
                        if (!currentCell.neighbors.isEmpty()) { // has neighbors
                            currentCell.isCurrentCell = false;
                            if (currentCell.y - prevCell.y < 0) { // came from south
                                if (cells[currentCell.x][currentCell.y].x + 1 < width) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x + 1][currentCell.y]) && !cells[currentCell.x + 1][currentCell.y].solveVisited) { // has neighbor to right
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x + 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].y - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y - 1]) && !cells[currentCell.x][currentCell.y - 1].solveVisited) { // has neighbor straight ahead
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y - 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].x - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x - 1][currentCell.y])  && !cells[currentCell.x - 1][currentCell.y].solveVisited) { // has neighbor to left
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x - 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                            }
                            else if (currentCell.y - prevCell.y > 0) { // came from north
                                if (cells[currentCell.x][currentCell.y].x - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x - 1][currentCell.y]) && !cells[currentCell.x - 1][currentCell.y].solveVisited) { // has neighbor to right
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x - 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].y + 1 < height) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y + 1])  && !cells[currentCell.x][currentCell.y + 1].solveVisited) { // has neighbor straight ahead
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y + 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].x + 1 < width) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x + 1][currentCell.y])  && !cells[currentCell.x + 1][currentCell.y].solveVisited) { // has neighbor to left
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x + 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                            }
                            else if (currentCell.x - prevCell.x > 0) { // came from west
                                if (cells[currentCell.x][currentCell.y].y + 1 < height) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y + 1]) && !cells[currentCell.x][currentCell.y + 1].solveVisited) { // has neighbor to right
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y + 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].x + 1 < width) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x + 1][currentCell.y]) && !cells[currentCell.x + 1][currentCell.y].solveVisited) { // has neighbor straight ahead
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x + 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].y - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y - 1]) && !cells[currentCell.x][currentCell.y - 1].solveVisited) { // has neighbor to left
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y - 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                            }
                            else if (currentCell.x - prevCell.x < 0) { // came from east
                                if (cells[currentCell.x][currentCell.y].y - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y - 1]) && !cells[currentCell.x][currentCell.y - 1].solveVisited) { // has neighbor to right
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y - 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].x - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x - 1][currentCell.y]) && !cells[currentCell.x - 1][currentCell.y].solveVisited) { // has neighbor straight ahead
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x - 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].y + 1 < height){
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y + 1]) && !cells[currentCell.x][currentCell.y + 1].solveVisited) { // has neighbor to left
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y + 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                            }
                            currentCell.isCurrentCell = true;
                        } else { // no neighbors
                            currentCell.isCurrentCell = false;
                            cellsVisited--;
                            solvePath.pop();
                            currentCell.isValidPath = false;
                            currentCell = prevCells.peek();
                            currentCell.isCurrentCell = true;
                            prevCells.pop();
                            repaint();
                        }

                        percentVisited = ((double) (cellsVisited + 1) / (double) (width * height)) * 100;
                        percent.setText("Percent Visited: " + (int) percentVisited + "%");
                    }
                    cells[prevCell.x][prevCell.y] = prevCell;
                    cells[currentCell.x][currentCell.y] = currentCell;
                    currentCell.neighbors.clear();
                    if (currentCell == destCell) {
                        solving = false;
                        cellsVisited++;
                    }
                }
                if (!solving) {
                    solved = true;
                    solveTimer.stop();
                }
            }
        });
        if (solving){
            solveTimer.start();
        }
    }
    void leftWallFollow(){
        solveTimer = new Timer(250 - speed, null);
        Stack<Cell> solvePath = new Stack<>();
        Stack<Cell> prevCells = new Stack<>();
        solvePath.push(cells[0][0]);
        cellsVisited++;
        prevCells.push(cells[0][0]);
        cells[0][0].solveVisited = true;
        cells[0][0].isValidPath = true;
        Cell destCell = cells[width - 1][height - 1];
        solveTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!solvePath.empty() && !prevCells.empty() && solving) {
                    Cell currentCell = solvePath.peek();
                    currentCell.isCurrentCell = true;
                    Cell prevCell = prevCells.peek();

                    //calculate neighbors
                    if (currentCell.walls[0] == 0 && !cells[currentCell.x][currentCell.y - 1].solveVisited) {currentCell.neighbors.add(cells[currentCell.x][currentCell.y - 1]); } // north neighbor
                    if (currentCell.walls[1] == 0 && !cells[currentCell.x + 1][currentCell.y].solveVisited) {currentCell.neighbors.add(cells[currentCell.x + 1][currentCell.y]); } // east neighbor
                    if (currentCell.walls[2] == 0 && !cells[currentCell.x][currentCell.y + 1].solveVisited) {currentCell.neighbors.add(cells[currentCell.x][currentCell.y + 1]); } // south neighbor
                    if (currentCell.walls[3] == 0 && !cells[currentCell.x - 1][currentCell.y].solveVisited) {currentCell.neighbors.add(cells[currentCell.x - 1][currentCell.y]); } // west neighbor

                    if (currentCell.x == 0 && currentCell.y == 0) {
                        currentCell.isCurrentCell = false;
                        solvePath.push(cells[currentCell.neighbors.get(0).x][currentCell.neighbors.get(0).y]);
                        currentCell = cells[currentCell.neighbors.get(0).x][currentCell.neighbors.get(0).y];
                        currentCell.isCurrentCell = true;
                        currentCell.solveVisited = true;
                        currentCell.isValidPath = true;
                        repaint();
                    }
                    else {
                        cellsVisited++;
                        revalidate();
                        if (!currentCell.neighbors.isEmpty()) { // has neighbors
                            currentCell.isCurrentCell = false;
                            if (currentCell.y - prevCell.y < 0) { // came from south
                                if (cells[currentCell.x][currentCell.y].x - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x - 1][currentCell.y])  && !cells[currentCell.x - 1][currentCell.y].solveVisited) { // has neighbor to left
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x - 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].y - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y - 1]) && !cells[currentCell.x][currentCell.y - 1].solveVisited) { // has neighbor straight ahead
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y - 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].x + 1 < width) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x + 1][currentCell.y]) && !cells[currentCell.x + 1][currentCell.y].solveVisited) { // has neighbor to right
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x + 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                            }
                            else if (currentCell.y - prevCell.y > 0) { // came from north
                                if (cells[currentCell.x][currentCell.y].x + 1 < width) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x + 1][currentCell.y])  && !cells[currentCell.x + 1][currentCell.y].solveVisited) { // has neighbor to left
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x + 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].y + 1 < height) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y + 1])  && !cells[currentCell.x][currentCell.y + 1].solveVisited) { // has neighbor straight ahead
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y + 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].x - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x - 1][currentCell.y]) && !cells[currentCell.x - 1][currentCell.y].solveVisited) { // has neighbor to right
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x - 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                            }
                            else if (currentCell.x - prevCell.x > 0) { // came from west
                                if (cells[currentCell.x][currentCell.y].y - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y - 1]) && !cells[currentCell.x][currentCell.y - 1].solveVisited) { // has neighbor to left
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y - 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].x + 1 < width) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x + 1][currentCell.y]) && !cells[currentCell.x + 1][currentCell.y].solveVisited) { // has neighbor straight ahead
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x + 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].y + 1 < height) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y + 1]) && !cells[currentCell.x][currentCell.y + 1].solveVisited) { // has neighbor to right
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y + 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                            }
                            else if (currentCell.x - prevCell.x < 0) { // came from east
                                if (cells[currentCell.x][currentCell.y].y + 1 < height){
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y + 1]) && !cells[currentCell.x][currentCell.y + 1].solveVisited) { // has neighbor to left
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y + 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].x - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x - 1][currentCell.y]) && !cells[currentCell.x - 1][currentCell.y].solveVisited) { // has neighbor straight ahead
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x - 1][currentCell.y]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                                if (cells[currentCell.x][currentCell.y].y - 1 >= 0) {
                                    if (currentCell.neighbors.contains(cells[currentCell.x][currentCell.y - 1]) && !cells[currentCell.x][currentCell.y - 1].solveVisited) { // has neighbor to right
                                        prevCells.push(cells[currentCell.x][currentCell.y]);
                                        solvePath.push(cells[currentCell.x][currentCell.y - 1]);
                                        currentCell = solvePath.peek();
                                        currentCell.solveVisited = true;
                                        currentCell.isValidPath = true;
                                        repaint();
                                    }
                                }
                            }
                            currentCell.isCurrentCell = true;
                        } else { // no neighbors
                            currentCell.isCurrentCell = false;
                            cellsVisited--;
                            solvePath.pop();
                            currentCell.isValidPath = false;
                            currentCell = prevCells.peek();
                            currentCell.isCurrentCell = true;
                            prevCells.pop();
                            repaint();
                        }

                        percentVisited = ((double) (cellsVisited + 1) / (double) (width * height)) * 100;
                        percent.setText("Percent Visited: " + (int) percentVisited + "%");
                    }
                    cells[prevCell.x][prevCell.y] = prevCell;
                    cells[currentCell.x][currentCell.y] = currentCell;
                    currentCell.neighbors.clear();
                    if (currentCell == destCell) {
                        solving = false;
                        cellsVisited++;
                    }
                }
                if (!solving) {
                    solved = true;
                    solveTimer.stop();
                }
            }
        });
        if (solving){
            solveTimer.start();
        }
    }
    void tremauxSolve(){
        solveTimer = new Timer(250 - speed, null);
        showPathTimer = new Timer(250 - speed, null);
        Stack<Cell> solvePath = new Stack<>();
        Stack<Cell> prevCells = new Stack<>();
        solvePath.push(cells[0][0]);
        cellsVisited++;
        prevCells.push(cells[0][0]);
        Cell destCell = cells[width - 1][height - 1];
        showPathTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!solvePath.empty()){
                    solvePath.peek().isValidPath = true;
                    repaint();
                    solvePath.pop();
                }
                else {
                    showPathTimer.stop();
                }
            }
        });
        solveTimer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                Random rand = new Random();
                if (!solvePath.empty() && !prevCells.empty() && solving) {
                    Cell currentCell = solvePath.peek();
                    currentCell.isCurrentCell = true;
                    Cell prevCell = prevCells.peek();

                    //calculate neighbors
                    if (currentCell.walls[0] == 0 && cells[currentCell.x][currentCell.y - 1].tremauxVisited == 0) {currentCell.neighbors.add(cells[currentCell.x][currentCell.y - 1]); } // north neighbor
                    if (currentCell.walls[1] == 0 && cells[currentCell.x + 1][currentCell.y].tremauxVisited == 0) {currentCell.neighbors.add(cells[currentCell.x + 1][currentCell.y]); } // east neighbor
                    if (currentCell.walls[2] == 0 && cells[currentCell.x][currentCell.y + 1].tremauxVisited == 0) {currentCell.neighbors.add(cells[currentCell.x][currentCell.y + 1]); } // south neighbor
                    if (currentCell.walls[3] == 0 && cells[currentCell.x - 1][currentCell.y].tremauxVisited == 0) {currentCell.neighbors.add(cells[currentCell.x - 1][currentCell.y]); } // west neighbor
                    currentCell.numNeighbors = currentCell.neighbors.size();

                    int numPaths = 0;
                    if (currentCell.walls[0] == 0) {
                        currentCell.paths.add(cells[currentCell.x][currentCell.y - 1]);
                        numPaths++;
                    } // north neighbor
                    if (currentCell.walls[1] == 0) {
                        currentCell.paths.add(cells[currentCell.x + 1][currentCell.y]);
                        numPaths++;
                    } // east neighbor
                    if (currentCell.walls[2] == 0) {
                        currentCell.paths.add(cells[currentCell.x][currentCell.y + 1]);
                        numPaths++;
                    } // south neighbor
                    if (currentCell.walls[3] == 0) {
                        currentCell.paths.add(cells[currentCell.x - 1][currentCell.y]);
                        numPaths++;
                    } // west neighbor

                    if (numPaths > 2){
                        for (int i = 0; i < currentCell.paths.size(); i++){
                            currentCell.paths.get(i).isCrossroads = true;
                        }
                    }

                    currentCell.tremauxVisited++;
                    if (currentCell.x == 0 && currentCell.y == 0) {
                        currentCell.isCurrentCell = false;
                        solvePath.push(cells[currentCell.neighbors.get(0).x][currentCell.neighbors.get(0).y]);
                        currentCell = cells[currentCell.neighbors.get(0).x][currentCell.neighbors.get(0).y];
                        currentCell.isCurrentCell = true;
                        repaint();
                    }
                    else {
                        cellsVisited++;
                        revalidate();
                        if (currentCell.numNeighbors > 1){
                            prevCells.push(currentCell);
                            Cell chosenNeighbor = currentCell.neighbors.get(rand.nextInt(currentCell.neighbors.size()));
                            currentCell.isCurrentCell = false;
                            prevCell = prevCells.peek();
                            currentCell = cells[chosenNeighbor.x][chosenNeighbor.y];
                            solvePath.push(currentCell);
                            currentCell.isCurrentCell = true;
                            repaint();
                        }
                        else if (currentCell.numNeighbors == 1){
                            if (currentCell.tremauxVisited > 1){
                                currentCell.tremauxVisited--;
                            }
                            prevCells.push(currentCell);
                            currentCell.isCurrentCell = false;
                            prevCell = prevCells.peek();
                            currentCell = cells[currentCell.neighbors.get(0).x][currentCell.neighbors.get(0).y];
                            solvePath.push(currentCell);
                            currentCell.isCurrentCell = true;
                            repaint();
                        }
                        else { // no new paths
                            solvePath.pop();
                            prevCell = prevCells.peek();
                            cellsVisited--;
                            currentCell.tremauxVisited = 2;
                            currentCell.isCurrentCell = false;
                            currentCell = cells[prevCell.x][prevCell.y];
                            prevCells.pop();
                            currentCell.isCurrentCell = true;
                            repaint();
                        }
                        currentCell = cells[currentCell.x][currentCell.y];
                    }
                    percentVisited = ((double) (cellsVisited + 1) / (double) (width * height)) * 100;
                    percent.setText("Percent Visited: " + (int) percentVisited + "%");
                    cells[prevCell.x][prevCell.y] = prevCell;
                    cells[currentCell.x][currentCell.y] = currentCell;
                    currentCell.neighbors.clear();
                    if (currentCell == destCell) {
                        currentCell.tremauxVisited++;
                        solving = false;
                        solved = true;
                        cellsVisited++;
                    }
                }
                if (!solving) {
                    showPathTimer.start();
                    solveTimer.stop();
                }
            }
        });
        if (solving){
            solveTimer.start();
        }
    }

    void initializeCells(){
        for (int i = 0; i < width; i++){
            for (int j = 0; j < height; j++){
                cells[i][j] = new Cell();
            }
        }
    }
    void mazeDisplay(Graphics g){
        int offsetX = 30, offsetY = 15, cellSize;
        if (width > height){ cellSize = (600 / width);}
        else {cellSize = (600 / height);}
        setSize(width * cellSize + 200, height * cellSize + 75);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setBackground(Color.BLACK);

        Dimension size = getSize();
        Insets insets = getInsets();

        int w = size.width - insets.left - insets.right;
        int h = size.height - insets.top - insets.bottom;

        g2d.clearRect(0,0,w, h);
        int x, y;
        int boxOffset = (cellSize / 3);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(offsetX, offsetY, cellSize, cellSize);
        g2d.setColor(Color.RED);
        g2d.fillRect((width - 1) * cellSize + offsetX, (height - 1) * cellSize + offsetY, cellSize, cellSize);
        g2d.setColor(Color.GRAY);
        repaint();
        if (generated) { g2d.setColor(Color.WHITE);}
        for (int i = 0; i < width; i++){
            x = i * cellSize + offsetX;
            for (int j = 0; j < height; j++){
                y = j * cellSize + offsetY;
                if (cells[i][j].walls[0] == 1){
                    g2d.drawLine(x, y, x + cellSize, y);
                    repaint();
                }
                if (cells[i][j].walls[1] == 1){
                    g2d.drawLine(x + cellSize, y, x + cellSize, y + cellSize);
                    repaint();
                }
                if (cells[i][j].walls[2] == 1){
                    g2d.drawLine(x, y + cellSize, x + cellSize, y + cellSize);
                    repaint();
                }
                if (cells[i][j].walls[3] == 1){
                    g2d.drawLine(x, y, x, y + cellSize);
                    repaint();
                }
                if (cells[i][j].solveVisited && cells[i][j].isValidPath) { // visited and on path
                    g2d.setColor(Color.YELLOW);
                    g2d.fillRect(x + (cellSize - (cellSize - boxOffset)), y + (cellSize - (cellSize - boxOffset)), cellSize - (2 * boxOffset), cellSize - (2 * boxOffset));
                    g2d.setColor(Color.WHITE);
                    repaint();
                } else if (cells[i][j].solveVisited && !cells[i][j].isValidPath) { // visited but not on path (backtracked)
                    g2d.setColor(Color.GRAY);
                    g2d.fillRect(x + (cellSize - (cellSize - boxOffset)), y + (cellSize - (cellSize - boxOffset)), cellSize - (2 * boxOffset), cellSize - (2 * boxOffset));
                    g2d.setColor(Color.WHITE);
                    repaint();
                }
                if (cells[i][j].tremauxVisited > 1 && cells[i][j].isCrossroads){
                    g2d.drawLine(x + boxOffset, y + boxOffset, x + cellSize - boxOffset, y + cellSize - boxOffset);
                    g2d.drawLine(x + cellSize - boxOffset, y + boxOffset, x + boxOffset, y + cellSize - boxOffset);
                    repaint();
                }
                if (cells[i][j].tremauxVisited == 1 && cells[i][j].isCrossroads){
                    g2d.drawLine(x + boxOffset, y + boxOffset, x + cellSize - boxOffset, y + cellSize - boxOffset);
                    repaint();
                }
                if (cells[i][j].tremauxVisited > 0 && cells[i][j].isValidPath){
                    g2d.setColor(Color.YELLOW);
                    g2d.fillRect(x + (cellSize - (cellSize - boxOffset)), y + (cellSize - (cellSize - boxOffset)), cellSize - (2 * boxOffset), cellSize - (2 * boxOffset));
                    g2d.setColor(Color.WHITE);
                    repaint();
                }
                if (cells[i][j].isCurrentCell){
                    g2d.setColor(Color.CYAN);
                    g2d.fillRect(x + (cellSize - (cellSize - boxOffset)), y + (cellSize - (cellSize - boxOffset)), cellSize - (2 * boxOffset), cellSize - (2 * boxOffset));
                    g2d.setColor(Color.WHITE);
                    repaint();
                }
                cells[i][j].x = i;
                cells[i][j].y = j;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        mazeDisplay(g);
    }
}
