package applications.basics;

/**
  found this on the net, cool and simple

**/

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class SplashScreen extends Window{

// Fredy's make Version
private static String fredysVersion = "Version 1.2.1.a  2000-2-27 11:38 Build: 13";

public String getVersion() {return fredysVersion; }


      JLabel StatusBar;

     // So that it can be run as an application for demonstration
     public static void main(String Args[]){
           SplashScreen Splash= new SplashScreen(new ImageIcon("virago.gif"));
           try{
             Splash.showStatus("Hi!");
             Thread.sleep(1500);
             Splash.showStatus("Splash Screens are cool!");
             Thread.sleep(1500);
             Splash.showStatus("Gives you something interesting...");
             Thread.sleep(1500);
             Splash.showStatus("... to look at...");
             Thread.sleep(1500);
             Splash.showStatus("... during startup!");
             Thread.sleep(1500);
             Splash.close();
           }catch(Exception e){e.printStackTrace();}
           System.exit(0);
     }

   // SplashScreen's constructor
   public SplashScreen(ImageIcon CoolPicture){
    super(new Frame());

    // Create a JPanel so we can use a BevelBorder
     JPanel PanelForBorder=new JPanel(new BorderLayout());
     PanelForBorder.setLayout(new BorderLayout());
     PanelForBorder.add(new JLabel(CoolPicture),
     BorderLayout.CENTER);
     PanelForBorder.add(
     StatusBar=new JLabel("...",SwingConstants.CENTER),
     BorderLayout.SOUTH);
     PanelForBorder.setBorder(new BevelBorder(BevelBorder.RAISED));

     add(PanelForBorder);    
     pack();

     // Plonk it on center of screen
     Dimension WindowSize=getSize(),
                          ScreenSize=Toolkit.getDefaultToolkit().getScreenSize();
     setBounds((ScreenSize.width-WindowSize.width)/2,
               (ScreenSize.height-WindowSize.height)/2,WindowSize.width,
                WindowSize.height);
      setVisible(true);  
   }

   public void showStatus(String CurrentStatus){
     try {
          // Update Splash-Screen's status bar in AWT thread
           SwingUtilities.invokeLater(new UpdateStatus(CurrentStatus));
     }catch(Exception e){e.printStackTrace();}
   }

   public void close() {
      try {
            // Close and dispose Window in AWT thread
            SwingUtilities.invokeLater(new CloseSplashScreen());
       }catch(Exception e){e.printStackTrace();}
   }


    public void setBackGround(Color c) { this.setBackground(c); }

   class UpdateStatus implements Runnable{
         String NewStatus;
         public UpdateStatus(String Status){NewStatus=Status;}
         public void run(){StatusBar.setText(NewStatus);}
   }

    

   class CloseSplashScreen implements Runnable{
         public void run(){setVisible(false);dispose();}
   }
}
