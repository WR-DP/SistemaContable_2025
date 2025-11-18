<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Prueba de filtrado backend</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        body { font-family: Arial, Helvetica, sans-serif; }
        .row { display:flex; gap:12px; flex-wrap:wrap; align-items:flex-end; margin: 10px 0; }
        .field { display:flex; flex-direction:column; }
        label { font-weight:600; }
        input[type="text"], input[type="number"], select { padding:6px; min-width: 180px; }
        button { padding:8px 14px; }
        .help { color:#666; font-size: .9em; }
        .examples a { display:inline-block; margin-right:10px; }
    </style>
</head>
<body>
<h2>Probar filtrado de transacciones (backend)</h2>
<p class="help">Esta página no cambia las vistas existentes. Solo envía parámetros al endpoint <code>/archivo</code> para probar el filtrado implementado en el backend.</p>

<form method="get" action="<%=request.getContextPath()%>/archivo">
    <div class="row">
        <div class="field" style="min-width:380px;">
            <label for="id">UUID de ArchivoCargado (id)</label>
            <input type="text" id="id" name="id" placeholder="xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx" required>
        </div>
        <div class="field">
            <label for="periodo">Periodo</label>
            <select id="periodo" name="periodo">
                <option value="">Todos</option>
                <option value="mensual">Mensual</option>
                <option value="trimestral">Trimestral</option>
                <option value="anual">Anual</option>
            </select>
        </div>
        <div class="field">
            <label for="anio">Año</label>
            <input type="number" id="anio" name="anio" min="1900" max="2100" placeholder="2025">
        </div>
        <div class="field">
            <label for="mes">Mes (1-12)</label>
            <input type="number" id="mes" name="mes" min="1" max="12" placeholder="10">
        </div>
        <div class="field">
            <label for="trimestre">Trimestre</label>
            <select id="trimestre" name="trimestre">
                <option value="">--</option>
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
            </select>
        </div>
        <div class="field">
            <button type="submit">Probar</button>
        </div>
    </div>
    <div class="help">Reglas: mensual requiere año y mes; trimestral requiere año y trimestre; anual requiere año. Si faltan datos se listan todas las transacciones.</div>
</form>

<hr>
<div class="examples">
    <div class="help">Enlaces de ejemplo (reemplaza <code>UUID_AQUI</code> por uno válido de tu base de datos):</div>
    <ul>
        <li>
            <a href="<%=request.getContextPath()%>/archivo?id=UUID_AQUI">Todas las transacciones del archivo</a>
        </li>
        <li>
            <a href="<%=request.getContextPath()%>/archivo?id=UUID_AQUI&periodo=mensual&anio=2025&mes=10">Mensual - 2025/10</a>
        </li>
        <li>
            <a href="<%=request.getContextPath()%>/archivo?id=UUID_AQUI&periodo=trimestral&anio=2025&trimestre=4">Trimestral - 2025 Q4</a>
        </li>
        <li>
            <a href="<%=request.getContextPath()%>/archivo?id=UUID_AQUI&periodo=anual&anio=2025">Anual - 2025</a>
        </li>
    </ul>
</div>

</body>
</html>

