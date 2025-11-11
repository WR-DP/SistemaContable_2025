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

public class ClasificacionService {

    @Inject
    private TransaccionDAO transaccionDAO;

    @Inject
    private CuentaContableDAO cuentaContableDAO;

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
            //transaccion.setCuentaContable(cuenta);
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
                }
            }
            return exitosas;
        });
    }
}
