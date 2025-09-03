package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cuentas_contables", schema = "public")
public class CuentasContable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 20)
    @NotNull
    @Column(name = "codigo", nullable = false, length = 20)
    private String codigo;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Lob
    @Column(name = "descripcion")
    private String descripcion;

    @Size(max = 50)
    @NotNull
    @Column(name = "tipo_cuenta", nullable = false, length = 50)
    private String tipoCuenta;

    @NotNull
    @Column(name = "nivel", nullable = false)
    private Integer nivel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_padre_id")
    private CuentasContable cuentaPadre;

    @Column(name = "activa")
    private Boolean activa;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "cuentaPadre")
    private Set<CuentasContable> cuentasContables = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cuentaDebe")
    private Set<Transaccione> transacciones = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public String getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(String tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public CuentasContable getCuentaPadre() {
        return cuentaPadre;
    }

    public void setCuentaPadre(CuentasContable cuentaPadre) {
        this.cuentaPadre = cuentaPadre;
    }

    public Boolean getActiva() {
        return activa;
    }

    public void setActiva(Boolean activa) {
        this.activa = activa;
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

    public Set<CuentasContable> getCuentasContables() {
        return cuentasContables;
    }

    public void setCuentasContables(Set<CuentasContable> cuentasContables) {
        this.cuentasContables = cuentasContables;
    }

    public Set<Transaccione> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(Set<Transaccione> transacciones) {
        this.transacciones = transacciones;
    }

}