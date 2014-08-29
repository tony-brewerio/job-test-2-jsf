package my.job.test2.book;

import lombok.Getter;
import lombok.Setter;
import my.job.test2.book.entity.PhoneBookItem;
import my.job.test2.book.entity.PhoneBookItemDAO;
import org.primefaces.model.LazyDataModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
public class BookItemsView implements Serializable {

    @ManagedProperty("#{phoneBookItemDAO}")
    @Setter
    PhoneBookItemDAO phoneBookItemDAO;

    @Getter
    LazyDataModel<PhoneBookItem> dataModel;

    @Getter
    List<SelectItem> yearsOptions;

    @Getter
    @Setter
    PhoneBookItem selected;

    @PostConstruct
    public void init() {
        loadYearsOptions();
        dataModel = new BookItemLazyDataModel(phoneBookItemDAO);
    }

    /**
     * Constructs options from list of birthDate years from DAO.
     * One empty item is added at the front to represent all years.
     */
    void loadYearsOptions() {
        yearsOptions = new ArrayList<>();
        yearsOptions.add(new SelectItem("", ""));
        for (Integer year : phoneBookItemDAO.findAllYears()) {
            yearsOptions.add(new SelectItem(year, year.toString()));
        }
    }

    /**
     *
     */
    public void removeSelection() {
        selected = null;
    }
}
