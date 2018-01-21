package com.mrphd.minesweeper.main;

public enum Difficulty {

	BEGINNER(0.08),
	EASY(0.10),
	MEDIUM(0.15),
	HARD(0.18),
	INSANE(0.20);
	
	private final double prob;
	
	private Difficulty(final double prob) {
		this.prob = prob;
	}
	
	public double getProb() {
		return this.prob;
	}
	
}
