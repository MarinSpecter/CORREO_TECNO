package utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.text.html.StyleSheet;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

public class HtmlBuilder {

    private static final String HTML_OPEN = "<html>";
    private static final String HTML_CLOSE = "</html>";
    private static final String BODY_OPEN = "<body>";
    private static final String BODY_CLOSE = "</body>";

    private static final String LOGO = "https://res.cloudinary.com/dcdhuwp0y/image/upload/v1750733014/logo1_agwmct.png";
    private static final String QR_CODE = "https://res.cloudinary.com/dcdhuwp0y/image/upload/v1750628532/QR_nawi7j.jpg";

    // Paleta de colores basada en la imagen proporcionada
    private static final String COLOR_AZUL_MARINO = "#2C3E50";           // Azul marino principal
    private static final String COLOR_AZUL_CLARO = "#4A6FA5";            // Azul claro para headers
    private static final String COLOR_NARANJA = "#FF6B35";               // Naranja vibrante
    private static final String COLOR_GRIS_CLARO = "#E8F1F5";            // Gris azulado claro
    private static final String COLOR_BLANCO = "#FFFFFF";                // Blanco puro
    private static final String COLOR_TEXTO_PRINCIPAL = "#2C3E50";       // Azul marino para texto
    private static final String COLOR_TEXTO_SECUNDARIO = "#5A6C7D";      // Gris azulado para texto secundario
    private static final String COLOR_FILA_ALTERNADA = "#F7F9FC";        // Gris muy claro para filas

    // Fuentes modernas
    private static final String FONT_FAMILY = "'Segoe UI', 'Roboto', 'Helvetica Neue', Arial, sans-serif";

    // Estilos base
    private static final String MODERN_CONTAINER_STYLE =
            "max-width: 900px; margin: 20px auto; padding: 0; " +
                    "background: " + COLOR_NARANJA + "; " +
                    "border-radius: 25px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); " +
                    "font-family: " + FONT_FAMILY + "; position: relative; overflow: hidden;";

    private static final String MODERN_HEADER_STYLE =
            "color: " + COLOR_BLANCO + "; font-size: 48px; font-weight: 700; " +
                    "margin: 0; padding: 40px 40px 20px 40px; text-align: center; " +
                    "background: " + COLOR_NARANJA + "; letter-spacing: -1px;";

    private static final String MODERN_TABLE_STYLE =
            "width: 100%; border-collapse: collapse; margin: 0; " +
                    "background: " + COLOR_BLANCO + "; border-radius: 0 0 25px 25px; overflow: hidden;";

    private static final String INNER_CONTAINER_STYLE =
            "background: " + COLOR_AZUL_MARINO + "; margin: 20px; padding: 30px; " +
                    "border-radius: 20px; box-shadow: inset 0 0 20px rgba(0,0,0,0.3);";

