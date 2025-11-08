package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.boundary.jsf.converter;

import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@FacesConverter(value = "instantConverter")
public class InstantConverter implements Converter<Instant> {

    private static final ZoneId ZONA = ZoneId.of("America/El_Salvador");

    @Override
    public Instant getAsObject(FacesContext context, UIComponent component, String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            // PrimeFaces DatePicker devuelve algo que JSF ya convierte a Date
            Object submittedValue = component.getAttributes().get("submittedValue");
            if (submittedValue instanceof Date date) {
                return date.toInstant();
            }

            // Si viene como texto ISO
            return LocalDateTime.parse(value.replace(" ", "T")).atZone(ZONA).toInstant();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Instant value) {
        if (value == null) {
            return "";
        }
        LocalDateTime ldt = LocalDateTime.ofInstant(value, ZONA);
        return ldt.toString().replace("T", " ");
    }
}
