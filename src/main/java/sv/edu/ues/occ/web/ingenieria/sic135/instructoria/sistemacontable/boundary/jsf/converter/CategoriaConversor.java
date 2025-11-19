package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf.converter;

import jakarta.enterprise.context.Dependent;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control.CategoriaDAO;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Categoria;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@FacesConverter(value ="categoriaConversor", managed = true)
@Dependent
public class CategoriaConversor implements Converter<Categoria>, Serializable {
    @Inject
    CategoriaDAO categoriaDAO;

    @Override
    public Categoria getAsObject(FacesContext facesContext, UIComponent uiComponent, String s) {
        if(s != null && !s.isBlank()){
            int inicioId =s.lastIndexOf('(');
            int finId =s.lastIndexOf(')');
            if(inicioId != -1 && finId != -1 && finId > inicioId){
                String idStr = s.substring(inicioId+1, finId);
                try{
                    Long id = Long.valueOf(idStr);
//                    Categoria categoria = new Categoria();
//                    categoria.setId(id);
//                    return categoria;
                    return categoriaDAO.findByID(id);
                }catch(Exception ex){
                    Logger.getLogger(CategoriaConversor.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Categoria categoria) {
        if(categoria != null && categoria.getId()!=null && categoria.getNombre()!=null){
            return categoria.getNombre()+" ("+categoria.getId().toString()+")";
        }
        return null;
    }
}
