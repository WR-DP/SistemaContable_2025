package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.event.SelectEvent;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.DAOInterface;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionClasificacionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionExcelParse;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Named("transaccionFrm")
@ViewScoped
@SuppressWarnings("unused")
public class TransaccionFrm extends DefaultFrm<Transaccion> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private FacesContext facesContext;
    @Inject
    private TransaccionDAO transaccionDAO;

    //injectar el parser para enviarle los datos
    @Inject
    private TransaccionExcelParse parser;

    //traer los metodos que corresponden a la clasificacion
    @Inject
    private TransaccionClasificacionDAO transaccionClasificacionDAO;

    private List<Transaccion> listaTransacciones;

    // referencia al archivo cargado para enlazar con las transacciones
    private ArchivoCargado archivoSeleccionado;

    private Transaccion transaccionSeleccionado;

    // Campos para filtrado por periodo contable
    private String periodoFiltro; // "mensual"|"trimestral"|"anual"
    private Integer anioFiltro;
    private Integer mesFiltro;
    private Integer trimestreFiltro;

    // ----- Nuevos campos para facturación -----
    private String tipoFacturacion; // "VENTA" o "COMPRA"
    private List<Transaccion> listaParaFacturar;
    private Map<String, BigDecimal> totalesPorMoneda; // moneda -> total
    private BigDecimal totalFactura; // suma cuando se usa una sola moneda
    private String invoiceJson; // factura digital en formato JSON (simple)
    // Rutas relativas de los archivos generados (p. ej. "fctr/factura-123.json")
    private String jsonFilePath;
    private String pdfFilePath;
     // Mapa para selección de transacciones en la vista (clave: idTransaccion as String)
     private Map<String, Boolean> seleccionMap = new HashMap<>();

    // Carpeta relativa dentro del webapp donde se guardarán los archivos generados
    private static final String FCTR_FOLDER = "fctr";

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
            int total = 0;
            try { total = transaccionDAO.count(); } catch (Exception e) { total = 0; }
            int max = total > 0 ? total : 1000;
            listaTransacciones = transaccionDAO.findRange(0, max);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(getClass().getName()).log(java.util.logging.Level.WARNING, "No se pudo cargar la lista inicial de transacciones", ex);
            listaTransacciones = java.util.Collections.emptyList();
        }

        // Si la servlet puso datos de factura en la sesión (PRG), recupéralos y limpialos
        try {
            FacesContext fc = FacesContext.getCurrentInstance();
            if (fc != null) {
                var sessionMap = fc.getExternalContext().getSessionMap();
                Object inv = sessionMap.get("invoiceJson");
                if (inv != null) {
                    this.invoiceJson = inv.toString();
                    sessionMap.remove("invoiceJson");
                }
                // recuperar rutas de archivos si la servlet/acción las dejó en sesión
                Object jPath = sessionMap.get("jsonFilePath");
                if (jPath != null) {
                    this.jsonFilePath = jPath.toString();
                    sessionMap.remove("jsonFilePath");
                }
                Object pPath = sessionMap.get("pdfFilePath");
                if (pPath != null) {
                    this.pdfFilePath = pPath.toString();
                    sessionMap.remove("pdfFilePath");
                }
                Object cnt = sessionMap.get("factCount");
                if (cnt != null) {
                    // no almacenamos factCount en bean separado por ahora
                    sessionMap.remove("factCount");
                }
                Object tpm = sessionMap.get("totalesPorMoneda");
                if (tpm != null && tpm instanceof java.util.Map) {
                    this.totalesPorMoneda = (Map<String, BigDecimal>) tpm;
                    sessionMap.remove("totalesPorMoneda");
                }
                Object listaAttr = sessionMap.get("listaParaFacturar");
                if (listaAttr != null && listaAttr instanceof java.util.List) {
                    this.listaParaFacturar = (List<Transaccion>) listaAttr;
                    sessionMap.remove("listaParaFacturar");
                }
                // mensajes opcionales
                Object msg = sessionMap.get("factMessage");
                if (msg != null) {
                    fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, msg.toString(), null));
                    sessionMap.remove("factMessage");
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected Transaccion nuevoRegistro() {
        Transaccion t = new Transaccion();
        t.setId(UUID.randomUUID());
        t.setFecha(new Date());
        t.setMonto(BigDecimal.ZERO);
        t.setDescripcion("");
        t.setMoneda("USD");
        t.setCreatedAt(new Date());
        return t;
    }

    @Override
    protected String getIdAsText(Transaccion r) {
        if (r != null && r.getId() != null) {
            return r.getId().toString();
        }
        return null;
    }

    @Override
    protected Transaccion getIdByText(String id) {
        if (id != null && this.modelo != null) {
            final String buscado = id;
            return this.modelo.getWrappedData().stream()
                    .filter(r -> r.getId() != null && r.getId().toString().equals(buscado))
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
                        .filter(r -> r.getId() != null && r.getId().toString().equals(buscado))
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

    /*
    ->verificar balance contable-> debe de verificar el valance general los corriente, no corriente de activo, pasivo, patrimonio segun las cuentas que se utilizan-> dentro de la vista de la informacion de archivo  para ver si todo cuadra.
            ->exportar resultados en excel y html-> en la vista principal con commandlik, debe exportar el archivo de todas las transacciones y con un checkbox se puede agregar la exportacion del balance*/
    /*
facturacion-> facturacion electronica..->¿
metodos de facturacion y clases necesarias
vista de facturacion dentro de la principal de transacciones->
*/

    public void crearTransaccionManual() {
        Transaccion nueva = nuevoRegistro();
        nueva.setArchivoCargado(archivoSeleccionado);
        transaccionDAO.create(nueva);
        listaTransacciones.add(nueva);
        enviarMensaje("Transaccion creada correctamente.", FacesMessage.SEVERITY_INFO);
    }

    /**
     * Aplica el filtro por periodo usando el archivo seleccionado como contexto.
     * Si no hay archivo seleccionado devuelve todas las transacciones (comportamiento actual).
     */
    public void aplicarFiltroPeriodo() {
        // Para la funcionalidad de factura actual solo filtramos por tipo (VENTA/COMPRA).
        if (tipoFacturacion == null || tipoFacturacion.isBlank()) {
            // Mostrar todas las transacciones
            try {
                int total = transaccionDAO.count();
                listaTransacciones = transaccionDAO.findRange(0, Math.max(total, 1));
            } catch (Exception e) {
                listaTransacciones = transaccionDAO.findRange(0, 10000);
            }
            enviarMensaje("Mostrando todas las transacciones (sin filtro de tipo)", FacesMessage.SEVERITY_INFO);
            return;
        }

        // Obtener transacciones por tipo (sin considerar archivo/periodo)
        // Intentar consulta exclusiva por clasificación (solo las transacciones que están clasificadas únicamente como este tipo)
        List<Transaccion> porClasificacion = transaccionDAO.findForFacturacionByTipoAllExclusive(tipoFacturacion);
        if (porClasificacion != null && !porClasificacion.isEmpty()) {
            listaTransacciones = porClasificacion;
            // Contar frecuencias de tipoTransaccion en las clasificaciones para diagnóstico
            Map<String,Integer> freq = new HashMap<>();
            for (Transaccion t : porClasificacion) {
                try {
                    var coll = t.getTransaccionClasificacionCollection();
                    if (coll == null) continue;
                    for (var tc : coll) {
                        if (tc == null) continue;
                        String tipo = tc.getTipoTransaccion();
                        if (tipo == null) tipo = "";
                        tipo = tipo.trim().toUpperCase();
                        freq.put(tipo, freq.getOrDefault(tipo, 0) + 1);
                    }
                } catch (Exception ignored) {}
            }
            StringBuilder detalles = new StringBuilder();
            for (var e : freq.entrySet()) { if (detalles.length() > 0) detalles.append(", "); detalles.append(e.getKey()).append("=").append(e.getValue()); }
            enviarMensaje("Filtro por clasificación: " + porClasificacion.size() + " registros. Tipos: " + (detalles.length()>0?detalles.toString():"(ninguno)"), FacesMessage.SEVERITY_INFO);
        } else {
            // 2) Intentar búsqueda flexible en DAO (clasificación LIKE, descripción, fallback)
            List<Transaccion> flex = transaccionDAO.findForFacturacionByTipoFlexible(tipoFacturacion);
            if (flex == null) flex = java.util.Collections.emptyList();
            // Filtrar resultados flexibles para excluir transacciones que tengan clasificación del tipo contrario
            String desired = tipoFacturacion != null ? tipoFacturacion.trim().toUpperCase() : "";
            String other = "VENTA".equals(desired) ? "COMPRA" : "VENTA";
            List<Transaccion> filtradas = new java.util.ArrayList<>();
            for (Transaccion t : flex) {
                boolean include = true;
                try {
                    var coll = t.getTransaccionClasificacionCollection();
                    if (coll != null && !coll.isEmpty()) {
                        boolean hasDesired = false;
                        boolean hasOther = false;
                        for (var tc : coll) {
                            if (tc == null) continue;
                            String tipo = tc.getTipoTransaccion();
                            if (tipo == null) continue;
                            String up = tipo.trim().toUpperCase();
                            if (up.contains(other)) hasOther = true;
                            if (up.contains(desired)) hasDesired = true;
                        }
                        // si tiene clasificación del otro tipo, excluimos
                        if (hasOther && !hasDesired) include = false;
                        // si tiene ambos, preferimos excluir (ya deberían haber sido capturadas por "exclusive")
                        if (hasOther && hasDesired) include = false;
                    }
                } catch (Exception ignored) {}
                if (include) filtradas.add(t);
            }
            listaTransacciones = filtradas;
            if (listaTransacciones.isEmpty()) {
                enviarMensaje("No se encontraron transacciones para tipo '" + tipoFacturacion + "' tras aplicar filtro exclusivo", FacesMessage.SEVERITY_WARN);
            } else {
                enviarMensaje("Filtro flexible (filtrado): " + listaTransacciones.size() + " registros encontrados.", FacesMessage.SEVERITY_INFO);
            }
        }
        // Limpiar selecciones al aplicar un nuevo filtro
        if (seleccionMap != null) seleccionMap.clear();
    }

    // Permite cargar filtro directamente por parámetros (útil desde otras vistas)
    public void aplicarFiltroPeriodoParaArchivoId(Object archivoId) {
        listaTransacciones = transaccionDAO.findByArchivoIdAndPeriodo(archivoId, periodoFiltro, anioFiltro, mesFiltro, trimestreFiltro);
    }

    // ===========================
    // Métodos de facturación
    // ===========================

    /**
     * Prepara y genera la factura digital usando JSF (Flash + PRG).
     */
    public String prepararFacturacion() {
        if (archivoSeleccionado == null || archivoSeleccionado.getIdArchivoCargado() == null) {
            enviarMensaje("Seleccione un archivo para preparar la facturación.", FacesMessage.SEVERITY_WARN);
            return null;
        }
        if (tipoFacturacion == null || tipoFacturacion.isBlank()) {
            enviarMensaje("Seleccione el tipo de facturación (VENTA o COMPRA).", FacesMessage.SEVERITY_WARN);
            return null;
        }

        Object archivoId = archivoSeleccionado.getIdArchivoCargado();
        listaParaFacturar = transaccionDAO.findForFacturacion(archivoId, tipoFacturacion, periodoFiltro, anioFiltro, mesFiltro, trimestreFiltro);

        // Calcular totales
        totalesPorMoneda = new HashMap<>();
        totalFactura = BigDecimal.ZERO;
        if (listaParaFacturar != null) {
            for (Transaccion t : listaParaFacturar) {
                String moneda = t.getMoneda() != null ? t.getMoneda() : "";
                BigDecimal monto = t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO;
                totalesPorMoneda.putIfAbsent(moneda, BigDecimal.ZERO);
                totalesPorMoneda.put(moneda, totalesPorMoneda.get(moneda).add(monto));
                totalFactura = totalFactura.add(monto);
            }
        }

        // Generar JSON y poner en flash
        String invoice = buildInvoiceJson();
        FacesContext fc = FacesContext.getCurrentInstance();
        fc.getExternalContext().getFlash().put("invoiceJson", invoice);
        fc.getExternalContext().getFlash().put("totalesPorMoneda", totalesPorMoneda);
        fc.getExternalContext().getFlash().put("factCount", listaParaFacturar != null ? listaParaFacturar.size() : 0);
        fc.getExternalContext().getFlash().setKeepMessages(true);

        enviarMensaje("Preparación de facturación completada. Registros: " + (listaParaFacturar != null ? listaParaFacturar.size() : 0), FacesMessage.SEVERITY_INFO);

        return "transacciones?faces-redirect=true"; // PRG
    }

    public String generarFacturaDigital() {
        // Generar factura usando las transacciones seleccionadas en la vista
        List<Transaccion> seleccionadas = getSeleccionadas();
        if (seleccionadas == null || seleccionadas.isEmpty()) {
            enviarMensaje("No ha seleccionado transacciones para facturar.", FacesMessage.SEVERITY_WARN);
            return null;
        }

        // Si no se seleccionó tipo, intentar inferirlo desde las transacciones seleccionadas
        if (tipoFacturacion == null || tipoFacturacion.isBlank()) {
            String inferido = inferirTipoDesdeSeleccion(seleccionadas);
            if (inferido != null) {
                this.tipoFacturacion = inferido.toUpperCase();
                enviarMensaje("Tipo inferido: " + this.tipoFacturacion, FacesMessage.SEVERITY_INFO);
            } else {
                // No se pudo inferir, asumimos VENTA por defecto (puedes cambiar esto)
                this.tipoFacturacion = "VENTA";
                enviarMensaje("Tipo no inferible. Se asumirá VENTA por defecto.", FacesMessage.SEVERITY_WARN);
            }
        }

        // Calcular totales (asumimos USD único)
        totalesPorMoneda = new HashMap<>();
        totalFactura = java.math.BigDecimal.ZERO;
        for (Transaccion t : seleccionadas) {
            java.math.BigDecimal monto = t.getMonto() != null ? t.getMonto() : java.math.BigDecimal.ZERO;
            totalFactura = totalFactura.add(monto);
        }
        totalesPorMoneda.put("USD", totalFactura);

        // Generar JSON en memoria
        this.listaParaFacturar = seleccionadas;
        this.invoiceJson = buildInvoiceJson();

        // Guardar JSON y PDF en carpeta fctr dentro del webapp
         try {
            // Ruta física del webapp
            String basePath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/");
            java.nio.file.Path folder = java.nio.file.Paths.get(basePath, FCTR_FOLDER);
            if (!java.nio.file.Files.exists(folder)) {
                java.nio.file.Files.createDirectories(folder);
            }
            String timestamp = String.valueOf(System.currentTimeMillis());
            String jsonFileName = "factura-" + timestamp + ".json";
            String pdfFileName = "factura-" + timestamp + ".pdf";
            java.nio.file.Path jsonPath = folder.resolve(jsonFileName);
            java.nio.file.Path pdfPath = folder.resolve(pdfFileName);

            // Escribir JSON
            java.nio.file.Files.writeString(jsonPath, this.invoiceJson, java.nio.charset.StandardCharsets.UTF_8);

            // Generar PDF usando OpenPDF
            try (java.io.OutputStream os = java.nio.file.Files.newOutputStream(pdfPath)) {

                //=========================================================
                // CONFIGURACIÓN BASE DEL DOCUMENTO
                //=========================================================
                com.lowagie.text.Document document = new com.lowagie.text.Document();
                com.lowagie.text.pdf.PdfWriter.getInstance(document, os);
                document.open();

                //=========================================================
                // ENCABEZADO PRINCIPAL DE FACTURA
                //=========================================================
                com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph(
                        "FACTURA ELECTRÓNICA",
                        new com.lowagie.text.Font(
                                com.lowagie.text.Font.HELVETICA,
                                18,
                                com.lowagie.text.Font.BOLD
                        )
                );
                title.setAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                document.add(title);

                document.add(new com.lowagie.text.Paragraph("\n"));

                //=========================================================
                // DATOS GENERALES DE FACTURA
                //=========================================================
                com.lowagie.text.pdf.PdfPTable infoTable = new com.lowagie.text.pdf.PdfPTable(2);
                infoTable.setWidthPercentage(100);

                infoTable.addCell("Tipo de Facturación:");
                infoTable.addCell(this.tipoFacturacion != null ? this.tipoFacturacion : "");

                infoTable.addCell("Fecha de emisión:");
                infoTable.addCell(new java.util.Date().toString());

                infoTable.addCell("Cantidad de transacciones:");
                infoTable.addCell(String.valueOf(seleccionadas.size()));

                document.add(infoTable);

                document.add(new com.lowagie.text.Paragraph("\n"));

                //=========================================================
                // TABLA DE ÍTEMS / DETALLE DE FACTURA
                //=========================================================
                com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(4);
                table.setWidths(new int[]{3, 3, 7, 3});
                table.setWidthPercentage(100);

                // CABECERAS
                com.lowagie.text.pdf.PdfPCell h1 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph("ID"));
                com.lowagie.text.pdf.PdfPCell h2 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph("Fecha"));
                com.lowagie.text.pdf.PdfPCell h3 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph("Descripción"));
                com.lowagie.text.pdf.PdfPCell h4 = new com.lowagie.text.pdf.PdfPCell(new com.lowagie.text.Paragraph("Monto (USD)"));

                // **Estilo de encabezados** (solo bordes)
                h1.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                h2.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                h3.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                h4.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);

                table.addCell(h1);
                table.addCell(h2);
                table.addCell(h3);
                table.addCell(h4);

                //=========================================================
                // Filas del detalle
                //=========================================================
                for (Transaccion t : seleccionadas) {

                    // IMPORTANTE:
                    // Estos valores deben estar convertidos a STRING para evitar NPE
                    table.addCell(t.getId() != null ? t.getId().toString() : "");
                    table.addCell(t.getFecha() != null ? t.getFecha().toString() : "");
                    table.addCell(t.getDescripcion() != null ? t.getDescripcion() : "");
                    table.addCell(t.getMonto() != null ? t.getMonto().toString() : "0.00");
                }

                document.add(table);
                document.add(new com.lowagie.text.Paragraph("\n"));

                //=========================================================
                // TOTAL FINAL
                //=========================================================
                com.lowagie.text.Paragraph total = new com.lowagie.text.Paragraph(
                        "TOTAL A PAGAR (USD): " + totalFactura,
                        new com.lowagie.text.Font(
                                com.lowagie.text.Font.HELVETICA,
                                14,
                                com.lowagie.text.Font.BOLD
                        )
                );
                total.setAlignment(com.lowagie.text.Element.ALIGN_RIGHT);
                document.add(total);

                // Cierre del documento
                document.close();
            }

            // guardar rutas relativas en el bean y en sesión para que la vista pueda enlazarlas tras el redirect
            String relativeJsonPath = FCTR_FOLDER + "/" + jsonFileName;
            String relativePdfPath = FCTR_FOLDER + "/" + pdfFileName;
            this.jsonFilePath = relativeJsonPath;
            this.pdfFilePath = relativePdfPath;
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("jsonFilePath", relativeJsonPath);
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("pdfFilePath", relativePdfPath);

             // Mensaje y dejar invoiceJson en flash para mostrar en la vista
             FacesContext fc = FacesContext.getCurrentInstance();
             fc.getExternalContext().getFlash().put("invoiceJson", this.invoiceJson);
             fc.getExternalContext().getFlash().put("totalesPorMoneda", this.totalesPorMoneda);
             fc.getExternalContext().getFlash().put("factCount", this.listaParaFacturar != null ? this.listaParaFacturar.size() : 0);

             enviarMensaje("Factura generada y guardada en carpeta fctr: " + jsonFileName + ", " + pdfFileName, FacesMessage.SEVERITY_INFO);
         } catch (Exception e) {
             enviarMensaje("Error al generar factura: " + e.getMessage(), FacesMessage.SEVERITY_ERROR);
             return null;
         }

         // Redirigir para mostrar resultado (PRG)
         return "transacciones?faces-redirect=true";
    }

    /**
     * Maneja acciones invocadas por GET (f:viewAction). Lee parámetros y ejecuta
     * la preparación o generación en la misma petición para evitar problemas de restauración de vista.
     */
    public void handleViewAction() {
        FacesContext fc = FacesContext.getCurrentInstance();
        if (fc == null) return;
        var params = fc.getExternalContext().getRequestParameterMap();
        String accion = params.get("accion");
        if (accion == null) return; // nada que hacer

        // leer parámetros y asignarlos al bean
        String tipo = params.get("tipo");
        String periodo = params.get("periodo");
        String anioStr = params.get("anio");
        String mesStr = params.get("mes");
        String trimestreStr = params.get("trimestre");
        String archivoIdStr = params.get("archivoId");

        this.tipoFacturacion = tipo != null && !tipo.isBlank() ? tipo : this.tipoFacturacion;
        this.periodoFiltro = periodo != null && !periodo.isBlank() ? periodo : this.periodoFiltro;
        try { this.anioFiltro = anioStr != null && !anioStr.isBlank() ? Integer.valueOf(anioStr) : this.anioFiltro; } catch (Exception e) { this.anioFiltro = null; }
        try { this.mesFiltro = mesStr != null && !mesStr.isBlank() ? Integer.valueOf(mesStr) : this.mesFiltro; } catch (Exception e) { this.mesFiltro = null; }
        try { this.trimestreFiltro = trimestreStr != null && !trimestreStr.isBlank() ? Integer.valueOf(trimestreStr) : this.trimestreFiltro; } catch (Exception e) { this.trimestreFiltro = null; }

        Object archivoIdObj = null;
        if (archivoIdStr != null && !archivoIdStr.isBlank()) {
            try {
                archivoIdObj = java.util.UUID.fromString(archivoIdStr);
            } catch (Exception ex) {
                // dejar archivoIdObj en null si no es UUID, también podría ser otro tipo
                archivoIdObj = archivoIdStr;
            }
        }

        if (this.tipoFacturacion == null || this.tipoFacturacion.isBlank()) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Seleccione el tipo de facturación (VENTA o COMPRA).", null));
            return;
        }

        // obtener transacciones y calcular totales
        try {
            this.listaParaFacturar = transaccionDAO.findForFacturacion(archivoIdObj, this.tipoFacturacion, this.periodoFiltro, this.anioFiltro, this.mesFiltro, this.trimestreFiltro);
            this.totalesPorMoneda = new HashMap<>();
            this.totalFactura = BigDecimal.ZERO;
            if (this.listaParaFacturar != null) {
                for (Transaccion t : this.listaParaFacturar) {
                    String moneda = t.getMoneda() != null ? t.getMoneda() : "";
                    BigDecimal monto = t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO;
                    this.totalesPorMoneda.putIfAbsent(moneda, BigDecimal.ZERO);
                    this.totalesPorMoneda.put(moneda, this.totalesPorMoneda.get(moneda).add(monto));
                    this.totalFactura = this.totalFactura.add(monto);
                }
            }

            // construir JSON para mostrar en la misma petición
            this.invoiceJson = buildInvoiceJson();

            if ("preparar".equalsIgnoreCase(accion)) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Preparación de facturación completada. Registros: " + (this.listaParaFacturar != null ? this.listaParaFacturar.size() : 0), null));
            } else if ("generar".equalsIgnoreCase(accion)) {
                fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Factura digital generada (JSON).", null));
            }
        } catch (Exception e) {
            fc.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error preparando facturación: " + e.getMessage(), null));
        }
    }

    /**
     * Construye el JSON de la factura basada en listaParaFacturar y totalesPorMoneda.
     */
    private String buildInvoiceJson() {
        if (listaParaFacturar == null || listaParaFacturar.isEmpty()) return "";
        String facturaId = UUID.randomUUID().toString();
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        sb.append("\"idFactura\":\"").append(facturaId).append('\"');
        sb.append(",\"tipo\":\"").append(tipoFacturacion).append('\"');
        sb.append(",\"fechaEmision\":\"").append(new Date()).append('\"');

        // Totales por moneda
        sb.append(",\"totalesPorMoneda\":{");
        boolean first = true;
        for (Map.Entry<String, BigDecimal> e : totalesPorMoneda.entrySet()) {
            if (!first) sb.append(',');
            sb.append('"').append(e.getKey()).append('"').append(':').append(e.getValue());
            first = false;
        }
        sb.append('}');

        // Items
        sb.append(",\"items\":[");
        boolean firstItem = true;
        for (Transaccion t : listaParaFacturar) {
            if (!firstItem) sb.append(',');
            sb.append('{');
            sb.append("\"idTransaccion\":\"").append(t.getId() != null ? t.getId().toString() : "").append('\"');
            sb.append(',');
            sb.append("\"fecha\":\"").append(t.getFecha() != null ? t.getFecha().toString() : "").append('\"');
            sb.append(',');
            sb.append("\"descripcion\":\"").append(t.getDescripcion() != null ? t.getDescripcion().replace("\"", "\\\"") : "").append('\"');
            sb.append(',');
            sb.append("\"monto\":").append(t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO);
            sb.append(',');
            sb.append("\"moneda\":\"").append(t.getMoneda() != null ? t.getMoneda() : "").append('\"');
            sb.append('}');
            firstItem = false;
        }
        sb.append(']');

        sb.append('}');
        return sb.toString();
    }

    // ===========================
    // Getters / Setters nuevos
    // ===========================
    public String getTipoFacturacion() { return tipoFacturacion; }
    public void setTipoFacturacion(String tipoFacturacion) { this.tipoFacturacion = tipoFacturacion; }

    public List<Transaccion> getListaParaFacturar() { return listaParaFacturar; }
    public void setListaParaFacturar(List<Transaccion> listaParaFacturar) { this.listaParaFacturar = listaParaFacturar; }

    public Map<String, BigDecimal> getTotalesPorMoneda() { return totalesPorMoneda; }
    public void setTotalesPorMoneda(Map<String, BigDecimal> totalesPorMoneda) { this.totalesPorMoneda = totalesPorMoneda; }

    public BigDecimal getTotalFactura() { return totalFactura; }
    public void setTotalFactura(BigDecimal totalFactura) { this.totalFactura = totalFactura; }

    public String getInvoiceJson() { return invoiceJson; }
    public void setInvoiceJson(String invoiceJson) { this.invoiceJson = invoiceJson; }

    // getters para rutas (usadas en la vista)
    public String getJsonFilePath() { return jsonFilePath; }
    public String getPdfFilePath() { return pdfFilePath; }

    // Selección
    public Map<String, Boolean> getSeleccionMap() { return seleccionMap; }
    public void setSeleccionMap(Map<String, Boolean> seleccionMap) { this.seleccionMap = seleccionMap; }

    public List<Transaccion> getSeleccionadas() {
        if (seleccionMap == null || seleccionMap.isEmpty() || listaTransacciones == null) return java.util.Collections.emptyList();
        List<Transaccion> res = new java.util.ArrayList<>();
        for (Transaccion t : listaTransacciones) {
            String key = t.getId() != null ? t.getId().toString() : null;
            if (key != null && Boolean.TRUE.equals(seleccionMap.get(key))) {
                res.add(t);
            }
        }
        return res;
    }

    /**
     * Intenta inferir el tipo de facturación (VENTA/COMPRA) a partir de las
     * clasificaciones asociadas a las transacciones seleccionadas.
     * Devuelve null si no encuentra información.
     */
    private String inferirTipoDesdeSeleccion(List<Transaccion> seleccionadas) {
        if (seleccionadas == null || seleccionadas.isEmpty()) return null;
        Map<String, Integer> contador = new HashMap<>();
        for (Transaccion t : seleccionadas) {
            try {
                var coll = t.getTransaccionClasificacionCollection();
                if (coll == null) continue;
                for (TransaccionClasificacion tc : coll) {
                    if (tc == null) continue;
                    String tipo = tc.getTipoTransaccion();
                    if (tipo == null || tipo.isBlank()) continue;
                    tipo = tipo.trim().toUpperCase();
                    contador.put(tipo, contador.getOrDefault(tipo, 0) + 1);
                }
            } catch (Exception ignored) {}
        }
        if (contador.isEmpty()) return null;
        // elegir el tipo con mayor frecuencia
        String mejor = null;
        int max = 0;
        for (var e : contador.entrySet()) {
            if (e.getValue() > max) { max = e.getValue(); mejor = e.getKey(); }
        }
        return mejor;
    }

    // campo para recibir el id pasado en la URL
    private String idArchivoSeleccionado;

    public String getIdArchivoSeleccionado() {
        return idArchivoSeleccionado;
    }
    public void setIdArchivoSeleccionado(String idArchivoSeleccionado) {
        this.idArchivoSeleccionado = idArchivoSeleccionado;
    }

    public void cargarTransaccionesDelArchivo() {
        try {
            if (idArchivoSeleccionado == null || idArchivoSeleccionado.isBlank()) {
                enviarMensaje("No se recibió el ID del archivo.", FacesMessage.SEVERITY_WARN);
                return;
            }
            UUID id = UUID.fromString(idArchivoSeleccionado);
            List<Transaccion> tmp = transaccionDAO.findByArchivoId(id);
            listaTransacciones = tmp != null ? tmp : java.util.Collections.emptyList();
            if (modelo != null) {
                modelo.setWrappedData(listaTransacciones);
            }
            enviarMensaje("Transacciones cargadas del archivo seleccionado.", FacesMessage.SEVERITY_INFO);

        } catch (Exception e) {
            enviarMensaje("Error cargando transacciones: " + e.getMessage(),
                    FacesMessage.SEVERITY_ERROR);
            e.printStackTrace();
        }
    }
    public void seleccionarRegistro(SelectEvent<Transaccion> event) {
        this.registro = event.getObject();
        this.estado = ESTADO_CRUD.MODIFICAR;
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