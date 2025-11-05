package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.ArchivoCargadoDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.TransaccionDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "ArchivoDetalleServlet", urlPatterns = {"/archivo"})
public class ArchivoDetalleServlet extends HttpServlet {

    @EJB
    private ArchivoCargadoDAO archivoCargadoDAO;

    @EJB
    private TransaccionDAO transaccionDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        ArchivoCargado archivo = null;
        List<Transaccion> transacciones = Collections.emptyList();
        if (idParam != null && !idParam.isBlank()) {
            try {
                UUID id = UUID.fromString(idParam);
                archivo = archivoCargadoDAO.findById(id);
                if (archivo != null) {
                    // Parámetros opcionales de filtrado
                    String periodo = trimToNull(req.getParameter("periodo")); // mensual | trimestral | anual
                    Integer anio = parseInt(req.getParameter("anio"));
                    Integer mes = parseInt(req.getParameter("mes")); // 1-12
                    Integer trimestre = parseInt(req.getParameter("trimestre")); // 1-4

                    LocalDate desde = null;
                    LocalDate hasta = null;

                    if ("mensual".equalsIgnoreCase(periodo) && anio != null && mes != null && mes >= 1 && mes <= 12) {
                        YearMonth ym = YearMonth.of(anio, mes);
                        desde = ym.atDay(1);
                        hasta = ym.atEndOfMonth();
                    } else if ("trimestral".equalsIgnoreCase(periodo) && anio != null && trimestre != null && trimestre >= 1 && trimestre <= 4) {
                        int mesInicio = (trimestre - 1) * 3 + 1; // 1,4,7,10
                        YearMonth ymInicio = YearMonth.of(anio, mesInicio);
                        YearMonth ymFin = ymInicio.plusMonths(2);
                        desde = ymInicio.atDay(1);
                        hasta = ymFin.atEndOfMonth();
                    } else if ("anual".equalsIgnoreCase(periodo) && anio != null) {
                        Year y = Year.of(anio);
                        desde = y.atDay(1);
                        hasta = y.atMonth(12).atEndOfMonth();
                    }

                    if (desde != null && hasta != null) {
                        transacciones = transaccionDAO.findByArchivoIdAndDateRange(id, desde, hasta);
                    } else {
                        transacciones = transaccionDAO.findByArchivoId(id);
                    }
                }
            } catch (IllegalArgumentException e) {
                // id no es UUID válido
            }
        }
        req.setAttribute("archivo", archivo);
        req.setAttribute("transacciones", transacciones);
        req.getRequestDispatcher("/detallesArchivo.jsp").forward(req, resp);
    }

    private static String trimToNull(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

    private static Integer parseInt(String v) {
        if (v == null || v.isBlank()) return null;
        try { return Integer.parseInt(v); } catch (NumberFormatException e) { return null; }
    }
}
