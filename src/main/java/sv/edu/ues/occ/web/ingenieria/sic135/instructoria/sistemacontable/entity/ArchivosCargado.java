package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "archivos_cargados", schema = "public")
public class ArchivosCargado {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Size(max = 255)
    @NotNull
    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Size(max = 500)
    @Column(name = "ruta_archivo", length = 500)
    private String rutaArchivo;

    @Column(name = "\"tamaño_bytes\"")
    private Long tamañoBytes;

    @Column(name = "total_registros")
    private Integer totalRegistros;

    @Column(name = "registros_procesados")
    private Integer registrosProcesados;

    @Column(name = "registros_con_error")
    private Integer registrosConError;

    @Size(max = 20)
    @Column(name = "estado", length = 20)
    private String estado;

    @Column(name = "fecha_carga")
    private Instant fechaCarga;

    @Size(max = 100)
    @Column(name = "usuario_carga", length = 100)
    private String usuarioCarga;

    @OneToMany(mappedBy = "archivoOrigen")
    private Set<Transaccione> transacciones = new LinkedHashSet<>();

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

    public Long getTamañoBytes() {
        return tamañoBytes;
    }

    public void setTamañoBytes(Long tamañoBytes) {
        this.tamañoBytes = tamañoBytes;
    }

    public Integer getTotalRegistros() {
        return totalRegistros;
    }

    public void setTotalRegistros(Integer totalRegistros) {
        this.totalRegistros = totalRegistros;
    }

    public Integer getRegistrosProcesados() {
        return registrosProcesados;
    }

    public void setRegistrosProcesados(Integer registrosProcesados) {
        this.registrosProcesados = registrosProcesados;
    }

    public Integer getRegistrosConError() {
        return registrosConError;
    }

    public void setRegistrosConError(Integer registrosConError) {
        this.registrosConError = registrosConError;
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

    public Set<Transaccione> getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(Set<Transaccione> transacciones) {
        this.transacciones = transacciones;
    }

}