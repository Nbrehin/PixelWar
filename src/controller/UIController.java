package controller;

import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Timer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import ui.ClientTCP;
import ui.Pixel;

public class UIController {

	@FXML
	private TextField pseudo_field;

	@FXML
	private TextField addresse_field;

	@FXML
	private TextField port_field;

	@FXML
	private Button connexion_btn;

	@FXML
	private Label etat_connexion_label;

	@FXML
	private Button deconnexion_btn;

	@FXML
	private Label timer;

	@FXML
	private Rectangle couleur_choisie_rect;

	@FXML
	private ColorPicker color_picker_btn;

	@FXML
	private GridPane grille;

	private ClientTCP client;

	private Paint color;

	private int temps_restant = 30;

	private Timeline timeline;

	private Timer wait_time;

	final int TAILLE_GRILLE = 20;

	private Pixel pixels[][] = new Pixel[TAILLE_GRILLE][TAILLE_GRILLE];

	/**
	 * Ensemble de méthodes d'affichage de messages popup
	 * @param message
	 * @param type
	 */
	private void afficherPopup(String message, AlertType type) {
		Alert alert = new Alert(type);
		if (type == AlertType.ERROR) {
			alert.setTitle("Erreur");
		} else {
			alert.setTitle("Information");
		}
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.setResizable(true);
		alert.showAndWait();
	}

	private void afficherPopupErreur(String message) {
		this.afficherPopup(message, AlertType.ERROR);
	}

	private void afficherPopupInformation(String message) {
		this.afficherPopup(message, AlertType.INFORMATION);
	}

