import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class ColumnsGUI {
    private Timer timer;
    private JFrame frame;
    private JPanel gameScreen;
    private JPanel boardPanel;
    private JPanel infoPanel;
    private JLabel scoreLabel;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem start;
    private Columns game;
    private String playerName;

    public void runGUI(String newPlayerName)
    {
        playerName = newPlayerName;
        game = new Columns(playerName);
        frame = new JFrame("Columns - " + game.getPlayerName().toUpperCase());
        gameScreen = new JPanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(gameScreen);
        setMenuBar();
        setGameScreen();

        frame.setSize(400, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void setMenuBar()
    {
        menuBar = new JMenuBar();
        menu = new JMenu("Game Options");
        start = new JMenuItem("Start New Game");

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer != null)
                {
                    timer.stop();
                }
                frame.getContentPane().removeAll();
                game = new Columns(playerName);
                setGameScreen();
                frame.setVisible(true);

                Random rand = new Random();
                int randCol = rand.nextInt(game.getCols()) + 1;
                game.initializeFaller(randCol);
                boardPanel.updateUI();
                boardPanel.removeAll();
                setBoardPanel();
                timer = new Timer(500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (game.isFrozen() && game.getNumMatched() == 0)
                        {
                            Random rand = new Random();
                            int randCol = rand.nextInt(game.getCols()) + 1;
                            game.initializeFaller(randCol);
                        }
                        else
                        {
                            if (game.getNumMatched() > 0)
                            {
                                timer.setDelay(20000);
                                game.deleteMatched();
                                timer.setDelay(500);
                            }

                            game.dropFaller();
                            game.checkMatch();
                        }

                        boardPanel.updateUI();
                        boardPanel.removeAll();
                        setBoardPanel();

                        infoPanel.updateUI();
                        infoPanel.removeAll();
                        setScoreLabel();
                    }
                });
                timer.start();
            }
        });

        menu.add(start);
        menuBar.add(menu);
        frame.setJMenuBar(menuBar);
    }

    public void setGameScreen()
    {
        JPanel contentPane = (JPanel) frame.getContentPane();
        int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap inputMap = contentPane.getInputMap(condition);
        ActionMap actionMap = contentPane.getActionMap();

        String up = "up";
        String down = "down";
        String left = "left";
        String right = "right";

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), up);
        actionMap.put(up, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.rotateFaller();
                boardPanel.updateUI();
                boardPanel.removeAll();
                setBoardPanel();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), down);
        actionMap.put(down, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (game.getNumMatched() > 0)
                {
                    game.deleteMatched();
                    timer.setDelay(500);
                }

                game.dropFaller();
                game.checkMatch();

                boardPanel.updateUI();
                boardPanel.removeAll();
                setBoardPanel();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), left);
        actionMap.put(left, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.moveFallerLeft();
                boardPanel.updateUI();
                boardPanel.removeAll();
                setBoardPanel();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), right);
        actionMap.put(right, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.moveFallerRight();
                boardPanel.updateUI();
                boardPanel.removeAll();
                setBoardPanel();
            }
        });

        frame.getContentPane().setLayout(new BorderLayout());

        infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout());
        setScoreLabel();

        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(game.getRows(), game.getCols()));
        setBoardPanel();
        frame.getContentPane().add(boardPanel, BorderLayout.CENTER);
    }

    public void setBoardPanel()
    {
        for (int i = 0 ; i < game.getRows(); i++)
        {
            for (int j = 0; j < game.getCols(); j++)
            {
                JPanel piecePanel = new JPanel();
                piecePanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));

                if (game.getNumMatched() > 0)
                {
                    for (ArrayList<Integer> match: game.getMatched())
                    {
                        if (i == match.get(0) && j == match.get(1))
                        {
                            piecePanel.setBorder(BorderFactory.createLineBorder(Color.WHITE, 5));
                        }
                    }
                }

                if (game.getCell(i, j) ==  0)
                {
                    piecePanel.setBackground(Color.BLACK);
                }
                else if (game.getCell(i, j) == 1)
                {
                    piecePanel.setBackground(Color.RED);
                }
                else if (game.getCell(i, j) == 2)
                {
                    piecePanel.setBackground(Color.ORANGE);
                }
                else if (game.getCell(i, j) == 3)
                {
                    piecePanel.setBackground(Color.GREEN);
                }
                else if (game.getCell(i, j) == 4)
                {
                    piecePanel.setBackground(Color.BLUE);
                }
                else if (game.getCell(i, j) == 5)
                {
                    piecePanel.setBackground(Color.MAGENTA);
                }
                boardPanel.add(piecePanel);
            }
        }
    }

    public void setScoreLabel()
    {
        scoreLabel = new JLabel("", SwingConstants.CENTER);
        if (game.isGameOver())
        {
            game.handleGameOver();
            timer.stop();
            scoreLabel.setText("GAME OVER");
            scoreLabel.setForeground(Color.WHITE);
            infoPanel.setBackground(Color.RED);
            scoreLabel.setFont(new Font("Calibri", Font.BOLD, 20));
            System.out.println("Final Score: " + game.getScore());
        }

        else
        {
            scoreLabel.setText("SCORE: " + game.getScore());
            scoreLabel.setForeground(Color.WHITE);
            infoPanel.setBackground(Color.BLACK);
            scoreLabel.setFont(new Font("Calibri", Font.BOLD, 20));
        }
        infoPanel.add(scoreLabel);
        frame.getContentPane().add(infoPanel, BorderLayout.NORTH);
    }
}