package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.DAOInterface;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionExcelParse;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Named
@ViewScoped
public class TransaccionFrm extends DefaultFrm<Transaccion> implements Serializable {

    @Inject
    FacesContext facesContext;
    @Inject
    TransaccionDAO transaccionDAO;

    //injectar el parser para enviarle los datos
    @Inject
    TransaccionExcelParse parser;

    private List<Transaccion> listaTransacciones;

    // referencia al archivo cargado para enlazar con las transacciones
    private ArchivoCargado archivoSeleccionado;

    private Transaccion transaccionSeleccionado;

    // Campos para filtrado por periodo contable
    private String periodoFiltro; // "mensual"|"trimestral"|"anual"
    private Integer anioFiltro;
    private Integer mesFiltro;
    private Integer trimestreFiltro;

    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected DAOInterface<Transaccion, Object> getDao() {
        return transaccionDAO;
    }

    @PostConstruct
    @Override
    public void inicializar() {
        // DefaultFrm.inicializar no lanza checked exceptions
        super.inicializar();
        try {
            listaTransacciones = transaccionDAO.findRange(0, Integer.MAX_VALUE);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.WARNING, "No se pudo cargar la lista inicial de transacciones", ex);
            listaTransacciones = java.util.Collections.emptyList();
        }
    }

    @Override
    protected Transaccion nuevoRegistro() {
        Transaccion t = new Transaccion();
        t.setIdTransaccion(UUID.randomUUID());
        t.setFecha(new Date());
        t.setMonto(BigDecimal.ZERO);
        t.setDescripcion("");
        t.setMoneda("USD");
        t.setCreatedAt(new Date());
        return t;
    }

    @Override
    protected String getIdAsText(Transaccion r) {
        if (r != null && r.getIdTransaccion() != null) {
            return r.getIdTransaccion().toString();
        }
        return null;
    }

    @Override
    protected Transaccion getIdByText(String id) {
        if (id != null && this.modelo != null) {
            final String buscado = id;
            return this.modelo.getWrappedData().stream()
                    .filter(r -> r.getIdTransaccion() != null && r.getIdTransaccion().toString().equals(buscado))
                    .findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public DAOInterface<Transaccion, Object> getDataAccess() {
        return transaccionDAO;
    }

    @Override
    protected Transaccion buscarRegistroPorId(Object id) {
        // Buscar por idTransaccion (objeto UUID o String)
        if (id == null) return null;
        try {
            return transaccionDAO.findById(id);
        } catch (Exception ex) {
            // intentar comparar como String en el modelo lazy
            if (this.modelo != null) {
                final String buscado = id.toString();
                return this.modelo.getWrappedData().stream()
                        .filter(r -> r.getIdTransaccion() != null && r.getIdTransaccion().toString().equals(buscado))
                        .findFirst().orElse(null);
            }
            return null;
        }
    }

    public void cargarTransaccionesPorArchivo(ArchivoCargado archivo) {
        if (archivo == null || archivo.getRutaArchivo() == null) {
            enviarMensaje("No se encontró la ruta del archivo.", FacesMessage.SEVERITY_ERROR);
            return;
        }
        try {
            List<Transaccion> transacciones = parser.parsearExcel(archivo.getRutaArchivo(), archivo);
            for (Transaccion t : transacciones) {
                transaccionDAO.create(t);
            }
            enviarMensaje("Transacciones importadas correctamente.", FacesMessage.SEVERITY_INFO);
            // recargar lista de transacciones del archivo
            listaTransacciones = transaccionDAO.findByArchivoId(archivo.getIdArchivoCargado());
        } catch (Exception e) {
            enviarMensaje("Error al importar transacciones: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void guardarTransaccion(){
        if(transaccionSeleccionado != null){
            transaccionSeleccionado.setUpdatedAt(new Date());
            transaccionDAO.edit(transaccionSeleccionado);
            enviarMensaje("Transaccion editada correctamente.", FacesMessage.SEVERITY_INFO);
        } else {
            enviarMensaje("No hay transaccion seleccionada para editar.", FacesMessage.SEVERITY_WARN);
        }
    }

    public void crearTransaccionManual() {
        Transaccion nueva = nuevoRegistro();
        nueva.setArchivoCargadoId(archivoSeleccionado);
        transaccionDAO.create(nueva);
        listaTransacciones.add(nueva);
        enviarMensaje("Transaccion creada correctamente.", FacesMessage.SEVERITY_INFO);
    }

    /**
     * Aplica el filtro por periodo usando el archivo seleccionado como contexto.
     * Si no hay archivo seleccionado devuelve todas las transacciones (comportamiento actual).
     */
    public void aplicarFiltroPeriodo() {
        if (archivoSeleccionado == null || archivoSeleccionado.getIdArchivoCargado() == null) {
            // Sin archivo, cargar todas o avisar
            listaTransacciones = transaccionDAO.findRange(0, Integer.MAX_VALUE);
            enviarMensaje("No hay archivo seleccionado; mostrando todas las transacciones", FacesMessage.SEVERITY_WARN);
            return;
        }
        Object archivoId = archivoSeleccionado.getIdArchivoCargado();
        listaTransacciones = transaccionDAO.findByArchivoIdAndPeriodo(archivoId, periodoFiltro, anioFiltro, mesFiltro, trimestreFiltro);
    }

    // Permite cargar filtro directamente por parámetros (útil desde otras vistas)
    public void aplicarFiltroPeriodoParaArchivoId(Object archivoId) {
        listaTransacciones = transaccionDAO.findByArchivoIdAndPeriodo(archivoId, periodoFiltro, anioFiltro, mesFiltro, trimestreFiltro);
    }

    //getters and setters
    public TransaccionExcelParse getParser() {return parser;}

    public void setParser(TransaccionExcelParse parser) {this.parser = parser;}

    public void setFacesContext(FacesContext facesContext) {this.facesContext = facesContext;}

    public TransaccionDAO getTransaccionDAO() {return transaccionDAO;}

    public void setTransaccionDAO(TransaccionDAO transaccionDAO) {this.transaccionDAO = transaccionDAO;}

    public List<Transaccion> getListaTransacciones() {return listaTransacciones;}

    public void setListaTransacciones(List<Transaccion> listaTransacciones) {this.listaTransacciones = listaTransacciones;}

    public ArchivoCargado getArchivoSeleccionado() {return archivoSeleccionado;}

    public void setArchivoSeleccionado(ArchivoCargado archivoSeleccionado) {this.archivoSeleccionado = archivoSeleccionado;}

    public Transaccion getTransaccionSeleccionado() {return transaccionSeleccionado;}

    public void setTransaccionSeleccionado(Transaccion transaccionSeleccionado) {this.transaccionSeleccionado = transaccionSeleccionado;}

    // Getters/Setters para filtros
    public String getPeriodoFiltro() { return periodoFiltro; }
    public void setPeriodoFiltro(String periodoFiltro) { this.periodoFiltro = periodoFiltro; }
    public Integer getAnioFiltro() { return anioFiltro; }
    public void setAnioFiltro(Integer anioFiltro) { this.anioFiltro = anioFiltro; }
    public Integer getMesFiltro() { return mesFiltro; }
    public void setMesFiltro(Integer mesFiltro) { this.mesFiltro = mesFiltro; }
    public Integer getTrimestreFiltro() { return trimestreFiltro; }
    public void setTrimestreFiltro(Integer trimestreFiltro) { this.trimestreFiltro = trimestreFiltro; }

}
