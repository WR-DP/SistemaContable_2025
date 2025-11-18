package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf.converter.InstantAttributeConverter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "archivo_cargado", schema = "public")
@NamedQueries({
        @NamedQuery(name = "ArchivoCargado.findAll", query = "SELECT a FROM ArchivoCargado a ORDER BY a.fechaCarga DESC"),
        @NamedQuery(name = "ArchivoCargado.findByNombreLike", query = "SELECT a FROM ArchivoCargado a WHERE LOWER(a.nombreArchivo) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY a.fechaCarga DESC"),
        @NamedQuery(name = "ArchivoCargado.findByUsuario", query = "SELECT a FROM ArchivoCargado a WHERE a.usuarioCarga = :usuario ORDER BY a.fechaCarga DESC")
})
public class ArchivoCargado {
    @Id
    @Column(name = "id_archivo_cargado", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Size(max = 500)
    @Column(name = "ruta_archivo", length = 500)
    private String rutaArchivo;

    @Column(name = "\"tamaño_byte\"")
    private Long tamañoByte;

    @Column(name = "total_registro")
    private Integer totalRegistro;

    @Column(name = "registro_procesado")
    private Integer registroProcesado;

    @Column(name = "registro_con_error")
    private Integer registroConError;

    @Size(max = 20)
    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "fecha_carga")
    @Convert(converter = InstantAttributeConverter.class)
    private Instant fechaCarga;

    @Size(max = 100)
    @Column(name = "usuario_carga", length = 100)
    private String usuarioCarga;


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public Long getTamañoByte() {
        return tamañoByte;
    }

    public void setTamañoByte(Long tamañoByte) {
        this.tamañoByte = tamañoByte;
    }

    public Integer getTotalRegistro() {
        return totalRegistro;
    }

    public void setTotalRegistro(Integer totalRegistro) {
        this.totalRegistro = totalRegistro;
    }

    public Integer getRegistroProcesado() {
        return registroProcesado;
    }

    public void setRegistroProcesado(Integer registroProcesado) {
        this.registroProcesado = registroProcesado;
    }

    public Integer getRegistroConError() {
        return registroConError;
    }

    public void setRegistroConError(Integer registroConError) {
        this.registroConError = registroConError;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Instant getFechaCarga() {
        return fechaCarga;
    }

    public void setFechaCarga(Instant fechaCarga) {
        this.fechaCarga = fechaCarga;
    }

    public String getUsuarioCarga() {
        return usuarioCarga;
    }

    public void setUsuarioCarga(String usuarioCarga) {
        this.usuarioCarga = usuarioCarga;
    }

    public boolean isEmpty() {
        return this.id == null;
    }

    public UUID getIdArchivoCargado() {
        return id;
    }

    public java.util.stream.Stream<ArchivoCargado> stream() {
        return java.util.stream.Stream.of(this);
    }
}