package my.job.test2.book;


import com.github.javafaker.Faker;
import lombok.Setter;
import my.job.test2.book.entity.PhoneBookItem;
import my.job.test2.book.entity.PhoneBookItemDAO;
import org.apache.commons.lang.math.RandomUtils;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import java.util.*;

@ManagedBean(eager = true)
@ApplicationScoped
public class BookItemDataGenerator {

    @ManagedProperty("#{phoneBookItemDAO}")
    @Setter
    PhoneBookItemDAO phoneBookItemDAO;

    @PostConstruct
    public void generate() {
        Faker faker = new Faker();

        List<PhoneBookItem> items = new ArrayList<>();
        for (int i = 0; i <= 1000; i++) {
            PhoneBookItem item = new PhoneBookItem();
            item.setName(faker.name().fullName());
            item.setBirthDate(birthDate());
            item.setAddress(faker.address().country() + " " + faker.address().cityPrefix() + faker.address().citySuffix() + " " + faker.address().streetAddress(false));
            item.setPhone(faker.phoneNumber().phoneNumber());
            items.add(item);
        }
        phoneBookItemDAO.insertAll(items);
    }

    Date birthDate() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(GregorianCalendar.YEAR, 1960 + RandomUtils.nextInt(50));
        gc.set(GregorianCalendar.DAY_OF_YEAR, 1 + RandomUtils.nextInt(gc.getActualMaximum(GregorianCalendar.DAY_OF_YEAR)));
        return gc.getTime();
    }

}
