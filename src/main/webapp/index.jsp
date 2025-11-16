<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sistema Contable - UES</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

    <style>
        /* Variables CSS */
        :root {
            --primary: #0d5a36;
            --primary-light: #16a34a;
            --accent: #22c55e;
            --dark: #0a4429;
            --shadow-strong: 0 10px 40px rgba(0,0,0,0.4);
            --shadow-soft: 0 4px 20px rgba(0,0,0,0.3);
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
            background-image: linear-gradient(rgba(10,68,41,0.4), rgba(13,90,54,0.6)),
            url("resources/imagenes/pexels-olia-danilevich-5466809.jpg");
            background-size: cover;
            background-position: center;
            background-attachment: fixed;
            background-repeat: no-repeat;
            color: #ffffff;
            min-height: 100vh;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            position: relative;
            overflow-x: hidden;
        }

        /* Overlay con blur */
        body::before {
            content: '';
            position: fixed;
            inset: 0;
            backdrop-filter: blur(2px);
            z-index: 0;
        }

        /* Contenedor principal */
        .main-container {
            position: relative;
            z-index: 1;
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 1.5rem;
            padding: 2rem;
            animation: fadeInUp 0.8s ease-out;
            max-width: 900px;
            width: 100%;
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* Título con ícono a la izquierda */
        .title-container {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 1.25rem;
            background: linear-gradient(135deg, rgba(13,90,54,0.95), rgba(22,163,74,0.9));
            padding: 1.25rem 2.5rem;
            border-radius: 16px;
            box-shadow: var(--shadow-strong);
            backdrop-filter: blur(10px);
            border: 2px solid rgba(255,255,255,0.2);
            animation: pulse 2s infinite;
        }

        @keyframes pulse {
            0%, 100% {
                box-shadow: 0 10px 40px rgba(34,197,94,0.3);
            }
            50% {
                box-shadow: 0 15px 50px rgba(34,197,94,0.5);
            }
        }

        .title-container i {
            font-size: 2.5rem;
            color: #fbbf24;
            filter: drop-shadow(0 4px 8px rgba(0,0,0,0.4));
            animation: float 3s ease-in-out infinite;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0); }
            50% { transform: translateY(-8px); }
        }

        /* Título principal */
        h1 {
            font-size: clamp(1.5rem, 3.5vw, 2.25rem);
            font-weight: 700;
            color: #ffffff;
            text-shadow: 0 3px 10px rgba(0,0,0,0.5);
            letter-spacing: 0.5px;
            margin: 0;
        }

        /* Card principal más compacta */
        .welcome-card {
            background: linear-gradient(135deg,
            rgba(255,255,255,0.15),
            rgba(255,255,255,0.08)
            );
            backdrop-filter: blur(20px) saturate(180%);
            border-radius: 20px;
            padding: 2rem 2.5rem;
            width: 100%;
            max-width: 600px;
            box-shadow: var(--shadow-strong);
            border: 1px solid rgba(255,255,255,0.25);
            position: relative;
            overflow: hidden;
        }

        .welcome-card::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 4px;
            background: linear-gradient(90deg, #22c55e, #16a34a, #0d5a36, #16a34a, #22c55e);
            background-size: 200% 100%;
            animation: gradient-shift 3s linear infinite;
        }

        @keyframes gradient-shift {
            0% { background-position: 0% 50%; }
            100% { background-position: 200% 50%; }
        }

        p.description {
            font-size: 1.0625rem;
            line-height: 1.6;
            color: rgba(255,255,255,0.95);
            margin: 0 0 1.75rem 0;
            text-shadow: 0 2px 8px rgba(0,0,0,0.4);
            font-weight: 400;
            text-align: center;
        }

        /* Iconos de características - más compactos */
        .features-icons {
            display: flex;
            justify-content: center;
            gap: 2rem;
            margin: 1.5rem 0;
            flex-wrap: wrap;
        }

        .feature-item {
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 0.625rem;
            transition: transform 0.3s ease;
        }

        .feature-item:hover {
            transform: translateY(-8px);
        }

        .feature-icon {
            width: 70px;
            height: 70px;
            background: linear-gradient(135deg, rgba(34,197,94,0.25), rgba(22,163,74,0.15));
            border-radius: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            border: 2px solid rgba(255,255,255,0.3);
            box-shadow: 0 6px 20px rgba(0,0,0,0.25);
            transition: all 0.3s ease;
        }

        .feature-item:hover .feature-icon {
            background: linear-gradient(135deg, rgba(34,197,94,0.4), rgba(22,163,74,0.25));
            box-shadow: 0 10px 28px rgba(34,197,94,0.4);
            transform: rotate(5deg);
        }

        .feature-icon i {
            font-size: 2rem;
            color: #fbbf24;
            filter: drop-shadow(0 2px 6px rgba(0,0,0,0.4));
        }

        .feature-label {
            font-size: 0.8125rem;
            font-weight: 600;
            color: rgba(255,255,255,0.9);
            text-shadow: 0 2px 4px rgba(0,0,0,0.4);
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        /* Botón centrado */
        .btn-container {
            margin-top: 1.25rem;
            display: flex;
            justify-content: center;
            width: 100%;
        }

        .btn-container form {
            display: flex;
            justify-content: center;
            width: 100%;
        }

        .btn-iniciar {
            background: linear-gradient(135deg, #22c55e, #16a34a);
            color: #ffffff;
            border: none;
            padding: 0.9375rem 3rem;
            font-size: 1.0625rem;
            font-weight: 700;
            cursor: pointer;
            border-radius: 12px;
            transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
            box-shadow: 0 6px 20px rgba(34,197,94,0.35),
            inset 0 -3px 12px rgba(0,0,0,0.15);
            text-transform: uppercase;
            letter-spacing: 0.8px;
            position: relative;
            overflow: hidden;
        }

        .btn-iniciar::before {
            content: '';
            position: absolute;
            top: 50%;
            left: 50%;
            width: 0;
            height: 0;
            border-radius: 50%;
            background: rgba(255,255,255,0.3);
            transform: translate(-50%, -50%);
            transition: width 0.6s, height 0.6s;
        }

        .btn-iniciar:hover::before {
            width: 300px;
            height: 300px;
        }

        .btn-iniciar:hover {
            background: linear-gradient(135deg, #16a34a, #15803d);
            transform: translateY(-3px);
            box-shadow: 0 10px 30px rgba(34,197,94,0.5),
            inset 0 -3px 12px rgba(0,0,0,0.2);
        }

        .btn-iniciar:active {
            transform: translateY(-1px);
            box-shadow: 0 5px 18px rgba(34,197,94,0.4);
        }

        .btn-iniciar i {
            margin-left: 0.5rem;
            transition: transform 0.3s ease;
        }

        .btn-iniciar:hover i {
            transform: translateX(5px);
        }

        /* Footer mejorado */
        footer {
            position: fixed;
            bottom: 0;
            left: 0;
            right: 0;
            padding: 1rem;
            text-align: center;
            background: linear-gradient(180deg, transparent, rgba(10,68,41,0.8));
            backdrop-filter: blur(8px);
            color: rgba(255,255,255,0.85);
            font-size: 0.875rem;
            text-shadow: 0 1px 4px rgba(0,0,0,0.6);
            z-index: 10;
            border-top: 1px solid rgba(255,255,255,0.1);
        }

        footer strong {
            color: #fbbf24;
            font-weight: 600;
        }

        /* Responsive */
        @media (max-width: 768px) {
            .main-container {
                padding: 1.5rem 1rem;
            }

            .title-container {
                padding: 1rem 1.75rem;
                gap: 1rem;
            }

            .title-container i {
                font-size: 2rem;
            }

            h1 {
                font-size: 1.375rem;
            }

            .welcome-card {
                padding: 1.5rem 1.75rem;
            }

            p.description {
                font-size: 1rem;
            }

            .features-icons {
                gap: 1.5rem;
            }

            .feature-icon {
                width: 60px;
                height: 60px;
            }

            .feature-icon i {
                font-size: 1.75rem;
            }

            .feature-label {
                font-size: 0.75rem;
            }

            .btn-iniciar {
                padding: 0.875rem 2.25rem;
                font-size: 1rem;
            }

            footer {
                font-size: 0.75rem;
                padding: 0.875rem;
            }
        }

        @media (max-width: 480px) {
            .title-container {
                flex-direction: column;
                gap: 0.75rem;
                padding: 1rem 1.5rem;
            }

            .features-icons {
                gap: 1.25rem;
            }

            .btn-iniciar {
                width: 100%;
            }
        }

        /* Animación de entrada escalonada */
        .title-container {
            animation: fadeInUp 0.6s ease-out 0.1s backwards;
        }

        .welcome-card {
            animation: fadeInUp 0.6s ease-out 0.3s backwards;
        }

        footer {
            animation: fadeIn 0.8s ease-out 0.5s backwards;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }
    </style>
</head>

<body>
<div class="main-container">
    <!-- Título con ícono a la izquierda -->
    <div class="title-container">
        <i class="fas fa-calculator"></i>
        <h1>Sistema Contable UES</h1>
    </div>

    <!-- Card principal -->
    <div class="welcome-card">
        <p class="description">
            Administre, registre y controle sus operaciones contables de manera eficiente y profesional.
        </p>

        <!-- Iconos de características -->
        <div class="features-icons">
            <div class="feature-item">
                <div class="feature-icon">
                    <i class="fas fa-file-invoice-dollar"></i>
                </div>
                <span class="feature-label">Facturas</span>
            </div>

            <div class="feature-item">
                <div class="feature-icon">
                    <i class="fas fa-chart-line"></i>
                </div>
                <span class="feature-label">Reportes</span>
            </div>

            <div class="feature-item">
                <div class="feature-icon">
                    <i class="fas fa-university"></i>
                </div>
                <span class="feature-label">Gestión</span>
            </div>
        </div>

        <!-- Botón centrado -->
        <div class="btn-container">
            <form action="paginas/archivoCargado.jsf" method="get">
                <button class="btn-iniciar" type="submit">
                    Iniciar Sistema
                    <i class="fas fa-arrow-right"></i>
                </button>
            </form>
        </div>
    </div>
</div>

<!-- Footer -->
<footer>
    <strong>Facultad Multidisciplinaria de Occidente</strong><br>
    Universidad de El Salvador © 2025
</footer>
</body>
</html>