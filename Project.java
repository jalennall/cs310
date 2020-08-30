import java.sql.*;
import java.util.InputMismatchException;

public class Project {
	/*
	 * Authors JalenNall JoshCoward
	 *
	 */

	public static void main(String[] args) throws ClassNotFoundException,SQLException{
		/*
		 * things to set up database conection
		 */
		Connection conn = null;  
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
			System.out.println();
			System.out.println("JDBC driver loaded");
			conn = makeConnection("58120", "Final ","1234567890");    
			/*
			 * 
			 *conditionals
			 */	

			int i; // used to keep track of number of arguments			
			if(args[0] == "/?") {
				Usage();
			}
			/*
			 * has two different calls one for if there is a desc and one if there isnt
			 * table doesnt need a desc, user would need to enter just null for arg 2 to work
			 */
			else if(args[0].toLowerCase().equals("createitem")){
				if(args.length < 4){
					Usage();
							}				
				if(args[1] == null || args[2] == null || args[3] == null) {
					System.out.println("Error in argument numbers");
					Usage();
					return;
				}
				if(args[1]!= null && args[2] == null && args[3]!=null) {
				}
				runQuery(conn,createItem(args[1], args[2],args[3]),false);

			}
			else if(args[0].toLowerCase().equals("createpurchase")) {
				i = 2;
				argumentCheck(i,args[1],args[2]);
				runQuery(conn,createPurchase(args[1],Integer.parseInt(args[2])),false);
			}
			else if(args[0].toLowerCase().equals("createshipment")) {
				if(args[1] == null || args[2] == null || args[3] == null) {
					System.out.println("Error in argument numbers");
					Usage();
					return;
				}
				runQuery(conn,createShipment(args[1],Integer.parseInt(args[2]),args[3]),false);
			}
			else if(args[0].toLowerCase().equals("getitems")) {
				i = 1;
				argumentCheck(i,args[1],"null");
				if(args[1].equals("%")) {
					runQuery(conn,"select * from Item;",true);
				}
        else{
				runQuery(conn,getItems(args[1]),true);
			}
      }
			else if(args[0].toLowerCase().equals("getshipments")) {
				i = 1;
				argumentCheck(i,args[1],"null");
				if(args[1].equals("%")) {
					runQuery(conn,"select * from Shipment;",true);
				}

				else runQuery(conn,getShipments(args[1]),true);
			}
			else if(args[0].toLowerCase().equals("getpurchases")) {
				i = 1;
				argumentCheck(i,args[1],"null");
				if(args[1].equals("%")) {
					runQuery(conn,"select * from Purchase;",true);
				}
			else	runQuery(conn,getPurchases(args[1]),true);

			}
			else if(args[0].toLowerCase().equals("itemsavailable")) {
				i = 1;
				argumentCheck(i,args[1],"null");
				if(args[1].equals("%")){
					runQuery(conn, "call itemsAvailable2();", true);
				}else{
					runQuery(conn,itemsAvailable(args[1]),true);
				}
			}
			else if(args[0].toLowerCase().equals("updateitem")) {
				i = 2;
				argumentCheck(i,args[1],args[2]);
				runQuery(conn,updateItem(args[1],Double.parseDouble(args[2])),false);
			}
			else if(args[0].toLowerCase().equals("deleteitem")) {
				i = 1;
				argumentCheck(i,args[1],"null");
				runQuery(conn,deleteItem(args[1]),false);

			}
			else if(args[0].toLowerCase().equals("deletepurchase")) {
				i = 1;
				argumentCheck(i,args[1],"null");
				runQuery(conn,deletePurchase(args[1]),false);
			}
			else if(args[0].toLowerCase().equals("deleteshipment")) {
				i = 1;
				argumentCheck(i,args[1],"null");
				runQuery(conn,deletePurchase(args[1]),false);
			}

			else Usage();
			conn.close();
			System.out.println();
			System.out.println("Database connection closed");
			System.out.println();

		} 
		catch (InputMismatchException e) {
			System.err.println(e);
		}
		catch (Exception ex) {
			// handle the error
			System.err.println(ex);
		}


	}
	/*
	 *Method can be called to create the connection to the database
	 *
	 */
	public static Connection makeConnection(String port, String database, String password) {
		try {
			Connection conn = null;

			System.out.println("try to get a connection");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:" + port+ "/" + database+ "?verifyServerCertificate=false&useSSL=true&serverTimezone=UTC", "msandbox", password);
			// Do something with the Connection
			System.out.println("Database " + database +" connection succeeded!");
			System.out.println();
			return conn;
		} catch (SQLException ex) {
			// handle any errors
			System.err.println("SQLException: " + ex.getMessage());
			System.err.println("SQLState: " + ex.getSQLState());
			System.err.println("VendorError: " + ex.getErrorCode());
		}
		return null;
	}

	//Used to create/run querys
	public static void runQuery(Connection conn,String statement,boolean select) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			if(select == true) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(statement);
			}
			else {
				stmt = conn.createStatement();
				stmt.executeUpdate(statement);  // no real code required... just a real db connection
				// Now do something with the ResultSet ....
			}
			rs.beforeFirst();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			String name;
			for(int i = 1; i <= columnsNumber; i++){
				name = rsmd.getColumnName(i);				
				System.out.print(name + " ");				
      		}
      		System.out.println();
			while (rs.next()) {
				for(int i = 1; i < columnsNumber + 1; i++)
					System.out.print(rs.getString(i) + " ");
				System.out.println();
			}
		} catch (SQLException ex) {
			// handle any errors
			System.err.println("SQLException: " + ex.getMessage());
			System.err.println("SQLState: " + ex.getSQLState());
			System.err.println("VendorError: " + ex.getErrorCode());
		} finally {
			// it is a good idea to release resources in a finally{} block
			// in reverse-order of their creation if they are no-longer needed
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {
				} // ignore
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {
				} // ignore
				stmt = null;
			}
		}
	}

	public static void argumentCheck(int i,String arg1, String arg2){
		if(i == 1){
			if(arg1 == null) {
				System.out.println("Error in argument numbers");
				Usage();
				return;
			}
		else if(i == 2){
			if(arg1 == null || arg2 == null) {
				System.out.println("Error in argument numbers");
				Usage();
				return;
				}			
			}

		} 

	}
	public static String createItem(String iCode,String desc, String price) {
		String stmnt = "Insert into Item(itemCode,itemDescription, price)"
				+ " Values ( '" + iCode + "', '" + desc + "', " + price +");";
		return stmnt;

	}
	public static String createPurchase(String pCode, int quantity) {
		String stmnt = "call createPurchase('" + pCode + "', " + quantity + ");";
		return stmnt;
	}
	public static String createShipment(String sCode,int shipQ, String shipDate) {
		String stmnt = "call CreateShipment('" + sCode + "', "+ shipQ + ", '"+ shipDate + "');"; 
		return stmnt;

	}
	public static String getItems(String iCode) {
		String stmnt = "Select * from Item where itemCode = '" + iCode + "';"; 
		return stmnt;
	}
	public static String getShipments(String sCode) {
		String stmnt = "call GetShipments('" + sCode + "');";
		return stmnt;
	}
	public static String getPurchases(String pCode) {
		String stmnt = "call GetPurchases('" + pCode + "');";
		return stmnt;
	}
	public static String itemsAvailable(String iCode) {
		String stmnt = "call itemsAvailable('" + iCode + "');";
		return stmnt;
	}
	public static String updateItem(String iCode, double price) {
		String stmnt = "UPDATE Item SET price = " + price + " where Item.itemCode = '" + iCode + "';";		
		return stmnt;
	}
	public static String deleteItem(String iCode) {
		String stmnt = "DELETE FROM Item where itemCode = '"  + iCode + "';";
		return stmnt;
	}
	public static String deletePurchase(String pCode) {
		String stmnt = "call DeletePurchase('"  + pCode + "');";
		return stmnt;
	}
	public static String deleteShipment(String sCode) {
		String stmnt = "Call DeleteShipment('"  + sCode + "');";
		return stmnt;
	}

	public static void Usage() {
		System.out.println("USAGE:\nFOR Create items\n "
				+ " CreateItem <itemCode> <itemDescription> <price>\n"
				+"\n"
				+ "FOR createPurchase\n"
				+ " CreatePurchase <itemCode> <PurchaseQuantity>\n"
				+"\n"
				+ "FOR creatShipment\n"
				+ " CreateShipment <itemCode> <ShipmentQuantity> <shipmentDate>\n"
				+"\n"
				+ "FOR getItems\n"
				+ " GetItems <itemCode>\n"
				+"\n"
				+ "FOR getShipment\n"
				+ " GetShipments <itemCode>\n"
				+"\n"
				+ "FOR getPurchase\n"
				+ " GetPurchases <itemCode>\n"
				+"\n"
				+ "FOR itemsAvailable\n"
				+ " ItemsAvailable <itemCode>\n"
				+"\n"
				+ "FOR updateItem\n"
				+ " UpdateItem <itemCode> <price>\n"
				+"\n"
				+ "FOR deleteItem\n"
				+ " DeleteItem <itemCode>\n"
				+"\n"
				+ "FOR deletePurchase\n"
				+ "DeletePurchase <itemCode>\n"
				+"\n"
				+ "FOR deleteShipment\n"
				+ " DeleteShipment <itemCode> "
				);
	}
}
