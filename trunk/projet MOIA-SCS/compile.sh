src=src/
bin=bin/
srcJoueur=${src}joueur/
binJoueur=${bin}joueur/
srcMoteurJava=${src}moteurJava/
binMoteurJava=${bin}moteurJava/

#export LD_LIBRARY_PATH=/applis/sicstus-3.11.2/lib/
#export CLASSPATH=/applis/sicstus-3.11.2/lib/sicstus-3.11.2/bin/jasper.jar #ou -cp <classpath>

#export LD_LIBRARY_PATH=/usr/local/sicstus4.1.3/lib/
#export CLASSPATH=/usr/local/sicstus4.1.3/lib/sicstus-4.1.3/bin/jasper.jar #ou -cp <classpath>

export LD_LIBRARY_PATH=/applis/sicstus-4.1.3/lib/
export CLASSPATH=.:bin/moteurJava:/applis/sicstus-4.1.3/lib/sicstus-4.1.3/bin/jasper.jar #ou -cp <classpath>

javac -d ${binMoteurJava} ${srcMoteurJava}*.java

cd ${srcJoueur}
make veryclean
make
cd ../..

cp ${srcJoueur}connexionArbitre ${binJoueur}connexionArbitre
