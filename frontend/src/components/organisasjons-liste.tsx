import { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Plus, Pencil, Trash2, Building2, AlertCircle } from 'lucide-react';

type OrganisasjonsType = 'UNIVERSITET' | 'HOGSKOLE' | 'FAGSKOLE' | 'PRIVAT';

interface Organisasjon {
  id: string;
  navn: string;
  kortNavn?: string;
  type: OrganisasjonsType;
  organisasjonsnummer: string;
  epost?: string;
  telefon?: string;
  adresse?: string;
  poststed?: string;
  postnummer?: string;
  nettside?: string;
  aktiv: boolean;
}

interface OpprettOrganisasjonInput {
  navn: string;
  organisasjonsnummer: string;
  organisasjonstype: OrganisasjonsType;
  epost?: string;
  telefon?: string;
  adresse?: string;
  poststed?: string;
  postnummer?: string;
}

const organisasjonsTypeLabels: Record<OrganisasjonsType, string> = {
  UNIVERSITET: 'Universitet',
  HOGSKOLE: 'Høgskole',
  FAGSKOLE: 'Fagskole',
  PRIVAT: 'Privat institusjon',
};

const organisasjonsTypeBadgeVariant: Record<OrganisasjonsType, 'default' | 'secondary' | 'destructive' | 'outline'> = {
  UNIVERSITET: 'default',
  HOGSKOLE: 'secondary',
  FAGSKOLE: 'outline',
  PRIVAT: 'destructive',
};

