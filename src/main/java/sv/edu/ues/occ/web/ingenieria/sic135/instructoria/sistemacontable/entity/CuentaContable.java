package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cuenta_contable", schema = "public")
public class CuentaContable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta_contable", nullable = false)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuenta_padre_id")
    private CuentaContable cuentaPadre;

    @Column(name = "activa")
    private Boolean activa;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @OneToMany(mappedBy = "cuentaPadre")
    private Set<CuentaContable> cuentaContables = new LinkedHashSet<>();
    @OneToMany(mappedBy = "cuentaContableDebe")
    private Set<TransaccionClasificacion> transaccionClasificacions = new LinkedHashSet<>();

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

    public CuentaContable getCuentaPadre() {
        return cuentaPadre;
    }

    public void setCuentaPadre(CuentaContable cuentaPadre) {
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

    public Set<CuentaContable> getCuentaContables() {
        return cuentaContables;
    }

    public void setCuentaContables(Set<CuentaContable> cuentaContables) {
        this.cuentaContables = cuentaContables;
    }

    public Set<TransaccionClasificacion> getTransaccionClasificacions() {
        return transaccionClasificacions;
    }

    public void setTransaccionClasificacions(Set<TransaccionClasificacion> transaccionClasificacions) {
        this.transaccionClasificacions = transaccionClasificacions;
    }

/*
 TODO [Reverse Engineering] create field to map the 'tipo_cuenta' column
 Available actions: Define target Java type | Uncomment as is | Remove column mapping
    @Column(name = "tipo_cuenta", columnDefinition = "tipo_cuenta_enum not null")
    private Object tipoCuenta;
*/
}