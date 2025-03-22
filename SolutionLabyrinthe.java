import java.util.*;

// Classe SolutionLabyrinthe : Implémente les algorithmes de résolution (DFS et BFS) pour le labyrinthe.
public class SolutionLabyrinthe 
{
    private Labyrinthe labyrinthe; // Référence au labyrinthe à résoudre
    private char[][] solution; // Grille contenant la solution (avec le chemin marqué par '+')
    private List<Labyrinthe.Position> etapes; // Liste des positions du chemin final
    private Stack<Labyrinthe.Position> chemin; // Pile pour retracer le chemin pendant DFS
    private List<Labyrinthe.Position> explorationOrder; // Liste pour stocker l'ordre d'exploration

    // Constructeur methode qui initialise les structures de données
    public SolutionLabyrinthe(Labyrinthe labyrinthe) 
    {
        this.labyrinthe = labyrinthe;
        this.solution = new char[labyrinthe.getLignes()][labyrinthe.getColonnes()];
        this.etapes = new ArrayList<>();
        this.chemin = new Stack<>();
        this.explorationOrder = new ArrayList<>();
        for (int i = 0; i < labyrinthe.getLignes(); i++) 
        {
            solution[i] = labyrinthe.getLabyrinthe()[i].clone();
        }
    }

    // Methode qui résout le labyrinthe avec DFS et mesure le temps d'exécution
    public boolean resoudreDFS() 
    {
        etapes.clear();
        chemin.clear();
        explorationOrder.clear();
        long tempsDebut = System.nanoTime();
        boolean resultat = dfs(labyrinthe.getDepart().ligne, labyrinthe.getDepart().colonne);
        long tempsFin = System.nanoTime();
        System.out.println("Temps DFS: " + (tempsFin - tempsDebut) / 1000000 + "ms");
        if (resultat) 
        {
            etapes.clear();
            for (Labyrinthe.Position pos : chemin) 
            {
                etapes.add(pos);
            }
            for (Labyrinthe.Position pos : etapes) 
            {
                if (labyrinthe.getLabyrinthe()[pos.ligne][pos.colonne] != 'S' &&
                    labyrinthe.getLabyrinthe()[pos.ligne][pos.colonne] != 'E') 
                    {
                    solution[pos.ligne][pos.colonne] = '+';
                }
            }
        }
        return resultat;
    }

    // Méthode récursive pour DFS : Explore les directions possibles
    private boolean dfs(int ligne, int colonne) 
    {
        if (ligne < 0 || ligne >= labyrinthe.getLignes() || colonne < 0 || 
            colonne >= labyrinthe.getColonnes() || solution[ligne][colonne] == '#' || 
            solution[ligne][colonne] == '+') 
            {
            return false;
        }
        Labyrinthe.Position courant = new Labyrinthe.Position(ligne, colonne);
        chemin.push(courant);
        explorationOrder.add(courant);
        if (labyrinthe.getLabyrinthe()[ligne][colonne] == 'E') 
        {
            return true;
        }
        solution[ligne][colonne] = '+';
        int[] deplacementsLigne = 
        {-1, 1, 0, 0};
        int[] deplacementsColonne = 
        {0, 0, -1, 1};
        for (int i = 0; i < 4; i++) 
        {
            if (dfs(ligne + deplacementsLigne[i], colonne + deplacementsColonne[i])) 
            {
                return true;
            }
        }
        chemin.pop();
        solution[ligne][colonne] = '=';
        return false;
    }

    // Méthode qui résout le labyrinthe avec BFS et mesure le temps d'exécution
    public boolean resoudreBFS() 
    {
        etapes.clear();
        explorationOrder.clear(); // Réinitialise l'ordre d'exploration
        long tempsDebut = System.nanoTime();
        Queue<Labyrinthe.Position> file = new LinkedList<>();
        Map<Labyrinthe.Position, Labyrinthe.Position> parents = new HashMap<>();
        boolean[][] visite = new boolean[labyrinthe.getLignes()][labyrinthe.getColonnes()];
        
        Labyrinthe.Position depart = labyrinthe.getDepart();
        file.add(depart);
        visite[depart.ligne][depart.colonne] = true;
        parents.put(depart, null);
        explorationOrder.add(depart); // Ajoute le point de départ à l'ordre d'exploration

        // BFS : explore les cases niveau par niveau
        while (!file.isEmpty()) 
        {
            Labyrinthe.Position courant = file.poll();
            if (labyrinthe.getLabyrinthe()[courant.ligne][courant.colonne] == 'E') 
            {
                long tempsFin = System.nanoTime();
                System.out.println("Temps BFS: " + (tempsFin - tempsDebut) / 1000000 + "ms");
                marquerChemin(parents, courant); // Marque le chemin final
                return true;
            }

            int[] deplacementsLigne = 
            {-1, 1, 0, 0};
            int[] deplacementsColonne = 
            {0, 0, -1, 1};
            
            for (int i = 0; i < 4; i++) 
            {
                int nouvelleLigne = courant.ligne + deplacementsLigne[i];
                int nouvelleColonne = courant.colonne + deplacementsColonne[i];
                Labyrinthe.Position prochaine = new Labyrinthe.Position(nouvelleLigne, nouvelleColonne);
                
                if (estValide(nouvelleLigne, nouvelleColonne) && !visite[nouvelleLigne][nouvelleColonne] && 
                    labyrinthe.getLabyrinthe()[nouvelleLigne][nouvelleColonne] != '#') 
                    {
                    file.add(prochaine);
                    visite[nouvelleLigne][nouvelleColonne] = true;
                    parents.put(prochaine, courant);
                    explorationOrder.add(prochaine); // Ajoute chaque position visitée à l'ordre d'exploration
                }
            }
        }
        return false; // Aucune solution trouvée
    }

    // Méthode qui reconstruit le chemin final pour BFS à partir des relations parent-enfant
    private void marquerChemin(Map<Labyrinthe.Position, Labyrinthe.Position> parents, Labyrinthe.Position fin) 
    {
        Labyrinthe.Position courant = fin;
        etapes.clear();
        while (courant != null && labyrinthe.getLabyrinthe()[courant.ligne][courant.colonne] != 'S') 
        {
            if (labyrinthe.getLabyrinthe()[courant.ligne][courant.colonne] != 'E') 
            {
                solution[courant.ligne][courant.colonne] = '+';
            }
            etapes.add(0, courant);
            courant = parents.get(courant);
        }
    }

    // Méthode qui vérifie si une position est dans les limites du labyrinthe
    private boolean estValide(int ligne, int colonne) 
    {
        return ligne >= 0 && ligne < labyrinthe.getLignes() && colonne >= 0 && colonne < labyrinthe.getColonnes();
    }

    // Affiche la grille de solution dans la console
    public void afficherSolution() 
    {
        for (char[] ligne : solution) 
        {
            System.out.println(new String(ligne));
        }
    }

    // Accesseurs
    public char[][] getSolution() 
    { return solution; }
    public List<Labyrinthe.Position> getEtapes() 
    { return etapes; }
    public List<Labyrinthe.Position> getExplorationOrder() 
    { return explorationOrder; }
}