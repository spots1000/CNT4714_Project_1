/*
 * Name:Anthony Dalia
 * Course: CNT 4714 - Spring 2020
 * Assignment Title: Project 1- event driven enterprise simualtion
 * Date: Sunday January 26th, 2020
 */




import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.BorderLayout;
import javax.swing.JTextField;
import java.awt.Button;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.awt.event.ActionEvent;

public class project1 {

	private JFrame frame;
	private JTextField textField;
	private JTextField bookIDtxt;
	private JTextField quanTxt;
	private JTextField itemTxt;
	private JTextField subtotalTxt;
	private static String[][] inv;
	private static String[][] order;
	private static int invNum;
	private static int curOrder = 0;
	private static int maxOrder = 0;
	private static Button processBtn;
	private static Button confirmBtn;
	private static Button viewBtn;
	private static Button finishBtn;
	
	private static boolean firstSetup = true;
	private static boolean ordersGen = false;
	private static double total = 0;
	private static int totalItems = 0;
	
	//Tax variables
	private static double taxRate = 0.06;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//Run the window
					project1 window = new project1();
					window.frame.setVisible(true);
					
					setup(null, null, null, null, null, null, null, null);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public project1() 
	{
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() 

	{
		frame = new JFrame();
		frame.setBounds(100, 100, 571, 367);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel orderLbl = new JLabel("Enter number of items in this order:");
		orderLbl.setBounds(30, 48, 215, 14);
		frame.getContentPane().add(orderLbl);
		
		JTextField ordertxt = new JTextField();
		ordertxt.setBounds(255, 45, 295, 20);
		frame.getContentPane().add(ordertxt);
		ordertxt.setColumns(10);
		
		bookIDtxt = new JTextField();
		bookIDtxt.setColumns(10);
		bookIDtxt.setBounds(255, 81, 295, 20);
		frame.getContentPane().add(bookIDtxt);
		
		JLabel bookIDLbl = new JLabel("Enter Book ID for Item #1:");
		bookIDLbl.setBounds(30, 84, 215, 14);
		frame.getContentPane().add(bookIDLbl);
		
		quanTxt = new JTextField();
		quanTxt.setColumns(10);
		quanTxt.setBounds(255, 114, 295, 20);
		frame.getContentPane().add(quanTxt);
		
		JLabel quanLbl = new JLabel("Enter quantity for item #1:");
		quanLbl.setBounds(30, 117, 215, 14);
		frame.getContentPane().add(quanLbl);
		
		itemTxt = new JTextField();
		itemTxt.setColumns(10);
		itemTxt.setBounds(255, 147, 295, 20);
		itemTxt.setEditable(false);
		frame.getContentPane().add(itemTxt);
		
		JLabel itemLbl = new JLabel("Item #1 info:");
		itemLbl.setBounds(30, 150, 215, 14);
		frame.getContentPane().add(itemLbl);
		
		subtotalTxt = new JTextField();
		subtotalTxt.setColumns(10);
		subtotalTxt.setBounds(255, 180, 295, 20);
		subtotalTxt.setEditable(false);
		frame.getContentPane().add(subtotalTxt);
		
		JLabel subtotalLbl = new JLabel("Order subtotal for (0) items(s):");
		subtotalLbl.setBounds(30, 183, 215, 14);
		frame.getContentPane().add(subtotalLbl);
		
		confirmBtn = new Button("Confirm Item #1");
		confirmBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				//Call important methods
				confirm(Integer.parseInt(bookIDtxt.getText()), itemTxt.getText(), Integer.parseInt(quanTxt.getText()));
				formUpdate(bookIDLbl, bookIDtxt, quanLbl, quanTxt, itemLbl, itemTxt, subtotalLbl, subtotalTxt, false);
				
				//Generate the printout
				try {
					printOut();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				//Move to the next order
				curOrder++;
			}
		});
		confirmBtn.setBounds(117, 297, 91, 22);
		confirmBtn.setEnabled(false);
		frame.getContentPane().add(confirmBtn);
		
