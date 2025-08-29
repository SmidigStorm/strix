#!/bin/bash

# Norsk Opptaksystem - Production Start Script
# For produksjon med PostgreSQL og port 80

echo "🚀 Starter Norsk Opptaksystem (PRODUCTION MODE)..."
echo ""

# Sjekk at vi kjører som root for port 80
if [ "$EUID" -ne 0 ]; then
    echo "❌ Production mode krever root-tilgang for port 80"
    echo "   Kjør: sudo ./start-prod.sh"
    exit 1
fi

# Sjekk at Java er installert
if ! command -v java &> /dev/null; then
    echo "❌ Java er ikke installert. Installer Java 21 eller nyere."
    exit 1
fi

# Vis Java-versjon
echo "☕ Java-versjon:"
java -version
echo ""

# Opprett data-mappe for H2 database
if [ ! -d "./data" ]; then
    echo "📁 Oppretter data-mappe for H2 database..."
    mkdir -p ./data
fi
echo "✅ Database setup OK"
echo ""

# Gjør Maven wrapper eksekverbar hvis ikke allerede
if [ ! -x "./mvnw" ]; then
    echo "🔧 Gjør Maven wrapper eksekverbar..."
    chmod +x mvnw
fi

# Production informasjon
echo "🏭 PRODUCTION MODE AKTIVERT"
echo "   - Database: H2 (fil-basert i ./data/)"
echo "   - Port: 80"
echo "   - Profil: production"
echo ""
echo "📱 Tilgjengelige endepunkt:"
echo "   - Frontend: http://opptaksapp.smidigakademiet.no/"
echo "   - GraphQL API: http://opptaksapp.smidigakademiet.no/graphql"
echo "   - GraphiQL: http://opptaksapp.smidigakademiet.no/graphiql"
echo ""
echo "⏹️  Trykk Ctrl+C for å stoppe"
echo ""

# Starter med production profil og separate build-mappe
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod -Dmaven.build.directory=target-prod