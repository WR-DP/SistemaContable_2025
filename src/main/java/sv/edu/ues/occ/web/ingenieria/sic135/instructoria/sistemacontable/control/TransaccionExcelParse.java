package sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.control;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.ArchivoCargado;
import sv.edu.ues.occ.web.ingenieria.sic135.instructoria.sistemacontable.entity.Transaccion;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@ApplicationScoped
public class TransaccionExcelParse {

    public List<Transaccion> parsearExcel(String archivo, ArchivoCargado archivoCargado) {
        List<Transaccion> transacciones = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet hoja = workbook.getSheetAt(0);
            Iterator<Row> filas = hoja.iterator();

            // Ignorar encabezado
            if (filas.hasNext()) filas.next();

            int numFila = 1;
            while (filas.hasNext()) {
                Row fila = filas.next();

                Transaccion t = new Transaccion();
                t.setId(UUID.randomUUID());
                t.setArchivoCargado(archivoCargado);
                t.setFilaExcel(numFila++);

                // Leer columnas
                Cell cellFecha = fila.getCell(0);
                Cell cellDesc = fila.getCell(1);
                Cell cellMonto = fila.getCell(2);
                Cell cellMoneda = fila.getCell(3);

                // Fecha
                if (cellFecha != null && cellFecha.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cellFecha)) {
                    // Excel ya devuelve un java.util.Date compatible con tu entidad
                    t.setFecha(cellFecha.getDateCellValue());
                } else if (cellFecha != null && cellFecha.getCellType() == CellType.STRING) {
                    // Convierte texto tipo "2025-11-10" a java.sql.Date (subtipo de java.util.Date)
                    t.setFecha(java.sql.Date.valueOf(cellFecha.getStringCellValue()));
                }




                // Descripción
                if (cellDesc != null) {
                    t.setDescripcion(cellDesc.getStringCellValue());
                }

                // Monto
                if (cellMonto != null) {
                    BigDecimal monto;
                    switch (cellMonto.getCellType()) {
                        case NUMERIC -> monto = BigDecimal.valueOf(cellMonto.getNumericCellValue());
                        case STRING -> monto = new BigDecimal(cellMonto.getStringCellValue());
                        default -> monto = BigDecimal.ZERO;
                    }
                    t.setMonto(monto);
                }

                // Moneda
                if (cellMoneda != null) {
                    t.setMoneda(cellMoneda.getStringCellValue());
                }

                transacciones.add(t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transacciones;
    }
}
