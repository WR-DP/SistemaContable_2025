package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "detalle_venta", schema = "public")
public class DetalleVenta {
    @Id
    @Column(name = "id_detalle", nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    private sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Producto producto;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", precision = 15, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "descuento", precision = 15, scale = 2)
    private BigDecimal descuento;

    @Column(name = "subtotal", precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "created_at")
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Venta getVenta() {
        return venta;
    }

    public void setVenta(sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Venta venta) {
        this.venta = venta;
    }

    public sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Producto getProducto() {
        return producto;
    }

    public void setProducto(sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Producto producto) {
        this.producto = producto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}