/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package sistemacentral;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class SistemaCentral {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Si se pasa el argumento "console", ejecutar en modo consola
        if (args.length > 0 && args[0].equals("console")) {
            System.out.println("Ejecutando en modo consola...");
            MailApplication app = new MailApplication();
            app.start();
        } else {
            // Por defecto, ejecutar con interfaz gráfica
            System.out.println("Iniciando interfaz gráfica...");

            // Configurar Look and Feel nativo del sistema
            try {
                // Busca Nimbus entre los disponibles
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
                System.out.println("Look and Feel configurado correctamente");
            } catch (Exception e) {
                System.err.println("No se pudo configurar Look and Feel: " + e.getMessage());
            }

            // Ejecutar la GUI en el Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                try {
                    SistemaCentralGUI gui = new SistemaCentralGUI();
                    gui.setVisible(true);
                    System.out.println("Interfaz gráfica iniciada correctamente");
                } catch (Exception e) {
                    System.err.println("Error al iniciar la interfaz gráfica: " + e.getMessage());
                    e.printStackTrace();

                    // Fallback a modo consola si falla la GUI
                    System.out.println("🔄 Ejecutando en modo consola como respaldo...");
                    MailApplication app = new MailApplication();
                    app.start();
                }
            });
        }
    }
}