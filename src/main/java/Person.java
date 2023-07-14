import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {

    private int position;
    @CsvBindByName (column = "first_name")
    private String firstName;
    @CsvBindByName (column = "last_name")
    private String lastName;
    @CsvBindByName (column = "company_name")
    private String companyName;
    @CsvBindByName
    private String address;
    @CsvBindByName
    private String city;
    @CsvBindByName
    private String county;
    @CsvBindByName
    private String postal;
    @CsvBindByName
    private String phone1;
    @CsvBindByName
    private String phone2;
    @CsvBindByName
    private String email;
    @CsvBindByName
    private String web;
}
