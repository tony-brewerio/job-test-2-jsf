package my.job.test2.book;

import lombok.Data;
import my.job.test2.book.entity.PhoneBookItem;
import my.job.test2.book.entity.PhoneBookItemDAO;
import net.coobird.thumbnailator.Thumbnails;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.validation.constraints.Past;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Date;

@Data
@ManagedBean
@ViewScoped
public class PhoneBookItemForm {

    Logger logger;

    @ManagedProperty("#{phoneBookItemDAO}")
    PhoneBookItemDAO phoneBookItemDAO;

    Integer id;

    String name;

    @Past
    Date birthDate;

    String address;

    String phone;

    boolean hasPhoto;

    @PostConstruct
    void init() {
        logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * Simple helper that adds message to predefined channel.
     * Is used for non-error status messages, like record save success.
     *
     * @param message Some short text, goes into FacesMessage's summary field
     */
    void addMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(
                "formMessagesChannel", new FacesMessage(FacesMessage.SEVERITY_INFO, message, "")
        );
    }

    /**
     * Clears the form state.
     * Invoked either after new record is added to database or on user action ( there's button for that ).
     */
    public void reset() {
        id = null;
        name = null;
        birthDate = null;
        address = null;
        phone = null;
        hasPhoto = false;
    }

    /**
     * Main form action.
     * If id property was set previously by selecting table row, will perform update on record with that id.
     * Otherwise, new record is inserted, form state is reset afterwards.
     * Note that reset is only on update, not on insert.
     * Displays message to the user in both cases.
     */
    public void save() {
        if (id == null) {
            logger.info("Insert new phone book record");
            Integer newRecordId = phoneBookItemDAO.insert(name, birthDate, address, phone);
            reset();
            addMessage(String.format("Added new record to phone book with id: %d", newRecordId));
        } else {
            logger.info("Update existing phone book record id: {}", id);
            phoneBookItemDAO.updateFields(id, name, birthDate, address, phone);
            addMessage(String.format("Updated existing phone book record with id: %d", id));
        }
    }

    /**
     * Called when user selects a table row.
     * Form is populated with selected record's data, and record id is saved for later use.
     * @param event PrimeFaces event containing selected record
     */
    public void onRowSelect(SelectEvent event) {
        PhoneBookItem item = (PhoneBookItem) event.getObject();
        id = item.getId();
        name = item.getName();
        birthDate = item.getBirthDate();
        address = item.getAddress();
        phone = item.getPhone();
        hasPhoto = item.getPhoto() != null;
    }

    /**
     * Updates currently selected record's photo with thumbnail of uploaded image file.
     * @param event This event from PrimeFaces must contain reference to uploaded file
     * @throws IOException
     * @throws SQLException
     */
    public void onPhotoUpload(FileUploadEvent event) throws IOException, SQLException {
        logger.info("onPhotoUpload with event: {}", event);
        try (InputStream stream = event.getFile().getInputstream()) {
            // users are allowed to upload quite a huge photos, much larger than what is displayed on the site
            // storing them full-size is a total waste, considering this is a demo application
            // in real world scenario, I would generate thumbs on serving, with filesystem caching of results
            ByteArrayOutputStream thumbStream = new ByteArrayOutputStream();
            Thumbnails.of(stream).size(240, 240).outputQuality(0.95).toOutputStream(thumbStream);
            phoneBookItemDAO.updatePhoto(id, thumbStream.toByteArray());
            hasPhoto = true;
        }
    }

}
