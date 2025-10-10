package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.ArchivoCargadoDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.DAOInterface;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Named
@ViewScoped
public class ArchivoCargadoFrm extends DefaultFrm<ArchivoCargado> implements Serializable {

    @Inject
    FacesContext facesContext;

    @Inject
    ArchivoCargadoDAO archivoCargadoDAO;


    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected DAOInterface<ArchivoCargado, Object> getDao() {
        return archivoCargadoDAO;
    }

    //probar la funcionalidad del retorno del UUID y su compatibilidad con los formatos<-----------------------------
    @Override
    protected String getIdAsText(ArchivoCargado r) {
        if(r != null && r.getIdArchivoCargado() != null){
            return r.getIdArchivoCargado().toString();
        }
        return null;
    }

    @Override
    protected ArchivoCargado getIdByText(String id) {
        if (id != null) {
            try {
                String buscado = id;
                return this.modelo.getWrappedData().stream().filter(r -> r.getIdArchivoCargado().toString().equals(buscado)).findFirst().orElse(null);
            } catch (NumberFormatException e) {
                Logger.getLogger(ArchivoCargado.class.getName()).log(java.util.logging.Level.SEVERE, null, e);
            }
        }
        return null;
    }

    List<ArchivoCargado> listaArchivoCargado;
    public ArchivoCargadoFrm() {}

    private Part archivo;
    private String rutaGuardado;

//arreglar la excepcion<------------------------------------------------------
    @PostConstruct
    @Override
    public void inicializar() throws IllegalAccessException {
        super.inicializar();
        listaArchivoCargado = archivoCargadoDAO.findRange(0, Integer.MAX_VALUE);
    }

    //aca debe ir una inicializacion para los archivos cargados<--------------
    @Override
    protected ArchivoCargado nuevoRegistro() {
        ArchivoCargado archivoCargado = new ArchivoCargado();
        archivoCargado.setNombreArchivo("");
        //ruta,estado
        return archivoCargado;
    }

    @Override
    public DAOInterface<ArchivoCargado, Object> getDataAccess() {
        return archivoCargadoDAO;
    }


    //verificar que el filtro funcione correctamente, teniendo en cuenta que es String<------------------------------
    @Override
    protected ArchivoCargado buscarRegistroPorId(Object id) {
        if(id != null&& id instanceof String buscado && this.modelo != null){
            return this.modelo.getWrappedData().stream().filter(r -> r.getIdArchivoCargado().equals(buscado)).findFirst().orElse(null);
        }
        return null;
    }

    protected String nombreBean = "Carga de Archivos";
    public String getNombreBean() {
        return nombreBean;
    }
    
    public void subirArchivo() {
        if (archivo != null) {
            try (InputStream input = archivo.getInputStream()) {

                // Carpeta destino dentro del servidor -> buscar otra ruta
                String folderPath = System.getProperty("user.home") + File.separator + "archivos_subidos";
                File carpeta = new File(folderPath);
                if (!carpeta.exists()) {
                    carpeta.mkdirs();
                }

                // Nombre original y ruta completa
                String nombreArchivo = archivo.getSubmittedFileName();
                String rutaCompleta = folderPath + File.separator + nombreArchivo;

                // Guardar el archivo físicamente
                try (FileOutputStream output = new FileOutputStream(rutaCompleta)) {
                    input.transferTo(output);
                }

                // Crear entidad y persistir en BD
                ArchivoCargado nuevo = new ArchivoCargado();
                nuevo.setNombreArchivo(nombreArchivo);
                nuevo.setRutaArchivo(rutaCompleta);
                nuevo.setEstado("CARGADO"); // o según tu enum

                archivoCargadoDAO.create(nuevo);

                this.rutaGuardado = rutaCompleta;
                mostrarMensajeInfo("Archivo cargado correctamente: " + nombreArchivo);

                // actualizar lista--> darle el manejo correcto a al IllegalAccessException
                listaArchivoCargado = archivoCargadoDAO.findRange(0, Integer.MAX_VALUE);

                //este IllegalAccessException
            } catch (IOException | IllegalAccessException e) {
                mostrarMensajeError("Error al subir el archivo: " + e.getMessage());
            }
        } else {
            mostrarMensajeAdvertencia("Debe seleccionar un archivo antes de subirlo.");
        }
    }

    //completar los metodos de los mensajes<------------------------------------------------
    private void mostrarMensajeAdvertencia(String s) {
    }

    private void mostrarMensajeInfo(String s) {
    }

    private void mostrarMensajeError(String s) {
    }


    public Part getArchivo() {
        return archivo;
    }
    public void setArchivo(Part archivo) {
        this.archivo = archivo;
    }
    public String getRutaGuardado() {
        return rutaGuardado;
    }


}

