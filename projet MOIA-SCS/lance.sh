#/applis/sicstus-3.11.2/lib/ et /applis/sicstus-3.11.2/lib/sicstus-3.11.2/bin/jasper.jar
export LD_LIBRARY_PATH=/usr/local/sicstus4.1.3/lib/
export CLASSPATH=$CLASSPATH:/usr/local/sicstus4.1.3/lib/sicstus-4.1.3/bin/jasper.jar

echo java -jar om.jar 2202
(java -jar om.jar 2202) &
sleep 2
echo ./src_c/connexionArbitre localhost 2222
(./src_c/connexionArbitre localhost 2222) &
