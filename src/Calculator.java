//
// Name: Whitney, Joshua
// Project: #2
// Due: 10/25/2015
// Course: CS-245-01-f15
//
// Description:
// This is a simple integer calculator, witch can do
// basic integer arithmetic (+,-,/,*).

import javax.swing.*;
import java.net.URL;
import java.util.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

public class Calculator implements ActionListener{
	JFrame jfrm;
	URL iconPath;
	JTextField tfield;
	Double op1 = null;
	Double op2 = null;
	String operator = null;
	Integer buffer = null;
	Queue queue = new Queue();
	
	
	Calculator() throws Exception{
//		iconPath = new URL("http://www.cpp.edu/~tvnguyen7/courses/" +
//						   "cs245f15/projs/Calculator.png");
		ImageIcon icon = new ImageIcon("Resources/Calculator.png");
		jfrm = new JFrame("Calculator");
		jfrm.setLayout(new GridLayout(2,1));
		jfrm.setSize(500,500);
		jfrm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jfrm.setIconImage(icon.getImage());
		tfield = new JTextField(10);
		
		//Initialize display
		tfield.setText("0");
		formatText();
		
		JPanel display = new JPanel();
		display.setLayout(new BorderLayout());
		display.add(tfield, BorderLayout.CENTER);
		jfrm.add(display);
		jfrm.add(keypad());
		jfrm.setVisible(true);
	}
	
