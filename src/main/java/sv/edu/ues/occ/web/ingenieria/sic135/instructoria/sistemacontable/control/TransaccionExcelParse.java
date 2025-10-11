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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class TransaccionExcelParse {

    /**
     * Lee un archivo Excel (.xlsx) con el formato esperado y devuelve una lista de transacciones.
     *
     * @param archivo        Ruta absoluta del archivo Excel a parsear.
     * @param archivoCargado Entidad ArchivoCargado asociada para enlazar con cada transacción.
     * @return Lista de transacciones extraídas del archivo.
     */
    public List<Transaccion> parsearExcel(String archivo, ArchivoCargado archivoCargado) {
        List<Transaccion> transacciones = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(archivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet hoja = workbook.getSheetAt(0); // solo la primera hoja de momento luego podemos ampliar
            Iterator<Row> filas = hoja.iterator();

            // Ignorar la primera fila (encabezados)
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
                    t.setFecha(cellFecha.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                } else if (cellFecha != null && cellFecha.getCellType() == CellType.STRING) {
                    t.setFecha(LocalDate.parse(cellFecha.getStringCellValue()));
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