    public static String generateTableHelp(String title, String explanation, String[] headers, List<String[]> data) {
        // Logo moderno
        String logoHtml = "<div style='text-align: center; margin-bottom: 20px;'>" +
                "<img src='" + LOGO + "' alt='Logo' style='max-width: 400px; max-height: 300px; " +
                "border-radius: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.2);'>" +
                "</div>";

        // Headers de tabla siguiendo el estilo de la imagen
        String tableHeadersHtml = "";
        for (String header : headers) {
            tableHeadersHtml += "<th style=\"" +
                    "border: none; padding: 20px 15px; " +
                    "background: " + COLOR_AZUL_CLARO + "; " +
                    "color: " + COLOR_BLANCO + "; font-weight: 600; font-size: 14px; " +
                    "text-align: center; vertical-align: middle; " +
                    "border-right: 2px solid " + COLOR_AZUL_MARINO + ";" +
                    "\">" + header + "</th>";
        }

        // Cuerpo de tabla
        String tableBodyHtml = "";
        int rowNum = 0;
        for (String[] element : data) {
            String rowColor = (rowNum % 2 == 0) ? COLOR_GRIS_CLARO : COLOR_BLANCO;
            tableBodyHtml += "<tr style=\"background-color: " + rowColor + ";\">";
            for (String value : element) {
                tableBodyHtml += "<td style=\"" +
                        "border: none; padding: 15px; " +
                        "color: " + COLOR_TEXTO_PRINCIPAL + "; font-size: 14px; " +
                        "text-align: center; vertical-align: middle; " +
                        "border-right: 1px solid #DDD; border-bottom: 1px solid #DDD;" +
                        "\">" + value + "</td>";
            }
            tableBodyHtml += "</tr>";
            rowNum++;
        }

        // HTML completo siguiendo el diseño de la imagen
        String html =
                "<div style=\"" + MODERN_CONTAINER_STYLE + "\">" +
                        "<h1 style=\"" + MODERN_HEADER_STYLE + "\">" + title + "</h1>" +

                        "<div style=\"" + INNER_CONTAINER_STYLE + "\">" +
                        logoHtml +

                        "<div style='text-align: center; margin-bottom: 20px;'>" +
                        "<p style=\"font-size: 16px; color: " + COLOR_BLANCO + "; " +
                        "line-height: 1.4; margin: 0; font-weight: 400;\">" + explanation + "</p>" +
                        "</div>" +
                        "</div>" +

                        "<table style=\"" + MODERN_TABLE_STYLE + "\">" +
                        "<thead>" + tableHeadersHtml + "</thead>" +
                        "<tbody>" + tableBodyHtml + "</tbody>" +
                        "</table>" +

                        "<div style='text-align: center; padding: 20px; background: " + COLOR_NARANJA + "; " +
                        "border-radius: 0 0 25px 25px; margin-top: -5px;'>" +
                        "<p style='color: " + COLOR_BLANCO + "; font-size: 12px; margin: 0; font-weight: 500;'>" +
                        "Sistema AgroVeterinaria La Fortaleza</p>" +
                        "</div>" +
                        "</div>";

        return insertInHtml(html);
    }

    public static String generateTable(String title, String[] headers, List<String[]> data) {
        // Headers siguiendo el estilo de la imagen
        String tableHeadersHtml = "";
        for (String header : headers) {
            tableHeadersHtml += "<th style=\"" +
                    "border: none; padding: 20px 15px; " +
                    "background: " + COLOR_AZUL_CLARO + "; " +
                    "color: " + COLOR_BLANCO + "; font-weight: 600; font-size: 14px; " +
                    "text-align: center; vertical-align: middle; " +
                    "border-right: 2px solid " + COLOR_AZUL_MARINO + ";" +
                    "\">" + header + "</th>";
        }

        // Cuerpo de tabla
        String tableBodyHtml = "";
        int rowNum = 0;
        for (String[] element : data) {
            String rowColor = (rowNum % 2 == 0) ? COLOR_GRIS_CLARO : COLOR_BLANCO;
            tableBodyHtml += "<tr style=\"background-color: " + rowColor + ";\">";
            for (String value : element) {
                tableBodyHtml += "<td style=\"" +
                        "border: none; padding: 15px; " +
                        "color: " + COLOR_TEXTO_PRINCIPAL + "; font-size: 14px; " +
                        "text-align: center; vertical-align: middle; " +
                        "border-right: 1px solid #DDD; border-bottom: 1px solid #DDD;" +
                        "\">" + value + "</td>";
            }
            tableBodyHtml += "</tr>";
            rowNum++;
        }

        String logoHtml = "<div style='text-align: center; margin-bottom: 20px;'>" +
                "<img src=\"" + LOGO + "\" alt=\"Company Logo\" style=\"max-width: 200px; max-height: 100px; " +
                "border-radius: 10px; box-shadow: 0 5px 15px rgba(0,0,0,0.2);\">" +
                "</div>";

        String html =
                "<div style=\"" + MODERN_CONTAINER_STYLE + "\">" +
                        "<h1 style=\"" + MODERN_HEADER_STYLE + "\">" + title + "</h1>" +

                        "<div style=\"" + INNER_CONTAINER_STYLE + "\">" +
                        logoHtml +
                        "</div>" +

                        "<table style=\"" + MODERN_TABLE_STYLE + "\">" +
                        "<thead>" + tableHeadersHtml + "</thead>" +
                        "<tbody>" + tableBodyHtml + "</tbody>" +
                        "</table>" +

                        "<div style='text-align: center; padding: 20px; background: " + COLOR_NARANJA + "; " +
                        "border-radius: 0 0 25px 25px; margin-top: -5px;'>" +
                        "<p style='color: " + COLOR_BLANCO + "; font-size: 12px; margin: 0; font-weight: 500;'>" +
                        "Datos actualizados • AgroVeterinaria La Fortaleza</p>" +
                        "</div>" +
                        "</div>";

        return insertInHtml(html);
    }

