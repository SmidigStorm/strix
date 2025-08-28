package no.utdanning.opptak.domain;

import java.time.LocalDateTime;

public class Utdanning {
    private String id;
    private String navn;
    private String studienivaa;
    private Integer studiepoeng;
    private Integer varighet;
    private String studiested;
    private String undervisningssprak;
    private String beskrivelse;
    private LocalDateTime opprettet;
    private Boolean aktiv;
    private String organisasjonId;
    private Organisasjon organisasjon;
    private UtdanningIOpptak opptakTilbud;

    public Utdanning() {}

    public Utdanning(String id, String navn, String studienivaa, Integer studiepoeng, Integer varighet,
                    String studiested, String undervisningssprak, String beskrivelse, 
                    LocalDateTime opprettet, Boolean aktiv, String organisasjonId) {
        this.id = id;
        this.navn = navn;
        this.studienivaa = studienivaa;
        this.studiepoeng = studiepoeng;
        this.varighet = varighet;
        this.studiested = studiested;
        this.undervisningssprak = undervisningssprak;
        this.beskrivelse = beskrivelse;
        this.opprettet = opprettet;
        this.aktiv = aktiv;
        this.organisasjonId = organisasjonId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNavn() { return navn; }
    public void setNavn(String navn) { this.navn = navn; }
    
    public String getStudienivaa() { return studienivaa; }
    public void setStudienivaa(String studienivaa) { this.studienivaa = studienivaa; }
    
    public Integer getStudiepoeng() { return studiepoeng; }
    public void setStudiepoeng(Integer studiepoeng) { this.studiepoeng = studiepoeng; }
    
    public Integer getVarighet() { return varighet; }
    public void setVarighet(Integer varighet) { this.varighet = varighet; }
    
    public String getStudiested() { return studiested; }
    public void setStudiested(String studiested) { this.studiested = studiested; }
    
    public String getUndervisningssprak() { return undervisningssprak; }
    public void setUndervisningssprak(String undervisningssprak) { this.undervisningssprak = undervisningssprak; }
    
    public String getBeskrivelse() { return beskrivelse; }
    public void setBeskrivelse(String beskrivelse) { this.beskrivelse = beskrivelse; }
    
    public LocalDateTime getOpprettet() { return opprettet; }
    public void setOpprettet(LocalDateTime opprettet) { this.opprettet = opprettet; }
    
    public Boolean getAktiv() { return aktiv; }
    public void setAktiv(Boolean aktiv) { this.aktiv = aktiv; }
    
    public String getOrganisasjonId() { return organisasjonId; }
    public void setOrganisasjonId(String organisasjonId) { this.organisasjonId = organisasjonId; }
    
    public Organisasjon getOrganisasjon() { return organisasjon; }
    public void setOrganisasjon(Organisasjon organisasjon) { this.organisasjon = organisasjon; }
    
    public UtdanningIOpptak getOpptakTilbud() { return opptakTilbud; }
    public void setOpptakTilbud(UtdanningIOpptak opptakTilbud) { this.opptakTilbud = opptakTilbud; }
}