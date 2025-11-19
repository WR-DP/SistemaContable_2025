package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.DAOInterface;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class DefaultFrm<T> implements Serializable {
    ESTADO_CRUD estado = ESTADO_CRUD.NADA;

    protected String nombreBean;
    protected abstract FacesContext getFacesContext();
    protected abstract DAOInterface<T, Object> getDao();
    protected LazyDataModel<T> modelo;
    protected T registro;
    protected int pageSize = 5;
    protected abstract String getIdAsText(T r);
    protected abstract T getIdByText(String id);
    protected abstract T nuevoRegistro();
    public abstract DAOInterface<T, Object> getDataAccess();
    protected abstract T buscarRegistroPorId(Object id);

    @PostConstruct
    public void inicializar() {
        inicializarRegistros();
        inicializarListas();
    }

    private void inicializarListas() {
    }

    protected void inicializarRegistros() {
        this.modelo = new LazyDataModel<T>() {

            @Override
            public int count(Map<String, FilterMeta> map) {
                try {
                    return getDao().count();
                } catch (Exception ex) {
                    Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, null, ex);
                }
                return 0;
            }

            @Override
            public List<T> load(int first, int max, Map<String, SortMeta> map, Map<String, FilterMeta> map1) {
                try {
                    return getDao().findRange(first, max);
                } catch (Exception ex) {
                    Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, null, ex);
                }
                return Collections.emptyList();
            }

            @Override
            public String getRowKey(T object) {
                if (object != null) {
                    ;
                    try {
                        return getIdAsText(object);
                    } catch (Exception e) {
                        Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
                return null;
            }

            @Override
            public T getRowData(String rowKey) {
                if (rowKey != null) {
                    try {
                        return getIdByText(rowKey);
                    } catch (Exception e) {
                        Logger.getLogger(DefaultFrm.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
                return null;
            }
        };
    }

    public void btnNuevoHandler(ActionEvent actionEvent) {
        this.registro = nuevoRegistro();
        this.estado = ESTADO_CRUD.CREAR;
    }

    public void btnCancelarHandler(ActionEvent actionEvent) {
        this.registro = null;
        this.estado = ESTADO_CRUD.NADA;
    }

    public void btnGuardarHandler(ActionEvent actionEvent) {
        try {
            if (registro != null) {
                getDao().create(registro);
                this.enviarMensaje("Registro creado", FacesMessage.SEVERITY_INFO);
                this.estado = ESTADO_CRUD.NADA;
                this.registro = null;
                inicializarRegistros();
                return;
            }
        } catch (Exception ex) {
            enviarMensaje("Error al crear el registro: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
            return;
        }
        enviarMensaje("El registro a almacenar no puede ser nulo", FacesMessage.SEVERITY_WARN);
        this.estado = ESTADO_CRUD.NADA;
    }

    public void btnSeleccionarHandler(T registro) {
        if (registro == null) {
            enviarMensaje("No se recibio el id del registro", FacesMessage.SEVERITY_ERROR);
            this.estado = ESTADO_CRUD.NADA;
            return;
        }
        this.registro = buscarRegistroPorId(registro);
        this.estado = ESTADO_CRUD.MODIFICAR;
    }

    public void btnModificarHandler(ActionEvent actionEvent) {
        if (this.registro == null) {
            this.enviarMensaje("No hay registro seleccionado", FacesMessage.SEVERITY_ERROR);
            return;
        }
        try {
            //hacer para modify en vez de update
            this.getDao().update(this.registro);
            enviarMensaje("Registro modificado", FacesMessage.SEVERITY_INFO);
            this.inicializarRegistros();
            this.estado = ESTADO_CRUD.NADA;
            this.registro = null;
        } catch (Exception ex) {
            enviarMensaje("Error al modificar el registro: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
            return;
        }
    }

    public void btnEliminarHandler(ActionEvent actionEvent) {
        if (this.registro == null) {
            this.enviarMensaje("No hay registros seleccionados", FacesMessage.SEVERITY_ERROR);
            return;
        }
        try {
            this.getDao().delete(this.registro);
            enviarMensaje("Registro eliminado", FacesMessage.SEVERITY_INFO);
            this.inicializarRegistros();
            this.estado = ESTADO_CRUD.NADA;
            this.registro = null;
        } catch (Exception ex) {
            enviarMensaje("Error al eliminar el registro: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void selectionHandler(SelectEvent<T> r) {
        if (r != null) {
            this.estado = ESTADO_CRUD.MODIFICAR;
        }
    }

    public void seleccionarRegistro(SelectEvent<T> r) {
        if(r!=null && r.getObject() != null){
            this.registro = r.getObject();
            this.estado = ESTADO_CRUD.MODIFICAR;
        } else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Advertencia", "No se selecciono ningun registro"));
        }
    }

    public void enviarMensaje(String s, FacesMessage.Severity severity) {
        FacesMessage msj = new FacesMessage();
        msj.setSeverity(severity);
        msj.setSummary(s);
        getFacesContext().addMessage(null, msj);
    }

    public ESTADO_CRUD getEstado() {
        return estado;
    }
    public void setEstado(ESTADO_CRUD estado) {
        this.estado = estado;
    }
    public String getNombreBean() {
        return nombreBean;
    }
    public void setNombreBean(String nombreBean) {
        this.nombreBean = nombreBean;
    }
    public T getRegistro() {
        return registro;
    }
    public void setRegistro(T registro) {
        this.registro = registro;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public LazyDataModel<T> getModelo() {
        return modelo;
    }
    public void setModelo(LazyDataModel<T> modelo) {
        this.modelo = modelo;
    }
    public boolean isEnModoEdicion() {
        return this.estado != ESTADO_CRUD.NADA;
    }



}
