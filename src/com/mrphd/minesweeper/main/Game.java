package com.mrphd.minesweeper.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Game {

	public static final int WIDTH=640,HEIGHT=640;
	public static final Dimension GRID_DIMENSION = new Dimension(WIDTH,HEIGHT);
	
	public static final GridPanel GRID_PANEL = new GridPanel(8,8);
	
	public Game() {
		JFrame window = new JFrame("Minesweeper");
		window.add(GRID_PANEL);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
	
	public static void main(String[] args) {
		new Game();
	}

	public static final class GridPanel extends JPanel {
		
		private static final long serialVersionUID = 1L;
		
		private final int rows,cols;
		private final List<CellPanel> cells;
		
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
		
		public Object[] getNeighborsAt(final int row,final int col){
			final Stream<CellPanel> neighbors = cells.stream().filter(cell -> 
				(cell.getRow() == row - 1 && (cell.getCol() >= col - 1 && cell.getCol() <= col + 1) ||
				cell.getRow() == row + 1 && (cell.getCol() >= col - 1 && cell.getCol() <= col + 1) ||
				cell.getCol() == col - 1 && (cell.getRow() >= row - 1 && cell.getRow() <= row + 1) ||
				cell.getCol() == col + 1 && (cell.getRow() >= row - 1 && cell.getRow() <= row + 1)) &&
				!CellPanel.hasMine(cell)
			);
			return neighbors.toArray();
		}
		
	}
	
	public static final class CellPanel extends JPanel implements MouseListener {
		
		private static final long serialVersionUID = 1L;
		
		private final GridPanel grid;
		private final int row;
		private final int col;
		private final boolean hasMine;
		
		private JLabel value;
		private boolean revealed;
		
		public CellPanel(final GridPanel grid,final int row,final int col) {
			super(new GridBagLayout());
			this.grid = grid;
			this.row = row;
			this.col = col;
			this.hasMine = (Math.random() < 0.5) ? true : false;
			
			this.value = new JLabel("");
			this.revealed = false;
			
			setBackground(Color.GRAY);
			setBorder(BorderFactory.createLineBorder(Color.BLACK));
			
			add(this.value);
			addMouseListener(this);
		}
		
		public void reveal() {
			this.revealed = true;
			
			// Reveal current cell
			removeAll();
			this.value = (hasMine) ?  Res.MINE_ICON : Res.EMPTY_LABEL;
			add(this.value);
			setBackground(Color.WHITE);
			repaint();
			revalidate();
			
			if(hasMine(this)) return;
			
			// Reveal neighbors
			final List<Object> neighbors = Arrays.asList(grid.getNeighborsAt(row,col));
			neighbors.forEach(obj -> {
				CellPanel cell = (CellPanel) obj;
				if(!hasMine(cell) && !cell.hasRevealed())
					cell.reveal();
			});
			
			Stream<Object> mineCells = neighbors.stream().filter(CellPanel::hasMine);
			int totalMinesNearby = (int) (mineCells.count());
			System.out.println(totalMinesNearby + " mines nearby " + this);
			System.out.println(mineCells + " nearby " + this);
			setValue(totalMinesNearby);
		}
		
		public String toString() {
			return "Cell[" + this.row + "," + this.col + "]";
		}
		
		public static boolean hasMine(final Object cell) {
			return ((CellPanel)cell).hasMine;
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
			if(hasMine(this)) return;
			this.removeAll();
			this.value = new JLabel(/*value == 0 ? "" : */String.valueOf(totalMinesNearby));
			add(this.value);
			repaint();
			revalidate();
		}

		public void mouseReleased(MouseEvent e) {
			if(hasRevealed()) return;
			reveal();
		}

		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		
	}
	
}
