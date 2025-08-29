package no.utdanning.opptak.domain;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "rolle")
public class Rolle {
    
    @Id
    @Column(name = "id", length = 50)
    private String id;
    
    @Column(name = "navn", length = 100, nullable = false, unique = true)
    private String navn;
    
    @Column(name = "beskrivelse")
    private String beskrivelse;
    
    @OneToMany(mappedBy = "rolle")
    private Set<BrukerRolle> brukerRoller;
    
    public Rolle() {}
    
    public Rolle(String id, String navn, String beskrivelse) {
        this.id = id;
        this.navn = navn;
        this.beskrivelse = beskrivelse;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNavn() {
        return navn;
    }
    
    public void setNavn(String navn) {
        this.navn = navn;
    }
    
    public String getBeskrivelse() {
        return beskrivelse;
    }
    
    public void setBeskrivelse(String beskrivelse) {
        this.beskrivelse = beskrivelse;
    }
    
    public Set<BrukerRolle> getBrukerRoller() {
        return brukerRoller;
    }
    
    public void setBrukerRoller(Set<BrukerRolle> brukerRoller) {
        this.brukerRoller = brukerRoller;
    }
}