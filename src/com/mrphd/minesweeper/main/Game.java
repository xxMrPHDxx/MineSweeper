package com.mrphd.minesweeper.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mrphd.minesweeper.main.Game.CellPanel;

public class Game extends JFrame {

	private static final long serialVersionUID = 1L;
	public static final int WIDTH=640,HEIGHT=640;
	public static final Dimension GRID_DIMENSION = new Dimension(WIDTH,HEIGHT);
	public int ROW = 30,COL = 30;

	public static Game game;
	public static GridPanel GRID_PANEL;
	public static Difficulty difficulty = Difficulty.EASY;
	
	public Game(final int row,final int col,final List<String> lines1) {
		super("Minesweeper");
		this.ROW = row;
		this.COL = col;
		GRID_PANEL = lines1 == null ? new GridPanel(ROW,COL) : new GridPanel(ROW,COL,lines1);
		
		final JMenuBar menu = new JMenuBar();
		final JMenu filemenu = new JMenu("File");
		
		final JMenuItem newGame = new JMenuItem("New Game");
		newGame.addActionListener((e)->{
			game = new Game(ROW,COL,null);
		});
		filemenu.add(newGame);
		
		final JMenuItem save = new JMenuItem("Save...");
		save.addActionListener((e)->{
			List<String> lines = new ArrayList<>();
			lines.add(GRID_PANEL.rows + "," + GRID_PANEL.cols);
			lines.addAll(GRID_PANEL.cells.stream().map(CellPanel::getInfo).collect(Collectors.toList()));
			
			final JFileChooser c = new JFileChooser();
			int respond = c.showOpenDialog(GRID_PANEL);
			if(respond == JFileChooser.APPROVE_OPTION) {
				try(BufferedWriter w = new BufferedWriter(new FileWriter(c.getSelectedFile()))) {
					lines.forEach(line -> {
						try {
							w.append(line + "\n");
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					});
					w.close();
					JOptionPane.showMessageDialog(c, "Successfully saved " + c.getSelectedFile(),"Info",JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(c, "Failed to save " + c.getSelectedFile(),"Info",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		filemenu.add(save);
		
		final JMenuItem load = new JMenuItem("Load...");
		load.addActionListener((e)->{			
			final JFileChooser c = new JFileChooser();
			int respond = c.showOpenDialog(GRID_PANEL);
			if(respond == JFileChooser.APPROVE_OPTION) {
				try(BufferedReader r = new BufferedReader(new FileReader(c.getSelectedFile()))) {
					final String[] dimension = r.readLine().split(",");
					removeAll();
					
					List<String> lines = new ArrayList<>();
					String line;
					do {
						line = r.readLine();
						if(line == null) break;
						lines.add(line);
					}while(line != null);
					
					game = new Game(Integer.parseInt(dimension[0]),Integer.parseInt(dimension[1]),lines);
					
					r.close();
					JOptionPane.showMessageDialog(c, "Successfully loaded " + c.getSelectedFile(),"Info",JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(c, "Failed to load " + c.getSelectedFile() + "\n" + e1,"Info",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		filemenu.add(load);
		
		menu.add(filemenu);
		setJMenuBar(menu);
		
		add(GRID_PANEL);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		game = new Game(30,30,null);
	}

	public static final class GridPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private final int rows,cols;
		private final List<CellPanel> cells;
		
		public GridPanel(final int rows,final int cols,final List<String> lines) {
			super(new GridLayout(rows,cols));
			setPreferredSize(GRID_DIMENSION);
			this.rows = rows;
			this.cols = cols;
			this.cells = new ArrayList<>();
			lines.forEach(line -> {
				String[] args = line.split(",");
				final int row = Integer.parseInt(args[0]);
				final int col = Integer.parseInt(args[1]);
				final String value = args[2];
				final boolean hasMine = Boolean.parseBoolean(args[3]);
				final boolean revealed = Boolean.parseBoolean(args[4]);
				final CellPanel cell = new CellPanel(this,row,col,value,hasMine,revealed);
				this.cells.add(cell);
				add(cell);
			});
		}
		
		public GridPanel(final int rows,final int cols) {
			super(new GridLayout(rows,cols));
			setPreferredSize(GRID_DIMENSION);
			this.rows = rows;
			this.cols = cols;
			this.cells = new ArrayList<>();
			IntStream.range(0, rows).forEach(row -> {
				IntStream.range(0, cols).forEach(col -> {
					final CellPanel cell = new CellPanel(this,row,col);
					this.cells.add(cell);
					add(cell);
				});
			});
		}
		
		public int getRows() {
			return this.rows;
		}
		
		public int getCols() {
			return this.cols;
		}
		
		public List<CellPanel> getNeighborsAt(final int row,final int col){
			final List<CellPanel> neighbors = cells.stream().filter(cell -> 
				(cell.getRow() == row - 1 && (cell.getCol() >= col - 1 && cell.getCol() <= col + 1) ||
				cell.getRow() == row + 1 && (cell.getCol() >= col - 1 && cell.getCol() <= col + 1) ||
				cell.getCol() == col - 1 && (cell.getRow() >= row - 1 && cell.getRow() <= row + 1) ||
				cell.getCol() == col + 1 && (cell.getRow() >= row - 1 && cell.getRow() <= row + 1))
			).collect(Collectors.toList());
			return neighbors;
		}
		
	}
	
	public static final class CellPanel extends JPanel implements MouseListener {
		
		private static final long serialVersionUID = 1L;
		
		private final GridPanel grid;
		private final int row;
		private final int col;
		private final boolean hasMine;
		
		private String value;
		private JLabel label;
		private boolean revealed;
		
		public CellPanel(final GridPanel grid,final int row,final int col) {
			super(new GridBagLayout());
			this.grid = grid;
			this.row = row;
			this.col = col;
			this.hasMine = (Math.random() < difficulty.getProb()) ? true : false;
			
			this.value = "";
			this.label = new JLabel(this.value);
			this.revealed = false;
			
			setBackground(Color.GRAY);
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			add(this.label);
			addMouseListener(this);
		}
		
		public CellPanel(final GridPanel grid,final int row,final int col, final String value,final boolean hasMine, boolean revealed) {
			super(new GridBagLayout());
			this.grid = grid;
			this.row = row;
			this.col = col;
			this.hasMine = hasMine;
			this.value = value;
			setValue(Integer.parseInt(value));
			this.label = (revealed && !hasMine) || !revealed ? Res.EMPTY_LABEL : this.label;
			this.revealed = revealed;
			
			setBackground(revealed ? Color.WHITE : Color.GRAY);
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			add(this.label);
			addMouseListener(this);
		}
		
		public static String getInfo(final CellPanel cell) {
			System.out.println(cell.value);
			return cell.row + "," + cell.col + "," + (cell.value == "" ? "0" : Integer.parseInt(cell.value)).toString() + "," + cell.hasMine() + "," + cell.hasRevealed();
		}
		
		public static boolean isNotDiagonal(final CellPanel cell,final int row,final int col) {
			return (cell.getRow() == row || cell.getCol() == col) && !cell.hasMine();
		}
		
		public static boolean notRevealedAndHasNoMine(final CellPanel cell) {
			return !cell.hasRevealed() && cell.hasNoMine();
		}
		
		public void reveal() {
			if(this.hasRevealed()) return;
			
			// Reveal current cell
			this.revealed = true;
			this.removeAll();
			this.label = (this.hasMine()) ?  Res.MINE_ICON : Res.EMPTY_LABEL;
			this.add(this.label);
			this.setBackground(Color.WHITE);
			this.repaint();
			this.revalidate();
			
			if(this.hasMine()) return;
			
			// Reveal neighbors
			final List<CellPanel> neighbors = grid.getNeighborsAt(row,col);
			neighbors.stream().filter(c -> CellPanel.isNotDiagonal(c,row,col)).forEach(CellPanel::reveal);
			
			int totalMinesNearby = neighbors.stream().filter(CellPanel::hasMine).collect(Collectors.toList()).size();
			setValue(totalMinesNearby);
		}
		
		public String toString() {
			return "Cell[" + this.row + "," + this.col + "]";
		}
		
		public boolean hasMine() {
			return this.hasMine;
		}
		
		public boolean hasNoMine() {
			return !this.hasMine();
		}
		
		public boolean hasRevealed() {
			return this.revealed;
		}
		
		public int getRow() {
			return this.row;
		}
		
		public int getCol() {
			return this.col;
		}
		
		public void setValue(final int totalMinesNearby) {
			if(this.hasMine()) return;
			this.removeAll();
			this.value = totalMinesNearby == 0 ? "" : String.valueOf(totalMinesNearby);
			this.label = new JLabel(this.value);
			this.add(this.label);
			this.repaint();
			this.revalidate();
		}

		public void mouseReleased(MouseEvent e) {
			this.reveal();
			
			if(grid.cells.parallelStream().noneMatch(CellPanel::notRevealedAndHasNoMine)){
				System.out.println("You win");
			}
		}

		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		
	}
	
}
