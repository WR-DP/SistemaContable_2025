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

    // Metodos auxiliares
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
                try { yield new BigDecimal(c.getStringCellValue()); }
                catch (Exception e) { yield BigDecimal.ZERO; }
            }
            default -> BigDecimal.ZERO;
        };
    }

    private Date getDate(Cell c) {
        if (c == null) return null;

        if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
            return c.getDateCellValue();
        }

        if (c.getCellType() == CellType.STRING) {
            try { return java.sql.Date.valueOf(c.getStringCellValue().trim()); }
            catch (Exception ignored) {}
        }

        return null;
    }

    //parser principal
    public List<Transaccion> parsearExcel(String archivo, ArchivoCargado archivoCargado) {
        List<Transaccion> lista = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet hoja = workbook.getSheetAt(0);
            Iterator<Row> filas = hoja.iterator();


            if (filas.hasNext()) filas.next();
            int numFila = 1;
            while (filas.hasNext()) {
                Row fila = filas.next();

                // si toda la fila vacia saltar
                if (fila.getCell(1) == null &&
                        fila.getCell(2) == null &&
                        fila.getCell(3) == null &&
                        fila.getCell(4) == null) {
                    continue;
                }

                Transaccion t = new Transaccion();
                t.setId(UUID.randomUUID());
                t.setArchivoCargado(archivoCargado);
                t.setFilaExcel(numFila++);

                t.setFecha(getDate(fila.getCell(1))); // Columna 1 Fecha
                t.setDescripcion(getString(fila.getCell(2)));// Columna 2 Descripción
                t.setMonto(getBigDecimal(fila.getCell(3))); // Columna 3 Monto
                t.setMoneda(getString(fila.getCell(4)));    // Columna 4 Moneda

                lista.add(t);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }
}