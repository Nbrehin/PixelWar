package ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import controller.UIController;

/**
 * Classe décrivant le client TCP.
 */
public class ClientTCP {

	// adresse IP du serveur
	private InetAddress adr;
	// socket de connexion avec le serveur
	private Socket socket;

	// flux objets
	private ObjectOutputStream output;
	private ObjectInputStream input;

	/**
	 * Le port peut aussi être renseigné en ligne de commande. Pour ce faire,
	 * commentez cette ligne, ajoutez un paramètre au constructeur et modifiez la
	 * méthode main pour récupérer le paramètre contenant le numéro de port.
	 */

	private String player_name;

	private UIController controller;

	/**
	 * Constructeur du client TCP initialisant la socket et les flux d'objet pour la
	 * communication avec le serveur.
	 *
	 * @throws IOException
	 */
	public ClientTCP(String adresse_name, int port, String pseudo, UIController controller) throws IOException {
		try {
			adr = InetAddress.getByName(adresse_name);
		} catch (UnknownHostException e) {
			System.err.print("Erreur adresse :");
			e.printStackTrace();
			throw e;
		}
		this.socket = new Socket(adr, port);
		System.out.println("Connexion établie avec le serveur " + adresse_name + ":" + port);
		this.output = new ObjectOutputStream(socket.getOutputStream());
		this.input = new ObjectInputStream(socket.getInputStream());
		this.player_name = pseudo;
		this.controller = controller;
		this.listenToServer();
	}

	/**
	 * Méthode permettant l'envoi d'un message au serveur.
	 *
	 * @param message à envoyer
	 */
	public void send(Object message) throws IOException {
		System.out.println("Envois du message");
		output.writeObject(message);
		output.flush();
		System.out.println("Objet envoyé au serveur");
	}

	/**
	 * Thread de réception des pixels envoyés par le serveur
	 */
	public void listenToServer() {
		new Thread(() -> {
			try {
				while (!socket.isClosed()) {
					try {
						if (input != null) {
							Object received = input.readObject();
							if (received instanceof Pixel) {
								Pixel p = (Pixel) received;
								controller.recevoir_selection_pixel(p.getX(), p.getY(), p.getId(), p.getColor());
							}
						}
					} catch (ClassNotFoundException e) {
						System.err.println("Erreur de classe : " + e.getMessage());
						e.printStackTrace();
					} catch (IOException e) {
						System.err.println("Connexion au serveur perdue : " + e.getMessage());
						break;
					}

				}
			} finally {
				try {
					if (input != null)
						input.close();
					if (output != null)
						output.close();
					if (socket != null)
						socket.close();
					System.out.println("Ressources fermées après déconnexion");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * Getter de l'état du client
	 *
	 * @return l'état de connexion du client
	 */
	public boolean isShutdown() {
		return socket.isInputShutdown() || socket.isOutputShutdown();
	}

	/**
	 * Getter du nom du joueur
	 *
	 * @return le nom du joueur
	 */
	public String getPlayerName() {
		return this.player_name;
	}

	/**
	 * Méthode permettant de fermer la communication avec le serveur.
	 */
	public void close() throws IOException {
		System.out.println("Fermeture de la connexion...");
		if (input != null)
			input.close();
		if (output != null)
			output.close();
		if (socket != null)
			socket.close();
		System.out.println("Connexion fermée.");
	}
}