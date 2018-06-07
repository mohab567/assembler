package assembler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Pass2 {

	private int lineNumber = 0;
	private ArrayList<String> labels = new ArrayList<>();
	private ArrayList<String> operationCodes = new ArrayList<>();
	private ArrayList<String> operands = new ArrayList<>();
	private ArrayList<String> Addresses = new ArrayList<>();
	private ArrayList<String> objectCodes = new ArrayList<>();
	private ArrayList<String> listingfile = new ArrayList<>();
	private ArrayList<String> objectProgramfile = new ArrayList<>();
	private static ArrayList<String> symTabName = new ArrayList<>();
	private static ArrayList<String> symTabAddress = new ArrayList<>();
	private static ArrayList<String> latable = new ArrayList<>();
	private static ArrayList<String> latableAdd = new ArrayList<>();
	private boolean check = true;
	private static  String FILENAME2 ;
	private static  String FILENAME3 ;
	private int[] mnemonicValue = { 24, 64, 40, 36, 60, 48, 52, 56, 72, 0, 80, 8, 4, 32, 68, 216, 76, 12, 84, 20, 16,
			28, 224, 44, 220 };
	private String[] mnemonicName = { "ADD", "AND", "COMP", "DIV", "J", "JEQ", "JGT", "JLT", "JSUB", "LDA", "LDCH",
			"LDL", "LDX", "MUL", "OR", "RD", "RSUB", "STA", "STCH", "STL", "STX", "SUB", "TD", "TIX", "WD" };

	Pass2(ArrayList<String> labels, ArrayList<String> operationCodes, ArrayList<String> operands,
			ArrayList<String> Addresses, ArrayList<String> symTabName, ArrayList<String> symTabAddress,ArrayList<String> latable,ArrayList<String> latableAdd,String obj,String txt) {
		FILENAME2=txt;
		FILENAME3=obj;
		this.labels = labels;
		this.operationCodes = operationCodes;
		this.operands = operands;
		this.Addresses = Addresses;
		this.symTabName = symTabName;
		this.symTabAddress = symTabAddress;
		this.latable=latable;
		this.latableAdd=latableAdd;
		this.lineNumber = 0;
		this.run();
	}

	private void run() {
		// this method is used to run pass2
		for (int i = 0; i < labels.size(); i++) {
			getObjectCode(operationCodes.get(i), operands.get(i));
		}
		for (int i = 0; i < labels.size(); i++) {
			writeListing(Addresses.get(i), labels.get(i), operationCodes.get(i), operands.get(i), objectCodes.get(i));
		}
		checkErrors();
		if (check) {
			writeObject();
		}
	}

	private void getObjectCode(String operationCode, String operand) {
		// this method is used to create object code
		lineNumber++;
		// search for operation Code in the mnemonic Table
		boolean found = false;
		boolean flag = false;
		String mnemonicValue = new String();
		for (int i = 0; i < this.mnemonicName.length; i++) {
			if (operationCode.equals(this.mnemonicName[i])) {
				found = true;
				mnemonicValue = String.valueOf(this.mnemonicValue[i]);
				mnemonicValue = Integer.toHexString(Integer.parseInt(mnemonicValue)).toUpperCase();
				if (mnemonicValue.length() == 1) {
					String concat = "0";
					concat = concat.concat(mnemonicValue);
					mnemonicValue = concat;
				}
				break;
			}
		}
		String operandAddress = new String();
		String objectCode = new String();
		if (found) {
			if (operand == null) {
				operandAddress = "0000";
				objectCode = mnemonicValue.concat(operandAddress);
			} else {
				if (operand.contains(",X")) {
					operand = operand.substring(0, operand.length() - 2);
				}
				for (int z = 0; z < symTabName.size(); z++) {
					if (operand.equals(symTabName.get(z))) {
						operandAddress = symTabAddress.get(z);
						flag = true;
						break;
					}
				}
				if (flag) {
					String concat = new String();
					for (int k = operandAddress.length(); k < 4; k++) {
						concat = concat.concat("0");
					}
					concat = concat.concat(operandAddress);
					operandAddress = concat;
					objectCode = mnemonicValue.concat(operandAddress);
					
				}else if(operand.charAt(0)=='=')	{
					boolean f = false;
					for(int i=0;i<latable.size();i++){
						if(latable.get(i).equals(operand)){
							f=true;
							objectCode = mnemonicValue.concat(latableAdd.get(i));
							break;
						}	
					}
					if(!f){
						check = false;
						operandAddress = "0000";
						objectCode = "UnDefinedSymbol";
					}
				} else {
					check = false;
					operandAddress = "0000";
					objectCode = "UnDefinedSymbol";
				}
			}
		} else if (operationCode.equals("WORD")) {
			String hex = Integer.toHexString(Integer.parseInt(operand));
			if (hex.length() == 1) {
				String concat = "00000";
				hex = concat.concat(hex);
			} else if (hex.length() == 2) {
				String concat = "0000";
				hex = concat.concat(hex);
			} else if (hex.length() == 3) {
				String concat = "000";
				hex = concat.concat(hex);
			} else if (hex.length() == 4) {
				String concat = "00";
				hex = concat.concat(hex);
			} else if (hex.length() == 5) {
				String concat = "0";
				hex = concat.concat(hex);
			}
			objectCode = hex;
		} else if (operationCode.equals("BYTE")) {
			if (operand.charAt(0) == 'X') {
				objectCode = operand.substring(2, operationCode.length()-1);
			} else if (operand.charAt(0) == 'C') {
				for (int i = 2; i < operand.length() - 1; i++) {
					objectCode = objectCode.concat(Integer.toHexString((int) operand.charAt(i)));
				}
			}
		} else if (!operationCode.equals("START") && !operationCode.equals("RESW") && !operationCode.equals("RESB")
				&& !operationCode.equals("END")) {
			objectCode="not Valid operation" ;
		}
		objectCodes.add(objectCode);
	}

	private void checkErrors() {
		// this method if the first line don't contain start
		// or the last line don't contain end
		// if happens the assembler will not write objectfile
		if (!this.operationCodes.get(0).equals("START")) {
			check = false;
		}
		if (!this.operationCodes.get(this.operationCodes.size() - 1).equals("END")) {
			check = false;
		}
	}

	private void writeListing(String address, String label, String operationCode, String operand, String objectCode) {
		// this method is used to write the listing file
		if (label == null) {
			label = " ";
		}
		if (operand == null) {
			operand = " ";
		}
		lineNumber++;
		String listing;
		if (operationCode.equals("START") || operationCode.equals("RESB") || operationCode.equals("RESW")) {
			listing = writeListingLine(address, label, operationCode, operand, null);
			listingfile.add(listing);
		} else if (operationCode.equals("END")) {
			listing = writeListingLine(" ", label, operationCode, operand, null);
			listingfile.add(listing);
			writeListingFile();
		} else {
			if (lineNumber == 1) {
				// error if the first line don't contain start
				listing = writeListingLine(" ", " ", " ", " ", "Error First Line Must contain Start");
				listingfile.add(listing);
				listing = writeListingLine(address, label, operationCode, operand, null);
				listingfile.add(listing);
			} else if (lineNumber == labels.size()) {
				// error if the last line don't contain end
				listing = writeListingLine(address, label, operationCode, operand, objectCode);
				listingfile.add(listing);
				listing = writeListingLine(" ", " ", " ", " ", "Error Last Line Must Contain End");
				listingfile.add(listing);
				writeListingFile();
			} else {
				/**
				if (operand.charAt(0) == '=') {
					listing = writeListingLine(address, label, operationCode, operand, "literals is not valid");
					listingfile.add(listing);
					check = false;
				}
				*/
				//else {
					if (objectCode.length() > 6) {
						if(objectCode.equals("UnDefinedSymbol"))
						{
							listing = writeListingLine(address, label, operationCode, operand,
									"Undefined Symbol");
						}else if(objectCode.equals("not Valid operation")){
							listing = writeListingLine(address, label, operationCode, operand,
									"not Valid operation");
							
						}
						else
						{		
						listing = writeListingLine(address, label, operationCode, operand,
								"Out of memory range of SIC machine");
					}
						check = false;
						listingfile.add(listing);
					} else {
						listing = writeListingLine(address, label, operationCode, operand, objectCode);
						listingfile.add(listing);
					}
				//}
			}
		}
	}

	private String writeListingLine(String address, String label, String operationCode, String operand,
			String objectCode) {
		// this method is used to make the listing line in the required format
		String listing = new String();
		listing = address;
		listing = addSpaces(listing, 8);
		listing = listing.concat(label);
		listing = addSpaces(listing, 18);
		listing = listing.concat(operationCode);
		listing = addSpaces(listing, 26);
		listing = listing.concat(operand);
		if (objectCode != null) {
			listing = addSpaces(listing, 46);
			listing = listing.concat(objectCode);
		}
		listing = listing.concat("\n");
		return listing;
	}

	private String addSpaces(String STR, int length) {
		while (STR.length() < length) {
			STR = STR.concat(" ");
		}
		return STR;
	}

	private void writeObject() {
		// this method is used to write the object file
		String objectLine = new String();
		String label;
		for (int i = 0; i < operationCodes.size(); i++) {
			if (operationCodes.get(i).equals("START")) {
				label = labels.get(i);
				while (label != null && label.length() != 6) {
					label = label.concat(" ");
				}
				int programLength = Integer.parseInt(Addresses.get(Addresses.size() - 2), 16)
						- Integer.parseInt(Addresses.get(0), 16);
				String startAddress = Addresses.get(0);
				String concat = new String();
				for (int l = startAddress.length(); l < 6; l++) {
					concat = concat.concat("0");
				}
				concat = concat.concat(startAddress);
				startAddress = concat;
				concat = new String();
				String programLen = Integer.toHexString(programLength);
				for (int l = programLen.length(); l < 6; l++) {
					concat = concat.concat("0");
				}
				concat = concat.concat(programLen);
				programLen = concat;
				objectLine = "H" + label + startAddress + programLen + "\n";
				objectProgramfile.add(objectLine);
				objectLine = new String();
			} else if (operationCodes.get(i).equals("END")) {
				if (objectLine.length() > 0) {
					String length = calculateObjectCodeLength(objectLine);
					objectLine = objectLine.replace("^^", length);
					objectLine = objectLine.concat("\n");
					objectProgramfile.add(objectLine);
					objectLine = new String();
				}
				String startAddress = Addresses.get(0);
				String concat = new String();
				for (int l = startAddress.length(); l < 6; l++) {
					concat = concat.concat("0");
				}
				concat = concat.concat(startAddress);
				startAddress = concat;
				objectLine = "E" + startAddress + "\n";
				objectProgramfile.add(objectLine);
				writeObjectProgramFile();
			} else if (operationCodes.get(i).equals("RESB") || operationCodes.get(i).equals("RESW")) {
				if (objectLine.length() > 0) {
					String length = calculateObjectCodeLength(objectLine);
					objectLine = objectLine.replace("^^", length);
					objectLine = objectLine.concat("\n");
					objectProgramfile.add(objectLine);
					objectLine = new String();
				}
				for (int k = i + 1; operationCodes.get(i).equals("RESB") || operationCodes.get(i).equals("RESW"); k++) {
					i = k;
				}
				i--;
			} else {
				if (checkTextRecord(objectLine, i) == null) {
					String length = calculateObjectCodeLength(objectLine);
					objectLine = objectLine.replace("^^", length);
					objectLine = objectLine.concat("\n");
					objectProgramfile.add(objectLine);
					objectLine = new String();
					objectLine = checkTextRecord(objectLine, i);
				} else {
					objectLine = checkTextRecord(objectLine, i);
				}
			}

		}
	}

	private String checkTextRecord(String objectLine, int current) {
		if (objectLine.length() == 0) {
			// initialize new Text record
			String address = String.valueOf(Addresses.get(current));
			String concat = new String();
			for (int k = address.length(); k < 6; k++) {
				concat = concat.concat("0");
			}
			concat = concat.concat(address);
			address = concat;
			objectLine = "T" + address + "^^";
		}
		if (objectLine.length() + objectCodes.get(current).length() <= 69) {
			objectLine = objectLine.concat(objectCodes.get(current));
		} else {
			return null;
		}

		return objectLine;
	}

	private String calculateObjectCodeLength(String objectLine) {
		// this method is used to calculate the length of the object file line
		int length = objectLine.length();
		length -= 9;
		length /= 2;
		String len = Integer.toHexString(length);
		len = len.toUpperCase();
		if (len.length() == 1) {
			String concat = "0";
			concat = concat.concat(len);
			len = concat;
		}
		return len;
	}

	private void writeListingFile() {
		// Listing File
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME2))) {
			for (int i = 0; i < listingfile.size(); i++) {
				bw.write(listingfile.get(i));
			}
			bw.close();
		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	private void writeObjectProgramFile() {
		// objectProgram file
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME3))) {
			for (int i = 0; i < objectProgramfile.size(); i++) {
				bw.write(objectProgramfile.get(i));
			}
			bw.close();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}