		processBtn = new Button("Process Item #1");
		processBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				process(ordertxt.getText(), bookIDtxt.getText(), quanTxt.getText(), itemTxt, subtotalLbl, itemLbl,subtotalTxt);
			}
		});
		processBtn.setBounds(10, 297, 91, 22);
		frame.getContentPane().add(processBtn);
		
		viewBtn = new Button("View Order");
		viewBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				viewOrder();
			}
		});
		viewBtn.setBounds(220, 297, 70, 22);
		viewBtn.setEnabled(false);
		frame.getContentPane().add(viewBtn);
		
		finishBtn = new Button("Finish Order");
		finishBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				finishOrder();
			}
		});
		finishBtn.setBounds(303, 297, 70, 22);
		finishBtn.setEnabled(false);
		frame.getContentPane().add(finishBtn);
		
		Button newBtn = new Button("New Order");
		newBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to reset the order?","Reset?", 1) == 0)
				{
					try {
						setup(bookIDLbl, bookIDtxt, quanLbl, quanTxt, itemLbl, itemTxt, subtotalLbl, subtotalTxt);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.out.println("Error in Reset Order method with IO.");
					}
				}
			}
		});
		newBtn.setBounds(394, 297, 70, 22);
		frame.getContentPane().add(newBtn);
		
		Button exitBtn = new Button("Exit");
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				System.exit(0);
			}
		});
		
		exitBtn.setBounds(475, 297, 70, 22);
		frame.getContentPane().add(exitBtn);
	}
	
	private static void setup(JLabel idLbl, JTextField idTxt, JLabel quanLbl, JTextField quanTxt, JLabel itemLbl, JTextField itemTxt, JLabel subLbl, JTextField subTxt) throws IOException
	{
		System.out.println("A reset has been intiated.");
		
		//Determine how many lines are in the program
		BufferedReader reader = new BufferedReader(new FileReader("inventory.txt"));
		invNum = 0;
		while (reader.readLine() != null) invNum++;
		reader.close();
		
		//Generate the array list of the correct size
		inv = new String[invNum][3];
		
		//Read in the file, placing every line into a correct block
		reader = new BufferedReader(new FileReader("inventory.txt"));
		int i = 0;
		while (i <= invNum)
		{
			String line = reader.readLine();
			
			if(line == null)
				break;
			
			String[] sep = line.split(", ");
			
			//Place the data into the correct place in the array
			inv[i][0] = sep[0];
			inv[i][1] = sep[1];
			inv[i][2] = sep[2];
			
			i++;
			
		}
		reader.close();			
		
		//Reset the transaction file
		BufferedWriter write = new BufferedWriter(new FileWriter("transactions.txt"));
		write.close();
		
		
		if( firstSetup == false)
		{
			//Reset the main values that control our program
			curOrder = 0;
			maxOrder = 0;
			ordersGen = false;
			total = 0;
			totalItems = 0;
			
			//Perform a form update with the nessisary passthrough varaibles
			formUpdate(idLbl, idTxt, quanLbl, quanTxt, itemLbl, itemTxt, subLbl, subTxt, true);
		}
		else
		{
			//skip a form update because the form is fresh and has just been created.
			firstSetup = false;
		}

		
	}
	
	private static void formUpdate(JLabel idLbl, JTextField idTxt, JLabel quanLbl, JTextField quanTxt, JLabel itemLbl, JTextField itemTxt, JLabel subLbl, JTextField subTxt, boolean flag)
	{	
		if(flag == true)
		{
			System.out.println("Form Update has initated a reset.");

			//Update Labels for new order #
			idLbl.setText("Enter Book ID for item #1");
			quanLbl.setText("Enter quantity for item #1");
			itemLbl.setText("Item #1 info");
			subLbl.setText("Order subtotal for (0) item(s)");
			
			//Make labels visible again
			idLbl.setVisible(true);
			quanLbl.setVisible(true);
			
			//Clean Text Box
			idTxt.setText("");
			quanTxt.setText("");
			itemTxt.setText("");
			subTxt.setText("");
			
			//Update button labels
			processBtn.setLabel("Process Item 1");
			confirmBtn.setLabel("Confirm Item 1");
		
			//Update button activations
			processBtn.setEnabled(true);
			confirmBtn.setEnabled(false);
			viewBtn.setEnabled(false);
			finishBtn.setEnabled(false);
		}
		else if(curOrder < maxOrder-1)
		{
			//Update Labels for new order #
			idLbl.setText("Enter Book ID for item #" + (curOrder+2));
			quanLbl.setText("Enter quantity for item #" + (curOrder+2));
			itemLbl.setText("Item #" + (curOrder+1) + " info");
			subLbl.setText("Order subtotal for (" + totalItems + ") item(s)");
			
			//Clean Text Box
			idTxt.setText("");
			quanTxt.setText("");
			itemTxt.setText("" + order[curOrder][0] + ", " + order[curOrder][1] + ", " + order[curOrder][2] + ", " + order[curOrder][3] + ", " + (Double.parseDouble(order[curOrder][4])*100) + "%, $" + order[curOrder][5]);
			subTxt.setText(total+"");
			
			//Update button labels
			processBtn.setLabel("Process Item " + (curOrder+2));
			confirmBtn.setLabel("Confirm Item " + (curOrder+2));
		
			//Update button activations
			processBtn.setEnabled(true);
			confirmBtn.setEnabled(false);
			viewBtn.setEnabled(true);
			finishBtn.setEnabled(true);
		}
		else
		{
			//Disable some boxes we do not need anymore
			idLbl.setVisible(false);
			quanLbl.setVisible(false);
			itemLbl.setText("Item #" + (curOrder+1) + " info");
			
			//Clean Text Box
			idTxt.setText("");
			quanTxt.setText("");
			itemTxt.setText("" + order[curOrder][0] + ", " + order[curOrder][1] + ", " + order[curOrder][2] + ", " + order[curOrder][3] + ", " + order[curOrder][4] + ", $" + order[curOrder][5]);
			subTxt.setText(total+"");
			
			//Update button activations
			processBtn.setEnabled(false);
			confirmBtn.setEnabled(false);
			
			//Activate buttons that should always be activated
			viewBtn.setEnabled(true); 
			finishBtn.setEnabled(true);
		}
	}
	
	private void process(String StrnumofOrders, String StritemID, String StritemQuan, JTextField info, JLabel sub, JLabel infoLbl, JTextField total)
	{
		/*
		System.out.println("Item #:" + itemNum);
		System.out.println("Item ID:" + itemID);
		System.out.println("Item Quantity:" + itemQuantity);
		 */
		
		//Convert everything to the correct values
		int numofOrders = Integer.parseInt(StrnumofOrders);
		int itemID = Integer.parseInt(StritemID);
		int itemQuan = Integer.parseInt(StritemQuan);
		
		//Check for Price
		double price = 0;
		int check;
		boolean found = false;
		
		//Check through the ID List for the valid book
		for(int i = 0; i < invNum; i++)
		{
			check = Integer.parseInt(inv[i][0]);
			
			if(check == itemID)
			{
				System.out.println("We have located the matching book. Title: " + inv[i][1]);
				infoLbl.setText("Item #" + (curOrder+1) + " info");
				info.setText(inv[i][1]);
				price = Double.parseDouble(inv[i][2]);
				found = true;
			}
		}
		
		//Throw a flag if we did not find the correct book
		if(found == false)
		{
			//Flag
			JOptionPane.showMessageDialog(frame, "The Book ID " + itemID + " is invalid.");
		}
		else
		{

			
			//Update item quantity in global variable
			totalItems += itemQuan;
			
			//Update subtotal label
			sub.setText("Order Subtotal for (" + totalItems + ") item(s):");
			
			//Calculate subtotal
			price = price * itemQuan;
			total.setText(""+price);
			
			//Only generate a new array of orders if we do not have one yet
			if (ordersGen == false)
			{
				//Generate Order array
				System.out.println("Generating a new array of orders");
				order = new String[numofOrders][7];
				maxOrder = numofOrders;
				ordersGen = true;
			}

			//Deactivate this button and activate the other button
			processBtn.setEnabled(false);
			confirmBtn.setEnabled(true);
		}
	}
	
	private void printOut() throws IOException//Item ID# / Title / Price / Quantity / Discount / Subtotal / Transaction ID
	{ 					//		Transaction ID				Item ID						Title						Price						Quantity                     Discount
		String printout = "" + order[curOrder][6] + ", " + order[curOrder][0] + ", " + order[curOrder][1] + ", " + order[curOrder][2] + ", " + order[curOrder][3] + ", " + order[curOrder][4] + ", $" + order[curOrder][5] + ", " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/uuuu")) + ", " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a ")) + "EST";
		System.out.println(printout);
		
		if (curOrder == 0)
		{
			//Open a write channel to our file
			BufferedWriter write = new BufferedWriter(new FileWriter("transactions.txt", true));
			write.write(printout);
			write.close();
		}
		else
		{
			//Open a write channel to our file
			BufferedWriter write = new BufferedWriter(new FileWriter("transactions.txt", true));
			write.newLine();
			write.write(printout);
			write.close();
		}

	}
	
	private void viewOrder()
	{
		String output = "";
		
		for(int i = 0; i < maxOrder; i++)
		{
			output += ((i+1) + ". " + order[i][0] + ", " + order[i][1] + ", " + order[i][2] + ", " + order[i][3] + ", " + (Double.parseDouble(order[i][4])*100) + "%, $" + order[i][5] + "\n");
		}
		
		JOptionPane.showMessageDialog(frame, output);
	}
	
	private void finishOrder()
	{
		double taxes = 0.0;
		
		String output = new String();
		
		output += "Date: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/uuuu")) + "    " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a ")) + "EST\n\n"; 		//Date
		output += "Number of line items: " + (maxOrder) + "\n\n"; //Line Items
		output += "Item# / Title / Price / Qty / Disc% / Subtotal: \n\n"; //Description of text lines
		
		//Line items
		for(int i = 0; i < maxOrder; i++)
		{
			output += ((i+1) + ". " + order[i][0] + ", " + order[i][1] + ", " + order[i][2] + ", " + order[i][3] + ", " + (Double.parseDouble(order[i][4])*100) + "%, $" + order[i][5] + "\n");
		}
		output += "\n\n";
		
		output += "Order Subtotal: $" + total + "\n\n"; //subtotal without tax
		output += "Tax Rate: " + taxRate*100 + "%\n\n"; //Tax Rate
		output += "Tax Ammount: $" + taxRate * total + "\n\n"; //Tax Paid
		output += "Order Total: $" + (total + taxRate * total + "\n\n"); //Order Total
		output += "Thanks for shopping at the Ye Olde Book Shoppe!"; //Ending message
		
		JOptionPane.showMessageDialog(frame, output);
		
	}
	
	private void confirm(int idNum, String bookTitle, int quantity)
	{
		double sb;
		double discount;
		double subtotal = 0;
		String disPercent;
		
		//Gather the price
		double price = 0;
		int check;
		
		//Check through the ID List for the valid book
		for(int i = 0; i < invNum; i++)
		{
			check = Integer.parseInt(inv[i][0]);
			
			if(check == idNum)
			{
				price = Double.parseDouble(inv[i][2]);
			}
		}
		
		//Generate Discount:
		if(quantity <= 5)
		{
			discount = 0.0;
			disPercent = "0.0";
		}
		else if (quantity <=10)
		{
			discount = 0.1;
			disPercent = "0.1";
		}
		else
		{
			discount = 0.15;
			disPercent = "0.15";
		}
		
		//Generate Price
		sb = (price * quantity);
		discount = (discount * sb);
		subtotal += sb - discount;
		total += subtotal;
		
		System.out.println("Discount caclulated as " + discount + " for order " + curOrder);
		System.out.println("Subtotal calculated as " + subtotal + " for order " + curOrder);
		
		//Format of array:
		//Item ID# / Title / Price / Quantity / Discount / Subtotal / Transaction ID
		order[curOrder][0] = idNum+"";
		order[curOrder][1] = bookTitle+"";
		order[curOrder][2] = price+"";
		order[curOrder][3] = quantity+"";
		order[curOrder][4] = disPercent;
		order[curOrder][5] = subtotal+"";
		order[curOrder][6] = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMuuuuHHmm")) + ""; 
		
		//Create the successfull Dialouge box
		JOptionPane.showMessageDialog(frame, "Item #" + (curOrder+1) + " accepted.");
	}
}


