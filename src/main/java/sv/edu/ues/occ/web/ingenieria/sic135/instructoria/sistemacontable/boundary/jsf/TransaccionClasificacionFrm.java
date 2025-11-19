package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.el.MethodExpression;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.*;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Categoria;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.CuentaContable;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@ViewScoped
public class TransaccionClasificacionFrm extends DefaultFrm<TransaccionClasificacion> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(TransaccionClasificacionFrm.class.getName());

    //Estado del Formulario
    private Transaccion transaccionSeleccionado;
    private List<Transaccion> transaccionesPendientes;
    private List<CuentaContable> cuentasSugeridas;
    private CuentaContable cuentasSeleccionadas;
    private String filtroDescripcion;
    private Boolean soloPendientes = true;
    private boolean procesadoIA = false;
    private int transaccionesProcesadas = 0;
    private int trasnsaccionesTotales = 0;

    @Inject
    private FacesContext facesContext;
    @Inject
    TransaccionClasificacionDAO transaccionClasificacionDAO;
    @Inject
    TransaccionDAO transaccionDAO;

    @Inject
    private ClasificacionService clasificasionesService;
    private Transaccion transaccionSeleccionada;

    //cuenta contable para poder recomendar la cuenta
    @Inject
    CuentaContableDAO cuentaContableDAO;
    protected UUID idCuentaContableDebe;
    protected UUID idCuentaContableHaber;

    protected Boolean cuentaEsDebe = Boolean.TRUE;


    //par apoder recomendar la categoria
    @Inject
    CategoriaDAO categoriaDAO;
    protected UUID idCategoria;

    protected Categoria categoriaSeleccionada;


    //muestra lista de categorias
    public List<Categoria> getListaCategorias() {
        return categoriaDAO.findAll();
    }


    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected DAOInterface<TransaccionClasificacion, Object> getDao() {
        return transaccionClasificacionDAO;
    }

    @Override
    protected String getIdAsText(TransaccionClasificacion r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected TransaccionClasificacion getIdByText(String id) {
        if (id != null && this.modelo != null) {
            final String buscado = id;
            return this.modelo.getWrappedData().stream()
                    .filter(r -> r.getId().toString().equals(buscado))
                    .findFirst().orElse(null);
        }
        return null;
    }


    @Override
    public DAOInterface<TransaccionClasificacion, Object> getDataAccess() {
        return transaccionClasificacionDAO;
    }

    @Override
    protected TransaccionClasificacion buscarRegistroPorId(Object id) {
        //Buscar por idTransaccionClasificacion
        if(id == null ) return null;
        try{
            //entidad de tipo Long
            return transaccionClasificacionDAO.findById(id);
        }catch (Exception e){
            if(this.modelo != null){
                final String buscado = id.toString();
                return this.modelo.getWrappedData().stream()
                        .filter(r -> r.getId() != null && r.getId().toString().equals(buscado))
                        .findFirst().orElse(null);
            }
        }
        return null;
    }

    @Override
    protected TransaccionClasificacion nuevoRegistro() {
        TransaccionClasificacion  tc = new TransaccionClasificacion();
        //tc.setId( ); //creo que no hace falta setearlo de momento por que es auto generado
        tc.setOrigen("MANUAL");
        tc.setTipoTransaccion("");
        tc.setCreatedAt(Date.from(new Date().toInstant()));
        return tc;
    }

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();


    }

    // Implementacion del manejo de Datos
    public void cargarTransaccionesPendientes() {
        try {
            transaccionesPendientes = transaccionDAO.findTransaccionesPendientes();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error cargado Transacciones Pendientes", e);
            mostrarMensajeError("Error al cargar  transacciones: " + e.getMessage());
        }
    }

    public void onTransaccionSelect(SelectEvent<Transaccion> event) {
        transaccionSeleccionado = event.getObject();
        if (transaccionSeleccionado != null) {
            cargarSugerenciasIA();
        }
    }

    private void cargarSugerenciasIA() {
        if (transaccionSeleccionado == null) return;

        procesadoIA = true;

        clasificasionesService.obtenerSugerenciasIA(transaccionSeleccionado)
                .thenAccept(sugerencias -> {
                    cuentasSugeridas = sugerencias;
                    procesadoIA = false;

                    PrimeFaces.current().ajax().update("SUgerenciasPanel");
                })
                .exceptionally(throwable -> {
                    logger.log(Level.SEVERE, "Error obteniendo sugerencias de IA", throwable);
                    procesadoIA = false;
                    cuentasSugeridas = cuentaContableDAO.findCuentaPrincipales();
                    mostrarMensajeError("Error al obtener sugerencias de IA");
                    return null;
                });
    }

    //Nuveo registro


    // Acciones de clasificación
    public void clasificarManual() {
        if (transaccionSeleccionado != null && cuentasSeleccionadas != null) {
            try {
                // Llama al servicio, que asignará la CuentaContable
                boolean exito = clasificasionesService.clasificarTransaccionManual(
                        transaccionSeleccionado,
                        cuentasSeleccionadas
                );

                if (exito) {
                    mostrarMensajeExito("Transacción clasificada exitosamente");
                    limpiarSeleccion();
                    cargarTransaccionesPendientes(); // Recarga la lista
                } else {
                    mostrarMensajeError("Error al clasificar la transacción");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error en clasificación manual", e);
                mostrarMensajeError("Error al clasificar: " + e.getMessage());
            }
        } else {
            mostrarMensajeAdvertencia("Seleccione una transacción y una cuenta");
        }
    }

    public void clasificarAutomatica() {
        if (transaccionSeleccionado != null) {
            procesadoIA = true;

            clasificasionesService.clasificarTransaccionAutomatica(transaccionSeleccionado)
                    .thenAccept(exito -> {
                        procesadoIA = false;

                        if (exito) {
                            mostrarMensajeExito("Transacción clasificada automáticamente");
                            limpiarSeleccion();
                            cargarTransaccionesPendientes();
                        } else {
                            mostrarMensajeError("No se pudo clasificar automáticamente");
                        }
                    })
                    .exceptionally(throwable -> {
                        procesadoIA = false;
                        logger.log(Level.SEVERE, "Error en clasificación automática", throwable);
                        mostrarMensajeError("Error en clasificación automática: " + throwable.getMessage());
                        return null;
                    });
        }
    }

    public void clasificarLote() {
        List<Transaccion> pendientes = transaccionDAO.findTransaccionesPendientes();
        if (pendientes.isEmpty()) {
            mostrarMensajeInfo("No hay transacciones pendientes para clasificar");
            return;
        }

        trasnsaccionesTotales = pendientes.size();
        transaccionesProcesadas = 0;
        procesadoIA = true;

        clasificasionesService.clasificarLoteAutomatico(pendientes)
                .thenAccept(clasificadas -> {
                    procesadoIA = false;
                    transaccionesProcesadas = clasificadas;

                    mostrarMensajeExito("Proceso completado: " + clasificadas +
                            " de " + trasnsaccionesTotales + " transacciones clasificadas");
                    cargarTransaccionesPendientes();
                })
                .exceptionally(throwable -> {
                    procesadoIA = false;
                    logger.log(Level.SEVERE, "Error en clasificación por lote", throwable);
                    mostrarMensajeError("Error en clasificación por lote: " + throwable.getMessage());
                    return null;
                });
    }

    public void buscarTransacciones() {
        try {
            if (filtroDescripcion != null && !filtroDescripcion.trim().isEmpty()) {

                transaccionesPendientes = transaccionDAO.finndByDescripcion(filtroDescripcion);
            } else {
                cargarTransaccionesPendientes();
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error buscando transacciones", e);
            mostrarMensajeError("Error en búsqueda: " + e.getMessage());
        }
    }

    public void limpiarFiltros() {
        filtroDescripcion = null;
        cargarTransaccionesPendientes();
    }

    // Métodos de utilidad (se mantienen igual)
    private void limpiarSeleccion() {
        trasnsaccionesTotales = 0;
        cuentasSeleccionadas = null;
        cuentasSugeridas = null;
    }

    private void mostrarMensajeExito(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", mensaje));
    }

    private void mostrarMensajeError(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }

    private void mostrarMensajeAdvertencia(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", mensaje));
    }

    private void mostrarMensajeInfo(String mensaje) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Información", mensaje));
    }

    // Getters y Setters
    public Transaccion getTransaccionSeleccionada() {
        return transaccionSeleccionado;
    }

    public void setTransaccionSeleccionada(Transaccion transaccionSeleccionada) {
        this.transaccionSeleccionada = transaccionSeleccionada;
    }

    public List<Transaccion> getTransaccionesPendientes() {
        return transaccionesPendientes;
    }

    public List<CuentaContable> getCuentasSugeridas() {
        return cuentasSugeridas;
    }

    public CuentaContable getCuentaSeleccionada() {
        return cuentasSeleccionadas;
    }

    public void setCuentaSeleccionada(CuentaContable cuentaSeleccionada) {
        this.cuentasSeleccionadas = cuentaSeleccionada;
    }

    public String getFiltroDescripcion() {
        return filtroDescripcion;
    }

    public void setFiltroDescripcion(String filtroDescripcion) {
        this.filtroDescripcion = filtroDescripcion;
    }

    // Eliminado el getter/setter para soloPendientes

    public boolean isProcesandoIA() {
        return procesadoIA;
    }

    public int getTransaccionesProcesadas() {
        return transaccionesProcesadas;
    }

    public int getTransaccionesTotales() {
        return trasnsaccionesTotales;
    }


    //getter y setter para recomendaciones


    public UUID getIdCuentaContableDebe() {
        return idCuentaContableDebe;
    }

    public void setIdCuentaContableDebe(UUID idCuentaContableDebe) {
        this.idCuentaContableDebe = idCuentaContableDebe;
    }

    public UUID getIdCuentaContableHaber() {
        return idCuentaContableHaber;
    }

    public void setIdCuentaContableHaber(UUID idCuentaContableHaber) {
        this.idCuentaContableHaber = idCuentaContableHaber;
    }

    public UUID getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(UUID idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Categoria getCategoriaSeleccionada() {
        return categoriaSeleccionada;
    }

    public void setCategoriaSeleccionada(Categoria categoriaSeleccionada) {
        this.categoriaSeleccionada = categoriaSeleccionada;
    }

    public List<Categoria> completarCategoria(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                // DefaultDataAcces normalmente provee findAll()
                return categoriaDAO.findAll();
            }
            return categoriaDAO.findByNombreLike(query);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error completando categorias: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }


    //completarCuenta
    public List<CuentaContable> completarCuenta(String query) {
        try {
            if (query == null || query.trim().isEmpty()) {
                return cuentaContableDAO.findCuentaPrincipales();
            }
            return cuentaContableDAO.findByNombreLike(query);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error completando cuentas: " + e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public Boolean getCuentaEsDebe() {
        return cuentaEsDebe;
    }

    public void setCuentaEsDebe(Boolean cuentaEsDebe) {
        this.cuentaEsDebe = cuentaEsDebe;
    }


}
