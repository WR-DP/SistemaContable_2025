<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Agregar transacción</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<h2>Agregar nueva transacción</h2>

<form action="detallesArchivo.jsp" method="post">
    <fieldset>
        <legend>Información general</legend>

        <label for="fecha">Fecha:</label><br>
        <input type="date" id="fecha" name="fecha" required><br><br>

        <label for="descripcion">Descripción / Concepto:</label><br>
        <input type="text" id="descripcion" name="descripcion" placeholder="Ej: Pago de luz" required><br><br>

        <label for="monto">Monto:</label><br>
        <input type="number" id="monto" name="monto" step="0.01" required><br><br>

        <label for="moneda">Moneda:</label><br>
        <select id="moneda" name="moneda" required>
            <option value="">Seleccione</option>
            <option value="USD">USD</option>
            <option value="EUR">EUR</option>
            <option value="BTC">BTC</option>
        </select><br><br>
    </fieldset>

    <fieldset>
        <legend>Clasificación</legend>

        <label for="tipo">Tipo:</label><br>
        <input type="text" id="tipo" name="tipo" placeholder="Ej: Gasto"><br><br>

        <label for="categoria">Categoría:</label><br>
        <input type="text" id="categoria" name="categoria" placeholder="Ej: Servicios públicos"><br><br>

        <label for="cuenta">Cuenta contable:</label><br>
        <input type="text" id="cuenta" name="cuenta" placeholder="Ej: 501-Gastos operativos"><br><br>

        <label>
            <input type="checkbox" name="autoClasificar"> Clasificar automáticamente
        </label><br><br>
    </fieldset>

    <div style="margin-top:15px;">
        <button type="submit">Guardar transacción</button>
        <button type="button" onclick="window.location.href='detallesArchivo.jsp'"
                style="background-color:#ccc; color:#000; margin-left:10px;">
            Cancelar
        </button>
    </div>
</form>
</body>
</html>
