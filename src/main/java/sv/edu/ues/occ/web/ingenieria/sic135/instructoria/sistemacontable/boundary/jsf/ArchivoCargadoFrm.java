package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;


import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.file.UploadedFile;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.ArchivoCargadoDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.DAOInterface;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionExcelParse;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf.ESTADO_CRUD.NADA;

@Named
@ViewScoped
public class ArchivoCargadoFrm extends DefaultFrm<ArchivoCargado> implements Serializable {

    @Inject
    FacesContext facesContext;
    @Inject
    ArchivoCargadoDAO archivoCargadoDAO;
    @Inject
    TransaccionExcelParse parser;
    @Inject
    TransaccionDAO transaccionDAO;

    private UploadedFile archivo; // tipo correcto para PrimeFaces

    private static final Logger LOGGER = Logger.getLogger(ArchivoCargadoFrm.class.getName());

    @PostConstruct
    @Override
    public void inicializar() {
        super.inicializar();
    }

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
        if (r != null && r.getIdArchivoCargado() != null) {
            return r.getIdArchivoCargado().toString();
        }
        return null;
    }

    @Override
    protected ArchivoCargado nuevoRegistro() {
        ArchivoCargado nuevo = new ArchivoCargado();
        nuevo.setId(UUID.randomUUID());
        nuevo.setNombreArchivo("");
        nuevo.setEstado("SIN PROCESAR");
        nuevo.setFechaCarga(Instant.now());
        nuevo.setUsuarioCarga("admin");
        nuevo.setRegistroProcesado(0);
        nuevo.setRegistroConError(0);
        nuevo.setTotalRegistro(0);
        return nuevo;
    }


