import com.sun.nio.sctp.AbstractNotificationHandler;

import java.util.ArrayList;

/**
 * Cette classe sert de plan pour l'écriture d'un jeu de bataille navale
 * en language d'assembleur Pep8.
 *
 * @author Nadir Hadji
 * HADN0869703
 */
public class Principale {

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////Les constantes d'affichages///////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Message d'introduction au programme.
     */
    public static final String intro = "Bienvenue au jeu de bataille navale!\n";

    /**
     * Caractère sur une case vide dite 'eau'
     */
    public static final char ocean = '~';

    /**
     * Caractère sur une case avec un bateau horizontale
     */
    public static final char horizontal = '<';

    /**
     * Caractère sur une case avec un bateau verticale
     */
    public static final char verticale = 'v';

    /**
     * Caractère sur une case dont le coup arrive sur l'eau
     */
    public static final char louper = 'o';

    /**
     * Caractère sur une case dont le coup atteind un bateau
     */
    public static final char toucher = '*';

    /**
     * Message descriptif de l'axe des X
     */
    public static final String msgX = "  ABCDEFGHIJKLMNOPQR\n";

    /**
     * Message de sollicitation pour placer les bateaux
     */
    public static final String msgPlace =
            "Entrer la description et la position des bateaux\n" +
            "selon le format suivant, séparés par des espaces:\n" +
            "taille[p/m/g] orientation[h/v] colonne[A-R] rangée[1-9]\n" +
            "ex: ghC4 mvM2 phK9\n";

    /**
     * Message de solicitation des case ciblé lors d'une attaque sur
     * le platau de jeux.
     */
    public static final String msgFeu =
            "Feu à volonté!\n" +
            "(entrer les coups à tirer: colonne [A-R] rangée [1-9])\n" +
            "ex: A3 I5 M3\n";

    /**
     * Message a afficher lorsque tout les bateaux sont atteint
     */
    public static final String msgFin =
            "Vous avez anéanti la flotte!\n" +
            "Appuyer sur <Enter> pour jouer à nouveau ou\n" +
            "n'importe quelle autre saisie pour quitter.\n";

    /**
     * Message de sortie de programme
     */
    public static final String outro = "Au revoir!";

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// Méthodes ////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /**
     * méthode qui initialiser un tableau vide avec la constanre 'ocean'
     * dans toute les cases
     *
     * @param plateau un tableau de jeu de type {@code char[][]}
     */
    private static void initPlateau(char[][] plateau) {

        Pep8.stro(msgX);

        for (int i = 0 ; i < plateau.length ; i++)
        {
            Pep8.stro(i+1+"|");
            for (char j = 0 ; j < plateau[i].length ; j++)
            {
                plateau[i][j] = ocean;
                Pep8.charo(plateau[i][j]);
            }
            Pep8.stro("|\n");
        }
    }

    /**
     * Afficher l'état de la table de jeu
     * @param plateau un tableau de jeu de type {@code char[][]}
     */
    private static void afficherTableau(char[][] plateau) {
        Pep8.stro(msgX);

        for (int i = 0 ; i < plateau.length ; i++)
        {
            Pep8.stro(i+1+"|");
            for (char j = 0 ; j < plateau[i].length ; j++)
            {
                Pep8.charo(plateau[i][j]);
            }
            Pep8.stro("|\n");
        }

    }

    ///////////////////////////// UTILS ///////////////////////////////////////

    /**
     * Convertir une lettre majuscule du format ASCII a sa position par rapport au A
     *
     * @param majuscule le code ascii d'une lettre majuscule (de 65 a 90)
     * @return la position de la lettre ('A'= 0 , 'Z' = 25)
     */
    private static int asciiMajusculeAIndex(char majuscule){
        return majuscule - 65;
    }

    /**
     * Convertir un chiffre du format ascii a ca forme entiére
     *
     * @param chiffre le code ascii d'un chiffre (de 48 a 57)
     * @return la valeur entiere du code ascii d'un chiffre
     */
    private static int asciiChiffreAIndex(char chiffre) {
        return (chiffre - 48) - 1;
    }

    /**
     * Trouver la taille en nombre de case d'un bateau
     *
     * @param taille Les caractères 'g' = 5  /ou 'm' = 3 /ou 'p' = 1.
     * @return la taille en nombre de case en fonction du caractère donné en argument.
     */
    private static final int trouverTaille(char taille) {

        int tailleBateau = 0;

        //ASCII de 'p' = 112 pour un bateau de petite taille
        if(taille == 112)
            tailleBateau = 1;

            //ASCII de 'm' = 109 pour un bateau de moyenne taille
        else if(taille == 109)
            tailleBateau = 3;

            //ASCII de 'g' = 103 pour un bateau de grande taille
        else if (taille == 103)
            tailleBateau = 5;

        return tailleBateau;
    }

