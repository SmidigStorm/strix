package no.utdanning.opptak.domain;

import java.time.LocalDateTime;

public class UtdanningIOpptak {
    private String id;
    private String utdanningId;
    private String opptakId;
    private Integer antallPlasser;
    private Boolean aktivt;
    private LocalDateTime opprettet;
    private Utdanning utdanning;
    private Opptak opptak;

    public UtdanningIOpptak() {}

    public UtdanningIOpptak(String id, String utdanningId, String opptakId, Integer antallPlasser, 
                           Boolean aktivt, LocalDateTime opprettet) {
        this.id = id;
        this.utdanningId = utdanningId;
        this.opptakId = opptakId;
        this.antallPlasser = antallPlasser;
        this.aktivt = aktivt;
        this.opprettet = opprettet;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getUtdanningId() { return utdanningId; }
    public void setUtdanningId(String utdanningId) { this.utdanningId = utdanningId; }
    
    public String getOpptakId() { return opptakId; }
    public void setOpptakId(String opptakId) { this.opptakId = opptakId; }
    
    public Integer getAntallPlasser() { return antallPlasser; }
    public void setAntallPlasser(Integer antallPlasser) { this.antallPlasser = antallPlasser; }
    
    public Boolean getAktivt() { return aktivt; }
    public void setAktivt(Boolean aktivt) { this.aktivt = aktivt; }
    
    public LocalDateTime getOpprettet() { return opprettet; }
    public void setOpprettet(LocalDateTime opprettet) { this.opprettet = opprettet; }
    
    public Utdanning getUtdanning() { return utdanning; }
    public void setUtdanning(Utdanning utdanning) { this.utdanning = utdanning; }
    
    public Opptak getOpptak() { return opptak; }
    public void setOpptak(Opptak opptak) { this.opptak = opptak; }
}