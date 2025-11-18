package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "transaccion_clasificacion", schema = "public")
public class TransaccionClasificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "transaccion_id", nullable = false)
    private Transaccion transaccion;

    @Size(max = 20)
    @NotNull
    @Column(name = "origen", nullable = false, length = 20)
    private String origen;

    @Size(max = 20)
    @Column(name = "tipo_transaccion", length = 20)
    private String tipoTransaccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_contable_debe_id")
    private CuentaContable cuentaContableDebe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_contable_haber_id")
    private CuentaContable cuentaContableHaber;

    @Column(name = "confianza_clasificacion", precision = 3, scale = 2)
    private BigDecimal confianzaClasificacion;

    @Column(name = "created_at")
    private Date createdAt;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transaccion getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(Transaccion transaccion) {
        this.transaccion = transaccion;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getTipoTransaccion() {
        return tipoTransaccion;
    }

    public void setTipoTransaccion(String tipoTransaccion) {
        this.tipoTransaccion = tipoTransaccion;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public CuentaContable getCuentaContableDebe() {
        return cuentaContableDebe;
    }

    public void setCuentaContableDebe(CuentaContable cuentaContableDebe) {
        this.cuentaContableDebe = cuentaContableDebe;
    }

    public CuentaContable getCuentaContableHaber() {
        return cuentaContableHaber;
    }

    public void setCuentaContableHaber(CuentaContable cuentaContableHaber) {
        this.cuentaContableHaber = cuentaContableHaber;
    }

    public BigDecimal getConfianzaClasificacion() {
        return confianzaClasificacion;
    }

    public void setConfianzaClasificacion(BigDecimal confianzaClasificacion) {
        this.confianzaClasificacion = confianzaClasificacion;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

}