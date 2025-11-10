package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "inventario", schema = "public")
public class Inventario {
    @Id
    @Column(name = "id_inventario", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Producto producto;

    @Size(max = 10)
    @Column(name = "tipo_movimiento", length = 10)
    private String tipoMovimiento;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "costo_unitario", precision = 15, scale = 2)
    private BigDecimal costoUnitario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaccion_id")
    private Transaccion transaccion;

    @Column(name = "fecha_movimiento")
    private LocalDate fechaMovimiento;

    @Size(max = 100)
    @Column(name = "motivo", length = 100)
    private String motivo;

    @Column(name = "created_at")
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Producto getProducto() {
        return producto;
    }

    public void setProducto(sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Producto producto) {
        this.producto = producto;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    public LocalDate getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(LocalDate fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}