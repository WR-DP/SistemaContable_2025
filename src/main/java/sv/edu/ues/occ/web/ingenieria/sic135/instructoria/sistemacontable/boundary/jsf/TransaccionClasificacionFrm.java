package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.CuentaContableDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.CuentaContable;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransaccionClasificacionFrm implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger= Logger.getLogger(TransaccionClasificacionFrm.class.getName());

    //Estado del Formulario
    private Transaccion transaccionSeleccionado;
    private List<Transaccion> transaccionesPendientes;
    private List<CuentaContable> cuentasSugeridas;
    private CuentaContable cuentasSeleccionadas;
    private String filtroDescripcion;
    private Boolean soloPendientes= true;
    private boolean procesadoIA=false;
    private int transaccionesProcesadas=0;
    private int trasnsaccionesTotales=0;

    @Inject
    private TransaccionDAO transaccionDAO;

    @Inject
    private CuentaContableDAO cuentaContableDAO;

    @Inject
    private ClasificacionService clasificasionesService;

    @PostConstruct
    public void init(){
        cargarTransaccionesPendientes();
    }

    // Implementacion del manejo de Datos
    public void cargarTransaccionesPendientes(){
        try{
            transaccionesPendientes = transaccionDAO.findTransaccionesPendinetes();
        }catch (Exception e){
            logger.log(Level.SEVERE,"Error cargado Transacciones Pendientes",e);
            mostrarMensajeError("Error al cargar  transacciones: "+e.getMessage());
        }
    }

    public void onTransaccionSelect(SelectEvent<Transaccion> event) {
        transaccionSeleccionado = event.getObject();
        if(transaccionSeleccionado!=null){
            cargarSugerenciasIA();
        }
    }
    private void cargarSugerenciasIA(){
        if(transaccionSeleccionado==null) return;

        procesadoIA=true;

        clasificasionesService.obtenerSugerenciasIA(transaccionSeleccionado)
                .thenAccept(sugerencias ->{
                    cuentasSugeridas = sugerencias;
                    procesadoIA=false;

                    PrimeFaces.current().ajax().update("SUgerenciasPanel");
                })
                .exceptionally(throwable->{
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
        List<Transaccion> pendientes = transaccionDAO.findTransaccionesPendinetes();
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
                            " de " + trasnsaccionesTotales+ " transacciones clasificadas");
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
                // Usa el método DAO que busca solo en pendientes
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
        trasnsaccionesTotales= Integer.parseInt(null);
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
    public Transaccion getTransaccionSeleccionada() { return transaccionSeleccionado; }
    public void setTransaccionSeleccionada(Transaccion transaccionSeleccionada) { this.transaccionSeleccionado = transaccionSeleccionada; }
    public List<Transaccion> getTransaccionesPendientes() { return transaccionesPendientes; }
    public List<CuentaContable> getCuentasSugeridas() { return cuentasSugeridas; }
    public CuentaContable getCuentaSeleccionada() { return cuentasSeleccionadas; }
    public void setCuentaSeleccionada(CuentaContable cuentaSeleccionada) { this.cuentasSeleccionadas = cuentaSeleccionada; }
    public String getFiltroDescripcion() { return filtroDescripcion; }
    public void setFiltroDescripcion(String filtroDescripcion) { this.filtroDescripcion = filtroDescripcion; }

    // Eliminado el getter/setter para soloPendientes

    public boolean isProcesandoIA() { return procesadoIA; }
    public int getTransaccionesProcesadas() { return transaccionesProcesadas; }
    public int getTransaccionesTotales() { return trasnsaccionesTotales; }

}
