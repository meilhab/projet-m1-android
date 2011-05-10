############# Réalisé par : Guillaume MONTAVON & Benoit MEILHAC ###############

Programme testé sous linux

Pour générer les bibliotheques ainsi que le fichier exécutable, il suffit de 
compiler avec la commande make à la racine du projet.
Il suffit de faire un -> "make veryclean" pour nettoyer le projet.
Ensuite, un -> "make cp" pour générer l'exécutable à la racine du projet.
Si des erreurs de compilation apparaissent, nettoyer le projet et relancer la génération.

Ce dernier va générer 3 dossiers : lib, obj et bin (qui contient l'exécutable)
Pour copier l'exécutable à la racine, on peut exécuter la commande : make cp

Le programme se lance en executant ./gestionGraphe
On pourra par exemple importer les graphes se trouvants soit dans le fichier 
exemple fichierTest.txt ou soit dans algo.txt
