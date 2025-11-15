<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistema Contable - UES</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

    <style>
        body {
            font-family: "Segoe UI", sans-serif;
            background-image: url("resources/imagenes/pexels-olia-danilevich-5466809.jpg");
            background-size: cover;
            background-position: center;
            background-attachment: fixed;
            margin: 0;
            padding: 0;
            color: #ffffff;
            text-align: center;
            height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }

        h1 {
            font-size: 3em;
            background-color: rgba(0, 0, 0, 0.6);
            padding: 15px 30px;
            border-radius: 10px;
            color: #f0f0f0;
            text-shadow: 2px 2px 8px rgba(0, 0, 0, 0.7);
            margin-bottom: 20px;
        }

        p {
            font-size: 1.5em;
            max-width: 600px;
            margin: 0 auto 30px auto;
            background-color: rgba(0, 0, 0, 0.5);
            padding: 10px 20px;
            border-radius: 10px;
            color: #e6e6e6;
        }

        .btn-iniciar {
            background-color: #a93226;
            color: #ffffff;
            border: none;
            padding: 15px 40px;
            font-size: 1.3em;
            cursor: pointer;
            border-radius: 8px;
            transition: all 0.3s ease;
            box-shadow: 0 5px 15px rgba(0,0,0,0.3);
        }

        .btn-iniciar:hover {
            background-color: #922b21;
            transform: scale(1.05);
        }

        footer {
            position: absolute;
            bottom: 10px;
            width: 100%;
            text-align: center;
            color: #dddddd;
            font-size: 0.9em;
            text-shadow: 1px 1px 4px rgba(0, 0, 0, 0.6);
        }

        .icon {
            font-size: 50px;
            margin: 15px;
            color: #f1c40f;
            text-shadow: 2px 2px 6px rgba(0,0,0,0.6);
        }
    </style>
</head>

<body>
<h1><i class="fas fa-calculator"></i> Sistema Contable UES</h1>
<p>Administre, registre y controle sus operaciones contables de manera eficiente.</p>

<div class="icon-container">
    <i class="fas fa-file-invoice-dollar icon"></i>
    <i class="fas fa-chart-line icon"></i>
    <i class="fas fa-university icon"></i>
</div>

<form action="paginas/archivoCargado.jsf" method="get">
    <button class="btn-iniciar">Iniciar Sistema</button>
</form>

<footer>
    Facultad Multidisciplinaria de Occidente - Universidad de El Salvador © 2025
</footer>
</body>
</html>
