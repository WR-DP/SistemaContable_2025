package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "cliente", schema = "public")
public class Cliente {
    @Id
    @Column(name = "id_cliente", nullable = false)
    private UUID id;

    @Size(max = 10)
    @Column(name = "tipo_cliente", length = 10)
    private String tipoCliente;

    @Size(max = 200)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 200)
    private String nombre;

    @Size(max = 20)
    @Column(name = "nit_ci", length = 20)
    private String nitCi;

    @Size(max = 15)
    @Column(name = "telefono", length = 15)
    private String telefono;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Lob
    @Column(name = "direccion")
    private String direccion;

    @Column(name = "credito_autorizado")
    private Boolean creditoAutorizado;

    @Column(name = "limite_credito", precision = 15, scale = 2)
    private BigDecimal limiteCredito;

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

    public String getTipoCliente() {
        return tipoCliente;
    }

    public void setTipoCliente(String tipoCliente) {
        this.tipoCliente = tipoCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNitCi() {
        return nitCi;
    }

    public void setNitCi(String nitCi) {
        this.nitCi = nitCi;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Boolean getCreditoAutorizado() {
        return creditoAutorizado;
    }

    public void setCreditoAutorizado(Boolean creditoAutorizado) {
        this.creditoAutorizado = creditoAutorizado;
    }

    public BigDecimal getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(BigDecimal limiteCredito) {
        this.limiteCredito = limiteCredito;
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