package my.job.test2.book.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

@Entity
@Table(name = "PHONE_BOOK_ITEMS")
@Data
public class PhoneBookItem implements Serializable {

    @Id
    @GeneratedValue(generator = "phoneBookItemsPkGen")
    @SequenceGenerator(name = "phoneBookItemsPkGen", sequenceName = "PHONE_BOOK_ITEMS_PK_SEQ", allocationSize = 1)
    @Column(name = "ID")
    Integer id;

    @Column(name = "NAME")
    String name;

    @Column(name = "BIRTH_DATE")
    Date birthDate;

    @Column(name = "ADDRESS")
    String address;

    @Column(name = "PHONE")
    String phone;

    @Lob
    @Column(name = "PHOTO")
    Blob photo;

    @Column(name = "PHOTO_UPLOADED_AT")
    Date photoUploadedAt;

}
