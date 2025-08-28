package no.utdanning.opptak.graphql;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class QueryResolver {
    
    @QueryMapping
    public String hei(@Argument String navn) {
        if (navn == null || navn.isEmpty()) {
            return "Hei fra opptaksystemet!";
        }
        return "Hei " + navn + ", velkommen til opptaksystemet!";
    }
    
    @QueryMapping
    public SystemInfo systemInfo() {
        return new SystemInfo();
    }
    
    // Indre klasse for SystemInfo
    public static class SystemInfo {
        public String getVersjon() {
            return "0.0.1-SNAPSHOT";
        }
        
        public String getNavn() {
            return "Norsk Opptaksystem";
        }
        
        public String getBeskrivelse() {
            return "Opptaksystem for norsk utdanning - GraphQL API";
        }
    }
}