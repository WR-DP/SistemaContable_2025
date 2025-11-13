package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "transaccion", schema = "public")
@NamedQueries({
        @NamedQuery(name = "Transaccion.findAll", query = "SELECT t FROM Transaccion t"),
        @NamedQuery(name = "Transaccion.findByFecha", query = "SELECT t FROM Transaccion t WHERE t.fecha = :fecha"),
        @NamedQuery(name = "Transaccion.findByDescripcion", query = "SELECT t FROM Transaccion t WHERE t.descripcion = :descripcion"),
        @NamedQuery(name = "Transaccion.findByMonto", query = "SELECT t FROM Transaccion t WHERE t.monto = :monto"),
        @NamedQuery(name = "Transaccion.findByMoneda", query = "SELECT t FROM Transaccion t WHERE t.moneda = :moneda"),
        @NamedQuery(name = "Transaccion.findByFilaExcel", query = "SELECT t FROM Transaccion t WHERE t.filaExcel = :filaExcel"),
        @NamedQuery(name = "Transaccion.findByCreatedAt", query = "SELECT t FROM Transaccion t WHERE t.createdAt = :createdAt"),
        @NamedQuery(name = "Transaccion.findByUpdatedAt", query = "SELECT t FROM Transaccion t WHERE t.updatedAt = :updatedAt"),
        @NamedQuery(name = "Transaccion.findByDateSpecificAll", query = "SELECT t FROM Transaccion t WHERE t.fecha = :fecha"),
        @NamedQuery(name = "Transaccion.findByDateSpecificItems", query = "SELECT t.idTransaccion, t.descripcion, t.monto, t.moneda, t.archivoCargadoId FROM Transaccion t WHERE t.fecha = :fecha"),
        @NamedQuery(name = "Transaccion.findByIdSpecific", query = "SELECT t.idTransaccion, t.descripcion, t.monto, t.moneda, t.archivoCargadoId FROM Transaccion t WHERE t.idTransaccion = :id_transaccion"),
        @NamedQuery(name = "Transaccion.findByQuarter", query = "SELECT t.idTransaccion, t.fecha, t.descripcion, t.monto, t.moneda, t.archivoCargadoId FROM Transaccion t WHERE t.fecha BETWEEN :fechaInicio AND :fechaFinal ORDER BY t.fecha ASC")
})
public class Transaccion {

    @Id
    @Basic(optional = false)
    @Column(name = "id_transaccion", nullable = false, updatable = false)
    private UUID idTransaccion;

    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Basic(optional = false)
    @Column(name = "descripcion")
    private String descripcion;

    @Basic(optional = false)
    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "moneda")
    private String moneda;

    @Column(name = "fila_excel")
    private Integer filaExcel;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @JoinColumn(name = "archivo_cargado_id", referencedColumnName = "id_archivo_cargado")
    @ManyToOne
    private ArchivoCargado archivoCargadoId;

    // 🔧 CORREGIDO: mappedBy debe coincidir con el atributo "transaccion" en TransaccionClasificacion
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "transaccion")
    private Collection<TransaccionClasificacion> transaccionClasificacionCollection;

    // ==============================
    // Constructores
    // ==============================
    public Transaccion() {
    }

    public Transaccion(UUID idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public Transaccion(UUID idTransaccion, Date fecha, String descripcion, BigDecimal monto) {
        this.idTransaccion = idTransaccion;
        this.fecha = fecha;
        this.descripcion = descripcion;
        this.monto = monto;
    }

    // ==============================
    // Getters y Setters
    // ==============================
    public UUID getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(UUID idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
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

    public Integer getFilaExcel() {
        return filaExcel;
    }

    public void setFilaExcel(Integer filaExcel) {
        this.filaExcel = filaExcel;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ArchivoCargado getArchivoCargadoId() {
        return archivoCargadoId;
    }

    public void setArchivoCargadoId(ArchivoCargado archivoCargadoId) {
        this.archivoCargadoId = archivoCargadoId;
    }

    public Collection<TransaccionClasificacion> getTransaccionClasificacionCollection() {
        return transaccionClasificacionCollection;
    }

    public void setTransaccionClasificacionCollection(Collection<TransaccionClasificacion> transaccionClasificacionCollection) {
        this.transaccionClasificacionCollection = transaccionClasificacionCollection;
    }
}
