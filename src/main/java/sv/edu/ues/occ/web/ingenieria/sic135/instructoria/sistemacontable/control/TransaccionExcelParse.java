package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@ApplicationScoped
public class TransaccionExcelParse {

    // Métodos auxiliares
    private String getString(Cell c) {
        if (c == null) return "";

        return switch (c.getCellType()) {
            case STRING -> c.getStringCellValue().trim();
            case NUMERIC -> String.valueOf(c.getNumericCellValue()).trim();
            case BOOLEAN -> String.valueOf(c.getBooleanCellValue());
            default -> "";
        };
    }

    private BigDecimal getBigDecimal(Cell c) {
        if (c == null) return BigDecimal.ZERO;

        return switch (c.getCellType()) {
            case NUMERIC -> BigDecimal.valueOf(c.getNumericCellValue());
            case STRING -> {
                try { yield new BigDecimal(c.getStringCellValue().trim()); }
                catch (Exception e) { yield BigDecimal.ZERO; }
            }
            default -> BigDecimal.ZERO;
        };
    }

    private Date getDate(Cell c) {
        if (c == null) return null;
        try {
            if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
                return c.getDateCellValue();
            }

            if (c.getCellType() == CellType.STRING) {
                String val = c.getStringCellValue().trim();
                if (!val.isEmpty()) {
                    return java.sql.Date.valueOf(val); // yyyy-MM-dd
                }
            }
        } catch (Exception ignored) {
            return null;
        }

        return null;
    }

    // parser principal
    public List<Transaccion> parsearExcel(String archivo, ArchivoCargado archivoCargado) {
        List<Transaccion> lista = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet hoja = workbook.getSheetAt(0);
            Iterator<Row> filas = hoja.iterator();

            //  Saltar solamente una fila
            if(filas.hasNext()){
                filas.next();
            }
            int numFila = 2;// empezar  desde la fila 2(porque se salta la fila 1)
            while (filas.hasNext()) {
                Row fila = filas.next();

                // Validar si todas las celdas están vacías
                boolean filaVacia =
                        (fila.getCell(1) == null || getString(fila.getCell(1)).isBlank()) &&
                                (fila.getCell(2) == null || getString(fila.getCell(2)).isBlank()) &&
                                (fila.getCell(3) == null || getString(fila.getCell(3)).isBlank()) &&
                                (fila.getCell(4) == null || getString(fila.getCell(4)).isBlank());

                if (filaVacia) {
                    continue;
                }

                Transaccion t = new Transaccion();
                t.setId(UUID.randomUUID());
                t.setArchivoCargado(archivoCargado);
                t.setFilaExcel(numFila++);

                // Lectura de las columnas
                Date fecha = getDate(fila.getCell(1));
                if (fecha == null) {
                    continue;
                }
                t.setFecha(fecha);
                t.setDescripcion(getString(fila.getCell(2)));
                t.setMonto(getBigDecimal(fila.getCell(3)));
                t.setMoneda(getString(fila.getCell(4)));

                lista.add(t);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }
}