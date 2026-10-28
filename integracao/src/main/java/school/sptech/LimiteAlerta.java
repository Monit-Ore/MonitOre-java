package school.sptech;

public class LimiteAlerta {

    private Integer fkPcIndustrial;
    private Integer fkComponente;
    private Double atencao;
    private Double critico;

    public LimiteAlerta() {
    }

    public Integer getFkPcIndustrial() {
        return fkPcIndustrial;
    }

    public void setFkPcIndustrial(Integer fkPcIndustrial) {
        this.fkPcIndustrial = fkPcIndustrial;
    }

    public Integer getFkComponente() {
        return fkComponente;
    }

    public void setFkComponente(Integer fkComponente) {
        this.fkComponente = fkComponente;
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
        return "limiteAlerta{" +
                "fkPcIndustrial=" + fkPcIndustrial +
                ", fkComponente=" + fkComponente +
                ", atencao=" + atencao +
                ", critico=" + critico +
                '}';
    }
}
