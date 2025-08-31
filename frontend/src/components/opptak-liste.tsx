import { useState, useEffect } from 'react';
import { useRole } from '@/contexts/RoleContext';
import { useAuth } from '@/contexts/AuthContext';
import { 
  graphQLRequest, 
  GET_ALL_OPPTAK_QUERY,
  CREATE_OPPTAK_MUTATION,
  UPDATE_OPPTAK_MUTATION,
  CHANGE_OPPTAK_STATUS_MUTATION,
  DEACTIVATE_OPPTAK_MUTATION,
  REACTIVATE_OPPTAK_MUTATION
} from '@/lib/api';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
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
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Plus, Pencil, Trash2, AlertCircle, MoreHorizontal, CheckCircle, XCircle, Clock, Archive } from 'lucide-react';

type OpptaksType = 'UHG' | 'FSU' | 'LOKALT';
type OpptaksStatus = 'FREMTIDIG' | 'APENT' | 'STENGT' | 'AVSLUTTET';

interface Organisasjon {
  id: string;
  navn: string;
  kortNavn?: string;
}

interface Opptak {
  id: string;
  navn: string;
  type: OpptaksType;
  aar: number;
  soknadsfrist?: string;
  svarfrist?: string;
  maxUtdanningerPerSoknad: number;
  status: OpptaksStatus;
  opptaksomgang?: string;
  beskrivelse?: string;
  opprettet: string;
  aktiv: boolean;
  samordnet: boolean;
  administrator: Organisasjon;
}

interface FormData {
  navn: string;
  type: OpptaksType;
  aar: number;
  administratorOrganisasjonId: string;
  soknadsfrist?: string;
  svarfrist?: string;
  maxUtdanningerPerSoknad: number;
  samordnet: boolean;
  opptaksomgang?: string;
  beskrivelse?: string;
}

