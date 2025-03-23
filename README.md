# Mya-Resolveur-Labyrinthe

## Description
MYA - Résolveur de Labyrinthe est un projet Java qui permet de générer, visualiser et résoudre des labyrinthes de manière interactive. Ce programme illustre des concepts fondamentaux d’algorithmique et de programmation, notamment la génération de labyrinthes avec l’algorithme Recursive Backtracking, et la résolution avec les algorithmes DFS (Depth-First Search) et BFS (Breadth-First Search). L’interface graphique, construite avec Swing, offre une expérience visuelle engageante : le chemin final est tracé en bleu, avec des performances (temps et nombre d’étapes) affichées en temps réel.

Ce projet a été développé dans le cadre d’un travail académique pour démontrer l’application pratique des algorithmes de recherche et de génération de labyrinthes, tout en mettant en avant des compétences en développement d’interfaces graphiques.

## Fonctionnalités
* Génération de labyrinthes :
 Créer des labyrinthes aléatoires de taille personnalisée (lignes et colonnes) avec l’algorithme Recursive Backtracking.
* Résolution avec DFS et BFS :
  DFS : Recherche en profondeur, rapide mais ne garantit pas le chemin le plus court.
  BFS : Recherche en largeur, garantit le chemin le plus court.
* Animation visuelle :
  Traçage du chemin final en bleu.
* Chargement depuis un fichier : 
  Importer des labyrinthes prédéfinis à partir de fichiers texte.
* Interface graphique intuitive :
  Thème sombre avec un dégradé de bleu-gris.
  Boutons stylisés avec effets de survol.
  Affichage des performances (temps d’exécution et nombre d’étapes).
  Réinitialisation : Remetter le labyrinthe à son état initial pour tester différents algorithmes.

 ## Utilisation
   Une fois le programme lancé, une interface graphique s’ouvre. Voici comment utiliser les différentes fonctionnalités :

 ### Générer un labyrinthe :
  Cliquer sur le bouton "Générer Labyrinthe" (bleu-gris, avec une icône 🖼️).
 Entrer les dimensions (lignes et colonnes) dans la boîte de dialogue. Les dimensions doivent être des nombres impairs supérieurs ou égaux à 5 (par exemple, 21x21).
 Cliquer sur "OK" pour générer et afficher le labyrinthe.
### Résoudre avec DFS :
 Cliquer sur le bouton "Résoudre avec DFS" (vert, avec une icône 🔍).
 Observer l’animation : le chemin final est tracé en bleu.
 Les performances (temps et nombre d’étapes) s’affichent en bas.
### Résoudre avec BFS :
 Cliquer sur le bouton "Résoudre avec BFS" (orange, avec une icône 🔎).
 L’animation montre le chemin final en bleu correspondant a la resolution.
 Comparer les performances avec celles de DFS.
### Charger un labyrinthe depuis un fichier :
  Cliquer sur le bouton "Charger Fichier" (violet, avec une icône 📂).
  Sélectionner un fichier texte contenant un labyrinthe (voir le format ci-dessous).
  Le labyrinthe s’affiche, prêt à être résolu.
### Réinitialiser l’affichage :
  Cliquer sur le bouton "Réinitialiser" (rouge, avec une icône 🔄) pour remettre le labyrinthe à son état initial.

### Format des fichiers de labyrinthe
Les fichiers texte doivent respecter le format suivant :

*# : Mur* 
*= : Passage*
*S : Point de départ*
*E : Point d’arrivée*

### Détails techniques
 Algorithmes utilisés
* Recursive Backtracking : Pour générer des labyrinthes parfaits (sans boucles ni zones inaccessibles).
* DFS (Depth-First Search) : Explore en profondeur, rapide mais ne garantit pas le chemin le plus court.
* BFS (Breadth-First Search) : Explore niveau par niveau, garantit le chemin le plus court.

Structure du code
Le projet est organisé en trois classes principales :

#### Labyrinthe.java :
 Gère la génération et le chargement des labyrinthes.
 Contient les méthodes genererLabyrintheAleatoire() et genererChemin() pour Recursive Backtracking.
#### SolutionLabyrinthe.java :
  Implémente les algorithmes de résolution DFS et BFS.
  Enregistre l’ordre d’exploration et le chemin final pour l’animation.
#### TestLabyrinthe.java :
  Gère l’interface graphique avec Swing.
  Utilise un Timer pour animer l’exploration et le traçage.
  Affiche les performances (temps mesuré avec System.nanoTime()).