    /////////////CORE : placement des bateaux sur le plateau //////////////////

    /**
     * Cette méthode place un bateau sur la table de jeu ci le placement est valide
     *
     * Un placement est possible uniquement si toute les cases que le bateau va
     * occuper contiennent la valeur 'ocean'.
     *
     * @param tab La table de jeux.
     * @param place Une chaine de caractère représentant un placement sur la table
     */
    private static void placementValide(char[][] tab, String place) {

        //La taille en nombre de case d'un bateau
        int taille = trouverTaille(place.charAt(0));

        //'h' = horizontale , 'v' = verticale
        char orientation = place.charAt(1);

        //Convertion ASCII d'une lettre majuscule en index de colonne pour tab
        int colonne = asciiMajusculeAIndex(place.charAt(2));

        //Convertion ASCII d'un chiffre en index de colonne pour tab
        int rangee = asciiChiffreAIndex(place.charAt(3));

        //Si orientation est egale au caractère 'h' pour un placement horizontale
        if( orientation == 104 )
            placementHorizontal(tab,taille,rangee,colonne);

        //Si orientation est egale au caractère 'v' pour un placement verticale
        if( orientation == 118 )
            placementVerticale(tab,taille,rangee,colonne);

    }

    /**
     * Verifier la validité d'un placement verticale et le faire si valide
     *
     * @param tab La table de jeu
     * @param taille Le nombre de case que va occuper le bateau
     * @param rangee La rangee de placement
     * @param colonne La colonne de placement
     */
    private static void placementVerticale(char[][] tab,int taille,int rangee,int colonne) {

        //Variable de verification qu'un placement horizontale est valide
        boolean verticaleValide;

        //Verification qu'un placement horizontal est possible
        verticaleValide = isPlacementVerticaleValide(tab,taille,rangee,colonne);

        if(verticaleValide)
            placementBateauVerticale(tab,rangee,colonne,taille);

    }

    /**
     * Verifier la validité d'un placement horizontale et le faire si valide
     *
     * @param tab La table de jeu
     * @param taille Le nombre de case que va occuper le bateau
     * @param rangee La rangee de placement
     * @param colonne La colonne de placement
     */
    private static void placementHorizontal(char[][] tab,int taille,int rangee,int colonne) {

        //Variable de verification qu'un placement horizontale est valide
        boolean horizontalValide;

        //Verification qu'un placement horizontal est possible
        horizontalValide = isPlacementHorizontalValide(tab,taille,rangee,colonne);

        if(horizontalValide)
            placementBateauHorizontale(tab,rangee,colonne,taille);
    }

    /**
     * Verification qu'un placement de bateau horizontale peut etre fait.
     *
     * @param tab La table de jeu
     * @param taille Le nombre de case que va occuper le bateau
     * @param rangee La rangee de placement
     * @param colonne La colonne de placement
     * @return Vrai si le nombre de case restant sur la rangée est suffisant
     * et que toute ces cases contiennent la contante 'ocean'. Sinon Faux.
     */
    private static boolean isPlacementHorizontalValide(char[][] tab, int taille,
                                                     int rangee, int colonne ) {

        boolean resultat;
        int derniereCaseH = (colonne + taille) - 1;

        if( derniereCaseH > 18)
            resultat = false;
        else {
            resultat = parcourHorizontalTableau(tab[rangee],derniereCaseH,colonne);
        }

        return resultat;
    }

    /**
     * Verifier que toute les cases entre colonne et derniere case contiennet 'ocean'
     *
     * @param tab Un tableau déja associé a une rangée
     * @param derniereCase index de la derniere case a verifier
     * @param colonne index de la premiere case a verifier
     * @return Vrai si toute les case contiennet 'ocean' , Sinon faux
     */
    private static boolean parcourHorizontalTableau(char[] tab,
                                                   int derniereCase, int colonne) {

        boolean resultat = true;

        while(derniereCase >= colonne && resultat) {

            if (tab[derniereCase] != ocean) {
                resultat = false;
            }

            derniereCase--;
        }

        return resultat;
    }

    /**
     * Verification qu'un placement de bateau verticle peut etre fait.
     *
     * @param tab La table de jeu
     * @param taille Le nombre de case que va occuper le bateau
     * @param rangee La rangee de placement
     * @param colonne La colonne de placement
     * @return Vrai si le nombre de case restant sur la colonne est suffisant
     * et que toute ces cases contiennent la contante 'ocean'. Sinon Faux.
     */
    private static boolean isPlacementVerticaleValide(char[][] tab, int taille
            , int rangee, int colonne) {

        boolean resultat;
        int derniereCaseV = (rangee + taille) - 1;

        if( derniereCaseV >= 9)
            resultat = false;
        else
            resultat = parcourVerticalTableau(tab,derniereCaseV,rangee,colonne);

        return resultat;
    }

