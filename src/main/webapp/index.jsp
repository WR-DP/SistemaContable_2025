<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Página principal</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<h2>Subir archivo Excel</h2>
<form method="post" enctype="multipart/form-data">
    <input type="file" name="archivoExcel">
    <button type="submit">Cargar archivo</button>
</form>

<h3>Lista de archivos</h3>
<table border="1" width="60%">
    <tr>
        <th>Nombre del archivo</th>
    </tr>
    <tr>
        <td><a href="detallesArchivo.jsp" style="text-decoration:none; color:#007bff;">archivo1.xlsx</a></td>
    </tr>
    <tr>
        <td><a href="detallesArchivo.jsp" style="text-decoration:none; color:#007bff;">archivo2.xlsx</a></td>
    </tr>
</table>
</body>
</html>
