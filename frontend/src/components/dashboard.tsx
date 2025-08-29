import { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Building, GraduationCap, Users, FileText, BookOpen, ClipboardList } from 'lucide-react';

interface DashboardStats {
  institusjoner: number;
  utdanningstilbud: number;
  sokere: number;
  regelsett: number;
  dokumenter: number;
  fagkoder: number;
}

interface TopInstitusjon {
  navn: string;
  kortNavn: string;
  antallTilbud: number;
}

interface OpptaksStatistikk {
  navn: string;
  antallSokere: number;
  antallPlasser: number;
}

export default function Dashboard() {
  const [loading, setLoading] = useState(true);
  
  // Mock data for demonstration
  const [stats] = useState<DashboardStats>({
    institusjoner: 47,
    utdanningstilbud: 1234,
    sokere: 15678,
    regelsett: 89,
    dokumenter: 3421,
    fagkoder: 567,
  });

  const [topInstitusjoner] = useState<TopInstitusjon[]>([
    { navn: 'Universitetet i Oslo', kortNavn: 'UiO', antallTilbud: 156 },
    { navn: 'Norges teknisk-naturvitenskapelige universitet', kortNavn: 'NTNU', antallTilbud: 143 },
    { navn: 'Universitetet i Bergen', kortNavn: 'UiB', antallTilbud: 122 },
    { navn: 'OsloMet – storbyuniversitetet', kortNavn: 'OsloMet', antallTilbud: 98 },
    { navn: 'Universitetet i Tromsø', kortNavn: 'UiT', antallTilbud: 87 },
  ]);

  const [populæreStudier] = useState<OpptaksStatistikk[]>([
    { navn: 'Medisin', antallSokere: 2341, antallPlasser: 156 },
    { navn: 'Dataingeniør', antallSokere: 1876, antallPlasser: 234 },
    { navn: 'Psykologi', antallSokere: 1654, antallPlasser: 89 },
    { navn: 'Juss', antallSokere: 1432, antallPlasser: 112 },
    { navn: 'Økonomi og administrasjon', antallSokere: 1298, antallPlasser: 187 },
  ]);

  useEffect(() => {
    // Simulate loading
    const timer = setTimeout(() => setLoading(false), 1000);
    return () => clearTimeout(timer);
  }, []);

  if (loading) {
    return (
      <div className="space-y-6">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
          <p className="text-muted-foreground">Oversikt over opptakssystemet</p>
        </div>
        <div className="text-center py-8">
          <p>Laster dashboard data...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
        <p className="text-muted-foreground">Oversikt over opptakssystemet</p>
      </div>

      {/* Stats cards grid */}
      <div className="grid gap-6 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
        <Card className="hover:shadow-md transition-shadow cursor-pointer">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-3xl font-bold text-primary">{stats.institusjoner}</p>
                <p className="text-sm text-muted-foreground">Institusjoner</p>
              </div>
              <Building className="h-10 w-10 text-primary" />
            </div>
          </CardContent>
        </Card>

        <Card className="hover:shadow-md transition-shadow cursor-pointer">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-3xl font-bold text-accent">{stats.utdanningstilbud}</p>
                <p className="text-sm text-muted-foreground">Utdanningstilbud</p>
              </div>
              <GraduationCap className="h-10 w-10 text-accent" />
            </div>
          </CardContent>
        </Card>

        <Card className="hover:shadow-md transition-shadow cursor-pointer">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-3xl font-bold text-secondary">{stats.sokere.toLocaleString()}</p>
                <p className="text-sm text-muted-foreground">Søkere</p>
              </div>
              <Users className="h-10 w-10 text-secondary" />
            </div>
          </CardContent>
        </Card>

        <Card className="hover:shadow-md transition-shadow cursor-pointer">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-3xl font-bold text-primary">{stats.regelsett}</p>
                <p className="text-sm text-muted-foreground">Regelsett</p>
              </div>
              <FileText className="h-10 w-10 text-primary" />
            </div>
          </CardContent>
        </Card>

        <Card className="hover:shadow-md transition-shadow cursor-pointer">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-3xl font-bold text-accent">{stats.fagkoder}</p>
                <p className="text-sm text-muted-foreground">Fagkoder</p>
              </div>
              <BookOpen className="h-10 w-10 text-accent" />
            </div>
          </CardContent>
        </Card>

        <Card className="hover:shadow-md transition-shadow cursor-pointer">
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-3xl font-bold text-secondary">{stats.dokumenter.toLocaleString()}</p>
                <p className="text-sm text-muted-foreground">Dokumenter</p>
              </div>
              <ClipboardList className="h-10 w-10 text-secondary" />
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Detailed stats */}
      <div className="grid gap-6 md:grid-cols-2">
        {/* Top institusjoner */}
        <Card>
          <CardHeader>
            <CardTitle className="text-xl">Top institusjoner</CardTitle>
            <CardDescription>Institusjoner med flest utdanningstilbud</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {topInstitusjoner.map((institusjon, index) => (
                <div
                  key={institusjon.navn}
                  className="flex items-center justify-between p-3 rounded-lg bg-muted/50"
                >
                  <div className="flex items-center gap-3">
                    <div className="bg-primary/10 text-primary rounded-full w-8 h-8 flex items-center justify-center text-sm font-bold">
                      {index + 1}
                    </div>
                    <div>
                      <p className="font-medium">{institusjon.kortNavn}</p>
                      <p className="text-sm text-muted-foreground">{institusjon.navn}</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-accent">{institusjon.antallTilbud}</p>
                    <p className="text-xs text-muted-foreground">tilbud</p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Populære studier */}
        <Card>
          <CardHeader>
            <CardTitle className="text-xl">Populære studier</CardTitle>
            <CardDescription>Studier med høyest konkurranse</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {populæreStudier.map((studie, index) => (
                <div
                  key={studie.navn}
                  className="flex items-center justify-between p-3 rounded-lg bg-muted/50"
                >
                  <div className="flex items-center gap-3">
                    <div className="bg-secondary/10 text-secondary rounded-full w-8 h-8 flex items-center justify-center text-sm font-bold">
                      {index + 1}
                    </div>
                    <div>
                      <p className="font-medium">{studie.navn}</p>
                      <p className="text-sm text-muted-foreground">
                        {(studie.antallSokere / studie.antallPlasser).toFixed(1)} søkere per plass
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-secondary">{studie.antallSokere}</p>
                    <p className="text-xs text-muted-foreground">{studie.antallPlasser} plasser</p>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}