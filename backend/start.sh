#!/bin/bash

# Norsk Opptaksystem - Start Script
# Enkelt script for å starte Spring Boot applikasjonen

echo "🚀 Starter Norsk Opptaksystem..."
echo ""

# Sjekk at Java er installert
if ! command -v java &> /dev/null; then
    echo "❌ Java er ikke installert. Installer Java 21 eller nyere."
    exit 1
fi

# Vis Java-versjon
echo "☕ Java-versjon:"
java -version
echo ""

# Gjør Maven wrapper eksekverbar hvis ikke allerede
if [ ! -x "./mvnw" ]; then
    echo "🔧 Gjør Maven wrapper eksekverbar..."
    chmod +x mvnw
fi

# Start applikasjonen
echo "🏃 Starter Spring Boot applikasjonen..."
echo "   - GraphiQL: http://localhost:8080/graphiql"
echo "   - H2 Console: http://localhost:8080/h2-console"
echo "   - Ekstern tilgang: Sett opp port forwarding eller reverse proxy for opptaksapp.smidigakademiet.no"
echo ""
echo "⏹️  Trykk Ctrl+C for å stoppe"
echo ""

# Starter applikasjonen (Maven wrapper er nå fikset)
./mvnw spring-boot:run