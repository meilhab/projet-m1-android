############# R�alis� par : Guillaume MONTAVON & Benoit MEILHAC ###############

Programme test� sous linux

Pour g�n�rer les bibliotheques ainsi que le fichier ex�cutable, il suffit de 
compiler avec la commande make � la racine du projet.
Il suffit de faire un -> "make veryclean" pour nettoyer le projet.
Ensuite, un -> "make cp" pour g�n�rer l'ex�cutable � la racine du projet.
Si des erreurs de compilation apparaissent, nettoyer le projet et relancer la g�n�ration.

Ce dernier va g�n�rer 3 dossiers : lib, obj et bin (qui contient l'ex�cutable)
Pour copier l'ex�cutable � la racine, on peut ex�cuter la commande : make cp

Le programme se lance en executant ./gestionGraphe
On pourra par exemple importer les graphes se trouvants soit dans le fichier 
exemple fichierTest.txt ou soit dans algo.txt