	/**
	 * Méthode d'affichage du temps d'attente pour le joueur
	 */
	public void lancerChrono() {
		temps_restant = 29;
		if (timeline != null) {
			timeline.stop();
		}

		timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
			if (temps_restant > 9) {
				timer.setText("00:" + temps_restant);
				temps_restant--;
			} else if (temps_restant > 0 && temps_restant <= 9) {
				timer.setText("00:0" + temps_restant);
				temps_restant--;
			} else {
				timer.setText("00:00");
				timeline.stop();
			}
		}));

		timeline.setCycleCount(30);
		timeline.play();
	}

	/**
	 * Initialisation des variables et méthodes nécessaires
	 */
	public void initialize() {
		// Gestion du clique sur le GridPane
		grille.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent evt) {
				grilleClicked(evt);
			}
		});

		// Arrêt du timer à la fin des 30 secondes
		wait_time = new Timer(30000, new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				wait_time.stop();
			}
		});
		wait_time.setInitialDelay(30000);

		// Désactivation des éléments graphiques 
		deconnexion_btn.setDisable(true);
		color_picker_btn.setDisable(true);
		grille.setDisable(true);

		// Définition d'une couleur par défaut
		color = color_picker_btn.getValue();
		couleur_choisie_rect.setFill(color);

		// Initialisation de la matrice
		for (int i = 0; i < TAILLE_GRILLE; i++) {
			for (int j = 0; j < TAILLE_GRILLE; j++) {
				pixels[i][j] = new Pixel(i, j, "default", Paint.valueOf("WHITE"));
				pixels[i][j].deverrouillage();
			}
		}
	}

	/**
	 * Connexion du client
	 */
	@FXML
	private void connexion_btn_clic(ActionEvent event) throws IOException {
		// Message popup si un champ est vide
		if (pseudo_field.getText().isEmpty() || addresse_field.getText().isEmpty() || port_field.getText().isEmpty()) {
			afficherPopupErreur("Veillez à ne laisser aucun champ de saisie vide.");
		}
		// Ou si le port est privé
		else if (Integer.parseInt(port_field.getText()) <= 1023 || Integer.parseInt(port_field.getText()) >= 49152) {
			afficherPopupErreur("Le port demandé n'est pas disponible.");
		} else {
			try {
				client = new ClientTCP(addresse_field.getText(), Integer.parseInt(port_field.getText()),
						pseudo_field.getText(), this);
			} catch (IOException e) {
				System.err.print("Erreur client: ");
				e.printStackTrace();
			}
			// Si la connexion s'effectue
			if (!client.isShutdown()) {
				etat_connexion_label.setText("Connecté");

				pseudo_field.setDisable(true);
				addresse_field.setDisable(true);
				port_field.setDisable(true);
				connexion_btn.setDisable(true);

				deconnexion_btn.setDisable(false);
				color_picker_btn.setDisable(false);
				grille.setDisable(false);

			}
		}
	}

	/**
	 * Déconnexion du client
	 *
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void deconnexion_btn_clic(ActionEvent event) throws IOException {
		if (client != null)
			client.close();
		
		if (client.isShutdown()) {
			etat_connexion_label.setText("Déconnecté");

			pseudo_field.setDisable(false);
			addresse_field.setDisable(false);
			port_field.setDisable(false);
			connexion_btn.setDisable(false);
	
			deconnexion_btn.setDisable(true);
			color_picker_btn.setDisable(true);
			grille.setDisable(true);
		}
	}

	/**
	 * Selection de la couleur des pixels
	 *
	 * @param event
	 * @throws IOException
	 */
	@FXML
	private void color_picker_btn_select(ActionEvent event) throws IOException {
		color = color_picker_btn.getValue();
		couleur_choisie_rect.setFill(color);
	}

	/**
	 * Méthode d'envoie d'un pixel via le client
	 *
	 * @param x coordonée x du pixel (après le floor)
	 * @param y coordonée y du pixel (après le floor)
	 * @param id pseudo du joueur qui a posé le pixel
	 */
	public void envoyer_selection_pixel(int x, int y, String id) {
		Pixel p = new Pixel(x, y, id, color);
		pixels[x][y] = p;
		wait_time.start();
		try {
			client.send(p);
			timer.setText("00:30");
			this.lancerChrono();
		} catch (IOException e) {
			System.out.print("Erreur envois pixel : ");
			e.printStackTrace();
		}
	}

	/**
	 * Méthode de création d'un pixel dont les informations ont été reçu dans le client
	 * 
	 * @param x coordonée x du pixel (après le floor)
	 * @param y coordonée y du pixel (après le floor)
	 * @param id pseudo du joueur qui a posé le pixel
	 * @param color couleur du pixel choisie par le joueur l'ayant posé
	 */
	public void recevoir_selection_pixel(int x, int y, String id, String color) {
		Pane pane = new Pane();
		pane.setBackground(new Background(
				new BackgroundFill(Paint.valueOf(color), CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
		Platform.runLater(() -> {
			grille.add(pane, x, y);
		});
		Pixel p = new Pixel(x, y, id, Paint.valueOf(color));
		p.verrouillage();
		pixels[x][y] = p;
	}

	/**
	 * Méthode de gestion des cliques sur la grille
	 * 
	 * @param evt
	 */
	private void grilleClicked(MouseEvent evt) {
		if (!this.wait_time.isRunning()) {
			double x = evt.getX();
			double y = evt.getY();

			Pane pane = new Pane();
			// grile 20x20
			int x_grille = (int) Math.floor((x * 20) / grille.getWidth());
			int y_grille = (int) Math.floor((y * 20) / grille.getHeight());

			if (!pixels[x_grille][y_grille].getEst_verrouille()) {
				if (!pixels[x_grille][y_grille].getId().equals(client.getPlayerName())) {
					pane.setBackground(
							new Background(new BackgroundFill(color, CornerRadii.EMPTY, javafx.geometry.Insets.EMPTY)));
					grille.add(pane, x_grille, y_grille);
					envoyer_selection_pixel(x_grille, y_grille, client.getPlayerName());
				} else {
					afficherPopupInformation("Ce pixel vous appartient déjà");
				}
			} else {
				afficherPopupInformation("Veuillez attendre la fin de l'embargo du pixel");
			}
		} else {
			afficherPopupInformation("Veuillez attendre la fin de votre timer");
		}
	}

}