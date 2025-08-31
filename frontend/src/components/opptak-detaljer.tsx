import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { ArrowLeft, Calendar, Users, Building, Settings, Check, X } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Separator } from '@/components/ui/separator'
import { graphQLRequest, GET_OPPTAK_QUERY } from '@/lib/api'
import { useRole } from '@/contexts/RoleContext'
import { useAuth } from '@/contexts/AuthContext'

type OpptaksType = 'UHG' | 'FSU' | 'LOKALT'
type OpptaksStatus = 'FREMTIDIG' | 'APENT' | 'STENGT' | 'AVSLUTTET'

interface Organisasjon {
  id: string
  navn: string
  kortNavn?: string
  type?: string
}

interface Utdanning {
  id: string
  navn: string
  studienivaa: string
  studiepoeng: number
  organisasjon: Organisasjon
}

interface UtdanningIOpptak {
  id: string
  antallPlasser: number
  aktivt: boolean
  opprettet: string
  utdanning: Utdanning
}

interface Opptak {
  id: string
  navn: string
  type: OpptaksType
  aar: number
  soknadsfrist?: string
  svarfrist?: string
  maxUtdanningerPerSoknad: number
  status: OpptaksStatus
  opptaksomgang?: string
  beskrivelse?: string
  opprettet: string
  aktiv: boolean
  samordnet: boolean
  administrator: Organisasjon
  utdanninger: UtdanningIOpptak[]
  tillatteTilgangsorganisasjoner: Organisasjon[]
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return 'Ikke satt'
  return new Date(dateStr).toLocaleDateString('nb-NO', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const getStatusVariant = (status: string) => {
  switch (status) {
    case 'APENT':
      return 'default'
    case 'STENGT':
      return 'secondary'
    case 'AVSLUTTET':
      return 'outline'
    default:
      return 'outline'
  }
}

const getTypeLabel = (type: string) => {
  switch (type) {
    case 'UHG':
      return 'Universiteter og høgskoler'
    case 'FSU':
      return 'Fagskoler'
    case 'LOKALT':
      return 'Lokalt opptak'
    default:
      return type
  }
}

export function OpptakDetaljer() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { hasPermission } = useRole()
  const { token } = useAuth()
  const [opptak, setOpptak] = useState<Opptak | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchOpptak = async () => {
      if (!id || !token) {
        setError('Mangler opptak ID eller autentisering')
        setLoading(false)
        return
      }

      try {
        setLoading(true)
        const data = await graphQLRequest<{ opptak: Opptak }>(
          GET_OPPTAK_QUERY,
          { id },
          token
        )

        if (!data.opptak) {
          setError('Opptak ikke funnet')
        } else {
          setOpptak(data.opptak)
        }
      } catch (err) {
        console.error('Feil ved henting av opptak:', err)
        setError(err instanceof Error ? err.message : 'Ukjent feil oppstod')
      } finally {
        setLoading(false)
      }
    }

    fetchOpptak()
  }, [id, token])

  if (loading) {
    return (
      <div className="container mx-auto p-6">
        <div className="flex items-center gap-4 mb-6">
          <Button variant="ghost" size="sm" onClick={() => navigate('/opptak')}>
            <ArrowLeft className="h-4 w-4" />
            Tilbake til opptak
          </Button>
        </div>
        <div className="text-center py-8">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto"></div>
          <p className="mt-4 text-muted-foreground">Laster opptak...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="container mx-auto p-6">
        <div className="flex items-center gap-4 mb-6">
          <Button variant="ghost" size="sm" onClick={() => navigate('/opptak')}>
            <ArrowLeft className="h-4 w-4" />
            Tilbake til opptak
          </Button>
        </div>
        <Card>
          <CardHeader>
            <CardTitle className="text-destructive">Feil</CardTitle>
          </CardHeader>
          <CardContent>
            <p>{error}</p>
          </CardContent>
        </Card>
      </div>
    )
  }

  if (!opptak) {
    return (
      <div className="container mx-auto p-6">
        <div className="flex items-center gap-4 mb-6">
          <Button variant="ghost" size="sm" onClick={() => navigate('/opptak')}>
            <ArrowLeft className="h-4 w-4" />
            Tilbake til opptak
          </Button>
        </div>
        <Card>
          <CardHeader>
            <CardTitle>Opptak ikke funnet</CardTitle>
          </CardHeader>
          <CardContent>
            <p>Det forespurte opptaket kunne ikke finnes.</p>
          </CardContent>
        </Card>
      </div>
    )
  }

  const canEdit = hasPermission('MANAGE_OPPTAK')

  return (
    <div className="container mx-auto p-6 space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="sm" onClick={() => navigate('/opptak')}>
            <ArrowLeft className="h-4 w-4" />
            Tilbake til opptak
          </Button>
          <div>
            <h1 className="text-3xl font-bold tracking-tight">{opptak.navn}</h1>
            <p className="text-muted-foreground">
              {getTypeLabel(opptak.type)} • {opptak.aar}
            </p>
          </div>
        </div>
        
        <div className="flex items-center gap-2">
          <Badge variant={getStatusVariant(opptak.status)} className="capitalize">
            {opptak.status.toLowerCase()}
          </Badge>
          {opptak.samordnet && (
            <Badge variant="secondary">
              <Users className="h-3 w-3 mr-1" />
              Samordnet
            </Badge>
          )}
          {opptak.aktiv ? (
            <Badge variant="default">
              <Check className="h-3 w-3 mr-1" />
              Aktiv
            </Badge>
          ) : (
            <Badge variant="destructive">
              <X className="h-3 w-3 mr-1" />
              Inaktiv
            </Badge>
          )}
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Main Content */}
        <div className="lg:col-span-2 space-y-6">
          {/* Basic Information */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Settings className="h-5 w-5" />
                Grunnleggende informasjon
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              {opptak.beskrivelse && (
                <div>
                  <h4 className="font-semibold mb-2">Beskrivelse</h4>
                  <p className="text-muted-foreground">{opptak.beskrivelse}</p>
                </div>
              )}

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <h4 className="font-semibold mb-1">Type</h4>
                  <p className="text-muted-foreground">{getTypeLabel(opptak.type)}</p>
                </div>
                <div>
                  <h4 className="font-semibold mb-1">År</h4>
                  <p className="text-muted-foreground">{opptak.aar}</p>
                </div>
                <div>
                  <h4 className="font-semibold mb-1">Status</h4>
                  <p className="text-muted-foreground capitalize">{opptak.status.toLowerCase()}</p>
                </div>
                {opptak.opptaksomgang && (
                  <div>
                    <h4 className="font-semibold mb-1">Opptaksomgang</h4>
                    <p className="text-muted-foreground">{opptak.opptaksomgang}</p>
                  </div>
                )}
              </div>

              <Separator />

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <h4 className="font-semibold mb-1">Søknadsfrist</h4>
                  <p className="text-muted-foreground">{formatDate(opptak.soknadsfrist)}</p>
                </div>
                <div>
                  <h4 className="font-semibold mb-1">Svarfrist</h4>
                  <p className="text-muted-foreground">{formatDate(opptak.svarfrist)}</p>
                </div>
                <div>
                  <h4 className="font-semibold mb-1">Maks utdanninger per søknad</h4>
                  <p className="text-muted-foreground">{opptak.maxUtdanningerPerSoknad}</p>
                </div>
                <div>
                  <h4 className="font-semibold mb-1">Opprettet</h4>
                  <p className="text-muted-foreground">{formatDate(opptak.opprettet)}</p>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Utdanninger */}
          {opptak.utdanninger && opptak.utdanninger.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Calendar className="h-5 w-5" />
                  Utdanninger i opptaket ({opptak.utdanninger.length})
                </CardTitle>
                <CardDescription>
                  Utdanninger som tilbys i dette opptaket
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-3">
                  {opptak.utdanninger.map((utdanningIOpptak: UtdanningIOpptak) => (
                    <div key={utdanningIOpptak.id} className="border rounded-lg p-4">
                      <div className="flex items-start justify-between">
                        <div className="flex-1">
                          <h4 className="font-semibold">{utdanningIOpptak.utdanning.navn}</h4>
                          <p className="text-sm text-muted-foreground">
                            {utdanningIOpptak.utdanning.studienivaa} • {utdanningIOpptak.utdanning.studiepoeng} studiepoeng
                          </p>
                          <p className="text-sm text-muted-foreground">
                            {utdanningIOpptak.utdanning.organisasjon.navn}
                          </p>
                        </div>
                        <div className="text-right">
                          <p className="font-semibold">{utdanningIOpptak.antallPlasser} plasser</p>
                          <Badge variant={utdanningIOpptak.aktivt ? "default" : "secondary"}>
                            {utdanningIOpptak.aktivt ? "Aktiv" : "Inaktiv"}
                          </Badge>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}
        </div>

        {/* Sidebar */}
        <div className="space-y-6">
          {/* Administrator */}
          <Card>
            <CardHeader>
              <CardTitle className="flex items-center gap-2">
                <Building className="h-5 w-5" />
                Administrator
              </CardTitle>
            </CardHeader>
            <CardContent>
              <div>
                <h4 className="font-semibold">{opptak.administrator.navn}</h4>
                {opptak.administrator.kortNavn && (
                  <p className="text-sm text-muted-foreground">({opptak.administrator.kortNavn})</p>
                )}
              </div>
            </CardContent>
          </Card>

          {/* Tilgangsorganisasjoner */}
          {opptak.samordnet && opptak.tillatteTilgangsorganisasjoner && opptak.tillatteTilgangsorganisasjoner.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Users className="h-5 w-5" />
                  Tilgangsorganisasjoner
                </CardTitle>
                <CardDescription>
                  Organisasjoner med tilgang til dette opptaket
                </CardDescription>
              </CardHeader>
              <CardContent>
                <div className="space-y-2">
                  {opptak.tillatteTilgangsorganisasjoner.map((org: Organisasjon) => (
                    <div key={org.id} className="flex items-center justify-between p-2 border rounded">
                      <div>
                        <p className="font-medium text-sm">{org.navn}</p>
                        {org.kortNavn && (
                          <p className="text-xs text-muted-foreground">({org.kortNavn})</p>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </CardContent>
            </Card>
          )}

          {/* Actions */}
          {canEdit && (
            <Card>
              <CardHeader>
                <CardTitle>Handlinger</CardTitle>
              </CardHeader>
              <CardContent className="space-y-2">
                <Button className="w-full" variant="outline">
                  Rediger opptak
                </Button>
                <Button className="w-full" variant="outline">
                  Endre status
                </Button>
                {opptak.aktiv ? (
                  <Button className="w-full" variant="destructive">
                    Deaktiver opptak
                  </Button>
                ) : (
                  <Button className="w-full" variant="default">
                    Reaktiver opptak
                  </Button>
                )}
              </CardContent>
            </Card>
          )}
        </div>
      </div>
    </div>
  )
}