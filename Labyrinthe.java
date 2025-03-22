import java.io.*;
import java.util.*;

// Classe Labyrinthe : Gère la création, le chargement et l'affichage du labyrinthe.
public class Labyrinthe {
    private char[][] labyrinthe; // Grille du labyrinthe
    private int lignes; // Nombre de lignes
    private int colonnes; // Nombre de colonnes
    private Position depart; // Position de départ
    private Position arrivee; // Position d'arrivée

    // Classe interne pour représenter une position (ligne, colonne)
    public static class Position {
        int ligne;
        int colonne;
        
        public Position(int ligne, int colonne) {
            this.ligne = ligne;
            this.colonne = colonne;
        }
    }

    // Constructeur avec matrice prédéfinie : Initialise un labyrinthe à partir d'une grille donnée
    public Labyrinthe(char[][] labyrinthe) {
        this.labyrinthe = labyrinthe;
        this.lignes = labyrinthe.length;
        this.colonnes = labyrinthe[0].length;
        trouverDepartEtArrivee();
    }

    // Constructeur avec génération aléatoire : Crée un labyrinthe de taille donnée
    public Labyrinthe(int lignes, int colonnes) {
        this.lignes = lignes;
        this.colonnes = colonnes;
        this.labyrinthe = new char[lignes][colonnes];
        genererLabyrintheAleatoire();
        trouverDepartEtArrivee();
    }

    // Constructeur avec fichier : Charge un labyrinthe depuis un fichier texte
    public Labyrinthe(String cheminFichier) throws IOException {
        List<String> lignesFichier = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(cheminFichier))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                lignesFichier.add(ligne);
            }
        }
        this.lignes = lignesFichier.size();
        this.colonnes = lignesFichier.get(0).length();
        this.labyrinthe = new char[lignes][colonnes];
        for (int i = 0; i < lignes; i++) {
            labyrinthe[i] = lignesFichier.get(i).toCharArray();
        }
        trouverDepartEtArrivee();
    }

    // Methode qui génère un labyrinthe aléatoire avec Recursive Backtracking
    private void genererLabyrintheAleatoire() {
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                labyrinthe[i][j] = '#'; // Remplit la grille de murs
            }
        }
        Random rand = new Random();
        genererChemin(1, 1, rand); // Commence à (1,1) pour laisser une bordure
        labyrinthe[1][1] = 'S'; // Place le départ
        labyrinthe[lignes-2][colonnes-2] = 'E'; // Place l'arrivée
    }

    // Methode qui creuse des passages récursivement en explorant des directions aléatoires
    private void genererChemin(int ligne, int colonne, Random rand) {
        labyrinthe[ligne][colonne] = '=';
        int[][] directions = {{-2, 0}, {2, 0}, {0, -2}, {0, 2}};
        List<int[]> dirs = Arrays.asList(directions);
        Collections.shuffle(dirs, rand);
        for (int[] dir : dirs) {
            int nouvelleLigne = ligne + dir[0];
            int nouvelleColonne = colonne + dir[1];
            if (estValide(nouvelleLigne, nouvelleColonne) && labyrinthe[nouvelleLigne][nouvelleColonne] == '#') {
                labyrinthe[ligne + dir[0]/2][colonne + dir[1]/2] = '='; // Ouvre le passage
                genererChemin(nouvelleLigne, nouvelleColonne, rand);
            }
        }
    }

    // Methode qui vérifie si une position est dans les limites du labyrinthe
    private boolean estValide(int ligne, int colonne) {
        return ligne > 0 && ligne < lignes-1 && colonne > 0 && colonne < colonnes-1;
    }

    //Methode qui localise les positions de départ et d'arrivée dans la grille
    private void trouverDepartEtArrivee() {
        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if (labyrinthe[i][j] == 'S') depart = new Position(i, j);
                else if (labyrinthe[i][j] == 'E') arrivee = new Position(i, j);
            }
        }
    }

    // Methode qui affiche le labyrinthe dans la console
    public void afficherLabyrinthe() {
        for (char[] ligne : labyrinthe) {
            System.out.println(new String(ligne));
        }
    }

    // Accesseurs
    public char[][] getLabyrinthe() { return labyrinthe; }
    public Position getDepart() { return depart; }
    public Position getArrivee() { return arrivee; }
    public int getLignes() { return lignes; }
    public int getColonnes() { return colonnes; }
}