    public static String generateText(String[] args) {
        StringBuilder accumulatedHtml = new StringBuilder(
                "<div style=\"" + MODERN_CONTAINER_STYLE + "\">" +
                        "<h1 style=\"" + MODERN_HEADER_STYLE + "\">📋 Informe de Petición</h1>" +

                        "<div style=\"" + INNER_CONTAINER_STYLE + "\">" +
                        "<div style='text-align: center; margin-bottom: 20px;'>" +
                        "<img src=\"" + LOGO + "\" alt=\"Logo\" style=\"width: 120px; height: 120px; " +
                        "border-radius: 15px; box-shadow: 0 8px 20px rgba(0,0,0,0.3); " +
                        "border: 3px solid " + COLOR_BLANCO + ";\">" +
                        "</div>"
        );

        for (int i = 0; i < args.length; i++) {
            String iconStyle = i == 0 ? "🔥 " : "• ";
            accumulatedHtml.append(
                    "<div style=\"" +
                            "background: " + COLOR_GRIS_CLARO + "; padding: 15px; margin: 10px 0; " +
                            "border-radius: 10px; border-left: 4px solid " + COLOR_NARANJA + "; " +
                            "box-shadow: 0 2px 8px rgba(0,0,0,0.1);" +
                            "\">" +
                            "<p style=\"" +
                            "color: " + COLOR_TEXTO_PRINCIPAL + "; font-size: 14px; " +
                            "line-height: 1.4; margin: 0; font-weight: 500;" +
                            "\">" + iconStyle + args[i] + "</p>" +
                            "</div>"
            );
        }

        accumulatedHtml.append(
                "</div>" +
                        "<div style='text-align: center; padding: 20px; background: " + COLOR_NARANJA + "; " +
                        "border-radius: 0 0 25px 25px; margin-top: -5px;'>" +
                        "<p style='color: " + COLOR_BLANCO + "; font-size: 12px; margin: 0; font-weight: 500;'>" +
                        "✨ Procesado por Sistema AgroVeterina La Fortaleza</p>" +
                        "</div>" +
                        "</div>"
        );

        return insertInHtml(accumulatedHtml.toString());
    }

