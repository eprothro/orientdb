package com.orientechnologies.lucene.test;

import com.orientechnologies.orient.core.db.ODatabase;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.OCommandSQL;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Enrico Risa on 05/10/16.
 */
public class LuceneNullTest {

  @Test
  public void testNullChangeToNotNullWithLists() {

    ODatabaseDocumentTx db = new ODatabaseDocumentTx("memory:testNullChangeToNotNull");
    try {
      db.create();

      db.command(new OCommandSQL("create class Test extends V")).execute();

      db.command(new OCommandSQL("create property Test.names EMBEDDEDLIST STRING")).execute();

      db.command(new OCommandSQL("create index Test.names on Test (names) fulltext engine lucene")).execute();

      db.begin();
      ODocument doc = new ODocument("Test");
      db.save(doc);
      db.commit();

      db.begin();
      doc.field("names", new String[] { "foo" });
      db.save(doc);
      db.commit();

      OIndex<?> index = db.getMetadata().getIndexManager().getIndex("Test.names");

      Assert.assertEquals(index.getSize(), 1);
    } finally {
      db.drop();
    }

  }

  @Test
  public void testNotNullChangeToNullWithLists() {

    ODatabase db = new ODatabaseDocumentTx("memory:testNotNullChangeToNullWithLists");
    try {

      db.command(new OCommandSQL("create class Test extends V")).execute();
      db.command(new OCommandSQL("create property Test.names EMBEDDEDLIST STRING")).execute();
      db.command(new OCommandSQL("create index Test.names on Test (names) fulltext engine lucene")).execute();

      ODocument doc = new ODocument("Test");

      db.begin();
      doc.field("names", new String[] { "foo" });
      db.save(doc);
      db.commit();

      db.begin();

      doc.removeField("names");

      db.save(doc);
      db.commit();

      OIndex<?> index = db.getMetadata().getIndexManager().getIndex("Test.names");
      Assert.assertEquals(index.getSize(), 0);
    } finally {
      db.drop();
    }

  }
}
