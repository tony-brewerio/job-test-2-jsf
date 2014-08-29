package my.job.test2.book;


import com.github.javafaker.Faker;
import lombok.Setter;
import my.job.test2.book.entity.PhoneBookItemDAO;
import org.apache.commons.lang.math.RandomUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

@ManagedBean(eager = true)
@ApplicationScoped
public class BookItemDataGenerator {

    @ManagedProperty("#{phoneBookItemDAO}")
    @Setter
    PhoneBookItemDAO phoneBookItemDAO;

    @PostConstruct
    public void generate() {
        Faker faker = new Faker();

        for (int i = 0; i <= 1000; i++) {
            phoneBookItemDAO.insert(
                    faker.name().fullName(),
                    birthDate(),
                    faker.address().country() + " " + faker.address().cityPrefix() + faker.address().citySuffix() + " " + faker.address().streetAddress(false),
                    faker.phoneNumber().phoneNumber()
            );
        }
    }

    Date birthDate() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(GregorianCalendar.YEAR, 1960 + RandomUtils.nextInt(50));
        gc.set(GregorianCalendar.DAY_OF_YEAR, 1 + RandomUtils.nextInt(gc.getActualMaximum(GregorianCalendar.DAY_OF_YEAR)));
        return gc.getTime();
    }

}
