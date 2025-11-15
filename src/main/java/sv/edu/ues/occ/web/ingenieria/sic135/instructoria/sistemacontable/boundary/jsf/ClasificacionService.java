package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.CuentaContableDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionClasificacionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.CuentaContable;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class ClasificacionService {

    private static final Logger LOGGER = Logger.getLogger(ClasificacionService.class.getName());

    @Inject
    private CuentaContableDAO cuentaContableDAO;

    @Inject
    private TransaccionClasificacionDAO transaccionClasificacionDAO;

    private static final java.util.Map<String, Pattern>REGLAS_CLASIFICACION =java.util.Map.of(
            "5.1.01", Pattern.compile(".*\\b(sueldo|salario|nómina|nomina|pago personal|planilla)\\b.*", Pattern.CASE_INSENSITIVE),
            "5.1.04", Pattern.compile(".*\\b(alquiler|arriendo|renta|leasing|renta local)\\b.*", Pattern.CASE_INSENSITIVE),
            "4.1.02", Pattern.compile(".*\\b(interés|interes|depósito|deposito|inversión|inversion|rendimiento)\\b.*", Pattern.CASE_INSENSITIVE),
            "5.1.03", Pattern.compile(".*\\b(servicio|mantenimiento|reparación|reparacion|soporte técnico)\\b.*", Pattern.CASE_INSENSITIVE),
            "5.1.02", Pattern.compile(".*\\b(luz|energía|energia|agua|teléfono|telefono|internet|servicio básico)\\b.*", Pattern.CASE_INSENSITIVE),
            "1.1.01", Pattern.compile(".*\\b(compra|adquisición|adquisicion|mercadería|mercaderia|inventario)\\b.*", Pattern.CASE_INSENSITIVE)
    );
    private String clasificarPorReglas(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return null;
        }

        try {
            // Probar cada regla en el mapa
            for (java.util.Map.Entry<String, Pattern> entry : REGLAS_CLASIFICACION.entrySet()) {
                if (entry.getValue().matcher(descripcion).find()) {
                    return entry.getKey();
                }
            }
            return null;

        } catch (Exception e) {
            System.err.println("Error en clasificación por reglas: " + e.getMessage());
            return null;
        }
    }

    public CompletionStage<List<CuentaContable>> obtenerSugerenciasIA(Transaccion transaccion) {
        return CompletableFuture.supplyAsync(() -> {
            String codigoSugerido = clasificarPorReglas(transaccion.getDescripcion());

            if (codigoSugerido != null) {
                CuentaContable cc = cuentaContableDAO.findByCodigo(codigoSugerido);
                if (cc != null) {
                    // convertir a LinkedList para poder añadir al principio sin warning
                    LinkedList<CuentaContable> sugerencias = new LinkedList<>(cuentaContableDAO.findCuentaPrincipales());
                    sugerencias.addFirst(cc);
                    return sugerencias;
                }
            }
            return cuentaContableDAO.findCuentaPrincipales();
        });
    }

    public boolean clasificarTransaccionManual(Transaccion transaccion, CuentaContable cuenta) {
        if (transaccion == null || cuenta == null) return false;
        try {
            // Crear registro de clasificación asociado a la transacción
            TransaccionClasificacion tc = new TransaccionClasificacion();
            tc.setTransaccion(transaccion);
            tc.setOrigen("MANUAL");
            tc.setCuentaContableDebe(cuenta);
            tc.setConfianzaClasificacion(new BigDecimal("1.00"));
            tc.setCreatedAt(Instant.now());
            transaccionClasificacionDAO.create(tc);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error clasificando transaccion manualmente", e);
            return false;
        }
    }

    public CompletionStage<Boolean> clasificarTransaccionAutomatica(Transaccion transaccion) {
        return obtenerSugerenciasIA(transaccion)
                 .thenApply(sugerencias -> {
                     if (!sugerencias.isEmpty()) {
                        CuentaContable mejorSugerencia = sugerencias.getFirst();
                        return clasificarTransaccionManual(transaccion, mejorSugerencia);
                     }
                     return false;
                 });
     }

     public CompletionStage<Integer> clasificarLoteAutomatico(List<Transaccion> transacciones) {
         return CompletableFuture.supplyAsync(() -> {
             int exitosas = 0;
             for (Transaccion t : transacciones) {
                 try {
                     if (clasificarTransaccionAutomatica(t).toCompletableFuture().get()) {
                         exitosas++;
                     }
                 } catch (Exception e) {
                    //
                 }
             }
             return exitosas;
         });
     }

}
