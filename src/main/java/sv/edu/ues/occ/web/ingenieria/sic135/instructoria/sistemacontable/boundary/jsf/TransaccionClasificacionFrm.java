package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.CuentaContableDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.DAOInterface;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionClasificacionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.CuentaContable;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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
    private TransaccionClasificacionDAO transaccionClasificacionDAO;
    @Inject
    private TransaccionDAO transaccionDAO;
    @Inject
    private CuentaContableDAO cuentaContableDAO;
    @Inject
    private ClasificacionService clasificasionesService;
    private Transaccion transaccionSeleccionada;

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
        if(id == null ) return null;
        try{
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
        TransaccionClasificacion tc = new TransaccionClasificacion();
        tc.setOrigen("MANUAL");
        tc.setTipoTransaccion("");
        tc.setCreatedAt(new Date());
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
            mostrarMensajeError("Error al cargar transacciones: " + e.getMessage());
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
                    PrimeFaces.current().ajax().update("sugerenciasPanel");
                })
                .exceptionally(throwable -> {
                    logger.log(Level.SEVERE, "Error obteniendo sugerencias de IA", throwable);
                    procesadoIA = false;
                    cuentasSugeridas = cuentaContableDAO.findCuentaPrincipales();
                    mostrarMensajeError("Error al obtener sugerencias de IA");
                    return null;
                });
    }

    // Acciones de clasificación
    public void clasificarManual() {
        if (transaccionSeleccionado != null && cuentasSeleccionadas != null) {
            try {
                boolean exito = clasificasionesService.clasificarTransaccionManual(
                        transaccionSeleccionado,
                        cuentasSeleccionadas
                );

                if (exito) {
                    mostrarMensajeExito("Transacción clasificada exitosamente");
                    limpiarSeleccion();
                    cargarTransaccionesPendientes();
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

    // Métodos de utilidad
    private void limpiarSeleccion() {
        transaccionSeleccionado = null;
        cuentasSeleccionadas = null;
        cuentasSugeridas = null;
        trasnsaccionesTotales = 0;
    }

    public List<CuentaContable> completarCuenta(String query) {
        try {
            return cuentaContableDAO.buscarPorNombreOCodigo(query);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en autocompletar cuentas", e);
            return List.of();
        }
    }

    public void onCuentaDebeSelect(SelectEvent<CuentaContable> event) {
        CuentaContable cuenta = event.getObject();
        if (this.registro != null) {
            this.registro.setCuentaContableDebe(cuenta);
            logger.info("Cuenta débito seleccionada: " + cuenta.getNombre());
        }
    }

    public void onCuentaHaberSelect(SelectEvent<CuentaContable> event) {
        CuentaContable cuenta = event.getObject();
        if (this.registro != null) {
            this.registro.setCuentaContableHaber(cuenta);
            logger.info("Cuenta haber seleccionada: " + cuenta.getNombre());
        }
    }

    public void prepararNuevaClasificacion(Transaccion transaccion) {
        this.transaccionSeleccionado = transaccion;
        this.registro = nuevoRegistro();
        this.registro.setTransaccion(transaccion);
        this.estado = ESTADO_CRUD.CREAR;
    }

    public void cargarClasificacionExistente(Transaccion transaccion) {
        this.transaccionSeleccionado = transaccion;
        try {
            List<TransaccionClasificacion> clasificaciones =
                    transaccionClasificacionDAO.findByTransaccion(transaccion);

            if (clasificaciones != null && !clasificaciones.isEmpty()) {
                this.registro = clasificaciones.get(0);
                this.estado = ESTADO_CRUD.MODIFICAR;
            } else {
                prepararNuevaClasificacion(transaccion);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error cargando clasificación existente", e);
            prepararNuevaClasificacion(transaccion);
        }
    }

    // MÉTODO CANCELAR CORREGIDO
    public void cancelar() {
        try {
            // CORRECCIÓN 1: Usar new TransaccionClasificacion() en lugar de new TransaccionClasificacionDAO()
            this.registro = new TransaccionClasificacion();

            // CORRECCIÓN 2: Usar el enum ESTADO_CRUD.NADA en lugar del String "NADA"
            this.estado = ESTADO_CRUD.NADA;

            // Limpiar selecciones
            this.transaccionSeleccionado = null;
            this.cuentasSeleccionadas = null;

            // Mensaje de confirmación
            mostrarMensajeInfo("Operación cancelada - La clasificación ha sido cancelada.");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al cancelar operación", e);
            mostrarMensajeError("No se pudo cancelar la operación: " + e.getMessage());
        }
    }

    // Método adicional para limpiar completamente
    public void limpiarTodo() {
        this.registro = new TransaccionClasificacion();
        this.estado = ESTADO_CRUD.NADA;
        this.transaccionSeleccionado = null;
        this.cuentasSeleccionadas = null;
        this.cuentasSugeridas = null;
        this.filtroDescripcion = null;
        mostrarMensajeInfo("Formulario limpiado completamente");
    }

    private void mostrarMensajeExito(String mensaje) {
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", mensaje));
    }

    private void mostrarMensajeError(String mensaje) {
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", mensaje));
    }

    private void mostrarMensajeAdvertencia(String mensaje) {
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", mensaje));
    }

    private void mostrarMensajeInfo(String mensaje) {
        facesContext.addMessage(null,
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

    public boolean isProcesandoIA() {
        return procesadoIA;
    }

    public int getTransaccionesProcesadas() {
        return transaccionesProcesadas;
    }

    public int getTransaccionesTotales() {
        return trasnsaccionesTotales;
    }
}