package my.job.test2.book.entity;

import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class PhoneBookItemDAO {

    @Data
    public static class DataModelQueryResults {
        Integer count;
        List<PhoneBookItem> list;
    }

    Logger logger;

    @Autowired
    SessionFactory sessionFactory;

    public PhoneBookItemDAO() {
        logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * Adds new record to database
     * @param name
     * @param birthDate
     * @param address
     * @param phone
     * @return Newly inserted record id
     */
    @Transactional
    public Integer insert(String name, Date birthDate, String address, String phone) {
        PhoneBookItem item = new PhoneBookItem();
        item.setName(name);
        item.setBirthDate(birthDate);
        item.setAddress(address);
        item.setPhone(phone);
        sessionFactory.getCurrentSession().save(item);
        return item.getId();
    }

    /**
     * Looks up the record by id and updates it's fields
     * @param id
     * @param name
     * @param birthDate
     * @param address
     * @param phone
     */
    @Transactional
    public void updateFields(Integer id, String name, Date birthDate, String address, String phone) {
        PhoneBookItem item = find(id);
        item.setBirthDate(birthDate);
        item.setAddress(address);
        item.setPhone(phone);
    }

    /**
     * Separate update method for photo field
     * @param id
     * @param photo
     * @throws SQLException
     */
    @Transactional
    public void updatePhoto(Integer id, byte[] photo) throws SQLException {
        PhoneBookItem item = find(id);
        item.setPhoto(new SerialBlob(photo));
    }

    /**
     * Returns record from database by id
     * @param id
     * @return
     */
    public PhoneBookItem find(Integer id) {
        return (PhoneBookItem) sessionFactory.getCurrentSession().get(PhoneBookItem.class, id);
    }

    /**
     * @param first Row number of first record to return
     * @param pageSize How much record to return
     * @param sortField Sort results by this field
     * @param sortOrder Sort direction
     * @param filters Map of [field -> value], for filtering the results
     * @return Container with list of found records and total record count
     */
    @SuppressWarnings(value = "unchecked")
    public DataModelQueryResults findAllForDataModel(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        logger.info("Invoked findAllForDataModel()");
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PhoneBookItem.class);

        logger.info("Sorting with sortField: {} and sortOrder: {}", sortField, sortOrder);
        switch (sortOrder) {
            case ASCENDING:
                criteria.addOrder(Order.asc(sortField));
                break;
            case DESCENDING:
                criteria.addOrder(Order.desc(sortField));
                break;
            default:
        }

        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            String field = entry.getKey();
            String value = (String) entry.getValue();
            switch (field) {
                case "id":
                    try {
                        criteria.add(Restrictions.eq("id", Integer.parseInt(value)));
                        logger.info("Filtering by id = {}", value);
                    } catch (NumberFormatException e) {
                        logger.info("Filtering by id skipped, {} is invalid number", value);
                    }
                    break;
                case "birthDate":
                    int year = Integer.parseInt(value);
                    Calendar start = Calendar.getInstance();
                    start.set(year, Calendar.JANUARY, 1, 0, 0, 0);
                    Calendar end = Calendar.getInstance();
                    end.set(year + 1, Calendar.JANUARY, 1, 0, 0, 0);
                    logger.info("Filtering by year(birthDate) = {}", value);
                    criteria.add(Restrictions.and(
                            Restrictions.ge("birthDate", start.getTime()),
                            Restrictions.lt("birthDate", end.getTime())
                    ));
                    break;
                case "address":
                case "phone":
                    logger.info("Filtering by {} ilike '%{}%'", value);
                    criteria.add(Restrictions.ilike(field, value, MatchMode.ANYWHERE));
                    break;
                default:
            }
        }

        DataModelQueryResults results = new DataModelQueryResults();

        criteria.setFirstResult(first).setMaxResults(pageSize);
        results.setList(criteria.list());

        criteria.setFirstResult(0).setMaxResults(0);
        criteria.setProjection(Projections.rowCount());
        results.setCount(((Long) criteria.uniqueResult()).intValue());

        logger.info("Returning {} of {} items", results.getList().size(), results.getCount());
        return results;
    }

    /**
     * @param id Record id
     * @return Byte array with contents of photo field's blob
     * @throws SQLException
     * @throws IOException
     */
    public byte[] loadPhotoBytes(Integer id) throws SQLException, IOException {
        PhoneBookItem item = find(id);
        if (item.getPhoto() != null) {
            try (InputStream stream = item.getPhoto().getBinaryStream()) {
                return IOUtils.toByteArray(stream);
            }
        } else {
            return null;
        }
    }

    /**
     * @return List of distinct birthDate years, in descending order
     */
    @SuppressWarnings("unchecked")
    public List<Integer> findAllYears() {
        return sessionFactory.getCurrentSession()
                .createQuery("select distinct year(birthDate) from PhoneBookItem where year(birthDate) is not null order by year(birthDate) desc")
                .list();
    }

}
