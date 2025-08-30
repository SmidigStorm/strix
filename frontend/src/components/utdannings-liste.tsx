import { useState, useEffect } from 'react';
import { useRole } from '@/contexts/RoleContext';
import { useAuth } from '@/contexts/AuthContext';
import { 
  graphQLRequest,
  GET_UTDANNINGER_QUERY,
  CREATE_UTDANNING_MUTATION,
  UPDATE_UTDANNING_MUTATION,
  DEACTIVATE_UTDANNING_MUTATION,
  ACTIVATE_UTDANNING_MUTATION,
  DELETE_UTDANNING_MUTATION,
  GET_ORGANISATIONS_QUERY
} from '@/lib/api';
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
import { Plus, Pencil, Trash2, Eye, EyeOff, GraduationCap, AlertCircle, ChevronLeft, ChevronRight } from 'lucide-react';

type Studieform = 'HELTID' | 'DELTID';

interface Organisasjon {
  id: string;
  navn: string;
  kortNavn?: string;
}

interface Utdanning {
  id: string;
  navn: string;
  studienivaa: string;
  studiepoeng: number;
  varighet: number;
  studiested: string;
  undervisningssprak: string;
  beskrivelse?: string;
  starttidspunkt: string;
  studieform: Studieform;
  aktiv: boolean;
  organisasjon: Organisasjon;
}

