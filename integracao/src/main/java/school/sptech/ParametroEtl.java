package school.sptech;

 public class ParametroEtl {
     private String componente;
     private String hostname;
     private Double atencao;
     private Double critico;
     private String torre;

    public ParametroEtl() {
    }

     public String getTorre() {
         return torre;
     }

     public void setTorre(String torre) {
         this.torre = torre;
     }

     public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getComponente() {
        return componente;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public Double getAtencao() {
        return atencao;
    }

    public void setAtencao(Double atencao) {
        this.atencao = atencao;
    }

    public Double getCritico() {
        return critico;
    }

    public void setCritico(Double critico) {
        this.critico = critico;
    }

     @Override
     public String toString() {
         return "ParametroEtl{" +
                 "componente='" + componente + '\'' +
                 ", hostname='" + hostname + '\'' +
                 ", atencao=" + atencao +
                 ", critico=" + critico +
                 ", torre=" + torre +
                 '}';
     }
 }
