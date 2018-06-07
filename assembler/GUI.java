package assembler;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;



public class GUI  implements ActionListener{
	public JFrame frame;
	boolean fileSelected=false;
	
	  
	   
	JPanel bar = new JPanel();
	JButton open = new JButton("open");
	JButton generate = new JButton("generate");
	JTextArea t = new JTextArea("instrutions \n 1- open file.m \n 2- press generate and select files directory ");
	
	String input;
	String name;
	String objFile;
	String txtFile;
	String interFile;
	GUI(){
		
		open.addActionListener(this);
		generate.addActionListener(this);
		run();
		
	
	}
	
	 public void run() {
	        frame = new JFrame("sic As");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        Container pane = frame.getContentPane();
	        bar.add(open);
	        bar.add(generate);
	        pane.add(bar,BorderLayout.NORTH);
	        pane.add(t);
            
	        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	        frame.pack();
	        frame.setVisible(true);

	    }
	@Override
	public void actionPerformed(ActionEvent ae) {
		String action = ae.getActionCommand();
		
        if (action.equals("open")) {
        	JFileChooser fileChooser = new JFileChooser();
    		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
    		fileChooser.setDialogTitle("choose input file");
    		int result = fileChooser.showOpenDialog(frame);
    		if (result == JFileChooser.APPROVE_OPTION) {
    		    File selectedFile = fileChooser.getSelectedFile();
    		    
    		    input=selectedFile.getAbsolutePath();
    		    fileSelected=true;
    		    String nameEx = selectedFile.getName();
    		    name=nameEx.substring(0, nameEx.lastIndexOf('.'));
    		    
        }
		}else if(action.equals("generate")){
        	if(fileSelected){
        		JFileChooser chooser = new JFileChooser(); 
        		    chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        		    chooser.setDialogTitle("choose directory");
        		    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        		   
        		    chooser.setAcceptAllFileFilterUsed(false);
        		     
        		    if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) { 
        		      
        		      
        		      }
        		    String Path = chooser.getSelectedFile().getAbsolutePath();
        		    objFile=Path+File.separator+name+" objectFile"+".obj";
        		    txtFile=Path+File.separator+name+" listingFile"+".txt";
        		    interFile=Path+File.separator+name+" intermidFile"+".txt";
        		    try {
						FileOutputStream listSave = new FileOutputStream(txtFile);
						listSave.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		    try {
						FileOutputStream objSave = new FileOutputStream(objFile);
						objSave.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		    try {
						FileOutputStream interSave = new FileOutputStream(interFile);
						interSave.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        		  // here is your main  
        		    Pass1 k = new Pass1(input,objFile,txtFile,interFile);
        			k.run();
        			 JOptionPane.showConfirmDialog(frame,
                             "generated", "success",JOptionPane.DEFAULT_OPTION);
        			 frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        		    
        	}
        	else{
        		
        		  JOptionPane.showConfirmDialog(frame,
                         "input file not opened please open it", "Error",JOptionPane.DEFAULT_OPTION);
        	}
        	
        }
        
	}
	
	public static void main(String[] args) {
		new GUI();

	}

}
