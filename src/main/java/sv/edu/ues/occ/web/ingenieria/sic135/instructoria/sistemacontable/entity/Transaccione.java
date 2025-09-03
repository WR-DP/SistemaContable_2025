package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transacciones", schema = "public")
public class Transaccione {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull
    @Lob
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @NotNull
    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Size(max = 3)
    @Column(name = "moneda", length = 3)
    private String moneda;

    @Size(max = 20)
    @Column(name = "tipo_transaccion", length = 20)
    private String tipoTransaccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_debe_id")
    private CuentasContable cuentaDebe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_haber_id")
    private CuentasContable cuentaHaber;

    @Size(max = 20)
    @Column(name = "tipo_transaccion_manual", length = 20)
    private String tipoTransaccionManual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_debe_manual_id")
    private CuentasContable cuentaDebeManual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_haber_manual_id")
    private CuentasContable cuentaHaberManual;

    @Column(name = "clasificada_automaticamente")
    private Boolean clasificadaAutomaticamente;

    @Column(name = "editada_manualmente")
    private Boolean editadaManualmente;

    @Column(name = "confianza_clasificacion", precision = 3, scale = 2)
    private BigDecimal confianzaClasificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archivo_origen_id")
    private ArchivosCargado archivoOrigen;

    @Column(name = "fila_excel")
    private Integer filaExcel;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public String getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(String tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public CuentasContable getCuentaDebe() {
        return cuentaDebe;
    }

    public void setCuentaDebe(CuentasContable cuentaDebe) {
        this.cuentaDebe = cuentaDebe;
    }

    public CuentasContable getCuentaHaber() {
        return cuentaHaber;
    }

    public void setCuentaHaber(CuentasContable cuentaHaber) {
        this.cuentaHaber = cuentaHaber;
    }

    public String getTipoTransaccionManual() {
        return tipoTransaccionManual;
    }

    public void setTipoTransaccionManual(String tipoTransaccionManual) {
        this.tipoTransaccionManual = tipoTransaccionManual;
    }

    public CuentasContable getCuentaDebeManual() {
        return cuentaDebeManual;
    }

    public void setCuentaDebeManual(CuentasContable cuentaDebeManual) {
        this.cuentaDebeManual = cuentaDebeManual;
    }

    public CuentasContable getCuentaHaberManual() {
        return cuentaHaberManual;
    }

    public void setCuentaHaberManual(CuentasContable cuentaHaberManual) {
        this.cuentaHaberManual = cuentaHaberManual;
    }

    public Boolean getClasificadaAutomaticamente() {
        return clasificadaAutomaticamente;
    }

    public void setClasificadaAutomaticamente(Boolean clasificadaAutomaticamente) {
        this.clasificadaAutomaticamente = clasificadaAutomaticamente;
    }

    public Boolean getEditadaManualmente() {
        return editadaManualmente;
    }

    public void setEditadaManualmente(Boolean editadaManualmente) {
        this.editadaManualmente = editadaManualmente;
    }

    public BigDecimal getConfianzaClasificacion() {
        return confianzaClasificacion;
    }

    public void setConfianzaClasificacion(BigDecimal confianzaClasificacion) {
        this.confianzaClasificacion = confianzaClasificacion;
    }

    public ArchivosCargado getArchivoOrigen() {
        return archivoOrigen;
    }

    public void setArchivoOrigen(ArchivosCargado archivoOrigen) {
        this.archivoOrigen = archivoOrigen;
    }

    public Integer getFilaExcel() {
        return filaExcel;
    }

    public void setFilaExcel(Integer filaExcel) {
        this.filaExcel = filaExcel;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}