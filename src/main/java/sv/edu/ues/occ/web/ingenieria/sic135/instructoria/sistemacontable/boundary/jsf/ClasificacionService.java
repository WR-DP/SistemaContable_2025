package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf;

import jakarta.inject.Inject;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.CuentaContableDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.CuentaContable;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Pattern;

public class ClasificacionService {

    @Inject
    private TransaccionDAO transaccionDAO;

    @Inject
    private CuentaContableDAO cuentaContableDAO;

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
                    List<CuentaContable> sugerencias = cuentaContableDAO.findCuentaPrincipales();
                    sugerencias.add(0, cc); // Pone la sugerencia IA de primera
                    return sugerencias;
                }
            }
            return cuentaContableDAO.findCuentaPrincipales();
        });
    }

    public boolean clasificarTransaccionManual(Transaccion transaccion, CuentaContable cuenta) {
        if (transaccion == null || cuenta == null) return false;
        try {
            // La asignación de la cuenta contable marca la transacción como clasificada
            transaccion.setCuentaContable(cuenta);
            transaccion.setUpdatedAt(Instant.now());
            transaccionDAO.edit(transaccion);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public CompletionStage<Boolean> clasificarTransaccionAutomatica(Transaccion transaccion) {
        return obtenerSugerenciasIA(transaccion)
                .thenApply(sugerencias -> {
                    if (!sugerencias.isEmpty()) {
                        CuentaContable mejorSugerencia = sugerencias.get(0);
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
                    // Log del error para debugging
                    System.err.println("Error clasificando transacción ID: " +
                            (t.getId() != null ? t.getId() : "N/A") +
                            " - " + e.getMessage());
                }
            }
            return exitosas;
        });
    }
}