export default function OrganisasjonsListe() {
  const [organisasjoner, setOrganisasjoner] = useState<Organisasjon[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingOrg, setEditingOrg] = useState<Organisasjon | null>(null);

  // Form state
  const [formData, setFormData] = useState<OpprettOrganisasjonInput>({
    navn: '',
    organisasjonsnummer: '',
    organisasjonstype: 'UNIVERSITET',
    epost: '',
    telefon: '',
    adresse: '',
    poststed: '',
    postnummer: '',
  });

  // Fetch organisasjoner fra GraphQL API
  const fetchOrganisasjoner = async () => {
    try {
      setLoading(true);
      const response = await fetch('/graphql', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          query: `
            query GetOrganisasjoner {
              organisasjoner {
                id
                navn
                kortNavn
                type
                organisasjonsnummer
                epost
                telefon
                adresse
                poststed
                postnummer
                nettside
                aktiv
              }
            }
          `,
        }),
      });

      const result = await response.json();
      if (result.errors) {
        throw new Error(result.errors[0].message);
      }

      setOrganisasjoner(result.data.organisasjoner);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Noe gikk galt');
    } finally {
      setLoading(false);
    }
  };

  // Opprett ny organisasjon
  const opprettOrganisasjon = async () => {
    try {
      const response = await fetch('/graphql', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          query: `
            mutation OpprettOrganisasjon($input: OpprettOrganisasjonInput!) {
              opprettOrganisasjon(input: $input) {
                id
                navn
                kortNavn
                type
                organisasjonsnummer
                epost
                telefon
                adresse
                poststed
                postnummer
                nettside
                aktiv
              }
            }
          `,
          variables: {
            input: formData,
          },
        }),
      });

      const result = await response.json();
      if (result.errors) {
        throw new Error(result.errors[0].message);
      }

      await fetchOrganisasjoner();
      setIsDialogOpen(false);
      resetForm();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Kunne ikke opprette organisasjon');
    }
  };

  // Deaktiver organisasjon
  const deaktiverOrganisasjon = async (id: string) => {
    try {
      const response = await fetch('/graphql', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          query: `
            mutation DeaktiverOrganisasjon($id: String!) {
              deaktiverOrganisasjon(id: $id) {
                id
                aktiv
              }
            }
          `,
          variables: { id },
        }),
      });

      const result = await response.json();
      if (result.errors) {
        throw new Error(result.errors[0].message);
      }

      await fetchOrganisasjoner();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Kunne ikke deaktivere organisasjon');
    }
  };

  const resetForm = () => {
    setFormData({
      navn: '',
      organisasjonsnummer: '',
      organisasjonstype: 'UNIVERSITET',
      epost: '',
      telefon: '',
      adresse: '',
      poststed: '',
      postnummer: '',
    });
    setEditingOrg(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await opprettOrganisasjon();
  };

  useEffect(() => {
    fetchOrganisasjoner();
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[400px]">
        <div className="text-center">
          <Building2 className="h-8 w-8 animate-pulse mx-auto mb-2" />
          <p className="text-muted-foreground">Laster organisasjoner...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold">Organisasjoner</h1>
          <p className="text-muted-foreground">
            Administrer utdanningsinstitusjoner og organisasjoner
          </p>
        </div>
        
        <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
          <DialogTrigger asChild>
            <Button onClick={resetForm}>
              <Plus className="h-4 w-4 mr-2" />
              Ny organisasjon
            </Button>
          </DialogTrigger>
          <DialogContent className="sm:max-w-[625px]">
            <form onSubmit={handleSubmit}>
              <DialogHeader>
                <DialogTitle>Opprett ny organisasjon</DialogTitle>
                <DialogDescription>
                  Registrer en ny utdanningsinstitusjon eller organisasjon
                </DialogDescription>
              </DialogHeader>
              <div className="grid gap-4 py-4">
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="navn" className="text-right">
                    Navn *
                  </Label>
                  <Input
                    id="navn"
                    value={formData.navn}
                    onChange={(e) => setFormData({ ...formData, navn: e.target.value })}
                    className="col-span-3"
                    required
                  />
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="organisasjonsnummer" className="text-right">
                    Org.nr *
                  </Label>
                  <Input
                    id="organisasjonsnummer"
                    value={formData.organisasjonsnummer}
                    onChange={(e) => setFormData({ ...formData, organisasjonsnummer: e.target.value })}
                    className="col-span-3"
                    placeholder="9 siffer"
                    required
                  />
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="type" className="text-right">
                    Type *
                  </Label>
                  <Select
                    value={formData.organisasjonstype}
                    onValueChange={(value: OrganisasjonsType) =>
                      setFormData({ ...formData, organisasjonstype: value })
                    }
                  >
                    <SelectTrigger className="col-span-3">
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {Object.entries(organisasjonsTypeLabels).map(([value, label]) => (
                        <SelectItem key={value} value={value}>
                          {label}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="epost" className="text-right">
                    E-post
                  </Label>
                  <Input
                    id="epost"
                    type="email"
                    value={formData.epost}
                    onChange={(e) => setFormData({ ...formData, epost: e.target.value })}
                    className="col-span-3"
                  />
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="telefon" className="text-right">
                    Telefon
                  </Label>
                  <Input
                    id="telefon"
                    value={formData.telefon}
                    onChange={(e) => setFormData({ ...formData, telefon: e.target.value })}
                    className="col-span-3"
                  />
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="adresse" className="text-right">
                    Adresse
                  </Label>
                  <Input
                    id="adresse"
                    value={formData.adresse}
                    onChange={(e) => setFormData({ ...formData, adresse: e.target.value })}
                    className="col-span-3"
                  />
                </div>
                <div className="grid grid-cols-4 items-center gap-4">
                  <Label htmlFor="poststed" className="text-right">
                    Poststed
                  </Label>
                  <Input
                    id="poststed"
                    value={formData.poststed}
                    onChange={(e) => setFormData({ ...formData, poststed: e.target.value })}
                    className="col-span-2"
                  />
                  <Input
                    id="postnummer"
                    placeholder="Postnr"
                    value={formData.postnummer}
                    onChange={(e) => setFormData({ ...formData, postnummer: e.target.value })}
                    className="col-span-1"
                  />
                </div>
              </div>
              <DialogFooter>
                <Button type="button" variant="outline" onClick={() => setIsDialogOpen(false)}>
                  Avbryt
                </Button>
                <Button type="submit">Opprett</Button>
              </DialogFooter>
            </form>
          </DialogContent>
        </Dialog>
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <Card>
        <CardHeader>
          <CardTitle>Registrerte organisasjoner</CardTitle>
          <CardDescription>
            Totalt {organisasjoner.length} organisasjoner (
            {organisasjoner.filter((o) => o.aktiv).length} aktive)
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Navn</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>Org.nummer</TableHead>
                <TableHead>Kontakt</TableHead>
                <TableHead>Status</TableHead>
                <TableHead className="text-right">Handlinger</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {organisasjoner.map((org) => (
                <TableRow key={org.id}>
                  <TableCell>
                    <div>
                      <div className="font-medium">{org.navn}</div>
                      {org.kortNavn && (
                        <div className="text-sm text-muted-foreground">{org.kortNavn}</div>
                      )}
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant={organisasjonsTypeBadgeVariant[org.type]}>
                      {organisasjonsTypeLabels[org.type]}
                    </Badge>
                  </TableCell>
                  <TableCell className="font-mono text-sm">{org.organisasjonsnummer}</TableCell>
                  <TableCell>
                    <div className="text-sm">
                      {org.epost && <div>{org.epost}</div>}
                      {org.telefon && <div>{org.telefon}</div>}
                      {org.adresse && (
                        <div className="text-muted-foreground">
                          {org.adresse}
                          {org.poststed && `, ${org.postnummer} ${org.poststed}`}
                        </div>
                      )}
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant={org.aktiv ? 'default' : 'secondary'}>
                      {org.aktiv ? 'Aktiv' : 'Inaktiv'}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-right">
                    <div className="flex justify-end gap-2">
                      <Button variant="ghost" size="sm">
                        <Pencil className="h-4 w-4" />
                      </Button>
                      {org.aktiv && (
                        <Button
                          variant="ghost"
                          size="sm"
                          onClick={() => deaktiverOrganisasjon(org.id)}
                          className="text-destructive hover:text-destructive"
                        >
                          <Trash2 className="h-4 w-4" />
                        </Button>
                      )}
                    </div>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </CardContent>
      </Card>
    </div>
  );
}