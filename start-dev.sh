#!/bin/bash

# Norsk Opptaksystem - Development Start Script
# For lokal utvikling med H2 database

echo "🚀 Starter Norsk Opptaksystem (DEVELOPMENT MODE)..."
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

# Development informasjon
echo "🛠️  DEVELOPMENT MODE AKTIVERT"
echo "   - Database: H2 in-memory"
echo "   - Port: 8080"
echo "   - Profil: development"
echo ""
echo "📱 Tilgjengelige endepunkt:"
echo "   - GraphiQL: http://localhost:8080/graphiql"
echo "   - H2 Console: http://localhost:8080/h2-console"
echo ""
echo "⏹️  Trykk Ctrl+C for å stoppe"
echo ""

# Starter med development profil og separate build-mappe
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev -Dmaven.build.directory=target-dev