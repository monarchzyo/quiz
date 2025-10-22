package Tennis;
import java.awt.*;
import javax.swing.*;
import java.awt.print.*;
import static java.awt.print.Printable.NO_SUCH_PAGE;
import static java.awt.print.Printable.PAGE_EXISTS;
public class PrintUtilities implements Printable
{
    private final double SCALE_FACTOR = 0.7;
    private Component componentToBePrinted;
    private String studentDetails;
    
    
    public PrintUtilities(Component componentToBePrinted)
    {
        this.componentToBePrinted = componentToBePrinted;
    }
    
    public PrintUtilities(String studentDetails)
    {
        this.componentToBePrinted = null;
        this.studentDetails = studentDetails;
    }
    public static void printComponent(Component c)
    {
        new PrintUtilities(c).print();
    }
    
    public static void printStudentDetails(String studentDetails)
    {
        new PrintUtilities(studentDetails).print();
    }
    public void print() 
    {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        printJob.setPrintable(this);
        if (printJob.printDialog())
            try 
            {
                printJob.print();
            } 
            catch(PrinterException pe) 
            {
                System.out.println("Error printing: " + pe);
            }
    }
     private void printStudentDetailsText(Graphics2D g2d, PageFormat pageFormat) {
        // Set up fonts and colors
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Print header
        g2d.drawString("STUDENT DETAILS - TENNIS PLAYERS QUIZ", 50, 50);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Printed on: " + new java.util.Date(), 50, 70);
        
        // Print student details
        String[] lines = studentDetails.split("\n");
        int y = 100;
        for (String line : lines) {
            g2d.drawString(line, 50, y);
            y += 20;
        }
    }
        @Override
            public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }

                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                if (componentToBePrinted != null) {
                    // Print GUI component
                    g2d.scale(SCALE_FACTOR, SCALE_FACTOR); 
                    disableDoubleBuffering(componentToBePrinted);
                    componentToBePrinted.paint(g2d);
                    enableDoubleBuffering(componentToBePrinted);
                } else if (studentDetails != null) {
                    printStudentDetailsText(g2d, pageFormat);
                }

                return PAGE_EXISTS;
            }
    public static void disableDoubleBuffering(Component c) 
    {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }
    public static void enableDoubleBuffering(Component c) 
    {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }
}