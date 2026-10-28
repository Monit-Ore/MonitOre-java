package school.sptech;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Leitura {
    private String hostname;
    private Double cpu;
    private Double ram;
    private Double disco;
    private LocalDateTime dataHora;

    public Leitura(String hostname, Double cpu, Double ram, Double disco, LocalDateTime dataHora) {
        this.hostname = hostname;
        this.cpu = cpu;
        this.ram = ram;
        this.disco = disco;
        this.dataHora = dataHora;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public Double getCpu() {
        return cpu;
    }

    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }

    public Double getRam() {
        return ram;
    }

    public void setRam(Double ram) {
        this.ram = ram;
    }

    public Double getDisco() {
        return disco;
    }

    public void setDisco(Double disco) {
        this.disco = disco;
    }

    public Double getValor(String componente) {
        if (componente.equalsIgnoreCase("CPU")) return cpu;
        if (componente.equalsIgnoreCase("RAM")) return ram;
        if (componente.equalsIgnoreCase("DISCO")) return disco;
        return null;
    }

    public String getDtHrFormatada() {
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dataHora.format(formatador);
    }

    @Override
    public String toString() {
        return "Leitura{" +
                "hostname='" + hostname + '\'' +
                ", cpu=" + cpu +
                ", ram=" + ram +
                ", disco=" + disco +
                ", dataHora=" + dataHora +
                '}';
    }
}
