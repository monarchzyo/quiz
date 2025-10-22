package Tennis;
import java.awt.*;
import javax.swing.*;
public class Splash extends JWindow
{
    private final int duration;
    private JProgressBar loading;
    private int progress;
    public Splash(int dur)
    {
        duration = dur;
        UIManager.put("ProgressBar.selectionForeground", Color.gray.darker());
        loading = new JProgressBar(0, 100);
        loading.setStringPainted(true);
        showSplash();
    }
    public void showSplash()
    {
        JPanel content = (JPanel)getContentPane();
        content.setBackground(Color.lightGray);
        int width = 660;
        int height = 530;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width)/2;
        int y = (screen.height - height)/2;
        setBounds(x,y,width,height);
        
        
        JLabel title = new JLabel("Tennis Players Quiz", JLabel.CENTER);
        title.setFont(new Font("Sans-Serif", Font.BOLD, 28));
        title.setForeground(new Color(0, 100, 0));
        
        JLabel imageLabel = new JLabel(new ImageIcon(temp)); //REPLACE
        
        
        content.setLayout(new BorderLayout());
        content.add(title, BorderLayout.NORTH);
        content.add(imageLabel, BorderLayout.CENTER);
        content.add(loading, BorderLayout.SOUTH);
        
        Color border = new Color(0, 128, 0); // Tennis green border
        content.setBorder(BorderFactory.createLineBorder(border, 10));

        setVisible(true);
        try 
        {
            incProgress(20);
            Thread.sleep(duration);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        setVisible(false);
    }
    public void incProgress(int amount)
    {
        ProgressThread up = new ProgressThread(amount);
        up.thread.start();
    }
    class ProgressThread 
    {
        private int amount;
        public ProgressThread(int amount)
        {
            this.amount = amount;
        }

        private Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                {
                    while (progress < 100) 
                    {
                        progress++;
                        loading.setString(String.valueOf(progress) + "%");
                        try 
                        {
                            Thread.sleep(30);
                        } 
                        catch (InterruptedException ex) 
                        {

                        }
                        loading.setValue(progress);
                    }
                }
            }
        });
    }
}