    public static String generatePaymentReport(String ventaId, String fechaPago, String monto, String metodoPago) {
        String accumulatedHtml =
                "<div style=\"" + MODERN_CONTAINER_STYLE + "\">" +
                        "<div style='position: absolute; top: 15px; right: 15px; " +
                        "background: #27AE60; color: " + COLOR_BLANCO + "; " +
                        "padding: 8px 15px; border-radius: 20px; font-size: 11px; font-weight: 600; " +
                        "text-transform: uppercase; letter-spacing: 0.5px;'>✅ PAGADO</div>" +

                        "<h1 style=\"" + MODERN_HEADER_STYLE + "\">💰 Comprobante de Pago</h1>" +

                        "<div style=\"" + INNER_CONTAINER_STYLE + "\">" +
                        "<div style='text-align: center; margin-bottom: 20px;'>" +
                        "<img src=\"" + LOGO + "\" alt=\"Logo\" style=\"width: 120px; height: 120px; " +
                        "border-radius: 15px; box-shadow: 0 8px 20px rgba(0,0,0,0.3); " +
                        "border: 3px solid " + COLOR_BLANCO + ";\">" +
                        "</div>";

        // Información del pago
        String[][] paymentData = {
                {"🆔 ID de Venta", ventaId},
                {"💵 Monto", "Bs" + monto},
                {"📅 Fecha de Pago", fechaPago},
                {"💳 Método de Pago", metodoPago}
        };

        for (String[] data : paymentData) {
            accumulatedHtml +=
                    "<div style=\"" +
                            "background: " + COLOR_GRIS_CLARO + "; padding: 15px; margin: 10px 0; " +
                            "border-radius: 10px; border-left: 4px solid " + COLOR_NARANJA + "; " +
                            "display: flex; justify-content: space-between; align-items: center;" +
                            "\">" +
                            "<span style=\"color: " + COLOR_TEXTO_SECUNDARIO + "; font-weight: 600; font-size: 14px;\">" + data[0] + "</span>" +
                            "<span style=\"color: " + COLOR_TEXTO_PRINCIPAL + "; font-weight: 700; font-size: 16px;\">" + data[1] + "</span>" +
                            "</div>";
        }

        // QR Code
        accumulatedHtml +=
                "<div style='text-align: center; margin: 20px 0;'>" +
                        "<div style='display: inline-block; padding: 15px; background: " + COLOR_GRIS_CLARO + "; " +
                        "border-radius: 12px; box-shadow: 0 5px 15px rgba(0,0,0,0.1);'>" +
                        "<img src=\"" + QR_CODE + "\" alt=\"QR Code\" style=\"width: 200px; height: 280px; " +
                        "border-radius: 8px;\">" +
                        "<p style='color: " + COLOR_TEXTO_SECUNDARIO + "; font-size: 12px; margin: 10px 0 0 0;'>" +
                        "Escanea para verificar el pago</p>" +
                        "</div>" +
                        "</div>" +
                        "</div>" +

                        "<div style='text-align: center; padding: 20px; background: " + COLOR_NARANJA + "; " +
                        "border-radius: 0 0 25px 25px; margin-top: -5px;'>" +
                        "<p style='color: " + COLOR_BLANCO + "; font-size: 12px; margin: 0; font-weight: 500;'>" +
                        "🔒 Pago procesado de forma segura</p>" +
                        "</div>" +
                        "</div>";

        return insertInHtml(accumulatedHtml);
    }

    public static String generateCharBar(String[] args) {
        String acumulative = "<center><h2>" + args[0] + "</h2></center>";
        for (int i = 1; i < args.length; i++) {
            acumulative += "<center><h3>" + args[i] + "</h3></center>";
        }

        String chartScript = "<script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n"
                + "<canvas id=\"myChart\"></canvas>\n"
                + "<script>\n"
                + "var ctx = document.getElementById('myChart').getContext('2d');\n"
                + "new Chart(ctx, {\n"
                + "type: 'bar',\n"
                + "data: {\n"
                + "labels: ['Red', 'Blue', 'Yellow', 'Green', 'Purple', 'Orange'],\n"
                + "datasets: [{\n"
                + "label: '# of Votes',\n"
                + "data: [12, 19, 3, 5, 2, 3],\n"
                + "borderWidth: 1\n"
                + "}]\n"
                + "},\n"
                + "options: {\n"
                + "scales: {\n"
                + "y: {\n"
                + "beginAtZero: true\n"
                + "}\n"
                + "}\n"
                + "}\n"
                + "});\n"
                + "</script>";

        return insertInHtml(acumulative);
    }

