package com.company;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class MazeBoard extends JFrame {

    JPanel gameboard = new JPanel();
    JPanel controls = new JPanel();
    JPanel bottomInfo = new JPanel();
    int height = 50, width = 50, Speed = 95;
    Maze m1 = new Maze();
    Maze currentMaze = m1;

    void setUpControls(){

        controls.setSize(300, 500);
        controls.setBackground(Color.WHITE);
        controls.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        JButton generate = new JButton("Generate");
        JLabel algos = new JLabel("Solving Algorithms:");
        JButton rightWallFollow = new JButton("Right Wall Follower");
        JButton leftWallFollow = new JButton("Left Wall Follower");
        JButton tremaux = new JButton("Tremaux's Algorithm");
        JButton stop = new JButton("Stop");
        JButton reset = new JButton("Reset");
        Dimension d = new Dimension(150, 30);
        generate.setPreferredSize(d);
        rightWallFollow.setPreferredSize(d);
        leftWallFollow.setPreferredSize(d);
        tremaux.setPreferredSize(d);
        stop.setPreferredSize(d);
        reset.setPreferredSize(d);
        JLabel sp = new JLabel("Speed:");
        sp.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel row = new JLabel("Rows: " + height);
        row.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel col = new JLabel("Columns: " + width);
        col.setHorizontalAlignment(SwingConstants.CENTER);
        JSlider speed = new JSlider(0, 190);
        JSlider rows = new JSlider(10, 50);
        rows.setMajorTickSpacing(10);
        rows.setMinorTickSpacing(5);
        rows.setPaintTicks(true);
        JSlider cols = new JSlider(10, 50);
        cols.setMajorTickSpacing(10);
        cols.setMinorTickSpacing(5);
        cols.setPaintTicks(true);
        rows.setValue(50);
        cols.setValue(50);

        JPanel generatePanel = new JPanel();
        JPanel solvePanel = new JPanel();
        GridBagLayout gl = new GridBagLayout();
        GridBagConstraints gc = new GridBagConstraints();
        solvePanel.setLayout(gl);
        JPanel stopPanel = new JPanel();
        JPanel speedPanel = new JPanel();
        speedPanel.setLayout(new GridLayout(2,1));
        JPanel rowsPanel = new JPanel();
        rowsPanel.setLayout(new GridLayout(2,1));
        JPanel colsPanel = new JPanel();
        colsPanel.setLayout(new GridLayout(2,1));

        rows.setBackground(Color.WHITE);
        cols.setBackground(Color.WHITE);
        speed.setBackground(Color.WHITE);
        generatePanel.setBackground(Color.WHITE);
        solvePanel.setBackground(Color.WHITE);
        stopPanel.setBackground(Color.WHITE);
        rowsPanel.setBackground(Color.WHITE);
        colsPanel.setBackground(Color.WHITE);
        speedPanel.setBackground(Color.WHITE);

        generatePanel.add(generate);
        gc.gridwidth = 2;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.ipady = 50;
        solvePanel.add(algos, gc);
        gc.gridwidth = 1;
        gc.gridy = 1;
        gc.ipady = 0;
        gc.insets = new Insets(0, 0, 0, 15);
        solvePanel.add(rightWallFollow, gc);
        gc.gridx = 1;
        solvePanel.add(leftWallFollow, gc);
        gc.gridy = 2;
        gc.gridx = 0;
        gc.insets = new Insets(15, 0, 0, 15);
        solvePanel.add(tremaux, gc);
        stopPanel.add(stop);
        stopPanel.add(reset);
        speedPanel.add(sp);
        speedPanel.add(speed);
        rowsPanel.add(row);
        rowsPanel.add(rows);
        colsPanel.add(col);
        colsPanel.add(cols);

        gbc.gridx = 0;
        gbc.gridy = 0;
        controls.add(generatePanel, gbc);
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.gridheight = 3;
        gbc.gridy = 1;
        controls.add(solvePanel, gbc);
        gbc.gridheight = 1;
        gbc.gridy = 4;
        controls.add(speedPanel, gbc);
        gbc.gridy = 5;
        controls.add(rowsPanel, gbc);
        gbc.gridy = 6;
        controls.add(colsPanel, gbc);
        gbc.gridy = 7;
        controls.add(stopPanel, gbc);

        rows.addChangeListener(changeEvent -> {
            height = rows.getValue();
            row.setText("Rows: " + height);
            currentMaze.setHeight(height);
            currentMaze.initializeCells();
            currentMaze.generated = false;
            repaint();
        });
        cols.addChangeListener(changeEvent -> {
            width = cols.getValue();
            col.setText("Columns: " + width);
            currentMaze.setWidth(width);
            currentMaze.initializeCells();
            currentMaze.generated = false;
            repaint();
        });
        speed.addChangeListener(changeEvent -> {
            Speed = speed.getValue();
        });

        generate.addActionListener(actionEvent -> {
            Random rand = new Random();
            Maze m2 = new Maze(width, height, Speed);
            gameboard.remove(currentMaze);
            currentMaze = m2;
            setUpGameboard();
            currentMaze.generating = true;
            currentMaze.generateMaze(rand);
        });
        rightWallFollow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (!currentMaze.generating && currentMaze.generated) {
                    currentMaze.resetMaze();
                    currentMaze.solving = true;
                    currentMaze.rightWallFollow();
                }
            }
        });
        leftWallFollow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (!currentMaze.generating && currentMaze.generated) {
                    currentMaze.resetMaze();
                    currentMaze.solving = true;
                    currentMaze.leftWallFollow();
                }
            }
        });
        tremaux.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!currentMaze.generating && currentMaze.generated){
                    currentMaze.resetMaze();
                    currentMaze.solving = true;
                    currentMaze.tremauxSolve();
                }
            }
        });
        stop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (currentMaze.generating){
                    currentMaze.genTimer.stop();
                }
                else if (currentMaze.solving){
                    currentMaze.solveTimer.stop();
                    currentMaze.showPathTimer.stop();
                    currentMaze.solving = false;
                }
            }
        });
        reset.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                currentMaze.resetMaze();
            }
        });
    }

    void setUpGameboard(){
        gameboard.setLayout(new GridLayout(1,1));
        gameboard.setSize(600,850);
        gameboard.setBackground(Color.BLACK);
        gameboard.add(currentMaze);
        currentMaze.setVisible(true);
        currentMaze.repaint();
    }

    void setUpBottomInfo(){
        bottomInfo.setLayout(new GridLayout(1, 1));
        bottomInfo.setSize(600, 75);
        bottomInfo.setBackground(Color.LIGHT_GRAY);
        Timer timer = new Timer(200 - Speed, null);
        timer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                bottomInfo.removeAll();
                bottomInfo.add(currentMaze.percent);
            }
        });
        timer.start();
    }

    MazeBoard(){

        Container c = getContentPane();
        c.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridwidth = 3;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 650;
        gbc.ipady = 630;
        c.add(gameboard, gbc);

        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridheight = 2;
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.ipadx = 75;
        gbc.ipady = 100;
        c.add(controls, gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipadx = 0;
        gbc.ipady = 20;
        c.add(bottomInfo, gbc);

        setUpControls();
        setUpGameboard();
        setUpBottomInfo();
        setVisible(true);
        setSize(1100,715);
        c.setBackground(Color.WHITE);
    }
    public static void main(String[] args) {
        MazeBoard maze = new MazeBoard();
        maze.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }
}
