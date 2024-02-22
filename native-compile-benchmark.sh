#!/bin/bash
#XtremeAlex

echo "Start Build..."

# Funzione per calcolare la differenza di tempo
calcola_tempo() {
    start=$1
    end=$2
    diff=$((end-start))
    minuti=$((diff / 60))
    secondi=$((diff % 60))
    echo "$minuti minuti e $secondi secondi"
}

start_time_totale=$(date +%s)

echo "Pulisco il target"
start_time=$(date +%s)
mvn clean
end_time=$(date +%s)
echo "Tempo impiegato: "
calcola_tempo $start_time $end_time

echo "Build normale"
start_time=$(date +%s)
mvn package -DskipTests
end_time=$(date +%s)
echo "Tempo impiegato: "
calcola_tempo $start_time $end_time

echo "Generazione conf native-image"
start_time=$(date +%s)
java -agentlib:native-image-agent=config-output-dir=src/main/resources/META-INF/native-image -jar ./target/*.jar com.xtremealex.aeroport.AeroportApplication &

JAVA_PID=$!
sleep 120
kill $JAVA_PID

end_time=$(date +%s)
echo -n "Tempo impiegato: "
calcola_tempo $start_time $end_time

echo "Build Nativa"
start_time=$(date +%s)
mvn clean package -DskipTests -Pnative
end_time=$(date +%s)
echo "Tempo impiegato: "
calcola_tempo $start_time $end_time

end_time_totale=$(date +%s)
echo -n "Tempo totale impiegato: "
calcola_tempo $start_time_totale $end_time_totale

echo "Fine Build; lancia ./target/aeroport"
echo "Compilare una Docker Img"
echo "> mvn package -DskipTests -Pdocker-m1-arm"
echo "oppure usare il docker-compose.yml"
