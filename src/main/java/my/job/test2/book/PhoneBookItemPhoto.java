package my.job.test2.book;

import lombok.Setter;
import my.job.test2.book.entity.PhoneBookItemDAO;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;

@ManagedBean
@RequestScoped
public class PhoneBookItemPhoto {

    Logger logger;

    @ManagedProperty("#{phoneBookItemDAO}")
    @Setter
    PhoneBookItemDAO phoneBookItemDAO;

    @PostConstruct
    void init() {
        logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * When "id" parameter is provided, finds record in database and returns it's photo field as a stream.
     * Otherwise an empty stream is returned.
     * @return Record's photo field as a StreamedContent
     * @throws IOException
     * @throws SQLException
     */
    public StreamedContent getPhoto() throws IOException, SQLException {
        FacesContext context = FacesContext.getCurrentInstance();
        String id = context.getExternalContext().getRequestParameterMap().get("id");
        if (id != null) {
            byte[] photo = phoneBookItemDAO.loadPhotoBytes(Integer.parseInt(id));
            logger.info("Returning photo for item id: {} | {}", id, photo == null ? -1 : photo.length);
            return new DefaultStreamedContent(new ByteArrayInputStream(photo == null ? new byte[]{} : photo));
        } else {
            logger.info("Returning empty streamed content");
            return new DefaultStreamedContent(new ByteArrayInputStream(new byte[]{}));
        }
    }

}