export default function OpptaksListe() {
  const { selectedRole } = useRole();
  const { token } = useAuth();
  const [opptak, setOpptak] = useState<Opptak[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [selectedOpptak, setSelectedOpptak] = useState<Opptak | null>(null);
  const [filterType, setFilterType] = useState<OpptaksType | 'ALL'>('ALL');
  const [filterStatus, setFilterStatus] = useState<OpptaksStatus | 'ALL'>('ALL');
  const [filterYear, setFilterYear] = useState<number | 'ALL'>('ALL');
  const [searchTerm, setSearchTerm] = useState('');

  const currentYear = new Date().getFullYear();
  const years = Array.from({ length: 5 }, (_, i) => currentYear + i);

  const [formData, setFormData] = useState<FormData>({
    navn: '',
    type: 'LOKALT',
    aar: currentYear + 1,
    administratorOrganisasjonId: 'ntnu', // TODO: Get from user's organization
    maxUtdanningerPerSoknad: 10,
    samordnet: false,
  });

  const canCreateOpptak = selectedRole === 'Administrator' || selectedRole === 'Opptaksleder';
  const canEditOpptak = selectedRole === 'Administrator' || selectedRole === 'Opptaksleder';
  const canChangeStatus = selectedRole === 'Administrator' || selectedRole === 'Opptaksleder';

  useEffect(() => {
    fetchOpptak();
  }, [token]);

  const fetchOpptak = async () => {
    try {
      setLoading(true);
      const data = await graphQLRequest<{ alleOpptak: Opptak[] }>(
        GET_ALL_OPPTAK_QUERY,
        {},
        token
      );
      setOpptak(data.alleOpptak || []);
      setError(null);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'En feil oppstod ved henting av opptak');
      console.error('Error fetching opptak:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateOpptak = async () => {
    try {
      const input = {
        navn: formData.navn,
        type: formData.type,
        aar: formData.aar,
        administratorOrganisasjonId: formData.administratorOrganisasjonId,
        soknadsfrist: formData.soknadsfrist,
        svarfrist: formData.svarfrist,
        maxUtdanningerPerSoknad: formData.maxUtdanningerPerSoknad,
        samordnet: formData.samordnet,
        opptaksomgang: formData.opptaksomgang,
        beskrivelse: formData.beskrivelse,
      };

      await graphQLRequest(CREATE_OPPTAK_MUTATION, { input }, token);
      await fetchOpptak();
      setIsCreateDialogOpen(false);
      resetForm();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'En feil oppstod ved opprettelse av opptak');
      console.error('Error creating opptak:', err);
    }
  };

  const handleUpdateOpptak = async () => {
    if (!selectedOpptak) return;

    try {
      const input = {
        id: selectedOpptak.id,
        navn: formData.navn,
        soknadsfrist: formData.soknadsfrist,
        svarfrist: formData.svarfrist,
        maxUtdanningerPerSoknad: formData.maxUtdanningerPerSoknad,
        opptaksomgang: formData.opptaksomgang,
        beskrivelse: formData.beskrivelse,
      };

      await graphQLRequest(UPDATE_OPPTAK_MUTATION, { input }, token);
      await fetchOpptak();
      setIsEditDialogOpen(false);
      setSelectedOpptak(null);
      resetForm();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'En feil oppstod ved oppdatering av opptak');
      console.error('Error updating opptak:', err);
    }
  };

  const handleChangeStatus = async (opptakId: string, nyStatus: OpptaksStatus) => {
    try {
      await graphQLRequest(
        CHANGE_OPPTAK_STATUS_MUTATION,
        { input: { opptakId, nyStatus } },
        token
      );
      await fetchOpptak();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'En feil oppstod ved endring av status');
      console.error('Error changing status:', err);
    }
  };

  const handleDeactivateOpptak = async (opptakId: string) => {
    try {
      await graphQLRequest(DEACTIVATE_OPPTAK_MUTATION, { opptakId }, token);
      await fetchOpptak();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'En feil oppstod ved deaktivering');
      console.error('Error deactivating opptak:', err);
    }
  };

  const handleReactivateOpptak = async (opptakId: string) => {
    try {
      await graphQLRequest(REACTIVATE_OPPTAK_MUTATION, { opptakId }, token);
      await fetchOpptak();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'En feil oppstod ved reaktivering');
      console.error('Error reactivating opptak:', err);
    }
  };

  const resetForm = () => {
    setFormData({
      navn: '',
      type: 'LOKALT',
      aar: currentYear + 1,
      administratorOrganisasjonId: 'ntnu',
      maxUtdanningerPerSoknad: 10,
      samordnet: false,
    });
  };

  const openEditDialog = (opptak: Opptak) => {
    setSelectedOpptak(opptak);
    setFormData({
      navn: opptak.navn,
      type: opptak.type,
      aar: opptak.aar,
      administratorOrganisasjonId: opptak.administrator.id,
      soknadsfrist: opptak.soknadsfrist,
      svarfrist: opptak.svarfrist,
      maxUtdanningerPerSoknad: opptak.maxUtdanningerPerSoknad,
      samordnet: opptak.samordnet,
      opptaksomgang: opptak.opptaksomgang,
      beskrivelse: opptak.beskrivelse,
    });
    setIsEditDialogOpen(true);
  };

  const getStatusBadge = (status: OpptaksStatus) => {
    const statusConfig = {
      FREMTIDIG: { label: 'Fremtidig', variant: 'secondary' as const, icon: Clock },
      APENT: { label: 'Åpent', variant: 'default' as const, icon: CheckCircle },
      STENGT: { label: 'Stengt', variant: 'destructive' as const, icon: XCircle },
      AVSLUTTET: { label: 'Avsluttet', variant: 'outline' as const, icon: Archive },
    };

    const config = statusConfig[status];
    const Icon = config.icon;

    return (
      <Badge variant={config.variant} className="gap-1">
        <Icon className="h-3 w-3" />
        {config.label}
      </Badge>
    );
  };

  const getTypeBadge = (type: OpptaksType) => {
    const typeConfig = {
      UHG: { label: 'Universitet/Høgskole', variant: 'default' as const },
      FSU: { label: 'Fagskole', variant: 'secondary' as const },
      LOKALT: { label: 'Lokalt', variant: 'outline' as const },
    };

    const config = typeConfig[type];
    return <Badge variant={config.variant}>{config.label}</Badge>;
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('nb-NO', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
    });
  };

  const filteredOpptak = opptak.filter((o) => {
    const matchesType = filterType === 'ALL' || o.type === filterType;
    const matchesStatus = filterStatus === 'ALL' || o.status === filterStatus;
    const matchesYear = filterYear === 'ALL' || o.aar === filterYear;
    const matchesSearch = o.navn.toLowerCase().includes(searchTerm.toLowerCase()) ||
                          o.administrator.navn.toLowerCase().includes(searchTerm.toLowerCase());
    
    return matchesType && matchesStatus && matchesYear && matchesSearch;
  });

  if (loading) {
    return (
      <div className="container mx-auto py-8">
        <Card>
          <CardContent className="p-12 text-center">
            <div className="text-muted-foreground">Laster opptak...</div>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="container mx-auto py-8 space-y-6">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-3xl font-bold">Opptak</h1>
          <p className="text-muted-foreground mt-1">
            Administrer opptak og søknadsperioder
          </p>
        </div>
        {canCreateOpptak && (
          <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
            <DialogTrigger asChild>
              <Button>
                <Plus className="mr-2 h-4 w-4" />
                Opprett opptak
              </Button>
            </DialogTrigger>
            <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
              <DialogHeader>
                <DialogTitle>Opprett nytt opptak</DialogTitle>
                <DialogDescription>
                  Fyll ut informasjonen under for å opprette et nytt opptak.
                </DialogDescription>
              </DialogHeader>
              <div className="grid gap-4 py-4">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="navn">Navn *</Label>
                    <Input
                      id="navn"
                      value={formData.navn}
                      onChange={(e) => setFormData({ ...formData, navn: e.target.value })}
                      placeholder="F.eks. Samordnet opptak høst 2025"
                    />
                  </div>
                  <div>
                    <Label htmlFor="type">Type *</Label>
                    <Select
                      value={formData.type}
                      onValueChange={(value) => setFormData({ ...formData, type: value as OpptaksType })}
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="UHG">Universitet/Høgskole</SelectItem>
                        <SelectItem value="FSU">Fagskole</SelectItem>
                        <SelectItem value="LOKALT">Lokalt</SelectItem>
                      </SelectContent>
                    </Select>
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="aar">År *</Label>
                    <Select
                      value={formData.aar.toString()}
                      onValueChange={(value) => setFormData({ ...formData, aar: parseInt(value) })}
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        {years.map((year) => (
                          <SelectItem key={year} value={year.toString()}>
                            {year}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>
                  <div>
                    <Label htmlFor="opptaksomgang">Opptaksomgang</Label>
                    <Input
                      id="opptaksomgang"
                      value={formData.opptaksomgang || ''}
                      onChange={(e) => setFormData({ ...formData, opptaksomgang: e.target.value })}
                      placeholder="F.eks. Høst, Vår"
                    />
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="soknadsfrist">Søknadsfrist</Label>
                    <Input
                      id="soknadsfrist"
                      type="datetime-local"
                      value={formData.soknadsfrist || ''}
                      onChange={(e) => setFormData({ ...formData, soknadsfrist: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label htmlFor="svarfrist">Svarfrist</Label>
                    <Input
                      id="svarfrist"
                      type="datetime-local"
                      value={formData.svarfrist || ''}
                      onChange={(e) => setFormData({ ...formData, svarfrist: e.target.value })}
                    />
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <Label htmlFor="maxUtdanninger">Maks utdanninger per søknad</Label>
                    <Input
                      id="maxUtdanninger"
                      type="number"
                      min="1"
                      max="50"
                      value={formData.maxUtdanningerPerSoknad}
                      onChange={(e) => setFormData({ ...formData, maxUtdanningerPerSoknad: parseInt(e.target.value) })}
                    />
                  </div>
                  <div className="flex items-center space-x-2 mt-6">
                    <input
                      type="checkbox"
                      id="samordnet"
                      checked={formData.samordnet}
                      onChange={(e) => setFormData({ ...formData, samordnet: e.target.checked })}
                      className="rounded border-gray-300"
                    />
                    <Label htmlFor="samordnet">Samordnet opptak</Label>
                  </div>
                </div>
                <div>
                  <Label htmlFor="beskrivelse">Beskrivelse</Label>
                  <Textarea
                    id="beskrivelse"
                    value={formData.beskrivelse || ''}
                    onChange={(e) => setFormData({ ...formData, beskrivelse: e.target.value })}
                    placeholder="Valgfri beskrivelse av opptaket..."
                    rows={3}
                  />
                </div>
              </div>
              <DialogFooter>
                <Button variant="outline" onClick={() => setIsCreateDialogOpen(false)}>
                  Avbryt
                </Button>
                <Button onClick={handleCreateOpptak} disabled={!formData.navn}>
                  Opprett opptak
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        )}
      </div>

      {error && (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertDescription>{error}</AlertDescription>
        </Alert>
      )}

      <Card>
        <CardHeader>
          <div className="flex justify-between items-center">
            <CardTitle>Alle opptak ({filteredOpptak.length})</CardTitle>
            <div className="flex gap-2">
              <Input
                placeholder="Søk etter navn eller organisasjon..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-64"
              />
              <Select value={filterType} onValueChange={(value) => setFilterType(value as OpptaksType | 'ALL')}>
                <SelectTrigger className="w-40">
                  <SelectValue placeholder="Type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL">Alle typer</SelectItem>
                  <SelectItem value="UHG">UHG</SelectItem>
                  <SelectItem value="FSU">Fagskole</SelectItem>
                  <SelectItem value="LOKALT">Lokalt</SelectItem>
                </SelectContent>
              </Select>
              <Select value={filterStatus} onValueChange={(value) => setFilterStatus(value as OpptaksStatus | 'ALL')}>
                <SelectTrigger className="w-32">
                  <SelectValue placeholder="Status" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL">Alle statuser</SelectItem>
                  <SelectItem value="FREMTIDIG">Fremtidig</SelectItem>
                  <SelectItem value="APENT">Åpent</SelectItem>
                  <SelectItem value="STENGT">Stengt</SelectItem>
                  <SelectItem value="AVSLUTTET">Avsluttet</SelectItem>
                </SelectContent>
              </Select>
              <Select value={filterYear.toString()} onValueChange={(value) => setFilterYear(value === 'ALL' ? 'ALL' : parseInt(value))}>
                <SelectTrigger className="w-28">
                  <SelectValue placeholder="År" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ALL">Alle år</SelectItem>
                  {years.map((year) => (
                    <SelectItem key={year} value={year.toString()}>
                      {year}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
          </div>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Navn</TableHead>
                <TableHead>Type</TableHead>
                <TableHead>År</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Søknadsfrist</TableHead>
                <TableHead>Administrator</TableHead>
                <TableHead>Samordnet</TableHead>
                <TableHead className="text-right">Handlinger</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredOpptak.map((opptak) => (
                <TableRow key={opptak.id} className={!opptak.aktiv ? 'opacity-50' : ''}>
                  <TableCell className="font-medium">{opptak.navn}</TableCell>
                  <TableCell>{getTypeBadge(opptak.type)}</TableCell>
                  <TableCell>{opptak.aar}</TableCell>
                  <TableCell>{getStatusBadge(opptak.status)}</TableCell>
                  <TableCell>{formatDate(opptak.soknadsfrist)}</TableCell>
                  <TableCell>{opptak.administrator.kortNavn || opptak.administrator.navn}</TableCell>
                  <TableCell>
                    {opptak.samordnet ? (
                      <Badge variant="outline">Samordnet</Badge>
                    ) : (
                      <span className="text-muted-foreground">Nei</span>
                    )}
                  </TableCell>
                  <TableCell className="text-right">
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button variant="ghost" className="h-8 w-8 p-0">
                          <MoreHorizontal className="h-4 w-4" />
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="end">
                        <DropdownMenuLabel>Handlinger</DropdownMenuLabel>
                        <DropdownMenuItem
                          onClick={() => {
                            // TODO: Navigate to detail view
                            console.log('View details:', opptak.id);
                          }}
                        >
                          Se detaljer
                        </DropdownMenuItem>
                        {canEditOpptak && (
                          <>
                            <DropdownMenuSeparator />
                            <DropdownMenuItem onClick={() => openEditDialog(opptak)}>
                              <Pencil className="mr-2 h-4 w-4" />
                              Rediger
                            </DropdownMenuItem>
                            {canChangeStatus && (
                              <>
                                {opptak.status === 'FREMTIDIG' && (
                                  <DropdownMenuItem
                                    onClick={() => handleChangeStatus(opptak.id, 'APENT')}
                                  >
                                    <CheckCircle className="mr-2 h-4 w-4" />
                                    Åpne opptak
                                  </DropdownMenuItem>
                                )}
                                {opptak.status === 'APENT' && (
                                  <DropdownMenuItem
                                    onClick={() => handleChangeStatus(opptak.id, 'STENGT')}
                                  >
                                    <XCircle className="mr-2 h-4 w-4" />
                                    Steng opptak
                                  </DropdownMenuItem>
                                )}
                                {opptak.status === 'STENGT' && (
                                  <DropdownMenuItem
                                    onClick={() => handleChangeStatus(opptak.id, 'AVSLUTTET')}
                                  >
                                    <Archive className="mr-2 h-4 w-4" />
                                    Avslutt opptak
                                  </DropdownMenuItem>
                                )}
                              </>
                            )}
                            <DropdownMenuSeparator />
                            {opptak.aktiv ? (
                              <DropdownMenuItem
                                onClick={() => handleDeactivateOpptak(opptak.id)}
                                className="text-destructive"
                              >
                                <Trash2 className="mr-2 h-4 w-4" />
                                Deaktiver
                              </DropdownMenuItem>
                            ) : (
                              <DropdownMenuItem
                                onClick={() => handleReactivateOpptak(opptak.id)}
                              >
                                <CheckCircle className="mr-2 h-4 w-4" />
                                Reaktiver
                              </DropdownMenuItem>
                            )}
                          </>
                        )}
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </TableCell>
                </TableRow>
              ))}
              {filteredOpptak.length === 0 && (
                <TableRow>
                  <TableCell colSpan={8} className="text-center py-8 text-muted-foreground">
                    Ingen opptak funnet
                  </TableCell>
                </TableRow>
              )}
            </TableBody>
          </Table>
        </CardContent>
      </Card>

      {/* Edit Dialog */}
      <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
        <DialogContent className="max-w-2xl max-h-[90vh] overflow-y-auto">
          <DialogHeader>
            <DialogTitle>Rediger opptak</DialogTitle>
            <DialogDescription>
              Oppdater informasjonen for dette opptaket.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div>
              <Label htmlFor="edit-navn">Navn *</Label>
              <Input
                id="edit-navn"
                value={formData.navn}
                onChange={(e) => setFormData({ ...formData, navn: e.target.value })}
              />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="edit-soknadsfrist">Søknadsfrist</Label>
                <Input
                  id="edit-soknadsfrist"
                  type="datetime-local"
                  value={formData.soknadsfrist || ''}
                  onChange={(e) => setFormData({ ...formData, soknadsfrist: e.target.value })}
                />
              </div>
              <div>
                <Label htmlFor="edit-svarfrist">Svarfrist</Label>
                <Input
                  id="edit-svarfrist"
                  type="datetime-local"
                  value={formData.svarfrist || ''}
                  onChange={(e) => setFormData({ ...formData, svarfrist: e.target.value })}
                />
              </div>
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <Label htmlFor="edit-maxUtdanninger">Maks utdanninger per søknad</Label>
                <Input
                  id="edit-maxUtdanninger"
                  type="number"
                  min="1"
                  max="50"
                  value={formData.maxUtdanningerPerSoknad}
                  onChange={(e) => setFormData({ ...formData, maxUtdanningerPerSoknad: parseInt(e.target.value) })}
                />
              </div>
              <div>
                <Label htmlFor="edit-opptaksomgang">Opptaksomgang</Label>
                <Input
                  id="edit-opptaksomgang"
                  value={formData.opptaksomgang || ''}
                  onChange={(e) => setFormData({ ...formData, opptaksomgang: e.target.value })}
                />
              </div>
            </div>
            <div>
              <Label htmlFor="edit-beskrivelse">Beskrivelse</Label>
              <Textarea
                id="edit-beskrivelse"
                value={formData.beskrivelse || ''}
                onChange={(e) => setFormData({ ...formData, beskrivelse: e.target.value })}
                rows={3}
              />
            </div>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setIsEditDialogOpen(false)}>
              Avbryt
            </Button>
            <Button onClick={handleUpdateOpptak} disabled={!formData.navn}>
              Lagre endringer
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}