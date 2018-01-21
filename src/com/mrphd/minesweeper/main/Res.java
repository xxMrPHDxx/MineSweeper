package com.mrphd.minesweeper.main;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Res {

	public static final JLabel MINE_ICON = new JLabel(loadImageIcon("res/mine.png"),JLabel.CENTER);
	public static final JLabel EMPTY_LABEL = new JLabel("");
	
	public static ImageIcon loadImageIcon(final String path) {
		final Image image;
		try {
			image = ImageIO.read(new File(path));
		}catch(Exception e) {
			throw new RuntimeException("Failed to load " + path);
		}
		final ImageIcon icon = new ImageIcon(
			image.getScaledInstance(
					Game.WIDTH / Game.GRID_PANEL.getCols(),
					Game.HEIGHT / Game.GRID_PANEL.getRows(),
					BufferedImage.SCALE_SMOOTH)
		);
		return icon;
	}
	
}
