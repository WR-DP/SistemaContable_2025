package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.CuentaContableDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionClasificacionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.CuentaContable;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.TransaccionClasificacion;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClasificacionService {

    private static final Logger LOGGER = Logger.getLogger(ClasificacionService.class.getName());

    @Inject
    private CuentaContableDAO cuentaContableDAO;

    @Inject
    private TransaccionClasificacionDAO transaccionClasificacionDAO;

    private String clasificarPorReglas(String descripcion) {
        if (descripcion.contains("sueldo") || descripcion.contains("salario")) {
            return "5.1.01"; // Gastos de Personal
        } else if (descripcion.contains("alquiler") || descripcion.contains("arriendo")) {
            return "5.1.04"; // Gastos de Renta
        } else if (descripcion.contains("interes") || descripcion.contains("deposito")) {
            return "4.1.02"; // Ingresos por Intereses
        }
        return null;
    }

    public CompletionStage<List<CuentaContable>> obtenerSugerenciasIA(Transaccion transaccion) {
        return CompletableFuture.supplyAsync(() -> {
            String codigoSugerido = clasificarPorReglas(transaccion.getDescripcion().toLowerCase());

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
                    LOGGER.log(Level.WARNING, "Error clasificando transaccion en lote", e);
                 }
             }
             return exitosas;
         });
     }
 }
