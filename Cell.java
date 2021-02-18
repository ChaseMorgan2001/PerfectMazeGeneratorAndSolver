package com.company;

import java.util.ArrayList;

public class Cell {
    byte[] walls = {1, 1, 1, 1};
    boolean genVisited = false, solveVisited = false, isValidPath = false, isCurrentCell = false, isCrossroads = false;
    int tremauxVisited = 0, numNeighbors = 0;
    int x, y;
    ArrayList<Cell> neighbors = new ArrayList<>(), paths = new ArrayList<>();
}
