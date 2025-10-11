package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.ArchivoCargadoDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.DAOInterface;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionExcelParse;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import static org.primefaces.component.message.MessageBase.PropertyKeys.severity;

@Named
@ViewScoped
public class ArchivoCargadoFrm extends DefaultFrm<ArchivoCargado> implements Serializable {

    @Inject
    FacesContext facesContext;

    @Inject
    ArchivoCargadoDAO archivoCargadoDAO;


    //parseo y reenvio a transaccion
    @Inject
    TransaccionExcelParse parser;

    @Inject
    TransaccionDAO transaccionDAO;



    @Override
    protected FacesContext getFacesContext() {
        return facesContext;
    }

    @Override
    protected DAOInterface<ArchivoCargado, Object> getDao() {
        return archivoCargadoDAO;
    }

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

                // luego cambiar la ruta<-----------------------------------------------------------------------
                String folderPath = System.getProperty("user.home") + File.separator + "archivos_subidos";
                File carpeta = new File(folderPath);
                if (!carpeta.exists()) {
                    carpeta.mkdirs();
                }

                String nombreArchivo = archivo.getSubmittedFileName();
                String rutaCompleta = folderPath + File.separator + nombreArchivo;

                try (FileOutputStream output = new FileOutputStream(rutaCompleta)) {
                    input.transferTo(output);
                }

                // Crear entidad y persistir en BD
                ArchivoCargado nuevo = new ArchivoCargado();
                nuevo.setNombreArchivo(nombreArchivo);
                nuevo.setRutaArchivo(rutaCompleta);
                nuevo.setEstado("PROCESANDO");
                archivoCargadoDAO.create(nuevo);

                //parseado y guardado de transacciones
                List<Transaccion> transacciones = parser.parsearExcel(rutaCompleta, nuevo);
                for (Transaccion t : transacciones) {
                    transaccionDAO.create(t);
                }
                mostrarMensaje("Archivo cargado correctamente: " + nombreArchivo, FacesMessage.SEVERITY_INFO);

                //listaArchivoCargado = archivoCargadoDAO.findRange(0, Integer.MAX_VALUE);
//            } catch (IOException | IllegalAccessException e) {
//                mostrarMensaje("Error al subir el archivo: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
//            }

            }catch (Exception e) {
                mostrarMensaje("Error al subir el archivo: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
            }
        } else {
            mostrarMensaje("Debe seleccionar un archivo antes de subirlo.", FacesMessage.SEVERITY_WARN);
        }
    }

    private void mostrarMensaje(String s, FacesMessage.Severity severity) {
        FacesMessage msj = new FacesMessage();
        msj.setSeverity(severity);
        msj.setSummary(s);
        getFacesContext().addMessage(null, msj);
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

