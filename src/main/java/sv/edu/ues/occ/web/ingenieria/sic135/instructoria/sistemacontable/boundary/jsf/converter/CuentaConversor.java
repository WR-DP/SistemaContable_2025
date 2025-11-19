package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf.converter;

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

@FacesConverter(value="cuentaConversor", managed = true)
@Deprecated
public class CuentaConversor implements Converter<CuentaContable>, Serializable {
    @Inject
    CuentaContableDAO cuentaContableDAO;

    @Override
    public CuentaContable getAsObject(FacesContext facesContext, UIComponent uiComponent, String s){
        if(s != null && !s.isBlank()){
            int inicioId =s.lastIndexOf('(');
            int finId =s.lastIndexOf(')');
            if(inicioId != -1 && finId != -1 && finId > inicioId){
                String idStr = s.substring(inicioId+1, finId);
                try{
                    Long id = Long.valueOf(idStr);
                    return cuentaContableDAO.findByID(id);
                }catch(Exception ex){
                    Logger.getLogger(CuentaConversor.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, CuentaContable cuentaContable) {
        if (cuentaContable != null && cuentaContable.getId() != null && cuentaContable.getCodigo() != null) {
            return cuentaContable.getCodigo() + " (" + cuentaContable.getId().toString() + ")";
        }
        return null;
    }

}
