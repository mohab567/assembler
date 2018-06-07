package assembler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Pass1 {
	private static ArrayList<String> labels = new ArrayList<>();
	private static ArrayList<String> operationCodes = new ArrayList<>();
	private static ArrayList<String> operands = new ArrayList<>();
	private static ArrayList<String> Addresses = new ArrayList<>();
	private static ArrayList<String> latable = new ArrayList<>();
	private static ArrayList<String> latableAdd = new ArrayList<>();
	private int lineNumber = 0;
	public static String FILENAME;
	public static String FILENAME2;
	int latableindx = 0;
	private static String objFile;
	private static String txtFile;
	private static ArrayList<String> symTabName = new ArrayList<>();
	private static ArrayList<String> symTabAddress = new ArrayList<>();
	private static ArrayList<String> intermediateFile = new ArrayList<>();
	private String[] mnemonicName = { "ADD", "AND", "COMP", "DIV", "J", "JEQ", "JGT", "JLT", "JSUB", "LDA", "LDCH",
			"LDL", "LDX", "MUL", "OR", "RD", "RSUB", "STA", "STCH", "STL", "STX", "SUB", "TD", "TIX", "WD" };
	private String[] directives = { "WORD", "BYTE", "RESW", "RESB", "EQU", "ORG", "START", "END", "LTORG" };
	boolean check = true;

	Pass1(String input, String obj, String txt, String inter) {

		FILENAME = input;
		FILENAME2 = inter;
		objFile = obj;
		txtFile = txt;
	}

	private void readfile() {
		BufferedReader br = null;
		FileReader fr = null;
		try {
			fr = new FileReader(FILENAME);
			br = new BufferedReader(fr);
			String CurrentLine;
			while ((CurrentLine = br.readLine()) != null) {
				// call for a function that uses the line
				this.line(CurrentLine);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
				if (fr != null)
					fr.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private void line(String line) {
		// check if comment
		char firstChar = line.charAt(0);
		if (firstChar == '.') {
			// if the line is commented will be ignored
		} else {
			// check if empty line
			// if empty line found will be ignored
			boolean empty = true;
			for (int k = 0; k < line.length(); k++) {
				if (line.charAt(k) != ' ' && line.charAt(k) != '\t') {
					empty = false;
					break;
				}
			}
			if (!empty) {
				// checking format
				line = this.checkFormat(line);
				// get Label From Line
				// if no Label in the line get Label will return null
				String label = getLabel(line);
				if (label != null) {
					label = label.toUpperCase();
				}

				// get OperationCode From Line
				String operationCode = getOperationCode(line).toUpperCase();
				operationCodes.add(operationCode);
				labels.add(label);
				String operand = getOperand(line);
				if (operand != null)
				operand = operand.toUpperCase();
				if (operand != null && operand.charAt(0) == '=' && operand.charAt(2) == '\''
						&& operand.charAt(operand.length() - 1) == '\'') {

					if (operand.charAt(1) == 'X') {

						if (!latable.contains(operand))
							latable.add(operand);
					} else if (operand.charAt(1) == 'C') {

						if (!latable.contains(operand))
							latable.add(operand);
					} else {
						operand = "not valid symbolic";
						check = false;

					}

				}  
					
				operands.add(operand);
				if (operationCode.trim().equals("END") || operationCode.trim().equals("LTORG")) {
					System.out.println(operationCode + "jhjh");
					while (latableindx != latable.size()) {
						labels.add("*");
						operands.add(" ");
						operationCodes.add(latable.get(latableindx));
						latableindx++;
					}
				}

				// get Operand From Line
				// if no operand in the line get operand will return null

			}
		}
	}

	private String checkFormat(String line) {
		// this method is used to modify the line and replace tabs if found with
		// equivalent spaces
		// and check that index 9 , 16 , 17 are blank
		if (line.charAt(0) == '\t') {
			line = line.replaceFirst("\t", "    ");
		}
		if (line.length() > 1 && line.charAt(1) == '\t') {
			line = line.replaceFirst("\t", "   ");
		}
		if (line.length() > 2 && line.charAt(2) == '\t') {
			line = line.replaceFirst("\t", "  ");
		}
		if (line.length() > 4 && line.charAt(4) == '\t') {
			line = line.replaceFirst("\t", "    ");
		}
		if (line.length() > 5 && line.charAt(5) == '\t') {
			line = line.replaceFirst("\t", "   ");
		}
		if (line.length() > 6 && line.charAt(6) == '\t') {
			line = line.replaceFirst("\t", "  ");
		}
		if (line.length() > 10 && line.charAt(10) == '\t') {
			line = line.replaceFirst("\t", "  ");
		}
		if (line.length() > 12 && line.charAt(12) == '\t') {
			line = line.replaceFirst("\t", "    ");
		}
		if (line.length() > 13 && line.charAt(13) == '\t') {
			line = line.replaceFirst("\t", "   ");
		}
		if (line.length() > 14 && line.charAt(14) == '\t') {
			line = line.replaceFirst("\t", "  ");
		}
		if (line.length() > 8 && line.charAt(8) != ' ') {
			throw new RuntimeException("Error in Format");
		}
		if (line.length() > 15 && line.charAt(15) != ' ' && line.charAt(15) != '\t') {
			throw new RuntimeException("Error in Format");
		}
		if (line.length() > 16 && line.charAt(16) != ' ' && line.charAt(16) != '\t') {
			throw new RuntimeException("Error in Format");
		}
		return line;
	}

	private String getLabel(String line) {
		// this method is used to get the label from the source code
		// and put them in arraylist
		boolean flag = false;
		int i = 0;
		char firstChar = line.charAt(0);
		if (firstChar == ' ' || firstChar == '\t') {
			flag = true;
		}
		if (!flag) {
			while (i < 8) {
				if (line.length() > i && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
				} else {
					break;
				}
				i++;
			}
			String label = line.substring(0, i);
			return label;
		} else
			return null;
	}

	private String getOperationCode(String line) {
		// this method is used to get the operation code from the source code
		// and put them in array list
		int i = 9;
		while (i < 15) {
			if (line.length() > i && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
			} else {
				break;
			}
			i++;
		}
		boolean isoperation = false;
		String opertionCode = line.substring(9, i);
		if (i > 10)
			opertionCode = opertionCode.toUpperCase();
		for (int j = 0; j < mnemonicName.length; j++) {
			if (opertionCode.equals(mnemonicName[j])) {
				isoperation = true;
				break;
			}
		}
		for (int j = 0; j < directives.length; j++) {
			if (opertionCode.equals(directives[j])) {
				isoperation = true;
				break;
			}
		}
		if (!isoperation) {
			opertionCode = "Error invalid Operation";
			System.out.println("Error invalid Operation");
			check = false;
		}
		return opertionCode;
	}

	private String getOperand(String line) {
		// this method is used to get the operand from the source code
		// and put them in array list
		boolean flag = false;
		int i = 17;
		while (i < 35) {
			if (line.length() > i && line.charAt(i) != ' ' && line.charAt(i) != '\t') {
				flag = true;
			} else {
				break;
			}
			i++;
		}
		if (flag) {
			String operand = line.substring(17, i);
			return operand;
		} else
			return null;
	}

	 private void createAddressArray() {
	        // this method is used to calculate the addresses of the source code
	        int Address = 0;
	        String currentAddress = new String();
	        int ORGAddress = 0;
	        try {
	            Address = Integer.parseInt(operands.get(0), 16);
	            currentAddress = Integer.toHexString(Address).toUpperCase();
	            Addresses.add(currentAddress);
	            Addresses.add(currentAddress);
	        } catch (Exception e) {
	            currentAddress = Integer.toHexString(Address).toUpperCase();
	            Addresses.add(currentAddress);
	        }
	 
	        for (int i = 1; i < labels.size(); i++) {
	            if (labels.get(i) != null && labels.get(i).equals("*")) {
	                if (operationCodes.get(i).charAt(1) == 'X') {
	                    Address += 1;
	                } else {
	                    int length = operationCodes.get(i).length() - 4;
	                    Address += length;
	                }
	            } else if (operationCodes.get(i).equals("WORD")) {
	                Address += 3;
	            } else if (operationCodes.get(i).equals("BYTE")) {
	                if (operands.get(i).charAt(0) == 'X') {
	                    Address += 1;
	                } else {
	                    int length = operands.get(i).length() - 3;
	                    Address += length;
	                }
	            } else if (operationCodes.get(i).equals("RESW")) {
	                Address += (3 * Integer.parseInt(operands.get(i)));
	            } else if (operationCodes.get(i).equals("RESB")) {
	                Address += (Integer.parseInt(operands.get(i)));
	            } else if (operationCodes.get(i).equals("EQU")) {
	                if (!operands.get(i).equals("*")) {
	                    if (calculate(operands.get(i)) == null) {
	                        operands.set(i, "Error Forward Refernce Is Not Allowed is EQU");
	                        check = false;
	                    } else {
	                        System.out.println(Addresses.get(i));
	                        Addresses.set(i, calculate(operands.get(i)));
	                    }
	                    currentAddress = Integer.toHexString(Address).toUpperCase();
	                }
	            } else if (operationCodes.get(i).equals("ORG")) {
	                if (operands.get(i) != null) {
	                    if (calculate(operands.get(i)) == null) {
	                        currentAddress = Integer.toHexString(Address).toUpperCase();
	                        operands.set(i, "Error Forward Refernce Is Not Allowed is ORG");
	                        check = false;
	                    } else {
	                        ORGAddress = Address;
	                        Address = Integer.parseInt(calculate(operands.get(i)), 16);
	                        currentAddress = Integer.toHexString(Address).toUpperCase();
	                    }
	                } else {
	                    Address = ORGAddress;
	                    currentAddress = Integer.toHexString(Address).toUpperCase();
	                }
	 
	            } else if (operationCodes.get(i).equals("END") || operationCodes.get(i).equals("LTORG")) {
	 
	            } else {
	                Address += 3;
	            }
	            if (operationCodes.get(i).equals("EQU") || operationCodes.get(i).equals("ORG")) {
	                Addresses.add(currentAddress);
	            } else {
	                currentAddress = Integer.toHexString(Address).toUpperCase();
	                Addresses.add(currentAddress);
	            }
	        }
	    }

	private void createSymTab() {
		/*
		 * this method is used to create symbol table and check if the label is
		 * found more than one time then will throw runtime exception
		 */
		for (int i = 0; i < labels.size(); i++) {
			if (labels.get(i) != null && !labels.get(i).equals("*")) {
				for (int k = 0; k < symTabName.size(); k++) {
					if (labels.get(i).equals(symTabName.get(k))) {
						System.out.println("dublicate in labels");
						String old = this.operands.get(i).concat(" Error Dublicate in Labels ");
						this.operands.set(i, old);
						System.out.println("ghfgh");
						this.check = false;
					}
				}
				symTabName.add(labels.get(i));
				symTabAddress.add(Addresses.get(i));
			}
		}
	}

	// intermediate File

	private void writeintermediate(String address, String label, String operationCode, String operand) {
		lineNumber++;
		// this method is used to write the intermediate file
		if (label == null) {
			label = " ";
		}
		if (operand == null) {
			operand = " ";
		}
		String intermediate;
		if (operationCode.equals("START") || operationCode.equals("RESB") || operationCode.equals("RESW")) {
			intermediate = writeintermediateLine(address, label, operationCode, operand);
			intermediateFile.add(intermediate);
		} else if (operationCode.equals("LTORG")||operationCode.equals("ORG")) {
			intermediate = writeintermediateLine(" ", label, operationCode, operand);
			intermediateFile.add(intermediate);
		} else if (operationCode.equals("END")) {
			intermediate = writeintermediateLine(" ", label, operationCode, operand);
			intermediateFile.add(intermediate);
			while (lineNumber != this.labels.size()) {
				intermediate = writeintermediateLine(this.Addresses.get(lineNumber), this.labels.get(lineNumber),
						this.operationCodes.get(lineNumber), " ");
				intermediateFile.add(intermediate);
				lineNumber++;
			}
			writeIntermediateFile();
		} else {
			intermediate = writeintermediateLine(address, label, operationCode, operand);
			intermediateFile.add(intermediate);
		}
	}

	String calculate(String x) {
		System.out.println(x);
		Integer z = null, y = null;
		String[] f;
		if (x.contains("-")) {

			f = x.split("\\-");
			if (f[0].matches("^-?\\d+$")) {
				y = Integer.parseInt(f[0]);
			}
			if (f[1].matches("^-?\\d+$")) {
				z = Integer.parseInt(f[1]);
			}
			for (int i = 0; i < Addresses.size(); i++) {
				if (labels.get(i) != null && labels.get(i).equals(f[0]) && y == null) {
					y = Integer.parseInt(Addresses.get(i).trim(), 16);
				}
				if (labels.get(i) != null && labels.get(i).equals(f[1]) && z == null) {
					z = Integer.parseInt(Addresses.get(i).trim(), 16);
				}
				if (y != null && z != null) {
					break;
				}
			}
			if (z == null || y == null){
				
				return null;}
			else
				return Integer.toHexString(y - z);
		} else if (x.contains("+")) {
            
			f = x.split("\\+");
			if (f[0].matches("^-?\\d+$")) {
				y = Integer.parseInt(f[0]);
			}
			if (f[1].matches("^-?\\d+$")) {
				z = Integer.parseInt(f[1]);
			}
			for (int i = 0; i < Addresses.size(); i++) {
				if (labels.get(i) != null && labels.get(i).equals(f[0]) && y == null) {
					y = Integer.parseInt(Addresses.get(i).trim(), 16);
				}
				if (labels.get(i) != null && labels.get(i).equals(f[1]) && z == null) {
					z = Integer.parseInt(Addresses.get(i).trim(), 16);
				}
				if (y != null && z != null) {
					break;
				}
			}
			if (z == null || y == null){
				
				return null;}
			else
				
				return Integer.toHexString(y + z);
		} else {
			if (x.matches("^-?\\d+$")) {
				return Integer.toHexString(Integer.parseInt(x));
			} else {
				for (int i = 0; i < Addresses.size(); i++) {
					if (labels.get(i) != null && labels.get(i).equals(x)) {
						return Addresses.get(i);
					}
				}
				
				return null;
			}
		}

	}

	private String writeintermediateLine(String address, String label, String operationCode, String operand) {
		// this method is used to make the Listing File in the required format
		String listing = new String();
		listing = address;
		listing = addSpaces(listing, 8);
		listing = listing.concat(label);
		listing = addSpaces(listing, 18);
		listing = listing.concat(operationCode);
		listing = addSpaces(listing, 26);
		listing = listing.concat(operand);
		listing = listing.concat("\n");
		return listing;
	}

	private String addSpaces(String STR, int length) {
		while (STR.length() < length) {
			STR = STR.concat(" ");
		}
		return STR;
	}

	private void writeIntermediateFile() {
		// Listing File
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME2))) {
			for (int i = 0; i < intermediateFile.size(); i++) {
				bw.write(intermediateFile.get(i));
			}
			bw.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	void run() {
		// this method is only used to run the assembler
		this.readfile();
		this.createAddressArray();
		this.createSymTab();
		this.createlatableAdd();
		for (int i = 0; i < labels.size(); i++) {
			this.writeintermediate(Addresses.get(i), labels.get(i), operationCodes.get(i), operands.get(i));
		}
		if (check) {
			System.out.println(check);
			Pass2 g = new Pass2(labels, operationCodes, operands, Addresses, symTabName, symTabAddress, latable,
					latableAdd, objFile, txtFile);
		}
	}

	private void createlatableAdd() {
		int k = 0;
		while (k < labels.size()) {
			if (labels.get(k) != null && labels.get(k).equals("*")) {
				latableAdd.add(Addresses.get(k));
			}
			k++;
		}
	}

}