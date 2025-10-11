<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Detalles del archivo</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<h2>Información del archivo</h2>
<p><strong>Nombre:</strong> archivo1.xlsx</p>
<p><strong>Tamaño:</strong> 123 KB</p>
<p><strong>Usuario:</strong> admin</p>

<h3>Contenido</h3>
<table border="1" width="100%">
    <tr>
        <th>Fecha</th>
        <th>Asunto</th>
        <th>Monto</th>
        <th>Moneda</th>
        <th>Clasificación</th>
        <th>Acción</th>
    </tr>
    <tr>
        <td>2025-10-01</td>
        <td>Pago de luz</td>
        <td>45.00</td>
        <td>USD</td>
        <td>Servicios</td>
        <td><a href="editarTransaccion.jsp">Editar</a></td>
    </tr>
    <tr>
        <td>2025-10-02</td>
        <td>Compra oficina</td>
        <td>23.50</td>
        <td>USD</td>
        <td>Insumos</td>
        <td><a href="editarTransaccion.jsp">Editar</a></td>
    </tr>
</table>

<br>
<div style="display: flex; gap: 10px;">
    <a href="agregarTransaccion.jsp">
        <button>Agregar nueva transacción</button>
    </a>
    <form action="eliminarArchivo.jsp" method="post" onsubmit="return confirm('¿Seguro que deseas eliminar este archivo?');">
        <input type="hidden" name="archivo" value="archivo1.xlsx">
        <button type="submit" style="background-color:#d9534f; color:white;">Eliminar archivo</button>
    </form>
</div>

<br><br>
<a href="index.jsp">Volver a la lista</a>
</body>
</html>
