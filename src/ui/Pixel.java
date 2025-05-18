package ui;

import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.Timer;

import javafx.scene.paint.Paint;

/**
 * Classe décrivant le Pixel, objet transmis entre clients et serveur
 */
public class Pixel implements Serializable {
	private static final long serialVersionUID = -2482955811369381413L;
	private String color;
	private int x;
	private int y;
	private String id;
	private Timer timer;
	private boolean est_verrouille;

	/**
	 * Contructeur de la classe Pixel
	 * 
	 * @param x coordonée x du pixel (après le floor)
	 * @param y coordonée y du pixel (après le floor)
	 * @param id pseudo du joueur qui a posé le pixel
	 * @param color couleur choisie par le joueur ayant posé le pixel
	 */
	public Pixel(int x, int y, String id, Paint color) {
		this.timer = new Timer(60000, new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				timerEvt(e);
			}
		});
		this.timer.setInitialDelay(60000);
		this.x = x;
		this.y = y;
		this.id = id;
		this.color = color.toString();
		this.est_verrouille = true;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public String getColor() {
		return color;
	}

	public String getId() {
		return this.id;
	}

	public boolean getEst_verrouille() {
		return this.est_verrouille;
	}

	public Timer getTimer() {
		return this.timer;
	}

	public void verrouillage() {
		this.timer.start();
		this.est_verrouille = true;
	}

	public void deverrouillage() {
		this.timer.stop();
		this.est_verrouille = false;
	}

	private void timerEvt(java.awt.event.ActionEvent e) {
		this.deverrouillage();
	}

}