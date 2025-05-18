# PixelWar
Ce mini-projet de RIP-IHM consiste à implémenter une application permettant à au moins 3 joueurs de sélectionner et colorier des cases en parallèle.

Le serveur est lancé en premier. Il ouvre une socket TCP d'attente de connexion.
Le serveur se lance en ligne de commande en précisant en paramètre le numéro de port de sa socket d'attente de connexion.
Quand un utilisateur lance un client, il précise dans la aprtie de gauche de sa fenêtre, l'adresse et le port de la socket du serveur et un pseudo. La connexion avec la partie serveur se fait en cliquant sur le bouton 'Connexion'.
Une fois connecté au serveur, l'interface utilisateur affiche l'état de la connexion (ex. 'Inconnu' devient 'Connecté'). Les éléments permettant la connexion deviennet inactifs. Le bouton déconnexion, le color picker et la grille deviennent actifs.
L'utilisateur peut sélectionner une couleur à tout moment lorsqu'il est connecté.
L’utilisateur peut sélectionner un pixel qui devient alors indisponible pour les autres utilisateurs pendant 1 minute. Ceci se traduit par l’envoi d’un objet de type Pixel au serveur. Ce pixel est distribué aux autres utilisateurs par le serveur. 
L’utilisateur courant ne peut plus colorier d’autres pixels pendant 30 secondes.
Le décompte de ces 30 secondes est affiché à gauche (à la place de 00 :00).
Le serveur notifie les utilisateurs lorsqu’ils tentent de sélectionner un pixel pour lequel la minute d’embargo n’est pas écoulée.
