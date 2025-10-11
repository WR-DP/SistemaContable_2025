<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Editar transacción</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<h2>Editar transacción</h2>

<form action="detallesArchivo.jsp" method="post">
    <fieldset>
        <legend>Información general</legend>

        <label for="id">ID:</label><br>
        <input type="text" id="id" name="id" value="1" readonly><br><br>

        <label for="fecha">Fecha:</label><br>
        <input type="date" id="fecha" name="fecha" value="2025-10-01" required><br><br>

        <label for="descripcion">Descripción / Concepto:</label><br>
        <input type="text" id="descripcion" name="descripcion" value="Pago de luz" required><br><br>

        <label for="monto">Monto:</label><br>
        <input type="number" id="monto" name="monto" step="0.01" value="45.00" required><br><br>

        <label for="moneda">Moneda:</label><br>
        <select id="moneda" name="moneda" required>
            <option value="USD" selected>USD</option>
            <option value="EUR">EUR</option>
            <option value="BTC">BTC</option>
        </select><br><br>
    </fieldset>

    <fieldset>
        <legend>Clasificación</legend>

        <label for="tipo">Tipo:</label><br>
        <input type="text" id="tipo" name="tipo" value="Gasto"><br><br>

        <label for="categoria">Categoría:</label><br>
        <input type="text" id="categoria" name="categoria" value="Servicios"><br><br>

        <label for="cuenta">Cuenta contable:</label><br>
        <input type="text" id="cuenta" name="cuenta" value="501-Gastos operativos"><br><br>

        <label>
            <input type="checkbox" name="autoClasificar" checked> Clasificar automáticamente
        </label><br><br>
    </fieldset>

    <div style="margin-top:15px;">
        <button type="submit">Guardar cambios</button>
        <a href="detallesArchivo.jsp"><button type="button">Cancelar</button></a>
        <button type="button" style="background-color:red;color:white;">Eliminar</button>
    </div>
</form>

</body>
</html>
