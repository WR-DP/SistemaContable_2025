package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transaccion", schema = "public")
public class Transaccion {
    @Id
    @ColumnDefault("gen_random_uuid()")
    @Column(name = "id_transaccion", nullable = false)
    private UUID id;

    @NotNull
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @NotNull
    @Column(name = "descripcion", nullable = false, length = Integer.MAX_VALUE)
    private String descripcion;

    @NotNull
    @Column(name = "monto", nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Size(max = 3)
    @Column(name = "moneda", length = 3)
    private String moneda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archivo_cargado_id")
    private ArchivoCargado archivoCargado;

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

    public ArchivoCargado getArchivoCargado() {
        return archivoCargado;
    }

    public void setArchivoCargado(ArchivoCargado archivoCargado) {
        this.archivoCargado = archivoCargado;
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