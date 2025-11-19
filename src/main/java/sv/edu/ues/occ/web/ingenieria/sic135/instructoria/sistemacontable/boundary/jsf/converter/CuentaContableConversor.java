package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf.converter;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.CuentaContableDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.CuentaContable;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value = "cuentaConversor", managed = true)
@Dependent
public class CuentaContableConversor implements Converter<CuentaContable>, Serializable {
    @Inject
    private CuentaContableDAO cuentaContableDAO;

    @Override
    public CuentaContable getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if (s == null || s.isBlank()) return null;
        int inicio = s.lastIndexOf('(');
        int fin = s.lastIndexOf(')');
        if (inicio != -1 && fin != -1 && fin > inicio) {
            String idStr = s.substring(inicio + 1, fin).trim();
            try {
                Long id = Long.valueOf(idStr);
                return cuentaContableDAO.findByID(id);
            } catch (Exception ex) {
                Logger.getLogger(CuentaContableConversor.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, CuentaContable cuenta) {
        if (cuenta != null && cuenta.getId() != null) {
            String nombre = cuenta.getNombre() != null ? cuenta.getNombre() : "";
            String codigo = "";
            try {
                // si existe getCodigo()
                java.lang.reflect.Method m = cuenta.getClass().getMethod("getCodigo");
                Object c = m.invoke(cuenta);
                codigo = c != null ? " " + c.toString() : "";
            } catch (Exception ignored) {}
            return nombre + codigo + " (" + cuenta.getId().toString() + ")";
        }
        return "";
    }
}