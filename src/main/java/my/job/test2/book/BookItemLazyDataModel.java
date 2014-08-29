package my.job.test2.book;

import my.job.test2.book.entity.PhoneBookItem;
import my.job.test2.book.entity.PhoneBookItemDAO;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import java.util.List;
import java.util.Map;

public class BookItemLazyDataModel extends LazyDataModel<PhoneBookItem> {

    PhoneBookItemDAO dao;

    public BookItemLazyDataModel(PhoneBookItemDAO dao) {
        this.dao = dao;
    }

    @Override
    public List<PhoneBookItem> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        PhoneBookItemDAO.DataModelQueryResults results = dao.findAllForDataModel(first, pageSize, sortField, sortOrder, filters);
        setRowCount(results.getCount());
        return results.getList();
    }

}
