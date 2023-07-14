import com.opencsv.bean.CsvToBeanBuilder;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.text.MessageFormat;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class PeopleQuery {
    protected final String MSG_WELCOME = "Welcome to querying people.";
    protected final String MSG_INPUT_FILE = "Please input the path of the file, \n" +
            "You can also use prepared file [testfile/input.csv]:";

    protected final String MSG_OPTIONS = """
                Please select from one of the six options below:\s
                OPTION 1: Every person who has “Esq” in their company name.
                OPTION 2: Every person who lives in “Derbyshire”.
                OPTION 3: Every person whose house number is exactly three digits.
                OPTION 4: Every person whose website URL is longer than 35 characters
                (including the protocol and subdomain).
                OPTION 5: Every person who lives in a postcode area with a single-digit value.
                (Note that in UK postcodes the “area” is the first portion before the space,
                so anything starting M3 or M7 would be acceptable, while anything startingM10+ would not.
                The portion after the space is not relevant.)
                OPTION 6: Every person whose first phone number is numerically larger than their second phone number.""";

    private final Scanner scanner;
    private List<Person> personList;

    public PeopleQuery(){
        scanner = new Scanner(System.in);
    }

    public void printWelcomeText(){
        System.out.println(MSG_WELCOME);
    }

    public void printInputFileText(){
        System.out.println(MSG_INPUT_FILE);
    }

    public void printOptionsText() {
        System.out.println(MSG_OPTIONS);
    }

    public String getFilePaths(){
        return scanner.nextLine().trim();
    }

    public String getOption(){
        return scanner.nextLine().trim();
    }

    public void printResult(List<Person> personList){
        if (personList == null)
            printUnknownOption();
        else {
            System.out.println("Count: " + personList.size());

            for (Person person : personList) {
                System.out.println(MessageFormat.format("{0} - {1} {2} - {3}",
                        person.getPosition(), person.getFirstName(), person.getLastName(), person.getCompanyName()));
            }
        }
    }

    public void printUnknownOption(){
        System.out.println("Unknown Option");
    }

    public void close(){
        scanner.close();
    }

    public List<Person> filterByName(String firstName, String lastName){
        return personList.stream()
                .filter(p->p.getFirstName().equalsIgnoreCase(firstName))
                .filter(p->p.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
    }

    public List<Person> filterByCounty(String text){
        return personList.stream()
                .filter(p->p.getCounty().equalsIgnoreCase(text))
                .collect(Collectors.toList());
    }

    public List<Person> filterByCompanyName(String text){
        return personList.stream()
                .filter(p->p.getCompanyName().contains(text))
                .collect(Collectors.toList());
    }

    public List<Person> filterByHouseNum(int numOfDigit){
        return personList.stream()
                .filter(p->(p.getAddress().substring(0, p.getAddress().indexOf(" ")).length()==numOfDigit))
                .collect(Collectors.toList());
    }

    public List<Person> filterByWebGreaterThanLength(int length){
        return personList.stream()
                .filter(p->(p.getWeb().length()>length))
                .collect(Collectors.toList());
    }

    public List<Person> filterByPhone1GreaterThanPhone2(){
        return personList.stream()
                .filter(p->parsePhoneNumber(p.getPhone1())>parsePhoneNumber(p.getPhone2()))
                .collect(Collectors.toList());
    }

    public List<Person> filterByPostCodeDigits(int numOfDigit){
        return personList.stream()
                .filter(p->parseFirstPortionPostalDigit(p.getPostal()).length() == numOfDigit)
                .collect(Collectors.toList());
    }

    protected long parsePhoneNumber(String phoneNumber){
        return Long.parseLong(phoneNumber.replaceAll("\\D", ""));
    }

    protected String parseFirstPortionPostalDigit(String postCode){
        String firstPortion = postCode.substring(0, postCode.indexOf(" "));
        return firstPortion.replaceAll("[a-zA-Z]", "");
    }

    public void start(){
        this.printWelcomeText();
        this.printInputFileText();
        String filePath = this.getFilePaths();
        try {
            processFileInput(filePath);
        }catch (FileNotFoundException e){
            System.err.println("File "+filePath+ " not found!");
            System.exit(1);
        }
        this.printOptionsText();
        String option = this.getOption();
        List<Person> result = processOption(option);
        this.printResult(result);
        this.close();
    }

    public void processFileInput(String filepath) throws FileNotFoundException {
        List<Person> personList = new CsvToBeanBuilder(new FileReader(filepath))
                .withType(Person.class).build().parse();

        for (int i=0; i<personList.size(); i++){
            personList.get(i).setPosition(i+1);
        }
        this.setPersonList(personList);
    }

    public List<Person> processOption(String option) {
        return switch (option) {
            case "1" -> this.filterByCompanyName("Esq");
            case "2" -> this.filterByCounty("Derbyshire");
            case "3" -> this.filterByHouseNum(3);
            case "4" -> this.filterByWebGreaterThanLength(35);
            case "5" -> this.filterByPostCodeDigits(1);
            case "6" -> this.filterByPhone1GreaterThanPhone2();
            default -> null;
        };
    }

    public void setPersonList(List<Person> personList){
        this.personList = personList;
    }
}
