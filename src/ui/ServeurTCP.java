package ui;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe décrivant le serveur TCP de l'application.
 */
public class ServeurTCP {

	/**
	 * Socket d'écoute du serveur.
	 */
	private ServerSocket serverSocket;
	private Socket[] clientSockets = new Socket[3];
	private int nbClientsConnectes;
	private ObjectInputStream[] input = new ObjectInputStream[3];
	private ObjectOutputStream[] output = new ObjectOutputStream[3];
	private MonThread[] threads = new MonThread[3];

	/**
	 * Le port peut aussi être renseigné en ligne de commande. Pour ce faire,
	 * commentez cette ligne, ajoutez un paramètre au constructeur et modifiez la
	 * méthode main pour récupérer le paramètre contenant le numéro de port.
	 */
	// private int port = 7777;

	/**
	 * Constructeur du serveur TCP initialisant la socket d'écoute sur le port.
	 */
	public ServeurTCP(int port) {
		this.nbClientsConnectes = 0;
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Erreur serveur.");
			e.printStackTrace();
		}

	}

	/**
	 * Thread de réception des pixels envoyés par un client et transmis aux restants
	 */
	class MonThread extends Thread {
		private int index;
		private boolean running = true;

		public MonThread(int index) {
			this.index = index;
		}

		public void run() {
			try {
				while (running && !clientSockets[this.index].isClosed()) {
					try {
						System.out.println("Début de réception");
						if (input[index] != null) {
							Object reception = input[index].readObject();
							if (reception instanceof Pixel) {
								System.out.println("Réception effectuée");
								Pixel pixel_recu = (Pixel) reception;
								for (int i = 0; i < nbClientsConnectes; i++) {
									if (i != this.index && output[i] != null) {
										output[i].writeObject(pixel_recu);
										output[i].flush();
										System.out.println("Pixel envoyé");
									}
								}
							}
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						System.out.println("Client " + index + " déconnecté");
						running = false;
					}
				}
			} finally {
				try {
					if (input[index] != null)
						input[index].close();
					if (output[index] != null)
						output[index].close();
					if (clientSockets[index] != null)
						clientSockets[index].close();
					System.out.println("Connexion fermée pour le client " + index);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Méthode lancant le serveur.
	 * 
	 * @throws IOException
	 */
	public void startServeur() throws IOException {
		System.out.println("Démarrage du serveur.");
		while (true) {
			// à compléter
			try {
				this.clientSockets[this.nbClientsConnectes] = serverSocket.accept();
				this.input[this.nbClientsConnectes] = new ObjectInputStream(
						this.clientSockets[this.nbClientsConnectes].getInputStream());
				this.output[this.nbClientsConnectes] = new ObjectOutputStream(
						this.clientSockets[this.nbClientsConnectes].getOutputStream());
				this.threads[this.nbClientsConnectes] = new MonThread(this.nbClientsConnectes);
				this.threads[this.nbClientsConnectes].start();
				this.nbClientsConnectes += 1;
				System.out.println("Nouveau Client");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Ferme la socket.
	 */
	public void close() {
		try {
			for (int i = 0; i < this.nbClientsConnectes; i++) {
				if (this.input[i] != null)
					this.input[i].close();
				if (this.output[i] != null)
					this.output[i].close();
			}
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		while (true) {
			try {
				ServeurTCP serveur = new ServeurTCP(Integer.parseInt(args[0]));
				serveur.startServeur();
				serveur.close();
			} catch (IOException e) {
				System.err.println("Erreur serveur.");
			}
		}
	}
}
