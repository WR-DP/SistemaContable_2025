package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable;

import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.ArchivoCargadoDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ArchivoCargadoServlet", urlPatterns = {"/archivos"})
public class ArchivoCargadoServlet extends HttpServlet {

    @EJB
    private ArchivoCargadoDAO archivoCargadoDAO;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ArchivoCargado> archivos = archivoCargadoDAO.findAll();
        req.setAttribute("archivos", archivos);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }
}