	/**
	 * This method will create the keypad to
	 * be added to the frame.
	 */
	private JPanel keypad(){
		JButton enter = null;
		char[] symbols = {'7','8','9','/',
						  '4','5','6','*',						  
						  '1','2','3','-',
						  '0','C','=','+'};
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(4,4));
		for(int i = 0; i < 16; i++){
			if(symbols[i] == '='){
				enter = new JButton("" + symbols[i]);
				enter.setFont(enter.getFont().deriveFont(30.0f));
				enter.addActionListener(this);
				panel.add(enter);
				jfrm.getRootPane().setDefaultButton(enter);
			}
			else if(symbols[i] == 'C'){
				JButton cButton = new JButton("" + symbols[i]);
				cButton.setFont(cButton.getFont().deriveFont(30.0f));
				cButton.addActionListener(this);
				cButton.setMnemonic('C');
				panel.add(cButton);
			}
			else{
				JButton button = new JButton("" + symbols[i]);
				button.setFont(button.getFont().deriveFont(30.0f));
				button.addActionListener(this);
				panel.add(button);
			}
		}
		return panel;
	}

	//Action performed method to listen for
	//action events.
	public void actionPerformed(ActionEvent ae) {
		String input = ae.getActionCommand();
		
//		//Do not allow to continue as long as "OVERFLOW"
//		//is showing.
//		if(tfield.getText().equals("OVERFLOW")){
//			if(!input.equals("C")){
//				return;
//			}
//			else{
//				tfield.setText("0");
//				return;
//			}
//		}
		
		if(input.equals("C")){
			
			//If CTRL + C is pressed, display info...
			if((ae.getModifiers() & InputEvent.CTRL_MASK) != 0){
				do{
					tfield.setText("(c) 2015 Joshua Whitney");
				}while(!input.equals("C"));
			}
			else{
				tfield.setText("0");				
				op1 = op2 = null;
				formatText();
			}
		}
		//Check if any operations have been called.
		else if(input.equals("+") || 
				input.equals("-") ||
				input.equals("/") ||
				input.equals("*") ||
				input.equals("=")){
			Calculate(ae);
		}
		else{
			//Do not allow more than 10 digits.
			String previousStr = tfield.getText();
			if(previousStr.length() >= 10 && operator == null){				
				return;
			}				
			
			//If operation has been called, input second
			//operand.
			if(Double.parseDouble(tfield.getText()) == 0 || 
					operator != null){
					tfield.setText(input);
					operator = null;
				}
				//Enter first operand.
			else{
					tfield.setText(previousStr + input);
			}
		}
		
	}
	
	public void Calculate(ActionEvent ae){
		//Handle addition
		if(ae.getActionCommand().equals("+")){
			operator = "+";
			queue.add(new Symbols(tfield.getText()));
			queue.add(new Symbols("+"));
		}
//		Handle Subtraction
		else if(ae.getActionCommand().equals("-")){
			operator = "-";
			queue.add(new Symbols(tfield.getText()));
			queue.add(new Symbols("-"));
		}
		//Handle division
		else if(ae.getActionCommand().equals("/")){
			operator = "/";
			queue.add(new Symbols(tfield.getText()));
			queue.add(new Symbols("/"));
		}
		//Handle multiplication
		else if(ae.getActionCommand().equals("*")){
			operator = "*";
			queue.add(new Symbols(tfield.getText()));
			queue.add(new Symbols("*"));
		}
		//Get answer
		else if(ae.getActionCommand().equals("=")){
			queue.add(new Symbols(tfield.getText()));
			while(!queue.empty()){
				String test = queue.poll();
				switch(test){
					case "+":
						op2 = Double.parseDouble(queue.poll());
//						System.out.println("op1 = " + op1);
						double sum = op1 + op2;
						queue.addFirst(new Symbols(Double.toString(sum)));
						op1 = null;
						op2 = null;
						operator = null;
						break;
					case "-":
						op2 = Double.parseDouble(queue.poll());
						double difference = op1 - op2;
						queue.addFirst(new Symbols(Double.toString(difference)));
						op1 = op2 = null;
						operator = null;
						break;
					case "/":
						op2 = Double.parseDouble(queue.poll());
						if(op2 == 0){
							tfield.setText("DIVIDE BY ZERO ERROR");
							return;
						}
						Double quotient = (double)op1/op2;
						queue.addFirst(new Symbols(quotient.toString()));
						op1 = op2 = null;
						operator = null;
						break;
						
					case "*":
						op2 = Double.parseDouble(queue.poll());
						Double product = op1*op2;
						queue.addFirst(new Symbols(Double.toString(product)));
						op1 = op2 = null;
						operator = null;
						break;
					default:
						if(op1 == null){
							op1 = Double.parseDouble(test);							
							break;
						}
						else
							op2 = Double.parseDouble(test);
						break;
					
				}
			}			
			tfield.setText(getFormattedNumber(op1));
			op1 = null;
			operator = null;	
		}
	}
	
	
	/**
	 * This method removes the fractional part of the 
	 * number if it is equal to zero. Otherwise it
	 * returns the number + the fractional part as a
	 * String.
	 * @param d		Double to be formatted.
	 * @return		Formatted number as a String.
	 */
	private String getFormattedNumber(Double d){
		String[] number = d.toString().split("[.]");
		String formattedValue = null;
		if(number.length > 1){
			if(number[1].equals("0")){
				formattedValue = number[0];
			}
			else
				formattedValue = d.toString();
		}
		else
			formattedValue = d.toString();
		
		return formattedValue;
	}
	
	/**
	 * This method formats the display to font size
	 * 30 and right-justifies the output.
	 */
	private void formatText(){
		tfield.setHorizontalAlignment(SwingConstants.RIGHT);
		tfield.setFont(tfield.getFont().deriveFont(40.0f));
	}
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				try {
					new Calculator();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * @author Joshua Whitney
	 * This is a simple queue implementation
	 * to handle the calculations.
	 */
	private class Queue{
		//Linked List implementation of queue.
		private LinkedList<Symbols> list;
		
		Queue(){
			list = new <Symbols>LinkedList();
		}
		
		//Adds symbol to  rear of queue.
		public void add(Symbols symbol){
			list.add(symbol);
		}
		
		//Removes the first symbol in queue.
		public String poll(){
			return list.poll().toString();
			
		}
		
		//Adds a symbol to the front of the queue.
		public void addFirst(Symbols symbol){
			list.addFirst(symbol);
		}
		
		
		/**
		 * Checks if queue is empty.
		 * @return True if queue is empty.
		 */
		public boolean empty(){
			if(list.size() == 0)
				return true;
			else 
				return false;
		}
	}
	
	/**
	 * @author Joshua Whitney
	 * This class will represent all possible
	 * characters that can be entered into the 
	 * calculator.
	 */
	private class Symbols implements Comparable{
		
		private String token;
		
		public Symbols(String str){
			token = str;
		}
		
		public String toString(){
			return token;
		}

		public int compareTo(Object obj) {
			String str = obj.toString();
			if(token.equals(str))
				return 0;
			else
				return 1;
		}
		
		
	}

}
