package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "producto", schema = "public")
public class Producto {
    @Id
    @Column(name = "id_producto", nullable = false)
    private UUID id;

    @Size(max = 50)
    @Column(name = "codigo_barras", length = 50)
    private String codigoBarras;

    @Size(max = 20)
    @Column(name = "codigo_interno", length = 20)
    private String codigoInterno;

    @Size(max = 200)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_ventas_id")
    private CuentaContable cuentaVentas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_inventario_id")
    private CuentaContable cuentaInventario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_costo_ventas_id")
    private CuentaContable cuentaCostoVentas;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Size(max = 20)
    @Column(name = "unidad_medida", length = 20)
    private String unidadMedida;

    @Column(name = "stock_actual")
    private Integer stockActual;

    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Column(name = "stock_maximo")
    private Integer stockMaximo;

    @Column(name = "precio_compra", precision = 15, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "precio_venta", precision = 15, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "margen_ganancia", precision = 5, scale = 2)
    private BigDecimal margenGanancia;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "created_at")
    private Instant createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }

    public void setCodigoInterno(String codigoInterno) {
        this.codigoInterno = codigoInterno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public CuentaContable getCuentaVentas() {
        return cuentaVentas;
    }

    public void setCuentaVentas(CuentaContable cuentaVentas) {
        this.cuentaVentas = cuentaVentas;
    }

    public CuentaContable getCuentaInventario() {
        return cuentaInventario;
    }

    public void setCuentaInventario(CuentaContable cuentaInventario) {
        this.cuentaInventario = cuentaInventario;
    }

    public CuentaContable getCuentaCostoVentas() {
        return cuentaCostoVentas;
    }

    public void setCuentaCostoVentas(CuentaContable cuentaCostoVentas) {
        this.cuentaCostoVentas = cuentaCostoVentas;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getStockMaximo() {
        return stockMaximo;
    }

    public void setStockMaximo(Integer stockMaximo) {
        this.stockMaximo = stockMaximo;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public BigDecimal getMargenGanancia() {
        return margenGanancia;
    }

    public void setMargenGanancia(BigDecimal margenGanancia) {
        this.margenGanancia = margenGanancia;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}