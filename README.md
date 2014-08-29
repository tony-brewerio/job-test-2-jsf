Sample PrimeFaces / MySQL Phone Book
=========

Should be running live here, until I shut it down - http://jsf.82.196.1.152.xip.io/ 

This is a simple PrimeFaces web application I've done for job interview.

Application consists of a single web page, divided into two main sections.

On the right side is the form for adding/editing phone book records.
Form has two states - bound and unbound.
When in unbound state ( default ) - form will insert new records, and reset fields after insert is complete.
Form can be bound to specific record by selecting rows in the table to the left. 
In this case, form will update this record, and won't reset after saving. 

When form is in bound state, a new button to handle photo upload will appear.
Clicking on this button will open modal dialog that displays current photo and allows to upload new one.

In the middle of the screen there's data table that displays phone book records.
Search, sorting and pagination is efficiently handled thanks to PrimeFaces `LazyDataModel`.

There are export to Excel/PDF buttons inside table's footer.

Photos are saved to database as blobs, and are served back to browser using `StreamedContent`.

On application start, table is created anew by Hibernate, then populated with 1000 new random records.