    public static String generateTableForSimpleData(String title, String[] headers, String[] data) {
        String acumulative = "";

        for (int i = 0; i < headers.length; i++) {
            acumulative +=
                    "<tr>" +
                            "<td style=\"" +
                            "color: " + COLOR_BLANCO + "; padding: 15px; " +
                            "border: 1px solid " + COLOR_AZUL_MARINO + "; font-weight: 600; " +
                            "background: " + COLOR_AZUL_CLARO + "; text-align: center;" +
                            "\">" + headers[i] + "</td>" +
                            "<td style=\"" +
                            "color: " + COLOR_TEXTO_PRINCIPAL + "; padding: 15px; " +
                            "border: 1px solid #DDD; font-weight: 500; font-size: 14px; " +
                            "background: " + COLOR_GRIS_CLARO + "; text-align: center;" +
                            "\">" + data[i] + "</td>" +
                            "</tr>";
        }

        String table =
                "<div style=\"" + MODERN_CONTAINER_STYLE + "\">" +
                        "<h1 style=\"" + MODERN_HEADER_STYLE + "\">" + title + "</h1>" +
                        "<table style=\"" +
                        "width: 100%; border-collapse: collapse; " +
                        "background: " + COLOR_BLANCO + "; border-radius: 0 0 25px 25px; overflow: hidden; " +
                        "margin: 0;" +
                        "\">" +
                        acumulative +
                        "</table>" +
                        "<div style='text-align: center; padding: 20px; background: " + COLOR_NARANJA + "; " +
                        "border-radius: 0 0 25px 25px; margin-top: -5px;'>" +
                        "<p style='color: " + COLOR_BLANCO + "; font-size: 12px; margin: 0; font-weight: 500;'>" +
                        "Datos actualizados • AgroVeterinaria La Fortaleza</p>" +
                        "</div>" +
                        "</div>";

        return insertInHtml(table);
    }

    public static String generateGrafica(String title, List<String[]> data) {
        StringBuilder labels = new StringBuilder();
        StringBuilder values = new StringBuilder();

        for (String[] element : data) {
            labels.append("'").append(element[0]).append("',");
            values.append(element[1]).append(",");
        }

        // Eliminar la última coma
        String labelsStr = labels.substring(0, labels.length() - 1);
        String valuesStr = values.substring(0, values.length() - 1);

        // Crear la configuración del gráfico
        String chartConfig = "{type:'pie',data:{labels:[" + labelsStr + "],datasets:[{data:[" + valuesStr + "]}]}}";

        // Codificar la configuración
        String encodedConfig = null;
        try {
            encodedConfig = URLEncoder.encode(chartConfig, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }

        // Generar el HTML para el gráfico
        String graficas =
                "<div style=\"" + MODERN_CONTAINER_STYLE + "\">" +
                        "<h1 style=\"" + MODERN_HEADER_STYLE + "\">📊 " + title + "</h1>" +

                        "<div style=\"" + INNER_CONTAINER_STYLE + "\">" +
                        "<div style='text-align: center; margin-bottom: 20px;'>" +
                        "<img src=\"" + LOGO + "\" alt=\"Logo\" style=\"width: 120px; height: 120px; " +
                        "border-radius: 15px; box-shadow: 0 8px 20px rgba(0,0,0,0.3); " +
                        "border: 3px solid " + COLOR_BLANCO + ";\">" +
                        "</div>" +

                        "<div style='text-align: center; margin: 20px 0;'>" +
                        "<div style='display: inline-block; padding: 15px; background: " + COLOR_GRIS_CLARO + "; " +
                        "border-radius: 12px; box-shadow: 0 5px 15px rgba(0,0,0,0.1);'>" +
                        "<img src=\"https://quickchart.io/chart?c=" + encodedConfig + "\" " +
                        "style=\"max-width: 100%; height: auto; border-radius: 8px;\">" +
                        "</div>" +
                        "</div>" +
                        "</div>" +

                        "<div style='text-align: center; padding: 20px; background: " + COLOR_NARANJA + "; " +
                        "border-radius: 0 0 25px 25px; margin-top: -5px;'>" +
                        "<p style='color: " + COLOR_BLANCO + "; font-size: 12px; margin: 0; font-weight: 500;'>" +
                        "📈 Gráfico generado dinámicamente</p>" +
                        "</div>" +
                        "</div>";

        return insertInHtml(graficas);
    }

    private static String insertInHtml(String data) {
        return HTML_OPEN + BODY_OPEN + data + BODY_CLOSE + HTML_CLOSE;
    }
}