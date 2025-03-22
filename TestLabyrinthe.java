import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

// Classe TestLabyrinthe : Gère l'interface graphique et l'animation de la résolution.
public class TestLabyrinthe extends JFrame 
{
    private Labyrinthe labyrinthe; // Le labyrinthe à afficher
    private SolutionLabyrinthe solveur; // Le solveur pour DFS/BFS
    private JPanel panneauLabyrinthe; // Panneau pour dessiner le labyrinthe
    private static final int TAILLE_CASE = 30; // Taille d'une case en pixels
    private List<Labyrinthe.Position> explorationAnimation; // Liste des positions explorées (jaune)
    private List<Labyrinthe.Position> cheminAnimation; // Liste du chemin final (bleu)
    private Set<Labyrinthe.Position> casesAffichees; // Cases actuellement affichées à l'écran
    private int indexEtape; // Indice de l'étape actuelle dans l'animation
    private Timer timer; // Timer pour l'animation
    private JLabel labelPerformance; // Affiche les performances (étapes, temps)
    private JLabel labelStatus; // Affiche le statut de l'application
    private boolean showingExploration; // Indique si on est en phase d'exploration (jaune)
    private boolean pauseBetweenPhases; // Indique si on est en pause entre les phases

    // Constructeur qui crée l'interface graphique avec Swing
    public TestLabyrinthe() 
    {
        setTitle(" MYA - Résolveur de Labyrinthe ");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setMinimumSize(new Dimension(900, 800));
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        JPanel mainPanel = new JPanel(new BorderLayout()) 
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(45, 55, 72), 0, getHeight(), new Color(72, 85, 99));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        add(mainPanel);

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(30, 41, 59));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebar.setPreferredSize(new Dimension(350, 0));

        JLabel titleLabel = new JLabel("MYA - Résolveur de Labyrinthe");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(255, 255, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);
        sidebar.add(Box.createVerticalStrut(20));

        Font buttonFont = new Font("Segoe UI Emoji", Font.BOLD, 14);
        JButton boutonGenerer = createStyledButton("🖼️ Générer Labyrinthe", new Color(96, 125, 139), buttonFont);
        JButton boutonDFS = createStyledButton("🔍 Résoudre avec DFS", new Color(76, 175, 80), buttonFont);
        JButton boutonBFS = createStyledButton("🔎 Résoudre avec BFS", new Color(255, 152, 0), buttonFont);
        JButton boutonCharger = createStyledButton("📂 Charger Fichier", new Color(156, 39, 176), buttonFont);
        JButton boutonReinitialiser = createStyledButton("🔄 Réinitialiser", new Color(244, 67, 54), buttonFont);

        sidebar.add(boutonGenerer);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(boutonDFS);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(boutonBFS);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(boutonCharger);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(boutonReinitialiser);
        sidebar.add(Box.createVerticalStrut(30));

        labelStatus = new JLabel("Statut : Prêt");
        labelStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelStatus.setForeground(new Color(255, 255, 255));
        labelStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(labelStatus);

        labelPerformance = new JLabel("Étapes : 0 | Temps : 0ms");
        labelPerformance.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelPerformance.setForeground(new Color(255, 255, 255));
        labelPerformance.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(labelPerformance);

        mainPanel.add(sidebar, BorderLayout.WEST);

        panneauLabyrinthe = new JPanel() 
        {
            @Override
            protected void paintComponent(Graphics g) 
            {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (labyrinthe != null) 
                {
                    dessinerLabyrinthe(g2d);
                }
            }
        };
        panneauLabyrinthe.setBackground(new Color(18, 18, 18));
        mainPanel.add(panneauLabyrinthe, BorderLayout.CENTER);

        casesAffichees = new HashSet<>();
        timer = new Timer(100, e -> 
        {
            if (pauseBetweenPhases) 
            {
                pauseBetweenPhases = false;
                labelStatus.setText("Statut : Traçage du chemin final...");
                return;
            }
            if (showingExploration) 
            {
                if (indexEtape < explorationAnimation.size()) 
                {
                    Labyrinthe.Position pos = explorationAnimation.get(indexEtape);
                    // Ajoute la position à casesAffichees pour l'afficher en jaune
                    casesAffichees.add(pos);
                    labelStatus.setText("Statut : Exploration case (" + pos.ligne + "," + pos.colonne + ") - " + 
                                       indexEtape + "/" + explorationAnimation.size());
                    indexEtape++;
                    panneauLabyrinthe.repaint();
                } else 
                {
                    showingExploration = false;
                    pauseBetweenPhases = true;
                    indexEtape = 0;
                    // Ne réinitialise pas casesAffichees ici pour garder les cases jaunes visibles
                    // Réinitialise la grille de solution pour le traçage
                    for (int i = 0; i < labyrinthe.getLignes(); i++) 
                    {
                        solveur.getSolution()[i] = labyrinthe.getLabyrinthe()[i].clone();
                    }
                }
            } else 
            {
                if (indexEtape < cheminAnimation.size()) 
                {
                    Labyrinthe.Position pos = cheminAnimation.get(indexEtape);
                    if (labyrinthe.getLabyrinthe()[pos.ligne][pos.colonne] != 'S' && 
                        labyrinthe.getLabyrinthe()[pos.ligne][pos.colonne] != 'E') 
                        {
                        solveur.getSolution()[pos.ligne][pos.colonne] = '+';
                        casesAffichees.add(pos); // Ajoute la case pour l'afficher en bleu
                    }
                    labelStatus.setText("Statut : Traçage case (" + pos.ligne + "," + pos.colonne + ") - " + 
                                       indexEtape + "/" + cheminAnimation.size());
                    indexEtape++;
                    panneauLabyrinthe.repaint();
                } else 
                {
                    timer.stop();
                    labelStatus.setText("Statut : Résolution terminée");
                }
            }
        });

        // Actions des boutons
        boutonGenerer.addActionListener(e -> 
        {
            JTextField lignesField = new JTextField("21", 5);
            JTextField colonnesField = new JTextField("21", 5);
            JPanel panel = new JPanel();
            panel.setBackground(new Color(30, 41, 59));
            panel.add(new JLabel("Lignes :") 
            {
                { setForeground(Color.WHITE); }});
            panel.add(lignesField);
            panel.add(new JLabel("Colonnes :") 
            {
                { setForeground(Color.WHITE); }});
            panel.add(colonnesField);
            int result = JOptionPane.showConfirmDialog(this, panel, "Taille du labyrinthe", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) 
            {
                try 
                {
                    int lignes = Integer.parseInt(lignesField.getText());
                    int colonnes = Integer.parseInt(colonnesField.getText());
                    if (lignes < 5 || colonnes < 5 || lignes % 2 == 0 || colonnes % 2 == 0) 
                    {
                        JOptionPane.showMessageDialog(this, "Veuillez entrer des nombres impairs supérieurs ou égaux à 5.");
                        return;
                    }
                    labelStatus.setText("Statut : Génération en cours...");
                    labyrinthe = new Labyrinthe(lignes, colonnes);
                    solveur = new SolutionLabyrinthe(labyrinthe);
                    casesAffichees.clear();
                    labelPerformance.setText("Étapes : 0 | Temps : 0ms");
                    labelStatus.setText("Statut : Labyrinthe généré");
                    panneauLabyrinthe.repaint();
                } catch (NumberFormatException ex) 
                {
                    JOptionPane.showMessageDialog(this, "Veuillez entrer des nombres valides.");
                }
            }
        });

        boutonDFS.addActionListener(e -> 
        {
            if (labyrinthe != null) 
            {
                labelStatus.setText("Statut : Résolution DFS en cours...");
                solveur = new SolutionLabyrinthe(labyrinthe);
                long tempsDebut = System.nanoTime();
                if (solveur.resoudreDFS()) 
                {
                    long tempsFin = System.nanoTime();
                    labelPerformance.setText("Étapes : " + solveur.getEtapes().size() + " | Temps : " + (tempsFin - tempsDebut) / 1000000 + "ms");
                    explorationAnimation = solveur.getExplorationOrder();
                    cheminAnimation = solveur.getEtapes();
                    lancerAnimation();
                } else 
                {
                    labelStatus.setText("Statut : Aucune solution trouvée");
                    JOptionPane.showMessageDialog(this, "Aucune solution trouvée avec DFS");
                }
            } else 
            {
                JOptionPane.showMessageDialog(this, "Veuillez d'abord générer ou charger un labyrinthe.");
            }
        });

        boutonBFS.addActionListener(e -> 
        {
            if (labyrinthe != null) 
            {
                labelStatus.setText("Statut : Résolution BFS en cours...");
                solveur = new SolutionLabyrinthe(labyrinthe);
                long tempsDebut = System.nanoTime();
                if (solveur.resoudreBFS()) 
                {
                    long tempsFin = System.nanoTime();
                    labelPerformance.setText("Étapes : " + solveur.getEtapes().size() + " | Temps : " + (tempsFin - tempsDebut) / 1000000 + "ms");
                    explorationAnimation = solveur.getExplorationOrder();
                    cheminAnimation = solveur.getEtapes();
                    lancerAnimation();
                } else 
                {
                    labelStatus.setText("Statut : Aucune solution trouvée");
                    JOptionPane.showMessageDialog(this, "Aucune solution trouvée avec BFS");
                }
            } else 
            {
                JOptionPane.showMessageDialog(this, "Veuillez d'abord générer ou charger un labyrinthe.");
            }
        });

        boutonCharger.addActionListener(e -> 
        {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
            {
                try 
                {
                    labelStatus.setText("Statut : Chargement en cours...");
                    labyrinthe = new Labyrinthe(chooser.getSelectedFile().getPath());
                    solveur = new SolutionLabyrinthe(labyrinthe);
                    casesAffichees.clear();
                    labelPerformance.setText("Étapes : 0 | Temps : 0ms");
                    labelStatus.setText("Statut : Labyrinthe chargé");
                    panneauLabyrinthe.repaint();
                } catch (IOException ex) 
                {
                    labelStatus.setText("Statut : Erreur de chargement");
                    JOptionPane.showMessageDialog(this, "Erreur de chargement du fichier");
                }
            }
        });

        boutonReinitialiser.addActionListener(e -> 
        {
            if (labyrinthe != null) 
            {
                solveur = new SolutionLabyrinthe(labyrinthe);
                casesAffichees.clear();
                labelPerformance.setText("Étapes : 0 | Temps : 0ms");
                labelStatus.setText("Statut : Affichage réinitialisé");
                panneauLabyrinthe.repaint();
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Methode qui crée un bouton stylisé avec effet de survol
    private JButton createStyledButton(String text, Color baseColor, Font font) 
    {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addMouseListener(new MouseAdapter() 
        {
            @Override
            public void mouseEntered(MouseEvent e) 
            {
                button.setBackground(baseColor.brighter());
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            @Override
            public void mouseExited(MouseEvent e) 
            {
                button.setBackground(baseColor);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        return button;
    }

    // Metjode qui lance l'animation de l'exploration et du traçage
    private void lancerAnimation() 
    {
        this.indexEtape = 0;
        this.showingExploration = !explorationAnimation.isEmpty();
        this.pauseBetweenPhases = false;
        casesAffichees.clear();
        // Réinitialise la grille de solution pour éviter toute interférence
        for (int i = 0; i < labyrinthe.getLignes(); i++) 
        {
            solveur.getSolution()[i] = labyrinthe.getLabyrinthe()[i].clone();
        }
        timer.start();
    }

    // Methode qui dessine le labyrinthe avec des couleurs spécifiques
    private void dessinerLabyrinthe(Graphics2D g) 
    {
        char[][] grille = solveur != null ? solveur.getSolution() : labyrinthe.getLabyrinthe();
        int offsetX = (panneauLabyrinthe.getWidth() - labyrinthe.getColonnes() * TAILLE_CASE) / 2;
        int offsetY = (panneauLabyrinthe.getHeight() - labyrinthe.getLignes() * TAILLE_CASE) / 2;
        for (int i = 0; i < labyrinthe.getLignes(); i++) 
        {
            for (int j = 0; j < labyrinthe.getColonnes(); j++) 
            {
                Labyrinthe.Position pos = new Labyrinthe.Position(i, j);
                if (casesAffichees.contains(pos)) 
                {
                    if (showingExploration && grille[i][j] != 'S' && grille[i][j] != 'E') 
                    {
                        g.setColor(new Color(255, 235, 59)); 
                    } else if (grille[i][j] == '+' && !showingExploration) 
                    {
                        g.setColor(new Color(66, 165, 245)); 
                    } else 
                    {
                        switch (grille[i][j]) 
                        {
                            case '#': g.setColor(new Color(55, 71, 79)); break;
                            case '=': g.setColor(new Color(38, 50, 56)); break;
                            case 'S': g.setColor(new Color(102, 187, 106)); break;
                            case 'E': g.setColor(new Color(239, 83, 80)); break;
                            default: g.setColor(new Color(38, 50, 56)); break;
                        }
                    }
                } else 
                {
                    switch (grille[i][j]) 
                    {
                        case '#': g.setColor(new Color(55, 71, 79)); break;
                        case '=': g.setColor(new Color(38, 50, 56)); break;
                        case 'S': g.setColor(new Color(102, 187, 106)); break;
                        case 'E': g.setColor(new Color(239, 83, 80)); break;
                        case '+': g.setColor(new Color(66, 165, 245)); break;
                    }
                }
                g.fillRoundRect(offsetX + j * TAILLE_CASE, offsetY + i * TAILLE_CASE, TAILLE_CASE - 2, TAILLE_CASE - 2, 8, 8);
                if (grille[i][j] == '#') 
                {
                    g.setColor(new Color(0, 0, 0, 50));
                    g.fillRoundRect(offsetX + j * TAILLE_CASE + 2, offsetY + i * TAILLE_CASE + 2, TAILLE_CASE - 6, TAILLE_CASE - 6, 8, 8);
                }
            }
        }
    }

    public static void main(String[] args) 
    {
        SwingUtilities.invokeLater(TestLabyrinthe::new);
    }
}