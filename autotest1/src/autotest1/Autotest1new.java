/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package autotest1;

 
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.util.List;
import java.util.*;
import java.util.logging.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.remote.DesiredCapabilities; 
import java.net.MalformedURLException;
import java.net.URL;


public class Autotest1new  extends JFrame implements ActionListener
  {
  //variables for the gui
  JButton SUBMIT;
  JPanel panel;
  JLabel label1,label2;
  final JTextField  text1,text2;
   
  //variables for the chrome driver
  ChromeOptions options;
  DesiredCapabilities capabilities;
  WebDriver driver;
    
  JavascriptExecutor js = null;
  List<String> friend_list;
  List<String> profile_image_list = new ArrayList<>();
  private final String PROFILE_DESTINATION_PATH = "C:\\Users\\Solomon\\Documents\\NetBeansProjects\\autotest1\\profilepic";
    
   Autotest1new()
   {
   //initilize gui elements    
   label1 = new JLabel();
   label1.setText("Username:");
   text1 = new JTextField(15);
 
   label2 = new JLabel();
   label2.setText("Password:");
   text2 = new JPasswordField(15);
  
   SUBMIT=new JButton("SUBMIT");
   
   panel=new JPanel(new GridLayout(3,1));
   panel.add(label1);
   panel.add(text1);
   panel.add(label2);
   panel.add(text2);
   panel.add(SUBMIT);
   add(panel,BorderLayout.CENTER);
   SUBMIT.addActionListener(this);
   setTitle("LOGIN FORM");
    
  // System.setProperty("webdriver.chrome.driver", "c:\\chromedriver.exe");
        options = new ChromeOptions();
        
        // grant access to all media devices
        options.addArguments("--use-fake-ui-for-media-stream=1");
        
        // add ultra-surf extension
        options.addExtensions(new File("ultra_surf.crx"));
        
        // disable notifications
        options.addArguments("--disable-notifications");
 
          
   
   }
   public void getFriends(String phone,String pass) throws IOException{
      System.setProperty("webdriver.chrome.driver", "c:\\chromedriver.exe");
      driver = new ChromeDriver(options);
      driver.get("http://facebook.com");
      if (driver instanceof JavascriptExecutor) {
            js = (JavascriptExecutor)driver;
        }
      
      //login into facebook
        driver.findElement(By.id("email")).sendKeys(phone);
        driver.findElement(By.id("pass")).sendKeys(pass);
        driver.findElement(By.id("loginbutton")).click();
        
        try {
          Thread.sleep(1000);
        } catch (InterruptedException ex) {
          Logger.getLogger(Autotest1new.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        WebElement profile =driver.findElement(By.xpath("//a[@title='Profile']"));
        profile.click();
       
        try{
         Thread.sleep(1000);
        }catch(Exception e){
       }
         
        // friends profile page
        WebElement profile_link = driver.findElement(By.cssSelector("a[title='Profile']"));
        driver.get(profile_link.getAttribute("href"));
        
        StringBuilder add_info = new StringBuilder();
        List<WebElement> additional_info = driver.findElements(By.cssSelector("#u_0_21 ._50f3"));//the intro section get the personal info
            additional_info.forEach((webElement) -> {
                add_info.append(",").append(webElement.getText().replaceAll(",",""));
                System.out.println(webElement.getText().replaceAll(",",""));
            });
        
        // click the friends tab
        WebElement friends_tab = driver.findElement(By.cssSelector("a[data-tab-key='friends']"));
        String total_friends = friends_tab.getText();
        driver.get(friends_tab.getAttribute("href"));
        
        // getting the personal information information
        String user_name = driver.findElement(By.id("fb-timeline-cover-name")).getText();
        StringBuilder personal_info = new StringBuilder();
        personal_info.append("Email,Name,Total Friends,University,School,City,Relationship,Town\n");
        personal_info.append(phone).append(",");
        personal_info.append(user_name).append(",");
        personal_info.append(total_friends.replaceAll("\\D+",""));
        personal_info.append(add_info.toString());
        
        File basic_dir = new File("./");
        basic_dir.mkdirs();
        File basic_file = new File(basic_dir, "basic_information.csv");
        try (FileWriter file_writer = new FileWriter(basic_file)) {
            file_writer.write(personal_info.toString());
        }
                
                
        // scroll down to get frinds
        boolean has_more = true; 
        while (has_more == true) {
            
          try {
              Thread.sleep(3000);
          } catch (InterruptedException ex) {
              Logger.getLogger(Autotest1new.class.getName()).log(Level.SEVERE, null, ex);
          }
            js.executeScript("window.scroll(0,40)");
            try {
                driver.findElement(By.cssSelector("img._359.img.async_saving"));
                js.executeScript("console.log('Passed')");
                
            } catch (Exception e) {
                has_more = false;
            }
           
        } 
        
        // retrieving all friends
        List<WebElement> friends_block = driver.findElements(By.cssSelector("#pagelet_timeline_medley_friends #collection_wrapper_2356318349 ul li"));
        friend_list = new ArrayList<>();
        for (WebElement webElement : friends_block) {
            try {
                //select the name of each firend and thier link
                String friend_link = webElement.findElement(By.cssSelector("div.fsl.fwb.fcb a")).getAttribute("href");
                String friend_name = webElement.findElement(By.cssSelector("div.fsl.fwb.fcb a")).getText();             
               
                friend_list.add(friend_link);//add each links to the list friend list
                
                System.out.println(webElement.findElement(By.cssSelector("div.fsl.fwb.fcb a")).getAttribute("href") );
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }            
        }
        String name[];
        StringBuilder friend_detail = new StringBuilder();
        for (String friend_link : friend_list) {
            Map<String, String> friend_info = getFriendsProfile(friend_link);
            System.out.println("profile link: " + friend_info.get("profile_link"));
            if(friend_info.get("profile_link") instanceof String)
                profile_image_list.add(friend_info.get("profile_link"));
            try{
                name=friend_info.get("name").split("\n");
                saveProfilePic(friend_info.get("profile_link"),name[0]);
            }catch(Exception e){
            
            }
            friend_detail.append(friend_info.get("name"));
            friend_detail.append(friend_info.get("work_info"));
            friend_detail.append("\n");            
        }

        File friend_dir = new File("./");
        basic_dir.mkdirs();
        File friends_file = new File(friend_dir, "friends_list.csv");
        try (FileWriter friend_writer = new FileWriter(friends_file)) {
            friend_writer.write(friend_detail.toString());
        } catch (IOException ex) {
            Logger.getLogger(Autotest1new.class.getName()).log(Level.SEVERE, null, ex);
        }
}
    public void saveProfilePic(String link,String name) throws MalformedURLException, IOException{
        URL url = new URL(link);
        BufferedImage img = ImageIO.read(url);
        File file = new File(PROFILE_DESTINATION_PATH+"\\"+name+".jpg");
        ImageIO.write(img, "jpg", file);
  }
  
   
    protected Map<String, String> getFriendsProfile(String account_link){
        driver.get(account_link);
        Map<String, String> profile_info =  new HashMap<>();   
        
        profile_info.put("name", driver.findElement(By.cssSelector("#fb-timeline-cover-name")).getText());
        profile_info.put("profile_link", driver.findElement(By.cssSelector("img.profilePic.img")).getAttribute("src"));
          
        StringBuilder add_info = new StringBuilder();
        List<WebElement> additional_info = driver.findElements(By.cssSelector("#intro_container_id ul li ._50f3"));
         
        additional_info.forEach((webElement) -> {
            
          
            add_info.append(",").append(webElement.getText().replaceAll(",","") );
        });        
        profile_info.put("work_info", add_info.toString());
        
        return profile_info;
    }
    public static void main(String[] args) {
          
         try
   {
   Autotest1new frame=new Autotest1new();
   frame.setSize(300,100);
   frame.setVisible(true);
   }
   catch(Exception e)
   {JOptionPane.showMessageDialog(null, e.getMessage());}
   }
    

    @Override
    public void actionPerformed(ActionEvent e) {
      try {
          String value1=text1.getText();
          String value2=text2.getText();;
          getFriends(value1,value2);
      } catch (IOException ex) {
          Logger.getLogger(Autotest1new.class.getName()).log(Level.SEVERE, null, ex);
      }
   
 }  }