interface UtdanningPage {
  content: Utdanning[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

interface UtdanningFilter {
  navn?: string;
  studienivaa?: string;
  studiested?: string;
  organisasjonId?: string;
  studieform?: Studieform;
  aktiv?: boolean;
}

export function UtdanningsListe() {
  const { selectedRole, hasPermission } = useRole();
  const { token } = useAuth();
  
  const [utdanninger, setUtdanninger] = useState<UtdanningPage>({
    content: [],
    totalElements: 0,
    totalPages: 0,
    currentPage: 0,
    pageSize: 20,
    hasNext: false,
    hasPrevious: false
  });
  
  const [organisasjoner, setOrganisasjoner] = useState<Organisasjon[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [editingUtdanning, setEditingUtdanning] = useState<Utdanning | null>(null);
  
  // Filter og pagination state
  const [filter, setFilter] = useState<UtdanningFilter>({ aktiv: true });
  const [currentPage, setCurrentPage] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');

  // Form state for create/edit
  const [formData, setFormData] = useState({
    navn: '',
    studienivaa: '',
    studiepoeng: '',
    varighet: '',
    studiested: '',
    undervisningssprak: 'norsk',
    beskrivelse: '',
    starttidspunkt: '',
    studieform: 'HELTID' as Studieform,
    organisasjonId: ''
  });

  const resetForm = () => {
    setFormData({
      navn: '',
      studienivaa: '',
      studiepoeng: '',
      varighet: '',
      studiested: '',
      undervisningssprak: 'norsk',
      beskrivelse: '',
      starttidspunkt: '',
      studieform: 'HELTID',
      organisasjonId: ''
    });
    setEditingUtdanning(null);
  };

  const loadUtdanninger = async (page = 0, searchFilter?: UtdanningFilter) => {
    try {
      setLoading(true);
      const finalFilter = { ...filter, ...searchFilter };
      const variables = {
        filter: finalFilter,
        page: { page, size: 20 }
      };
      
      const data = await graphQLRequest(GET_UTDANNINGER_QUERY, variables, token);
      setUtdanninger(data.utdanninger);
      setCurrentPage(page);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Feil ved lasting av utdanninger');
    } finally {
      setLoading(false);
    }
  };

  const loadOrganisasjoner = async () => {
    try {
      const data = await graphQLRequest(GET_ORGANISATIONS_QUERY, {}, token);
      setOrganisasjoner(data.organisasjoner.filter((org: any) => org.aktiv));
    } catch (err) {
      console.error('Feil ved lasting av organisasjoner:', err);
    }
  };

  useEffect(() => {
    if (token) {
      loadUtdanninger();
      if (hasPermission('ADMIN_ORGANISATIONS')) {
        loadOrganisasjoner();
      }
    }
  }, [token, filter]);

  const handleSearch = () => {
    const searchFilter = searchTerm 
      ? { ...filter, navn: searchTerm }
      : { ...filter, navn: undefined };
    loadUtdanninger(0, searchFilter);
  };

  const handleCreateUtdanning = async () => {
    try {
      const input = {
        ...formData,
        studiepoeng: parseInt(formData.studiepoeng),
        varighet: parseInt(formData.varighet)
      };
      
      await graphQLRequest(CREATE_UTDANNING_MUTATION, { input }, token);
      setIsDialogOpen(false);
      resetForm();
      await loadUtdanninger(currentPage);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Feil ved opprettelse av utdanning');
    }
  };

  const handleUpdateUtdanning = async () => {
    if (!editingUtdanning) return;
    
    try {
      const input = {
        id: editingUtdanning.id,
        ...formData,
        studiepoeng: formData.studiepoeng ? parseInt(formData.studiepoeng) : undefined,
        varighet: formData.varighet ? parseInt(formData.varighet) : undefined
      };
      
      // Fjern tomme felter
      Object.keys(input).forEach(key => {
        if (input[key as keyof typeof input] === '') {
          delete input[key as keyof typeof input];
        }
      });
      
      await graphQLRequest(UPDATE_UTDANNING_MUTATION, { input }, token);
      setIsDialogOpen(false);
      resetForm();
      await loadUtdanninger(currentPage);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Feil ved oppdatering av utdanning');
    }
  };

  const handleToggleActive = async (utdanning: Utdanning) => {
    try {
      const mutation = utdanning.aktiv ? DEACTIVATE_UTDANNING_MUTATION : ACTIVATE_UTDANNING_MUTATION;
      await graphQLRequest(mutation, { id: utdanning.id }, token);
      await loadUtdanninger(currentPage);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Feil ved endring av status');
    }
  };

  const handleDeleteUtdanning = async (id: string) => {
    if (!confirm('Er du sikker på at du vil slette denne utdanningen permanent? Dette kan ikke angres.')) return;
    
    try {
      await graphQLRequest(DELETE_UTDANNING_MUTATION, { id }, token);
      await loadUtdanninger(currentPage);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Feil ved sletting av utdanning');
    }
  };

  const openEditDialog = (utdanning: Utdanning) => {
    setEditingUtdanning(utdanning);
    setFormData({
      navn: utdanning.navn,
      studienivaa: utdanning.studienivaa,
      studiepoeng: utdanning.studiepoeng.toString(),
      varighet: utdanning.varighet.toString(),
      studiested: utdanning.studiested,
      undervisningssprak: utdanning.undervisningssprak,
      beskrivelse: utdanning.beskrivelse || '',
      starttidspunkt: utdanning.starttidspunkt,
      studieform: utdanning.studieform,
      organisasjonId: utdanning.organisasjon.id
    });
    setIsDialogOpen(true);
  };

  const getStudienivaaColor = (nivaa: string) => {
    const lowerNivaa = nivaa.toLowerCase();
    if (lowerNivaa.includes('bachelor')) return 'bg-blue-100 text-blue-800';
    if (lowerNivaa.includes('master')) return 'bg-green-100 text-green-800';
    if (lowerNivaa.includes('phd') || lowerNivaa.includes('doktor')) return 'bg-purple-100 text-purple-800';
    if (lowerNivaa.includes('fagskole')) return 'bg-orange-100 text-orange-800';
    return 'bg-gray-100 text-gray-800';
  };

  const canEdit = hasPermission('ADMIN_ORGANISATIONS') || hasPermission('MANAGE_UTDANNINGER');
  const canDelete = hasPermission('ADMIN_ORGANISATIONS');

  if (loading && utdanninger.content.length === 0) {
    return <div className="flex items-center justify-center h-64">Laster utdanninger...</div>;
  }

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <GraduationCap className="h-5 w-5" />
            Utdanninger
          </CardTitle>
          <CardDescription>
            Administrer utdanninger i systemet. {selectedRole === 'Administrator' ? 'Du kan se og redigere alle utdanninger.' : 'Du kan kun se utdanninger fra din organisasjon.'}
          </CardDescription>
        </CardHeader>
        
        <CardContent className="space-y-4">
          {error && (
            <Alert variant="destructive">
              <AlertCircle className="h-4 w-4" />
              <AlertDescription>{error}</AlertDescription>
            </Alert>
          )}

          {/* Søk og filter */}
          <div className="flex gap-4 items-end">
            <div className="flex-1">
              <Label htmlFor="search">Søk i utdanningsnavn</Label>
              <Input
                id="search"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Søk etter utdanninger..."
                onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
              />
            </div>
            <Button onClick={handleSearch}>Søk</Button>
            <Button
              onClick={() => {
                setSearchTerm('');
                setFilter({ aktiv: true });
                loadUtdanninger(0, { aktiv: true });
              }}
              variant="outline"
            >
              Nullstill
            </Button>
            {canEdit && (
              <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
                <DialogTrigger asChild>
                  <Button onClick={resetForm}>
                    <Plus className="h-4 w-4 mr-2" />
                    Ny utdanning
                  </Button>
                </DialogTrigger>
                <DialogContent className="max-w-2xl">
                  <DialogHeader>
                    <DialogTitle>
                      {editingUtdanning ? 'Rediger utdanning' : 'Opprett ny utdanning'}
                    </DialogTitle>
                    <DialogDescription>
                      {editingUtdanning 
                        ? 'Oppdater informasjonen for denne utdanningen.'
                        : 'Legg til en ny utdanning i systemet.'
                      }
                    </DialogDescription>
                  </DialogHeader>
                  
                  <div className="grid grid-cols-2 gap-4">
                    <div className="col-span-2">
                      <Label htmlFor="navn">Utdanningsnavn *</Label>
                      <Input
                        id="navn"
                        value={formData.navn}
                        onChange={(e) => setFormData({ ...formData, navn: e.target.value })}
                        placeholder="f.eks. Bachelor i informatikk"
                        required
                      />
                    </div>
                    
                    <div>
                      <Label htmlFor="studienivaa">Studienivå *</Label>
                      <Select
                        value={formData.studienivaa}
                        onValueChange={(value) => setFormData({ ...formData, studienivaa: value })}
                      >
                        <SelectTrigger>
                          <SelectValue placeholder="Velg studienivå" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="bachelor">Bachelor</SelectItem>
                          <SelectItem value="master">Master</SelectItem>
                          <SelectItem value="phd">PhD/Doktorgrad</SelectItem>
                          <SelectItem value="fagskole">Fagskole</SelectItem>
                          <SelectItem value="kurs">Kurs/Videreutdanning</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                    
                    <div>
                      <Label htmlFor="studiepoeng">Studiepoeng *</Label>
                      <Input
                        id="studiepoeng"
                        type="number"
                        value={formData.studiepoeng}
                        onChange={(e) => setFormData({ ...formData, studiepoeng: e.target.value })}
                        placeholder="f.eks. 180"
                        required
                      />
                    </div>
                    
                    <div>
                      <Label htmlFor="varighet">Varighet (semestre) *</Label>
                      <Input
                        id="varighet"
                        type="number"
                        value={formData.varighet}
                        onChange={(e) => setFormData({ ...formData, varighet: e.target.value })}
                        placeholder="f.eks. 6"
                        required
                      />
                    </div>
                    
                    <div>
                      <Label htmlFor="studiested">Studiested *</Label>
                      <Input
                        id="studiested"
                        value={formData.studiested}
                        onChange={(e) => setFormData({ ...formData, studiested: e.target.value })}
                        placeholder="f.eks. Oslo, Trondheim"
                        required
                      />
                    </div>
                    
                    <div>
                      <Label htmlFor="starttidspunkt">Starttidspunkt *</Label>
                      <Select
                        value={formData.starttidspunkt}
                        onValueChange={(value) => setFormData({ ...formData, starttidspunkt: value })}
                      >
                        <SelectTrigger>
                          <SelectValue placeholder="Velg starttidspunkt" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="HØST_2025">Høst 2025</SelectItem>
                          <SelectItem value="VÅR_2026">Vår 2026</SelectItem>
                          <SelectItem value="HØST_2026">Høst 2026</SelectItem>
                          <SelectItem value="VÅR_2027">Vår 2027</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                    
                    <div>
                      <Label htmlFor="studieform">Studieform *</Label>
                      <Select
                        value={formData.studieform}
                        onValueChange={(value: Studieform) => setFormData({ ...formData, studieform: value })}
                      >
                        <SelectTrigger>
                          <SelectValue />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="HELTID">Heltid</SelectItem>
                          <SelectItem value="DELTID">Deltid</SelectItem>
                        </SelectContent>
                      </Select>
                    </div>
                    
                    {hasPermission('ADMIN_ORGANISATIONS') && organisasjoner.length > 0 && (
                      <div>
                        <Label htmlFor="organisasjon">Organisasjon *</Label>
                        <Select
                          value={formData.organisasjonId}
                          onValueChange={(value) => setFormData({ ...formData, organisasjonId: value })}
                        >
                          <SelectTrigger>
                            <SelectValue placeholder="Velg organisasjon" />
                          </SelectTrigger>
                          <SelectContent>
                            {organisasjoner.map((org) => (
                              <SelectItem key={org.id} value={org.id}>
                                {org.navn}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                      </div>
                    )}
                    
                    <div className="col-span-2">
                      <Label htmlFor="beskrivelse">Beskrivelse</Label>
                      <Input
                        id="beskrivelse"
                        value={formData.beskrivelse}
                        onChange={(e) => setFormData({ ...formData, beskrivelse: e.target.value })}
                        placeholder="Kort beskrivelse av utdanningen"
                      />
                    </div>
                  </div>
                  
                  <DialogFooter>
                    <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                      Avbryt
                    </Button>
                    <Button onClick={editingUtdanning ? handleUpdateUtdanning : handleCreateUtdanning}>
                      {editingUtdanning ? 'Oppdater' : 'Opprett'}
                    </Button>
                  </DialogFooter>
                </DialogContent>
              </Dialog>
            )}
          </div>

          {/* Resultat info */}
          <div className="flex items-center justify-between text-sm text-muted-foreground">
            <span>
              Viser {utdanninger.content.length} av {utdanninger.totalElements} utdanninger
            </span>
            <div className="flex gap-1">
              <Badge variant={filter.aktiv === true ? 'default' : 'secondary'}>Aktive</Badge>
              {filter.aktiv !== true && <Badge variant="secondary">Inkluderer inaktive</Badge>}
            </div>
          </div>

          {/* Tabell */}
          <div className="rounded-md border">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Utdanning</TableHead>
                  <TableHead>Organisasjon</TableHead>
                  <TableHead>Nivå</TableHead>
                  <TableHead>Studiepoeng</TableHead>
                  <TableHead>Varighet</TableHead>
                  <TableHead>Form</TableHead>
                  <TableHead>Start</TableHead>
                  <TableHead>Status</TableHead>
                  {canEdit && <TableHead>Handlinger</TableHead>}
                </TableRow>
              </TableHeader>
              <TableBody>
                {utdanninger.content.map((utdanning) => (
                  <TableRow key={utdanning.id}>
                    <TableCell>
                      <div>
                        <div className="font-medium">{utdanning.navn}</div>
                        {utdanning.beskrivelse && (
                          <div className="text-sm text-muted-foreground mt-1">
                            {utdanning.beskrivelse}
                          </div>
                        )}
                        <div className="text-xs text-muted-foreground mt-1">
                          {utdanning.studiested}
                        </div>
                      </div>
                    </TableCell>
                    <TableCell>
                      <div>
                        <div className="font-medium">{utdanning.organisasjon.navn}</div>
                        {utdanning.organisasjon.kortNavn && (
                          <div className="text-xs text-muted-foreground">
                            {utdanning.organisasjon.kortNavn}
                          </div>
                        )}
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge className={getStudienivaaColor(utdanning.studienivaa)}>
                        {utdanning.studienivaa}
                      </Badge>
                    </TableCell>
                    <TableCell>{utdanning.studiepoeng}</TableCell>
                    <TableCell>{utdanning.varighet} sem</TableCell>
                    <TableCell>
                      <Badge variant={utdanning.studieform === 'HELTID' ? 'default' : 'secondary'}>
                        {utdanning.studieform === 'HELTID' ? 'Heltid' : 'Deltid'}
                      </Badge>
                    </TableCell>
                    <TableCell>{utdanning.starttidspunkt}</TableCell>
                    <TableCell>
                      <Badge variant={utdanning.aktiv ? 'default' : 'destructive'}>
                        {utdanning.aktiv ? 'Aktiv' : 'Inaktiv'}
                      </Badge>
                    </TableCell>
                    {canEdit && (
                      <TableCell>
                        <div className="flex gap-2">
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => openEditDialog(utdanning)}
                          >
                            <Pencil className="h-4 w-4" />
                          </Button>
                          <Button
                            variant="ghost"
                            size="sm"
                            onClick={() => handleToggleActive(utdanning)}
                          >
                            {utdanning.aktiv ? (
                              <EyeOff className="h-4 w-4" />
                            ) : (
                              <Eye className="h-4 w-4" />
                            )}
                          </Button>
                          {canDelete && (
                            <Button
                              variant="ghost"
                              size="sm"
                              onClick={() => handleDeleteUtdanning(utdanning.id)}
                            >
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          )}
                        </div>
                      </TableCell>
                    )}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>

          {/* Paginering */}
          {utdanninger.totalPages > 1 && (
            <div className="flex items-center justify-between">
              <div className="text-sm text-muted-foreground">
                Side {currentPage + 1} av {utdanninger.totalPages}
              </div>
              <div className="flex gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => loadUtdanninger(currentPage - 1)}
                  disabled={!utdanninger.hasPrevious}
                >
                  <ChevronLeft className="h-4 w-4" />
                  Forrige
                </Button>
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => loadUtdanninger(currentPage + 1)}
                  disabled={!utdanninger.hasNext}
                >
                  Neste
                  <ChevronRight className="h-4 w-4" />
                </Button>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}