    /**
     * Verifier que toute les cases entre rangée et derniere case contiennet 'ocean'
     * @param tab La table de jeu
     * @param derniereCaseV index de la derniere rangée a verifier verticalement
     * @param rangee index de la premiere rangée ou sera placé le bateau
     * @param colonne index de la colonne ou sera placé le bateau
     * @return Vrai si toute les case contiennet 'ocean' , Sinon faux
     */
    private static boolean parcourVerticalTableau(char[][] tab, int derniereCaseV
            ,int rangee,int colonne ) {

        boolean resultat = true;

        while(derniereCaseV >= rangee && resultat) {

            if(tab[derniereCaseV][colonne] != ocean) {
                resultat = false;
            }

            derniereCaseV--;
        }

        return resultat;
    }

    /**
     * Placer un bateau verticale dans le tableau de jeu une fois la verification faite
     *
     * @param tab La table de jeu
     * @param rangee index de la premiere rangée ou sera placé le bateau
     * @param colonne index de la colonne ou sera placé le bateau
     * @param taille nombre de case que le bateau va occuper
     */
    private static void placementBateauVerticale(char[][] tab , int rangee
            , int colonne, int taille ) {

        int compteur = 0;

        while(compteur < taille){

            tab[rangee+compteur][colonne] = verticale;
            compteur++;
        }
    }

    /**
     * Placer un bateau horizontalement dans le tableau de jeu une fois la verification faite
     *
     * @param tab La table de jeu
     * @param rangee index de la premiere rangée ou sera placé le bateau
     * @param colonne index de la colonne ou sera placé le bateau
     * @param taille nombre de case que le bateau va occuper
     */
    private static void placementBateauHorizontale(char[][] tab , int rangee
            , int colonne, int taille ) {

        int compteur = 0;

        while(compteur < taille){

            tab[rangee][colonne+compteur] = horizontal;
            compteur++;
        }
    }

    /////////////////////// CORE : Feu sur une case ///////////////////////////

    /**
     * Modificer l'etat du plateau de jeu si besoin en fonction d'un chaine de cacartères
     *
     * @param tab le plateau de jeu
     * @param coup une chaine de caractéres indicant les coordonnées de l'attaque
     *             Exemple : "D5" attaque sur la colonne 'D' sur la 5 ieme rangés.
     */
    private static void wrapperFeu(char[][] tab, String coup){
        int colonne = asciiMajusculeAIndex(coup.charAt(0));
        int rangee = asciiChiffreAIndex(coup.charAt(1));
        feu(tab,colonne,rangee);
    }

    /**
     * Modificer l'etat du plateau de jeu si besoin en fonction des coordonnés du coup
     *
     * @param tab le plateau de jeu
     * @param colonne l'index de l'axe des X du plateau
     * @param rangee l'index de l'axe des Y du plateau
     */
    private static void feu (char[][] tab, int colonne, int rangee) {

        char cellule = tab[rangee][colonne];
        int nextColonne = colonne + 1;
        int prevColonne = colonne - 1;
        int nextRangee = rangee + 1;
        int prevRangee = rangee - 1;

        switch (cellule) {

            case ocean:
                tab[rangee][colonne] = louper;
                break;

            case horizontal,verticale:
                tab[rangee][colonne] = toucher;

                if(nextColonne <= 17 )
                    feu(tab,nextColonne,rangee);

                if(prevColonne >= 0)
                    feu(tab,prevColonne,rangee);

                if(nextRangee <= 8)
                    feu(tab,colonne,nextRangee);

                if(prevRangee >= 0)
                    feu(tab,colonne,prevRangee);
                break;

            default:
                break;
        }
    }

    ////////////////////////// Le jeu commence ////////////////////////////////

    public static void main(String[] args) {

        Pep8.stro(intro);

        char[][] plateau = new char[9][18];

        initPlateau(plateau);

        Pep8.stro(msgPlace);

        String p1 = "ghN1";
        String p2 = "gvA1";
        String p3 = "gvI5";

        placementValide(plateau,p1);
        placementValide(plateau,p2);
        placementValide(plateau,p3);

        afficherTableau(plateau);

        Pep8.stro(msgFeu);

        String coup1 = "D5";
        String coup2 = "I5";
        String coup3 = "N1";
        String coup4 = "A3";

        wrapperFeu(plateau,coup1);
        wrapperFeu(plateau,coup2);
        wrapperFeu(plateau,coup3);
        wrapperFeu(plateau,coup4);

        afficherTableau(plateau);
    }
}
