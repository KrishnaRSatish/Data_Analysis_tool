
import java.io.*;
import java.util.ArrayList;


public class Data_Util {
    private static String SourceFileName;
    private static final String TargetFileName1 = "Pass_fail.csv";
    private static final String TargetFileName2 = "Errors.csv";

    private static final ArrayList<String> errorIds = new ArrayList<>();
    private static final ArrayList<String> passedList = new ArrayList<>();
    private static final ArrayList<String> failedList = new ArrayList<>();
    private static final ArrayList<String> manualCheckList = new ArrayList<>();
    private static final ArrayList<String> errorsList = new ArrayList<>();
    private static String formattedIdString;


    public static void main(String[] args) {
        SourceFileName = "test-Output_preparation.atr";
        readFileForPassFailCSV(SourceFileName);
        readFileForErrorsCSV(SourceFileName);

    }

    //function to read file name and check for string
    public static void readFileForPassFailCSV(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream("./input_files/" + fileName);
            DataInputStream in = new DataInputStream(fileInputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                if (strLine.contains("-- Test ")) {
                    String tempStr = strLine.replaceAll("\\s", "");
                    formattedIdString = tempStr.replaceAll("--", "");
                    errorIds.add(formattedIdString);
                }
                if ((strLine.toLowerCase().contains("check") && strLine.toLowerCase().contains("failed"))) {
                    failedList.add(formattedIdString);
                } else if ((strLine.toLowerCase().contains("check") && strLine.toLowerCase().contains("passed"))) {
                    passedList.add(formattedIdString);
                } else if ((strLine.toLowerCase().contains("check") && strLine.toLowerCase().contains("fail"))) {
                    failedList.add(formattedIdString);
                }
            }
            checkDuplicates();
            removeDuplicates(passedList);
            removeDuplicates(failedList);
            removeList(passedList, failedList);
            removeList(failedList, manualCheckList);

            System.out.println("LOG: ALL ERROR ID's" + errorIds);
            System.out.println("LOG: ALL PASSED ID's" + passedList);
            System.out.println("LOG: ALL FAILED ID's" + failedList);

            //check if file exists
            File file = new File(TargetFileName1);
            if (!file.exists()) {
                createPassFailCSV();
            }
            for (String manualCheck : manualCheckList) {
                appendPassFailCSV(SourceFileName, manualCheck, "Failed", "Manual check required");
            }
            for (String failed : failedList) {
                appendPassFailCSV(SourceFileName, failed, "FAILED", "");
            }

            for (String passed : passedList) {
                appendPassFailCSV(SourceFileName, passed, "PASSED", "");
            }

            //Close the input stream
            in.close();

        } catch (Exception e) {
            //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static void readFileForErrorsCSV(String fileName) {
        try {
            FileInputStream fileInputStream = new FileInputStream("./input_files/" + fileName);
            DataInputStream in = new DataInputStream(fileInputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;

            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                if (strLine.contains("Tests with Stub Failures")) {
                    if (strLine.contains("0")) {
                        System.out.println("LOG:TESTING" + strLine);
                        errorsList.add("NO");
                    } else {
                        System.out.println("LOG:TESTING" + strLine);
                        errorsList.add("YES");
                    }
                }
                if (strLine.contains("Script Errors")) {
                    if (strLine.contains("0")) {
                        System.out.println("LOG:TESTING" + strLine);
                        errorsList.add("NO");
                    } else {
                        System.out.println("LOG:TESTING" + strLine);
                        errorsList.add("YES");
                    }
                }
                if (strLine.contains("Failed ")) {
                    if (strLine.contains("0")) {
                        System.out.println("LOG: TESTING" + strLine);
                        errorsList.add("NO");
                    } else {
                        System.out.println("LOG :TESTING" + strLine);
                        errorsList.add("YES");
                    }
                }
            }

            //check if file exists
            File file = new File(TargetFileName2);
            if (!file.exists()) {
                createErrorsCSV();
            }
            appendErrorsCSV(SourceFileName, errorsList.get(0), errorsList.get(1), errorsList.get(2));

            //Close the input stream
            in.close();

        } catch (Exception e) {
            //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }


    //function to remove duplicates from arraylist
    static void removeDuplicates(ArrayList<String> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (list.get(i).equals(list.get(j))) {
                    list.remove(j);
                    j--;
                }
            }
        }
    }


    //function to remove list of strings from arraylist
    static void removeList(ArrayList<String> list, ArrayList<String> listToRemove) {
        for (String s : listToRemove) {
            for (int j = 0; j < list.size(); j++) {
                if (s.equals(list.get(j))) {
                    list.remove(j);
                    j--;
                }
            }
        }
    }

    //function to check which element appear more than once in arraylist
    static void checkDuplicates() {
        for (int i = 0; i < Data_Util.failedList.size() - 1; i++) {
            for (int j = i + 1; j < Data_Util.failedList.size(); j++) {
                if (Data_Util.failedList.get(i).equals(Data_Util.failedList.get(j))) {
                    System.out.println("LOG:Manual Testing Required  " + Data_Util.failedList.get(i));
                    manualCheckList.add(Data_Util.failedList.get(i));
                }
            }
        }
    }

    //function to create csv file with header
    static void createPassFailCSV() {
        try {
            FileWriter writer = new FileWriter(Data_Util.TargetFileName1);
            writer.append("File_name");
            writer.append(',');
            writer.append("Error_ID");
            writer.append(',');
            writer.append("Status");
            writer.append(',');
            writer.append("Remarks");
            writer.append('\n');
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void createErrorsCSV() {
        try {
            FileWriter writer = new FileWriter(Data_Util.TargetFileName2);
            writer.append("File_name");
            writer.append(',');
            writer.append("Script Errors");
            writer.append(',');
            writer.append("Failures");
            writer.append(',');
            writer.append("stub_Errors");
            writer.append('\n');
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //function to append data to csv file
    static void appendPassFailCSV(String sourceFileName, String errorId, String status, String remarks) {
        try {
            FileWriter writer = new FileWriter(Data_Util.TargetFileName1, true);
            writer.append(sourceFileName);
            writer.append(',');
            writer.append(errorId);
            writer.append(',');
            writer.append(status);
            writer.append(',');
            writer.append(remarks);
            writer.append('\n');
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void appendErrorsCSV(String sourceFileName, String error1, String error2, String error3) {
        try {
            FileWriter writer = new FileWriter(Data_Util.TargetFileName2, true);
            writer.append(sourceFileName);
            writer.append(',');
            writer.append(error1);
            writer.append(',');
            writer.append(error2);
            writer.append(',');
            writer.append(error3);
            writer.append('\n');
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
