package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

public class ResumenTransaccionDTO {
    private UUID transaccionID;
    private Integer año;
    private Integer trimestre;
    private Integer mes;
    private String periodo;
    private Long cantidadTransacciones;
    private BigDecimal totalMonto;
    private String moneda;
    private String descripcion;

    // Constructor 1: Para transacciones individuales (con ID)
    public ResumenTransaccionDTO() {
        this.transaccionID = transaccionID;
        this.año = año;
        this.mes = mes;
        YearMonth yearMonth = YearMonth.of(año, mes);
        this.periodo = yearMonth.getMonth().toString() + " " + año;
        this.descripcion = descripcion;
        this.totalMonto = totalMonto;
        this.moneda = moneda;
        this.cantidadTransacciones = 1L;
    }

    // Constructor 2: Para agrupación por Año
    public ResumenTransaccionDTO(Integer año, Long cantidadTransacciones,
                                 BigDecimal totalMonto, String moneda) {
        this.año = año;
        this.periodo = "Año " + año;
        this.cantidadTransacciones = cantidadTransacciones;
        this.totalMonto = totalMonto;
        this.moneda = moneda;
    }
    // Métodos factory para crear instancias específicas
    public static ResumenTransaccionDTO crearParaTrimestre(Integer año, Integer trimestre,
                                                           Long cantidad, BigDecimal total, String moneda) {
        ResumenTransaccionDTO dto = new ResumenTransaccionDTO();
        dto.año = año;
        dto.trimestre = trimestre;
        dto.periodo = String.format("%d-T%d", año, trimestre);
        dto.cantidadTransacciones = cantidad;
        dto.totalMonto = total;
        dto.moneda = moneda;
        return dto;
    }
    public static ResumenTransaccionDTO crearParaMes(Integer año, Integer mes,
                                                     Long cantidad, BigDecimal total, String moneda) {
        ResumenTransaccionDTO dto = new ResumenTransaccionDTO();
        dto.año = año;
        dto.mes = mes;
        YearMonth yearMonth = YearMonth.of(año, mes);
        dto.periodo = yearMonth.getMonth().toString() + " " + año;
        dto.cantidadTransacciones = cantidad;
        dto.totalMonto = total;
        dto.moneda = moneda;
        return dto;
    }
    // GETTERS Y SETTERS (los mismos que antes)
    public UUID getTransaccionID() {return transaccionID;}
    public void setTransaccionID(UUID transaccionID) {this.transaccionID = transaccionID;}
    public Integer getAño() {return año;}
    public void setAño(Integer año) {this.año = año;}
    public Integer getTrimestre() {return trimestre;}
    public void setTrimestre(Integer trimestre) {this.trimestre = trimestre;}
    public Integer getMes() {return mes;}
    public void setMes(Integer mes) {this.mes = mes;}
    public String getPeriodo() {return periodo;}
    public void setPeriodo(String periodo) {this.periodo = periodo;}
    public Long getCantidadTransacciones() {return cantidadTransacciones;}
    public void setCantidadTransacciones(Long cantidadTransacciones) {this.cantidadTransacciones = cantidadTransacciones;}
    public BigDecimal getTotalMonto() {return totalMonto;}
    public void setTotalMonto(BigDecimal totalMonto) {this.totalMonto = totalMonto;}
    public String getMoneda() {return moneda;}
    public void setMoneda(String moneda) {this.moneda = moneda;}
    public String getDescripcion() {return descripcion;}
    public void setDescripcion(String descripcion) {this.descripcion = descripcion;}
    public boolean esTransaccionIndividual() {return transaccionID != null;}
}