//    public void subirArchivo() {
//        System.out.println("=== Ejecutando subirArchivo()");
//        System.out.println("=== Archivo recibido: " + (archivo == null ? "null" : archivo.getFileName()));
//
//        if (archivo != null) {
//            try (InputStream input = archivo.getInputStream()) {
//
//                String folderPath = System.getProperty("user.home") + File.separator + "archivos_subidos";
//                File carpeta = new File(folderPath);
//                if (!carpeta.exists()) carpeta.mkdirs();
//
//                String nombreArchivo = archivo.getFileName();
//                String rutaCompleta = folderPath + File.separator + nombreArchivo;
//
//                // Guardar archivo físico
//                try (FileOutputStream output = new FileOutputStream(rutaCompleta)) {
//                    input.transferTo(output);
//                }
//
//                // Crear registro del archivo cargado
//                ArchivoCargado nuevo = new ArchivoCargado();
//                nuevo.setId(UUID.randomUUID());
//                nuevo.setFechaCarga(Instant.now());
//                nuevo.setNombreArchivo(nombreArchivo);
//                nuevo.setRutaArchivo(rutaCompleta);
//                nuevo.setTamañoByte(archivo.getSize());
//                nuevo.setEstado("CARGADO");
//                nuevo.setUsuarioCarga("admin");
//
//                archivoCargadoDAO.create(nuevo);
//                // Obtener entidad  desde BD
//                ArchivoCargado archivManaged = archivoCargadoDAO.findById(nuevo.getId());
//                // Procesar transacciones desde Excel
//                try {
//                    List<Transaccion> transacciones = parser.parsearExcel(rutaCompleta, archivManaged);
//                    for (Transaccion t : transacciones) {
//                        t.setArchivoCargado(archivManaged);
//                        transaccionDAO.create(t);
//                    }
//                    archivManaged.setTotalRegistro(transacciones.size());
//                    archivoCargadoDAO.editArchivo(archivManaged);
//                } catch (Exception ex) {
//                    Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error al parsear Excel", ex);
//                }
//
//                mostrarMensaje("Archivo cargado correctamente: " + nombreArchivo, FacesMessage.SEVERITY_INFO);
//
//            } catch (Exception e) {
//                Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error al subir archivo", e);
//                mostrarMensaje("Error al subir el archivo: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
//            }
//        } else {
//            mostrarMensaje("Debe seleccionar un archivo antes de subirlo.", FacesMessage.SEVERITY_WARN);
//        }
//    }

    public void subirArchivo() {
        try {
            System.out.println("=== Ejecutando subirArchivo()");
            System.out.println("=== Archivo recibido: " + (archivo == null ? "null" : archivo.getFileName()));

            if (archivo == null || archivo.getContent() == null) {
                mostrarMensaje("Debe seleccionar un archivo antes de subirlo.", FacesMessage.SEVERITY_WARN);
                return;
            }

            // 1. PRIMERO guardar el archivo físicamente
            String folderPath = System.getProperty("user.home") + File.separator + "archivos_subidos";
            File carpeta = new File(folderPath);
            if (!carpeta.exists()) carpeta.mkdirs();

            String nombreArchivo = archivo.getFileName();
            String rutaCompleta = folderPath + File.separator + nombreArchivo;

            try (InputStream input = archivo.getInputStream();
                 FileOutputStream output = new FileOutputStream(rutaCompleta)) {
                input.transferTo(output);
            }

            // 2. Configurar el registro con la ruta del archivo
            registro.setRutaArchivo(rutaCompleta);
            registro.setTamañoByte(archivo.getSize());
            registro.setNombreArchivo(nombreArchivo);
            registro.setEstado("CARGADO");

            // 3. CREAR el registro en la base de datos
            archivoCargadoDAO.create(registro);

            // 4. Parsear el Excel (usará la versión corregida que solo salta 1 fila)
            List<Transaccion> transacciones = parser.parsearExcel(rutaCompleta, registro);

            // 5. Guardar las transacciones
            for (Transaccion t : transacciones) {
                t.setArchivoCargado(registro);
                transaccionDAO.create(t);
            }

            // 6. Actualizar el archivo con los totales
            registro.setTotalRegistro(transacciones.size());
            registro.setEstado("PROCESADO");
            archivoCargadoDAO.editArchivo(registro);

            mostrarMensaje("Archivo subido y procesado correctamente. " + transacciones.size() + " transacciones importadas.", FacesMessage.SEVERITY_INFO);
            estado = NADA;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al subir archivo", e);
            mostrarMensaje("Error al subir el archivo: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);

            // Opcional: Marcar como error si falla
            if (registro != null) {
                registro.setEstado("ERROR");
                try {
                    archivoCargadoDAO.editArchivo(registro);
                } catch (Exception ex) {
                    LOGGER.log(Level.SEVERE, "Error al actualizar estado", ex);
                }
            }
        }
    }

    private void mostrarMensaje(String s, FacesMessage.Severity severity) {
        FacesMessage msj = new FacesMessage(severity, s, null);
        getFacesContext().addMessage(null, msj);
    }

    public UploadedFile getArchivo() {
        return archivo;
    }

    public void setArchivo(UploadedFile archivo) {
        this.archivo = archivo;
    }

    @Override
    protected ArchivoCargado getIdByText(String id) {
        if (id != null && this.modelo != null) {
            return this.modelo.getWrappedData().stream()
                    .filter(r -> r.getIdArchivoCargado() != null && r.getIdArchivoCargado().toString().equals(id))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Override
    public DAOInterface<ArchivoCargado, Object> getDataAccess() {
        return archivoCargadoDAO;
    }

    @Override
    protected ArchivoCargado buscarRegistroPorId(Object id) {
        if (id instanceof String buscado && this.modelo != null) {
            return this.modelo.getWrappedData().stream()
                    .filter(r -> r.getIdArchivoCargado().toString().equals(buscado))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    @Inject
    DeleteManagerContable deleteManagerContable;

    @Override
    public void btnEliminarHandler(ActionEvent event) {

        if (this.registro == null) {
            enviarMensaje("No hay archivo seleccionado", FacesMessage.SEVERITY_ERROR);
            return;
        }

        UUID id = this.registro.getIdArchivoCargado();

        int totalTrans = deleteManagerContable.contarTransaccionesDeArchivo(id);
        int totalClasif = deleteManagerContable.contarClasificacionesDeArchivo(id);

        if (totalTrans > 0) {
            enviarMensaje(
                    "Este archivo tiene " + totalTrans + " transacciones y " +
                            totalClasif + " clasificaciones asociadas.",
                    FacesMessage.SEVERITY_WARN
            );
        }

        try {

            deleteManagerContable.eliminarArchivoEnCascada(id);

            enviarMensaje("Archivo eliminado correctamente.", FacesMessage.SEVERITY_INFO);

            inicializarRegistros();
            this.estado = ESTADO_CRUD.NADA;
            this.registro = null;

        } catch (Exception ex) {
            enviarMensaje("Error al eliminar: " + ex.getMessage(), FacesMessage.SEVERITY_ERROR);
        }
    }

    public void abrirDetalleDesdeTabla(ArchivoCargado seleccionado) {
        this.registro = seleccionado;
        this.seleccion = null; // <-- LIMPIA SELECCIÓN
        this.estado = ESTADO_CRUD.MODIFICAR;
    }

}
